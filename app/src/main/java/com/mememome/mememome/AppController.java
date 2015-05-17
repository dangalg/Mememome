package com.mememome.mememome;

import android.app.Application;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.orm.SugarApp;

/**
 * Created by dangal on 5/8/15.
 */
public class AppController extends SugarApp {
    public static DropboxAPI<AndroidAuthSession> dropboxApi;


    public static final String TAG = AppController.class
            .getSimpleName();

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }
}
