package com.aopujidian.launcher.utils;

import android.content.Intent;

public class LauncherIntents {
	
	private static final String ANDROID_LAUNCHER_PACKAGE = "com.android.launcher";
	
	private static final String ANDROID_CAMERA_PACKAGE = "com.android.camera";
	
	public static Intent getLauncherIntent() {
		Intent intent = new Intent();
		intent.setPackage(ANDROID_LAUNCHER_PACKAGE);
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
		return intent;
	}
	
	public static Intent getAirPlayIntent() {
		Intent intent = new Intent();
		return intent;
	}
	
	public static Intent getTripIntent() {
		Intent intent = new Intent();
		return intent;
	}
	
	public static Intent getCameraIntent() {
		Intent intent = new Intent();
		intent.setPackage(ANDROID_CAMERA_PACKAGE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		return intent;
	}
}
