package com.example.oeager.sample;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;

import com.developer.bsince.log.AndroidLogger;
import com.developer.bsince.log.GOL;
import com.x91tec.appshelf.components.AppHook;
import com.x91tec.appshelf.components.AppWatcher;
import com.x91tec.appshelf.components.activities.ActivityLifecycleCallbacksCompat;

/**
 * Created by oeager on 16-3-8.
 */
public class SampleApplication extends Application implements AppWatcher{

    @Override
    public void onCreate() {
        super.onCreate();
        GOL.addLog(new AndroidLogger());
        AppHook.get().hookApplicationWatcher(this,this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppHook.get().onTerminate(this);
    }

    @Override
    public boolean onAppExit(Application application) {
        return false;
    }

    @Override
    public void onTrimMemory(Application application, int level) {

    }

    @Override
    public void onLowMemory(Application application) {

    }

    @Override
    public void onConfigurationChanged(Application application, Configuration newConfig) {

    }
}
