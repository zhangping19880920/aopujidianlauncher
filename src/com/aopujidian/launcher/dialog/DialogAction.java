package com.aopujidian.launcher.dialog;

import android.view.View;

public class DialogAction {
	public static interface OnClickListener {
		public void onClick(View view);
	}
	private String mShowText;
	
	private OnClickListener mListener;

	public String getShowText() {
		return mShowText;
	}

	public void setShowText(String showText) {
		this.mShowText = showText;
	}

	public OnClickListener getListener() {
		return mListener;
	}

	public void setListener(OnClickListener mListener) {
		this.mListener = mListener;
	}

	public DialogAction(String showText, OnClickListener listener) {
		super();
		mShowText = showText;
		mListener = listener;
	}
}
