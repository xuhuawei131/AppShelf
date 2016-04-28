package com.x91tec.appshelf.v7;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by oeager on 16-4-13.
 */
public class XRecyclerView extends RecyclerView {

    LayoutManagerCompat mLayout;

    HeaderDecoration mHeaderDecoration;

    FooterDecoration mFooterDecoration;

    SizeLayout mHeaderView;

    SizeLayout mFooterView;

    boolean isVertical;

    Callback mCallback;

    boolean attachHeader = false;

    boolean attachFooter = false;

    public XRecyclerView(Context context) {
        super(context);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        mLayout = new LayoutManagerCompat(layout);
        isVertical = mLayout.isVertical();
    }

    public void attachHeader(SizeLayout view) {
        validate();
        if (mHeaderView != null) {
            throw new UnsupportedOperationException("can not attach header twice and more");
        }
        mHeaderView = view;
        attachHeader = true;
        mHeaderDecoration = new HeaderDecoration();
        mHeaderView.registerSizeChangedListener(new SizeLayout.SizeChangedListener() {
            @Override
            public void onSizeChanged(int width, int height) {
                onHeaderSizeChanged(width, height);
                post(new Runnable() {
                    @Override
                    public void run() {
                        invalidateItemDecorations();
                    }
                });
            }
        });
        addItemDecoration(mHeaderDecoration, 0);
    }
    public void attachFooter(SizeLayout view, Callback callback) {
        validate();
        if (mFooterView != null) {
            throw new UnsupportedOperationException("can not attach header twice and more");
        }
        mFooterView = view;
        attachFooter = true;
        mCallback = callback;
        mFooterDecoration = new FooterDecoration();
        addItemDecoration(mFooterDecoration);
        mFooterView.registerSizeChangedListener(new SizeLayout.SizeChangedListener() {
            @Override
            public void onSizeChanged(int width, int height) {
                onFooterSizeChanged(width, height);
                post(new Runnable() {
                    @Override
                    public void run() {
                        invalidateItemDecorations();
                    }
                });
            }
        });
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                onEndChecked();
            }
        });
    }
    void onScrolledOfHeader() {
        if(!attachHeader){
            return;
        }
        if (mLayout == null) {
            return;
        }
        //处理header
        boolean firstRowVisible = mLayout.isFirstRowVisible();
        if (mHeaderView != null) {
            mHeaderView.setVisibility(firstRowVisible ? VISIBLE : INVISIBLE);
            if (firstRowVisible) {
                final int translation = -(isVertical ? computeVerticalScrollOffset()-getPaddingTop() : computeHorizontalScrollOffset()-getPaddingLeft());
                if (isVertical) {
                    mHeaderView.setTranslationY(translation);
                } else {
                    mHeaderView.setTranslationX(translation);
                }
            }
        }
    }

    void onScrolledOfFooter() {
        if(!attachFooter){
            return;
        }
        if (mLayout == null) {
            return;
        }
        //处理Footer
        boolean LastRowVisible = mLayout.isLastRowVisible();
        if (mFooterView != null) {
            mFooterView.setVisibility(LastRowVisible ? VISIBLE : INVISIBLE);
            if (LastRowVisible) {
                onEndChecked();
            }
        }
    }

    public void onEndChecked(){
        final int translation = calculateTranslation();
        if (isVertical) {
            mFooterView.setTranslationY(translation);
        } else {
            mFooterView.setTranslationX(translation);
        }
    }



    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if(attachHeader){
            onScrolledOfHeader();
        }
        if(attachFooter){
            onScrolledOfFooter();
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if(!attachFooter){
            return;
        }
        if (mLayout == null) {
            return;
        }
        if (state == SCROLL_STATE_IDLE && mCallback != null&&mLayout.getVisibleItemCount()>0) {
            if (mLayout.isLastRowVisible()) {
                mCallback.onNextPageLoad();
            }
        }
    }

    void validate() {
        if (getLayoutManager() == null) {
            throw new IllegalStateException("Be sure to attach after setting your RecyclerView's LayoutManager.");
        }
    }

    @Override
    public void onChildDetachedFromWindow(View child) {
        super.onChildDetachedFromWindow(child);
        post(mChildDetachedRunnable);
    }

    final Runnable mChildDetachedRunnable = new Runnable() {
        @Override
        public void run() {
            invalidateItemDecorations();
            onScrolledOfHeader();
            onScrolledOfFooter();
        }
    };


    public void onHeaderSizeChanged(int width, int height) {
        if (mHeaderDecoration != null) {
            mHeaderDecoration.setSize(width, height);
        }
        invalidateItemDecorations();
    }

    public void onFooterSizeChanged(int width, int height) {
        if (mFooterDecoration != null) {
            mFooterDecoration.setSize(width, height);
        }
        invalidateItemDecorations();
    }

    private int calculateTranslation() {
        int offset = isVertical ? computeVerticalScrollOffset() : computeHorizontalScrollOffset();
        int base = getTranslationBase();
        return base - offset;
    }

    public final int getTranslationBase() {
        return isVertical ?
                computeVerticalScrollRange()+getPaddingTop() - getHeight() :
                computeHorizontalScrollRange()+getPaddingLeft() - getWidth();
    }

    static class BaseItemDecoration extends ItemDecoration {

        int width, height;

        public void setSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    class HeaderDecoration extends BaseItemDecoration {
        final int targetSpan;

        public HeaderDecoration() {
            targetSpan = mLayout.getFirstRowSpan();
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int position = parent.getChildAdapterPosition(view);
            final boolean headerRelatedPosition = position < targetSpan;
            int heightOffset = headerRelatedPosition && isVertical ? height : 0;
            int widthOffset = headerRelatedPosition && !isVertical ? width : 0;
            outRect.top = heightOffset;
            outRect.left = widthOffset;
        }
    }

    class FooterDecoration extends BaseItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int position = parent.getChildAdapterPosition(view);
            final boolean footerRelatedPosition = position >= mLayout.getLastRowSpan();
            int heightOffset = footerRelatedPosition && isVertical ? height : 0;
            int widthOffset = footerRelatedPosition && !isVertical ? width : 0;
            outRect.bottom = heightOffset;
            outRect.right = widthOffset;
        }
    }

}
