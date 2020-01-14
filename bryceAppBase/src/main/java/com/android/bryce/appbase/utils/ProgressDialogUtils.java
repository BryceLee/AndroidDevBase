package com.android.appbase.utils;

//import com.hss01248.dialog.StyledDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;

public class ProgressDialogUtils {
    /**
     * 现在的写法可能出现dialog无法关闭的情况
     * <p>
     * 建议在onPause()方法是关闭dialog
     * <p>
     * 不要重复开启dialog,否则可能出现ANR,特别注意谨慎在ShareUtis开启dialog,必须确保在调用代码前，无法开启dilaog,
     * 否则将出现dialog无法关闭，进一步导致AnR
     */
    private static ProgressDialog dialog;
    public static int currentProgress;

    //	java.lang.IllegalArgumentException: View not attached to window manager
//	This exception usually comes from dialogs that are still active when the activity is finishing.
    //无法全局用一个dialog
    public static void Show(Context context, String text) {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
            dialog = new ProgressDialog(context);
            dialog.setMessage(text);
            dialog.show();
        } catch (Exception e) {
            currentProgress = 0;
        }
    }
    public static void Show(Context context) {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
            dialog = new ProgressDialog(context);
            dialog.setMessage("请稍候...");
            dialog.show();
        } catch (Exception e) {
            currentProgress = 0;
        }
    }
    public static void Show(Context context,DialogInterface.OnCancelListener listener) {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
            dialog = new ProgressDialog(context);
            dialog.setMessage("请稍候...");
            dialog.setOnCancelListener(listener);
            dialog.show();
        } catch (Exception e) {
            currentProgress = 0;
        }
    }
    public static void Show(Context context,boolean isCanClose) {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
            dialog = new ProgressDialog(context);
            dialog.setMessage("请稍候...");
            dialog.setCanceledOnTouchOutside(isCanClose);
            dialog.show();
        } catch (Exception e) {
            currentProgress = 0;
        }
    }

    public static void ShowOld(Context context, String text) {
        try {
            if (dialog != null) {
//            if (dialog.isShowing()) {
//                dialog.cancel();
//            }
            } else {
                dialog = new ProgressDialog(context);
            }
            dialog.setMessage(text);
            dialog.show();
        } catch (Exception e) {
            currentProgress = 0;
        }
    }

    public static void ShowControl(Context context, String text, boolean isCanClose) {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
            dialog = new ProgressDialog(context);
            dialog.setMessage(text);
            dialog.setCanceledOnTouchOutside(isCanClose);
            dialog.show();
        } catch (Exception e) {
            currentProgress = 0;
        }
    }
    public static void To100(){
        if (dialog != null&&Looper.myLooper()==Looper.getMainLooper()&&currentProgress<100) {
            if (dialog.isShowing()) {
                while (currentProgress<100){
                    currentProgress+=1;
                    dialog.setMessage("视频处理中"+currentProgress+"%");
                }
            }
        }
    }
    public static void ShowOldControl(Context context, String text, boolean isCanClose) {
        try {
            if (dialog != null) {
//            if (dialog.isShowing()) {
//                dialog.cancel();
//            }
            } else {
                dialog = new ProgressDialog(context);
            }
            dialog.setMessage(text);
            dialog.setCanceledOnTouchOutside(isCanClose);
            dialog.show();
        } catch (Exception e) {
            currentProgress = 0;
        }
    }

    public static void setText(String message) {
        if (dialog != null && Looper.myLooper() == Looper.getMainLooper()) {
            if (dialog.isShowing()) {
                dialog.setMessage(message);
            }
        }
    }

    public static void setTextCompound(int progress) {
        currentProgress = progress;
        if (dialog != null && Looper.myLooper() == Looper.getMainLooper()) {
            if (dialog.isShowing()) {
                dialog.setMessage("视频合成中" + progress + "%");
            }
        }
    }
    public static void setTextCompress(int progress) {
        currentProgress = progress;
        if (dialog != null && Looper.myLooper() == Looper.getMainLooper()) {
            if (dialog.isShowing()) {
                dialog.setMessage("视频压缩中" + progress + "%");
            }
        }
    }
    public static void setTextCompoundGif(int progress) {
        currentProgress = progress;
        if (dialog != null && Looper.myLooper() == Looper.getMainLooper()) {
            if (dialog.isShowing()) {
                dialog.setMessage("文件合成中" + progress + "%");
            }
        }
    }

    public static void setTextDealWith(int progress) {
        currentProgress = progress;
        if (dialog != null && Looper.myLooper() == Looper.getMainLooper()) {
            if (dialog.isShowing()) {
                dialog.setMessage("视频处理中" + progress + "%");
            }
        }
    }

    public static void Cancel() {
        currentProgress = 0;
        try {
            if (dialog != null) {
                com.orhanobut.logger.Logger.d("dialog,!=null");
                if (dialog.isShowing()) {
                    com.orhanobut.logger.Logger.d("dialog,isshowing_cancel");
                    dialog.cancel();
                } else {
                    com.orhanobut.logger.Logger.d("dialog,isnotshowing");
                }
            } else {
                com.orhanobut.logger.Logger.d("dialog==null");
            }
            dialog = null;
        } catch (Exception e) {

        }
    }

    private ProgressDialogUtils() {
    }

}
