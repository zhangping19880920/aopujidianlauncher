package com.aopujidian.launcher;


import java.io.File;

import com.aopujidian.launcher.slide.ImageGridActivity;
import com.aopujidian.launcher.utils.LauncherIntents;
import com.aopujidian.launcher.utils.ShowGallery;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClick (View view) {
    	switch (view.getId()) {
		case R.id.ib_top_first:
			goMiracast();
			break;
		case R.id.ib_top_second:
			goAirPlay();
			break;
		case R.id.ib_top_third:
			// 进入电子屏幕
			goActivity(ImageGridActivity.class);
//			goGallery();
			break;
		case R.id.ib_top_fourth:
			// 进入android桌面
			goLauncher();
			break;
		case R.id.ib_bottom_first:
			goTrip();
			break;
		case R.id.ib_bottom_second:
			// 提示停车
			goActivity(Parking.class);
			break;
		case R.id.ib_bottom_third:
			goCamera();
			break;
		case R.id.ib_bottom_fourth:
			goSettings();
			break;
		}
    }
    
    private void goGallery() {
    	ShowGallery.loadFrom(new File(getFilesDir(), "last_thumb"));
		Uri lastThumbnail = ShowGallery.getLastThumbnail(getContentResolver());
		startActivity(new Intent("com.android.camera.action.REVIEW", lastThumbnail));
    	
    	Intent galleryIntent = new Intent();
    	galleryIntent.setAction(Intent.ACTION_MAIN);
    	galleryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    	galleryIntent.addCategory(Intent.CATEGORY_APP_GALLERY);
    	try {
			startActivity(galleryIntent);
		} catch (Exception e) {
			String msg = getString(R.string.do_not_start_activity) + getString(R.string.top_third);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	/** 启动activity */
    private void goActivity(Class<?> cls){
    	Intent intent = new Intent(getApplicationContext(), cls);
    	startActivityWithIntent(intent);
    }
    
    /** 启动android桌面 */
    private void goLauncher(){
    	Intent launcherIntent = LauncherIntents.getLauncherIntent();
    	startActivityWithIntent(launcherIntent);
    }
    
    /** 启动设置 */
    private void goSettings(){
    	Intent settingsIntent = LauncherIntents.getSettingsIntent();
    	startActivityWithIntent(settingsIntent);
    }
    
    private void startActivityWithIntent(Intent intent) {
    	try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			String msg = getString(R.string.do_not_start_activity);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
    }
    
    private void goMiracast() {
    	Intent miracastIntent = LauncherIntents.getMiracastIntent();
    	startActivityWithIntent(miracastIntent);
    }
    
    private void goAirPlay() {
    	Intent airPlayIntent = LauncherIntents.getAirPlayIntent();
    	startActivityWithIntent(airPlayIntent);
    }
    
    private void goTrip() {
    	Intent tripIntent = LauncherIntents.getTripIntent();
    	startActivityWithIntent(tripIntent);
    }
    
    private void goCamera() {
    	Intent cameraIntent = LauncherIntents.getCameraIntent();
    	startActivityWithIntent(cameraIntent);
    }
    
    @Override
    public void onBackPressed() {
//    	super.onBackPressed();
    }
}
