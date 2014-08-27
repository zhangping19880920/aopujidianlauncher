package com.aopujidian.launcher;

import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    
    private static final String ANDROID_LAUNCHER_PACKAGE = "com.android.launcher";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick (View view) {
    	switch (view.getId()) {
		case R.id.ib_top_first:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.top_first), Toast.LENGTH_SHORT).show();
			break;
		case R.id.ib_top_second:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.top_second), Toast.LENGTH_SHORT).show();
			break;
		case R.id.ib_top_third:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.top_third), Toast.LENGTH_SHORT).show();
			//TODO 进入电子屏幕
			goActivity(Slide.class);
			break;
		case R.id.ib_top_fourth:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.top_fourth) + "进入android 桌面", Toast.LENGTH_SHORT).show();
			//TODO 进入android桌面
			goLauncher();
			break;
		case R.id.ib_bottom_first:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.buttom_first), Toast.LENGTH_SHORT).show();
			break;
		case R.id.ib_bottom_second:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.buttom_second), Toast.LENGTH_SHORT).show();
			//TODO 提示停车
			goActivity(Parking.class);
			break;
		case R.id.ib_bottom_third:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.buttom_third), Toast.LENGTH_SHORT).show();
			break;
		case R.id.ib_bottom_fourth:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.buttom_fourth), Toast.LENGTH_SHORT).show();
			break;
		}
    }
    
    /** 启动activity */
    private void goActivity(Class<?> cls){
    	Intent intent = new Intent(getApplicationContext(), cls);
    	try {
    		startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getApplicationContext(), R.string.do_not_start_launcher, 0).show();
		}
    }
    
    /** 启动android桌面 */
    private void goLauncher(){
    	Intent intent = new Intent();
		intent.setPackage(ANDROID_LAUNCHER_PACKAGE);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			String msg = getString(R.string.do_not_start_launcher) + getString(R.string.top_fourth);
			Toast.makeText(getApplicationContext(), msg, 0).show();
		}
    }
}
