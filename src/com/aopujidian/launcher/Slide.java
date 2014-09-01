package com.aopujidian.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aopujidian.launcher.utils.BitmapHelp;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

public class Slide extends Activity {
	private static final String TAG = "Slide";
	public static BitmapUtils bitmapUtils;
	@ViewInject(R.id.grid_view)
    private GridView imageGridView;
	
    private ImageListAdapter imageListAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slide_activity);
        ViewUtils.inject(this); //注入view和事件
        bitmapUtils = BitmapHelp.getBitmapUtils(getApplicationContext());
        bitmapUtils.configDefaultLoadingImage(R.drawable.ic_launcher);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);
        bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
        bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(getApplicationContext()).scaleDown(3));
        
        imageListAdapter = new ImageListAdapter(getApplicationContext());
        imageGridView.setAdapter(imageListAdapter);
		new LoadThemeTask().execute();
	}

	@OnClick(R.id.btn_exit)
	public void exit(View view) {
		finish();
	}

	private class LoadThemeTask extends AsyncTask<Void, Integer, Void> {

		private List<String> mInternalThemePaths = new ArrayList<String>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mInternalThemePaths.clear();
		}

		@Override
		protected void onPostExecute(Void result) {
			imageListAdapter.addSrc(mInternalThemePaths);
			imageListAdapter.notifyDataSetChanged();// 通知listview更新数据
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// /data/data/hk.com.dycx.lebao.systemservice/files/theme

			File file = new File("mnt/sdcard/theme");
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
					String suffix = absolutePath.substring(absolutePath.indexOf(".") + 1);
					suffix = suffix.toLowerCase();
					if (filterMedia(suffix)) {
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
	
	@OnItemClick(R.id.grid_view)
    public void onImageItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.e(TAG, "onImageItemClick");
	}
	
	private class ImageListAdapter extends BaseAdapter {

        private Context mContext;
        private final LayoutInflater mInflater;
        private ArrayList<String> imgSrcList;

        public ImageListAdapter(Context context) {
            super();
            this.mContext = context;
            mInflater = LayoutInflater.from(context);
            imgSrcList = new ArrayList<String>();
        }

        public void addSrc(List<String> imgSrcList) {
            this.imgSrcList.addAll(imgSrcList);
        }

        public void addSrc(String imgUrl) {
            this.imgSrcList.add(imgUrl);
        }

        @Override
        public int getCount() {
            return imgSrcList.size();
        }

        @Override
        public Object getItem(int position) {
            return imgSrcList.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ImageItemHolder holder = null;
            if (view == null) {
                view = mInflater.inflate(R.layout.bitmap_item, null);
                holder = new ImageItemHolder();
                ViewUtils.inject(holder, view);
                view.setTag(holder);
            } else {
                holder = (ImageItemHolder) view.getTag();
            }
            holder.imgPb.setProgress(0);
            bitmapUtils.display(holder.imgItem, imgSrcList.get(position), new CustomBitmapLoadCallBack(holder));
            return view;
        }
    }
	
    private class ImageItemHolder {
        @ViewInject(R.id.img_item)
        private ImageView imgItem;

        @ViewInject(R.id.img_pb)
        private ProgressBar imgPb;
    }
    
    public class CustomBitmapLoadCallBack extends DefaultBitmapLoadCallBack<ImageView> {
        private final ImageItemHolder holder;

        public CustomBitmapLoadCallBack(ImageItemHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onLoading(ImageView container, String uri, BitmapDisplayConfig config, long total, long current) {
            this.holder.imgPb.setProgress((int) (current * 100 / total));
        }

        @Override
        public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
            //super.onLoadCompleted(container, uri, bitmap, config, from);
            fadeInDisplay(container, bitmap);
            this.holder.imgPb.setProgress(100);
        }
        
        @Override
        public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
        	Log.e("zhangping", "onLoadFailed");
        	super.onLoadFailed(container, uri, drawable);
        }
    }
    
    private static final ColorDrawable TRANSPARENT_DRAWABLE = new ColorDrawable(android.R.color.transparent);
    
    private void fadeInDisplay(ImageView imageView, Bitmap bitmap) {
        final TransitionDrawable transitionDrawable =
                new TransitionDrawable(new Drawable[]{
                        TRANSPARENT_DRAWABLE,
                        new BitmapDrawable(imageView.getResources(), bitmap)
                });
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(500);
    }
}
