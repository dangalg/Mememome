package com.mememome.mememome;

import android.app.Application;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

/**
 * Created by dangal on 5/8/15.
 */
public class AppController extends Application {
    public static DropboxAPI<AndroidAuthSession> dropboxApi;

    public AppController() {

    }
}
