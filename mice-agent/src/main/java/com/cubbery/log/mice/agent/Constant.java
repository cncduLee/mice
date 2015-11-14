/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.agent;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.agent <br>
 * <b>类名称</b>： Constant <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public interface Constant {
    static final String DEFAULT_TIME =  "^(\\d\\d-\\d\\d-\\d\\d\\.\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d).";
    static final String DEFAULT_THREAD = "\\[(.+?)\\].";
    static final String DEFAULT_LEVEL = "(INFO|DEBUG|ERROR|FATAL).";
    static final String DEFAULT_CLASS= "(\\S+)";
    static final String DEFAULT_MSG = "(.+)";

    // 用户配置的正则表达式，有默认值
    public static final String LOG_REGEX = "log.regex";
    public static final String DEFAULT_LOG_REGEX = DEFAULT_TIME + DEFAULT_THREAD + DEFAULT_LEVEL + DEFAULT_CLASS + DEFAULT_MSG;

    public static final String LOG_DATE_GROUP = "date.group";
    public static final int DEFAULT_LOG_DATE_GROUP = 1;

    public static final String LOG_THREAD_GROUP = "thread.group";
    public static final int DEFAULT_LOG_THREAD_GROUP = 2;

    public static final String LOG_LEVEL_GROUP = "level.group";
    public static final int DEFAULT_LOG_LEVEL_GROUP = 3;


    public static final String LOG_CLASS_GROUP = "class.group";
    public static final int DEFAULT_LOG_CLASS_GROUP = 4;

    public static final String LOG_MESSAGE_GROUP = "message.group";
    public static final int DEFAULT_LOG_MESSAGE_GROUP = 5;

    public static final String LOG_DATE_FORMAT = "date.format";
    public static final String DEFAULT_LOG_DATE_FORMAT = "yy-MM-dd.HH:mm:ss.SSS";
}
