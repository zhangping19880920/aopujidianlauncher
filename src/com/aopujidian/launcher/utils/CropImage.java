package com.aopujidian.launcher.utils;

import java.io.FileNotFoundException;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class CropImage {
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/.temp.jpg";
	
	private static final Uri IMAGE_URI = Uri.parse(IMAGE_FILE_LOCATION);
	
    public static Uri getImageUri() {
		return IMAGE_URI;
	}

	public static final String KEY_RETURN_DATA = "return-data";
    
    public static final String KEY_CROPPED_RECT = "cropped-rect";
    
    public static final String KEY_ASPECT_X = "aspectX";
    
    public static final String KEY_ASPECT_Y = "aspectY";
    
    public static final String KEY_SPOTLIGHT_X = "spotlightX";
    
    public static final String KEY_SPOTLIGHT_Y = "spotlightY";
    
    public static final String KEY_OUTPUT_X = "outputX";
    
    public static final String KEY_OUTPUT_Y = "outputY";
    
    public static final String KEY_SCALE = "scale";
    
    public static final String KEY_DATA = "data";
    
    public static final String KEY_SCALE_UP_IF_NEEDED = "scaleUpIfNeeded";
    
    public static final String KEY_OUTPUT_FORMAT = "outputFormat";
    
    public static final String KEY_SET_AS_WALLPAPER = "set-as-wallpaper";
    
    public static final String KEY_NO_FACE_DETECTION = "noFaceDetection";
    
    public static final String KEY_SHOW_WHEN_LOCKED = "showWhenLocked";
    
    public static Intent getCropIntent(int width, int height, float aspect){
    	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra(CropImage.KEY_ASPECT_X, aspect);
		intent.putExtra(CropImage.KEY_ASPECT_Y, 1);
		intent.putExtra(CropImage.KEY_OUTPUT_X, width);
        intent.putExtra(CropImage.KEY_OUTPUT_Y, height);
        intent.putExtra(CropImage.KEY_SCALE, true);
        intent.putExtra(KEY_OUTPUT_FORMAT, Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, IMAGE_URI);

        intent.putExtra(CropImage.KEY_SCALE_UP_IF_NEEDED, true);
        intent.putExtra(CropImage.KEY_NO_FACE_DETECTION, true);
		intent.putExtra(CropImage.KEY_RETURN_DATA, false);
		return intent;
    }
    
	public static Bitmap decodeUriAsBitmap(Uri uri, ContentResolver contentResolver) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}
