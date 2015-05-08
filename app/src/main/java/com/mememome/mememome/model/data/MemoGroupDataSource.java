package com.mememome.mememome.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mememome.mememome.model.dao.MemoGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mememome.mememome.model.contentprovider.MememomeContract.MemoGroupEntry;

/**
 * Created by dangal on 4/9/15.
 */
public class MemoGroupDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] allColumns = {
            MemoGroupEntry._ID,
            MemoGroupEntry.COLUMN_MEMO_GROUP_NAME,
            MemoGroupEntry.COLUMN_MEMO_GROUP_ORDER_INDEX};

    public MemoGroupDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public MemoGroup createMemoGroup(String memoGroupName, int memoGroupOrderIndex) {
        ContentValues values = new ContentValues();
        values.put(MemoGroupEntry.COLUMN_MEMO_GROUP_NAME, memoGroupName);
        values.put(MemoGroupEntry.COLUMN_MEMO_GROUP_ORDER_INDEX, memoGroupOrderIndex);
        long insertId = database.insert(MemoGroupEntry.TABLE_NAME, null,
                values);
        Cursor cursor = database.query(MemoGroupEntry.TABLE_NAME,
                allColumns, MemoGroupEntry._ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MemoGroup newMemoGroup = cursorToMemoGroup(cursor);
        cursor.close();
        return newMemoGroup;
    }

    // TODO check if empty before deletion
    public void deleteMemoGroup(MemoGroup memoGroup) {
        long id = memoGroup.getId();
        System.out.println("Memo Group deleted with id: " + id);
        database.delete(MemoGroupEntry.TABLE_NAME, MemoGroupEntry._ID
                + " = " + id, null);
    }

    public List<MemoGroup> getAllMemoGroups() {
        List<MemoGroup> memoGroups = new ArrayList<MemoGroup>();

        Cursor cursor = database.query(MemoGroupEntry.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MemoGroup memoGroup = cursorToMemoGroup(cursor);
            memoGroups.add(memoGroup);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return memoGroups;
    }

    private MemoGroup cursorToMemoGroup(Cursor cursor) {
        MemoGroup memoGroup = new MemoGroup();
        memoGroup.setId(cursor.getLong(0));
        memoGroup.setName(cursor.getString(1));
        memoGroup.setOrderIndex(Integer.parseInt(cursor.getString(2)));
        return memoGroup;
    }
}
