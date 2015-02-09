package com.baidu.ssp.type;

/**
 * Created by yijiezhu on 15-2-8.
 */
public interface TypeConverter<F,T> {

    /**
     * the original type
     * */
    Class<F> from();

    /**
     * the target type
     * */
    Class<T> to();

    /**
     * cast the type
     * */
    T convert(F from);
}
