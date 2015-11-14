/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.console.common;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.console.dao <br>
 * <b>类名称</b>： Sort <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/15 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class Sort implements Iterable<Sort.Order>,Serializable {
    public static final SortOrder DEFAULT_DIRECTION = SortOrder.ASC;
    private final List<Order> orders;

    public Sort(Order... orders) {
        this(Arrays.asList(orders));
    }

    public Sort(List<Order> orders) {
        this.orders = orders;
    }

    public Sort(SortOrder direction, List<String> properties) {
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }
        this.orders = new ArrayList<Order>(properties.size());
        for (String property : properties) {
            this.orders.add(new Order(direction, property));
        }
    }

    public Sort(SortOrder direction, String... properties) {
        this(direction, Arrays.asList(properties));
    }

    public Sort and(Sort sort) {
        if (sort == null) {
            return this;
        }
        ArrayList<Order> these = new ArrayList<Order>(this.orders);
        for (Order order : sort) {
            these.add(order);
        }
        return new Sort(these);
    }

    public Order getOrderFor(String property) {
        for (Order order : this) {
            if (order.getProperty().equals(property)) {
                return order;
            }
        }
        return null;
    }

    @Override
    public Iterator<Order> iterator() {
        return this.orders.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sort orders1 = (Sort) o;

        if (orders != null ? !orders.equals(orders1.orders) : orders1.orders != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return orders != null ? orders.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) .append("orders", orders).toString();
    }

    public static class Order implements Serializable {
        private final SortOrder direction;
        private final String property;

        public Order(SortOrder direction, String property) {
            if (!StringUtils.hasText(property)) {
                throw new IllegalArgumentException("Property must not null or empty!");
            }
            this.property = property;
            this.direction = direction == null ? DEFAULT_DIRECTION : direction;
        }

        public Order(String property) {
            this(DEFAULT_DIRECTION,property);
        }

        public String getProperty() {
            return property;
        }

        public SortOrder getDirection() {
            return direction;
        }
    }
}
