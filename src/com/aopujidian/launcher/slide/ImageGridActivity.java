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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aopujidian.launcher.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageGridActivity extends AbsListViewBaseActivity {
	
	public static class Extra {
		public static final String IMAGES = "IMAGES";
		public static final String IMAGE_POSITION = "IMAGE_POSITION";
	}

	protected static final String TAG = "ImageGridActivity";

	private String[] mImageUrls = new String[]{};
	
	private Set<Integer> mSelectedPosition = new HashSet<Integer>();

	DisplayImageOptions mOptions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);
		ViewUtils.inject(this);
		
		mOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.image_loading)
		.showImageForEmptyUri(R.drawable.image_loading)
		.showImageOnFail(R.drawable.image_loading)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
		mGridView = (GridView) findViewById(R.id.gridview);
		((GridView) mGridView).setAdapter(new ImageAdapter());
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ViewHolder holder = (ViewHolder) view.getTag();
				if (mSelectedPosition.contains(position)) {
					mSelectedPosition.remove(position);
					holder.indication.setVisibility(View.GONE);
				} else {
					mSelectedPosition.add(position);
					holder.indication.setVisibility(View.VISIBLE);
				}
			}
		});
		
		new LoadImageTask(getApplicationContext(), new LoadImageTask.LoadImageListener() {
			@Override
			public void onLoadImageListener(String[] result) {
				mImageUrls = result;
				if (null != mGridView) {
					((ImageAdapter)mGridView.getAdapter()).notifyDataSetChanged();
				}
			}
		}).execute();
	}
	
	@OnClick(R.id.btn_exit)
	public void exit(View view) {
		finish();
	}

	@OnClick(R.id.btn_start_slide)
	public void startImagePagerActivity(View view) {
		//TODO
		List<String> selectUrls = new ArrayList<String>();
		Iterator<Integer> iterator = mSelectedPosition.iterator();
		while (iterator.hasNext()) {
			Integer integer = (Integer) iterator.next();
			selectUrls.add(mImageUrls[integer]);
		}
		String[] urls = selectUrls.toArray(new String[0]);
		if (null == urls || 0 == urls.length) {
			Toast.makeText(getApplicationContext(), R.string.no_selected_image, Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, urls);
		startActivity(intent);
	}

	static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
		ImageView indication;
	}

	public class ImageAdapter extends BaseAdapter {
		private static final String TAG = "ImageAdapter";

		@Override
		public int getCount() {
			return mImageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				holder.indication = (ImageView) view.findViewById(R.id.indication);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			if (mSelectedPosition.contains(position)) {
				holder.indication.setVisibility(View.VISIBLE);
			} else {
				holder.indication.setVisibility(View.GONE);
			}

			mImageLoader.displayImage(mImageUrls[position], holder.imageView, mOptions, new SimpleImageLoadingListener() {
										 @Override
										 public void onLoadingStarted(String imageUri, View view) {
											 holder.progressBar.setProgress(0);
											 holder.progressBar.setVisibility(View.VISIBLE);
										 }

										 @Override
										 public void onLoadingFailed(String imageUri, View view,
												 FailReason failReason) {
											 holder.progressBar.setVisibility(View.GONE);
										 }

										 @Override
										 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
											 holder.progressBar.setVisibility(View.GONE);
										 }
									 }, new ImageLoadingProgressListener() {
										 @Override
										 public void onProgressUpdate(String imageUri, View view, int current,
												 int total) {
											 holder.progressBar.setProgress(Math.round(100.0f * current / total));
										 }
									 }
			);

			return view;
		}
	}
}