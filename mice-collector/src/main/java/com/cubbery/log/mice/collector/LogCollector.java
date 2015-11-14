/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.collector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.elasticsearch.ElasticSearchIndexRequestBuilderFactory;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.collector <br>
 * <b>类名称</b>： LogCollectorESBuilder <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class LogCollector implements ElasticSearchIndexRequestBuilderFactory {
    private static Logger logger = LoggerFactory.getLogger(LogCollector.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    private Context context;
    private ComponentConfiguration conf;


    @Override
    public IndexRequestBuilder createIndexRequest(Client client, String indexPrefix, String indexType, Event event) throws IOException {
        Map<String, String> headers = event.getHeaders();
        headers = headers == null ? new HashMap<String,String>(0) : headers;
        String typeApp = headers.get(Logs.APP) == null ? "NoDefine" : headers.get(Logs.APP);

        String body = new String(event.getBody());
        logger.info("Created Index :{}", body);
        logger.info(headers.toString());
        long d = Long.valueOf(headers.get(Logs.DATE));
        LogBean log = new LogBean();
        log.host = headers.get(Logs.HOST);
        log.level = headers.get(Logs.LEVEL);
        log.thread = headers.get(Logs.THREAD);
        log.time = d;
        log.clazz = headers.get(Logs.CLASS);
        log.msg = new String(event.getBody());
        Date date = new Date(d);
        String strDate = dateFormat.format(date);

        String json = gson.toJson(log);
        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(strDate, typeApp).setSource(json);
        return indexRequestBuilder;
    }

    @Override
    public void configure(Context context) {
        this.context = context;
    }

    @Override
    public void configure(ComponentConfiguration conf) {
        this.conf = conf;
    }

    class Logs {
        public static final String DATE = "vDate";
        public static final String THREAD = "vThread";
        public static final String LEVEL = "vLevel";
        public static final String APP = "vAPP";
        public static final String HOST= "host";
        public static final String CLASS = "vClass";
    }

    class LogBean implements Serializable {
        String host;
        String thread;
        String level;
        long time;
        String msg;
        String clazz;
    }
}