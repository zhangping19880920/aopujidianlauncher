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
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.aopujidian.launcher.R;
import com.aopujidian.launcher.slide.ImageGridActivity.Extra;
import com.aopujidian.launcher.utils.PrefsConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends BaseActivity {

	private static final String STATE_POSITION = "STATE_POSITION";

	private static final String TAG = "ImagePagerActivity";
	
	private WakeLock mScreenLock;

	private DisplayImageOptions mOptions;

	private ViewPager mPager;
	
	private int mInterval;
	
	@ViewInject(R.id.time_spinner)
	private Spinner mSpinner;
	
	private static final int MSG_SHOW_NEXT = 100;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SHOW_NEXT:
				
				break;
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_pager);
		ViewUtils.inject(this);
		mScreenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,TAG);
		
		mInterval = getSharedPreferences(PrefsConfig.PREFS_NAME, Context.MODE_PRIVATE).getInt(PrefsConfig.PREFS_KEY_INTERVAL, PrefsConfig.PREFS_INTERVAL_DEFAULT_VALUE);
		
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		String[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		for (int i = 0; i < imageUrls.length; i++) {
			Log.e(TAG, "imageUrls[ " + i + " ] = " + imageUrls[i]);
		}

		mOptions = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.image_loading)
			.showImageOnFail(R.drawable.image_loading)
			.resetViewBeforeLoading(true)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(500))
			.build();

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(new ImagePagerAdapter(imageUrls));
		
		
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
//                        showToast("Spinner1: position=" + position + " value=" + value);
                        Editor edit = getSharedPreferences(PrefsConfig.PREFS_NAME, Context.MODE_PRIVATE).edit();
            			edit.putInt(PrefsConfig.PREFS_KEY_INTERVAL, mInterval);
            			edit.commit();
            			
            			//TODO重新记时
            			mHandler.removeMessages(MSG_SHOW_NEXT);
            			mHandler.sendEmptyMessage(MSG_SHOW_NEXT);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    	
                        showToast("Spinner1: unselected");
                    }
                });
	}
	
    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}
	
	@OnClick(R.id.btn_exit)
	public void exit(View view) {
		finish();
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			mImageLoader.displayImage(images[position], imageView, mOptions, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
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

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
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