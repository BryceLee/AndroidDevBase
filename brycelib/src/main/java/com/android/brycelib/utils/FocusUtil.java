package com.android.brycelib.utils;

import android.view.View;

public class FocusUtil {
    public static void setFoucus(View v){
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.requestFocusFromTouch();
    }
    public static void clearFoucus(View v){
        v.setFocusable(false);
        v.setFocusableInTouchMode(false);
        v.clearFocus();
    }
}
