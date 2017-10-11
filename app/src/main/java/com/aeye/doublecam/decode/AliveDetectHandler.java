package com.aeye.doublecam.decode;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aeye.android.data.AEFaceInfo;
import com.aeye.android.uitls.BitmapUtils;
import com.aeye.helibao.AuthenActivity;
import com.aeye.sdk.AEFaceDetect;
import com.aeye.sdk.AEFaceQuality;

public class AliveDetectHandler extends Handler{
	
	private boolean DEBUG = false;
	private boolean DEBUG_TIME = false;
	
	private int loseCount = 0;

	private Handler mUIHdl = null;
	private AliveDetectInterface mCallback;
	
	AEFaceInfo faceInfo = new AEFaceInfo();
	private int CfgLoseFace = 2;
	
//	private AliveDetectThread mTask = null;
	
	public AliveDetectHandler(Looper looper, int camId) {
		super(looper);
//		mTask = new AliveDetectThread(320, 240, this);
//		mCamDev = CameraDeviceMgr.getInstance();
		
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch(msg.what) {
		case AliveDetectMessage.ALIVE_PREPARE:
//			mTask.prepareThread();
			prepare();
//			post(mTask);
			break;
			
		case AliveDetectMessage.ALIVE_DECODE:
			ArrayList<byte[]>  list = (ArrayList<byte[]>) msg.obj;
//			obj 鏄�  绗竴涓槸鍙鍏夛紝绗簩涓槸杩戠孩澶栫殑ArrayList<byte[]> list;
			if(list !=null && list.size()>1) {
				decode(list, msg.arg1, msg.arg2);
			}
			break;
			
		case AliveDetectMessage.ALIVE_SIDE:
			Log.d("ZDX", "MSG   ALIVE_SIDE");
			break;
			
		case AliveDetectMessage.ALIVE_QUIT:
//			mTask.freeThread();
			getLooper().quit();
		default:
			break;
		}
	} 
	
	public void setUIHandler(Handler hdl) {
		mUIHdl = hdl;
	}
	
	public void setCallback(AliveDetectInterface callback) {
		mCallback = callback;
	}
	
	private void LOG_TIME(String str) {
		if(DEBUG_TIME) {
			Log.d("aliveHandler", str + " :" + System.currentTimeMillis());
		}
	}
	
	private void LOG(String str) {
		if(DEBUG) {
			Log.d("aliveHandler", str);
		}
	}
	
	private void initFaceInfo(List<byte[]> list, int width, int height) {
		faceInfo.imgByteA = list.get(0);
		faceInfo.imgWidth = width;
		faceInfo.imgHeight = height;
		
		faceInfo.imgByteANir = list.get(1);
		faceInfo.imgWidthNir = width;
		faceInfo.imgHeightNir = height;
		
		faceInfo.width = width;
		faceInfo.height = height;
	}
	
	private void decode(List<byte[]> list, int width, int height) {
		if(!AuthenActivity.mNeed){
			return;
		}
		initFaceInfo(list, width, height);
		list.clear();
		if (loseCount == 0) {
			mCallback.updateFace(true);
			loseCount = 1;
		} else {
			faceInfo.grayByteA = BitmapUtils.yuv2Array2Y(faceInfo.imgByteA, width, height, 0);
			// 銆併�併�併�併�併�併�併�併�併�佸Bitmap杩涜绠楁硶澶勭悊 鏍规嵁 鍥惧儚鏁版嵁 鎵惧埌浜鸿劯鐨勫叿浣撲綅缃� 銆併�併�併�併�併�併�併�併�併�併�併��
			
			Log.i("FUCK", "AEYE_FaceDetectImg VIS");
			Rect[] rect = AEFaceDetect.getInstance().AEYE_FaceDetectImg(faceInfo.grayByteA, 
					faceInfo.imgWidth, faceInfo.imgHeight);

			// 濡傛灉 鎵惧埌 浜鸿劯 鐨� 鍏蜂綋浣嶇疆 鍐嶅幓 鏍规嵁 绠楁硶 瀵绘壘 鐪肩潧浣嶇疆
			if (rect != null) {
				faceInfo.imgRect = rect[0];
				mUIHdl.sendMessage(mUIHdl.obtainMessage(
						AliveDetectMessage.MAIN_RECT, faceInfo.imgRect));
				
				mCallback.updateFace(true);
				loseCount = 1;
				int quality = AEFaceQuality.QUALITY_OK;
				quality = AEFaceQuality.getInstance().AEYE_FaceQuality(faceInfo.grayByteA, 
						faceInfo.imgWidth, faceInfo.imgHeight, faceInfo.imgRect);
				System.out.println(" 质量评估："+quality);
				if (quality == AEFaceQuality.QUALITY_OK || quality == AEFaceQuality.QUALITY_BRIGHT || quality == AEFaceQuality.QUALITY_FAR || quality == AEFaceQuality.QUALITY_NEAR){
					faceInfo.cutFaceImageNir();
//					faceInfo.cutFaceImage();
					Message message = Message.obtain(mUIHdl,
										AliveDetectMessage.MAIN_SUCCESS, faceInfo);
					message.sendToTarget();
					return;
				}
				} else { // 濡傛灉娌℃壘鍒� 浜鸿劯 鐨� 鍏蜂綋浣嶇疆 灏辩户缁鎵�
				if (loseCount++ == CfgLoseFace) {
					mCallback.updateFace(false);
				}
				mUIHdl.sendMessage(mUIHdl.obtainMessage(
						AliveDetectMessage.MAIN_RECT, null));
			}
		}
		if (faceInfo.isAlive) {
			return;
		}
		checkAgain();
		LOG_TIME("end end end");
	}

	private void prepare() {
		Log.d("aliveHandler", "prepare");
		loseCount = 0;

		if(mCallback != null) {
			mCallback.updateStatsMsg(
					AliveDetectMessage.STRING_LOOK_CAMERA, Color.WHITE);
		} else {
			Log.e("aliveHandler", "mCallback == NULL");
		}
		
		if(mUIHdl != null) {
			Message message = Message.obtain(mUIHdl,
								AliveDetectMessage.MAIN_RESET);
			message.sendToTarget();
		} else {
			Log.e("aliveHandler", "mUIHdl == NULL");
		}
		LOG("prepare");
	}
	
	/**
	 * 鍐嶆妫�娴� <BR/>
	 * 
	 * @see RecognizeActivity鐨凜aptureActivityHandler鎺ュ彈骞跺鐞嗗け璐ラ噸鏂版娴嬫秷鎭�
	 */
	private void checkAgain() {
		if (mUIHdl != null) {
			Message message = Message.obtain(mUIHdl,AliveDetectMessage.MAIN_DATA);
			message.sendToTarget();
		}
	}
}
