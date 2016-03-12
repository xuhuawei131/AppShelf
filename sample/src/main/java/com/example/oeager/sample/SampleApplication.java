package com.example.oeager.sample;

import android.app.Application;
import android.content.res.Configuration;

import com.developer.bsince.log.AndroidLogger;
import com.developer.bsince.log.GOL;
import com.x91tec.appshelf.components.AppHook;

/**
 * Created by oeager on 16-3-8.
 */
public class SampleApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        AppHook.onCreate(this);
        GOL.addLog(new AndroidLogger());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppHook.onTerminate(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        AppHook.onLowMemory(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AppHook.onConfigurationChanged(this,newConfig );
    }
}
