package com.aopujidian.launcher.dialog;


import com.aopujidian.launcher.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekBarDialog extends Dialog {
	public interface OnSeekBarProgressChangeListener{
		public void onSeekBarProgressChange(int progress);
	}

	private static final String TAG = "SeekBarDialog";
	
	private static final int BASE = 50;
	
	private static final int MAX = BASE + 50;
	
	private OnSeekBarProgressChangeListener mListener;
	private int mProgress;
	public SeekBarDialog(Context context, OnSeekBarProgressChangeListener listener, int progress){
		super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mListener = listener;
        mProgress = progress;
        Log.e(TAG, "mProgress: " + mProgress);
        mProgress -= BASE;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.seek_bar_dialog);
		SeekBar seekBar = (SeekBar) findViewById(R.id.sb);
		seekBar.setMax(MAX);
		seekBar.setProgress(mProgress);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (null != mListener) {
					mListener.onSeekBarProgressChange(BASE + progress);
				}
			}
		});
	}
}
