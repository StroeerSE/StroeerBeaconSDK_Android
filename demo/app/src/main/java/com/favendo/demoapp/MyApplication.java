package com.favendo.demoapp;

import android.app.Application;

import de.stroeer.proxity.StroeerProxityApi;

/**
 * Created by dustin on 05.02.16.
 */
public class MyApplication extends Application {

    private static StroeerProxityApi stroeerProxityApi;

    @Override
    public void onCreate() {
        super.onCreate();
        stroeerProxityApi = new StroeerProxityApi(this, "", MyActivity.class);
    }

    public static StroeerProxityApi getStroeerProxityApi() {
        return MyApplication.stroeerProxityApi;
    }

}
