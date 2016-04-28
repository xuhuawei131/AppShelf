package com.x91tec.appshelf.v7;

import android.support.v7.widget.RecyclerView;

/**
 * Created by oeager on 16-4-12.
 */
public class LayoutManagerCompat {

    interface ILayoutManager {

        int getFirstRowSpan();

        int getLastRowSpan();

        boolean isLastRowVisible();

        boolean isFirstRowVisible();

        boolean isVertical();

        int getItemCount();

        int getVisibleItemCount();

    }


    final ILayoutManager manager;

    public LayoutManagerCompat(RecyclerView.LayoutManager manager) {
        if (manager instanceof android.support.v7.widget.GridLayoutManager) {
            this.manager = new GridLayoutManager((android.support.v7.widget.GridLayoutManager) manager);
        } else {
            this.manager = new LinearLayoutManager((android.support.v7.widget.LinearLayoutManager) manager);
        }
    }

    public int getFirstRowSpan() {
        return manager.getFirstRowSpan();
    }

    public boolean isFirstRowVisible() {
        return manager.isFirstRowVisible();
    }

    public boolean isVertical() {
        return manager.isVertical();
    }

    public int getLastRowSpan() {
        return manager.getLastRowSpan();
    }

    public boolean isLastRowVisible() {
        return manager.isLastRowVisible();
    }
    public int getItemCount() {
        return manager.getItemCount();
    }

    public int getVisibleItemCount() {
        return manager.getVisibleItemCount();
    }

    static class LinearLayoutManager implements ILayoutManager {

        android.support.v7.widget.LinearLayoutManager manager;

        public LinearLayoutManager(android.support.v7.widget.LinearLayoutManager manager) {
            this.manager = manager;
        }

        @Override
        public int getFirstRowSpan() {
            return 1;
        }

        @Override
        public int getLastRowSpan() {
            return Math.max(0, getItemCount() - 1);
        }

        public int getItemCount() {
            return manager.getItemCount();
        }

        @Override
        public int getVisibleItemCount() {
            return manager.getChildCount();
        }

        @Override
        public boolean isLastRowVisible() {
            int size = getItemCount();
            return manager.findLastVisibleItemPosition() == size - 1;
        }

        @Override
        public boolean isFirstRowVisible() {
            return manager.findFirstVisibleItemPosition() == 0;
        }

        @Override
        public boolean isVertical() {
            return manager.getOrientation() == android.support.v7.widget.LinearLayoutManager.VERTICAL;
        }

    }

    static class GridLayoutManager extends LinearLayoutManager {

        final android.support.v7.widget.GridLayoutManager gridLayoutManager;

        public GridLayoutManager(android.support.v7.widget.GridLayoutManager manager) {
            super(manager);
            gridLayoutManager = manager;
        }

        @Override
        public int getLastRowSpan() {
            int spanCount = gridLayoutManager.getSpanCount();
            int count = getItemCount();
            return Math.max(0, count - spanCount);
        }

        @Override
        public boolean isLastRowVisible() {
            return manager.findLastVisibleItemPosition() >= getLastRowSpan();
        }

        @Override
        public int getFirstRowSpan() {
            return gridLayoutManager.getSpanCount();
        }
    }
}
