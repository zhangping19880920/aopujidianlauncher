package com.aopujidian.launcher;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aopujidian.launcher.dialog.ColorPickerDialog.OnColorChangedListener;
import com.aopujidian.launcher.dialog.DialogAction;
import com.aopujidian.launcher.dialog.DialogAction.OnClickListener;
import com.aopujidian.launcher.dialog.ListFragmentDialog;
import com.aopujidian.launcher.dialog.ColorPickerDialog;
import com.aopujidian.launcher.dialog.SeekBarDialog;
import com.aopujidian.launcher.dialog.SeekBarDialog.OnSeekBarProgressChangeListener;
import com.aopujidian.launcher.slide.BaseActivity;
import com.aopujidian.launcher.utils.CropImage;
import com.aopujidian.launcher.utils.ExternalStorageUtil;
import com.aopujidian.launcher.utils.PrefsConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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
		.showImageOnLoading(R.drawable.action_background)
		.showImageForEmptyUri(R.drawable.parking_background)
		.showImageOnFail(R.drawable.parking_background)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
		boolean isSetBackground = getSharedPreferences(PrefsConfig.PREFS_NAME, Context.MODE_PRIVATE).getBoolean(PrefsConfig.PREFS_KEY_PARKING_BACKGROUND, false);
		if (isSetBackground) {
			reallySetBackground();
		}
	}

	@OnClick(R.id.btn_exit)
	public void exit(View view) {
		finish();
	}

	@OnClick(R.id.more)
	public void more(View view){
		List<DialogAction> actions = new ArrayList<DialogAction>();
		DialogAction setBackground = new DialogAction(getString(R.string.set_background), new OnClickListener() {
			@Override
			public void onClick(View view) {	//set background
				setBackground();
			}
		});
		
		DialogAction setDefaultBackground = new DialogAction(getString(R.string.set_default_background), new OnClickListener() {
			@Override
			public void onClick(View view) {
				setDefaultBackground();
			}
		});
		
		DialogAction setSize = new DialogAction(getString(R.string.set_text_size), new OnClickListener() {
			@Override
			public void onClick(View view) {
				new SeekBarDialog(Parking.this, new OnSeekBarProgressChangeListener() {
					@Override
					public void onSeekBarProgressChange(int progress) {
						Log.e(TAG, "onSeekBarProgressChange: " + progress);
					}
				}, 0).show();
			}
		});
		
		DialogAction setColor = new DialogAction(getString(R.string.set_text_color), new OnClickListener() {
			@Override
			public void onClick(View view) {
				int currentTextColor = mTextView.getCurrentTextColor();
				new ColorPickerDialog(Parking.this, new OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						mTextView.setTextColor(color);
					}
				}, currentTextColor).show();
			}
		});
		
		actions.add(setBackground);
		actions.add(setDefaultBackground);
		actions.add(setSize);
		actions.add(setColor);
		ListFragmentDialog fragmentDialog = new ListFragmentDialog(actions);
		fragmentDialog.showDialog(getFragmentManager());
	}
	
	protected void setDefaultBackground() {
		Editor edit = getSharedPreferences(PrefsConfig.PREFS_NAME, Context.MODE_PRIVATE).edit();
		edit.putBoolean(PrefsConfig.PREFS_KEY_PARKING_BACKGROUND, false);
		edit.commit();
		mBackgroundImageView.setImageResource(R.drawable.parking_background);
	}

	public void setBackground() {
		boolean isMount = ExternalStorageUtil.isMount(getApplicationContext());
		if (!isMount) {
			return;
		}
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
		if (PHOTO_REQUEST_CUT == requestCode && RESULT_OK == resultCode && null != data) {
			reallySetBackground();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void reallySetBackground() {
		if(null != CropImage.getImageUri()){
			String url = CropImage.getImageUri().toString();
		    mImageLoader.displayImage(url, mBackgroundImageView, mOptions, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					
				}
			});
		    
		    Editor edit = getSharedPreferences(PrefsConfig.PREFS_NAME, Context.MODE_PRIVATE).edit();
		    edit.putBoolean(PrefsConfig.PREFS_KEY_PARKING_BACKGROUND, true);
		    edit.commit();
		}
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
		if (keepScreen) {	//keep screen on
			if (!mScreenLock.isHeld()) {
				mScreenLock.acquire();
			}
		}else {				//keep screen off
			if (mScreenLock.isHeld()) {
				mScreenLock.release();
			}
		}
	}
}
