package com.aeye.doublecam.decode;

import android.graphics.Bitmap;
import android.graphics.Rect;


/**
 * 该内部里存储了 人脸 的图像 和 人脸位置
 */
public class AliveDetectResult {

	public Bitmap m_Bitmap = null;
	
	public byte[] m_imageY = null;
	
	/** 人脸区域  */
	public Rect m_Rect = null;
	
	/** 记录本地算法 活体是否成功 **/
	public boolean aliveSucceed = false;
	
	public boolean aliveEyeSucceed = false;
	
	public Bitmap aliveFrame = null;
}
