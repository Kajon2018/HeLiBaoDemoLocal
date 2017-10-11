package com.aeye.net;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Log;

import com.aeye.helibao.AppManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * 网络访问控制中心 用于统一管理网络访问接口及相关配�??
 * 
 */
public class NetCenter {

	private static final String TAG = NetCenter.class.getSimpleName();
	private static NetCenter mNetCenter;
	private static Map<Context, RequestQueue> mRequestMap;
	/**
	 * 每个请求中都要加的apiKey
	 */
	private String apiKey = "phonetest";

	private NetCenter() {
		mRequestMap = new HashMap<Context, RequestQueue>();
	}

	public synchronized static NetCenter getInstance() {
		if (mNetCenter == null) {
			mNetCenter = new NetCenter();
		}

		return mNetCenter;
	}

	/**
	 * 根据Context创建�??��请求队列,用于与Activity生命周期联动
	 * 
	 * @param context
	 */
	public RequestQueue init(Context context) {
		RequestQueue mRequestQueue = Volley.newRequestQueue(context);
		mRequestMap.put(context, mRequestQueue);
		return mRequestQueue;
	}

	/** 清除当前Activity的请求队�??*/
	public void clearRequestQueue() {
		mRequestMap.clear();
		Context context = AppManager.getAppManager().currentActivity();
		if (context != null) {
			VolleyRequestManager.cancelAll(context.getClass().getSimpleName());
		} else {
			VolleyRequestManager.cancelAll("");
		}
	}


	/**
	 * 发起�??��带tag的get请求
	 * 
	 * @param url
	 *            请求url地址
	 * @param listener
	 *            请求成功回调
	 * @param errorListener
	 *            请求失败回调
	 * @param tag
	 *            请求标记,用于取消请求
	 */
	public void get(String url, Listener<String> listener,
			ErrorListener errorListener) {
		StringRequest mRequest = new StringRequest(url, listener, errorListener);
		// RequestQueue mRequestQueue = mRequestMap.get(context);
		VolleyRequestManager.addRequest(mRequest, "");
	}

	/**
	 * 发起�??��带tag的自定义请求
	 * 
	 * @param <T>
	 * 
	 * @param url
	 *            请求url地址
	 * @param listener
	 *            请求成功回调
	 * @param errorListener
	 *            请求失败回调
	 * @param tag
	 *            请求标记,用于取消请求
	 */
	public <T> void gson(int method, String url, Class<T> clazz,
			Map<String, String> headers, HashMap<String, String> params,
			Listener<T> listener, ErrorListener errorListener) {
		Log.d(TAG,"request url :" +url);
		Context context = AppManager.getAppManager().currentActivity();
		// 添加基础参数
		addBaseParams(params);
		GsonRequest<T> mRequest = new GsonRequest<T>(method, url, clazz,
				headers, params, listener, errorListener);
		if (context != null) {
			VolleyRequestManager.addRequest(mRequest, context.getClass()
					.getSimpleName());
		} else {
			VolleyRequestManager.addRequest(mRequest, "");
		}
	}
	
	public <T> void xmlRequest(int method, String url,  String params,	Listener<XmlPullParser> listener, ErrorListener errorListener) {
		Log.d(TAG,"request url :" +url);
		clearRequestQueue();
		Context context = AppManager.getAppManager().currentActivity();
		XMLRequest mRequest = new XMLRequest(method, url, params, listener, errorListener);
		mRequest.setRetryPolicy(new DefaultRetryPolicy(120000, 
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		if (context != null) {
			VolleyRequestManager.addRequest(mRequest, context.getClass()
					.getSimpleName());
		} else {
			VolleyRequestManager.addRequest(mRequest, "");
		}
	}

	/**
	 * 添加系统参数
	 * 
	 * @param map
	 * @return
	 */
	Map<String, String> addBaseParams(HashMap<String, String> params) {

		params.put("userId","api_card");
		params.put("password","123456");
		params.put("sysId","4");
//		try {
//			params.put("apiSign", encodeSHA(params));
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Log.d(TAG,"params:" +params.toString());
		return params;
	}
	/**
	 * 转成加密的apiSign
	 *TODO
	 * 
	 *@return
	 *@throws NoSuchAlgorithmException
	 */
	private  String encodeSHA(HashMap<String, String> paramMap) throws NoSuchAlgorithmException {
		
		String[] keyArray = paramMap.keySet().toArray(new String[0]);
		Arrays.sort(keyArray);

		// 拼接有序的参数名-值串
		StringBuilder stringBuilder = new StringBuilder("");
		for (String key : keyArray) {
			stringBuilder.append(key).append(paramMap.get(key)); // 拼接
		}
		stringBuilder.append(apiKey);// 加上私密Key

		String codes = stringBuilder.toString();

		// 初始化MessageDigest,SHA即SHA-1的简�??
		MessageDigest md = MessageDigest.getInstance("SHA");
		// 执行摘要方法
		byte[] digest = md.digest(codes.getBytes());
		return bytesToHexString(digest).toUpperCase();
	}
	private  String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
}
