package com.x91tec.appshelf.v7;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by oeager on 16-3-11.
 */
public class DecorationFactory {

    public final static int HORIZONTAL = 0;

    public final static int VERTICAL = 1;

    @IntDef({HORIZONTAL,VERTICAL})
    public @interface Orientation{}


    public interface PaintProvider {
        Paint dividerPaint(int position, RecyclerView parent);
    }

    public interface SizeProvider {
        int dividerSize(int position, RecyclerView parent);
    }

    public interface DrawableProvider {
        Drawable drawableProvider(int position, RecyclerView parent);
    }

    public interface ColorProvider {
        int dividerColor(int position, RecyclerView parent);
    }

    public interface VisibilityProvider {

        boolean shouldHideDivider(int position, RecyclerView parent);
    }

    public interface MarginProvider {

        int dividerStartMargin(int position, RecyclerView parent);

        int dividerEndMargin(int position, RecyclerView parent);
    }

    public interface DecorationPainter {

        void painting(RecyclerView recyclerView, Canvas canvas, Rect bounds, int position);

        int dividerSize(int position, RecyclerView parent);
    }

    public interface ItemInfoProvider {


        int lastDividerOffset(RecyclerView parent);

        int itemIndex(int position, RecyclerView parent);

        boolean hasItemDrawnDivider(int position, RecyclerView parent);
    }

    public interface OrientationHandler {
        Rect dividerBoundsSetting(RecyclerView parent, View child, int marginStart, int marginEnd,int dividerSize,boolean isDrawablePaint);

        void setItemOffsets(Rect outRect, int position, int dividerSize);
    }

    static class LinerItemInfoProvider implements ItemInfoProvider {

        @Override
        public int lastDividerOffset(RecyclerView parent) {
            return 1;
        }

        @Override
        public int itemIndex(int position, RecyclerView parent) {
            return position;
        }

        @Override
        public boolean hasItemDrawnDivider(int position, RecyclerView parent) {
            return false;
        }
    }

    static class GridItemInfoProvider implements ItemInfoProvider {

        @Override
        public int lastDividerOffset(RecyclerView parent) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            int itemCount = parent.getAdapter().getItemCount();
            for (int i = itemCount - 1; i >= 0; i--) {
                if (spanSizeLookup.getSpanIndex(i, spanCount) == 0) {
                    return itemCount - i;
                }
            }
            return 1;
        }

        @Override
        public int itemIndex(int position, RecyclerView parent) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            return spanSizeLookup.getSpanGroupIndex(position, spanCount);
        }

        @Override
        public boolean hasItemDrawnDivider(int position, RecyclerView parent) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            return spanSizeLookup.getSpanIndex(position, spanCount) > 0;
        }
    }

    static class PaintPainter implements DecorationPainter {

        public PaintProvider mPaintProvider;

        private Paint mPaint;

        public PaintPainter(@NonNull PaintProvider mPaintProvider) {
            this.mPaintProvider = mPaintProvider;

        }

        @Override
        public void painting(RecyclerView recyclerView, Canvas canvas, Rect bounds, int position) {
            mPaint = mPaintProvider.dividerPaint(position, recyclerView);
            canvas.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
        }

        @Override
        public int dividerSize(int position, RecyclerView parent) {
            return (int) mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
        }
    }

    static class ColorPainter implements DecorationPainter {

        private ColorProvider mColorProvider;

        private Paint mPaint;

        private SizeProvider mSizeProvider;

        public ColorPainter(ColorProvider mColorProvider, SizeProvider sizeProvider) {
            this.mColorProvider = mColorProvider;
            this.mSizeProvider = sizeProvider;
            mPaint = new Paint();
        }

        @Override
        public void painting(RecyclerView recyclerView, Canvas canvas, Rect bounds, int position) {
            int color = mColorProvider.dividerColor(position, recyclerView);
            mPaint.setColor(color);
            mPaint.setStrokeWidth(dividerSize(position, recyclerView));
            canvas.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
        }

        @Override
        public int dividerSize(int position, RecyclerView parent) {
            return mSizeProvider.dividerSize(position, parent);
        }
    }

    static class DrawablePainter implements DecorationPainter {

        private DrawableProvider mDrawableProvider;

        public DrawablePainter(DrawableProvider mDrawableProvider) {
            this.mDrawableProvider = mDrawableProvider;
        }

        @Override
        public void painting(RecyclerView recyclerView, Canvas canvas, Rect bounds, int position) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, recyclerView);
            drawable.setBounds(bounds);
            drawable.draw(canvas);
        }

        @Override
        public int dividerSize(int position, RecyclerView parent) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, parent);
            return drawable.getIntrinsicHeight();
        }
    }

    public static class HorizontalHandler implements OrientationHandler{

        @Override
        public Rect dividerBoundsSetting(RecyclerView parent,View child, int marginStart, int marginEnd,int dividerSize,boolean isDrawablePaint) {
            Rect bounds = new Rect(0, 0, 0, 0);
            int transitionX = (int) ViewCompat.getTranslationX(child);
            int transitionY = (int) ViewCompat.getTranslationY(child);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            bounds.left = parent.getPaddingLeft() +marginStart + transitionX;
            bounds.right = parent.getWidth() - parent.getPaddingRight() -marginEnd + transitionX;
            if(isDrawablePaint){
                bounds.top = child.getBottom() + params.topMargin + transitionY;
            bounds.bottom = bounds.top + dividerSize;
            }else{
                bounds.top = child.getBottom() + params.topMargin + dividerSize / 2 + transitionY;
                bounds.bottom = bounds.top;
            }

            return bounds;
        }

        @Override
        public void setItemOffsets(Rect outRect, int position, int dividerSize) {
            outRect.set(0,0,0,dividerSize);
        }
    }

    public static class VerticalHandler implements OrientationHandler{

        @Override
        public Rect dividerBoundsSetting(RecyclerView parent, View child, int marginStart, int marginEnd, int dividerSize,boolean isDrawablePaint) {
            Rect bounds = new Rect(0, 0, 0, 0);
            int transitionX = (int) ViewCompat.getTranslationX(child);
            int transitionY = (int) ViewCompat.getTranslationY(child);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            bounds.top = parent.getPaddingTop() +marginStart + transitionY;
            bounds.bottom = parent.getHeight() - parent.getPaddingBottom() -marginEnd + transitionY;

            if(isDrawablePaint){
                bounds.left = child.getRight() + params.leftMargin + transitionX;
                bounds.right = bounds.left + dividerSize;
            }else {
                bounds.left = child.getRight() + params.leftMargin + dividerSize / 2 + transitionX;
                bounds.right = bounds.left;
            }
            return bounds;
        }

        @Override
        public void setItemOffsets(Rect outRect, int position, int dividerSize) {
            outRect.set(0, 0, dividerSize, 0);
        }
    }

    public static class Builder {

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        private static final int DEFAULT_SIZE = 2;

        boolean gridLayout = false;

        boolean paintBottomOfEnd = false;

        DecorationPainter mDecorationPainter;

        VisibilityProvider mVisibilityProvider;

        MarginProvider mMarginProvider;

        OrientationHandler mOrientationHandler;

        boolean isDrawablePaint = false;


        public Builder() {

        }

        public Builder paint(final Paint paint) {
            return paintProvider(new PaintProvider() {
                @Override
                public Paint dividerPaint(int position, RecyclerView parent) {
                    return paint;
                }
            });
        }

        public Builder paintProvider(PaintProvider provider) {
            return decorationPainter(new PaintPainter(provider));
        }

        public Builder color(final int color) {
            return colorProvider(new ColorProvider() {
                @Override
                public int dividerColor(int position, RecyclerView parent) {
                    return color;
                }
            });
        }

        public Builder colorProvider(ColorProvider colorProvider) {
            return decorationPainter(new ColorPainter(colorProvider, generateSizeProvider(DEFAULT_SIZE)));
        }

        public Builder colorProvider(ColorProvider colorProvider, int size) {
            return decorationPainter(new ColorPainter(colorProvider, generateSizeProvider(size)));
        }

        public Builder colorProvider(ColorProvider colorProvider, @NonNull SizeProvider sizeProvider) {
            return decorationPainter(new ColorPainter(colorProvider, sizeProvider));
        }

        public Builder drawable(final Drawable d) {
            return drawableProvider(new DrawableProvider() {
                @Override
                public Drawable drawableProvider(int position, RecyclerView parent) {
                    return d;
                }
            });
        }

        public Builder drawableProvider(DrawableProvider drawableProvider) {
            return decorationPainter(new DrawablePainter(drawableProvider));
        }

        public Builder decorationPainter(DecorationPainter painter) {
            this.mDecorationPainter = painter;
            this.isDrawablePaint = painter instanceof DrawablePainter;
            return this;
        }

        public Builder visibilityProvider(VisibilityProvider visibilityProvider) {
            this.mVisibilityProvider = visibilityProvider;
            return this;
        }

        public Builder paintBottom(boolean flag) {
            this.paintBottomOfEnd = flag;
            return this;
        }

        public Builder orientation(@Orientation int orientation){
            this.mOrientationHandler = orientation==HORIZONTAL?new HorizontalHandler():new VerticalHandler();
            return this;
        }

        public Builder gridLayout(boolean flag) {
            this.gridLayout = flag;
            return this;
        }


        public Builder margin(final int leftMargin, final int rightMargin) {
            return marginProvider(new DecorationFactory.MarginProvider() {
                @Override
                public int dividerStartMargin(int position, RecyclerView parent) {
                    return leftMargin;
                }

                @Override
                public int dividerEndMargin(int position, RecyclerView parent) {
                    return rightMargin;
                }
            });
        }

        public Builder margin(int horizontalMargin) {
            return margin(horizontalMargin, horizontalMargin);
        }

        public Builder marginProvider(DecorationFactory.MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        SizeProvider generateSizeProvider(final int size) {
            return new SizeProvider() {
                @Override
                public int dividerSize(int position, RecyclerView parent) {
                    return size;
                }
            };
        }

        public XDividerDecoration build(Context context) {
            checkParams(context);
            return new XDividerDecoration(this);
        }

        void checkParams(Context context) {
            if (mDecorationPainter == null) {
                TypedArray a = context.obtainStyledAttributes(ATTRS);
                final Drawable divider = a.getDrawable(0);
                a.recycle();
                mDecorationPainter = new DrawablePainter(new DrawableProvider() {
                    @Override
                    public Drawable drawableProvider(int position, RecyclerView parent) {
                        return divider;
                    }
                });
            }
            if (mVisibilityProvider == null) {
                mVisibilityProvider = new VisibilityProvider() {
                    @Override
                    public boolean shouldHideDivider(int position, RecyclerView parent) {
                        return false;
                    }
                };
            }
            if (mMarginProvider == null) {
                mMarginProvider = new DecorationFactory.MarginProvider() {
                    @Override
                    public int dividerStartMargin(int position, RecyclerView parent) {
                        return 0;
                    }

                    @Override
                    public int dividerEndMargin(int position, RecyclerView parent) {
                        return 0;
                    }
                };
            }
            if(mOrientationHandler==null){
                mOrientationHandler = new HorizontalHandler();
            }


        }
    }
}
