package com.innobuddy.SmartStudy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.fragment.MainActivity;
import com.innobuddy.SmartStudy.utils.VersionUtils;

public class SplashActivity extends Activity {
	private static final int ENTERHOMECODE = 100;
	private TextView mTvVersion;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ENTERHOMECODE:
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mTvVersion = (TextView) findViewById(R.id.tv_version);
		String version=VersionUtils.getAppVersion(this);
		mTvVersion.setText(version);
		handler.sendEmptyMessageDelayed(ENTERHOMECODE, 2000);
		
		//SystemClock.uptimeMillis();
		//自从开机所用的时间
	}

}
