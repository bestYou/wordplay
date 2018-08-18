package com.bestyou.wtc1;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }



    public static Context getContext() {
        return sContext;
    }
}
