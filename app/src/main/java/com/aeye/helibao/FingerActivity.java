package com.aeye.helibao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aeye.android.constant.AEReturnCode;
import com.aeye.android.uitls.BitmapUtils;
import com.aeye.db.ModelFingerDB;
import com.aeye.db.SqlManager;
import com.aeye.helibaolocal.R;
import com.aeye.net.iview.ICreateModelView;
import com.aeye.net.manager.CreateModelPrasenter;
import com.aeye.sdk.AEFVeinAlg;
import com.aeye.sdk.AEFVeinDev;
import com.aeye.sdk.AEFVeinDev.FINGER_STAUS;
import com.aeye.sdk.AEFVeinDev.LED_COLOR;

import 	android.util.Base64;

public class FingerActivity extends BaseActivity implements ICreateModelView{
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

	private ImageView ivfingershow;
	private TextView tvfingerstatus;
	private int mCapturedNum = 0;
	private int mFingerNum = 2;
	private String name,card,phone;
	private String base64Pics;
	private CreateModelPrasenter createPrasenter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aeye_finger_detect);
		tvfingerstatus = (TextView) findViewById(R.id.tvfingerstatus);
		ivfingershow = (ImageView) findViewById(R.id.ivfingershow);
		createPrasenter = new CreateModelPrasenter(this);
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		initDevice();
	}

	@Override
	protected void onResume() {
		super.onResume();
		name = getIntent().getStringExtra("name");
		card = getIntent().getStringExtra("card");
		phone = getIntent().getStringExtra("phone");
		base64Pics ="";
	}
	
	String  getFeature(Bitmap  bitmap){
		int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] pix1 = new int[width * height];
        mBitmap.getPixels(pix1, 0, width, 0, 0, width, height);
        byte []feature=AEFVeinAlg.getInstance().AEYE_VeinExtract(pix1,width,height);
        
        String enToStr = Base64.encodeToString(feature, Base64.DEFAULT); 
        return enToStr;
	}
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_FINGER_DEVICE_OK:
				tvfingerstatus.setText(R.string.aeye_working);
				tvfingerstatus.setText("请放入左手食指");
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
									Thread.sleep(300);
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
				tvfingerstatus.setText(R.string.aeye_device_open_error);
				break;

			case MSG_FINGER_UPDATE_STATUS:
				FINGER_STAUS status = (FINGER_STAUS) msg.obj;
				switch (status) {
				case FINGER_TIP:
					tvfingerstatus.setText(R.string.aye_not_touch);
					break;
				case FINGER_PULP:
					tvfingerstatus.setText(R.string.aeye_put_finger_in);
					break;
				case PRESSED:
					tvfingerstatus.setText(R.string.aeye_please_relax);
					break;
				case NONE:
				default:
					tvfingerstatus.setText(R.string.aeye_finger_free);
					break;
				}
				break;

			case MSG_QUALITY_OK:
				synchronized (FingerActivity.this) {
					/*
					 * mStart = false; mRun = false; mAnimator.cancel();
					 */
					ivfingershow.setImageBitmap(mBitmap);
					Log.e("finger", "mBitmap width = " + mBitmap.getWidth()
							+ " , height = " + mBitmap.getHeight());
				}
				if (++mCapturedNum < mFingerNum) {

			        
			        String enToStr = FingerActivity.this.getFeature(mBitmap);
					base64Pics = enToStr;//BitmapUtils.convertIconToString(mBitmap);
					tvfingerstatus.setText("请放入右手食指");
					restartCapture();
				} else {
//					base64Pics = base64Pics+","+BitmapUtils.convertIconToString(mBitmap);
					mCapturedNum = 0;
					//建模
					showLoading();
//					createPrasenter.createPersonAndModel(name, card, base64Pics, phone);
					createPersonAndModel(name, card, base64Pics,FingerActivity.this.getFeature(mBitmap), phone);
				}
				
				break;
			case MSG_QUALITY_FAIL:
				int err = ((Integer) msg.obj).intValue();
				switch (err) {
				case QUALITY_BAD_PRESS:
					tvfingerstatus.setText(R.string.aeye_press_error);
					break;
				case QUALITY_BAD_LIGHT_LEAK:
					tvfingerstatus.setText(R.string.aeye_light_overexposed);
					break;
				case QUALITY_ROI_NOTFOUND:
					tvfingerstatus.setText(R.string.aeye_no_realize_zone);
					break;
				default:
					break;
				}
				synchronized (FingerActivity.this) {
					//if (mStart) {
					restartCapture();
					//}
					ivfingershow.setImageBitmap(mBitmap);
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
	private void startCapture() {
//		mStart = voices;

		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_FINGER_CAPTURE),
				1000-100);
	}

	/**
	 * 建人建模
	 * @param name2
	 * @param card2
	 * @param base64Pics2
	 * @param phone2
	 * @param phone3 
	 */
	protected void createPersonAndModel(String name2, String card2,
			String base64Pics2, String base64Pic, String phone3) {
		ModelFingerDB dto = new ModelFingerDB();
		dto.setFeatureFinger(base64Pics2);
		dto.setFeatureFinger1(base64Pic);
		dto.setIdCard(card2);
		dto.setName(name2);
		dto.setPhone(phone3);
		boolean flag = SqlManager.get().insertDTO(dto);
		if(flag){
			createModelSucc(null, null);
		}else{
			createModelFail("");
		}
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
			synchronized (FingerActivity.this) {
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

	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mRun = false;
		stopCapture();
		mHandler.removeCallbacksAndMessages(null);
		mFingerDevice.AEYE_SetLedStatus(CUR_DEV, LED_COLOR.ALL, false);
		mFingerDevice.AEYE_SetIRBrightness(CUR_DEV, AEFVeinDev.IR_OFF);
		mFingerDevice.AEYE_CloseVein(CUR_DEV);
	}

	@Override
	public void createModelSucc(String message, String personId) {
		progressDialog.dismiss();
//		showShortToast("指静脉建模成功");
		showSucDialog();
//		Intent intent = new Intent(this,FaceActivity.class);
//		intent.putExtra("personId", personId);
//		startActivity(intent);
//		finish();
	}

	@Override
	public void createModelFail(String result) {
		progressDialog.dismiss();
		showShortToast("建模失败 ");
		finish();
	}
	
	private void showSucDialog() {
		final Dialog customizeDialog = new Dialog(FingerActivity.this,R.style.CustomDialog);
		final View dialogView = LayoutInflater.from(FingerActivity.this)
				.inflate(R.layout.create_suc_dialog, null);
		final Button addPerson = (Button) dialogView.findViewById(R.id.add_person);
		final Button returnHome = (Button) dialogView.findViewById(R.id.return_home);
		addPerson.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideLoading();
				startActivity(new Intent(FingerActivity.this,AddPersonActivity.class));
				finish();
			}
		});
		returnHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideLoading();
				startActivity(new Intent(FingerActivity.this,LoginActivity.class));
				finish();
			}
		});
		customizeDialog.setContentView(dialogView);
		customizeDialog.show();
	}
}
