package com.aeye.helibao;

import com.aeye.helibaolocal.R;
import com.aeye.widget.SettingData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity {
	private Button back;
	private EditText mEditText_password;
	private Button mButton_ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mEditText_password = (EditText)findViewById(R.id.password);
		mButton_ok = (Button)findViewById(R.id.ok);
		mButton_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String pwd = mEditText_password.getText().toString();
				String sPwd = SettingData.getInstance().getPwd();
				if(pwd !=null && !pwd.trim().equals("") && pwd.trim().equals(sPwd)){
				Intent intent = new Intent(LoginActivity.this,SettingActivity.class);
				startActivity(intent);
				}else{
					showShortToast("请输入正确的密码");
				}
			}
		});
	}
}
