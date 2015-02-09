package com.baidu.ssp.element;

import com.baidu.ssp.Row;
import com.baidu.ssp.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 没有关联到表的行
 * Created by mojie on 2015/2/5.
 */
public class ListFrameRow implements Row {

    private List<String> titles;

    private List<Object> values;


    public ListFrameRow(List<String> titles, List<Object> values) {
        this.titles = titles;
        this.values = values;
    }

    @Override
    public List<String> titles() {
        return this.titles;
    }

    @Override
    public List<Object> values() {
        return this.values;
    }

    @Override
    public Row select(List<String> subTitles) {
        return new ListFrameRow(subTitles,
                Utils.select(values, Utils.indexOf(titles, subTitles)));
    }

    @Override
    public Object select(String title) {
        return values().get(titles().indexOf(title));
    }

    @Override
    public Row join(Row another) {
        List<String> newTitles = new ArrayList<String>(this.titles.size()
                + another.titles().size());
        List<Object> newValues = new ArrayList<Object>(newTitles.size());
        newTitles.addAll(this.titles);
        newValues.addAll(this.values);
        newTitles.addAll(another.titles());
        newValues.addAll(another.values());
        return new ListFrameRow(newTitles, newValues);
    }

    @Override
    public Map<String, Object> mapView() {
        int length = titles.size();
        Map<String, Object> retVal = new HashMap<String, Object>();
        for (int i = 0;i < length;i++) {
            retVal.put(this.titles.get(i), this.values.get(i));
        }
        return retVal;
    }
}
