package com.baidu.ssp;

import java.util.List;
import java.util.Map;

/**
 * Created by mojie on 2015/2/5.
 */
public interface Row {

    List<String> titles();

    List<Object> values();

    Row select(List<String> subTitles);

    Object select(String title);

    Row join(Row another);

    Map<String, Object> mapView();
}
