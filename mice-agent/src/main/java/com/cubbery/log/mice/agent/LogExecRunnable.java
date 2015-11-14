/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.agent;

import com.cubbery.log.mice.agent.parser.LogTailReader;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.flume.Event;
import org.apache.flume.SystemClock;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.instrumentation.SourceCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.agent <br>
 * <b>类名称</b>： LogExecRunnable <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class LogExecRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(LogExecRunnable.class);

    private final ChannelProcessor channelProcessor;
    private final SourceCounter sourceCounter;
    private final long restartThrottle;
    private final int bufferCount;
    private final Charset charset;
    private final String logFile;
    private final LogConf logConf;
    private final long batchTimeout;

    private volatile boolean restart;
    private Process process = null;
    private SystemClock systemClock = new SystemClock();
    private Long lastPushToChannel = systemClock.currentTimeMillis();
    private ScheduledExecutorService timedFlushService;
    private ScheduledFuture<?> future;
    String cmd;

    public LogExecRunnable(ChannelProcessor channelProcessor, SourceCounter sourceCounter, long restartThrottle, int bufferCount, Charset charset,LogConf logConf,String logFile,long batchTimeout) {
        this.channelProcessor = channelProcessor;
        this.sourceCounter = sourceCounter;
        this.restartThrottle = restartThrottle;
        this.bufferCount = bufferCount;
        this.charset = charset;
        this.batchTimeout = batchTimeout;
        this.logConf = logConf;
        this.logFile = logFile;
        cmd = "tail -F " + logFile;
    }

    @Override
    public void run() {
        String exitCode = null;
        do {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
            try {
                run0(reader);
            } catch (Exception e) {
                logger.error("Failed while running tail file: " + logFile, e);
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        logger.error("Failed to close reader for exec source", ex);
                    }
                }
                exitCode = String.valueOf(kill());
            }
            if (restart) {
                logger.info("Restarting in {} ms, exit code {}", restartThrottle,exitCode);
                try {
                    Thread.sleep(restartThrottle);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                logger.info("Command [" + logFile + "] exited with " + exitCode);
            }
        } while (restart);
    }

    private void run0(BufferedReader reader) throws Exception {
        final List<Event> eventList = new ArrayList<Event>();
        timedFlushService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("timedFlushExecService" + Thread.currentThread().getId() + "-%d").build());

        process = Runtime.getRuntime().exec(cmd);
        future = timedFlushService.scheduleWithFixedDelay(new Runnable() {
                                                              @Override
                                                              public void run() {
                                                                  try {
                                                                      synchronized (eventList) {
                                                                          if (!eventList.isEmpty() && timeout()) {
                                                                              flushEventBatch(eventList);
                                                                          }
                                                                      }
                                                                  } catch (Exception e) {
                                                                      logger.error("Exception occured when processing event batch", e);
                                                                      if (e instanceof InterruptedException) {
                                                                          Thread.currentThread().interrupt();
                                                                      }
                                                                  }
                                                              }
                                                          },
                batchTimeout, batchTimeout, TimeUnit.MILLISECONDS);
        LogTailReader tailFReader = new LogTailReader(reader, logConf);
        while (!Thread.interrupted()) {
            Event event = tailFReader.produceEvent();
            if (event == null) {
                Thread.sleep(50);
                continue;
            }
            synchronized (eventList) {
                sourceCounter.incrementEventReceivedCount();
                eventList.add(event);
                if (eventList.size() >= bufferCount || timeout()) {
                    flushEventBatch(eventList);
                }
            }
        }
        logger.info("############interrupted############");
        synchronized (eventList) {
            if (!eventList.isEmpty()) {
                flushEventBatch(eventList);
            }
        }
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    private void flushEventBatch(List<Event> eventList) {
        channelProcessor.processEventBatch(eventList);
        sourceCounter.addToEventAcceptedCount(eventList.size());
        eventList.clear();
        lastPushToChannel = systemClock.currentTimeMillis();
    }

    private boolean timeout() {
        return (systemClock.currentTimeMillis() - lastPushToChannel) >= batchTimeout;
    }

    public int kill() {
        if (process != null) {
            synchronized (process) {
                process.destroy();
                try {
                    int exitValue = process.waitFor();
                    // Stop the Thread that flushes periodically
                    if (future != null) {
                        future.cancel(true);
                    }
                    if (timedFlushService != null) {
                        timedFlushService.shutdown();
                        while (!timedFlushService.isTerminated()) {
                            try {
                                timedFlushService.awaitTermination(500, TimeUnit.MILLISECONDS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    return exitValue;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            return Integer.MIN_VALUE;
        }
        return Integer.MIN_VALUE / 2;
    }
}
