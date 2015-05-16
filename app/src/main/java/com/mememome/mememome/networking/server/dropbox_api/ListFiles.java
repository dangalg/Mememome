package com.mememome.mememome.networking.server.dropbox_api;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.mememome.mememome.AppController;
import com.mememome.mememome.model.dao.Memo;
import com.mememome.mememome.model.data.MemoDataSource;
import com.mememome.mememome.model.file_manager.FileManager;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by dangal on 5/8/15.
 */
public class ListFiles  extends AsyncTask<Void, Long, Boolean> {
    public final String TAG = ListFiles.class.getSimpleName();
    private String mPath;

    private Context mContext;

    private String mErrorMsg;

    private MemoDataSource memoDataSource ;


    public ListFiles(Context context, String dropboxPath) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();
        mPath = dropboxPath;
        Log.d(TAG, "I have run");
        memoDataSource = new MemoDataSource(mContext);
        try {
            memoDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
//            metadata(java.lang.String path, int fileLimit, java.lang.String hash, boolean list, java.lang.String rev)
            Entry existingEntries = AppController.dropboxApi.metadata("/" + mPath, 0, null, true, null);
            Log.d(TAG, "The file's rev is now: " + String.valueOf(existingEntries));
            ArrayList<Entry> files = new ArrayList<>();
            Memo m = new Memo();
            String filename = "";
            for(Entry entry : existingEntries.contents){
                //If file is not in data base add it

                filename = String.valueOf(entry.fileName());
                String[] strArr = filename.split("\\.");
                m = memoDataSource.getMemoByName(strArr[0]);
                if(m == null){

                    File file = new File(FileManager.getExternalDir() + filename);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    DropboxAPI.DropboxFileInfo info = AppController.dropboxApi.getFile(filename, null, outputStream, null);
                    Log.d(TAG, "Downloaded: " + info.getMetadata().rev);

//                    createMemo(String memoName, String memoText, long memoCreated, long memoUpdated, long memoGroupId)
                    String text = FileManager.readFromFile(mContext,FileManager.getExternalDir() + filename);
                    m = new Memo(strArr[0], text,
                                                                    System.currentTimeMillis(),
                                                                    System.currentTimeMillis(),
                                                                    1);

                    memoDataSource.createMemo(m.getName(), m.getText(), m.getCreated(), m.getUpdated(), m.getMemoGroupId());
                }

            }

            memoDataSource.close();


        } catch (DropboxUnlinkedException e) {
            // This session wasn't authenticated properly or user unlinked
            mErrorMsg = "This app wasn't authenticated properly.";
        } catch (DropboxFileSizeException e) {
            // File size too big to upload via the API
            mErrorMsg = "This file is too big to upload";
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Upload canceled";
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be
                // thumbnailed)
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
            } else {
                // Something else
            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mErrorMsg = "File Not Found! " + String.valueOf(e);
        }


        Log.d(TAG, "Error " + mErrorMsg);
        return false;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {

    }

    @Override
    protected void onPostExecute(Boolean result) {
    }
}
