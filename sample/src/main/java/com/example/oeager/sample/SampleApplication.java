package com.example.oeager.sample;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;

import com.developer.bsince.log.AndroidLogger;
import com.developer.bsince.log.GOL;
import com.x91tec.appshelf.components.AppHook;
import com.x91tec.appshelf.components.activities.ActivityLifecycleCallbacksCompat;

/**
 * Created by oeager on 16-3-8.
 */
public class SampleApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        AppHook.onCreate(this);
        GOL.addLog(new AndroidLogger());
        ActivityLifecycleCallbacksCompat compat =new ActivityLifecycleCallbacksCompat() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onPostCreate(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

                AppHook.get().dumpStackInfo();
                GOL.tag("AppHook").e("-------------------------");
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
        AppHook.get().registerActivityLifecycleCallbacks(compat);
//        AppHook.get().unregisterActivityLifecycleCallbacks(compat);
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
