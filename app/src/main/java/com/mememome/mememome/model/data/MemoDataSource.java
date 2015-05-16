package com.mememome.mememome.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.mememome.mememome.AppController;
import com.mememome.mememome.model.dao.Memo;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mememome.mememome.model.contentprovider.MememomeContract.MemoEntry;
import com.mememome.mememome.model.file_manager.FileManager;
import com.mememome.mememome.networking.server.dropbox_api.UploadMemo;

/**
 * Created by dangal on 4/9/15.
 */
public class MemoDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    Context mContext;

    private String[] allColumns = {
            MemoEntry._ID,
            MemoEntry.COLUMN_MEMO_NAME,
            MemoEntry.COLUMN_MEMO_TEXT,
            MemoEntry.COLUMN_MEMO_UPDATED,
            MemoEntry.COLUMN_MEMO_CREATED,
            MemoEntry.COLUMN_MEMO_GROUP_ID};

    public MemoDataSource(Context context) {
        mContext = context;
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Memo createMemo(String memoName, String memoText, long memoCreated, long memoUpdated, long memoGroupId) {
        ContentValues values = new ContentValues();
        values.put(MemoEntry.COLUMN_MEMO_NAME, memoName);
        values.put(MemoEntry.COLUMN_MEMO_TEXT, memoText);
        values.put(MemoEntry.COLUMN_MEMO_CREATED, memoCreated);
        values.put(MemoEntry.COLUMN_MEMO_UPDATED, memoUpdated);
        values.put(MemoEntry.COLUMN_MEMO_GROUP_ID, memoGroupId);
        long insertId = database.insert(MemoEntry.TABLE_NAME, null,
                values);
        Cursor cursor = database.query(MemoEntry.TABLE_NAME,
                allColumns, MemoEntry._ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Memo newMemo = cursorToMemo(cursor);
        cursor.close();

        saveMemoTofFileAndUploadToDropbox(memoName, memoText);

        return newMemo;
    }

    private void saveMemoTofFileAndUploadToDropbox(String memoName, String memoText) {
        File file = FileManager.saveMemo(mContext, memoName, memoText);

        //TODO add groupname as folder in front of the name
        UploadMemo um = new UploadMemo(mContext, memoName, file);
        um.execute();
    }

    // TODO think if to delete or just mark as deleted
    public void deleteMemo(Memo memo) {
        long id = memo.getId();
        System.out.println("Memo deleted with id: " + id);
        database.delete(MemoEntry.TABLE_NAME, MemoEntry._ID
                + " = " + id, null);
    }

    public List<Memo> getAllMemos() {
        List<Memo> memos = new ArrayList<Memo>();

        Cursor cursor = database.query(MemoEntry.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Memo memo = cursorToMemo(cursor);
            memos.add(memo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return memos;
    }

    public List<String> getAllMemoNames() {
        List<String> memos = new ArrayList<String>();

        Cursor cursor = database.query(MemoEntry.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            memos.add(cursor.getString(1));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return memos;
    }

    public Memo getMemoByName(String name) {
        Memo memo = new Memo();

        String whereClause = MemoEntry.COLUMN_MEMO_NAME + " = ?";
        String[] whereArgs = new String[] {
                name
        };
        Cursor cursor = database.query(MemoEntry.TABLE_NAME,
                allColumns, whereClause, whereArgs, null, null, null);

        if(cursor.moveToFirst()){
            memo = cursorToMemo(cursor);
        }
        else
        {
            memo = null;
        }

        // make sure to close the cursor
        cursor.close();
        return memo;
    }

    public boolean saveMemo(Memo memo) {
        ContentValues values = new ContentValues();
        values.put(MemoEntry.COLUMN_MEMO_NAME, memo.getName());
        values.put(MemoEntry.COLUMN_MEMO_TEXT, memo.getText());
        values.put(MemoEntry.COLUMN_MEMO_UPDATED, System.currentTimeMillis());
        values.put(MemoEntry.COLUMN_MEMO_GROUP_ID, memo.getMemoGroupId());

        String whereClause = MemoEntry._ID + " = ?";
        String[] whereArgs = new String[] {
                String.valueOf(memo.getId())
        };

        int numberOfRowsAffected = database.update(MemoEntry.TABLE_NAME, values, whereClause,
                whereArgs);

        if(numberOfRowsAffected > 0) {
            saveMemoTofFileAndUploadToDropbox(memo.getName(), memo.getText());
            return true;
        }
        else
        {
            return false;
        }
    }

    private Memo cursorToMemo(Cursor cursor) {
        Memo memo = new Memo();
        memo.setId(cursor.getLong(0));
        memo.setName(cursor.getString(1));
        memo.setText(cursor.getString(2));
        memo.setCreated(Long.parseLong(cursor.getString(3)));
        memo.setUpdated(Long.parseLong(cursor.getString(4)));
        memo.setMemoGroupId(Long.parseLong(cursor.getString(5)));
        return memo;
    }
}
