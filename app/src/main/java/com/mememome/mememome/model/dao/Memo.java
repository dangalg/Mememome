package com.mememome.mememome.model.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.mememome.mememome.model.contentprovider.MememomeContract;

import java.io.Serializable;

/**
 * created by dangal on 4/9/15.
 */
public class Memo implements Serializable {

    private long id;
    private String name;
    private String text;
    private long created;
    private long updated;
    private long memoGroupId;

    public Memo() {
    }

    public Memo(String name, String text, long created, long updated, long memoGroupId) {
        this.name = name;
        this.text = text;
        this.created = created;
        this.updated = updated;
        this.memoGroupId = memoGroupId;
    }

    public static final String COLUMN_MEMO_NAME = "name";
    public static final String COLUMN_MEMO_TEXT = "text";
    public static final String COLUMN_MEMO_CREATED = "created";
    public static final String COLUMN_MEMO_UPDATED = "updated";
    public static final String COLUMN_MEMO_GROUP_ID = "group_id";

    // region Properties

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setUpdated(System.currentTimeMillis());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setUpdated(System.currentTimeMillis());
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public long getMemoGroupId() {
        return memoGroupId;
    }

    public void setMemoGroupId(long memoGroupId) {
        this.memoGroupId = memoGroupId;
    }

    // endregion Properties

    /**
     * Convenient method to get the objects data members in ContentValues object.
     * This will be useful for Content Provider operations,
     * which use ContentValues object to represent the data.
     *
     * @return
     */
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(MememomeContract.MemoEntry.COLUMN_MEMO_NAME, name);
        values.put(MememomeContract.MemoEntry.COLUMN_MEMO_TEXT, text);
        values.put(MememomeContract.MemoEntry.COLUMN_MEMO_CREATED, created);
        values.put(MememomeContract.MemoEntry.COLUMN_MEMO_UPDATED, updated);
        values.put(MememomeContract.MemoEntry.COLUMN_MEMO_GROUP_ID, memoGroupId);

        return values;
    }

    // Create a TvShow object from a cursor
    public static Memo fromCursor(Cursor curEmuticon) {
        String Name = curEmuticon.getString(curEmuticon.getColumnIndex(MememomeContract.MemoEntry.COLUMN_MEMO_NAME));
        String Text = curEmuticon.getString(curEmuticon.getColumnIndex(MememomeContract.MemoEntry.COLUMN_MEMO_TEXT));
        long Created = curEmuticon.getLong(curEmuticon.getColumnIndex(MememomeContract.MemoEntry.COLUMN_MEMO_CREATED));
        long Updated = curEmuticon.getLong(curEmuticon.getColumnIndex(MememomeContract.MemoEntry.COLUMN_MEMO_UPDATED));
        long memoGroupId = curEmuticon.getLong(curEmuticon.getColumnIndex(MememomeContract.MemoEntry.COLUMN_MEMO_GROUP_ID));

        return new Memo( Name,
                Text,
                Created,
                Updated,
                memoGroupId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Memo memo = (Memo) o;

//        if (name != pack.name) return false;
        if (!name.equals(memo.name)) return false;

        return true;
    }

//    @Override
//    public int hashCode() {
//        int result = name.hashCode();
//        result = 31 * result + year;
//        return result;
//    }

    @Override
    public String toString() {
        return name;
    }



    //TODO parse file to contentvalues and insert to db



    //TODO parse to file and upload to dropbox

}
