package com.aeye.doublecam.decode;




public interface AliveDetectInterface {
	
	/** 更新预览框上方文字提示信息 */
	public void updateStatsMsg(int msg, final int color);
	
	public void updateStatsMsg(String msg, final int color);
	
	public void updateStatsMsg();
	
	public void updateFace(boolean face);

	public void playVoice(int id);
}
