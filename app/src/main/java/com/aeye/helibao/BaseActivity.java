/*   
 *  ""
 *    
 */
package com.aeye.helibao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.aeye.helibaolocal.R;


public abstract class BaseActivity extends FragmentActivity {


	/** 是否允许全屏 */
	private boolean allowFullScreen = true;

	/** 是否允许�?�? */
	private boolean allowDestroy = true;

	/** view */
	private View view;

	/** app */
	protected MyApplication app;

	/** 分页的pageNumber */
	protected int pageSize = 10;

	/** handler */
	protected Handler handler;

	protected Dialog progressDialog;

	protected View back;

	protected View forward;
	
	/** 拍照图片路径 */
	protected Uri mImageCaptureUri;
	
	/** �?键检测的 requestCode 跳到DetectonResult 界面 */
	protected static final int REQUEST_YIJIAN = 0;
	/** 活体�?测的 requestCode 跳到DetectonResult 界面 */
	protected static final int REQUEST_DETE = 1;
	/**人脸�?测和质量评估的请�?*/
	protected static final int REQUEST_FACE = 2;
	/**
	 * 设置界面的修改后的服务器地址
	 */
	protected static final String SERVERURL="serverurl";
	/**设置中的阈�?�设置�??*/
	protected static final String YUZHISHARE="yuzhi";
	/**活体阈�??*/
	protected static final String HUOTISHARE ="shibie";
	/**
	 * 是否在线�?�? ，true为走在线�?测的接口 
	 */
	protected static final String ISBIOASSY = "isalive";
	protected Bundle recogData;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		allowFullScreen = true;
		app = MyApplication.getApp();
		AppManager.getAppManager().addActivity(this);
		recogData = new Bundle();
		//ȡ��������
	    requestWindowFeature(Window.FEATURE_NO_TITLE); 
	    //ȡ��״̬��
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	        WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	};


	protected void showShortToast(int pResId) {
		showShortToast(getString(pResId));
	}
	protected void showLongToast(int pResId) {
		showLongToast(getString(pResId));
	}
	protected void showLongToast(String pMsg) {
		Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
	}

	protected void showShortToast(String pMsg) {
		Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
	}
	protected void showLoading() {
		if(this.isFinishing()){
			return;
		}
		if (progressDialog == null) {
			progressDialog = new Dialog(this, R.style.Dialog_ContentOverlay);
			progressDialog.setContentView(R.layout.progress_dialog);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
//					if (requestData != null) {
//						requestData.setCancel(true);
//					}
				}
			});
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(true);
		}
		progressDialog.show();
	}


	protected boolean isShowProgress() {
		if(progressDialog!=null)
		{
			return progressDialog.isShowing();
		}else{
			return false;
		}
	}

	protected void hideLoading() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) { // 截获菜单事件
		return false; // 返回为true 则显示系统menu
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建�?�?
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 设置是否可以全屏
	 * 
	 * @param allowFullScreen
	 */
	public void setAllowFullScreen(boolean allowFullScreen) {
		this.allowFullScreen = allowFullScreen;
	}

	public void setAllowDestroy(boolean allowDestroy) {
		this.allowDestroy = allowDestroy;
	}

	public void setAllowDestroy(boolean allowDestroy, View view) {
		this.allowDestroy = allowDestroy;
		this.view = view;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
			view.onKeyDown(keyCode, event);
			if (!allowDestroy) {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	


	
	private static String bytesToHexString(byte[] src) {
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
	
	
	/**
	 *TODO
	 * 将拍照获取的图片转为base64
	 */
	protected String getBase64PicStr() {
		String m_PicBase64s = "";
		try {
			Bitmap bit = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri); 
			if(bit !=null){
				ByteArrayOutputStream baos = null;
				try {
					if (bit != null) {
						baos = new ByteArrayOutputStream();
						bit.compress(Bitmap.CompressFormat.JPEG, 30, baos);

						baos.flush();
						baos.close();
						byte[] bitmapBytes = baos.toByteArray();
						m_PicBase64s = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (baos != null) {
							baos.flush();
							baos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return m_PicBase64s;
	}
	
	/**
	 * 计算图片的缩放�??
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * TODO 压缩图片
	 * 
	 * @param filePath
	 * @return
	 */
	protected String bitmapToBase64(String filePath) {
		String pic =null;
		ByteArrayOutputStream baos = null;
		try {
			Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(filePath)); 
			if (bm == null) {
				return null;
			}
			baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
			pic = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return pic;
	}
	
	
	/**
	 * 照相
	 * 
	 * @param view
	 */
	protected void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File sd = Environment.getExternalStorageDirectory();
		String path = sd.getPath() + "/aeye";
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
		// Wysie_Soh: Create path for temp file
		mImageCaptureUri = Uri.fromFile(new File(file, "tmp_contact_"
				+ String.valueOf(System.currentTimeMillis()) + ".jpg"));

		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				mImageCaptureUri);
		try {
			intent.putExtra("return-data", true);
			startActivityForResult(intent, 4);
		} catch (ActivityNotFoundException e) {
		}
	}
	
}
