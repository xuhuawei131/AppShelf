package com.x91tec.appshelf.components.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is an auxiliary class associated with the app operation</p>
 *
 * @author oeager
 * @since 1.0
 */
public class AppUtils {
    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }



    /**
     * get the packageInfo of the application
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        return info;
    }

    /**
     * install an app on user's mobile phone by the path of the apk.
     *
     * @param apkFilePath the path of the apk file;
     */
    public static void installApk(Context context, String apkFilePath) {
        File apkFile = new File(apkFilePath);
        if (!apkFile.exists()) {
            Toast.makeText(context, "apk file is not exist", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.fromFile(apkFile);
        installApk(context, uri);
    }

    public static void installApk(Context context, Uri uri) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(i);
    }

    /**
     * uninstall the application by packageName
     *
     * @param packageName packageName
     */
    public static void uninstallApk(Context cx, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        cx.startActivity(intent);
    }

    public static String getDeviceId(Context mContext) {
        TelephonyManager tel = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tel.getDeviceId();
    }

    public static String getPhoneType() {
        return android.os.Build.MODEL;
    }


    public static Drawable getAppIconDrawable(Context ctx, String pkgName) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(pkgName, 0);
            return pi.applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    public static Drawable getAppIconDrawable(Context ctx, PackageInfo pi) {
        PackageManager pm = ctx.getPackageManager();
        if (pi != null) {
            return pi.applicationInfo.loadIcon(pm);
        }
        return null;
    }

    public static List<PackageInfo> getAllPackageInfo(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        return pm.getInstalledPackages(0);
    }

    public static boolean isAppInstalled(Context ctx, String pkg, int versionCode) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(pkg, 0);
            if (pi != null && pi.versionCode == versionCode) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public static boolean isAppInstalled(Context ctx, String pkg) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(pkg, 0);
            if (pi != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public static List<PackageInfo> getSystemPackageInfo(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        List<PackageInfo> allApps = pm.getInstalledPackages(0);
        List<PackageInfo> sysAppList = new ArrayList<>();

        for (PackageInfo pi : allApps) {
            if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                sysAppList.add(pi);
            }
        }

        return sysAppList;
    }

    public static List<PackageInfo> getInstallPackageInfo(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        List<PackageInfo> allApps = pm.getInstalledPackages(0);
        List<PackageInfo> installAppList = new ArrayList<>();

        for (PackageInfo pi : allApps) {
            if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                installAppList.add(pi);
            }
        }

        return installAppList;
    }

    public static Drawable getAPKIconDrawable(Context ctx, String filePath) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(filePath,0);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            appInfo.sourceDir = filePath;
            appInfo.publicSourceDir = filePath;
            return appInfo.loadIcon(pm);
        }
        return null;
    }

    public static PackageInfo getPackageInfo(Context ctx, String pkgName) {
        PackageManager pm = ctx.getPackageManager();
        try {
            return pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    public static ApplicationInfo getApplicationInfo(Context ctx, String pkgName) {
        PackageManager pm = ctx.getPackageManager();
        try {
            return pm.getPackageInfo(pkgName,0).applicationInfo;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    public static PackageInfo getAPKPackageInfo(Context ctx, String filePath) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(filePath,0);
        if (pi != null) {
            pi.applicationInfo.sourceDir = filePath;
            pi.applicationInfo.publicSourceDir = filePath;
        }
        return pi;
    }

    public static String getAppName(Context ctx, PackageInfo pi) {
        return pi.applicationInfo.loadLabel(ctx.getPackageManager()).toString();
    }

    public static String getAppName(Context ctx, String pkg) {
        PackageManager pm = ctx.getPackageManager();
        try {
            ApplicationInfo ai = pm.getPackageInfo(pkg, 0).applicationInfo;
            return ai.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    public static String getAppSignature(Context ctx, String pkgName) {
        PackageInfo pis = null;
        try {
            pis = ctx.getPackageManager()
                    .getPackageInfo(pkgName,0);
            return hexDigest(pis.signatures[0].toByteArray());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }
    /**
     * 将签名字符串转换成需要的32位签名
     *
     * @param paramArrayOfByte 签名byte数组
     * @return 32位签名字符串
     */
    private static String hexDigest(byte[] paramArrayOfByte) {
        final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97,
                98, 99, 100, 101, 102 };
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            for (int i = 0, j = 0; ; i++, j++) {
                if (i >= 16) {
                    return new String(arrayOfChar);
                }
                int k = arrayOfByte[i];
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                arrayOfChar[++j] = hexDigits[(k & 0xF)];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static boolean isAllowInstallUnknownApp(Context ctx) {
        try {
            return Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
        } catch (Exception e) {
            return false;
        }
    }
}

