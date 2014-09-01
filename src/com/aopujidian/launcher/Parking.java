package com.aopujidian.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Parking extends Activity {
	
	private static final String TAG = "Parking";
	
	private static final String PREFS_NAME = "config";
	
	private static final String PREFS_KEY_PARKING = "parking_text";
	
	@ViewInject(R.id.iv_background)
	private ImageView mBackgroundImageView;
	
	@ViewInject(R.id.et_text)
	private EditText mEdittext;
	
	@ViewInject(R.id.tv_text)
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parking_activity);
		ViewUtils.inject(this);
		
		String latText = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(PREFS_KEY_PARKING, "");
		if (null != latText && !TextUtils.isEmpty(latText)) {
			mTextView.setText(latText);
		}
	}

	@OnClick(R.id.btn_exit)
	public void exit(View view) {
		finish();
	}

	@OnClick(R.id.btn_set_background)
	public void setBackground(View view) {
		Log.e(TAG, "setBackground");

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1.5);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, 0);
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
			Editor edit = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
			edit.putString(PREFS_KEY_PARKING, mTextView.getText().toString());
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
		if (null != data) {
			Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
			if (null != cameraBitmap) {
				mBackgroundImageView.setImageBitmap(cameraBitmap);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
