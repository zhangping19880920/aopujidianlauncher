package com.aopujidian.launcher.floatwindow;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FloatWindowService extends Service {

	private static final String TAG = "FloatWindowService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		// 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗
		if (!MyWindowManager.isWindowShowing()) {
			MyWindowManager.createSmallWindow(getApplicationContext());
		}
		return super.onStartCommand(intent, flags, startId);
	}
}
