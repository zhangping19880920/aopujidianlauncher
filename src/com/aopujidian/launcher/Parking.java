package com.aopujidian.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aopujidian.launcher.slide.BaseActivity;
import com.aopujidian.launcher.utils.CropImage;
import com.aopujidian.launcher.utils.PrefsConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class Parking extends BaseActivity {
	
	private static final String TAG = "Parking";
	
	private static final int DEFAULT_WIDTH = 640;
	
	private static final int DEFAULT_HEIGHT = 400;
	
	private static final int PHOTO_REQUEST_CUT = 100;
	
	@ViewInject(R.id.iv_background)
	private ImageView mBackgroundImageView;
	
	@ViewInject(R.id.et_text)
	private EditText mEdittext;
	
	@ViewInject(R.id.tv_text)
	private TextView mTextView;
	
	@ViewInject(R.id.btn_edit)
	private TextView mEditButton;
	
	private WakeLock mScreenLock;
	
	private DisplayImageOptions mOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parking_activity);
		ViewUtils.inject(this);
		mScreenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,TAG);
		
		String latText = getSharedPreferences(PrefsConfig.PREFS_NAME, Context.MODE_PRIVATE).getString(PrefsConfig.PREFS_KEY_PARKING, PrefsConfig.PREFS_PARKING_DEFAULT_VALUE);
		if (null != latText && !TextUtils.isEmpty(latText)) {
			mTextView.setText(latText);
		} else {
			edit(mEditButton);
		}
		
		mEdittext.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				edit(mEditButton);
				return false;
			}
		});
		
		mOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.image_loading)
		.showImageForEmptyUri(R.drawable.parking_background)
		.showImageOnFail(R.drawable.parking_background)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	@OnClick(R.id.btn_exit)
	public void exit(View view) {
		finish();
	}

	@OnClick(R.id.btn_set_background)
	public void setBackground(View view) {
		int width = mBackgroundImageView.getWidth();
		int height = mBackgroundImageView.getHeight();
		width = (width == 0 ? DEFAULT_WIDTH : width);
		height = (height == 0 ? DEFAULT_HEIGHT : height);
		float aspect = width / (float)height;
//		Log.e(TAG, "width = " + width + " ,height = " + height + " ,aspect = " + aspect);
		
		Intent cropIntent = CropImage.getCropIntent(width, height, aspect);
		startActivityForResult(cropIntent, PHOTO_REQUEST_CUT);
	}
	
	@OnClick(R.id.btn_edit)
	public void edit(View view){
		Button btn = (Button)view;
		if (mEdittext.isEnabled()) {
			mEdittext.setEnabled(false);
			btn.setText(R.string.edit);
			mEdittext.setVisibility(View.GONE);
			mTextView.setVisibility(View.VISIBLE);
			String text = mEdittext.getText().toString();
			mTextView.setText(text);
			Editor edit = getSharedPreferences(PrefsConfig.PREFS_NAME, Context.MODE_PRIVATE).edit();
			edit.putString(PrefsConfig.PREFS_KEY_PARKING, mTextView.getText().toString());
			edit.commit();
		} else {
			mEdittext.setEnabled(true);
			mEdittext.setVisibility(View.VISIBLE);
			mEdittext.requestFocus();
			mEdittext.setText(mTextView.getText());
			btn.setText(R.string.finish_edit);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			mTextView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (PHOTO_REQUEST_CUT == requestCode) {
			if(null != CropImage.getImageUri()){
				String url = CropImage.getImageUri().toString();
		        mImageLoader.displayImage(url, mBackgroundImageView, mOptions);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume() {
		keepScreen(true);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		keepScreen(false);
		super.onPause();
	}
	
	private void keepScreen(boolean keepScreen){
		if (keepScreen) {	//保持屏幕亮
			if (!mScreenLock.isHeld()) {
				mScreenLock.acquire();
			}
		}else {				//不保持屏幕亮
			if (mScreenLock.isHeld()) {
				mScreenLock.release();
			}
		}
	}
}
