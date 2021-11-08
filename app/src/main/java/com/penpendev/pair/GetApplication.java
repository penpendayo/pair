package com.penpendev.pair;


import android.app.Application;

public class GetApplication extends Application {
    private static GetApplication instance;

    public static synchronized GetApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}

