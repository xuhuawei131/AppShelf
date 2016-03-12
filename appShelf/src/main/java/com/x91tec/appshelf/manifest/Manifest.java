package com.x91tec.appshelf.manifest;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.developer.bsince.log.GOL;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by oeager on 16-3-3.
 */
public class Manifest {

    public static String getApplicationMetaData(Context mContext,String name){
        try {
            String packageName = mContext.getPackageName();
            GOL.d("packageName = %1$s", packageName);
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(packageName
                    , PackageManager.GET_META_DATA);
            return String.valueOf(appInfo.metaData.get(name));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从apk中获取Manifest信息
     *
     * @param context
     * @param metaDataKey
     * @return
     */
    public static String getMetaDataValueFromApk(Context context, String metaDataKey) {
        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + metaDataKey;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split("\\*");
        String channel = "";
        if (split.length >= 2) {
            channel = ret.substring(split[0].length() + 1);
        }
        return channel;
    }
}
