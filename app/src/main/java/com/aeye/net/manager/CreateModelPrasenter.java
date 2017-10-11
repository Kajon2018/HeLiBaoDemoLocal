package com.aeye.net.manager;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.aeye.helibao.MyApplication;
import com.aeye.helibaolocal.R;
import com.aeye.net.Constants;
import com.aeye.net.NetCenter;
import com.aeye.net.URLs;
import com.aeye.net.iview.ICreateModelView;
import com.aeye.utils.Element;
import com.aeye.utils.XmlUtil;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class CreateModelPrasenter {
	private ICreateModelView iview;

	public CreateModelPrasenter(ICreateModelView view) {
		this.iview = view;
	}
	/**
	 * 建人建模,用的是指静脉图片
	 * @param personName
	 * @param identifyCard
	 * @param base64Pics
	 * @param bioType
	 */
	public void createPersonAndModel(String personName,String identifyCard,String base64Pics,String phone) {
		///person/personAndModelAdd
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
		el14.setNodeText("/person/personAndModelAdd");  
		el1.addChild(el14);
		Element el15 = new Element("channelId");
		el15.setNodeText("4");  
		el1.addChild(el15);
		Element el16 = new Element("businessId");
		el16.setNodeText("4");  
		el1.addChild(el16);
		
		Element el2 = new Element("body"); 
		Element el21 = new Element("base64Pics");
		el21.setNodeText(base64Pics);  
		el2.addChild(el21);
		Element el22 = new Element("personName");
		el22.setNodeText(personName);  
		el2.addChild(el22);
		Element el23 = new Element("orgId");
		el23.setNodeText("1");  
		el2.addChild(el23);
		Element el24 = new Element("bioType");
		el24.setNodeText("3");  //首先指静脉的建模
		el2.addChild(el24);
		Element el25 = new Element("identifyCardType");
		el25.setNodeText("10010");  
		el2.addChild(el25);
		Element el26 = new Element("identifyCardNo");
		el26.setNodeText(identifyCard);  
		el2.addChild(el26);
		Element el27 = new Element("sex");
		el27.setNodeText("1");  
		el2.addChild(el27);
		Element el28 = new Element("nation");
		el28.setNodeText("1");  
		el2.addChild(el28);
		Element el29 = new Element("linkPhone");
		el29.setNodeText(phone);  
		el2.addChild(el29);
		
		Element el222 = new Element("linkAddress");
		el222.setNodeText("中电软件园"); 
		Element el2221 = new Element("machine");
		el2221.setNodeText("0");
		el2.addChild(el2221);
		Element el2222 = new Element("version");
		el2222.setNodeText("FINGER30");
		el2.addChild(el2222);
		el2.addChild(el222);
		el.addChild(el1);
		el.addChild(el2);
		String paramsRequest = XmlUtil.elementToXml(el);
		System.out.println(paramsRequest); 
		NetCenter.getInstance().xmlRequest(Method.POST,
				URLs.getURL(Constants.METHOD_RECOG_N),  paramsRequest, new Listener<XmlPullParser>() {

					@Override
					public void onResponse(XmlPullParser response) {
						try {  
							System.out.println("response success: "+response.toString());
							int eventType = response.getEventType(); 
		                    while (eventType != XmlPullParser.END_DOCUMENT) {  
		                        switch (eventType) {  
		                        case XmlPullParser.START_TAG:  
		                            String nodeName = response.getName();
		                            
		                            if ("personId".equals(nodeName)) {  
		                            	response.next();
		                            	if(response.getEventType()==XmlPullParser.TEXT) 
		                            	{
		                            		String personId = response.getText();
											Log.d("TAG","personId: "+personId);  
		                            		if(personId !=null){
		                            			iview.createModelSucc(null,personId);	
		                            		}else{
		                            			iview.createModelFail("建模失败");
		                            		}
		                            	}
		                            }else{
//		                            	iview.createModelFail("建模失败");
		                            }
		                        }  
		                        eventType = response.next(); 
		                    }  
		                } catch (Exception e) { 
		                	System.out.println("response error ");
		                	iview.createModelFail("建模失败");
		                    e.printStackTrace();  
		                }  
					}
				}, new ErrorListener() {
					
					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("response error1111 "+error.getMessage()+" error : "+error.toString());
						if (error != null)
							iview.createModelFail(error.toString());
					}
				});
	}
	
	public void addModel(String personId,String base64Pics) {
		///person/personAndModelAdd
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
		el14.setNodeText("/model/add");  
		el1.addChild(el14);
		Element el15 = new Element("channelId");
		el15.setNodeText("4");  
		el1.addChild(el15);
		Element el16 = new Element("businessId");
		el16.setNodeText("4");  
		el1.addChild(el16);
		
		Element el2 = new Element("body"); 
		Element el21 = new Element("personId");
		el21.setNodeText(personId);  
		el2.addChild(el21);
		Element el22 = new Element("base64Pics");
		el22.setNodeText(base64Pics);  
		el2.addChild(el22);
		Element el24 = new Element("bioType");
		el24.setNodeText("1");  //人脸增加模板
		el2.addChild(el24);
		Element el2221 = new Element("machine");
		el2221.setNodeText("0");
		el2.addChild(el2221);
		Element el2222 = new Element("version");
		el2222.setNodeText("FACE302");
		el2.addChild(el2222);
		el.addChild(el1);
		el.addChild(el2);
		String paramsRequest = XmlUtil.elementToXml(el);
		System.out.println(paramsRequest); 
		NetCenter.getInstance().xmlRequest(Method.POST,
				URLs.getURL(Constants.METHOD_RECOG_N),  paramsRequest, new Listener<XmlPullParser>() {

					@Override
					public void onResponse(XmlPullParser response) {
						try {  
							System.out.println("response success: "+response.toString());
							int eventType = response.getEventType(); 
							boolean isSuc = false;
		                    while (eventType != XmlPullParser.END_DOCUMENT) {  
		                        switch (eventType) {  
		                        case XmlPullParser.START_TAG:  
		                            String nodeName = response.getName();
		                            
		                            if ("total".equals(nodeName)) {  
		                            	response.next();
		                            	if(response.getEventType()==XmlPullParser.TEXT) 
		                            	{
		                            		isSuc = true;
		                            		String total = response.getText();
											Log.d("TAG","total: "+total);  
		                            		if(total !=null){
		                            			iview.createModelSucc(null,total);
		                            			return;
		                            		}else{
		                            			iview.createModelFail("建模失败");
		                            			return;
		                            		}
		                            	}
		                            }else{
//		                            	iview.createModelFail("建模失败");
		                            }
		                        }  
		                        eventType = response.next(); 
		                    }  
		                    if(!isSuc){
		                    	iview.createModelFail("建模失败");
		                    }
		                } catch (Exception e) { 
		                	System.out.println("response error ");
		                	iview.createModelFail("建模失败");
		                    e.printStackTrace();  
		                }  
					}
				}, new ErrorListener() {
					
					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("response error1111 "+error.getMessage()+" error : "+error.toString());
						if (error != null)
							iview.createModelFail(error.toString());
					}
				});
	}
}
