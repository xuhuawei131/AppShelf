package com.x91tec.appshelf.v7;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by oeager on 16-3-9.
 */
public final class RecyclerFactory {

    public static class RecyclerViewType {

        public final static int VIEW_TYPE_HEADER = Integer.MIN_VALUE;

        public final static int VIEW_TYPE_FOOTER =Integer.MIN_VALUE/2;
    }



    public interface RecyclerExtraAdapter extends IViewDataBinder{

        int getCount();

        void addExtraView(int position, View extraView);

        void removeExtraView(int position);

        RecyclerView.ViewHolder onCreateExtraView(ViewGroup parent, int position);

        void setIViewDataBinder(IViewDataBinder dataBinder);

    }


    public static class BasicRecyclerExtraAdapter implements RecyclerExtraAdapter {

        private final SparseArray<View> mExtras = new SparseArray<>();

        private IViewDataBinder iBinder;

        @Override
        public int getCount() {
            return mExtras.size();
        }

        @Override
        public void addExtraView(int position, View extraView) {
            mExtras.put(position,extraView);
        }

        @Override
        public void removeExtraView(int position) {
            mExtras.remove(position);
        }

        @Override
        public ViewHolder onCreateExtraView(ViewGroup parent, int position) {
            View extraView =mExtras.get(position);
            return new ViewHolder(extraView);
        }

        @Override
        public void setIViewDataBinder(IViewDataBinder dataBinder) {
            iBinder = dataBinder;
        }

        @Override
        public void onBindExtraView(int position, RecyclerView.ViewHolder viewHolder) {
            if(iBinder!=null){
                iBinder.onBindExtraView(position,viewHolder);
            }
        }
    }
    public static class SingleExtraAdapter implements RecyclerExtraAdapter {

        private IViewDataBinder iBinder;

        public SingleExtraAdapter(IViewDataBinder iBinder){
            this.iBinder = iBinder;
        }
        public SingleExtraAdapter(){
            this.iBinder =null;
        }
        private View extraView;

        @Override
        public int getCount() {
            return extraView==null?0:1;
        }

        @Override
        public void addExtraView(int position, View extraView) {
            this.extraView = extraView;
        }

        @Override
        public void removeExtraView(int position) {
            extraView=null;
        }

        @Override
        public ViewHolder onCreateExtraView(ViewGroup parent, int position) {
            return new ViewHolder(extraView);
        }

        @Override
        public void setIViewDataBinder(IViewDataBinder dataBinder) {
            this.iBinder = dataBinder;
        }

        @Override
        public void onBindExtraView(int position, RecyclerView.ViewHolder viewHolder) {
            if(iBinder!=null){
                iBinder.onBindExtraView(position,viewHolder);
            }
        }
    }

    public interface IViewDataBinder{
        void onBindExtraView(int position, RecyclerView.ViewHolder viewHolder);
    }
    public static class PageLoadingFooterAdapter extends SingleExtraAdapter{
        private PageLoadingFooter pageLoadingFooter;


        public PageLoadingFooterAdapter(PageLoadingFooter pageLoadingFooter) {
            this.pageLoadingFooter = pageLoadingFooter;
            addExtraView(0,pageLoadingFooter.getLoadingLayout());
        }

        public void startLoading(){
            if(pageLoadingFooter.getLoadingState()==PageLoadingFooter.VIEW_STATE_LOADING){
                return;
            }
            pageLoadingFooter.onStartLoading();

        }

        public void finishLoading(){
            if(pageLoadingFooter.getLoadingState()==PageLoadingFooter.VIEW_STATE_NORMAL){
                return;
            }
            pageLoadingFooter.onLoadingComplete();
        }

        public void loadErrorHappened(){
            if(pageLoadingFooter.getLoadingState()==PageLoadingFooter.VIEW_STATE_ERROR){
                return;
            }
            pageLoadingFooter.onLoadingError();
        }

        public void loadLastPage(){
            if(pageLoadingFooter.getLoadingState()==PageLoadingFooter.VIEW_STATE_LAST){
                return;
            }
            pageLoadingFooter.onReachToLastPage();
        }

        public int getLoadingState(){
            return pageLoadingFooter.getLoadingState();
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
