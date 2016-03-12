package com.x91tec.appshelf.v7;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by oeager on 16-3-10.
 */
public class RecyclerViewCompat {

    public static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        RecyclerViewAdapterWrapper adapterWrapper = new RecyclerViewAdapterWrapper(adapter);
        recyclerView.setAdapter(adapterWrapper);
    }

    public static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter, RecyclerFactory.RecyclerExtraAdapter mHeaderFactory, RecyclerFactory.RecyclerExtraAdapter mFooterFactory) {
        RecyclerViewAdapterWrapper adapterWrapper = new RecyclerViewAdapterWrapper(adapter);
        adapterWrapper.setHeaderFactory(mHeaderFactory);
        adapterWrapper.setFooterFactory(mFooterFactory);
        recyclerView.setAdapter(adapterWrapper);
    }

    public static void setHeaderView(RecyclerView recyclerView, View view) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            throw new UnsupportedOperationException("please set recyclerView's adapter first");
        }
        if (adapter instanceof RecyclerViewAdapterWrapper) {
            RecyclerViewAdapterWrapper adapterWrapper = (RecyclerViewAdapterWrapper) adapter;
            RecyclerFactory.RecyclerExtraAdapter headerFactory = adapterWrapper.getHeaderAdapter();
            if (headerFactory == null) {
                headerFactory = new RecyclerFactory.SingleExtraAdapter();
                adapterWrapper.setHeaderFactory(headerFactory);
            }
            int count = headerFactory.getCount();
            for (int i = 0; i < count; i++) {
                headerFactory.removeExtraView(i);
            }
            headerFactory.addExtraView(0, view);
            adapterWrapper.notifyDataSetChanged();
        }
    }

    public static void setAdapterAndHeaderView(RecyclerView recyclerView, RecyclerView.Adapter adapter, View view) {
        RecyclerViewAdapterWrapper adapterWrapper = new RecyclerViewAdapterWrapper(adapter);
        RecyclerFactory.RecyclerExtraAdapter headerFactory = new RecyclerFactory.SingleExtraAdapter();
        headerFactory.addExtraView(0, view);
        adapterWrapper.setHeaderFactory(headerFactory);
        recyclerView.setAdapter(adapterWrapper);
    }

    public static void addHeaderView(RecyclerView recyclerView, View view) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            throw new UnsupportedOperationException("please set recyclerView's adapter first");
        }
        if (adapter instanceof RecyclerViewAdapterWrapper) {
            RecyclerViewAdapterWrapper adapterWrapper = (RecyclerViewAdapterWrapper) adapter;
            RecyclerFactory.RecyclerExtraAdapter headerFactory = adapterWrapper.getHeaderAdapter();
            if (headerFactory == null) {
                headerFactory = new RecyclerFactory.BasicRecyclerExtraAdapter();
                adapterWrapper.setHeaderFactory(headerFactory);
            }
            headerFactory.addExtraView(headerFactory.getCount(), view);
            adapterWrapper.notifyDataSetChanged();
        }
    }


    public static void setFooterView(RecyclerView recyclerView, View view) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            throw new UnsupportedOperationException("please set recyclerView's adapter first");
        }
        if (adapter instanceof RecyclerViewAdapterWrapper) {
            RecyclerViewAdapterWrapper adapterWrapper = (RecyclerViewAdapterWrapper) adapter;
            RecyclerFactory.RecyclerExtraAdapter footerAdapter = adapterWrapper.getFooterAdapter();
            if (footerAdapter == null) {
                footerAdapter = new RecyclerFactory.SingleExtraAdapter();
                adapterWrapper.setFooterFactory(footerAdapter);
            }
            int count = footerAdapter.getCount();
            for (int i = 0; i < count; i++) {
                footerAdapter.removeExtraView(i);
            }
            footerAdapter.addExtraView(0, view);
            adapterWrapper.notifyDataSetChanged();
        }
    }

    public static void setAdapterAndFooterView(RecyclerView recyclerView, RecyclerView.Adapter adapter, View view) {
        RecyclerViewAdapterWrapper adapterWrapper = new RecyclerViewAdapterWrapper(adapter);
        RecyclerFactory.RecyclerExtraAdapter footerFactory = new RecyclerFactory.SingleExtraAdapter();
        footerFactory.addExtraView(0, view);
        adapterWrapper.setFooterFactory(footerFactory);
        recyclerView.setAdapter(adapterWrapper);
    }

    public static void addFooterView(RecyclerView recyclerView, View view) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            throw new UnsupportedOperationException("please set recyclerView's adapter first");
        }
        if (adapter instanceof RecyclerViewAdapterWrapper) {
            RecyclerViewAdapterWrapper adapterWrapper = (RecyclerViewAdapterWrapper) adapter;
            RecyclerFactory.RecyclerExtraAdapter footerExtraAdapter = adapterWrapper.getFooterAdapter();
            if (footerExtraAdapter == null) {
                footerExtraAdapter = new RecyclerFactory.BasicRecyclerExtraAdapter();
                adapterWrapper.setFooterFactory(footerExtraAdapter);
            }
            footerExtraAdapter.addExtraView(footerExtraAdapter.getCount(), view);
            adapterWrapper.notifyDataSetChanged();
        }
    }

    public static void setLoadingFooter(RecyclerView recyclerView, PageLoadingFooter footer) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            throw new UnsupportedOperationException("please set recyclerView's adapter first");
        }
        if (adapter instanceof RecyclerViewAdapterWrapper) {
            RecyclerViewAdapterWrapper adapterWrapper = (RecyclerViewAdapterWrapper) adapter;
            RecyclerFactory.PageLoadingFooterAdapter footerExtraAdapter = new RecyclerFactory.PageLoadingFooterAdapter(footer);
            adapterWrapper.setFooterFactory(footerExtraAdapter);
            adapterWrapper.notifyDataSetChanged();
        }
    }

    public static RecyclerFactory.PageLoadingFooterAdapter setAdapterAndLoadingFooter(RecyclerView recyclerView, RecyclerView.Adapter adapter, PageLoadingFooter footer) {
        RecyclerViewAdapterWrapper adapterWrapper = new RecyclerViewAdapterWrapper(adapter);
        RecyclerFactory.PageLoadingFooterAdapter footerAdapter = new RecyclerFactory.PageLoadingFooterAdapter(footer);
        adapterWrapper.setFooterFactory(footerAdapter);
        recyclerView.setAdapter(adapterWrapper);

        return footerAdapter;
    }

    public static RecyclerFactory.PageLoadingFooterAdapter setAdapterAndLoadingFooter(RecyclerView recyclerView, RecyclerView.Adapter adapter, IDataGetter dataGetter) {
        RecyclerViewAdapterWrapper adapterWrapper = new RecyclerViewAdapterWrapper(adapter);
        RecyclerFactory.PageLoadingFooterAdapter footerAdapter = new RecyclerFactory.PageLoadingFooterAdapter(new SimpleLoadFooter(recyclerView.getContext(),dataGetter));
        adapterWrapper.setFooterFactory(footerAdapter);
        recyclerView.setAdapter(adapterWrapper);
        recyclerView.addOnScrollListener(new RecyclerViewScrollUpListener(dataGetter));
        return footerAdapter;
    }

    public static void setRecyclerLoadingState(RecyclerView recyclerView, @PageLoadingFooter.LoadingState int loadingState) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            throw new UnsupportedOperationException("please set recyclerView's adapter first");
        }
        if (adapter instanceof RecyclerViewAdapterWrapper) {
            RecyclerViewAdapterWrapper adapterWrapper = (RecyclerViewAdapterWrapper) adapter;
            RecyclerFactory.RecyclerExtraAdapter footerAdapter = adapterWrapper.getFooterAdapter();
            if (footerAdapter == null) {
                return;
            }
            if (footerAdapter instanceof RecyclerFactory.PageLoadingFooterAdapter) {
                RecyclerFactory.PageLoadingFooterAdapter pageLoadingFooterAdapter = (RecyclerFactory.PageLoadingFooterAdapter) footerAdapter;
                if (loadingState == PageLoadingFooter.VIEW_STATE_NORMAL) {
                    pageLoadingFooterAdapter.finishLoading();
                } else if (loadingState == PageLoadingFooter.VIEW_STATE_LOADING) {
                    pageLoadingFooterAdapter.startLoading();
                } else if (loadingState == PageLoadingFooter.VIEW_STATE_LAST) {
                    pageLoadingFooterAdapter.loadLastPage();
                } else if (loadingState == PageLoadingFooter.VIEW_STATE_ERROR) {
                    pageLoadingFooterAdapter.loadErrorHappened();
                }

            }
        }
    }

    public static RecyclerFactory.PageLoadingFooterAdapter tryToGetLoadingFooter(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return null;
        }
        if (adapter instanceof RecyclerViewAdapterWrapper) {
            RecyclerViewAdapterWrapper adapterWrapper = (RecyclerViewAdapterWrapper) adapter;
            RecyclerFactory.RecyclerExtraAdapter footerAdapter = adapterWrapper.getFooterAdapter();
            if (footerAdapter == null) {
                return null;
            }
            if (footerAdapter instanceof RecyclerFactory.PageLoadingFooterAdapter) {
                return  (RecyclerFactory.PageLoadingFooterAdapter) footerAdapter;
            }
        }
        return null;
    }

    public static int getRecyclerLoadingState(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return -1;
        }
        if (adapter instanceof RecyclerViewAdapterWrapper) {
            RecyclerViewAdapterWrapper adapterWrapper = (RecyclerViewAdapterWrapper) adapter;
            RecyclerFactory.RecyclerExtraAdapter footerAdapter = adapterWrapper.getFooterAdapter();
            if (footerAdapter == null) {
                return -1;
            }
            if (footerAdapter instanceof RecyclerFactory.PageLoadingFooterAdapter) {
                RecyclerFactory.PageLoadingFooterAdapter pageLoadingFooterAdapter = (RecyclerFactory.PageLoadingFooterAdapter) footerAdapter;
                return pageLoadingFooterAdapter.getLoadingState();

            }
        }
        return -1;
    }

    public static void setRecyclerHeaderBinder(RecyclerView recyclerView,RecyclerFactory.IViewDataBinder iViewDataBinder){
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        if (adapter instanceof RecyclerViewAdapterWrapper) {
            RecyclerViewAdapterWrapper adapterWrapper = (RecyclerViewAdapterWrapper) adapter;
            RecyclerFactory.RecyclerExtraAdapter headerAdapter = adapterWrapper.getHeaderAdapter();
            if (headerAdapter == null) {
                return ;
            }
            headerAdapter.setIViewDataBinder(iViewDataBinder);
        }
    }
}
