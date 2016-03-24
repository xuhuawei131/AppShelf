package com.x91tec.appshelf.components.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.x91tec.appshelf.components.fragments.ProgressDialogFragment;
import com.x91tec.appshelf.ui.swipeback.SwipeConfiguration;
import com.x91tec.appshelf.ui.swipeback.SwipePanelLayout;


public abstract class BaseAppActivity extends AppCompatActivity implements ActivityFace {

    private Toast toast = null;

    private boolean wasCreated, wasInterrupted;

    private boolean isFocus;

    private Intent currentIntent;

    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.wasCreated = true;
        this.currentIntent = getIntent();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LifecycleCompatDispatcher.getDefault().onActivityCreated(this, savedInstanceState);
        }

    }

    public abstract void initTitleBar();

    public abstract void initComponents();

    public abstract void initComponentsData();


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initTitleBar();
        initComponents();
        initComponentsData();
    }


    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initTitleBar();
        initComponents();
        initComponentsData();
    }


    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initTitleBar();
        initComponents();
        initComponentsData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //yes,if you do not set parent activity in Manifests.xml ,
        // and you also want come back when user click the menuItem home;
        //just use super.onOptionsItemSelected();
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LifecycleCompatDispatcher.getDefault().onActivityStarted(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isFocus = true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LifecycleCompatDispatcher.getDefault().onActivityResumed(this);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        isFocus = false;
        wasCreated = wasInterrupted = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LifecycleCompatDispatcher.getDefault().onActivityPaused(this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LifecycleCompatDispatcher.getDefault().onActivityStopped(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LifecycleCompatDispatcher.getDefault().onActivityDestroyed(this);
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LifecycleCompatDispatcher.getDefault().onActivitySaveInstanceState(this, outState);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        wasInterrupted = true;
    }

    @Override
    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();

    }

    @Override
    public void showToast(@StringRes int resId) {
        if (toast == null) {
            toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resId);
        }
        toast.show();
    }


    @Override
    public void showProgressDialog(String msg, boolean cancelable) {
        dialogFragment = ProgressDialogFragment.newInstance(msg);
        dialogFragment.setCancelable(cancelable);
        ((ProgressDialogFragment) dialogFragment).show(this);
    }


    @Override
    public void showProgressDialog(@StringRes int id, boolean cancelable) {
        showProgressDialog(getString(id), cancelable);
    }

    @Override
    public void dismissProgressDialog() {
        if (dialogFragment != null) {
            dialogFragment.dismiss();
            dialogFragment = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.currentIntent = intent;
    }

    public void supportSwipeBack(@SwipeConfiguration.SwipePosition int position, boolean isCaptureFullScreen) {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        SwipeConfiguration configuration = new SwipeConfiguration.Builder()
                .position(position)
                .edgeSize(isCaptureFullScreen ? 1f : 0.18f)
                .build();
        SwipePanelLayout.attachToActivity(this, configuration);
    }

    @Override
    public boolean isRestoring() {
        return wasInterrupted;
    }

    @Override
    public boolean isResuming() {
        return !wasCreated;
    }

    @Override
    public boolean isLaunching() {
        return !wasInterrupted && wasCreated;
    }

    @Override
    public boolean isActivityFont() {
        return isFocus;
    }

    @Override
    public Intent getCurrentIntent() {
        return currentIntent;
    }

    @Override
    public boolean isLandscapeMode() {
        return getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90;
    }


}
