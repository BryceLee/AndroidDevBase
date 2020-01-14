package com.android.appbase.utils.common;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * 页面跳转工具类
 *
 * @author linmd
 * @date 2016-2-24 下午2:39:20
 */
@Deprecated
public class PageSwitchUtil {

	public static void start(Context context, Class<?> target) {
		Intent intent = new Intent(context, target);
		start(context, intent);
	}

	public static void start(Context current, Intent intent) {
		current.startActivity(intent);
	}

	public static void startForResult(Context context, Class<?> target,
									  int requestCode) {
		startForResult(context, new Intent(context, target), requestCode);
	}

	public static void startForResult(Context context, Intent intent,
									  int requestCode) {
		((Activity) context).startActivityForResult(intent, requestCode);
	}


	public static void finish(Activity current) {
		if (null == current)
			return;
		current.finish();
	}
}
