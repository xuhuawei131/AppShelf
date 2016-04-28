package com.x91tec.appshelf.ui;

import android.view.View;
import android.view.animation.AnimationUtils;

/**
 * Created by oeager on 16-3-10.
 */
public class ViewUtils {

    public static void checkAndSetViewVisibility(View view, int visibility){

        if(view!=null&&view.getVisibility()!=visibility){
            view.setVisibility(visibility);
        }
    }

    public static void checkAndSetViewEnable(View view,boolean enable){
        if(view!=null&&view.isEnabled()!=enable){
            view.setEnabled(enable);
        }

    }

    public static void checkAndSetViewClickable(View view,boolean clickable){
        if(view==null||view.isClickable()==clickable){
            return;
        }
        view.setClickable(clickable);
    }

    public static void hideView(View v) {
        checkAndSetViewVisibility(v,View.GONE);
    }

    public static void showView(View v) {
        checkAndSetViewVisibility(v,View.VISIBLE);
    }

    public static void hideViewAnimated(View v) {
        if (v != null && v.getVisibility() == View.VISIBLE) {
            v.setVisibility(View.GONE);
            v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out));
        }
    }

    public static void showViewAnimated(View v) {
        if (v != null && v.getVisibility() != View.VISIBLE) {
            v.setVisibility(View.VISIBLE);
            v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in));

        }
    }

    public static void safeClearViewAnimation(View v) {
        if (v != null) {
            v.clearAnimation();
        }
    }

    public static boolean isViewVisible(View v){
        if(v==null){
            return false;
        }
        return v.getVisibility()==View.VISIBLE;
    }
}
