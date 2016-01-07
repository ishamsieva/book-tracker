package org.iliza.booktracker.model;

import java.io.Serializable;

/**
 * Created by ishamsieva on 28/12/2015.
 */
public class Day implements Serializable {

    String date;
    String type; // START, READING, END, NOT_READING

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
