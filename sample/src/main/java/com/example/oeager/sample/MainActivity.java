package com.example.oeager.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.x91tec.appshelf.components.AppHook;
import com.x91tec.appshelf.components.activities.BaseAppActivity;
import com.x91tec.appshelf.ui.MultiStateLayout;
import com.x91tec.appshelf.v7.Callback;
import com.x91tec.appshelf.v7.DecorationFactory;
import com.x91tec.appshelf.v7.LinearDecoration;
import com.x91tec.appshelf.v7.SizeLayout;
import com.x91tec.appshelf.v7.XRecyclerView;

import java.util.ArrayList;

public class MainActivity extends BaseAppActivity {

    private XRecyclerView recyclerView;

    /**
     * 服务器端一共多少条数据
     */
    private static final int TOTAL_COUNTER = 64;

    /**
     * 每一页展示多少条数据
     */
    private static final int REQUEST_COUNT = 10;

    /**
     * 已经获取到多少条数据了
     */
    private int mCurrentCounter = 0;

    private DataAdapter mDataAdapter = null;

    MultiStateLayout.StateController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewGroup content =  (ViewGroup) findViewById(R.id.root);
        MultiStateLayout layout = MultiStateLayout.attach(this,content)
                .attachLayout(MultiStateLayout.STATE_EMPTY, R.layout.state_empty)
                .attachLayout(MultiStateLayout.STATE_ERROR, R.layout.state_error)
                .attachLayout(MultiStateLayout.STATE_LOADING, R.layout.state_loading);
        controller = layout.compile();
//        showToast("content:count___{"+layout.getChildCount());
//        controller.showLoading(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(1, 1, 1, "显示内容");
        menu.add(1, 2, 1, "暂无数据");
        menu.add(1, 3, 1, "加载失败");
        menu.add(1, 4, 1, "正在加载");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case 1:
                controller.showContent(true);
                return true;
            case 2:
                controller.showEmpty(true);
                return true;
            case 3:
                controller.showError(true);
                return true;
            case 4:
                controller.showLoading(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initTitleBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (XRecyclerView) findViewById(R.id.recyclerView);

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
        SizeLayout footer = (SizeLayout) findViewById(R.id.footer);
        recyclerView.attachFooter(footer, new Callback() {
            @Override
            public void onNextPageLoad() {
//                onLoading();
            }
        });
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStrokeWidth(100);
        LinearDecoration decoration = new LinearDecoration.Builder(DecorationFactory.VERTICAL)
                .header(Color.RED, 1)
                .footer(Color.BLUE, 10)
                .colorProvider(new DecorationFactory.ColorProvider() {
                    @Override
                    public int dividerColor(int position, RecyclerView parent) {
                        int color = 0;
                        switch (position) {
                            case 0:
                                color = Color.WHITE;
                                break;
                            case 1:
                                color = Color.RED;
                                break;
                            case 2:
                                color = Color.BLUE;
                                break;
                            case 3:
                                color = Color.YELLOW;
                                break;
                            default:
                                break;
                        }
                        return color;
                    }
                })
                .size(new DecorationFactory.SizeProvider() {
                    @Override
                    public int dividerSize(int position, RecyclerView parent) {
                        return 100 - 20 * position;
                    }
                })
                .marginProvider(new DecorationFactory.MarginProvider() {
                    @Override
                    public int startMargin(int position, RecyclerView parent) {
                        return position + 1;
                    }

                    @Override
                    public int endMargin(int position, RecyclerView parent) {
                        return position + 1;
                    }
                })
                .build();
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(mDataAdapter);

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

            for (ItemModel itemModel : list) {
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
//            if(position%2==0){
//                viewHolder.itemView.setBackgroundColor(Colors.BLUE);
//            }else {
//                viewHolder.itemView.setBackgroundColor(Colors.YELLOW);
//            }
            viewHolder.textView.setText(item.title);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, TestActivity.class);
                    startActivity(i);
                }
            });
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

    void onLoading() {
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
                for (int i = startCount; i < startCount + 10; i++) {

                    ItemModel item = new ItemModel();
                    item.id = i;
                    item.title = "item" + i;
                    dataList.add(item);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mDataAdapter.addItems(dataList);
                        mDataAdapter.notifyDataSetChanged();
                        mCurrentCounter = mDataAdapter.getItemCount();

                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppHook.get().appExit();
    }
}
