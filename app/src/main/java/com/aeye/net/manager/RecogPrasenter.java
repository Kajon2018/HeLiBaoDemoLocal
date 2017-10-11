package com.aeye.net.manager;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.aeye.net.Constants;
import com.aeye.net.NetCenter;
import com.aeye.net.URLs;
import com.aeye.net.iview.IRecogView;
import com.aeye.utils.Element;
import com.aeye.utils.XmlUtil;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class RecogPrasenter {
	private IRecogView iview;

	public RecogPrasenter(IRecogView view) {
		this.iview = view;
	}

	public void recog_N(String topN,String base64Pic,String biotype) {
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
		el14.setNodeText("/recog/compareN1");  
		el1.addChild(el14);
		Element el15 = new Element("channelId");
		el15.setNodeText("4");  
		el1.addChild(el15);
		Element el16 = new Element("businessId");
		el16.setNodeText("4");  
		el1.addChild(el16);
		Element el2 = new Element("body"); 
		Element el21 = new Element("base64Pic");
		el21.setNodeText(base64Pic);  
		el2.addChild(el21);
		Element el22 = new Element("threshold");
		if(biotype.equals("1"))
		{
			el22.setNodeText("0.6");  
		}else{
			el22.setNodeText("0.512");  
		}
		el2.addChild(el22);
		Element el23 = new Element("noPass");
		el23.setNodeText("1");  
		el2.addChild(el23);
		Element el24 = new Element("bioType");
		el24.setNodeText(biotype);  
		el2.addChild(el24);
		Element el25 = new Element("dataType");
		el25.setNodeText("0");  
		el2.addChild(el25);
		Element el26 = new Element("topN");
		el26.setNodeText("1");  
		el2.addChild(el26);
		Element el27 = new Element("indexType");
		el27.setNodeText("1");  
		el2.addChild(el27);
		Element el28 = new Element("withImage");
		el28.setNodeText("1");  
		el2.addChild(el28);
		Element el29 = new Element("machine");
		el29.setNodeText("0");  
		el2.addChild(el29);
		
		Element el222 = new Element("version");
		if(biotype.equals("1"))
		{
			el222.setNodeText("FACE302");
		}else{
			el222.setNodeText("FINGER30");
		}
		Element el2221 = new Element("left");
		el2221.setNodeText("");
		el2.addChild(el2221);
		Element el2222 = new Element("right");
		el2222.setNodeText("");
		el2.addChild(el2222);
		Element el2223 = new Element("top");
		el2223.setNodeText("");
		el2.addChild(el2223);
		Element el2224 = new Element("bottom");
		el2224.setNodeText("");
		el2.addChild(el2224);
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
		                            
		                            if ("resultCode".equals(nodeName)) {  
		                            	response.next();
		                            	if(response.getEventType()==XmlPullParser.TEXT) 
		                            	{
		                            		String resultCode = response.getText();
											Log.d("TAG","resultCode: "+resultCode);  
		                            		if(resultCode.equals("0")){
		                            			iview.recogSucc(null,response);	
		                            		}else{
		                            			iview.recogFail("认证失败");
		                            		}
		                            	}
		                            }else{
		                            	
		                            }
		                        }  
		                        eventType = response.next(); 
		                    }  
		                } catch (Exception e) { 
		                	System.out.println("response error ");
		                	iview.recogFail("认证失败");
		                    e.printStackTrace();  
		                }  
					}
				}, new ErrorListener() {
					
					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("response error1111 "+error.getMessage()+" error : "+error.toString());
						if (error != null)
							iview.recogFail(error.toString());
					}
				});
	}
}
