package com.aopujidian.launcher.floatwindow;

import com.aopujidian.launcher.R;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class FloatWindowSmallView extends LinearLayout {

	/**记录小悬浮窗的宽*/
	public static int viewWidth;
	/**记录小悬浮窗的高*/
	public static int viewHeight;

	/**用于更新小悬浮窗的位*/
	private WindowManager windowManager;
	/**小悬浮窗的参*/
	private WindowManager.LayoutParams mParams;

	/** 记录当前手指位置在屏幕上的横坐标*/
	private float xInScreen;
	/**记录当前手指位置在屏幕上的纵坐标*/
	private float yInScreen;
	/**记录手指按下时在屏幕上的横坐标的*/
	private float xDownInScreen;
	/**记录手指按下时在屏幕上的纵坐标的*/
	private float yDownInScreen;
	/**记录手指按下时在小悬浮窗的View上的横坐标的*/
	private float xInView;
	/**记录手指按下时在小悬浮窗的View上的纵坐标的*/
	private float yInView;

	public FloatWindowSmallView(Context context) {
		super(context);
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
		View view = findViewById(R.id.small_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Rect frame = new Rect();
		getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		xInScreen = event.getRawX();
		yInScreen = event.getRawY() - statusBarHeight;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时记录必要数,纵坐标的值都要减去状态栏高
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY() - statusBarHeight;
			break;
		case MotionEvent.ACTION_MOVE:
			// 手指移动的时候更新小悬浮窗的位置
			updateViewPosition();
			
			break;
		case MotionEvent.ACTION_UP:
			// 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件�?
			if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
				openBigWindow();
			}
			xInView = 0;
			yInView = 0;
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	/**
	 * 更新小悬浮窗在屏幕中的位置
	 */
	private void updateViewPosition() {
		mParams.x = (int) (xInScreen - xInView);
		mParams.y = (int) (yInScreen - yInView);
		windowManager.updateViewLayout(this, mParams);
	}

	/**
	 * 打开大悬浮窗，同时关闭小悬浮窗
	 */
	private void openBigWindow() {
		MyWindowManager.createBigWindow(getContext());
		MyWindowManager.removeSmallWindow(getContext());
	}
}
