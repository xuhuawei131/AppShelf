package com.x91tec.appshelf.v7;

import android.content.Context;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.x91tec.appshelf.R;
import com.x91tec.appshelf.ui.ViewUtils;

/**
 * Created by oeager on 16-3-10.
 */
public class SimpleLoadFooter implements PageLoadingFooter {

    private View loadingLayout;
    private View mLoadingView;
    private View mNetworkErrorView;
    private View mTheEndView;

    private IDataGetter dataGetter;

    public SimpleLoadFooter(Context context,IDataGetter dataGetter) {
        loadingLayout = LayoutInflater.from(context).inflate(R.layout.simple_list_loading_footer, null);
        loadingLayout.setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        this.dataGetter = dataGetter;
    }

    private int currentState;

    @Override
    public void onStartLoading() {
        onStateChanged(VIEW_STATE_LOADING);

        if (mTheEndView != null) {
            ViewUtils.checkAndSetViewVisibility(mTheEndView,View.GONE);
        }
        if (mNetworkErrorView != null) {
            ViewUtils.checkAndSetViewVisibility(mNetworkErrorView,View.GONE);
        }

        if (mLoadingView == null) {
            ViewStub viewStub = (ViewStub) loadingLayout.findViewById(R.id.loading_viewstub);
            mLoadingView = viewStub.inflate();
        }

        ViewUtils.checkAndSetViewVisibility(mLoadingView, View.VISIBLE);
    }

    @Override
    public void onLoadingComplete() {
        onStateChanged(VIEW_STATE_NORMAL);
        if (mLoadingView != null) {
            ViewUtils.checkAndSetViewVisibility(mLoadingView,View.GONE);
        }

        if (mTheEndView != null) {
            ViewUtils.checkAndSetViewVisibility(mTheEndView, View.GONE);
        }

        if (mNetworkErrorView != null) {
            ViewUtils.checkAndSetViewVisibility(mNetworkErrorView, View.GONE);
        }
    }

    @Override
    public void onReachToLastPage() {
        onStateChanged(VIEW_STATE_LAST);
        if (mLoadingView != null) {
            ViewUtils.checkAndSetViewVisibility(mLoadingView, View.GONE);
        }

        if (mNetworkErrorView != null) {
            ViewUtils.checkAndSetViewVisibility(mNetworkErrorView, View.GONE);
        }

        if (mTheEndView == null) {
            ViewStub viewStub = (ViewStub) loadingLayout.findViewById(R.id.end_viewstub);
            mTheEndView = viewStub.inflate();

        }

        ViewUtils.checkAndSetViewVisibility(mTheEndView, View.VISIBLE);
    }


    @Override
    public void onLoadingError() {
        onStateChanged(VIEW_STATE_ERROR);
        if (mLoadingView != null) {
            ViewUtils.checkAndSetViewVisibility(mLoadingView, View.GONE);
        }
        if (mTheEndView != null) {
            ViewUtils.checkAndSetViewVisibility(mTheEndView, View.GONE);
        }

        if (mNetworkErrorView == null) {
            ViewStub viewStub = (ViewStub) loadingLayout.findViewById(R.id.network_error_viewstub);
            mNetworkErrorView = viewStub.inflate();
            mNetworkErrorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dataGetter!=null){
                        dataGetter.loadingData();
                    }
                }
            });
        }

        ViewUtils.checkAndSetViewVisibility(mNetworkErrorView,View.VISIBLE);
    }

    void onStateChanged(@LoadingState int newState){
        this.currentState = newState;
    }

    public int getLoadingState(){
        return this.currentState;
    }

    public View getLoadingLayout(){
        return loadingLayout;
    }

    public void setLoadingLayoutParam(ViewGroup.LayoutParams param){
        loadingLayout.setLayoutParams(param);
    }

}
