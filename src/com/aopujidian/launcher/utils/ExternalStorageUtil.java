package com.aopujidian.launcher.utils;

import com.aopujidian.launcher.R;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class ExternalStorageUtil {
	public static boolean isMount(Context context) {
		boolean result = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (!result) {
			showUnmounted(context);
		}
		return result;
	}
	
	public static void showUnmounted(Context context){
		Toast.makeText(context, R.string.external_unmount, Toast.LENGTH_SHORT).show();
	}
}
