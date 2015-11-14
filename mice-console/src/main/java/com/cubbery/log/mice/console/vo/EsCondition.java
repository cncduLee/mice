/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.console.vo;

import java.io.Serializable;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.console.vo <br>
 * <b>类名称</b>： EsCondition <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class EsCondition implements Serializable {
    private String app;
    private String msg;
    private String date;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "EsCondition{" +
                "app='" + app + '\'' +
                ", msg='" + msg + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
