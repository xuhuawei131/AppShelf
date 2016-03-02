package com.x91tec.appshelf.components;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;
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

    public static volatile AppHook mDelegate;

    private AppHook(){}

    public static AppHook get(){
        if(mDelegate==null){
            synchronized (AppHook.class){
                if(mDelegate==null){
                    mDelegate = new AppHook();
                }
            }
        }
        return mDelegate;
    }

    private Application mApplication;

    private AppWatcher mWatcher;

    private final Stack<Activity> activityStack = new Stack<>();

    public void ensureApplication(Application application){
        if(mApplication==null){
            mApplication = application;
        }
    }

    public void setWatcher(AppWatcher watcher){
        this.mWatcher = watcher;
    }

    public static void onCreate(Application application){
        get().ensureApplication(application);
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
    }

    public static void onTerminate(Application application){
        AppHook hook = get();
        hook.ensureApplication(application);
        AppWatcher watcher = hook.mWatcher;
        if(watcher!=null){
            watcher.onTerminate(hook.mApplication);
        }
    }

    public static void onLowMemory(Application application) {
        AppHook hook = get();
        hook.ensureApplication(application);
        AppWatcher watcher = hook.mWatcher;
        if(watcher!=null){
            watcher.onLowMemory(hook.mApplication);
        }
    }

    public static void onConfigurationChanged(Application application,Configuration newConfig) {
        AppHook hook = get();
        hook.ensureApplication(application);
        AppWatcher watcher = hook.mWatcher;
        if(watcher!=null){
            watcher.onConfigurationChanged(hook.mApplication, newConfig);
        }
    }

    /**
     * 添加Activity到堆栈
     */
    public static void joinActivity(Activity activity) {

        get().activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if(activityStack.isEmpty()){
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
            if (activityStack.contains(activity)) {
                this.activityStack.remove(activity);
            }
            if (!activity.isFinishing()) {
                activity.finish();
            }

        }
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            if (activityStack.contains(activity)) {
                this.activityStack.remove(activity);
            }
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
            if(mWatcher!=null&&mWatcher.onAppExit(mApplication)){
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            System.exit(0);
        } catch (Exception e) {
        }
    }

    public boolean checkApplication(){
        if(mApplication==null){
            GOL.e("mApplication is null,had you called onCreate method of this class on your own Application onCreated?");
            return false;
        }
        return true;
    }

    public static <App extends Application> App getApp() {
        Application app = get().mApplication;
        if(!get().checkApplication()){
            return null;
        }
       return  (App) get().mApplication;
    }


    public static void quitActivity(Activity activity) {
        get().popActivity(activity);
    }

    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksCompat callback) {

        if(!checkApplication()){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            get().mApplication.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksWrapper(callback));
        } else {
            LifecycleCompatDispatcher.getDefault().registerActivityLifecycle(callback);
        }
    }

    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacksCompat callback) {
        if(!checkApplication()){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
           get().mApplication.unregisterActivityLifecycleCallbacks(new ActivityLifecycleCallbacksWrapper(callback));
        } else {
            LifecycleCompatDispatcher.getDefault().unregisterActivityLifecycle(callback);
        }
    }
}
