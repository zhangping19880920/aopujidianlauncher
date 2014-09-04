package com.aopujidian.launcher.utils;

import android.content.Intent;

public class LauncherIntents {
	
	private static final String ANDROID_LAUNCHER_PACKAGE = "com.android.launcher";
	
	private static final String ANDROI_LAUNCHER3_PACKAGE = "com.android.launcher3";
	
	public static Intent getLauncherIntent() {
		Intent intent = new Intent();
		intent.setPackage(ANDROID_LAUNCHER_PACKAGE);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		return intent;
	}
	
	public static Intent getLauncherIntent3() {
		Intent intent = new Intent();

		intent.setPackage(ANDROI_LAUNCHER3_PACKAGE);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		return intent;
	}
	
	public static Intent getSettingsIntent() {
		Intent settingIntent = new Intent();
		settingIntent.setAction("android.settings.SETTINGS");
		settingIntent.addCategory(Intent.CATEGORY_DEFAULT);
		settingIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		return settingIntent;
	}
	
	public static Intent getMiracastIntent() {
		Intent intent = new Intent();
		intent.setClassName("com.android.settings", "com.android.settings.DisplaySettings");
		return intent;
	}
	
	public static Intent getMiracastIntentl() {
		Intent intent = new Intent();
		intent.setClassName("com.android.settings", "com.android.settings.DisplaySettings");
		return intent;
	}
	
	public static Intent getAirPlayIntent() {
		Intent intent = new Intent();
		return intent;
	}
	
	public static Intent getTripIntent() {
		Intent intent = new Intent();
		intent.setClassName("com.mapbar.android.carobd", "com.mapbar.android.carobd.MainActivity");
		return intent;
	}
	
	public static Intent getCameraIntent() {
		Intent intent = new Intent();
		intent.setClassName("com.android.gallery3d", "com.android.camera.CameraLauncher");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		return intent;
	}
}
