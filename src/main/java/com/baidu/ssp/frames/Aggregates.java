package com.baidu.ssp.frames;

import com.baidu.ssp.type.Converters;

/**
 * Created by yijiezhu on 15-2-8.
 */
public final class Aggregates {

    /**
     * the default sum function, use Double to hold the tmp result, then cast to the actually type
     * */
    public static AggregateFunction sum(final String title) {
        return new AggregateFunction() {

            Class type = null;
            Double result = 0.0;
            Boolean hasNull = false;

            /**
             * 在哪个字段上进行汇总
             */
            @Override
            public String title() {
                return title;
            }

            @Override
            public Object getResult() {
                if (hasNull) {
                    return null;
                } else {
                    return type != null ? Converters.convert(result, type) : null;
                }
            }

            @Override
            public void consume(Object value) {
                if (value == null) {
                    hasNull = true;
                } else {
                    if (type == null) {
                        type = value.getClass();
                    }
                    result += Converters.convert(value, Double.class);
                }
            }

            @Override
            public void reset() {
                type = null;
                result = 0.0;
                hasNull = false;
            }
        };
    }

    public static AggregateFunction takeAnyOne(final String title) {
        return new AggregateFunction() {
            Object value = null;
            @Override
            public String title() {
                return title;
            }

            @Override
            public Object getResult() {
                return value;
            }

            @Override
            public void consume(Object value) {
                this.value = value;
            }

            @Override
            public void reset() {
                this.value = null;
            }
        };
    }

}
