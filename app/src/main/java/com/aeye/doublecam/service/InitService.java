package com.aeye.doublecam.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.aeye.android.config.ConfigData;
import com.aeye.sdk.AEFaceDetect;
import com.aeye.sdk.AEFaceQuality;

public class InitService extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initModel();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		System.out.println(" initservice  destory ");
		release();
		super.onDestroy();
	}

	private void release() {
		AEFaceDetect.getInstance().AEYE_FaceDetect_Destory();
		AEFaceQuality.getInstance().AEYE_FaceQuality_Destory();
	}

	private void initModel() {
		System.out.println(" initservice  init ");
		ConfigData.makeDestDir();
		AEFaceDetect.getInstance().AEYE_FaceDetect_Init(this, null);
		long ret = AEFaceQuality.getInstance().AEYE_FaceQuality_Init(this, null);
		System.out.println(" qualit ret : "+ret);
	}
}
