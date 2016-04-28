package com.x91tec.appshelf.components.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.x91tec.appshelf.components.fragments.ProgressDialogFragment;
import com.x91tec.appshelf.ui.swipeback.SwipeConfiguration;
import com.x91tec.appshelf.ui.swipeback.SwipePanelLayout;

import static com.x91tec.appshelf.ui.MultiStateLayout.StateController;


public abstract class BaseAppActivity extends AppCompatActivity {

    private Toast toast = null;

    private DialogFragment dialogFragment;

    private StateController mStateController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public abstract void initTitleBar();

    public abstract void initComponents();

    public abstract void initComponentsData();


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        supportToDoOnContentSettled();
    }


    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        supportToDoOnContentSettled();
    }


    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        supportToDoOnContentSettled();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }


    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();

    }

    public void showToast(@StringRes int resId) {
        if (toast == null) {
            toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resId);
        }
        toast.show();
    }


    public void showProgressDialog(String msg, boolean cancelable) {
        dialogFragment = ProgressDialogFragment.newInstance(msg);
        dialogFragment.setCancelable(cancelable);
        ((ProgressDialogFragment) dialogFragment).show(this);
    }


    public void showProgressDialog(@StringRes int id, boolean cancelable) {
        showProgressDialog(getString(id), cancelable);
    }

    public void dismissProgressDialog() {
        if (dialogFragment != null) {
            dialogFragment.dismiss();
            dialogFragment = null;
        }
    }

    protected StateController supportActivityStateController(){
        return null;
    }

    void supportToDoOnContentSettled(){
        mStateController = supportActivityStateController();
        initTitleBar();
        initComponents();
        initComponentsData();
    }

    public void supportSwipeBack(@SwipeConfiguration.SwipePosition int position, boolean isCaptureFullScreen) {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        SwipeConfiguration configuration = new SwipeConfiguration.Builder()
                .position(position)
                .edgeSize(isCaptureFullScreen ? 1f : 0.18f)
                .build();
        SwipePanelLayout.attachToActivity(this, configuration);
    }

    public boolean isLandscapeMode() {
        return getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90;
    }

    public StateController getStateController(){
        return AppEmptySessions.fromEmptyNullable(mStateController);
    }


}
