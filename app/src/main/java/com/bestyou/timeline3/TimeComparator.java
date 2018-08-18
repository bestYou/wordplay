package com.bestyou.timeline3;

import java.util.Comparator;

public class TimeComparator implements Comparator<TimeData> {
    @Override
    public int compare(TimeData td1, TimeData td2) {
        return td2.getPosttime().compareTo(td1.getPosttime());
    }
}
