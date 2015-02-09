package com.baidu.ssp;

import com.baidu.ssp.frames.AlphanumComparator;
import com.google.common.collect.Ordering;

/**
 * Created by yijiezhu on 15-2-6.
 */
public class Order {

    private String title;

    private Ordering order;

    private Order(String title, Ordering order) {
        this.title = title;
        this.order = order;
    }

    public static Order desc(String title) {
        return new Order(title, Ordering.from(new AlphanumComparator()).compound(Ordering.natural()).nullsLast().reverse());
    }

    public static Order asc(String title) {
        return new Order(title, Ordering.from(new AlphanumComparator()).compound(Ordering.natural()).nullsLast());
    }

    public static Order withOrder(String title, Ordering ordering) {
        return new Order(title, ordering);
    }

    public Ordering getOrder() {
        return order;
    }

    public String getTitle() {
        return title;
    }

}
