package com.baidu.ssp.frames;

/**
 * Created by yijiezhu on 15-2-8.
 */
public interface AggregateFunction {

    /**
     * 在哪个字段上进行汇总
     * */
    String title();
    /**
     * 获取聚合的结果
     * */
    Object getResult();

    /**
     * 接受一个值
     * */
    void consume(Object value);

    /**
     * 设置为初始状态
     * */
    void reset();
}
