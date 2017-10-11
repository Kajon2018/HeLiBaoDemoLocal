package com.aeye.helibao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.aeye.db.SqlManager;
import com.aeye.helibaolocal.R;

public class SettingActivity extends BaseActivity{
	private Button back,add_person,server,modify_pwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settting);
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		add_person = (Button) findViewById(R.id.add_person);
		add_person.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this,AddPersonActivity.class));
				
			}
		});
		server = (Button) findViewById(R.id.server);
		server.setText("清空数据库");
		server.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				startActivity(new Intent(SettingActivity.this,ServerSetActivity.class));
				SqlManager.get().clearFinger();
				showShortToast("清空数据库成功");
			}
		});
		modify_pwd = (Button) findViewById(R.id.modify_pwd);
		modify_pwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this,ModifyPwdActivity.class));
			}
		});
	}

}
