package com.x91tec.appshelf.connectivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

/**
 * 跟网络相关的工具类
 */
public class Connectivity {
    private Connectivity() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 描述：判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Gps是否打开
     * 需要android.permission.ACCESS_FINE_LOCATION权限
     *
     * @param context the context
     * @return true, if is gps enabled
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * wifi是否打开.
     *
     * @param context the context
     * @return true, if is wifi enabled
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断当前网络是否是wifi网络.
     *
     * @param context the context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前网络是否是3G网络.
     *
     * @param context the context
     * @return boolean
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    public static int getNetworkType(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            return -1;
        }
        return ni.getType();
    }

    public static void openNetworkSetting(Context ctx) {
        try {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT > 10) {
                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            } else {
                intent = new Intent();
                ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                intent.setComponent(component);
                intent.setAction("android.intent.action.VIEW");
            }
            ctx.startActivity(intent);
        } catch (Exception e) {
            try {
                openNetworkSetting2((Activity) ctx);
            } catch (Exception e2) {

            }
        }
    }

    /**
     * 打开网络设置界面
     */
    public static void openNetworkSetting2(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    public static String getMacAddress(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            if(ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CHANGE_WIFI_STATE)== PackageManager.PERMISSION_GRANTED){
                wifiManager.setWifiEnabled(true);
            }else {
                return "";
            }

        }
        if(ContextCompat.checkSelfPermission(mContext,android.Manifest.permission.ACCESS_WIFI_STATE)== PackageManager.PERMISSION_GRANTED){
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress();
        }
        return "";
    }


    public static String getPhoneIp(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            if(ContextCompat.checkSelfPermission(mContext,android.Manifest.permission.CHANGE_WIFI_STATE)== PackageManager.PERMISSION_GRANTED){
                wifiManager.setWifiEnabled(true);
            }else {
                return "";
            }
        }
        if(ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_WIFI_STATE)== PackageManager.PERMISSION_GRANTED){
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            return ip;
        }
        return "";
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }
    /** 网络类型（文字形式） */
    public static String getNetTypeString(Context mContext) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkConstants.NETWORK_TYPE_WIFI;
            }
            int type = info.getSubtype();
            switch (type) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return NetworkConstants.NETWORK_TYPE_1xRTT;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return NetworkConstants.NETWORK_TYPE_CDMA;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return NetworkConstants.NETWORK_TYPE_EDGE;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return NetworkConstants.NETWORK_TYPE_EVDO_0;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return NetworkConstants.NETWORK_TYPE_EVDO_A;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return NetworkConstants.NETWORK_TYPE_GPRS;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return NetworkConstants.NETWORK_TYPE_HSDPA;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return NetworkConstants.NETWORK_TYPE_HSPA;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return NetworkConstants.NETWORK_TYPE_HSUPA;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NetworkConstants.NETWORK_TYPE_IDEN;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return NetworkConstants.NETWORK_TYPE_UMTS;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NetworkConstants.NETWORK_TYPE_HSPAP;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return NetworkConstants.NETWORK_TYPE_EHRPD;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NetworkConstants.NETWORK_TYPE_LTE;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return NetworkConstants.NETWORK_TYPE_EVDO_B;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return NetworkConstants.NETWORK_TYPE_UNKNOWN;
                default:
                    return NetworkConstants.NETWORK_TYPE_UNKNOWN;
            }
        } else {
            return NetworkConstants.NETWORK_TYPE_NONE;
        }
    }

}
