package com.x91tec.appshelf.v7;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oeager on 16-3-9.
 */
public abstract class BasicRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

    private final List<T> mListData = new ArrayList<>();

    private LayoutInflater mLayoutInflater;

    private Context context;

    public BasicRecyclerViewAdapter(Context context) {
        this(context, null);
    }

    public BasicRecyclerViewAdapter(Context context, List<T> list) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        if (list != null) {
            mListData.addAll(list);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateRecycleItemView(mLayoutInflater, parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindItemViewData(holder, position, mListData.get(position));
    }
    @Override
    public int getItemViewType(int position) {
        T t = mListData.get(position);

        return generateViewType(position, t);
    }


    @Override
    public int getItemCount() {
        return mListData.size();
    }


    public void changeDataSet(boolean keepOld, T... newData) {
        changeDataSet(keepOld, Arrays.asList(newData));
    }


    public void changeDataSet(boolean keepOld, List<T> newData) {
        if (!keepOld) {
            mListData.clear();
        }
        if (newData != null) {
            mListData.addAll(newData);
        }
    }

    public void clearRecyclerData() {
        mListData.clear();
    }

    protected Context getContext() {
        return context;
    }

    public List<T> getListData() {
        return mListData;
    }

    protected abstract void onBindItemViewData(VH holder, int position, T data);

    protected abstract VH onCreateRecycleItemView(LayoutInflater layoutInflater, ViewGroup parent, int viewType);

    protected int generateViewType(int itemPosition, T data) {
        return 0;
    }
}
