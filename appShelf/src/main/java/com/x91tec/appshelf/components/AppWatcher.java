package com.x91tec.appshelf.components;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by oeager on 16-3-2.
 */
public interface AppWatcher {

    void onTerminate(Application application);

    //need kill the process?
    boolean onAppExit(Application application);

    void onLowMemory(Application application);

    void onConfigurationChanged(Application application,Configuration newConfig);
}
