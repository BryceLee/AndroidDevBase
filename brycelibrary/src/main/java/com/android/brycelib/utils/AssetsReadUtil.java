package com.android.brycelib.utils;
import android.content.res.AssetManager;

import com.biaoqing.library.base.IApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by lizhongxin on 2017/7/20.
 */

public class AssetsReadUtil {
    public static String getAssetsFileString(String name)
    {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        AssetManager am = IApplication.getInstance().getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(am.open(name)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }
}
