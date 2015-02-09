package com.baidu.ssp.element;

import com.baidu.ssp.Column;
import com.baidu.ssp.Frame;
import com.baidu.ssp.Row;
import com.baidu.ssp.util.Utils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by mojie on 2015/2/5.
 */
public class ListFrame implements Frame {

    private List<List<Object>> data;

    private Map<String, Integer> titles2Idx;

    private List<String> titles;


    private ListFrame(List<List<Object>> data, Map<String, Integer> titles2Idx) {
        this.data = data;
        this.titles2Idx = titles2Idx;
        this.titles = ImmutableList.copyOf(titles2Idx.keySet());
    }

    public static Frame newInstance(List<List<Object>> data, List<String> titles) {
        checkNotNull(data, "data should not be null");
        checkNotNull(titles, "titles should not be null");
        List<List<Object>> newData = new ArrayList<List<Object>>(data.size());
        for (int i = 0;i < data.size();i++) {
            List<Object> row = Lists.newArrayList(data.get(i));
            checkState(row.size() == titles.size());
            newData.add(row);
        }
        Map<String, Integer> titles2Idx = new HashMap<String, Integer>();
        for (int i = 0;i < titles.size();i++) {
            titles2Idx.put(titles.get(i), i);
        }
        return new ListFrame(newData, titles2Idx);
    }

    public static Frame newInstance(List<Row> rows) {
        checkState(rows != null && rows.size() > 0);
        Row firstRow = rows.get(0);
        List<String> titles = firstRow.titles();
        List<List<Object>> data = new ArrayList<List<Object>>();
        for (Row row : rows) {
            checkState(titles.equals(row.titles()));
            data.add(row instanceof ListFrameRow ? row.values() : Lists.newArrayList(row.values()));
        }
        return new ListFrame(data, Utils.reverseIndex(titles));
    }

    @Override
    public int height() {
        return data.size();
    }

    @Override
    public List<String> titles() {
        return Collections.unmodifiableList(titles);
    }


    @Override
    public Row row(int index) {
        checkElementIndex(index, data.size());
        return new ListRow(titles, Collections.unmodifiableList(data.get(index)));
    }

    @Override
    public Column column(String title) {
        checkState(titles.contains(title));
        final Integer index = titles2Idx.get(title);
        return new ListFrameColumn(title, Lists.transform(data, new Function<List<Object>, Object>() {
            @Override
            public Object apply(List<Object> input) {
                return input.get(index);
            }
        }));
    }

    @Override
    public Frame addColumn(Column column) {
        checkState(!this.titles.contains(column.title()));
        checkState(column.values().size() == height());
        Integer columnCapacity = columnCapacity();
        Map<String, Integer> newTitleMap = Maps.newHashMap(titles2Idx);
        newTitleMap.put(column.title(), columnCapacity);
        for (int i = 0;i < height();i++) {
            data.get(i).add(column.values().get(i));
        }
        return new ListFrame(data, newTitleMap);
    }

    @Override
    public Frame removeColumn(String title) {
        checkState(titles.contains(title));
        Map<String, Integer> newTitleMap = Maps.newHashMap(titles2Idx);
        newTitleMap.remove(title);
        return new ListFrame(data, newTitleMap);
    }

    public Integer columnCapacity() {
        return data.isEmpty() ? Ordering.natural().max(titles2Idx.values()) : data.get(0).size();
    }

    @Override
    public Frame select(List<String> titles) {
        checkState(this.titles.containsAll(titles));
        Map<String, Integer> newTitleMap = Maps.newHashMap(this.titles2Idx);
        newTitleMap.keySet().retainAll(titles);
        return new ListFrame(data, newTitleMap);
    }


    @Override
    public Iterator<Row> iterator() {
        return new Itr();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Joiner joiner = Joiner.on(' ');
        Function paddingFunction = new Function<Object, Object>() {
            @Override
            public Object apply(Object input) {
                return Strings.padEnd(String.valueOf(input), 10, ' ');
            }
        };
        sb.append(joiner.join(Lists.transform(titles, paddingFunction)));
        sb.append('\n');
        for (final List<Object> row : data) {
            sb.append(joiner.join(Lists.transform(Lists.transform(titles, new Function<String, Object>() {
                @Override
                public Object apply(String input) {
                    return row.get(titles2Idx.get(input));
                }
            }), paddingFunction)));
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ListFrame)) {
            return false;
        }
        ListFrame other = (ListFrame) obj;
        if (!Sets.newHashSet(this.titles())
                .equals(Sets.newHashSet(other.titles()))) {
            return false;
        }
        other = (ListFrame) other.select(this.titles());
        if (this.height() != other.height()) {
            return false;
        }
        for (int i = 0;i < this.height();i++) {
            Row thisRow = this.row(i);
            Row otherRow = other.row(i);
            if (!thisRow.values().equals(otherRow.values())) {
                return false;
            }
        }
        return true;
    }

    private class ListRow implements Row {

        private List<String> titles;

        private List<Object> fullRow;

        public ListRow(List<String> titles, List<Object> fullRow) {
            this.titles = titles;
            this.fullRow = fullRow;
        }

        private final Function<String, Object> function =  new Function<String, Object>() {
            @Override
            public Object apply(String title) {
                return fullRow.get(titles2Idx.get(title));
            }
        };

        @Override
        public List<String> titles() {
            return Collections.unmodifiableList(this.titles);
        }

        @Override
        public List<Object> values() {
            return Lists.transform(titles, function);
        }

        @Override
        public Row select(List<String> subTitles) {
            checkState(this.titles.containsAll(subTitles));
            return new ListRow(subTitles, this.fullRow);
        }

        @Override
        public Object select(String title) {
            return fullRow.get(titles2Idx.get(title));
        }

        @Override
        public Row join(Row another) {
            return new ListFrameRow(titles(), values()).join(another);
        }

        @Override
        public Map<String, Object> mapView() {
            return Maps.toMap(this.titles, function);
        }
    }

    private class Itr implements Iterator<Row> {

        int current = 0;

        int size = height();


        public Itr() {
        }

        @Override
        public boolean hasNext() {
            return current < size;
        }

        @Override
        public Row next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return row(current++);

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
