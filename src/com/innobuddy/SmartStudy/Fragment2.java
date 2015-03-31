package com.innobuddy.SmartStudy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment2 extends Fragment {

    private String[] listTitle = {"离线观看", "我的收藏", "近期观看"};
    
    private int[] listImage = {R.drawable.mine_offline, R.drawable.mine_connect, R.drawable.mine_recent_watch};

    SimpleAdapter adapter = null;

    ArrayList<Map<String,Object>> arrayList = new ArrayList<Map<String,Object>>();
    
    View view = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		
		view = inflater.inflate(R.layout.fragment2, container, false);
		
		ListView listView = (ListView)view.findViewById(R.id.listView1);
		
		if (arrayList.size() == 0) {
			for	(int i = 0; i < listTitle.length; i++) {
			    Map<String,Object> item = new HashMap<String,Object>();
			    item.put("image", listImage[i]);
			    item.put("title", listTitle[i]);
			    arrayList.add(item);
			}
		}
		
		adapter = new SimpleAdapter(getActivity(), arrayList, R.layout.mine_cell, new String[]{"image", "title"}, new int[]{R.id.image, R.id.title});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapterView, View view, int position,
			    long id) {
		    	
		    	if (position == 0) {
		    		((MainActivity)getActivity()).mTabHost.setCurrentTab(2);
		    		((MainActivity)getActivity()).mTabRg.check(R.id.tab_rb_3);
		    	} else if (position == 1) {
					
					Intent intent = new Intent();
					intent.setClass(getActivity(), CollectActivity.class);
					startActivity(intent);

				} else if (position == 2) {
					
					Intent intent = new Intent();
					intent.setClass(getActivity(), RecentWatchActivity.class);
					startActivity(intent);

				}
		    	
			    }
	        });
		
		return view;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("MainScreen"); //统计页面
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("MainScreen"); 
	}
	
	
}
