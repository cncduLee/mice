/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.console.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.console.dao <br>
 * <b>类名称</b>： Page <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/15 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class Page<T> implements Serializable {
    private final List<T> content = new ArrayList<T>();
    private final Pageable pageable;// 参数
    private final long     total;

    public Page(Pageable pageable, long total) {
        this.pageable = pageable;
        this.total = total;
    }

    public int getNumber() {

        return pageable == null ? 0 : pageable.getPageNumber();
    }

    public int getSize() {
        return pageable == null ? 0 : pageable.getPageSize();
    }

    public int getTotalPages() {
        return getSize() == 0 ? 0 : (int) Math.ceil((double) total / (double) getSize());
    }

    public int getNumberOfElements() {
        return content.size();
    }

    public long getTotalElements() {
        return total;
    }

    public boolean hasPreviousPage() {
        return getNumber() > 0;
    }

    public boolean isFirstPage() {

        return !hasPreviousPage();
    }

    public boolean hasNextPage() {
        return ((getNumber() + 1) * getSize()) < total;
    }

    public boolean isLastPage() {
        return !hasNextPage();
    }

    public Iterator<T> iterator() {

        return content.iterator();
    }

    public List<T> getContent() {

        return Collections.unmodifiableList(content);
    }

    public Page<T> addAll(List<T> datas) {
        this.content.addAll(datas);
        return this;
    }

    public boolean hasContent() {
        return !content.isEmpty();
    }

    public Sort getSort() {
        return pageable == null ? null : pageable.getSort();
    }

}
