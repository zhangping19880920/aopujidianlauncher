package com.aopujidian.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Parking extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slide_activity);
	}
	
	public void exit(View view) {
		finish();
	}
}
