package com.x91tec.appshelf.components.services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Iterator;
import java.util.List;

/**
 * Created by oeager on 16-3-3.
 */
public class ServiceChecker {

    /**
     * This method is used to determine whether the service is running
     *
     * @param ctx       Interface to global information about an application environment
     * @param className name of the service
     * @return true if the service is running
     * @see android.app.Service
     */
    public static boolean isServiceRunning(Context ctx, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo si : servicesList) {
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    public static boolean stopRunningService(Context context, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(context, Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent_service != null) {
            ret = context.stopService(intent_service);
        }
        return ret;
    }

    public static void runningService(Context context,String className,Bundle bundle){
        Intent intent = new Intent();
        intent.setClassName(context, className);
        if(bundle!=null){
            intent.putExtras(bundle);
        }
        context.startService(intent);
    }

    public static void runningService(Context context,String action){
        Intent intent = new Intent(action);
        context.startService(intent);
    }
}
