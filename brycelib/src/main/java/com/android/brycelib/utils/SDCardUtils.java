package com.android.brycelib.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;


import com.android.brycelib.utils.base.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by lizhongxin on 2017/12/6.
 */

public class SDCardUtils {

    private SDCardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断 SD 卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return !getSDCardPaths().isEmpty();
    }

    /**
     * 获取 SD 卡路径
     *
     * @param removable true : 外置 SD 卡<br>false : 内置 SD 卡
     * @return SD 卡路径
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    public static List<String> getSDCardPaths(boolean removable) {
        List<String> paths = new ArrayList<>();
        StorageManager mStorageManager = (StorageManager) Utils.getApp()
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean res = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable == res) {
                    paths.add(path);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return paths;
    }
    @TargetApi(18)
    public static long getFreeSpace() {
        if(!isSDCardEnable()) {
            return 0;
        } else {
            StatFs stat = new StatFs(getSDCardPath());
            long availableBlocks = stat.getAvailableBlocksLong();
            long blockSize = stat.getBlockSizeLong();
//            return ConvertUtils.byte2FitMemorySize(availableBlocks * blockSize);
            return availableBlocks * blockSize;
        }
    }
    public static String getSDCardPath() {
        if(!isSDCardEnable()) {
            return "sdcard unable!";
        } else {
            String cmd = "cat /proc/mounts";
            Runtime run = Runtime.getRuntime();
            BufferedReader bufferedReader = null;

            try {
                Process p = run.exec(cmd);
                bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));

                do {
                    String lineStr;
                    if((lineStr = bufferedReader.readLine()) == null) {
                        return Environment.getExternalStorageDirectory().getPath() + File.separator;
                    }

                    if(lineStr.contains("sdcard") && lineStr.contains(".android_secure")) {
                        String[] strArray = lineStr.split(" ");
                        if(strArray.length >= 5) {
                            String var6 = strArray[1].replace("/.android_secure", "") + File.separator;
                            return var6;
                        }
                    }
                } while(p.waitFor() == 0 || p.exitValue() != 1);

                return Environment.getExternalStorageDirectory().getPath() + File.separator;
            } catch (Exception var10) {
                var10.printStackTrace();
                return Environment.getExternalStorageDirectory().getPath() + File.separator;
            } finally {
                CloseUtils.closeIO(bufferedReader);
            }
        }
    }
    /**
     * 获取 SD 卡路径
     *
     * @return SD 卡路径
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    public static List<String> getSDCardPaths() {
        StorageManager storageManager = (StorageManager) Utils.getApp()
                .getSystemService(Context.STORAGE_SERVICE);
        List<String> paths = new ArrayList<>();
        try {
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            getVolumePathsMethod.setAccessible(true);
            Object invoke = getVolumePathsMethod.invoke(storageManager);
            paths = Arrays.asList((String[]) invoke);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return paths;
    }
}
