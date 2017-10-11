package com.aeye.helibao;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.aeye.net.VolleyRequestManager;


public class MyApplication extends Application {
	private static MyApplication instence;
	/**
	 * 建模上传的图片base64的数�?
	 */
	public String m_PicBase64s = null;
	/**
	 * 活体认证后的照片
	 */
	public String m_picModleBase = null;

	/**
	 * 指静脉建模的图片list
	 */
	public List<String> m_fingerModleList = null;
	public static int width;
	public static int height;

	@Override
	public void onCreate() {
		super.onCreate();
		instence = this;
		VolleyRequestManager.init(this);
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();
	}

	public static MyApplication getApp() {
		return instence;
	}

}
