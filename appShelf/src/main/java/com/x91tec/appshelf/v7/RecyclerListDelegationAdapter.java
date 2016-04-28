package com.x91tec.appshelf.v7;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oeager on 16-4-11.
 */
public class RecyclerListDelegationAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected AdapterDelegatesManager<T, VH> mDelegatesManager;

    final List<T> mListData = new ArrayList<>();

    public RecyclerListDelegationAdapter() {
        this(new AdapterDelegatesManager<T, VH>());
    }

    public RecyclerListDelegationAdapter(AdapterDelegatesManager<T, VH> mDelegatesManager) {
        if (mDelegatesManager == null) {
            throw new NullPointerException("AdapterDelegatesManager is null");
        }
        this.mDelegatesManager = mDelegatesManager;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mDelegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        mDelegatesManager.onBindViewHolder(mListData, position, holder);
    }


    @Override
    public int getItemViewType(int position) {
        return mDelegatesManager.getItemViewType(mListData, position);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public List<T> getListData() {
        return mListData;
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
}
