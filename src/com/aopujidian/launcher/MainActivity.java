package com.aopujidian.launcher;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick (View view) {
    	switch (view.getId()) {
		case R.id.ib_top_first:
			
			break;
		case R.id.ib_top_second:
			
			break;
		case R.id.ib_top_third:
			
			break;
		case R.id.ib_top_fourth:
			
			break;
		case R.id.ib_bottom_first:
			
			break;
		case R.id.ib_bottom_second:
			
			break;
		case R.id.ib_bottom_third:
			
			break;
		case R.id.ib_bottom_fourth:
			
			break;
		}
    }
}
