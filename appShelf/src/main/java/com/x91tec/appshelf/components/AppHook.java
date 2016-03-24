package com.x91tec.appshelf.components;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.developer.bsince.log.GOL;
import com.x91tec.appshelf.components.activities.ActivityLifecycleCallbacksCompat;
import com.x91tec.appshelf.components.activities.ActivityLifecycleCallbacksWrapper;
import com.x91tec.appshelf.components.activities.LifecycleCompatDispatcher;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by oeager on 16-3-2.
 */
public final class AppHook {

    private AppHook() {
    }

    public static AppHook get() {
        return AppHolder.IMPL;
    }

    private static class AppHolder {
        private final static AppHook IMPL = new AppHook();
    }

    private Application mApplication;

    private AppWatcher mWatcher;

    private int appCount;

    private final Stack<Activity> activityStack = new Stack<>();

    private Application.ActivityLifecycleCallbacks callbacksCompat;

    private ComponentCallbacks2 componentCallbacks2;

    public void ensureApplication(Application application) {
        if (mApplication == null) {
            mApplication = application;
        }
    }


    public void hookApplicationWatcher(Application application, AppWatcher appWatcher) {
        this.mWatcher = appWatcher;
        ensureApplication(application);
        if (AppEnvironment.DEBUG) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()   //检测Activity泄漏
                    .detectLeakedSqlLiteObjects()//检测数据库泄漏
                    .detectLeakedClosableObjects();//检测Closeable对象泄漏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder.detectLeakedRegistrationObjects();//检测注册对象的泄漏
            }

            StrictMode.setVmPolicy(builder.penaltyLog().build());
        }
        application.registerComponentCallbacks(componentCallbacks2 = new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                if (mWatcher != null) {
                    mWatcher.onTrimMemory(mApplication, level);
                }
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                if (mWatcher != null) mWatcher.onConfigurationChanged(mApplication, newConfig);
            }

            @Override
            public void onLowMemory() {
                if (mWatcher != null) mWatcher.onLowMemory(mApplication);
            }
        });
        application.registerActivityLifecycleCallbacks(callbacksCompat = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                joinActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                appCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                appCount--;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                popActivity(activity);
            }
        });

    }

    public int getAppCount() {
        return appCount;
    }

    public int getStackCount() {
        return activityStack.size();
    }

    public void onTerminate(Application application) {
        try {
            finishAllActivity();
            if (callbacksCompat != null) {
                application.unregisterActivityLifecycleCallbacks(callbacksCompat);
            }
            if (componentCallbacks2 != null) {
                application.unregisterComponentCallbacks(componentCallbacks2);
            }
            if (mWatcher != null) {
                mWatcher.onAppExit(application);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        } catch (Exception ignored) {
        }
    }

    /**
     * 添加Activity到堆栈
     */
    public static void joinActivity(Activity activity) {
        get().activityStack.add(activity);
        if (AppEnvironment.DEBUG)
            GOL.tag("AppHook").e("add activity:" + activity.getClass().getName());
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.peek();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack.isEmpty()) {
            return;
        }
        Activity activity = activityStack.pop();
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            this.activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }

        }
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            this.activityStack.remove(activity);
            if (AppEnvironment.DEBUG)
                GOL.tag("AppHook").e("remove activity:" + activity.getClass().getName());
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<? extends Activity> cls) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                iterator.remove();
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }

    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        while (!activityStack.isEmpty()) {
            Activity activity = activityStack.pop();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 获取指定的Activity
     */
    public Activity getActivity(Class<? extends Activity> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }


    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            finishAllActivity();
            if (mWatcher != null && mWatcher.onAppExit(mApplication)) {
                if (mApplication != null) {
                    if (callbacksCompat != null) {
                        mApplication.unregisterActivityLifecycleCallbacks(callbacksCompat);
                    }

                    if (componentCallbacks2 != null) {
                        mApplication.unregisterComponentCallbacks(componentCallbacks2);
                    }
                }

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }

        } catch (Exception ignored) {
        }
    }

    public boolean checkApplication() {
        if (mApplication == null) {
            GOL.e("mApplication is null,had you called onCreate method of this class on your own Application onCreated?");
            return false;
        }
        return true;
    }

    public static <App extends Application> App getApp() {
        Application app = get().mApplication;
        if (app == null) {
            Activity activity = get().currentActivity();
            if (activity == null) {
                return null;
            }
            app = activity.getApplication();
            get().ensureApplication(app);
        }
        return (App) app;
    }

    /**
     * this method is deprecated on 4.0 or above
     *
     * @param callback
     */
    @Deprecated
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksCompat callback) {

        if (!checkApplication()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            get().mApplication.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksWrapper(callback));
        } else {
            LifecycleCompatDispatcher.getDefault().registerActivityLifecycle(callback);
        }
    }

    /**
     * this method is deprecated on 4.0 or above
     *
     * @param callback
     */
    @Deprecated
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacksCompat callback) {
        if (!checkApplication()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            get().mApplication.unregisterActivityLifecycleCallbacks(new ActivityLifecycleCallbacksWrapper(callback));
        } else {
            LifecycleCompatDispatcher.getDefault().unregisterActivityLifecycle(callback);
        }
    }

    public String dumpStackInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Activity activity : activityStack) {
          stringBuilder.append(activity.getClass().getName()).append("\n");
        }
        return stringBuilder.toString();
    }

}
