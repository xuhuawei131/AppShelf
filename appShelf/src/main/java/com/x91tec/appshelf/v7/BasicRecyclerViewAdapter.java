package com.x91tec.appshelf.v7;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oeager on 16-3-9.
 */
public abstract class BasicRecyclerViewAdapter<VH extends RecyclerView.ViewHolder,T> extends RecyclerView.Adapter<VH> {

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
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
