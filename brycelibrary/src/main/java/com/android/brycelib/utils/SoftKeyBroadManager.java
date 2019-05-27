package com.android.brycelib.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.biaoqing.library.utils.base.CommonPrefUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lizhongxin on 2017/7/12.
 */

public class SoftKeyBroadManager implements ViewTreeObserver.OnGlobalLayoutListener
{

    public interface SoftKeyboardStateListener
    {
        void onSoftKeyboardOpened(int keyboardHeightInPx);

        void onSoftKeyboardClosed();

        void onMyGlobalLayout();
    }

    private final List<SoftKeyboardStateListener> listeners = new LinkedList<SoftKeyboardStateListener>();
    private final View activityRootView;
    private int lastSoftKeyboardHeightInPx;
    private boolean isSoftKeyboardOpened;

    public SoftKeyBroadManager(View activityRootView) {
        this(activityRootView, false);
    }

    public SoftKeyBroadManager(View activityRootView, boolean isSoftKeyboardOpened) {
        this.activityRootView = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        //r will be populated url the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);
        onMyGlobalLayout();
        final int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
        if (!isSoftKeyboardOpened && heightDiff > 500) { // if more than 100 pixels， its probably a keyboard...
            isSoftKeyboardOpened = true;
            notifyOnSoftKeyboardOpened(heightDiff);

            //保存键盘高度
            CommonPrefUtils.saveKeyboardHeightInPx(heightDiff);

            //if (isSoftKeyboardOpened && heightDiff < 100)
        } else if (isSoftKeyboardOpened && heightDiff < 500) {
            isSoftKeyboardOpened = false;
            notifyOnSoftKeyboardClosed();
        }
    }

    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }

    /**
     * Default value is zero (0)
     *
     * @return last saved keyboard height in px
     */
    public int getLastSoftKeyboardHeightInPx() {
        return lastSoftKeyboardHeightInPx;
    }

    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx) {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx;

        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed() {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }

    //TODO
    private void onMyGlobalLayout() {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onMyGlobalLayout();
            }
        }
    }
}
