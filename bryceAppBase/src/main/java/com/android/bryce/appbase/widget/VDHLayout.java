package com.android.appbase.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


import com.android.appbase.utils.ScreenUtils;
import com.android.appbase.utils.common.LogUtil;

import static android.support.v4.widget.ViewDragHelper.STATE_IDLE;

public class VDHLayout extends LinearLayout {

    private ViewDragHelper mDragger;
    private View mDragView;
    private int newLeft;
    private int newTop = 0;
    private int limitTop = 0;
    private int limitBottom = (int) (ScreenUtils.getFullScreenHeight());

    public VDHLayout(Context context) {
        super(context);
        init();
    }

    public VDHLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            View view;

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                //mEdgeTrackerView禁止直接移动
                boolean isCatch = child == mDragView;
                return isCatch;
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
                if (state == STATE_IDLE) {

                } else {
                }
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (mDragView == null) {
                    return super.clampViewPositionHorizontal(child, left, dx);
                }
                final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - mDragView.getWidth() - leftBound;
                newLeft = Math.min(Math.max(left, leftBound), rightBound);
                view = child;
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (mDragView == null) {
                    return super.clampViewPositionVertical(child, top, dy);
                }
                final int topBound = getPaddingTop();
                final int bottomBound = getHeight() - mDragView.getHeight();

                newTop = Math.min(Math.max(top, topBound), bottomBound);
                return newTop;
            }

            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (mDragView == null) {
                    return;
                }
                if (newTop < limitTop) {
                    newTop = limitTop;
                }
                if (newTop > limitBottom) {
                    newTop = limitBottom;
                }
                mDragView.layout(newLeft, newTop, newLeft + mDragView.getWidth(), newTop + mDragView.getHeight());
            }

            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int width = (getWidth() - mDragView.getWidth()) / 2;
                final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - mDragView.getWidth() - leftBound;
                if (newLeft > width) {
                    newLeft = rightBound;
                } else {
                    newLeft = 0;
                }
                mDragView.layout(newLeft, newTop, newLeft + mDragView.getWidth(), newTop + mDragView.getHeight());
            }

            @Override
            public boolean onEdgeLock(int edgeFlags) {
                return true;
            }

            //在边界拖动时回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
//                mDragger.captureChildView(mEdgeTrackerView, pointerId);
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }

        });
        mDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mDragView != null) {
            boolean iscatch = ev.getX() > mDragView.getLeft() && ev.getX() < mDragView.getRight() && ev.getY() > mDragView.getTop() && ev.getY() < mDragView.getBottom();
            LogUtil.d("VDHLAYOUT,dispatchTouchEvent:" + iscatch);
            getParent().requestDisallowInterceptTouchEvent(iscatch);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mDragView != null) {
            return mDragger.shouldInterceptTouchEvent(event);
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return false;
    }

    @Override
    public void computeScroll() {
    }

    /**
     * 貌似是因为在textture图层之上，所有View会被强制重回，所以需要重新layout，否则会回到原始问题
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mDragView == null) {
            mDragView = getChildAt(0);
            if (mDragView == null) {
                return;
            }
            //初始化显示宽高
            newTop = 0;
            newLeft = getWidth() - mDragView.getWidth();
            //放置在最外层内部需要限制可滑动高低点
            limitTop = 0;
//            limitBottom = (int) (limitBottom - mDragView.getHeight() - XResUtils.getDimenByBQ(205));
        }
        mDragView.layout(newLeft, newTop, newLeft + mDragView.getWidth(), newTop + mDragView.getHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragView = getChildAt(0);
    }
}
