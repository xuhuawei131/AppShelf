package com.x91tec.appshelf.v7;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by oeager on 16-3-10.
 */
public class RecyclerViewAdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView.Adapter  mInnerAdapter;

    private RecyclerFactory.RecyclerExtraAdapter mHeaderFactory;

    private RecyclerFactory.RecyclerExtraAdapter mFooterFactory;

    public RecyclerViewAdapterWrapper() {
        this(null);
    }

    public RecyclerViewAdapterWrapper(RecyclerView.Adapter mInnerAdapter) {
        setAdapter(mInnerAdapter);
    }

    public void setAdapter(RecyclerView.Adapter  adapter) {
        if (adapter == null) {
            return;
        }
        if (mInnerAdapter != null) {
            notifyItemRangeRemoved(getHeaderCount(), mInnerAdapter.getItemCount());
            mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
        }

        this.mInnerAdapter = adapter;
        mInnerAdapter.registerAdapterDataObserver(mDataObserver);
        notifyItemRangeInserted(getHeaderCount(), mInnerAdapter.getItemCount());
    }

    public void setHeaderFactory(RecyclerFactory.RecyclerExtraAdapter mHeaderFactory) {
        this.mHeaderFactory = mHeaderFactory;
    }

    public void setFooterFactory(RecyclerFactory.RecyclerExtraAdapter mFooterFactory) {
        this.mFooterFactory = mFooterFactory;
    }

    public RecyclerView.Adapter getInnerAdapter() {
        return mInnerAdapter;
    }

    public RecyclerFactory.RecyclerExtraAdapter getHeaderAdapter() {
        return mHeaderFactory;
    }

    public RecyclerFactory.RecyclerExtraAdapter getFooterAdapter() {
        return mFooterFactory;
    }

    @Override
    public int getItemViewType(int position) {
        int innerCount = mInnerAdapter == null ? 0 : mInnerAdapter.getItemCount();
        int headerCount = getHeaderCount();
        if (position < headerCount) {
            return RecyclerFactory.RecyclerViewType.VIEW_TYPE_HEADER + position;
        }
        if (headerCount <= position && position < headerCount + innerCount) {
            assert mInnerAdapter != null;
            int innerItemViewType = mInnerAdapter.getItemViewType(position - headerCount);
            if (innerItemViewType >= Integer.MAX_VALUE / 2) {
                throw new IllegalArgumentException("your adapter's return value of getItemViewType() must < Integer.MAX_VALUE / 2");
            }
            return innerItemViewType + Integer.MAX_VALUE / 2;
        }
        return RecyclerFactory.RecyclerViewType.VIEW_TYPE_FOOTER + position - headerCount - innerCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int headerViewsCountCount = getHeaderCount();
        if (viewType < RecyclerFactory.RecyclerViewType.VIEW_TYPE_HEADER + headerViewsCountCount) {
            int position = viewType - RecyclerFactory.RecyclerViewType.VIEW_TYPE_HEADER;
            return mHeaderFactory.onCreateExtraView(parent, position);
        } else if (viewType >= RecyclerFactory.RecyclerViewType.VIEW_TYPE_FOOTER && viewType < Integer.MAX_VALUE / 2) {
            int position = viewType - RecyclerFactory.RecyclerViewType.VIEW_TYPE_FOOTER;
            return mFooterFactory.onCreateExtraView(parent, position);
        } else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType - Integer.MAX_VALUE / 2);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int headerViewsCountCount = getHeaderCount();
        int viewType = holder.getItemViewType();
        if (viewType >= Integer.MAX_VALUE / 2) {
           if(mInnerAdapter!=null){
               mInnerAdapter.onBindViewHolder(holder, position - headerViewsCountCount);
           }
        } else {

            if (position < headerViewsCountCount) {
                mHeaderFactory.onBindExtraView(position, holder);
            } else {
                int realPosition = viewType - RecyclerFactory.RecyclerViewType.VIEW_TYPE_FOOTER;
                mFooterFactory.onBindExtraView(realPosition, holder);
            }
        }

    }

    @Override
    public int getItemCount() {
        int innerCount = mInnerAdapter == null ? 0 : mInnerAdapter.getItemCount();
        return getHeaderCount() + getFooterCount() + innerCount;
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            int headerViewsCountCount = getHeaderCount();
            notifyItemRangeChanged(fromPosition + headerViewsCountCount, toPosition + headerViewsCountCount + itemCount);
        }
    };

    int getHeaderCount() {
        return mHeaderFactory == null ? 0 : mHeaderFactory.getCount();
    }

    int getFooterCount() {
        return mFooterFactory == null ? 0 : mFooterFactory.getCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
     if(mInnerAdapter!=null){
         mInnerAdapter.onAttachedToRecyclerView(recyclerView);
     }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if(mInnerAdapter!=null){
            mInnerAdapter.onDetachedFromRecyclerView(recyclerView);
        }
    }

}
