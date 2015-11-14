/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.console.dao;

import com.cubbery.log.mice.console.common.Conn;
import com.cubbery.log.mice.console.common.Page;
import com.cubbery.log.mice.console.common.Pageable;
import com.cubbery.log.mice.console.common.Sort;
import com.cubbery.log.mice.console.vo.EsCondition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.console.dao <br>
 * <b>类名称</b>： EsSearchDao <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/14 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public interface EsSearchDao {
    /**
     * 简单查询
     * @param searchCondition
     * @param clazz
     * @param <T>
     * @return
     */
    <T> List<T> search(EsCondition searchCondition, Class<T> clazz);

    /**
     * 分页查询
     * @param pageable
     * @param <T>
     * @return
     */
    <T> Page<T> search(Pageable<T> pageable);
}

@Repository
class EsSearchDaoImpl implements EsSearchDao{
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    @Resource
    private ESClient esClient;

    @Override
    public <T> List<T> search(EsCondition searchCondition, Class<T> clazz) {
        SearchRequestBuilder builder = esClient.getClient().prepareSearch(searchCondition.getDate());//time
        builder.setTypes(searchCondition.getApp());//app
        if (StringUtils.isNotBlank(searchCondition.getMsg())) {
            builder.setPostFilter(FilterBuilders.queryFilter(new QueryStringQueryBuilder(searchCondition.getMsg()).field("msg")));
            builder.addHighlightedField("msg");
            builder.setHighlighterFilter(true);
        }
        builder.addSort("time", SortOrder.ASC);//sort
        builder.setFrom(0).setSize(200);
        SearchResponse response = builder.execute().actionGet();
        int hits = (int) response.getHits().getTotalHits();

        List<T> rtn = new ArrayList<T>(hits);
        for (SearchHit hit : response.getHits()) {
            T t = gson.fromJson(hit.getSourceAsString(), clazz);
            rtn.add(t);
        }
        return rtn;
    }

    @Override
    public <T> Page<T> search(Pageable<T> pageable) {
        Set<Conn> connSet = pageable.getParams();
        SearchRequestBuilder builder = esClient.getClient().prepareSearch(pageable.getDate());//time
        builder.setTypes(pageable.getApp());//app
        for(Conn conn : connSet) {
            builder.setPostFilter(FilterBuilders.queryFilter(new QueryStringQueryBuilder(conn.getValue()).field(conn.getKey())));
            if(conn.isHighlighted()) {
                builder.addHighlightedField(conn.getKey());
            }
        }
        builder.setHighlighterFilter(true);
        Sort sort = pageable.getSort();
        if(sort != null) {
            Iterator<Sort.Order> iterator = sort.iterator();
            while (iterator.hasNext()) {
                Sort.Order order = iterator.next();
                builder.addSort(order.getProperty(), order.getDirection());//sort
            }
        }
        builder.setFrom(pageable.getOffset()).setSize(pageable.getPageSize());
        SearchResponse response = builder.execute().actionGet();
        int hits = (int) response.getHits().getTotalHits();

        List<T> rtn = new ArrayList<T>(hits);
        for (SearchHit hit : response.getHits()) {
            T t = gson.fromJson(hit.getSourceAsString(), pageable.clazz());
            rtn.add(t);
        }
        return new Page<T>(pageable,hits).addAll(rtn);
    }
}

