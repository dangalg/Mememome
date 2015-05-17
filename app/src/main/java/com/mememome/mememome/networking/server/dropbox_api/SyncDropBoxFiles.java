package com.mememome.mememome.networking.server.dropbox_api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
import com.mememome.mememome.convertions.StringConvertions;
import com.mememome.mememome.model.dao.Memo;
import com.mememome.mememome.model.file_manager.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dangal on 5/8/15.
 */
//TODO change to service!!!
public class SyncDropBoxFiles extends AsyncTask<Void, Long, Boolean> {

    public static final String RETRIEVE_FILES_FROM_DROPBOX = "retrieve_files_from_dropbox";
    public static final String FILES_UPDATED = "files_updated";
    public final String TAG = SyncDropBoxFiles.class.getSimpleName();
    private String mPath;

    private Context mContext;

    private String mErrorMsg;

    private boolean filesUpdatedLocally = false;



    public SyncDropBoxFiles(Context context, String dropboxPath) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();
        mPath = dropboxPath;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {

            Entry existingEntries = AppController.dropboxApi.metadata("/" + mPath, 0, null, true, null);
            Log.d(TAG, "The file's rev is now: " + String.valueOf(existingEntries));
            ArrayList<Entry> files = new ArrayList<>();

            filesUpdatedLocally = updateLocalFilesFromDropBox(existingEntries);

            filesUpdatedLocally = updateDropBoxFromLocalFiles(existingEntries);

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

        if(filesUpdatedLocally){
            // Send intent response of finished refresh and files updated
            Intent intent = new Intent(RETRIEVE_FILES_FROM_DROPBOX);
            intent.putExtra(FILES_UPDATED, filesUpdatedLocally);
            LocalBroadcastManager.getInstance(AppController.getInstance()).sendBroadcast(intent);
        }


        Log.d(TAG, "Error " + mErrorMsg);
        return false;
    }

    private boolean updateDropBoxFromLocalFiles(Entry existingEntries) throws FileNotFoundException, DropboxException {

        boolean localFilesUpdated = false;
        String filename = "";
        String memoName = "";
        boolean fileNotFoundOnDropBox = true;

        // go through all local files and check if they are on dropbox. If not then upload them
        for(Memo memo : Memo.listAll(Memo.class)){
            for(Entry entry : existingEntries.contents) {

                //If file is not in data base add it
                filename = String.valueOf(entry.fileName());
                String[] strArr = filename.split("\\.");
                memoName = strArr[0];
                if(memo.getName() == memoName){
                    // file found, check if local version is newer and update if neccessary
                    fileNotFoundOnDropBox = false;
                }
            }
            if(fileNotFoundOnDropBox){
                // Upload file to dropbox
                uploadFileToDropBox(memo);

            }
            fileNotFoundOnDropBox = true;
        }

        return localFilesUpdated;
    }

    private boolean updateLocalFilesFromDropBox(Entry existingEntries) throws DropboxException, FileNotFoundException {

        boolean localFilesUpdated = false;

        List<Memo> memos;
        Memo m = new Memo();
        String filename = "";
        for(Entry entry : existingEntries.contents){
            //If file is not in data base add it

            filename = String.valueOf(entry.fileName());
            String[] strArr = filename.split("\\.");
            memos = Memo.find(Memo.class, Memo.COLUMN_NAME + " = ?", strArr[0]);
            if(memos.size() == 0){
                downloadFileFromDropbox(filename, entry, strArr[0]);
                localFilesUpdated = true;
            }
            else{
                m = memos.get(0);
                // if Memo on dropbox is newer then local Memo update local Memo
                if(StringConvertions.stringDateToMillis(entry.modified) > m.getUpdated()){
                    // update file locally
                    downloadFileFromDropbox(filename, entry, strArr[0]);
                    localFilesUpdated = true;

                }
                // if local Memo is newer then Memo on dropbox update Memo on dropbox
                else if(StringConvertions.stringDateToMillis(entry.modified) < m.getUpdated()){
                    // Update file on dropbox
                    uploadFileToDropBox(m);
                }
            }

        }

        return localFilesUpdated;
    }

    private void uploadFileToDropBox(Memo memo) throws FileNotFoundException, DropboxException {
        // Upload file to dropbox
        File localFile = new File(memo.getLocalFilePath());
        if(memo.getLocalFilePath().equals("")){
            memo.setLocalFilePath(FileManager.getExternalDir() + memo.getName());
        }
        if(localFile == null) {
            // Create local file from Memo
            localFile = FileManager.writeToFile(memo.getLocalFilePath(),memo.getText());
        }
        if(localFile != null) {
            FileInputStream fis = new FileInputStream(localFile);
            DropboxAPI.Entry mRequest = AppController.dropboxApi.putFileOverwrite(mPath, fis,
                    localFile.length(), null);

            // update local Memo in db
            memo.setUpdated(StringConvertions.stringDateToMillis(mRequest.modified));
            memo.setRev(mRequest.rev);
            memo.setHash(mRequest.hash);
            memo.setDropboxFilePath(mRequest.path);
            memo.save();
        }

    }

    private void downloadFileFromDropbox(String filename, Entry entry, String name) throws FileNotFoundException, DropboxException {
        // Download file from dropbox
        Memo m;
        String localFilePath = FileManager.getExternalDir() + filename;
        File file = new File(localFilePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        DropboxAPI.DropboxFileInfo info = AppController.dropboxApi.getFile(filename, null, outputStream, null);
        Log.d(TAG, "Downloaded: " + info.getMetadata().rev);

        // add local Memo to db

        String text = FileManager.readFromFile(mContext,localFilePath);
        m = new Memo(name, text,
                StringConvertions.stringDateToMillis(entry.modified),
                StringConvertions.stringDateToMillis(entry.modified),
                entry.rev,
                entry.hash,
                1,
                localFilePath,
                entry.path);
        m.save();
    }

    @Override
    protected void onProgressUpdate(Long... progress) {

    }

    @Override
    protected void onPostExecute(Boolean result) {
    }
}
