package com.baidu.ssp.frames;

import com.baidu.ssp.Row;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Created by yijiezhu on 15-2-7.
 */
public final class FramePredicates {

    private Predicate<Row> rowPredicate;

    public static Predicate<Row> single(final String title, final Predicate predicate) {
        return new Predicate<Row>() {
            @Override
            public boolean apply(Row input) {
                return predicate.apply(input.select(title));
            }
        };
    }

    private FramePredicates(Predicate<Row> rowPredicate) {
        this.rowPredicate = rowPredicate;
    }

    public static FramePredicates from(final String title, final Predicate predicate) {
        return new FramePredicates(single(title, predicate));
    }

    public FramePredicates compound(final String title, final Predicate predicate) {
        this.rowPredicate = Predicates.and(rowPredicate, single(title, predicate));
        return this;
    }

    public Predicate<Row> toPredicate() {
        return rowPredicate;
    }
}
