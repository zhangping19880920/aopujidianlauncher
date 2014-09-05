package com.aopujidian.launcher.dialog;

import java.util.List;

import com.aopujidian.launcher.R;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListFragmentDialog extends DialogFragment{
	private static final String TAG = "ListFragmentDialog";

	private List<DialogAction> mActions;
	public ListFragmentDialog (List<DialogAction> actions) {
		mActions = actions;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int style = DialogFragment.STYLE_NO_TITLE;
//		int theme = android.R.style.Theme_Translucent;
		setStyle(style, 0);
		super.onCreate(savedInstanceState);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.list_fragment_dialog, container, false);
        final ListView listView = (ListView) view.findViewById(R.id.lv);
        
        if (null != mActions && mActions.size() > 0) {
        	String[] items = new String[mActions.size()];
        	for (int i = 0; i < mActions.size(); i++) {
        		items[i] = mActions.get(i).getShowText();
			}
        	
        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_fragment_dialog_item, items);
        	listView.setAdapter(adapter);
        	listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Log.e(TAG, "onItemClick: " + position);
					if (position <= mActions.size() - 1) {
						DialogAction dialogAction = mActions.get(position);
						if (null != dialogAction && null != dialogAction.getListener()) {
							dialogAction.getListener().onClick(listView);
							ListFragmentDialog.this.dismiss();
						}
					}
				}
        		
			});
		}
        return view;
    }
	
	public void showDialog(FragmentManager fragmentManager) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(ListFragmentDialog.TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        
        show(ft, ListFragmentDialog.TAG);
        Log.e(TAG, "--------------show dialog");
	}
}
