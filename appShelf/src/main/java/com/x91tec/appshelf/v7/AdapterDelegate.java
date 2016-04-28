package com.x91tec.appshelf.v7;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by oeager on 16-4-11.
 */
public interface AdapterDelegate<T,VH extends RecyclerView.ViewHolder> {

    int getItemViewType();

    VH onCreateViewHolder(ViewGroup parent);

    void onBindViewHolder(@NonNull List<T> items, int position, @NonNull VH holder);
}
