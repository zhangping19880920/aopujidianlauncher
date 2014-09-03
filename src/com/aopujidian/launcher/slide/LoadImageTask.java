package com.aopujidian.launcher.slide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class LoadImageTask extends AsyncTask<Void, Integer, Void> {
	
	public interface LoadImageListener {
		public void onLoadImageFinish(String[] result);
	}

	private List<String> mInternalThemePaths = new ArrayList<String>();
	private LoadImageListener mLoadImageListener;

	private Context mContext;
	
	private File mExternalStoragePath = Environment.getExternalStorageDirectory();

	public LoadImageTask(Context context, LoadImageListener listener) {
		mContext = context;
		mLoadImageListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mInternalThemePaths.clear();
	}

	@Override
	protected void onPostExecute(Void result) {
		if (null != mLoadImageListener) {
			String[] images = (String[]) mInternalThemePaths.toArray(new String[0]);
			mLoadImageListener.onLoadImageFinish(images);
		}
//		Toast.makeText(mContext, "load ok", Toast.LENGTH_SHORT).show();
		super.onPostExecute(result);
	}

	@Override
	protected Void doInBackground(Void... params) {
		// /data/data/hk.com.dycx.lebao.systemservice/files/theme
		File file = new File(mExternalStoragePath + "/slide");
		recursionLoad(file);
		return null;
	}

	private void recursionLoad(File loadFile) {
		File[] files = loadFile.listFiles();
		if (isCancelled() || files == null || files.length == 0) {
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				recursionLoad(file);
			} else {
				String absolutePath = file.getAbsolutePath();
				String suffix = absolutePath.substring(absolutePath
						.indexOf(".") + 1);
				suffix = suffix.toLowerCase();
				if (filterMedia(suffix)) {
//					Log.e("zhangping", "absolutePath1 = " + absolutePath);
					absolutePath = "file://" + absolutePath;
//					Log.e("zhangping", "absolutePath2 = " + absolutePath);
					mInternalThemePaths.add(absolutePath);
				}
			}
		}
	}

	private boolean filterMedia(String filePath) {
		if (filePath.endsWith("jpg") || filePath.endsWith("png")) {
			return true;
		}
		return false;
	}
}
