package com.x91tec.appshelf.components.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by oeager on 16-3-3.
 */
public class LaunchUtils {
    public static boolean LaunchApp(Context context, String packageName) {
        Intent mainIntent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (mainIntent == null) {
            return false;
        }
        context.startActivity(mainIntent);
        return true;
    }

    public static boolean isLauncherActivityExisted(Context context, String packageName){
        Intent mainIntent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        return mainIntent!=null;
    }



    public static boolean LaunchActivityOfApp(Context context, String packageName,
                                          String activityName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, activityName);
        intent.setComponent(cn);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isActivityLaunched(Context context, Class activityClass) {
        Intent mainIntent = new Intent(context, activityClass);
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(1);
        if (appTask.size() > 0
                && appTask.get(0).baseActivity
                .equals(mainIntent.getComponent())) {
            return true;
        } else {
            return false;
        }
    }


}
