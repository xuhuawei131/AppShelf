package com.x91tec.appshelf.v7;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by oeager on 16-4-14.
 */
public class SizeLayout extends FrameLayout {
    public SizeLayout(Context context) {
        super(context);
    }

    public SizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    SizeChangedListener mSizeChangedListener;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && mSizeChangedListener != null) {
            int verticalMargins = 0;
            int horizontalMargins = 0;
            ViewGroup.LayoutParams mHeaderViewLayoutParams = getLayoutParams();
            if (mHeaderViewLayoutParams instanceof MarginLayoutParams) {
                final MarginLayoutParams layoutParams = (MarginLayoutParams) mHeaderViewLayoutParams;
                verticalMargins = layoutParams.topMargin + layoutParams.bottomMargin;
                horizontalMargins = layoutParams.leftMargin + layoutParams.rightMargin;
            }
            mSizeChangedListener.onSizeChanged(getWidth() + horizontalMargins, getHeight() + verticalMargins);
        }
    }

    public interface SizeChangedListener {
        void onSizeChanged(int width, int height);
    }

    public void registerSizeChangedListener(SizeChangedListener sizeChangedListener) {
        mSizeChangedListener = sizeChangedListener;
        int width = getWidth();
        int height = getHeight();
        if (width > 0 || height > 0) {
            mSizeChangedListener.onSizeChanged(width, height);
        }
    }
}
