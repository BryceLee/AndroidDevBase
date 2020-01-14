package com.android.appbase.widget.seekbar;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/12
 *     desc  : 图片相关工具类
 * </pre>
 */
public final class ImageUtils {
    private static boolean isEmptyBitmap(final Bitmap src)
    {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    public static Bitmap rotate(final Bitmap src, final int degrees, final float px, final float py, final boolean
            recycle)
    {
        if (isEmptyBitmap(src))
            return null;
        if (degrees == 0)
            return src;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled())
            src.recycle();
        return ret;
    }

}
