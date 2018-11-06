package com.cbox.facmodemanager;

import android.app.Application;

import org.xutils.x;

/**
 * Created by RyanJ on 2018/2/5.
 */

public class application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
