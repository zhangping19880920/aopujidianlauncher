package com.aopujidian.launcher;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Video.VideoColumns;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    
    private static final String ANDROID_LAUNCHER_PACKAGE = "com.android.launcher";

    public static final String DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
    public static final String DIRECTORY = DCIM + "/Camera";

    // Match the code in MediaProvider.computeBucketValues().
    public static final String BUCKET_ID = String.valueOf(DIRECTORY.toLowerCase().hashCode());
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        WindowManager windowManager = getWindowManager();  
        Display display = windowManager.getDefaultDisplay();  
        int screenWidth = screenWidth = display.getWidth();  
        int screenHeight = screenHeight = display.getHeight();  
        
        Log.e(TAG, "screenWidth = " + screenWidth + " ,screenHeight = " + screenHeight);
    }
	
    private static class Media {
        public Media(long id, int orientation, long dateTaken, Uri uri) {
            this.id = id;
            this.orientation = orientation;
            this.dateTaken = dateTaken;
            this.uri = uri;
        }

        public final long id;
        public final int orientation;
        public final long dateTaken;
        public final Uri uri;
    }

    public static Media getLastImageThumbnail(ContentResolver resolver) {
        Uri baseUri = Images.Media.EXTERNAL_CONTENT_URI;

        Uri query = baseUri.buildUpon().appendQueryParameter("limit", "1").build();
        String[] projection = new String[] {ImageColumns._ID, ImageColumns.ORIENTATION,
                ImageColumns.DATE_TAKEN};
        String selection = ImageColumns.MIME_TYPE + "='image/jpeg' AND " +
                ImageColumns.BUCKET_ID + '=' + BUCKET_ID;
        String order = ImageColumns.DATE_TAKEN + " DESC," + ImageColumns._ID + " DESC";

        Cursor cursor = null;
        try {
            cursor = resolver.query(query, projection, selection, null, order);
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(0);
                return new Media(id, cursor.getInt(1), cursor.getLong(2),
                        ContentUris.withAppendedId(baseUri, id));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static Media getLastVideoThumbnail(ContentResolver resolver) {
        Uri baseUri = Video.Media.EXTERNAL_CONTENT_URI;

        Uri query = baseUri.buildUpon().appendQueryParameter("limit", "1").build();
        String[] projection = new String[] {VideoColumns._ID, MediaColumns.DATA,
                VideoColumns.DATE_TAKEN};
        String selection = VideoColumns.BUCKET_ID + '=' + BUCKET_ID;
        String order = VideoColumns.DATE_TAKEN + " DESC," + VideoColumns._ID + " DESC";

        Cursor cursor = null;
        try {
            cursor = resolver.query(query, projection, selection, null, order);
            if (cursor != null && cursor.moveToFirst()) {
                Log.d(TAG, "getLastVideoThumbnail: " + cursor.getString(1));
                long id = cursor.getLong(0);
                return new Media(id, 0, cursor.getLong(2),
                        ContentUris.withAppendedId(baseUri, id));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
	
    public static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }
    
    public static boolean isUriValid(Uri uri, ContentResolver resolver) {
        if (uri == null) return false;

        try {
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
            if (pfd == null) {
                Log.e(TAG, "Fail to open URI. URI=" + uri);
                return false;
            }
            pfd.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
    public static Uri getLastThumbnail(ContentResolver resolver) {
        Media image = getLastImageThumbnail(resolver);
        Media video = getLastVideoThumbnail(resolver);
        if (image == null && video == null) return null;

        Bitmap bitmap = null;
        Media lastMedia;
        // If there is only image or video, get its thumbnail. If both exist,
        // get the thumbnail of the one that is newer.
        if (image != null && (video == null || image.dateTaken >= video.dateTaken)) {
            bitmap = Images.Thumbnails.getThumbnail(resolver, image.id,
                    Images.Thumbnails.MINI_KIND, null);
            lastMedia = image;
        } else {
            bitmap = Video.Thumbnails.getThumbnail(resolver, video.id,
                    Video.Thumbnails.MINI_KIND, null);
            lastMedia = video;
        }

        // Ensure database and storage are in sync.
        if (isUriValid(lastMedia.uri, resolver)) {
        	Log.e(TAG, "lastMedia.uri = " + lastMedia.uri);
        	return lastMedia.uri;
        }
        return null;
    }
	
    public static void loadFrom(File file) {
    	Log.e(TAG, "loadFrom = " + file.getAbsolutePath());
        Uri uri = null;
        Bitmap bitmap = null;
        FileInputStream f = null;
        BufferedInputStream b = null;
        DataInputStream d = null;
            try {
                f = new FileInputStream(file);
                b = new BufferedInputStream(f, 4096);
                d = new DataInputStream(b);
                uri = Uri.parse(d.readUTF());
                Log.e(TAG, "uri = " + uri.toString());
                bitmap = BitmapFactory.decodeStream(d);
                d.close();
            } catch (IOException e) {
                Log.i(TAG, "Fail to load bitmap. " + e);
            } finally {
                closeSilently(f);
                closeSilently(b);
                closeSilently(d);
            }
    }

    public void onClick (View view) {
    	switch (view.getId()) {
		case R.id.ib_top_first:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.top_first), Toast.LENGTH_SHORT).show();
			break;
		case R.id.ib_top_second:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.top_second), Toast.LENGTH_SHORT).show();
			break;
		case R.id.ib_top_third:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.top_third), Toast.LENGTH_SHORT).show();
			//TODO 进入电子屏幕
//			goActivity(Slide.class);
//			loadFrom(new File(getFilesDir(), "last_thumb"));
			Uri lastThumbnail = getLastThumbnail(getContentResolver());
			startActivity(new Intent("com.android.camera.action.REVIEW", lastThumbnail));
//	        startActivity(intent);
			break;
		case R.id.ib_top_fourth:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.top_fourth) + "进入android 桌面", Toast.LENGTH_SHORT).show();
			//TODO 进入android桌面
			goLauncher();
			break;
		case R.id.ib_bottom_first:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.buttom_first), Toast.LENGTH_SHORT).show();
			break;
		case R.id.ib_bottom_second:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.buttom_second), Toast.LENGTH_SHORT).show();
			//TODO 提示停车
			goActivity(Parking.class);
			break;
		case R.id.ib_bottom_third:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.buttom_third), Toast.LENGTH_SHORT).show();
			break;
		case R.id.ib_bottom_fourth:
			Toast.makeText(getApplicationContext(), "点击了: " + getString(R.string.buttom_fourth), Toast.LENGTH_SHORT).show();
			break;
		}
    }
    
    /** 启动activity */
    private void goActivity(Class<?> cls){
    	Intent intent = new Intent(getApplicationContext(), cls);
    	try {
    		startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getApplicationContext(), R.string.do_not_start_launcher, 0).show();
		}
    }
    
    /** 启动android桌面 */
    private void goLauncher(){
    	Intent intent = new Intent();
		intent.setPackage(ANDROID_LAUNCHER_PACKAGE);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			String msg = getString(R.string.do_not_start_launcher) + getString(R.string.top_fourth);
			Toast.makeText(getApplicationContext(), msg, 0).show();
		}
    }
}
