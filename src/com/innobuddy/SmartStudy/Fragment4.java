package com.innobuddy.SmartStudy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.umeng.analytics.MobclickAgent;

public class Fragment4 extends Fragment {

		
    View view = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment4, container, false);
		
		Switch switch1 = (Switch)view.findViewById(R.id.switch1);
		
		SharedPreferences settingPreferences = getActivity().getSharedPreferences(Utilitys.SETTING_INFOS, 0);

		boolean mobileHint = settingPreferences.getBoolean(Utilitys.MOBILE_HINT, true);
		
		switch1.setChecked(mobileHint);
		
		switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				SharedPreferences settingPreferences = getActivity().getSharedPreferences(Utilitys.SETTING_INFOS, 0);
				SharedPreferences.Editor editor = settingPreferences.edit();
				editor.putBoolean(Utilitys.MOBILE_HINT, isChecked);
				editor.commit();
				
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
