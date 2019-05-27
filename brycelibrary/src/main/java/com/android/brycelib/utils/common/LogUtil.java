package com.android.brycelib.utils.common;

import android.util.Log;

import com.android.brycelib.BuildConfig;


/**
 * Log统一管理类
 *
 * @author Administrator
 */
@Deprecated
public class LogUtil {

    private LogUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    private static boolean isDebug = BuildConfig.DEBUG;

    private static final String TAG = "DebugLog";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug && msg != null)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug && msg != null)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug && msg != null)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug && msg != null)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug && msg != null)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug && msg != null)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug && msg != null)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug && msg != null)
            Log.v(tag, msg);
    }
}
