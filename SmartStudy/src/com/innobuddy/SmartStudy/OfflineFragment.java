package com.innobuddy.SmartStudy;


import org.json.JSONException;
import org.json.JSONObject;

import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.SmartStudy.Video.VideoPlayerActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class OfflineFragment extends Fragment {
	
	FinishReceiver mFinishReceiver;
	CourseCell2Adapter adapter;

	public OfflineFragment() {
		// Required empty public constructor
	}

	@Override
	public void onDestroy() {
		
		if (adapter.cursor != null) {
			adapter.cursor.close();
			adapter.cursor = null;
		}
				
		if (mFinishReceiver != null) {
	        getActivity().unregisterReceiver(mFinishReceiver);
		}
		
		super.onDestroy();
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		View view = inflater.inflate(R.layout.fragment_offline, container, false);
		ListView listView = (ListView)view.findViewById(R.id.listView1);
				
		Cursor cursor = DBHelper.getInstance(null).queryOffline();

		adapter = new CourseCell2Adapter(getActivity(), cursor);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				
				Cursor cursor = adapter.cursor;
				cursor.moveToPosition(position);
				
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("id", cursor.getInt(cursor.getColumnIndex("id")));
					jsonObject.put("name", cursor.getString(cursor.getColumnIndex("name")));
					jsonObject.put("poster", cursor.getString(cursor.getColumnIndex("poster")));
					jsonObject.put("url", cursor.getString(cursor.getColumnIndex("url")));
					jsonObject.put("cache_url", cursor.getString(cursor.getColumnIndex("cache_url")));
					jsonObject.put("hot", cursor.getInt(cursor.getColumnIndex("hot")));
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				Intent intent = new Intent();
				intent.putExtra("json", jsonObject.toString());
				intent.setClass(getActivity(), VideoPlayerActivity.class);
				startActivity(intent);
				
			}
		});
		
		if (mFinishReceiver == null) {
	        mFinishReceiver = new FinishReceiver();
	        IntentFilter filter = new IntentFilter();
	        filter.addAction("downloadFinished");
	        getActivity().registerReceiver(mFinishReceiver, filter);
		}
		
		return view;
	}
	
	public class FinishReceiver extends BroadcastReceiver {
		
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("downloadFinished")) {
        		if (adapter.cursor != null) {
        			adapter.cursor.close();
        			adapter.cursor = null;
        		}

        		adapter.cursor = DBHelper.getInstance(null).queryOffline();
            	adapter.notifyDataSetChanged();
            }
        }
		
	}

}
