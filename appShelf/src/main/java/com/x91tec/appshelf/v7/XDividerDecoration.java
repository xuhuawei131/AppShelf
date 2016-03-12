package com.x91tec.appshelf.v7;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by oeager on 16-3-11.
 */

/**
 * 调用顺序为onDraw->drawChildren->onDrawOver
 * getItemOffsets 可以通过outRect.set()为每个Item设置一定的偏移量，主要用于绘制Decorator。
 */
public class XDividerDecoration extends RecyclerView.ItemDecoration {

    private final boolean paintBottomOfEnd;

    private final boolean isDrawablePaint;

    private DecorationFactory.ItemInfoProvider mItemInfoProvider;

    private DecorationFactory.MarginProvider mMarginProvider;

    private DecorationFactory.VisibilityProvider mVisibilityProvider;

    private DecorationFactory.DecorationPainter mDecorationPainter;

    private DecorationFactory.OrientationHandler mOrientationHandler;

    private DecorationFactory.SizeProvider mSizeProvider;

    protected XDividerDecoration(DecorationFactory.Builder builder) {
        this.mDecorationPainter = builder.mDecorationPainter;
        this.mVisibilityProvider = builder.mVisibilityProvider;
        this.paintBottomOfEnd = builder.paintBottomOfEnd;
        this.mMarginProvider = builder.mMarginProvider;
        this.mOrientationHandler = builder.mOrientationHandler;
        this.isDrawablePaint = builder.isDrawablePaint;
        this.mSizeProvider = builder.mSizeProvider;
        setItemInfoProvider(builder.gridLayout);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mDecorationPainter==null){
            return;
        }
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }

        int itemCount = adapter.getItemCount();
        int lastDividerOffset = mItemInfoProvider.lastDividerOffset(parent);
        int validChildCount = parent.getChildCount();
        int lastChildPosition = -1;
        for (int i = 0; i < validChildCount; i++) {
            View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);

            if (childPosition < lastChildPosition) {
                continue;
            }
            lastChildPosition = childPosition;

            if (!paintBottomOfEnd && childPosition >= itemCount - lastDividerOffset) {
                continue;
            }

            if (mItemInfoProvider.hasItemDrawnDivider(childPosition, parent)) {
                continue;
            }

            int groupIndex = mItemInfoProvider.itemIndex(childPosition, parent);
            if (mVisibilityProvider!=null&&mVisibilityProvider.shouldHideDivider(groupIndex, parent)) {
                continue;
            }
            int marginStart =mMarginProvider==null?0: mMarginProvider.dividerStartMargin(groupIndex, parent);
            int marginEnd = mMarginProvider==null?0:mMarginProvider.dividerEndMargin(groupIndex, parent);
            int dividerSize = mDecorationPainter.dividerSize(groupIndex, parent);
            Rect bounds = mOrientationHandler.dividerBoundsSetting(parent, child, marginStart, marginEnd, dividerSize,isDrawablePaint);
            mDecorationPainter.painting(parent, c, bounds, groupIndex);

        }

    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();
        int lastDividerOffset = mItemInfoProvider.lastDividerOffset(parent);
        if (!paintBottomOfEnd && position >= itemCount - lastDividerOffset) {
            return;
        }
        int itemIndex = mItemInfoProvider.itemIndex(position, parent);
        int dividerSize;
        if(mDecorationPainter!=null){
            dividerSize = mDecorationPainter.dividerSize(itemIndex, parent);
        }else if (mSizeProvider!=null){
            dividerSize = mSizeProvider.dividerSize(itemIndex,parent);
        }else{
            dividerSize = 0;
        }
        mOrientationHandler.setItemOffsets(outRect, itemIndex, dividerSize);

    }

    public void setItemInfoProvider(boolean gridLayout) {
        setItemInfoProvider(gridLayout ? new DecorationFactory.GridItemInfoProvider() : new DecorationFactory.LinerItemInfoProvider());
    }

    public void setItemInfoProvider(DecorationFactory.ItemInfoProvider provider) {
        this.mItemInfoProvider = provider;
    }


}
