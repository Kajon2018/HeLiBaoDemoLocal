package com.aeye.net;

import com.aeye.widget.SettingData;


/**
 * URL路径处理�??
 * 
 */
public class URLs {
	public static String HOST = "http://175.6.27.148:7001/banking/http/any/request";//��������ַ
//	public static String HOST = "http://192.168.10.186:7001/banking/http/any/request";

	public static String getURL(String method) {
		String host = SettingData.getInstance().getHost();
		if(!host.startsWith("http")) {
			host = "http://"+host;
		}
		return  host;
	}
}
