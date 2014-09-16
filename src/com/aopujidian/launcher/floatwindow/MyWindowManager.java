package com.aopujidian.launcher.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.ScaleAnimation;

public class MyWindowManager {

	/**小悬浮窗View的实*/
	private static FloatWindowSmallView smallWindow;
	/**大悬浮窗View的实*/
	private static FloatWindowBigView bigWindow;

	/**小悬浮窗View的参*/
	private static LayoutParams smallWindowParams;
	/**大悬浮窗View的参*/
	private static LayoutParams bigWindowParams;

	/**用于控制在屏幕上添加或移除悬浮窗*/
	private static WindowManager mWindowManager;

	/**
	 * 创建小悬浮窗。初始位置为屏幕的右部中间位置
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createSmallWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		if (smallWindow == null) {
			smallWindow = new FloatWindowSmallView(context);
			if (smallWindowParams == null) {
				smallWindowParams = new LayoutParams();
				smallWindowParams.type = LayoutParams.TYPE_PHONE;
				smallWindowParams.format = PixelFormat.RGBA_8888;
				smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				smallWindowParams.width = FloatWindowSmallView.viewWidth;
				smallWindowParams.height = FloatWindowSmallView.viewHeight;
				smallWindowParams.x = ScreenUtils.getScreenW();
				smallWindowParams.y = ScreenUtils.getScreenH() / 2;
			}
			smallWindow.setParams(smallWindowParams);
			windowManager.addView(smallWindow, smallWindowParams);
		}
	}

	/**
	 * 将小悬浮窗从屏幕上移除
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeSmallWindow(Context context) {
		if (smallWindow != null) {
			final WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(smallWindow);
			smallWindow = null;
		}
	}

	/**
	 * 创建大悬浮窗。位置为屏幕正中间
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createBigWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		if (bigWindow == null) {
			bigWindow = new FloatWindowBigView(context);
			if (bigWindowParams == null) {
				bigWindowParams = new LayoutParams();
				bigWindowParams.x = ScreenUtils.getScreenW() / 2 - FloatWindowBigView.viewWidth / 2;
				bigWindowParams.y = ScreenUtils.getScreenH() / 2 - FloatWindowBigView.viewHeight / 2;
				bigWindowParams.type = LayoutParams.TYPE_PHONE;
				bigWindowParams.format = PixelFormat.RGBA_8888;
				bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				bigWindowParams.width = FloatWindowBigView.viewWidth;
				bigWindowParams.height = FloatWindowBigView.viewHeight;
			}
			windowManager.addView(bigWindow, bigWindowParams);
			
			ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, 0.5f, 0.5f);
			scaleAnimation.setDuration(500);
			View childAt = bigWindow.getChildAt(0);
			childAt.startAnimation(scaleAnimation);
		}
	}

	/**
	 * 将大悬浮窗从屏幕上移除
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeBigWindow(final Context context) {
		if (bigWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(bigWindow);
			bigWindow = null;
		}
	}

	/**
	 * 是否有悬浮窗(包括小悬浮窗和大悬浮窗显示在屏幕上
	 * @return 有悬浮窗显示在桌面上返回true，没有的话返回false�?	 */
	public static boolean isWindowShowing() {
		return smallWindow != null || bigWindow != null;
	}
	
	/**
	 * 如果WindowManager还未创建，则创建新的WindowManager返回。否则返回当前已创建的WindowManager�?	 * 
	 * @param context 必须为应用程序的Context.
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗*/
	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

}
