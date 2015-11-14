/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.agent;

import com.cubbery.log.mice.agent.utils.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.apache.flume.source.ExecSourceConfigurationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.agent <br>
 * <b>类名称</b>： LogExecSource <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class LogExecSource extends AbstractSource implements EventDrivenSource,Configurable {
    private static final Logger logger = LoggerFactory.getLogger(LogExecSource.class);

    private SourceCounter sourceCounter;
    private ExecutorService executor;
    private Future<?> future;
    private long restartThrottle;
    private boolean restart;
    private Integer bufferCount;
    private long batchTimeout;
    private LogExecRunnable runner;
    private LogConf logConf;
    private Charset charset;

    public static final String appName;
    public static final String logFile;

    static {
        // -Dmice.log.name=$LOG_NAME -Dmice.log.file=$LOG_FILE
        appName = System.getProperty("mice.log.name");
        logFile = System.getProperty("mice.log.file");

        if(StringUtils.isBlank(appName)) {
            logger.error("No set the properties with :mice.log.name");
            throw new RuntimeException();
        }
        if(StringUtils.isBlank(logFile)) {
            logger.error("No set the properties with :mice.log.file");
            throw new RuntimeException();
        }
    }

    @Override
    public void configure(Context context) {
        restartThrottle = context.getLong(ExecSourceConfigurationConstants.CONFIG_RESTART_THROTTLE,
                ExecSourceConfigurationConstants.DEFAULT_RESTART_THROTTLE);
        restart = context.getBoolean(ExecSourceConfigurationConstants.CONFIG_RESTART,
                ExecSourceConfigurationConstants.DEFAULT_RESTART);
        bufferCount = context.getInteger(ExecSourceConfigurationConstants.CONFIG_BATCH_SIZE,
                ExecSourceConfigurationConstants.DEFAULT_BATCH_SIZE);
        batchTimeout = context.getLong(ExecSourceConfigurationConstants.CONFIG_BATCH_TIME_OUT,
                ExecSourceConfigurationConstants.DEFAULT_BATCH_TIME_OUT);
        charset = Charset.forName(context.getString(ExecSourceConfigurationConstants.CHARSET,
                ExecSourceConfigurationConstants.DEFAULT_CHARSET));

        String regex = context.getString(Constant.LOG_REGEX,Constant.DEFAULT_LOG_REGEX);

        int dateGroupNo = context.getInteger(Constant.LOG_DATE_GROUP, Constant.DEFAULT_LOG_DATE_GROUP);
        int levelGroupNo = context.getInteger(Constant.LOG_LEVEL_GROUP, Constant.DEFAULT_LOG_LEVEL_GROUP);
        int threadGroupNo = context.getInteger(Constant.LOG_THREAD_GROUP, Constant.DEFAULT_LOG_THREAD_GROUP);
        int classGroupNo = context.getInteger(Constant.LOG_CLASS_GROUP, Constant.DEFAULT_LOG_CLASS_GROUP);
        int msgGroupNo = context.getInteger(Constant.LOG_MESSAGE_GROUP, Constant.DEFAULT_LOG_MESSAGE_GROUP);
        String dateFormat = context.getString(Constant.LOG_DATE_FORMAT, Constant.DEFAULT_LOG_DATE_FORMAT);

        logConf = new LogConf(appName, regex,dateGroupNo, levelGroupNo, threadGroupNo, classGroupNo, msgGroupNo, dateFormat);
        if (sourceCounter == null) {
            sourceCounter = new SourceCounter(getName());
        }
        //logger.debug();
    }

    @Override
    public synchronized void start() {
        executor = Executors.newSingleThreadExecutor();
        runner = new LogExecRunnable(getChannelProcessor(),sourceCounter,restartThrottle,bufferCount,charset,logConf,logFile,batchTimeout);
        future = executor.submit(runner);
        sourceCounter.start();
        super.start();
    }

    @Override
    public synchronized void stop() {
        logger.info("Stopping exec source with command:{}", logFile);
        if (runner != null) {
            runner.setRestart(false);
            runner.kill();
        }

        if (future != null) {
            logger.debug("Stopping exec runner");
            future.cancel(true);
            logger.debug("Exec runner stopped");
        }
        executor.shutdown();

        while (!executor.isTerminated()) {
            logger.debug("Waiting for exec executor service to stop");
            try {
                executor.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.debug("Interrupted while waiting for exec executor service to stop. Just exiting.");
                Thread.currentThread().interrupt();
            }
        }
        sourceCounter.stop();
        super.stop();
    }
}
