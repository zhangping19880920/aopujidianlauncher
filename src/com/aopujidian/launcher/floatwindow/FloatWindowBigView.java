package com.aopujidian.launcher.floatwindow;

import com.aopujidian.launcher.MainActivity;
import com.aopujidian.launcher.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class FloatWindowBigView extends LinearLayout {
	/**记录大悬浮窗的宽*/
	public static int viewWidth;
	/** 记录大悬浮窗的高*/
	public static int viewHeight;
	
	/**当点击位置与悬浮框的距离小于该时，不认为点击了空白区域*/
	private static final int OFFSET_DISTANCE=5;

	public FloatWindowBigView(final Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		Button close = (Button) findViewById(R.id.close);
		Button back = (Button) findViewById(R.id.back);
		Button startApp = (Button) findViewById(R.id.start);
		
		//打开应用
		startApp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
				Intent intent = new Intent(getContext(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(intent);
				
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.createSmallWindow(context);
			}
		});
		
		//关闭悬浮
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击关闭悬浮窗的时，移除所有悬浮窗，并停止Service
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.removeSmallWindow(context);
				Intent intent = new Intent(getContext(), FloatWindowService.class);
				context.stopService(intent);
			}
		});
		
		//返回
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击返回的时候，移除大悬浮窗，创建小悬浮�?				MyWindowManager.removeBigWindow(context);
				MyWindowManager.createSmallWindow(context);
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN && isTouchBlank(event.getRawX(), event.getRawY())){
			// 点击大悬浮框以外的区域，移除大悬浮窗，创建小悬浮
			MyWindowManager.removeBigWindow(getContext());
			MyWindowManager.createSmallWindow(getContext());
		}
		return true;
	}
	
	/**判断是否点击了大悬浮框以外的区域*/
	private boolean isTouchBlank(float positionX, float positionY){
		if(positionX<(ScreenUtils.getScreenW()-viewWidth)/2-OFFSET_DISTANCE||
				positionX>ScreenUtils.getScreenW()/2+viewWidth/2+OFFSET_DISTANCE||
				positionY<(ScreenUtils.getScreenH()-viewHeight)/2+ScreenUtils.getStatusBarHeight()-OFFSET_DISTANCE||
				positionY>(ScreenUtils.getScreenH())/2+viewHeight/2+ScreenUtils.getStatusBarHeight()+OFFSET_DISTANCE  
				){
			return true;
		}
		return false;
	}
}
