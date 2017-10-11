package com.aeye.helibao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;

import com.aeye.db.SqlManager;
import com.aeye.helibaolocal.R;
import com.aeye.net.iview.ICheckView;
import com.aeye.net.manager.CheckPersonPrasenter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddPersonActivity extends BaseActivity implements ICheckView {
	private Button back,ok;
	private EditText name,card,phone;
	private CheckPersonPrasenter checkModel;
	String nameStr,cardStr,phoneStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_person);
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		checkModel = new CheckPersonPrasenter(this);
		name = (EditText) findViewById(R.id.name);
		card = (EditText) findViewById(R.id.card);
		phone = (EditText) findViewById(R.id.phone);
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nameStr = name.getText().toString();
				cardStr = card.getText().toString();
				phoneStr = phone.getText().toString();
				if(nameStr == null || nameStr.trim().equals("")){
					showShortToast("请输入姓名");
					return;
				}
//				if(!isStrAndLetter(nameStr)){
//					showShortToast("姓名请输入中文或字母 ");
//					return;
//				}
				if(cardStr == null || cardStr.trim().equals("")){
					showShortToast("请输入身份证号");
					return;
				}
				if(!isIDCard(cardStr)){
					showShortToast("请输入正确的身份证号");
					return;
				}
				if(phoneStr == null || phoneStr.trim().equals("")){
					showShortToast("请输入手机号码");
					return;
				}
				if(!isMobileNO(phoneStr)){
					showShortToast("请输入正确的手机号码");
					return;
				}
//				showLoading();
//				checkModel.getByIdCard(nameStr, cardStr);
				String name = SqlManager.get().queryNameByCardId(cardStr);
				if(name !=null&& !name.equals("")){
					showShortToast("人员已存在,请重试");
				}else{
					goFingerCollect();
				}
			}
		});
	}

	
	public static boolean isStrAndLetter(String str) {
		boolean flag = true;
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).matches("[\\u4e00-\\u9fbf]+")
					|| str.substring(i, i + 1).matches("[a-zA-Z]")) {
			} else {
				return false;
			}
		}
		return flag;
	}
	
	public static boolean isIDCard(String idCard){
		//定义判别用户身份证号的正则表达式（要么是15位，要么是18位，最后一位可以为字母）  
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");  
        //通过Pattern获得Matcher  
        Matcher idNumMatcher = idNumPattern.matcher(idCard);  
        //判断用户输入是否为身份证号  
        return idNumMatcher.matches();
	}
	
	public static boolean isMobileNO(String mobiles) {
		 Pattern p = Pattern
		 .compile("^((13[0-9])|(15[^4,\\D])|(18[0,1,3,5-9])|(17[6-8]))\\d{8}$");
		 Matcher m = p.matcher(mobiles);
		 return m.matches();
	}


	@Override
	public void checkSucc(String message, XmlPullParser response) {
		hideLoading();
		 try {
			int eventType = response.getEventType(); 
			boolean isExit = false;
			 while (eventType != XmlPullParser.END_DOCUMENT) {  
			     switch (eventType) {  
			     case XmlPullParser.START_TAG:  
			         String nodeName = response.getName();
			         
			         if ("personId".equals(nodeName)) {  
			         	response.next();
			         	if(response.getEventType()==XmlPullParser.TEXT) 
			         	{
							String personId = response.getText();
							if(personId !=null && !personId.trim().equals(""))
							{
								isExit = true;
								Log.d("TAG", "personId: " + personId);
								showShortToast("人员已存在");
								return;
							}else{
								goFingerCollect();
							}
			         	}
			         }
			     }  
			     eventType = response.next(); 
			 }
			 if(!isExit){
				 goFingerCollect();
			 }
		} catch (Exception e) {
			checkFail("");
			e.printStackTrace();
		}   
	}


	private void goFingerCollect() {
		Intent intent = new Intent(AddPersonActivity.this,FingerActivity.class);
		intent.putExtra("name", nameStr);
		intent.putExtra("card", cardStr);
		intent.putExtra("phone", phoneStr);
		startActivity(intent);
	}


	@Override
	public void checkFail(String result) {
		hideLoading();
		showShortToast("查询人员失败");
	}
}
