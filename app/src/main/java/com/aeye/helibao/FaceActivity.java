package com.aeye.helibao;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aeye.android.data.AEFaceInfo;
import com.aeye.android.uitls.BitmapUtils;
import com.aeye.android.uitls.MResource;
import com.aeye.android.uitls.VoicePlayer;
import com.aeye.doublecam.decode.AliveDetectHandler;
import com.aeye.doublecam.decode.AliveDetectInterface;
import com.aeye.doublecam.decode.AliveDetectMessage;
import com.aeye.helibaolocal.R;
import com.aeye.net.iview.ICreateModelView;
import com.aeye.net.manager.CreateModelPrasenter;
import com.aeye.view.CameraPreviewView_1;

public class FaceActivity extends BaseActivity implements AliveDetectInterface,ICreateModelView{


	private int cameraId = 0;
	private AliveDetectHandler mAliveHdl;

	private TextView tvCheckHint;
	private CameraPreviewView_1 sfPreview;
	private ImageView ivFaceMask;


	private volatile boolean mNeed = false;
	
	private String personId = "";
	private CreateModelPrasenter createModel;
	public static boolean isFinish = false;

	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case AliveDetectMessage.MAIN_SUCCESS:
				Log.d("alivedetect", "MAIN_SUCCESS");
				if(!isShowProgress() && !isFinish ){
						sfPreview.setFaceRect(null);
					AEFaceInfo bitRect = (AEFaceInfo) msg.obj;
					Bitmap bit = bitRect.faceBitmap;
					showLoading();
					personId = "1022";
					mNeed = false;
					createModel.addModel(personId, BitmapUtils.convertIconToString(bit));
				}
				break;

			case AliveDetectMessage.MAIN_FAILED:
				break;

			case AliveDetectMessage.MAIN_RESET:
			case AliveDetectMessage.MAIN_DATA:
				mNeed = true;
				break;

			case AliveDetectMessage.MAIN_RECT: {
				Rect rect = (Rect)msg.obj;
				sfPreview.setFaceRect(rect);
				break;
			}
			case AliveDetectMessage.MAIN_DEVICE_ERROR: {
				showShortToast("�豸��ʧ��");
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aeye_alive_detect);
		// 如果为true则只拍照，不作动�?
		tvCheckHint = (TextView) findViewById(R.id.aeye_tvCheckHint);

		ivFaceMask = (ImageView) findViewById(R.id.aeye_ivFaceMask);

		sfPreview = (CameraPreviewView_1) findViewById(R.id.aeye_sfPreview);
		sfPreview.setActivity(this);

		cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
		createModel = new CreateModelPrasenter(this);
		new VoicePlayer(this);

		HandlerThread thread = new HandlerThread("AliveThread", HandlerThread.MAX_PRIORITY);
		thread.start();
		mAliveHdl = new AliveDetectHandler(thread.getLooper(), cameraId);
		mAliveHdl.setCallback(this);
		mAliveHdl.setUIHandler(mUIHandler);
		startFaceRecognize();
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		personId = getIntent().getStringExtra("personId");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	

	public void startFaceRecognize() {
		mAliveHdl.sendEmptyMessage(AliveDetectMessage.ALIVE_PREPARE);
	}

	public boolean isNeedData() {
		return mNeed;
	}

	public void sendDataToDecode(byte[] data, byte[] nir_Data, int width, int height) {
		if(mNeed && !isShowProgress()){
		mNeed = false;
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		list.add(data);// 可见光的图片
		list.add(nir_Data);// 近红外的图片
		mAliveHdl.sendMessage(mAliveHdl.obtainMessage(AliveDetectMessage.ALIVE_DECODE, width, height, list));
		}
	}

	@Override
	protected void onDestroy() {
		mUIHandler.removeCallbacksAndMessages(null);
		mAliveHdl.removeCallbacksAndMessages(null);
		mAliveHdl.sendMessage(mAliveHdl.obtainMessage(AliveDetectMessage.ALIVE_QUIT));
		super.onDestroy();
	}

	@Override
	public void updateStatsMsg(final int msg, final int color) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				int id;
				switch (msg) {
				case AliveDetectMessage.STRING_LOOK_CAMERA:
					id = MResource.getIdByName(getApplication(), "string", "aeye_look_camera");
					tvCheckHint.setText(id);
					break;
				case AliveDetectMessage.STRING_ALIVE_SUCCESS:
					id = MResource.getIdByName(getApplication(), "string", "aeye_alive_recog_success");
					tvCheckHint.setText(id);
					break;
				case AliveDetectMessage.STRING_NO_FACE:
					id = MResource.getIdByName(getApplication(), "string", "aeye_no_find_face");
					tvCheckHint.setText(id);
					break;
					// 以下是质量评估的原因
				case AliveDetectMessage.STRING_QUALITY_UNEVEN:
					id = MResource.getIdByName(getApplication(), "string", "quality_light_reason");
					tvCheckHint.setText(id);
					break;
				case AliveDetectMessage.STRING_QUALITY_DARK:
					id = MResource.getIdByName(getApplication(), "string", "aye_quality_light_black");
					tvCheckHint.setText(id);
					break;
				case AliveDetectMessage.STRING_QUALITY_BRIGHT:
					id = MResource.getIdByName(getApplication(), "string", "aeye_quality_light_bright");
					tvCheckHint.setText(id);
					break;
				case AliveDetectMessage.STRING_QUALITY_NEAR:
					id = MResource.getIdByName(getApplication(), "string", "aeye_quality_close");
					tvCheckHint.setText(id);
					break;
				case AliveDetectMessage.STRING_QUALITY_FAR:
					id = MResource.getIdByName(getApplication(), "string", "aeye_quality_far");
					tvCheckHint.setText(id);
					break;
				// 多角�?
				case AliveDetectMessage.SIDE_FACE:
					id = MResource.getIdByName(getApplication(), "string", "aeye_look_camera");
					tvCheckHint.setText(id);
					break; 
				case AliveDetectMessage.SIDE_LEFT:
					id = MResource.getIdByName(getApplication(), "string", "aeye_turn_head_left");
					tvCheckHint.setText(id);
					break;
				case AliveDetectMessage.SIDE_RIGHT:
					id = MResource.getIdByName(getApplication(), "string", "aeye_trun_head_right");
					tvCheckHint.setText(id);
					break;
				case AliveDetectMessage.SIDE_UP:
					id = MResource.getIdByName(getApplication(), "string", "aeye_head_up");
					tvCheckHint.setText(id);
					break;
				case AliveDetectMessage.SIDE_DOWN:
					id = MResource.getIdByName(getApplication(), "string", "aeye_head_down");
					tvCheckHint.setText(id);
					break;
				default:

					break;
				}
				tvCheckHint.setTextColor(color);
			}
		});
	}

	@Override
	public void updateStatsMsg(final String msg, final int color) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// ivFaceMask.setImageResource(R.drawable.face_back);
				tvCheckHint.setText(msg);
				tvCheckHint.setTextColor(color);
			}
		});
	}
	
	private void updateStatus(int side, boolean text, boolean anim, boolean voice) {}
	
	
	public void updateStatsMsg() {
//		updateStatus(getCurSide(), true, false, false);
	}

	@Override
	public void updateFace(final boolean face) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				if (face) {
//					int id = R.drawable.aeye_face_back;
//					ivFaceMask.setImageResource(id);
//				} else {
//					int id = R.drawable.aeye_face_back_noface;
//					ivFaceMask.setImageResource(id);
//				}
			}
		});
	}
	
	@Override
	public void playVoice(final int id) {}

	private boolean isZh() {
		Locale locale = getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("zh"))
			return true;
		else
			return false;
	}
	
	

	public void deviceError() {
		mUIHandler.sendEmptyMessage(AliveDetectMessage.MAIN_DEVICE_ERROR);
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void createModelSucc(String message, String personId) {
		showSucDialog();
		isFinish = true;
	}

	@Override
	public void createModelFail(String result) {
		hideLoading();
		showShortToast("��ģʧ��");
	}
	
	private void showSucDialog() {
		final Dialog customizeDialog = new Dialog(FaceActivity.this,R.style.CustomDialog);
		final View dialogView = LayoutInflater.from(FaceActivity.this)
				.inflate(R.layout.create_suc_dialog, null);
		final Button addPerson = (Button) dialogView.findViewById(R.id.add_person);
		final Button returnHome = (Button) dialogView.findViewById(R.id.return_home);
		addPerson.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideLoading();
				startActivity(new Intent(FaceActivity.this,AddPersonActivity.class));
				finish();
			}
		});
		returnHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideLoading();
				startActivity(new Intent(FaceActivity.this,LoginActivity.class));
				finish();
			}
		});
		customizeDialog.setContentView(dialogView);
		customizeDialog.show();
	}
}
