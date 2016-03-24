package com.x91tec.appshelf.components.activities;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by oeager on 2015/6/5.
 * email: oeager@foxmail.com
 */
@Deprecated
public interface ActivityLifecycleCallbacksCompat {

    void onActivityCreated(Activity activity, Bundle savedInstanceState);
    void onActivityStarted(Activity activity);
    void onActivityResumed(Activity activity);
    void onActivityPaused(Activity activity);
    void onActivityStopped(Activity activity);
    void onActivitySaveInstanceState(Activity activity, Bundle outState);
    void onActivityDestroyed(Activity activity);
}
