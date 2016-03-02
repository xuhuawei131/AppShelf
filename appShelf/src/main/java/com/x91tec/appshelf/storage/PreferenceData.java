package com.x91tec.appshelf.storage;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.developer.bsince.log.GOL;
import com.x91tec.appshelf.components.AppHook;

import java.util.Map;
import java.util.Set;

/**
 * Created by oeager on 2016/1/20.
 * email:oeager@foxmail.com
 */
public final class PreferenceData {

    private static final String DEFAULT_FILE_NAME = "preference_data";

    private static String configFileName = DEFAULT_FILE_NAME;

    private static PreferenceData mInstance = null;

    private final String FILE_NAME;

    private final SharedPreferences preferences;

    private PreferenceData() {
        //no instance
        if (TextUtils.isEmpty(configFileName)) {
            FILE_NAME = DEFAULT_FILE_NAME;
        } else {
            FILE_NAME = configFileName;
        }
        Application app = AppHook.getApp();
        if(app==null){
            Activity activity = AppHook.get().currentActivity();
            if(activity==null){
                throw new UnsupportedOperationException("UnSupport operation of get application");
            }
            app = activity.getApplication();
        }
        preferences = app.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceData getInstance() {
        if (mInstance == null) {
            synchronized (PreferenceData.class) {
                if (mInstance == null) {
                    mInstance = new PreferenceData();
                }
            }
        }
        return mInstance;
    }

    public static void configFileName(String fileName) {
        configFileName = fileName;
        GOL.d("you should call this method before init");
    }

    public <T> void save(String key, T value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,String.valueOf(value));
        SharedPreferencesCompat.commit(editor);
    }

    @SuppressWarnings("unchecked")
    public <T> T take(String key,T defaultValue){
        String s = preferences.getString(key, String.valueOf(defaultValue));
        if (defaultValue instanceof String) {
            return (T) s;
        } else if (defaultValue instanceof Integer) {
            return (T) Integer.valueOf(s);
        } else if (defaultValue instanceof Boolean) {
            return (T) Boolean.valueOf(s);
        } else if (defaultValue instanceof Float) {
            return (T) Float.valueOf(s);
        } else if (defaultValue instanceof Long) {
            return (T) Long.valueOf(s);
        }
        return null;
    }

    public void save(String key,Set<String> set){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key,set);
        SharedPreferencesCompat.commit(editor);
    }

    public Set<String> take(String key,Set<String> defaultValue){
        return preferences.getStringSet(key,defaultValue);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        SharedPreferencesCompat.commit(editor);
    }

    public void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        SharedPreferencesCompat.commit(editor);
    }

    public boolean contains(String key) {
        return preferences.contains(key);
    }

    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    interface PreferenceController{
        void commit(SharedPreferences.Editor editor);
    }

    private static class SharedPreferencesCompat{

        private static final PreferenceController controller;

        static {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD){
                controller = new PreferenceGingerBread();
            }else {
                controller = new PreferenceDefault();
            }
        }

        public static void commit(SharedPreferences.Editor editor) {
           controller.commit(editor);
        }
    }

    private static class PreferenceGingerBread implements PreferenceController{

        @Override
        public void commit(SharedPreferences.Editor editor) {
            editor.apply();
        }
    }

    private static class PreferenceDefault implements PreferenceController{

        @Override
        public void commit(SharedPreferences.Editor editor) {
            editor.commit();
        }
    }

}
    