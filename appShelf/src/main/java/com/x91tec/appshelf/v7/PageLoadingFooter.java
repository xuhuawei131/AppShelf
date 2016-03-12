package com.x91tec.appshelf.v7;

import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by oeager on 16-3-10.
 */
public interface PageLoadingFooter {

    int VIEW_STATE_NORMAL = 0;

    int VIEW_STATE_LOADING = 1;

    int VIEW_STATE_ERROR = 2;

    int VIEW_STATE_LAST = 3;

    @IntDef({
            VIEW_STATE_NORMAL,
            VIEW_STATE_LOADING,
            VIEW_STATE_ERROR,
            VIEW_STATE_LAST
    })
    public @interface LoadingState{

    }

    View getLoadingLayout();

    void onStartLoading();

    void onLoadingComplete();

    void onReachToLastPage();

    void onLoadingError();

    int getLoadingState();
}
