/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.console.service;

import com.cubbery.log.mice.console.dao.EsIndexDao;
import com.google.common.cache.*;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.SerializationUtils;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.console.service <br>
 * <b>类名称</b>： IndexService <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/15 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public interface IndexService {
    /**
     * 获取所有app
     *
     * @return
     */
    Set<String> getAllApp();

    /**
     * 获取所有index
     *
     * @return
     */
    Set<String> getAllIndex();
}

@Component
class IndexServiceImpl implements IndexService {
    Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);

    @Resource
    EsIndexDao esIndexDao;

    private LoadingCache<String,List<String>> indexApp = CacheBuilder.newBuilder()
            .maximumSize(200)//设置缓存最大容量为100
            .expireAfterWrite(30, TimeUnit.MINUTES)//设置写缓存后30分钟过期，过期后会触发removalListener
            .concurrencyLevel(4)//并发级别为4，并发级别是指可以同时写缓存的线程数
            .removalListener(new RemovalListener<Object, Object>() {//这段代码可以移除
                @Override
                public void onRemoval(RemovalNotification<Object, Object> notification) {
                    refreshCache();
                    logger.debug("config was removed with key" + notification.getKey() + " cause is :" + notification.getCause());
                }
            })
            .build(new CacheLoader<String, List<String>>() {//如果缓存中根据Key没有取到值，那么执行这里面的操作了
                @Override
                public List<String> load(String key) throws Exception {
                    return null;
                }
            });

    @Override
    public Set<String> getAllApp() {
        Collection<List<String>> allApps = indexApp.asMap().values();
        Set<String> all = new HashSet<String>();
        for(List<String> cell : allApps) {
            all.addAll(cell);
        }
        return all;
    }

    @Override
    public Set<String> getAllIndex() {
        return indexApp.asMap().keySet();
    }

    //重新刷新
    private synchronized void refreshCache() {
        ImmutableOpenMap<String, IndexMetaData> indexes = esIndexDao.listAllIndex();
        // 保存所有app
        for (ObjectObjectCursor<String, IndexMetaData> index : indexes) {
            ArrayList<String> apps = new ArrayList<String>();
            for (ObjectObjectCursor<String, MappingMetaData> mappings : index.value.getMappings()) {
                apps.add(mappings.key);
            }
            // 保存每个index的app
            indexApp.put(index.key, apps);
            logger.info("Index [{}]refresh app to cache : {}", index.key, index.value.getMappings());
        }
    }

}
