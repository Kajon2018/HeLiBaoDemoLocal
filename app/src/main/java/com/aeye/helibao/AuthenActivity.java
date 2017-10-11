package com.aeye.helibao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aeye.android.constant.AEReturnCode;
import com.aeye.android.data.AEFaceInfo;
import com.aeye.android.uitls.BitmapUtils;
import com.aeye.db.ModelFingerDB;
import com.aeye.db.RecordOfPunch;
import com.aeye.db.SqlManager;
import com.aeye.doublecam.decode.AliveDetectHandler;
import com.aeye.doublecam.decode.AliveDetectInterface;
import com.aeye.doublecam.decode.AliveDetectMessage;
import com.aeye.doublecam.service.InitService;
import com.aeye.doublecam.service.ListenNetStateService;
import com.aeye.helibaolocal.R;
import com.aeye.net.iview.IRecogView;
import com.aeye.net.manager.RecogPrasenter;
import com.aeye.sdk.AEFVeinAlg;
import com.aeye.sdk.AEFVeinDev;
import com.aeye.sdk.AEFVeinDev.FINGER_STAUS;
import com.aeye.sdk.AEFVeinDev.LED_COLOR;
import com.aeye.utils.ShellUtils;
import com.aeye.view.CameraPreviewView;

public class AuthenActivity extends BaseActivity implements IRecogView,AliveDetectInterface{
	private final static int MSG_QUALITY_OK = 0;
	private final static int MSG_QUALITY_FAIL = 1;
	private final static int MSG_FINGER_DEVICE_OK = 2;
	private final static int MSG_FINGER_DEVICE_FAIL = 3;
	private final static int MSG_FINGER_UPDATE_STATUS = 4;
	private final static int MSG_FINGER_CAPTURE = 5;
	private final static int QUALITY_BAD_PRESS = 2001; /* ��ѹ */
	private final static int QUALITY_BAD_LIGHT_LEAK = 2003; /* ©�� */
	private final static int QUALITY_ROI_NOTFOUND = 2002; /* δ�ҵ���ָROI���� */
	/**
	 * 开始的语音延时提示
	 */
	private final static int MSG_START_VOICE = 6;
	protected static final int MSG_SHOWTIME = 7;

	private Bitmap mBitmap = null;
	private static AEFVeinDev mFingerDevice = null;
	private static AEFVeinAlg mFingerAlg = null;
	private static int CUR_DEV = 0;
	private static FINGER_STAUS mCurStatus = FINGER_STAUS.NONE;
//	private boolean mStart = false;
	private volatile boolean mRun = true;
	private RecogPrasenter iRecog;
	private AliveDetectHandler mAliveHdl;

	private TextView tvfingerstatus;
	private CameraPreviewView sfPreview;
	public volatile static boolean mNeed = false;
	private TextView time;
	private Button setting;
	private String date;
	private String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authen);
		tvfingerstatus = (TextView) findViewById(R.id.tvfingerstatus);
		tvfingerstatus.setText("请放入手指");
		startService(new Intent(this,ListenNetStateService.class));
		time = (TextView) findViewById(R.id.time);
		setting = (Button) findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AuthenActivity.this,LoginActivity.class);
				startActivity(intent);
			}
		});
		sfPreview = (CameraPreviewView) findViewById(R.id.aeye_sfPreview);
		sfPreview.setActivity(this);
		iRecog = new RecogPrasenter(this);
		startService(new Intent(this,InitService.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		initDevice();
		getDate();
		Thread time = new Thread(showTime);
		time.start();
		HandlerThread thread = new HandlerThread("AliveThread", HandlerThread.MAX_PRIORITY);
		thread.start();
		mAliveHdl = new AliveDetectHandler(thread.getLooper(), 1);
		mAliveHdl.setCallback(this);
		mAliveHdl.setUIHandler(mUIHandler);
	}
	@Override
	protected void onPause() {
		super.onPause();
		mRun = false;
		stopCapture();
		if(!isShowProgress()){
			mHandler.removeCallbacksAndMessages(null);
			mFingerDevice.AEYE_SetLedStatus(CUR_DEV, LED_COLOR.ALL, false);
			mFingerDevice.AEYE_SetIRBrightness(CUR_DEV, AEFVeinDev.IR_OFF);
			mFingerDevice.AEYE_CloseVein(CUR_DEV);
			}
			mUIHandler.removeCallbacksAndMessages(null);
			mAliveHdl.removeCallbacksAndMessages(null);
			mAliveHdl.sendMessage(mAliveHdl.obtainMessage(AliveDetectMessage.ALIVE_QUIT));
	}
	Runnable showTime = new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
					mHandler.sendEmptyMessage(MSG_SHOWTIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private void getDate() {
		// TODO Auto-generated method stub
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String year = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		String month = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		date = "" + year + "年" + month + "月" + day ;
	}
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case AliveDetectMessage.MAIN_SUCCESS:
				Log.d("alivedetect", "MAIN_SUCCESS");
				if(!isShowProgress()){
					sfPreview.setFaceRect(null);
				mNeed = false;
				AEFaceInfo bitRect = (AEFaceInfo) msg.obj;
				Bitmap bit = bitRect.faceBitmap;
				long sysTime = System.currentTimeMillis();
				CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);
				BitmapUtils.saveUserPicture(bit, name+"_"+sysTimeStr);
//				showLoading();
//				iRecog.recog_N("1",BitmapUtils.convertIconToString(bit),"1");
				}
				break;

			case AliveDetectMessage.MAIN_FAILED:
//				finishActivityReason(AEFaceNirPack.ERROR_FAIL, getString(MResource.getIdByName(
//						getApplication(), "string", "aeye_alive_fail")));
				break;

			case AliveDetectMessage.MAIN_RESET:
//				succeedNum = 0;
//				mSideSucc = true;
//				poseIndex = Constants.SIDE_MIN;
			case AliveDetectMessage.MAIN_DATA:
				mNeed = true;
				break;

			case AliveDetectMessage.MAIN_RECT: {
				Rect rect = (Rect)msg.obj;
				sfPreview.setFaceRect(rect);
				break;
			}
			case AliveDetectMessage.MAIN_DEVICE_ERROR: {
				break;
			}
			default:
				break;
			}
		}
	};
	/**
	 * 当前认证是否指静脉
	 */
	protected boolean isFinger = false;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOWTIME:
				long sysTime = System.currentTimeMillis();
				CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);
				time.setText(date+" "+sysTimeStr);
				break;
			case MSG_FINGER_DEVICE_OK:
				mHandler.sendEmptyMessageDelayed(MSG_START_VOICE,500);
				mRun = true;
				new Thread(new Runnable() {

					@Override
					public void run() {
						for (;;) {
							if (mRun) {
								FINGER_STAUS status = mFingerDevice.AEYE_GetVeinStatus(CUR_DEV);
								if (mCurStatus != status) {
									mHandler.sendMessage(mHandler.
											obtainMessage(MSG_FINGER_UPDATE_STATUS, status));
									mCurStatus = status;
								}
								if (mCurStatus == FINGER_STAUS.PRESSED) {
									mFingerDevice.AEYE_SetLedStatus(CUR_DEV, LED_COLOR.GREEN, true);
								} else {
									mFingerDevice.AEYE_SetLedStatus(CUR_DEV, LED_COLOR.RED, true);
								}
								try {
									Thread.sleep(3);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							} else {
								break;
							}
						}
					}
				}).start();
				break;

			case MSG_FINGER_DEVICE_FAIL:
				String str = (String) msg.obj;
				break;

			case MSG_FINGER_UPDATE_STATUS:
				FINGER_STAUS status = (FINGER_STAUS) msg.obj;
				switch (status) {
				case FINGER_TIP:
					break;
				case FINGER_PULP:
					break;
				case PRESSED:
					break;
				case NONE:
				default:
					break;
				}
				break;

			case MSG_QUALITY_OK:
				synchronized (AuthenActivity.this) {
					/*
					 * mStart = false; mRun = false; mAnimator.cancel();
					 */
//					ivfingershow.setImageBitmap(mBitmap);后台比对
					if(!isShowProgress()){
						name = "";
					showLoading();
					isFinger = true;
//					iRecog.recog_N("1",BitmapUtils.convertIconToString(mBitmap),"3");
					Log.e("finger", "mBitmap width = " + mBitmap.getWidth()
							+ " , height = " + mBitmap.getHeight());
					recogN();
					}
				}
				
				
				break;
			case MSG_QUALITY_FAIL:
				int err = ((Integer) msg.obj).intValue();
				switch (err) {
				case QUALITY_BAD_PRESS:
					break;
				case QUALITY_BAD_LIGHT_LEAK:
					break;
				case QUALITY_ROI_NOTFOUND:
					break;
				default:
					break;
				}
				synchronized (AuthenActivity.this) {
					//if (mStart) {
					restartCapture();
					//}
//					ivfingershow.setImageBitmap(mBitmap);
				}
				break;
			case MSG_FINGER_CAPTURE:
				//if (mStart) {
					if (mCurStatus == FINGER_STAUS.PRESSED) {
						doCapture();
					} else {
						restartCapture();
					}
				//}
				break;
			case MSG_START_VOICE:
				startCapture();
				break;
			}
			super.handleMessage(msg);
		}

	};
	byte[]  getFeatureMatch(Bitmap  bitmap){
		int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] pix1 = new int[width * height];
        mBitmap.getPixels(pix1, 0, width, 0, 0, width, height);
        byte []feature=AEFVeinAlg.getInstance().AEYE_VeinExtractMatch(pix1,width,height);
        
        return feature;
	}
	private void recogN() {
		List<byte[]> veinList = new ArrayList<byte[]>();
		List<Integer>idList=new ArrayList<Integer>();
		int size = SqlManager.get().queryModelSize(new ModelFingerDB());
		if(size <= 0){
			recogFail("");
			return;
		}
		if(size >0){
			List<ModelFingerDB> temp = new ArrayList<ModelFingerDB>();
			temp = SqlManager.get().queryModelFingerList();
			for (int i = 0; i < temp.size(); i++) {
				if(temp.get(i).getFeatureFinger() !=null && !temp.get(i).getFeatureFinger().equals(null)){
					byte[]f=Base64.decode(temp.get(i).getFeatureFinger(), Base64.DEFAULT);
					veinList.add(f);
					idList.add(i);
				}
				if(temp.get(i).getFeatureFinger1() !=null && !temp.get(i).getFeatureFinger1().equals(null)){
					byte[]f=Base64.decode(temp.get(i).getFeatureFinger1(), Base64.DEFAULT);
					veinList.add(f);
					idList.add(i);
				}
			}
//		float[] score = AEFVeinAlg.getInstance()
//				.AEYE_VeinON_GetHighestScore(BitmapUtils.convertIconToString(mBitmap), veinList);
		byte[]featureMatch=getFeatureMatch(mBitmap);
		float []score=new float[2];
		AEFVeinAlg.getInstance().AEYE_GetHighestScore(featureMatch,veinList,score);
		if(score[1] >= 0.6){
			int index=(int) score[0];
			index=idList.get(index);
			name = temp.get(index).getName();
			recogSucc(name);
			
			RecordOfPunch dto = new RecordOfPunch();
			dto.setIdCard(temp.get(index).getIdCard());
			dto.setTime(new Date(System.currentTimeMillis()));
			dto.setFvScore(score[1]);
			boolean flag = SqlManager.get().insertDTO(dto);
		}else{
			recogFail("");
		}
		}
	}

	private void startCapture() {
//		mStart = voices;
		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_FINGER_CAPTURE),
				1);
	}

	private void stopCapture() {
//		mStart = false;
		mHandler.removeMessages(MSG_FINGER_CAPTURE);
	}

	private void pauseCapture() {
		mHandler.removeMessages(MSG_FINGER_CAPTURE);
	}

	private void initDevice() {

		mFingerAlg = new AEFVeinAlg();
		mFingerAlg.AEYE_VeinQuality_Init(this, null);
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
				mFingerDevice = new AEFVeinDev();
				int ret = mFingerDevice.AEYE_OpenVein(CUR_DEV);
				if (ret == AEReturnCode.OK) {
					mFingerDevice.AEYE_SetLedStatus(CUR_DEV, LED_COLOR.RED, true);
					mFingerDevice.AEYE_SetIRBrightness(CUR_DEV, AEFVeinDev.IR_OFF);
					mHandler.sendMessage(mHandler.obtainMessage(MSG_FINGER_DEVICE_OK));
				} else {
					mHandler.sendMessage(mHandler.obtainMessage(MSG_FINGER_DEVICE_FAIL));
					Log.d("ZDX", "getDeviceCount error!");
				}
//			}
//		}).start();
	}
	
	@Override
	public void onBackPressed() {
		mHandler.removeCallbacksAndMessages(null);
		super.onBackPressed();
	}
	private void restartCapture() {
		mHandler.sendEmptyMessageDelayed(MSG_FINGER_CAPTURE, 1000-100);
	}
	
	private Thread captureThread = new Thread(new Runnable() {

		@Override
		public void run() {
			mFingerDevice.AEYE_SetIRBrightness(CUR_DEV, AEFVeinDev.IR_DEFAULT);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (AuthenActivity.this) {
				mBitmap = mFingerDevice.AEYE_GetVeinImage(CUR_DEV);
				if (mBitmap != null) {
					int quality = mFingerAlg.AEYE_VeinQuality(mBitmap);
					if (quality == AEReturnCode.OK) {
						mHandler.sendMessage(mHandler
								.obtainMessage(MSG_QUALITY_OK));
					} else {
						mHandler.sendMessage(mHandler.obtainMessage(
								MSG_QUALITY_FAIL,
								Integer.valueOf(quality)));
					}
				} else {
					Log.d("finger", "getImage is null");
				}
			}
			Log.d("debug", "doCapture");
			mFingerDevice.AEYE_SetIRBrightness(CUR_DEV, AEFVeinDev.IR_OFF);
		}
	});

	private void doCapture() {
		mHandler.removeCallbacks(captureThread);
		mHandler.post(captureThread);
	}

	/**
	 * 本地比对认证成功，采集人脸到本地
	 * @param name
	 */
	public void recogSucc(String name) {
		GpioControl.openDoor();
		System.out.println("认证成功");
		tvfingerstatus.setText("请放手指");
		hideLoading();
		mNeed = true;
		
		showRecogSucDialog(name);
	}
	@Override
	public void recogSucc(String message, XmlPullParser response) {
		mNeed = false;
		System.out.println("认证成功");
		tvfingerstatus.setText("请放手指");
		hideLoading();
		mNeed = true;
		 try {
			int eventType = response.getEventType(); 
			 while (eventType != XmlPullParser.END_DOCUMENT) {  
			     switch (eventType) {  
			     case XmlPullParser.START_TAG:  
			         String nodeName = response.getName();
			         
			         if ("personName".equals(nodeName)) {  
			         	response.next();
			         	if(response.getEventType()==XmlPullParser.TEXT) 
			         	{
							String personName = response.getText();
//							personName = new String(personName.getBytes("gbk"),"utf-8");
							Log.d("TAG", "personName: " + personName);
							showRecogSucDialog(personName);
			         	}
			         }
			     }  
			     eventType = response.next(); 
			 }
		} catch (Exception e) {
			recogFail("");
			e.printStackTrace();
		}   
	}

	@Override
	public void recogFail(String result) {
		System.out.println("认证失败");
		hideLoading();
		showShortToast("认证失败！");
//		System.out.println(" isFinger : "+isFinger);
//		if(isFinger)
//		{
//			//当前是指静脉认证失败后才做人脸的认证
//			mNeed = true;
//			isFinger = false;
//		}else{
//			mNeed = false;
//			isFinger = false;
//		}
		tvfingerstatus.setText("请再按一次");
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				//3秒后再开始采集指静脉
				startCapture();
			}
		}, 1000);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		
	}
	public void startFaceRecognize() {
		mAliveHdl.sendEmptyMessage(AliveDetectMessage.ALIVE_PREPARE);
	}

	public boolean isNeedData() {
		return mNeed;
	}

	public void sendDataToDecode(byte[] data, byte[] nir_Data, int width, int height) {
		if(mNeed && !isShowProgress()){
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		list.add(data);// 鍙鍏夌殑鍥剧墖
		list.add(nir_Data);// 杩戠孩澶栫殑鍥剧墖
		mAliveHdl.sendMessage(mAliveHdl.obtainMessage(AliveDetectMessage.ALIVE_DECODE, width, height, list));
		}
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	public void deviceError() {
		mUIHandler.sendEmptyMessage(AliveDetectMessage.MAIN_DEVICE_ERROR);
	}
	@Override
	public void updateStatsMsg(int msg, int color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatsMsg(String msg, int color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatsMsg() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFace(boolean face) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playVoice(int id) {
		// TODO Auto-generated method stub
		
	}
	
	
	private void showRecogSucDialog(String name) {
		final Dialog customizeDialog = new Dialog(AuthenActivity.this,R.style.CustomDialog);
		final View dialogView = LayoutInflater.from(AuthenActivity.this)
				.inflate(R.layout.recog_suc_dialog, null);
		final TextView time = (TextView) dialogView.findViewById(R.id.time);
		final TextView nameTxt = (TextView) dialogView.findViewById(R.id.name);
		nameTxt.setText(name + ",感谢您的使用");
		long sysTime = System.currentTimeMillis();
		CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);
		time.setText(date + " " + sysTimeStr);
		customizeDialog.setContentView(dialogView);
		customizeDialog.show();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				customizeDialog.dismiss();
				startCapture();
			}
			
		}, 3000);
	}
}
