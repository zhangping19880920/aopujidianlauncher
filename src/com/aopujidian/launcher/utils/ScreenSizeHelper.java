package com.aopujidian.launcher.utils;

import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class ScreenSizeHelper {
	private static final String TAG = "ScreenSizeHelper";

	public static void getScreenSize(Activity activity) {
		WindowManager windowManager = activity.getWindowManager();  
        Display display = windowManager.getDefaultDisplay();  
        int screenWidth = screenWidth = display.getWidth();  
        int screenHeight = screenHeight = display.getHeight();  
        
        Log.e(TAG, "screenWidth = " + screenWidth + " ,screenHeight = " + screenHeight);
	}
}
