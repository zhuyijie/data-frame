package com.baidu.ssp.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mojie on 2015/2/5.
 */
public final class Utils {
    /**
     * 在values中选取index在idx里面的
     * */
    public static List<Object> select(final List<Object> values, final List<Integer> idx) {
        return Lists.transform(idx, new Function<Integer, Object>() {
            @Override
            public Object apply(Integer input) {
                return values.get(input);
            }
        });
    }

    /**
     * 选取出strings里面每一个对象在full里面的index
     * */
    public static List<Integer> indexOf(final List<String> full, final List<String> strings) {
        return Lists.transform(strings, new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return full.indexOf(input);
            }
        });
    }

    public static <T> Map<T, Integer> reverseIndex(final List<T> list) {
        checkNotNull(list);
        Map<T, Integer> ret = new HashMap<T, Integer>();
        for (int i = 0; i < list.size(); i++) {
            ret.put(list.get(i), i);
        }
        return ret;
    }
}
