package com.aeye.helibao;

import com.aeye.helibaolocal.R;
import com.aeye.widget.SettingData;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ServerSetActivity extends BaseActivity {
	private Button back,ok;
	private EditText server_edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_modify);
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		server_edit = (EditText) findViewById(R.id.server_edit);
		server_edit.setText(SettingData.getInstance().getHost());
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String server = server_edit.getText().toString();
				if(server!=null && !server.trim().equals("")){
					SettingData.getInstance().setHost(server);
					SettingData.getInstance().saveData(app);
					showShortToast("修改服务器地址成功");
					finish();
				}else{
					showShortToast("请输入服务器地址");
				}
			}
		});
	}

}
