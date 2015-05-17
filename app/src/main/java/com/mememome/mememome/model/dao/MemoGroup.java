package com.mememome.mememome.model.dao;

import com.orm.SugarRecord;

/**
 * Created by dangal on 4/9/15.
 */
public class MemoGroup extends SugarRecord<MemoGroup> {

    private String name;
    private int orderIndex;

    public MemoGroup() {
    }

    public MemoGroup(String name, int orderIndex) {
        this.name = name;
        this.orderIndex = orderIndex;
    }

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ORDER_INDEX = "order_index";

    // region Properties

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    // endregion Properties

    // region Methods


    // endregion Methods



}
