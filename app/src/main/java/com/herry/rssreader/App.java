package com.herry.rssreader;

import android.app.Application;

import com.herry.rssreader.dao.DbContext;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DbContext.init(this);
    }
}
