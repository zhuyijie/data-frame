package com.baidu.ssp.element;

import com.baidu.ssp.Column;

import java.util.List;

/**
 * Created by mojie on 2015/2/6.
 */
public class ListFrameColumn implements Column {

    private String title;

    private List<Object> values;

    public ListFrameColumn(String title, List<Object> values) {
        this.title = title;
        this.values = values;
    }

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public List<Object> values() {
        return this.values;
    }
}
