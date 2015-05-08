package com.mememome.mememome.model.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mememome.mememome.model.contentprovider.MememomeContract.MemoEntry;
import com.mememome.mememome.model.contentprovider.MememomeContract.MemoGroupEntry;

/**
 * Created by dangal on 4/9/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mememome.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE =

            // Table Memos
            "create table "
            + MemoEntry.TABLE_NAME + "("
            + MemoEntry._ID + " integer primary key autoincrement, "
            + MemoEntry.COLUMN_MEMO_NAME + " text not null, "
            + MemoEntry.COLUMN_MEMO_TEXT + " text not null, "
            + MemoEntry.COLUMN_MEMO_CREATED + " integer not null, "
            + MemoEntry.COLUMN_MEMO_UPDATED + " integer not null, "
            + MemoEntry.COLUMN_MEMO_GROUP_ID + " integer not null, "
            + " FOREIGN KEY ("+MemoEntry.COLUMN_MEMO_GROUP_ID+") REFERENCES "+MemoGroupEntry.TABLE_NAME+" ("+MemoGroupEntry._ID+")); "

            // Table Memo Groups
            + "create table "
            + MemoGroupEntry.TABLE_NAME + "("
            + MemoGroupEntry._ID + " integer primary key autoincrement, "
            + MemoGroupEntry.COLUMN_MEMO_GROUP_NAME + " text not null,"
            + MemoGroupEntry.COLUMN_MEMO_GROUP_ORDER_INDEX + " integer not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will save all old data");
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMOS);
//        onCreate(db);
    }


}
