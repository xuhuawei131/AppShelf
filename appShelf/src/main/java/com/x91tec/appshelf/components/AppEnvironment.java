package com.x91tec.appshelf.components;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.x91tec.appshelf.BuildConfig;
import com.x91tec.appshelf.components.utils.AppUtils;

/**
 * Created by oeager on 16-3-2.
 */
public final class AppEnvironment {

    public final static boolean DEBUG;

    public final static String NAME;

    public final static String VERSION_NAME;

    public final static int VERSION_CODE;

    static {
        Application application = AppHook.getApp();
        if (application == null) {
            DEBUG = BuildConfig.DEBUG;
            NAME = null;
            VERSION_CODE = -1;
            VERSION_NAME = "UnSupport";
        } else {
            DEBUG = isDebug(application);
            PackageInfo packageInfo = AppUtils.getPackageInfo(application);
            if (packageInfo == null) {
                NAME = null;
                VERSION_CODE = -1;
                VERSION_NAME = "UnSupport";
            } else {
                int labelRes = packageInfo.applicationInfo.labelRes;
                NAME = application.getResources().getString(labelRes);
                VERSION_CODE = packageInfo.versionCode;
                VERSION_NAME = packageInfo.versionName;
            }

        }

    }

    public static String getAppName(Context context) {
        PackageInfo packageInfo = AppUtils.getPackageInfo(context);
        if (packageInfo != null) {
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        }
        return null;
    }

    /**
     * get the versionName of the application
     */
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = AppUtils.getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return null;
    }
    public static int getVersionCode(Context context) {
        PackageInfo packageInfo = AppUtils.getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return -1;
    }

    static boolean isDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
        }
        return false;
    }
}
