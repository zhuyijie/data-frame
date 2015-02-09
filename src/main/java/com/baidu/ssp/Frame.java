package com.baidu.ssp;

import java.util.Iterator;
import java.util.List;

/**
 * Created by mojie on 2015/2/3.
 */
public interface Frame extends Iterable<Row> {

    int height();

    List<String> titles();

    Row row(int index);

    Column column(String title);

    Frame addColumn(Column column);

    Frame removeColumn(String title);

    Frame select(List<String> titles);

    Iterator<Row> iterator();

}
