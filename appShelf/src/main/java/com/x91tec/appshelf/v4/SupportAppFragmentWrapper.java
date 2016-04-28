package com.x91tec.appshelf.v4;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.x91tec.appshelf.R;
import com.x91tec.appshelf.components.activities.AppEmptySessions;
import com.x91tec.appshelf.ui.MultiStateLayout;

import static com.x91tec.appshelf.ui.MultiStateLayout.*;

/**
 * Created by oeager on 2015/11/10.
 * email:oeager@foxmail.com
 */
public abstract class SupportAppFragmentWrapper<T extends Activity> extends Fragment {

    private T context;

    private boolean hasCalledFirst = false;

    private StateController mStateController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (T) getActivity();
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MultiStateLayout stateLayout = onCreateRootView(inflater, container, savedInstanceState);
        if (stateLayout == null) {
            mStateController = AppEmptySessions.fromEmptyNullable(null);
            return onCreateContentView(inflater, container, savedInstanceState);
        }
        mStateController = stateLayout.compile();
        return stateLayout;
    }


    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view, savedInstanceState);
    }

    @Override
    public final void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponentsData(savedInstanceState);
        if (!hasCalledFirst) {
            hasCalledFirst = true;
            onShowToUserFirst();
        }
    }
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    protected MultiStateLayout onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MultiStateLayout stateLayout = new MultiStateLayout(inflater.getContext());
        stateLayout.attachLayout(STATE_CONTENT, onCreateContentView(inflater, stateLayout, savedInstanceState))
                .attachLayout(STATE_LOADING, onCreateLoadingView(inflater, stateLayout, savedInstanceState))
                .attachLayout(STATE_EMPTY, onCreateEmptyView(inflater, stateLayout, savedInstanceState))
                .attachLayout(STATE_ERROR, onCreateErrorView(inflater, stateLayout, savedInstanceState));
        return stateLayout;
    }

    protected View onCreateErrorView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText(R.string.error_happened);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowToUserFirst();
            }
        });
        return textView;
    }

    protected View onCreateEmptyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText(R.string.empty_tip);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowToUserFirst();
            }
        });
        return textView;
    }

    protected View onCreateLoadingView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProgressBar progressBar = new ProgressBar(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(params);
        progressBar.setIndeterminate(false);
        return progressBar;
    }

    public T getContext() {
        return context;
    }

    protected void onShowToUserFirst() {

    }

    public StateController getStateController(){
        return mStateController;
    }

    protected abstract void initComponents(View createView, Bundle savedInstanceState);

    protected abstract void initComponentsData(Bundle savedInstanceState);
}
    