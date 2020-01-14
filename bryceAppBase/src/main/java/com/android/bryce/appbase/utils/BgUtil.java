package com.android.appbase.utils;

import com.biaoqing.library.R;

import java.util.Random;

/**
 * @Description:
 * @Author: zenghuanxing
 * @Date: $date$ $time$$params$
 */
public class BgUtil {

    public static int getImgBg() {
        //占位符随机颜色
        int resId = R.drawable.shape_placeholder_bg_1;
        Random random = new Random();
        int randomNum = random.nextInt(7);
        switch (randomNum) {
            case 0:
                resId = R.drawable.shape_placeholder_bg_1;
                break;
            case 1:
                resId = R.drawable.shape_placeholder_bg_2;
                break;
            case 2:
                resId = R.drawable.shape_placeholder_bg_3;
                break;
            case 3:
                resId = R.drawable.shape_placeholder_bg_4;
                break;
            case 4:
                resId = R.drawable.shape_placeholder_bg_5;
                break;
            case 5:
                resId = R.drawable.shape_placeholder_bg_6;
                break;
            case 6:
                resId = R.drawable.shape_placeholder_bg_7;
                break;
        }
        return resId;
    }
}
