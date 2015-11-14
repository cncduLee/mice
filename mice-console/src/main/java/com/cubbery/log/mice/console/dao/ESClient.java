/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.console.dao;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.InvalidPropertiesFormatException;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.console.dao <br>
 * <b>类名称</b>： ESClient <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
@Component
@Scope("singleton")
public final class ESClient {
    private static final Logger logger = LoggerFactory.getLogger(ESClient.class);

    @Value("${es.host.list}")
    private String esHosts;

    @Value("${es.cluster.name}")
    private String clusterName;

    private TransportClient client;

    public ESClient(String esHosts,String clusterName) throws InvalidPropertiesFormatException {
        logger.info("############### Init EsClient Begin ###############");
        if(StringUtils.isNotBlank(this.esHosts)) {
            logger.error("Es Hosts Can't Be Null!");
            throw new InvalidPropertiesFormatException("Es Hosts Can't Be Null!");
        }
        if(StringUtils.isNotBlank(this.clusterName)) {
            logger.error("Es Cluster Name Can't Be Null!");
            throw new InvalidPropertiesFormatException("Es Cluster Name Can't Be Null!");
        }

        ImmutableSettings.Builder builder = ImmutableSettings.builder();
        builder.put("cluster.name", clusterName);
        client = new TransportClient(builder);

        String[] cells = esHosts.split(",");
        for (String cell : cells) {
            String[] hosts = cell.split(":");
            if (hosts.length > 2) {
                throw new InvalidPropertiesFormatException("es.host.list not well formatted: " + esHosts);
            }
            int port = hosts.length == 1 ? 9300 : Integer.valueOf(hosts[1]);//default
            logger.info("ES ready add server : {}:{}",hosts[0],port);
            client.addTransportAddress(new InetSocketTransportAddress(hosts[0], port));
            logger.info("ES finish add server : {}:{}",hosts[0],port);
        }
        logger.info("############### Init EsClient End ###############");
    }

    public String getEsHosts() {
        return esHosts;
    }

    public String getClusterName() {
        return clusterName;
    }

    public TransportClient getClient() {
        return client;
    }
}
