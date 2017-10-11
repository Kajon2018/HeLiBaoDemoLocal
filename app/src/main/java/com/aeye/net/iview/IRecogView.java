package com.aeye.net.iview;

import org.xmlpull.v1.XmlPullParser;



public interface IRecogView {
	/**1:N 识别的结�?*/
	void recogSucc(String message, XmlPullParser response);

	void recogFail(String result);
}
