package com.mememome.mememome.model.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.mememome.mememome.model.data.MySQLiteHelper;

/**
 * Created by dangal on 4/25/15.
 */
public class MememomeProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MySQLiteHelper mOpenHelper;

    static final int MEMO = 100;
    static final int MEMO_WITH_NAME = 150;
    static final int MEMO_GROUP = 200;
    static final int MEMOS_BY_GROUP_ID = 250;

    private static final SQLiteQueryBuilder sMemoByNameQueryBuilder;

    static{
        sMemoByNameQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMemoByNameQueryBuilder.setTables(
                MememomeContract.MemoEntry.TABLE_NAME);
    }

    //location.location_setting = ?
    private static final String sMemoNameSelection =
            MememomeContract.MemoEntry.TABLE_NAME+
                    "." + MememomeContract.MemoEntry.COLUMN_MEMO_NAME + " = ? ";

    //location.location_setting = ?
    private static final String sMemoGroupIdSelection =
            MememomeContract.MemoEntry.TABLE_NAME+
                    "." + MememomeContract.MemoEntry.COLUMN_MEMO_GROUP_ID + " = ? ";

    private Cursor getMemoByName(Uri uri, String[] projection, String sortOrder) {
        String name = MememomeContract.MemoEntry.getNameFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMemoNameSelection;
        selectionArgs = new String[]{name};


        return sMemoByNameQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMemosByGroupId(Uri uri, String[] projection, String sortOrder) {
        String id = MememomeContract.MemoGroupEntry.getIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMemoGroupIdSelection;
        selectionArgs = new String[]{id};


        return sMemoByNameQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MememomeContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MememomeContract.PATH_MEMO, MEMO);
        matcher.addURI(authority, MememomeContract.PATH_MEMO + "/*", MEMO_WITH_NAME);
        matcher.addURI(authority, MememomeContract.PATH_MEMO_GROUP + "/#", MEMOS_BY_GROUP_ID);

        matcher.addURI(authority, MememomeContract.PATH_MEMO_GROUP, MEMO_GROUP);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MySQLiteHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "memos_group/#"
            case MEMOS_BY_GROUP_ID:
            {
                retCursor = getMemosByGroupId(uri, projection, sortOrder);
                break;
            }
//            // "memos/*"
            case MEMO_WITH_NAME: {
                retCursor = getMemoByName(uri, projection, sortOrder);
                break;
            }
            // "memo"
            case MEMO: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MememomeContract.MemoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "memo group"
            case MEMO_GROUP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MememomeContract.MemoGroupEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MEMOS_BY_GROUP_ID:
                return MememomeContract.MemoGroupEntry.CONTENT_TYPE;
            case MEMO_WITH_NAME:
                return MememomeContract.MemoEntry.CONTENT_ITEM_TYPE;
            case MEMO:
                return MememomeContract.MemoEntry.CONTENT_TYPE;
            case MEMO_GROUP:
                return MememomeContract.MemoGroupEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MEMO: {
                long _id = db.insert(MememomeContract.MemoEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MememomeContract.MemoEntry.buildMemoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MEMO_GROUP: {
                long _id = db.insert(MememomeContract.MemoGroupEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MememomeContract.MemoGroupEntry.buildMemoGroupUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MEMO:
                rowsDeleted = db.delete(
                        MememomeContract.MemoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEMO_GROUP:
                rowsDeleted = db.delete(
                        MememomeContract.MemoGroupEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MEMO:
                rowsUpdated = db.update(MememomeContract.MemoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MEMO_GROUP:
                rowsUpdated = db.update(MememomeContract.MemoGroupEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
