package com.aeye.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aeye.android.constant.AEReturnCode;
import com.aeye.android.libutils.ComplexUtil;
import com.aeye.helibao.AuthenActivity;
import com.aeye.helibao.FaceActivity;
import com.aeye.sdk.AEDoubleCamDev;

public class CameraPreviewView_1 extends SurfaceView implements 
											SurfaceHolder.Callback, Runnable{
	public static final int CAMERA_WIDTH = 640;
	public static final int CAMERA_HEIGHT = 480;
	public static final boolean CAMERA_COLOR_DISPLAY = true;
	public static final int CAMERA_SIZE_RGB = CAMERA_WIDTH * CAMERA_HEIGHT;
	public static final int CAMERA_SIZE_YUYV = CAMERA_SIZE_RGB * 2;

	private AEDoubleCamDev mCamDev = null;
	private FaceActivity mActivity = null;
	private Paint mPaint;
	private Rect mRect;
	private boolean cfgShowRect = false;
	private int mDevId;
	
	private int mColor = 0;
	private int mWidth = CAMERA_WIDTH;
	private int mHeight = CAMERA_HEIGHT;
	private int mSize = CAMERA_SIZE_YUYV;
	private byte[] mDataGray = null;
	private byte[] mDataEye = null;
	private byte[] mDataSend = null;
	private int[] mDataRGB = null;
	private byte[] mDataGraySend = null;
	
	private int mX, mY, bmpWidth, bmpHeight;
	private float mScale;
	
	private boolean mRun = false;
	private boolean mExit = false;
	
	private void init() {
		mRun = false;
		mExit = false;
		mCamDev = new AEDoubleCamDev();
		getHolder().addCallback(this);
		mPaint = new Paint();
	}
	
	public CameraPreviewView_1(Context context) {
		super(context);
		init();
	}

	public CameraPreviewView_1(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setActivity(FaceActivity activity) {
		mActivity = activity;
	}
	
	public void setFaceRect(Rect rect) {
		mRect = rect;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, 
			int width, int height) {
		
	}

	/*prepareCamera 第一个参数为第几个设�?  默认�?0�?�?*/
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSize = mCamDev.AEYE_OpenCamera(10, mWidth, mHeight);
		Log.d("ZDX", "prepareCamera mSize = " + mSize);
		if (mSize > 0) {
			mRun = true;
			mDataGray = new byte[mSize];
			mDataEye = new byte[mSize];
			mDataSend = new byte[mSize];
			mDataRGB = new int[CAMERA_SIZE_RGB]; 
			mDataGraySend = new byte[mSize];
			measurePosition();
			
			mPaint.setStrokeWidth(5);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.parseColor("#00fc45"));
			cfgShowRect = true;
			
			Thread thread = new Thread(this);
			thread.start();
		} else {
			Log.e("ZDX", "prepareCamera ERROR!!! " + mSize);
			mActivity.deviceError();
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mRun = false;
		for (int i=0; i<10; i++) {
			if(mExit) {
				break;
			} else {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		mCamDev.AEYE_CloseCamera(mDevId);
	}

	private void measurePosition() {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		mScale = (float)width / (float)mWidth;
		float yScale = (float)height / (float)mHeight;
		mScale = yScale < mScale ? yScale : mScale;
		mScale = 1;
		bmpWidth = (int)(mWidth * mScale);
		bmpHeight = (int)(mHeight * mScale);
		mX = (width - bmpWidth) >> 1;
		mY = (height - bmpHeight) >> 1;
	}
	
    public static Bitmap setScale(Bitmap bitMap) {  
        Matrix    mMatrix = new Matrix();
        mMatrix.reset(); 
        mMatrix.setScale(-1.0f,1.0f);  
        float fHeight=bitMap.getHeight();
        mMatrix.postTranslate(0.0f,fHeight);  
        //float sx X轴缩�?   
        //float sy Y轴缩�?  
       // mMatrix.setRotate(degrees);//.postScale(mScale, mScale);  
        return Bitmap.createBitmap(bitMap, 0, 0, bitMap.getWidth(),  
                bitMap.getHeight(), mMatrix, true);      
    }
    
	@Override
	public void run() {
		while (mRun) {
			//处理两个摄像头的数据�? 第一个是近红外，第二个是彩色可见光的图片
			int ret = mCamDev.AEYE_ProcessCamera(mDevId, mDataEye, mDataGray);
			if (ret != AEReturnCode.OK) {
				mActivity.deviceError();
				mRun = false;
				break;
			}
			if (mActivity.isNeedData()) {
				System.arraycopy(mDataEye, 0, mDataSend, 0, mSize);
				System.arraycopy(mDataGray, 0, mDataGraySend, 0, mSize);
				mActivity.sendDataToDecode(mDataSend,mDataGraySend, mWidth, mHeight);
			}
			
			if(CAMERA_COLOR_DISPLAY) {
				ComplexUtil.getInstance().
								YUY2ToBitmap(mDataEye, mDataRGB, mWidth, mHeight,0);
			} else {
				ComplexUtil.getInstance().
								YUY2ToBitmap(mDataGray, mDataRGB, mWidth, mHeight,0);
			}
			
			Canvas canvas = getHolder().lockCanvas();
			if(canvas != null) {
				canvas.drawARGB(0xff, Color.red(mColor), Color.green(mColor), Color.blue(mColor));
				Bitmap bmp = Bitmap.createBitmap(mDataRGB, mWidth, mHeight, 
						Bitmap.Config.ARGB_8888);
				bmp =setScale(bmp);
				canvas.drawBitmap(bmp, null, new RectF(mX, mY, mX+bmpWidth, mY+bmpHeight),
						null);
				if (mRect != null && cfgShowRect) {
					Rect draw = new Rect(mRect);
					draw.offset(0, mY);
					draw.left = mX + mWidth - mRect.right;
					draw.right = mX + mWidth - mRect.left;
					canvas.drawRect(draw, mPaint);
				}
				
				getHolder().unlockCanvasAndPost(canvas);
			}
		}
		mExit = true;
	}
	
}
