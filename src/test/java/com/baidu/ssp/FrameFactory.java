package com.baidu.ssp;

import com.baidu.ssp.element.ListFrame;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yijiezhu on 15-2-7.
 */
public final class FrameFactory {
    public static Frame makeFrame(String[] titles, Object[][] data) {
        return ListFrame.newInstance(Lists.newArrayList(Iterables.transform(Arrays.asList(data), new Function<Object[], List<Object>>() {
            @Override
            public List<Object> apply(Object[] input) {
                return Arrays.asList(input);
            }
        })), Arrays.asList(titles));
    }

    public static final Frame frame1 = frame1();
    public static final Frame frame1_1 = frame1_1();
    public static final Frame frame2 = frame2();
    public static final Frame frame3 = frame3();

    public static Frame frame1() {
        String[] titles = new String[] {"t1", "t2", "t3", "t4"};
        Object[][] data = new Object[][] {
                {1, 1, 1, 1},
                {2, 2, 2, 2}
        };
        return makeFrame(titles, data);
    }

    public static Frame frame1_1() {
        String[] titles = new String[] {"t1", "t2", "t3", "t4"};
        Object[][] data = new Object[][] {
                {11, 11, 11, 11},
                {22, 22, 22, 22}
        };
        return makeFrame(titles, data);
    }

    public static Frame frame2() {
        String[] titles = new String[] {"t1", "t2", "t3_", "t4_"};
        Object[][] data = new Object[][] {
                {1, 1, 1, 1},
                {2, 2, 2, 2},
                {3, 3, 3, 3}
        };
        return makeFrame(titles, data);
    }

    public static Frame frame3() {
        String[] titles = new String[] {"t1", "t2", "st"};
        Object[][] data = new Object[][] {
                {1, 1, "20.11"},
                {1, 1, "3.55"},
                {5, 5, "10.00"},
                {6, 6, null}
        };
        return makeFrame(titles, data);
    }
}
