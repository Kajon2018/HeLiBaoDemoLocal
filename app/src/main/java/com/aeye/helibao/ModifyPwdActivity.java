package com.aeye.helibao;

import com.aeye.helibaolocal.R;
import com.aeye.widget.SettingData;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ModifyPwdActivity extends BaseActivity {
	private Button back,ok;
	private EditText old_pwd,new_pwd,new_pwd_again;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		old_pwd = (EditText) findViewById(R.id.old_pwd);
		new_pwd = (EditText) findViewById(R.id.new_pwd);
		new_pwd_again = (EditText) findViewById(R.id.new_pwd_again);
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String oldPwd = old_pwd.getText().toString();
				String newPwd = new_pwd.getText().toString();
				String newPwdAgain = new_pwd_again.getText().toString();
				if(oldPwd == null || oldPwd.trim().equals("")){
					showShortToast("请输入旧密码");
					return;
				}
				if(!oldPwd.equals(SettingData.getInstance().getPwd())){
					showShortToast("请输入正确的旧密码");
					return;
				}
				if(newPwd == null || newPwd.trim().equals("")){
					showShortToast("请输入新密码");
					return;
				}
				if(newPwdAgain == null || newPwdAgain.trim().equals("")){
					showShortToast("请再次输入新密码");
					return;
				}
				
				if(!newPwd.equals(newPwdAgain)){
					showShortToast("新密码不一致，请重新输入");
					return;
				}
				SettingData.getInstance().setPwd(newPwd);
				SettingData.getInstance().saveData(app);
				showShortToast("修改密码成功");
				startActivity(new Intent(ModifyPwdActivity.this,LoginActivity.class));
				finish();
			}
		});
	}

}
