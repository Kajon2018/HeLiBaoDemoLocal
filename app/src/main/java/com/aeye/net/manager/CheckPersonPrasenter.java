package com.aeye.net.manager;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.aeye.net.Constants;
import com.aeye.net.NetCenter;
import com.aeye.net.URLs;
import com.aeye.net.iview.ICheckView;
import com.aeye.utils.Element;
import com.aeye.utils.XmlUtil;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
/**
 * 查询这个人员是否已建模
 * @author Administrator
 *
 */
public class CheckPersonPrasenter {
	private ICheckView iview;

	public CheckPersonPrasenter(ICheckView view) {
		this.iview = view;
	}

	public void getByIdCard (String name,String card) {
		Element el = new Element("anyRequest"); 
		el.addProperty("xmlns", "http://aeye.com/aeye/schemas");
		Element el1 = new Element("head"); 
		Element el11 = new Element("userAccount");
		el11.setNodeText("real");  
		el1.addChild(el11);
		Element el12 = new Element("userPassword");
		el12.setNodeText("1");  
		el1.addChild(el12);
		Element el13 = new Element("appId");
		el13.setNodeText("2");  
		el1.addChild(el13);
		Element el14 = new Element("appKey");
		el14.setNodeText("/person/getPersonByIdCard");  
		el1.addChild(el14);
		Element el15 = new Element("channelId");
		el15.setNodeText("4");  
		el1.addChild(el15);
		Element el16 = new Element("businessId");
		el16.setNodeText("4");  
		el1.addChild(el16);
		Element el2 = new Element("body"); 
		Element el21 = new Element("identifyCardType");
		el21.setNodeText("10010");  
		el2.addChild(el21);
		Element el24 = new Element("identifyCardNo");
		el24.setNodeText(card);  
		el2.addChild(el24);
		el.addChild(el1);
		el.addChild(el2);
		String paramsRequest = XmlUtil.elementToXml(el);
		System.out.println(paramsRequest); 
		NetCenter.getInstance().xmlRequest(Method.POST,
				URLs.getURL(""),  paramsRequest, new Listener<XmlPullParser>() {

					@Override
					public void onResponse(XmlPullParser response) {
						try {  
							System.out.println("response success: "+response.toString());
		                    int eventType = response.getEventType(); 
		                    while (eventType != XmlPullParser.END_DOCUMENT) {  
		                        switch (eventType) {  
		                        case XmlPullParser.START_TAG:  
		                            String nodeName = response.getName();
		                            
		                            if ("resultCode".equals(nodeName)) {  
		                            	response.next();
//		                            	if(response.getEventType()==XmlPullParser.TEXT) 
//		                            	{
////		                            		String resultCode = response.getText();
////											Log.d("TAG","resultCode: "+resultCode);  
////		                            		if(resultCode.equals("0")){
		                            			iview.checkSucc(null,response);	
//		                            		}
//		                            	}
		                            }else{
//		                            	iview.checkFail("查询失败");
		                            }
		                        }  
		                        eventType = response.next(); 
		                    }  
		                } catch (Exception e) { 
		                	System.out.println("response error ");
		                	iview.checkFail("查询失败");
		                    e.printStackTrace();  
		                }  
					}
				}, new ErrorListener() {
					
					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("response error1111 "+error.getMessage()+" error : "+error.toString());
						if (error != null)
							iview.checkFail(error.toString());
					}
				});
	}
}
