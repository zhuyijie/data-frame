package com.baidu.ssp.type;

import com.baidu.ssp.exception.TypeConvertException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yijiezhu on 15-2-8.
 */
public final class Converters {

    private static final List<TypeConverter> CONVERTERS = new ArrayList<TypeConverter>();

    static {

        register(new TypeConverter<Object, String>() {
            @Override
            public Class<Object> from() {
                return Object.class;
            }

            @Override
            public Class<String> to() {
                return String.class;
            }

            @Override
            public String convert(Object from) {
                return String.valueOf(from);
            }
        });

        register(new TypeConverter<String, Boolean>() {
            @Override
            public Class from() {
                return String.class;
            }

            @Override
            public Class<Boolean> to() {
                return Boolean.class;
            }

            @Override
            public Boolean convert(String from) {
                return Boolean.valueOf(from);
            }
        });
        register(new TypeConverter<Number, Boolean>() {
            @Override
            public Class<Number> from() {
                return Number.class;
            }

            @Override
            public Class<Boolean> to() {
                return Boolean.class;
            }

            @Override
            public Boolean convert(Number from) {
                return from.intValue() != 0;
            }
        });
        register(new TypeConverter<Object, Boolean>() {

            @Override
            public Class<Object> from() {
                return Object.class;
            }

            @Override
            public Class<Boolean> to() {
                return Boolean.class;
            }

            @Override
            public Boolean convert(Object from) {
                return false;
            }
        });

        register(new TypeConverter<Boolean, Integer>() {
            @Override
            public Class<Boolean> from() {
                return Boolean.class;
            }

            @Override
            public Class<Integer> to() {
                return Integer.class;
            }

            @Override
            public Integer convert(Boolean from) {
                return from ? 1 : 0;
            }
        });
        register(new TypeConverter<Number, Integer>() {
            @Override
            public Class<Number> from() {
                return Number.class;
            }

            @Override
            public Class<Integer> to() {
                return Integer.class;
            }

            @Override
            public Integer convert(Number from) {
                return from.intValue();
            }
        });
        register(new TypeConverter<Object, Integer>() {

            @Override
            public Class<Object> from() {
                return Object.class;
            }

            @Override
            public Class<Integer> to() {
                return Integer.class;
            }

            @Override
            public Integer convert(Object from) {
                return Integer.valueOf(from.toString());
            }
        });

        register(new TypeConverter<Boolean, Long>() {
            @Override
            public Class<Boolean> from() {
                return Boolean.class;
            }

            @Override
            public Class<Long> to() {
                return Long.class;
            }

            @Override
            public Long convert(Boolean from) {
                return from ? 1L : 0L;
            }
        });
        register(new TypeConverter<Number, Long>() {
            @Override
            public Class<Number> from() {
                return Number.class;
            }

            @Override
            public Class<Long> to() {
                return Long.class;
            }

            @Override
            public Long convert(Number from) {
                return from.longValue();
            }
        });
        register(new TypeConverter<Object, Long>() {

            @Override
            public Class<Object> from() {
                return Object.class;
            }

            @Override
            public Class<Long> to() {
                return Long.class;
            }

            @Override
            public Long convert(Object from) {
                return Long.valueOf(from.toString());
            }
        });

        register(new TypeConverter<Number, Double>() {
            @Override
            public Class<Number> from() {
                return Number.class;
            }

            @Override
            public Class<Double> to() {
                return Double.class;
            }

            @Override
            public Double convert(Number from) {
                return from.doubleValue();
            }
        });
        register(new TypeConverter<Object, Double>() {

            @Override
            public Class<Object> from() {
                return Object.class;
            }

            @Override
            public Class<Double> to() {
                return Double.class;
            }

            @Override
            public Double convert(Object from) {
                return Double.valueOf(from.toString());
            }
        });
    }

    /**
     *
     * 注册一个类型装换器
     *
     * */
    public static void register(TypeConverter converter) {
        for (TypeConverter item : CONVERTERS) {
            if (item.from().equals(converter.from())
                    && item.to().equals(converter.to())) {
                return;
            }
        }
        CONVERTERS.add(converter);
    }

    public static <T> T convert(Object from, Class<T> targetType) {
        if (from == null) {
            return null;
        } else if (targetType.isAssignableFrom(from.getClass())) {
            return (T) from;
        }
        int distance = -1;
        TypeConverter choosed = null;
        for (TypeConverter item : CONVERTERS) {
            if (suitable(item, from.getClass(), targetType)) {
                int d = distance(from.getClass(), item.from());
                if (choosed == null || d < distance) {
                    choosed = item;
                    distance = d;
                }
            }
        }
        if (choosed != null) {
            return (T) choosed.convert(from);
        }
        throw new TypeConvertException("no converter found for "
                + from.getClass() + "->" + targetType.getClass());
    }

    private static boolean suitable(TypeConverter converter, Class fromType, Class targetType) {
        return converter.from().isAssignableFrom(fromType) && converter.to().equals(targetType);
    }

    private static int distance(Class<?> c, Class<?> emtc) {
        int distance = 0;
        if (!emtc.isAssignableFrom(c))
            return Integer.MAX_VALUE;

        while (c != emtc) {
            c = c.getSuperclass();
            distance++;
        }

        return distance;
    }
}
