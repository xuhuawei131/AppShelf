package com.x91tec.appshelf.v7;

import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by oeager on 16-3-10.
 */
public class RecyclerViewScrollUpListener extends RecyclerView.OnScrollListener {

    public final static int LINEAR_LAYOUT = 0;

    public final static int GRID_LAYOUT = 1;

    public final static int STAGGERED_GRID_LAYOUT = 2;

    @IntDef({LINEAR_LAYOUT, GRID_LAYOUT, STAGGERED_GRID_LAYOUT})
    public @interface LayoutManagerType {

    }

    @LayoutManagerType
    private int layoutManagerType = -1;

    private int currentScrollState;

    private int lastVisibleItemPosition;

   private IDataGetter dataGetter;

    public RecyclerViewScrollUpListener(IDataGetter dataGetter) {
        this.dataGetter = dataGetter;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        currentScrollState = newState;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        lastVisibleItemPosition = findLastVisibleItem(layoutManager);
        if(lastVisibleItemPosition<0){
            return;
        }
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 2)) {
            if(dataGetter!=null){
                dataGetter.loadingData();
            }
        }
    }

    public void setLayoutManagerType(@LayoutManagerType int layoutManagerType) {
        this.layoutManagerType = layoutManagerType;
    }

    int findLastVisibleItem(RecyclerView.LayoutManager layoutManager) {
        if (layoutManagerType < 0) {
            if (layoutManager instanceof GridLayoutManager) {
                setLayoutManagerType(GRID_LAYOUT);
            } else if (layoutManager instanceof LinearLayoutManager) {
                setLayoutManagerType(LINEAR_LAYOUT);
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                setLayoutManagerType(STAGGERED_GRID_LAYOUT);
            } else {
                throw new IllegalArgumentException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }
        switch (layoutManagerType) {
            case LINEAR_LAYOUT:
                return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            case GRID_LAYOUT:
                return ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            case STAGGERED_GRID_LAYOUT:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                return findMax(lastPositions);


        }
        return -1;
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
