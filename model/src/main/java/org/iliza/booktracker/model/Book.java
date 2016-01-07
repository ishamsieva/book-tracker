package org.iliza.booktracker.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ishamsieva on 27/12/2015.
 */
public class Book implements Serializable{

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
