/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.aopujidian.launcher.slide;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

import com.aopujidian.launcher.R;
import com.aopujidian.launcher.slide.ImageGridActivity.Extra;
import com.aopujidian.launcher.utils.PrefsConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends BaseActivity {

	private static final String STATE_POSITION = "STATE_POSITION";

	private static final String TAG = "ImagePagerActivity";
	
	private WakeLock mScreenLock;

	private DisplayImageOptions mOptions;

	private int mInterval;
	
	private String[] imageUrls;
    
	private int index;
	
	@ViewInject(R.id.time_spinner)
	private Spinner mSpinner;
	
	@ViewInject(R.id.switcher)
	private ImageSwitcher mSwitcher;
	
	private static final int MSG_SHOW_NEXT = 100;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SHOW_NEXT:
				showNext();
				break;
			}
		};
	};
	
	private void stopShow(){
		mHandler.removeMessages(MSG_SHOW_NEXT);
	}
	
    protected void showNext() {
    	Log.e(TAG, "show Next: " + mInterval);
    	mHandler.removeMessages(MSG_SHOW_NEXT);
    	mHandler.sendEmptyMessageDelayed(MSG_SHOW_NEXT, mInterval);
    	
    	index = index % imageUrls.length;
    	
    	mImageLoader.loadImage(imageUrls[index], mOptions, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
					case IO_ERROR:
						message = "Input/Output error";
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
				}
				Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				ImageView image = (ImageView)mSwitcher.getNextView();
		        image.setImageBitmap(loadedImage);
		        mSwitcher.showNext();
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				
			}
		});
    	
    	index++;
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_pager);
		ViewUtils.inject(this);
		mScreenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,TAG);
		
		mInterval = getSharedPreferences(PrefsConfig.PREFS_NAME, Context.MODE_PRIVATE).getInt(PrefsConfig.PREFS_KEY_INTERVAL, PrefsConfig.PREFS_INTERVAL_DEFAULT_VALUE);
		
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
//		STRING[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		imageUrls = bundle.getStringArray(Extra.IMAGES);
		for (int i = 0; i < imageUrls.length; i++) {
			Log.e(TAG, "imageUrls[ " + i + " ] = " + imageUrls[i]);
		}

		mOptions = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.image_loading)
			.showImageOnFail(R.drawable.image_loading)
			.resetViewBeforeLoading(true)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new SimpleBitmapDisplayer())
			.build();
		
		mSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView i = new ImageView(ImagePagerActivity.this);
		        i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,
		                LayoutParams.MATCH_PARENT));
		        return i;
			}
		});

		Animations.setAnimationDuration(mInterval);
        mSwitcher.setInAnimation(Animations.inAnimation(getApplicationContext()));
        mSwitcher.setOutAnimation(Animations.outAnimation(getApplicationContext()));

		mSpinner = (Spinner) findViewById(R.id.time_spinner);
		final int[] time_inter_values = getResources().getIntArray(R.array.time_interval_value);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.time_interval, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        
        int i = 0;
        for (; i < time_inter_values.length; i++) {
        	int value = time_inter_values[i];
			if (value == mInterval) {
				break;
			}
		}
        
        mSpinner.setSelection(i);
        mSpinner.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                    	int value = time_inter_values[position];
                    	mInterval = value;
                    	Animations.setAnimationDuration(mInterval);
//                        showToast("Spinner1: position=" + position + " value=" + value);
                        Editor edit = getSharedPreferences(PrefsConfig.PREFS_NAME, Context.MODE_PRIVATE).edit();
            			edit.putInt(PrefsConfig.PREFS_KEY_INTERVAL, mInterval);
            			edit.commit();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        showToast("Spinner1: unselected");
                    }
                });
	}
	
	void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

	
	@OnClick(R.id.btn_exit)
	public void exit(View view) {
		finish();
	}
	
	@Override
	protected void onResume() {
		showNext();
		keepScreen(true);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		keepScreen(false);
		stopShow();
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