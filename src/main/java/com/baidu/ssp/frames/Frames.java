package com.baidu.ssp.frames;

import com.baidu.ssp.Frame;
import com.baidu.ssp.Order;
import com.baidu.ssp.element.ListFrame;
import com.baidu.ssp.Row;
import com.baidu.ssp.element.ListFrameColumn;
import com.baidu.ssp.element.ListFrameRow;
import com.baidu.ssp.util.Utils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by mojie on 2015/2/6.
 */
public final class Frames {

    public static <K> Map<K, Collection<Row>> group(Frame frame,
                                                    List<String> by,
                                                    KeyGenerator<K> keyGenerator) {
        checkState(by != null && by.size() > 0);
        checkNotNull(keyGenerator);
        Map<K, Collection<Row>> keyToDataMap = new HashMap<K, Collection<Row>>();
        ListMultimap<K, Row> resultMaps = Multimaps.newListMultimap(keyToDataMap, new Supplier<List<Row>>() {
            @Override
            public List<Row> get() {
                return new ArrayList<Row>();
            }
        });
        Iterator<Row> iterator = frame.iterator();
        List<Integer> idx = Utils.indexOf(frame.titles(), by);
        while (iterator.hasNext()) {
            Row row = iterator.next();
            K key = keyGenerator.make(Utils.select(row.values(), idx));
            resultMaps.put(key, row);
        }
        return keyToDataMap;
    }

    public static Frame leftJoin(final Frame left, final Frame right, List<String> on) {
        checkState(left.titles().containsAll(on));
        checkState(right.titles().containsAll(on));
        if (left.height() == 0) {
            return ListFrame.newInstance(Collections.<List<Object>>emptyList(),
                    new ArrayList<String>() {
                        {
                            addAll(left.titles());
                            addAll(right.titles());
                        }
                    });
        }
        KeyGenerator<String> keyGenerator = new StringKeyGenerator();
        Map<String, Collection<Row>> rightGroup = group(right, on, keyGenerator);
        List<Row> data = new ArrayList<Row>();
        List<String> rightJoinTitles = FluentIterable.from(right.titles()).filter(Predicates.not(Predicates.in(on))).toList();
        for (Row leftRow : left) {
            String leftKey = makeKey(leftRow, on, keyGenerator);
            Collection<Row> rightRows = rightGroup.get(leftKey);
            if (rightRows != null) {
                for (Row rightRow : rightRows) {
                    data.add(leftRow.join(rightRow.select(rightJoinTitles)));
                }
            } else {
                data.add(leftRow.join(new ListFrameRow(rightJoinTitles, Arrays.asList(new Object[rightJoinTitles.size()]))));
            }
        }
        return ListFrame.newInstance(data);
    }

    public static Frame outJoin(final Frame left, final Frame right, List<String> on) {
        checkState(left.titles().containsAll(on));
        checkState(right.titles().containsAll(on));
        if (left.height() == 0 && right.height() == 0) {
            return ListFrame.newInstance(Collections.<List<Object>>emptyList(),
                    new ArrayList<String>() {
                        {
                            addAll(left.titles());
                            addAll(right.titles());
                        }
                    });
        }
        // first group data
        KeyGenerator<String> keyGenerator = new StringKeyGenerator();
        Map<String, Collection<Row>> rightGroup = group(right, on, keyGenerator);

        List<Row> data = new ArrayList<Row>();

        // the part titles
        List<String> rightJoinTitles = FluentIterable.from(right.titles()).filter(Predicates.not(Predicates.in(on))).toList();
        List<String> leftJoinTitles = FluentIterable.from(left.titles()).filter(Predicates.not(Predicates.in(on))).toList();

        // the row titles
        List<String> titles = Lists.newArrayList(left.titles());
        titles.addAll(rightJoinTitles);

        // record the keys that appeared in the left frame
        Set<String> walkedKeys = Sets.newHashSet();
        for (Row leftRow : left) {
            String leftKey = makeKey(leftRow, on, keyGenerator);
            walkedKeys.add(leftKey);
            Collection<Row> rightRows = rightGroup.get(leftKey);
            // there is corresponding elements in the right frame
            if (rightRows != null) {
                for (Row rightRow : rightRows) {
                    data.add(leftRow.join(rightRow.select(rightJoinTitles)));
                }
            } else {
                data.add(leftRow.join(new ListFrameRow(rightJoinTitles, Arrays.asList(new Object[rightJoinTitles.size()]))));
            }
        }
        for (Map.Entry<String, Collection<Row>> entry : rightGroup.entrySet()) {
            if (walkedKeys.contains(entry.getKey())) {
                continue;
            }
            // the remaining right rows
            for (Row rightRow : entry.getValue()) {
                data.add(rightRow.join(new ListFrameRow(leftJoinTitles,
                        Arrays.asList(new Object[leftJoinTitles.size()]))).select(titles));
            }
        }

        return ListFrame.newInstance(data);

    }

    public static Frame rightJoin(Frame left, Frame right, List<String> on) {
        return leftJoin(right, left, on);
    }

    public static Frame union(Frame up, Frame down) {
        if (down.height() == 0) {
            return up;
        }
        checkState(up.titles().equals(down.titles()));

        List<Row> newRows = new ArrayList<Row>(up.height() + down.height());

        for (Row upRow : up) {
            newRows.add(upRow);
        }

        for (Row downRow : down) {
            newRows.add(downRow);
        }

        return ListFrame.newInstance(newRows);
    }

    /**
     *
     * 从Frame中过滤出满足条件的行
     *
     * */
    public static Frame filter(Frame frame, Predicate<Row> condition) {
        List<Row> rows = FluentIterable.from(frame).filter(condition).toList();
        if (rows.isEmpty()) {
            return ListFrame.newInstance(Collections.<List<Object>>emptyList(), frame.titles());
        }
        return ListFrame.newInstance(rows);
    }


    public static Frame compute(Frame frame, String title, Function<Row, Object> function) {
        checkState(!frame.titles().contains(title));
        List<Object> newValues = new ArrayList<Object>(frame.height());
        for (Row row : frame) {
            newValues.add(function.apply(row));
        }
        return frame.addColumn(new ListFrameColumn(title, newValues));
    }

    public static Frame sort(Frame frame, final List<Order> orders) {
        if (frame.height() == 0) {
            return frame;
        }
        final int orderSize = orders.size();
        final List<String> titles = Lists.transform(orders, new Function<Order, String>() {
            @Override
            public String apply(Order input) {
                return input.getTitle();
            }
        });
        Ordering<Row> rowOrdering = Ordering.from(new Comparator<Row>() {
            @Override
            public int compare(Row row1, Row row2) {
                List<Object> row1Values = row1.select(titles).values();
                List<Object> row2Values = row2.select(titles).values();
                for (int i = 0;i < orderSize;i++) {
                    Comparable cell1 = (Comparable) row1Values.get(i);
                    Comparable cell2 = (Comparable) row2Values.get(i);
                    Ordering ordering = orders.get(i).getOrder();
                    int compareResult = ordering.compare(cell1, cell2);
                    if (compareResult != 0) {
                        return compareResult;
                    }
                }
                return 0;
            }
        });
        return ListFrame.newInstance(rowOrdering.immutableSortedCopy(frame));
    }

    public static Frame limit(Frame frame, int begin, int size) {
        int height = frame.height();
        if (height <= begin || size == 0) {
            return ListFrame.newInstance(Collections.<List<Object>>emptyList(), frame.titles());
        }
        List<Row> data = new ArrayList<Row>(size);
        for (int i = 0;i < size && i + begin < height;i++) {
            data.add(frame.row(i + begin));
        }
        return ListFrame.newInstance(data);
    }

    public static Frame aggregate(Frame frame, final List<String> by, final List<AggregateFunction> aggregates) {
        checkNotNull(frame);
        checkNotNull(by);
        checkNotNull(aggregates);

        final List<String> aggregateTitles = Lists.transform(aggregates, new Function<AggregateFunction, String>() {
            @Override
            public String apply(AggregateFunction input) {
                return input.title();
            }
        });
        List<String> fullTitles = new ArrayList<String>() {
            {
                addAll(by);
                addAll(aggregateTitles);
            }
        };
        final List<AggregateFunction> byFunctions = Lists.transform(by, new Function<String, AggregateFunction>() {
            @Override
            public AggregateFunction apply(String input) {
                return Aggregates.takeAnyOne(input);
            }
        });
        List<AggregateFunction> fullFunctions = new ArrayList<AggregateFunction>() {
            {
                addAll(byFunctions);
                addAll(aggregates);
            }
        };
        checkState(frame.titles().containsAll(fullTitles));
        Map<String, Collection<Row>> groupedRows = group(frame, by, new StringKeyGenerator());
        List<Row> newRows = new ArrayList<Row>(groupedRows.size());
        for (Collection<Row> aGroup : groupedRows.values()) {
            for (AggregateFunction aFunction : fullFunctions) {
                aFunction.reset();
            }
            for (Row aRow : aGroup) {
                List<Object> values = aRow.select(fullTitles).values();
                for (int i = 0;i < values.size();i++) {
                    fullFunctions.get(i).consume(values.get(i));
                }
            }
            newRows.add(new ListFrameRow(fullTitles,
                    Lists.newArrayList(Lists.transform(fullFunctions, new Function<AggregateFunction, Object>() {
                        @Override
                        public Object apply(AggregateFunction input) {
                            return input.getResult();
                        }
                    }))));
        }
        return ListFrame.newInstance(newRows);
    }

    /**
     * 生成索引键
     * */
    private static <T> T makeKey(Row row, List<String> on, KeyGenerator<T> keyGenerator) {
        return keyGenerator.make(row.select(on).values());
    }

    /**
     * Created by mojie on 2015/2/5.
     */
    public static class StringKeyGenerator implements KeyGenerator<String> {
        @Override
        public String make(List<Object> values) {
            return Joiner.on('\u200E').useForNull("\u200F").join(values);
        }
    }
}
