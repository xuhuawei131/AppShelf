package com.example.oeager.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.x91tec.appshelf.components.activities.BaseAppActivity;
import com.x91tec.appshelf.components.services.DefaultUIDetector;
import com.x91tec.appshelf.resources.Colors;
import com.x91tec.appshelf.v7.DecorationFactory;
import com.x91tec.appshelf.v7.IDataGetter;
import com.x91tec.appshelf.v7.PageLoadingFooter;
import com.x91tec.appshelf.v7.RecyclerViewCompat;
import com.x91tec.appshelf.v7.RecyclerViewScrollUpListener;
import com.x91tec.appshelf.v7.SimpleLoadFooter;
import com.x91tec.appshelf.v7.XDividerDecoration;

import java.util.ArrayList;

public class MainActivity extends BaseAppActivity {

    private RecyclerView recyclerView;

    /**服务器端一共多少条数据*/
    private static final int TOTAL_COUNTER = 64;

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private int mCurrentCounter = 0;

    private DataAdapter mDataAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initTitleBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

    }

    @Override
    public void initComponents() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    public void initComponentsData() {
        //init data
        ArrayList<ItemModel> dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            ItemModel item = new ItemModel();
            item.id = i;
            item.title = "item" + i;
            dataList.add(item);
        }

        mCurrentCounter = dataList.size();

        mDataAdapter = new DataAdapter(this);
        mDataAdapter.addItems(dataList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        IDataGetter dataGetter = new IDataGetter() {
            @Override
            public void loadingData() {
                int state = RecyclerViewCompat.getRecyclerLoadingState(recyclerView);
                if (state == PageLoadingFooter.VIEW_STATE_LOADING) {
                    return;
                }

                if (mCurrentCounter < TOTAL_COUNTER) {
                    onLoading();
                    RecyclerViewCompat.tryToGetLoadingFooter(recyclerView).startLoading();
                } else {
                    RecyclerViewCompat.tryToGetLoadingFooter(recyclerView).loadLastPage();
                }
            }
        };
        RecyclerViewCompat.setAdapterAndLoadingFooter(recyclerView, mDataAdapter, dataGetter);
        XDividerDecoration decoration = new DecorationFactory.Builder()
                .onlySize(30)
                .orientation(DecorationFactory.HORIZONTAL)
                .paintBottom(true)
                        .gridLayout(false)
//                .drawable(ContextCompat.getDrawable(this,R.drawable.divider))
                .build();
        recyclerView.addItemDecoration(decoration);
    }

    private class DataAdapter extends RecyclerView.Adapter {

        private LayoutInflater mLayoutInflater;
        private SortedList<ItemModel> mSortedList;

        public DataAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            mSortedList = new SortedList<>(ItemModel.class, new SortedList.Callback<ItemModel>() {

                /**
                 * 返回一个负整数（第一个参数小于第二个）、零（相等）或正整数（第一个参数大于第二个）
                 */
                @Override
                public int compare(ItemModel o1, ItemModel o2) {

                    if (o1.id < o2.id) {
                        return -1;
                    } else if (o1.id > o2.id) {
                        return 1;
                    }

                    return 0;
                }

                @Override
                public boolean areContentsTheSame(ItemModel oldItem, ItemModel newItem) {
                    return oldItem.title.equals(newItem.title);
                }

                @Override
                public boolean areItemsTheSame(ItemModel item1, ItemModel item2) {
                    return item1.id == item2.id;
                }

                @Override
                public void onInserted(int position, int count) {
                    notifyItemRangeInserted(position, count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    notifyItemRangeRemoved(position, count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    notifyItemMoved(fromPosition, toPosition);
                }

                @Override
                public void onChanged(int position, int count) {
                    notifyItemRangeChanged(position, count);
                }
            });
        }

        public void addItems(ArrayList<ItemModel> list) {
            mSortedList.beginBatchedUpdates();

            for(ItemModel itemModel : list) {
                mSortedList.add(itemModel);
            }

            mSortedList.endBatchedUpdates();
        }

        public void deleteItems(ArrayList<ItemModel> items) {
            mSortedList.beginBatchedUpdates();
            for (ItemModel item : items) {
                mSortedList.remove(item);
            }
            mSortedList.endBatchedUpdates();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.sample_item_text, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ItemModel item = mSortedList.get(position);

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.textView.setText(item.title);
            if(position%2==0){
                viewHolder.itemView.setBackgroundColor(Colors.BLUE);
            }else {
                viewHolder.itemView.setBackgroundColor(Colors.YELLOW);
            }
        }

        @Override
        public int getItemCount() {
            return mSortedList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.info_text);

            }
        }
    }

    void onLoading(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final ArrayList<ItemModel> dataList = new ArrayList<>();
                int startCount = mCurrentCounter;
                for (int i = startCount; i < startCount+10; i++) {

                    ItemModel item = new ItemModel();
                    item.id = i;
                    item.title = "item" + i;
                    dataList.add(item);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCurrentCounter >= 39) {
                            RecyclerViewCompat.tryToGetLoadingFooter(recyclerView).loadErrorHappened();
                            return;
                        }
                        RecyclerViewCompat.tryToGetLoadingFooter(recyclerView).finishLoading();
                        mDataAdapter.addItems(dataList);
                        mDataAdapter.notifyDataSetChanged();
                        mCurrentCounter =mDataAdapter.getItemCount();

                    }
                });
            }
        }).start();
    }
}
