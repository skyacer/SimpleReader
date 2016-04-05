package com.rssreader.app.application;

import android.app.Application;

/**
 * Created by LuoChangAn on 16/4/5.
 */
public class RssApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppProfile.sContext = getApplicationContext();

    }
}
