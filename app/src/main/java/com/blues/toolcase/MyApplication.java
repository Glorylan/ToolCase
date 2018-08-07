package com.blues.toolcase;

import android.app.Application;
import android.content.Context;

/**
 * 用于全局的application
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
