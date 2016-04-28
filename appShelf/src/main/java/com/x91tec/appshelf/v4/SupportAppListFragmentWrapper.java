package com.x91tec.appshelf.v4;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.x91tec.appshelf.ui.MultiStateLayout;

public abstract class SupportAppListFragmentWrapper<T extends Activity> extends SupportAppFragmentWrapper<T> {
    final private Handler mHandler = new Handler();

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mList.focusableViewAvailable(mList);
        }
    };

    final private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            onListItemClick((ListView) parent, v, position, id);
        }
    };

    ListAdapter mAdapter;

    ListView mList;

    View mEmptyView;


    public SupportAppListFragmentWrapper() {
    }


    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView lv = new ListView(inflater.getContext());
        lv.setCacheColorHint(Color.TRANSPARENT);
        lv.setId(android.R.id.list);
        lv.setDrawSelectorOnTop(false);
        mList = lv;
        return lv;
    }

    @Override
    protected void initComponents(View createView, Bundle savedInstanceState) {
        mList.setOnItemClickListener(mOnClickListener);
        mHandler.post(mRequestFocus);
        mEmptyView = createView.findViewById(android.R.id.empty);
        if (mEmptyView == null) {
            mEmptyView = getStateController().getStateView(MultiStateLayout.STATE_EMPTY);
        }
        if (mEmptyView != null) {
            mList.setEmptyView(mEmptyView);
        }
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {
        mHandler.removeCallbacks(mRequestFocus);
        mList = null;
        super.onDestroyView();
    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the data
     * associated with the selected item.
     *
     * @param l        The ListView where the click happened
     * @param v        The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     */
    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    /**
     * Provide the cursor for the list view.
     */
    public void setListAdapter(ListAdapter adapter) {
        boolean hadAdapter = mAdapter != null;
        mAdapter = adapter;
        if (mList != null) {
            mList.setAdapter(adapter);
            if (getStateController().getCurrentState() != MultiStateLayout.STATE_CONTENT && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter. It is now time to show it.
                getStateController().showContent(false);
            }
        }
    }

    /**
     * Set the currently selected list item to the specified position with the
     * adapter's data
     *
     * @param position
     */
    public void setSelection(int position) {
        mList.setSelection(position);
    }

    /**
     * Get the position of the currently selected list item.
     */
    public int getSelectedItemPosition() {
        return mList.getSelectedItemPosition();
    }

    /**
     * Get the cursor row ID of the currently selected list item.
     */
    public long getSelectedItemId() {
        return mList.getSelectedItemId();
    }

    /**
     * Get the context's list view widget.
     */
    public ListView getListView() {
        return mList;
    }

    /**
     * The default content for a ListFragment has a TextView that can be shown
     * when the list is empty. If you would like to have it shown, call this
     * method to supply the text it should use.
     */
    public void setEmptyText(CharSequence text) {
        if (mEmptyView != null) {
            TextView emptyText = (TextView) mEmptyView
                    .findViewById(android.R.id.empty);
            if (emptyText != null) {
                emptyText.setText(text);
            }
        }
    }


    /**
     * Get the ListAdapter associated with this context's ListView.
     */
    public ListAdapter getListAdapter() {
        return mAdapter;
    }


}
