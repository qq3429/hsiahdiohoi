package com.innobuddy.SmartStudy;

import com.innobuddy.SmartStudy.global.GlobalParams;
import com.innobuddy.SmartStudy.utils.ValidateUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
	private EditText mEtLoginMobileNumber;
	private Button mBtnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mEtLoginMobileNumber = (EditText) findViewById(R.id.et_login_mobile_number);
		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mBtnLogin.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		String mobileNumber = mEtLoginMobileNumber.getText().toString().trim();
		boolean isNumber = ValidateUtil.isMobile(mobileNumber);
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_login:
			
			if (!isNumber) {
				Toast.makeText(this, "电话号码格式不正确", Toast.LENGTH_LONG).show();
				return;
			} else {
				// 校验通过
				GlobalParams.isLogin = true;
			}

			break;

		default:
			break;
		}
	}

}
