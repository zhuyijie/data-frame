package com.baidu.ssp;

import com.baidu.ssp.frames.Aggregates;
import com.baidu.ssp.frames.FramePredicates;
import com.baidu.ssp.frames.Frames;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import org.junit.Test;

import java.util.Arrays;

import static com.baidu.ssp.FrameFactory.frame1;
import static com.baidu.ssp.FrameFactory.frame1_1;
import static com.baidu.ssp.FrameFactory.frame2;
import static com.baidu.ssp.FrameFactory.frame3;
import static com.baidu.ssp.FrameFactory.makeFrame;
import static org.junit.Assert.assertEquals;

/**
 * Created by yijiezhu on 15-2-7.
 */
public class FrameTest {

    @Test
    public void testJoin() {
        assertEquals(Frames.outJoin(frame1, frame2, Arrays.asList("t1", "t2")),
                makeFrame(new String[]{"t4_", "t3", "t2", "t1", "t4", "t3_"},
                        new Object[][]{
                                {1, 1, 1, 1, 1, 1},
                                {2, 2, 2, 2, 2, 2},
                                {3, null, 3, 3, null, 3}
                        }));
        assertEquals(Frames.outJoin(frame1, frame3, Arrays.asList("t1", "t2")),
                makeFrame(new String[]{"t3", "t2", "t1", "t4", "st"}, new Object[][]{
                        {1, 1, 1, 1, "20.11"},
                        {1, 1, 1, 1, "3.55"},
                        {2, 2, 2, 2, null},
                        {null, 6, 6, null, null},
                        {null, 5, 5, null, "10.00"}
                }));
        assertEquals(Frames.outJoin(Frames.limit(frame1, 0, 0),
                        Frames.limit(frame2, 0, 0), Arrays.asList("t1", "t2")),
                makeFrame(new String[]{"t4_", "t3", "t2", "t1", "t4", "t3_"}, new Object[][]{}));
    }

    @Test
    public void testSort() {
        assertEquals(Frames.sort(frame3, Arrays.asList(Order.asc("st"))),
                makeFrame(new String[]{"t2", "t1", "st"}, new Object[][]{
                        {1, 1, "3.55"},
                        {5, 5, "10.00"},
                        {1, 1, "20.11"},
                        {6, 6, null}
                }));
        assertEquals(Frames.sort(frame3, Arrays.asList(Order.asc("t1"), Order.desc("st"))),
                makeFrame(new String[]{"t2", "t1", "st"}, new Object[][]{
                        {1, 1, "20.11"},
                        {1, 1, "3.55"},
                        {5, 5, "10.00"},
                        {6, 6, null}
                }));

        assertEquals(Frames.sort(Frames.limit(frame3, 0, 0), Arrays.asList(Order.asc("st"))),
                makeFrame(new String[]{"t2", "t1", "st"}, new Object[][]{}));
    }

    @Test
    public void testFilter() {
        assertEquals(Frames.filter(frame3, FramePredicates.single("t1", Predicates.in(Arrays.asList(5)))),
                makeFrame(new String[]{"t2", "t1", "st"}, new Object[][]{
                        {5, 5, "10.00"}
                }));
        assertEquals(Frames.filter(Frames.limit(frame3, 0, 0),
                        FramePredicates.single("t1", Predicates.in(Arrays.asList(5)))),
                makeFrame(new String[]{"t2", "t1", "st"}, new Object[][]{}));
    }

    @Test
    public void testCompute() {
        assertEquals(Frames.compute(frame1, "(+ t1 t2)", new Function<Row, Object>() {
                    @Override
                    public Object apply(Row input) {
                        Integer v1 = (Integer) input.select("t1");
                        Integer v2 = (Integer) input.select("t2");
                        return v1 + v2;
                    }
                }),
                makeFrame(new String[]{"t3", "t2", "t1", "t4", "(+ t1 t2)"},
                        new Object[][]{
                                {1, 1, 1, 1, 2},
                                {2, 2, 2, 2, 4}
                        }));
        assertEquals(Frames.compute(Frames.limit(frame1, 0, 0), "(+ t1 t2)", new Function<Row, Object>() {
                    @Override
                    public Object apply(Row input) {
                        Integer v1 = (Integer) input.select("t1");
                        Integer v2 = (Integer) input.select("t2");
                        return v1 + v2;
                    }
                }),
                makeFrame(new String[]{"t3", "t2", "t1", "t4", "(+ t1 t2)"},
                        new Object[][]{}));
    }

    @Test
    public void testUnion() {
        assertEquals(Frames.union(frame1, frame1_1),
                makeFrame(new String[]{"t1", "t2", "t3", "t4"},
                        new Object[][] {
                                {1, 1, 1, 1},
                                {2, 2, 2, 2},
                                {11, 11, 11, 11},
                                {22, 22, 22, 22}
                        }));
    }

    @Test
    public void testAggregate() {
        assertEquals(Frames.aggregate(frame3,
                        Arrays.asList("t2", "t1"), Arrays.asList(Aggregates.sum("st"))),
                makeFrame(new String[] {"t1", "st", "t2"},
                        new Object[][] {
                                {6, null, 6},
                                {1, "23.66", 1},
                                {5, "10.0", 5}
                        }));
    }
}
