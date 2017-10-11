package com.aeye.net;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class XMLRequest extends Request<XmlPullParser> {
	/** Charset for request. */
	private static final String PROTOCOL_CHARSET = "UTF-8";

	/** Content type for request. */
	private static final String PROTOCOL_CONTENT_TYPE = String.format(
			"text/xml; charset=%s", PROTOCOL_CHARSET);
	private final String mRequestBody;
	private final Listener<XmlPullParser> mListener;

	public XMLRequest(int method, String url, String requestBody,
			Listener<XmlPullParser> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
		mRequestBody = requestBody;
		setRetryPolicy(new DefaultRetryPolicy(120*000, 
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	public XMLRequest(String url, String requestBody,
			Listener<XmlPullParser> listener, ErrorListener errorListener) {
		this(Method.POST, url, requestBody, listener, errorListener);

	}

	
	@Override
	protected Response<XmlPullParser> parseNetworkResponse(
			NetworkResponse response) {
		try {
			System.out.println(" responese ....");
			String xmlString = new String(response.data,"utf-8");
//					HttpHeaderParser.parseCharset(response.headers));
			Log.d("responese : ",xmlString);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(xmlString));
			return Response.success(xmlPullParser,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(XmlPullParser response) {
		mListener.onResponse(response);
	}

	@Override
	public String getPostBodyContentType() {
		return getBodyContentType();
	}

	@Override
	public byte[] getPostBody() {
		byte[] body = getBody();
		return body;
	}

	@Override
	public String getBodyContentType() {
		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody() {
		try {
			byte[] bytes = mRequestBody
					.getBytes(PROTOCOL_CHARSET);
			return mRequestBody == null ? null : bytes;
		} catch (UnsupportedEncodingException uee) {
			VolleyLog
					.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
							mRequestBody, PROTOCOL_CHARSET);
			return null;
		}
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Charset", "UTF-8");
		headers.put("Content-Type", "application/xml");
		headers.put("Accept-Encoding", "*/*");
		headers.put("Connection", "close");
		return headers;
	}
}
