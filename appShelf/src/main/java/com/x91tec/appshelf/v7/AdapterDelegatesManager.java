package com.x91tec.appshelf.v7;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by oeager on 16-4-11.
 */
public class AdapterDelegatesManager<T, VH extends RecyclerView.ViewHolder> {

    final SparseArray<AdapterDelegate<T, VH>> delegates = new SparseArray<>();

    private AdapterDelegate fallbackDelegate;

    private ViewTypeGenerator<T> mViewTypeGenerator;

    public AdapterDelegatesManager<T, VH> addDelegate(@NonNull AdapterDelegate<T, VH> delegate) {
        return addDelegate(delegate, false);
    }


    public AdapterDelegatesManager<T, VH> addDelegate(@NonNull AdapterDelegate<T, VH> delegate,
                                                      boolean allowReplacingDelegate) {
        int viewType = delegate.getItemViewType();

        if (fallbackDelegate != null && fallbackDelegate.getItemViewType() == viewType) {
            throw new IllegalArgumentException(
                    "Conflict: the passed AdapterDelegate has the same ViewType integer (value = " + viewType
                            + ") as the fallback AdapterDelegate");
        }
        if (!allowReplacingDelegate && delegates.get(viewType) != null) {
            throw new IllegalArgumentException(
                    "An AdapterDelegate is already registered for the viewType = " + viewType
                            + ". Already registered AdapterDelegate is " + delegates.get(viewType));
        }

        delegates.put(viewType, delegate);

        return this;
    }

    public AdapterDelegatesManager<T, VH> removeDelegate(@NonNull AdapterDelegate<T, VH> delegate) {

        AdapterDelegate<T, VH> queried = delegates.get(delegate.getItemViewType());
        if (queried != null && queried == delegate) {
            delegates.remove(delegate.getItemViewType());
        }
        return this;
    }

    public AdapterDelegatesManager<T, VH> removeDelegate(int viewType) {
        delegates.remove(viewType);
        return this;
    }
    public AdapterDelegatesManager<T, VH> releaseDelegate() {
        delegates.clear();
        fallbackDelegate = null;
        mViewTypeGenerator = null;
        return this;
    }

    public void setViewTypeGenerator(ViewTypeGenerator<T> generator){
        this.mViewTypeGenerator = generator;
    }
    public int getItemViewType(@NonNull List<T> items, int position) {

        if(mViewTypeGenerator==null){
            if (fallbackDelegate != null) {
                return fallbackDelegate.getItemViewType();
            }
            return 0;
        }
        return mViewTypeGenerator.getItemViewType(items,position);
    }

    @NonNull
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterDelegate<T, VH> delegate = delegates.get(viewType);
        if (delegate == null) {
            if (fallbackDelegate == null) {
                throw new NullPointerException("No AdapterDelegate added for ViewType " + viewType);
            } else {
                delegate = fallbackDelegate;
            }
        }

       VH vh = delegate.onCreateViewHolder(parent);
        if (vh == null) {
            throw new NullPointerException(
                    "ViewHolder returned from AdapterDelegate " + delegate + " for ViewType =" + viewType
                            + " is null!");
        }
        return vh;
    }


    public void onBindViewHolder(@NonNull List<T> items, int position,
                                 @NonNull VH viewHolder) {

        AdapterDelegate<T, VH> delegate = delegates.get(viewHolder.getItemViewType());
        if (delegate == null) {
            if (fallbackDelegate == null) {
                throw new NullPointerException(
                        "No AdapterDelegate added for ViewType " + viewHolder.getItemViewType());
            } else {
                delegate = fallbackDelegate;
            }
        }

        delegate.onBindViewHolder(items, position, viewHolder);
    }


    public AdapterDelegatesManager<T, VH> setFallbackDelegate(
            @Nullable AdapterDelegate fallbackDelegate) {

        if (fallbackDelegate != null) {
            // Setting a new fallback delegate
            int delegatesCount = delegates.size();
            int fallbackViewType = fallbackDelegate.getItemViewType();
            for (int i = 0; i < delegatesCount; i++) {
                AdapterDelegate<T, VH> delegate = delegates.valueAt(i);
                if (delegate.getItemViewType() == fallbackViewType) {
                    throw new IllegalArgumentException(
                            "Conflict: The given fallback - delegate has the same ViewType integer (value = "
                                    + fallbackViewType + ")  as an already assigned AdapterDelegate "
                                    + delegate.getClass().getName());
                }
            }
        }
        this.fallbackDelegate = fallbackDelegate;

        return this;
    }
}
