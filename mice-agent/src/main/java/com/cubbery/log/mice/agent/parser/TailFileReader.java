/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.agent.parser;

import com.cubbery.log.mice.agent.LogConf;
import com.cubbery.log.mice.agent.utils.StringUtils;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.agent.parser <br>
 * <b>类名称</b>： TailFileReader <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class TailFileReader {
    private static final Logger logger = LoggerFactory.getLogger(TailFileReader.class);
    public static final String LINE_SEPARATOR = "\r\n";

    private final BufferedReader reader;
    private final StringBuilder stringBuilder = new StringBuilder();
    private final Pattern pattern;
    private Matcher matcher;
    private String line;
    private Map<String, String> headers;
    private String strDate;
    private final LogConf logConf;
    private final SimpleDateFormat simpleDateFormat;
    private final String zero = "0";


    public TailFileReader(BufferedReader reader, LogConf logConf) {
        this.reader = reader;
        this.pattern = Pattern.compile(logConf.getRegex());
        this.logConf = logConf;
        this.simpleDateFormat = new SimpleDateFormat(logConf.getDateFormat());
    }

    private HashMap<String, String> getHeaders(Matcher matcher) {
        strDate = matcher.group(logConf.getDateGroupNo());
        HashMap<String, String> rtn = new HashMap<String, String>();
        try {
            rtn.put(Logs.DATE, String.valueOf(simpleDateFormat.parse(strDate).getTime()));
        } catch (ParseException e) {
            logger.error("parse error", e);
            rtn.put(Logs.DATE, zero);
        }
        rtn.put(Logs.THREAD, matcher.group(logConf.getThreadGroupNo()));
        rtn.put(Logs.LEVEL, matcher.group(logConf.getLevelGroupNo()));
        rtn.put(Logs.CLASS, matcher.group(logConf.getClassGroupNo()));
        rtn.put(Logs.APP, logConf.getAppName());
        return rtn;
    }

    private String getMsg(Matcher matcher) {
        return matcher.group(logConf.getMsgGroupNo());
    }


    /**
     * 1.需要判断是否是有打印堆栈信息，如果有打印堆栈信息，则继续读取下一行
     * 2.条件1需要有一个往下最大读取行数判断，以免oom 或者出bug
     *
     * @return 如果在行尾，有可能返回null，不代表文件结束
     * @throws java.io.IOException
     */
    public Event produceEvent() throws IOException {
        while (true) {
            line = reader.readLine();
            if (StringUtils.isEmpty(line)) {
                logger.info("Sleep 50 ms");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    logger.error("error", e);
                    return null;
                }
            } else {
                // 判断是不是一个新的log，如果是新的log，则返回上一条
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    // 如果找到，则StringBuilder 的消息成功
                    if (stringBuilder.length() > 0) {
                        // 如果之前的消息存在，则返回
                        Event event = EventBuilder.withBody(stringBuilder.toString().getBytes(), headers);
                        logger.info("create event : {}", stringBuilder);
                        // 确保清除上次事件的缓存
                        stringBuilder.delete(0, stringBuilder.length());
                        stringBuilder.append(getMsg(matcher));
                        headers = getHeaders(matcher);
                        return event;
                    } else {
                        // 如果之前没有内容，则初始化一个
                        logger.info("init first line !");
                        headers = getHeaders(matcher);
                        stringBuilder.append(getMsg(matcher));
                    }
                } else {
                    if (stringBuilder.length() > 0) {
                        // 如果没找到，则append上
                        stringBuilder.append(LINE_SEPARATOR);
                        stringBuilder.append(line);
                        logger.info("append to last line");
                    } else {
                        // stringBuilder还没有初始化，证明数据有问题，跳过
                        logger.warn("skip !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! {}",line);
                    }
                }
            }
        }
    }
}

