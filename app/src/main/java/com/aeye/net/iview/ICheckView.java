package com.aeye.net.iview;

import org.xmlpull.v1.XmlPullParser;



public interface ICheckView {
	void checkSucc(String message, XmlPullParser response);

	void checkFail(String result);
}
