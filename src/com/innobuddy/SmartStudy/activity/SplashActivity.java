package com.innobuddy.SmartStudy.activity;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.R.layout;
import com.innobuddy.SmartStudy.fragment.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1000:
				Intent intent=new Intent();
				intent.setClass(SplashActivity.this,MainActivity.class);
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
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
					handler.sendEmptyMessage(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
