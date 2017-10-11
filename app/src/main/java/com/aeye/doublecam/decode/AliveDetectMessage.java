package com.aeye.doublecam.decode;



public class AliveDetectMessage {
	public static final int MAIN_SUCCESS = 0;
	public static final int MAIN_FAILED = 1;
	public static final int MAIN_RESET = 2;
	public static final int MAIN_DATA = 3;
	public static final int MAIN_CAPTURE = 4;
	public static final int MAIN_RECT = 5;
	public static final int MAIN_DEVICE_ERROR = 6;
	
	
	public static final int ALIVE_PREPARE = 100;
	public static final int ALIVE_CAPTURE = 101;
	public static final int ALIVE_DECODE = 102;
	public static final int ALIVE_QUIT = 103;
	public static final int ALIVE_SIDE = 104;
	
	
	/** 开始进行认证 */
	public static final int STRING_START_RECOGNIZE = 1000;
	/** 正在进行真人检测，请眨眼 */
	public static final int STRING_BEGIN_BLINK = 1001;
	/** 活体检测成功 */
	public static final int STRING_ALIVE_SUCCESS = 1002;
	/** 未检测到人脸信息 */
	public static final int STRING_NO_FACE = 1003;
	/** 请确保您的面部出现在绿色检测框里，适当调整您的认证姿势、距离、光线！ */
	public static final int STRING_LOOK_CAMERA = 1004;
	
	public static final int STRING_QUALITY_UNEVEN = 1011;
	public static final int STRING_QUALITY_DARK = 1012;
	public static final int STRING_QUALITY_BRIGHT = 1013;
	public static final int STRING_QUALITY_NEAR = 1014;
	public static final int STRING_QUALITY_FAR = 1015;
	
	public static final int SIDE_FACE = 1021;
	public static final int SIDE_LEFT = 1022;
	public static final int SIDE_RIGHT = 1023;
	public static final int SIDE_UP = 1024;
	public static final int SIDE_DOWN = 1025;
	
	public static final int VOICE_LOOK_CAMERA = 2000;
}
