package com.x91tec.appshelf.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

/**
 * Created by oeager on 16-4-28.
 */
public class MultiStateLayout extends ViewGroup {

    public final static int STATE_LOADING = 0;

    public final static int STATE_EMPTY = 1;

    public final static int STATE_ERROR = 2;

    public final static int STATE_CONTENT = 3;

    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;

    private int mCurrentState = STATE_CONTENT;

    private final SparseArray<View> mStateCache = new SparseArray<>();

    private boolean hasDefined = false;

    public MultiStateLayout(Context context) {
        super(context);
    }

    public static MultiStateLayout attachToActivity(Activity activity){

        ViewGroup content = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup parent = (ViewGroup) content.getParent();
        MultiStateLayout multiStateLayout = new MultiStateLayout(activity);
        int index = parent.indexOfChild(content);
        parent.removeView(content);
        LayoutParams params = content.getLayoutParams();
        multiStateLayout.attachLayout(STATE_CONTENT, content);
        parent.addView(multiStateLayout, index, params);
        return multiStateLayout;
    }

    public static MultiStateLayout attach(Context context,ViewGroup group){

        ViewGroup parent = (ViewGroup) group.getParent();
        MultiStateLayout multiStateLayout = new MultiStateLayout(context);
        int index = parent.indexOfChild(group);
        parent.removeView(group);
        LayoutParams params = group.getLayoutParams();
        multiStateLayout.attachLayout(STATE_CONTENT, group);
        parent.addView(multiStateLayout, index, params);
        return multiStateLayout;
    }

    public synchronized MultiStateLayout attachLayout(int state,View childView){
        if(hasDefined){
            throw new IllegalArgumentException("attachLayout must before compile");
        }
        View child = mStateCache.get(state);
        if(child!=null){
            throw new IllegalArgumentException("had attach layout of state ="+state);
        }
        if(childView==null){
            return this;
        }
        addView(childView);
        mStateCache.put(state, childView);
        return this;
    }

    public synchronized MultiStateLayout attachLayout(int state,@LayoutRes int layout){
        if(hasDefined){
            throw new IllegalArgumentException("attachLayout must before compile");
        }
        View child = mStateCache.get(state);
        if(child!=null){
           throw new IllegalArgumentException("had attach layout of state ="+state);
        }
        child =LayoutInflater.from(getContext()).inflate(layout,this,false);
        addView(child);
        mStateCache.put(state, child);
        return this;
    }

    public synchronized void detachLayout(int state){
        View child = mStateCache.get(state);
        if(child==null){
            return;
        }
        mStateCache.delete(state);
    }

    public synchronized StateController compile(){
        hasDefined = true;
        mCurrentState = STATE_CONTENT;
        int size = mStateCache.size();
        for (int i =0;i<size;i++){
            int state = mStateCache.keyAt(i);
            View view = mStateCache.valueAt(i);
            if(state==mCurrentState){
                ViewUtils.showView(view);
                continue;
            }
            ViewUtils.hideView(view);
        }
        return mStateController;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        final boolean measureMatchParentChildren =
                MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                        MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                if (measureMatchParentChildren&&(lp.width == LayoutParams.MATCH_PARENT ||
                        lp.height == LayoutParams.MATCH_PARENT)) {
                    final int childWidthMeasureSpec;
                    if (lp.width == LayoutParams.MATCH_PARENT) {
                        final int width = Math.max(0, getMeasuredWidth()
                                - getPaddingLeft() - getPaddingRight()
                                - lp.leftMargin - lp.rightMargin);
                        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                                width, MeasureSpec.EXACTLY);
                    } else {
                        childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                                getPaddingLeft() + getPaddingRight() +
                                        lp.leftMargin + lp.rightMargin,
                                lp.width);
                    }

                    final int childHeightMeasureSpec;
                    if (lp.height == LayoutParams.MATCH_PARENT) {
                        final int height = Math.max(0, getMeasuredHeight()
                                - getPaddingTop() - getPaddingBottom()
                                - lp.topMargin - lp.bottomMargin);
                        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                                height, MeasureSpec.EXACTLY);
                    } else {
                        childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                                getPaddingTop() + getPaddingBottom() +
                                        lp.topMargin + lp.bottomMargin,
                                lp.height);
                    }

                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }else {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                    maxWidth = Math.max(maxWidth,
                            child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                    maxHeight = Math.max(maxHeight,
                            child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                    childState = combineMeasuredStates(childState, child.getMeasuredState());
                }


            }
        }

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());


        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }

                final int horizontalGravity = gravity&Gravity.HORIZONTAL_GRAVITY_MASK;
                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                switch (horizontalGravity) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = parentLeft + (parentRight - parentLeft - width) / 2 +
                                lp.leftMargin - lp.rightMargin;
                        break;
                    case Gravity.RIGHT:
                        childLeft = parentRight - width - lp.rightMargin;
                        break;
                    case Gravity.LEFT:
                    default:
                        childLeft = parentLeft + lp.leftMargin;
                }

                switch (verticalGravity) {
                    case Gravity.TOP:
                        childTop = parentTop + lp.topMargin;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = parentTop + (parentBottom - parentTop - height) / 2 +
                                lp.topMargin - lp.bottomMargin;
                        break;
                    case Gravity.BOTTOM:
                        childTop = parentBottom - height - lp.bottomMargin;
                        break;
                    default:
                        childTop = parentTop + lp.topMargin;
                }

                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof FrameLayout.LayoutParams;
    }
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(),attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new FrameLayout.LayoutParams(p);
    }

    final StateController mStateController = new StateController() {
        @Override
        public void showLoading(boolean animate) {
            checkViewState(STATE_LOADING,animate);
        }

        @Override
        public void showContent(boolean animate) {
            checkViewState(STATE_CONTENT,animate);
        }

        @Override
        public void showEmpty(boolean animate) {
            checkViewState(STATE_EMPTY,animate);
        }

        @Override
        public void showError(boolean animate) {
            checkViewState(STATE_ERROR,animate);
        }

        @Override
        public void showState(int state, boolean animate) {
            checkViewState(state,animate);
        }

        public int getCurrentState(){
            return mCurrentState;
        }

        public View getStateView(int state){
            return mStateCache.get(state);
        }
    };

    void checkViewState(int willState,boolean animate){
        int size = mStateCache.size();
        for (int i =0;i<size;i++){
            int state = mStateCache.keyAt(i);
            if(state==willState||state==mCurrentState){
                continue;
            }
            View view = mStateCache.valueAt(i);
            ViewUtils.hideView(view);
        }
        if(willState==mCurrentState){
            return;
        }
        if(animate){
            ViewUtils.hideViewAnimated(mStateCache.get(mCurrentState));
            ViewUtils.showViewAnimated(mStateCache.get(willState));
        }else {
            ViewUtils.hideView(mStateCache.get(mCurrentState));
            ViewUtils.showView(mStateCache.get(willState));
        }
        mCurrentState = willState;
    }




    public interface StateController{

        void showLoading(boolean animate);

        void showContent(boolean animate);

        void showEmpty(boolean animate);

        void showError(boolean animate);

        void showState(int state,boolean animate);

        int getCurrentState();

        View getStateView(int state);
    }
}
