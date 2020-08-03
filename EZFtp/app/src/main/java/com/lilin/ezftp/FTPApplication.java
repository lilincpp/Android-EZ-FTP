package com.lilin.ezftp;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

public class FTPApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
