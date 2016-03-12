package com.x91tec.appshelf.admin;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.x91tec.appshelf.R;
import com.x91tec.appshelf.components.AppHook;

/**
 * Created by oeager on 16-3-3.
 */
public class AppExitConditionImp {

    private final static long DEFAULT_INTERVAL_TIME = 1000;

    private String triggerTips;

    private long effectiveIntervalTime;

    private long lastClickTime;

    public AppExitConditionImp() {
        Context context = AppHook.getApp();
        if(context==null){
            context = AppHook.get().currentActivity();
            if(context==null){
                throw new UnsupportedOperationException("Context auto get is null");
            }
        }
        triggerTips = context.getString(R.string.triggerTips);
        effectiveIntervalTime = DEFAULT_INTERVAL_TIME;
    }

    public void setEffectiveIntervalTime(long effectiveIntervalTime) {
        this.effectiveIntervalTime = effectiveIntervalTime;
    }

    public void setTriggerTips(String triggerTips) {
        this.triggerTips = triggerTips;
    }

    public void trigger(Activity activity){
        long currentMillions = System.currentTimeMillis();
        boolean result = (currentMillions-lastClickTime) < effectiveIntervalTime;
        lastClickTime = currentMillions;
        if(!result){
            Toast.makeText(activity, triggerTips, Toast.LENGTH_SHORT).show();
        }else {
            AppHook.get().appExit();
        }
    }
}
