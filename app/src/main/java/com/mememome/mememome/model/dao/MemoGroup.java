package com.mememome.mememome.model.dao;

import java.util.Calendar;

/**
 * Created by dangal on 4/9/15.
 */
public class MemoGroup {

    private long id;
    private String Name;
    private int OrderIndex;

    // region Properties

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getOrderIndex() {
        return OrderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        OrderIndex = orderIndex;
    }

    // endregion Properties

    // region Methods

    @Override
    public String toString() {
        return Name;
    }

    // endregion Methods



}
