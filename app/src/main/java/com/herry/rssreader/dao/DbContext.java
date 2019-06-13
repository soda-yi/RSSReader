package com.herry.rssreader.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbContext {
    private static final String DB_NAME = "rss.db";
    private static DaoSession sDaoSession;

    public static void init(Context context) {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        sDaoSession = daoMaster.newSession();
    }

    public static DaoSession getsDaoSession() {
        return sDaoSession;
    }

    public static ArticleDao getArticleDao(){
        return sDaoSession.getArticleDao();
    }

    public static ChannelDao getChannelDao(){
        return sDaoSession.getChannelDao();
    }

}
