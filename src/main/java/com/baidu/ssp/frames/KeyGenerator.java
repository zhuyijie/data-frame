package com.baidu.ssp.frames;

import java.util.List;

/**
 * Created by mojie on 2015/2/5.
 */
public interface KeyGenerator<K> {
    K make(List<Object> values);
}
