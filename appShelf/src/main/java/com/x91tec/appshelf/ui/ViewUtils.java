package com.x91tec.appshelf.ui;

import android.view.View;

/**
 * Created by oeager on 16-3-10.
 */
public class ViewUtils {

    public static void checkAndSetViewVisibility(View view, int visibility){

        if(view.getVisibility()!=visibility){
            view.setVisibility(visibility);
        }
    }

    public static void checkAndSetViewEnable(View view,boolean enable){
        if(view.isEnabled()==enable){
            return;
        }
        view.setEnabled(enable);
    }

    public static void checkAndSetViewClickable(View view,boolean clickable){
        if(view.isClickable()==clickable){
            return;
        }
        view.setClickable(clickable);
    }
}
