/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.console.common;

import org.elasticsearch.search.sort.SortOrder;

import java.util.HashSet;
import java.util.Set;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.console.dao <br>
 * <b>类名称</b>： PageRequest <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/15 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class PageRequest<T> implements Pageable<T> {
    private final int page;
    private final int size;
    private final Set<Conn> params = new HashSet<Conn>();
    private final String app;
    private final String date;
    private Sort sort;
    private Class<T> clazz;

    public PageRequest(int page, int size,String app,String date) {
        this(page, size, null,app,date);
    }

    public PageRequest(int page, int size,SortOrder direction,String app,String date, String... properties) {

        this(page, size, new Sort(direction, properties),app,date);
    }

    public PageRequest(int page, int size, Sort sort,String app,String date) {
        if (0 > page) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        if (0 >= size) {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero!");
        }
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.app = app;
        this.date =date;
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public int getOffset() {
        return (page - 1) * size;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Set<Conn> getParams() {
        return params;
    }

    @Override
    public void addParam(Conn conn) {
        this.params.add(conn);
    }

    @Override
    public String getApp() {
        return this.app;
    }

    @Override
    public String getDate() {
        return this.date;
    }

    @Override
    public Class clazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
