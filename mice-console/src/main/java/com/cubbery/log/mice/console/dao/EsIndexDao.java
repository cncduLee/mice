/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.console.dao;

import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.console.dao <br>
 * <b>类名称</b>： EsIndexDao <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public interface EsIndexDao {
    ImmutableOpenMap<String, IndexMetaData> listAllIndex();
}

@Repository
class EsIndexDaoImp implements EsIndexDao {

    @Resource
    private ESClient esClient;

    @Override
    public ImmutableOpenMap<String, IndexMetaData> listAllIndex() {
        ImmutableOpenMap<String, IndexMetaData> indexes = esClient.getClient().admin().cluster()
                .prepareState().execute()
                .actionGet().getState()
                .getMetaData().indices();
        return indexes;
    }
}
