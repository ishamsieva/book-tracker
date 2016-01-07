package org.iliza.booktracker.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ishamsieva on 29/12/2015.
 */
public class MonthDayData {

    String month;
    List<Integer> days;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }

    public void addDay(Integer day) {
        if (days == null) {
            days = new LinkedList<>();
        }
        days.add(day);
    }
}
