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
package com.aopujidian.launcher.utils;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class MyApplication extends Application {
	protected static final String TAG = "MyApplication";

	private static MyApplication mInstance;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}
		super.onCreate();
		mInstance = this;
		initImageLoader(getApplicationContext());
	}
	
	public static MyApplication getInstance(){
		return mInstance;
	}

	public static void initImageLoader(Context context) {
		final int maxWidth = 800;
		final int maxHeight = 1280;
		final int MB = 1024 * 1024; 
//		File cacheDir = StorageUtils.getOwnCacheDirectory(context, ".imageloader/.Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheExtraOptions(maxWidth, maxHeight) // max width, max height，即保存的每个缓存文件的最大长宽
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(3)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheExtraOptions(maxWidth, maxHeight, new BitmapProcessor() {
					
					@Override
					public Bitmap process(Bitmap bitmap) {
						if (null != bitmap && (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight)) {
							return bitmap;
						}
						return null;
					}
				})
//				.diskCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径 
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(40 * MB) // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCache(new UsingFreqLimitedMemoryCache(5 * MB))
				.memoryCacheSize(5 * MB)
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
	}
	
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
}