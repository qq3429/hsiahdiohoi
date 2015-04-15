package com.innobuddy.SmartStudy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.innobuddy.SmartStudy.global.GlobalParams;
import com.innobuddy.SmartStudy.utils.ValidateUtil;

public class LoginActivity extends BaseActivity {
	private EditText mEtLoginMobileNumber;
	private Button mBtnLogin;
	private EditText mEtLoginPsw;
	private TextView mTvRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mEtLoginMobileNumber = (EditText) findViewById(R.id.et_login_mobile_number);
		mEtLoginPsw = (EditText) findViewById(R.id.et_login_psw);

		mTvRegister = (TextView) findViewById(R.id.tv_register);

		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mBtnLogin.setOnClickListener(this);
		mTvRegister.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		String mobileNumber = mEtLoginMobileNumber.getText().toString().trim();
		String password = mEtLoginPsw.getText().toString().trim();
		boolean isNumber = ValidateUtil.isMobile(mobileNumber);

		switch (v.getId()) {
		case R.id.btn_login:

			if (TextUtils.isEmpty(mobileNumber)) {
				Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (TextUtils.isEmpty(password)) {
				Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (!isNumber) {
				Toast.makeText(this, "电话号码格式不正确", Toast.LENGTH_LONG).show();
				return;
			}
			// 校验通过
			GlobalParams.isLogin = true;
			finish();// 关闭当前界面

			break;
		case R.id.tv_register:
			Toast.makeText(this,  "注册", Toast.LENGTH_LONG).show();
			Intent intent=new Intent();
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			
			break;

		default:
			break;
		}
	}

}
