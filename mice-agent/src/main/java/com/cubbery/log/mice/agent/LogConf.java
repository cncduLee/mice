/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.agent;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.agent <br>
 * <b>类名称</b>： LogConf <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class LogConf {
    private String appName;
    private String regex;
    private int dateGroupNo;
    private int levelGroupNo;
    private int threadGroupNo;
    private int classGroupNo;
    private int msgGroupNo;
    private String dateFormat;

    public LogConf(String appName, String regex, int dateGroupNo, int levelGroupNo, int threadGroupNo, int classGroupNo, int msgGroupNo, String dateFormat) {
        this.appName = appName;
        this.regex = regex;
        this.dateGroupNo = dateGroupNo;
        this.levelGroupNo = levelGroupNo;
        this.threadGroupNo = threadGroupNo;
        this.classGroupNo = classGroupNo;
        this.msgGroupNo = msgGroupNo;
        this.dateFormat = dateFormat;
    }

    public String getAppName() {
        return appName;
    }

    public String getRegex() {
        return regex;
    }

    public int getDateGroupNo() {
        return dateGroupNo;
    }

    public int getLevelGroupNo() {
        return levelGroupNo;
    }

    public int getThreadGroupNo() {
        return threadGroupNo;
    }

    public int getClassGroupNo() {
        return classGroupNo;
    }

    public int getMsgGroupNo() {
        return msgGroupNo;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public String toString() {
        return "LogConf{" +
                "appName='" + appName + '\'' +
                ", regex='" + regex + '\'' +
                ", dateGroupNo=" + dateGroupNo +
                ", levelGroupNo=" + levelGroupNo +
                ", threadGroupNo=" + threadGroupNo +
                ", classGroupNo=" + classGroupNo +
                ", msgGroupNo=" + msgGroupNo +
                ", dateFormat='" + dateFormat + '\'' +
                '}';
    }
}
