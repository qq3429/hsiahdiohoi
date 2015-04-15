package com.innobuddy.SmartStudy;

import java.util.Timer;
import java.util.TimerTask;

import com.innobuddy.SmartStudy.utils.ValidateUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {
	private Button mBtnObtionCode;
	private Timer timer;
	private TimerTask task;
	private int time = 60;
	private EditText mEtMobileNumber;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 60:
				time = time - 1;
				mBtnObtionCode.setText("倒计时"+time + "");
				if(time==0)
				{	mBtnObtionCode.setText("倒计时");
					mBtnObtionCode.setEnabled(true);
					cancelCountDownTask();
					time=60;
				}
				break;

			default:
				break;
			}
		};
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mBtnObtionCode = (Button) findViewById(R.id.btn_obtain_code);
		mEtMobileNumber = (EditText) findViewById(R.id.et_reg_mobile_number);
		mBtnObtionCode.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		String mobileNumber=mEtMobileNumber.getText().toString().trim();
		boolean isNumber = ValidateUtil.isMobile(mobileNumber);
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_obtain_code:
			if (TextUtils.isEmpty(mobileNumber)) {
				Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (!isNumber) {
				Toast.makeText(this, "电话号码格式不正确", Toast.LENGTH_LONG).show();
				return;
			}
			timer = new Timer();
			task = new TimerTask() {

				@Override
				public void run() {
					// --发送给handler
					Message msg = Message.obtain();
					msg.what = 60;
					handler.sendMessage(msg);
				}

			};
			timer.schedule(task, 0, 1000);
			// --每一秒钟无延迟运行一次
			mBtnObtionCode.setEnabled(false);// 不可被点击
			mEtMobileNumber.setEnabled(false);
			break;

		default:
			break;
		}
	}

	/**
	 * 取消倒计时任务
	 */
	private void cancelCountDownTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (task != null) {
			task.cancel();
			task = null;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelCountDownTask();
	}

}
