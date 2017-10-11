package com.aeye.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.aeye.helibao.MyApplication;
import com.aeye.net.URLs;


public class SettingData {
	public static final int APP_THEME_GREEN = 0;
	public static final int APP_THEME_BLUE = 1;

	private String serverAddr;
	private String top;
	public String getTop() {
		return top;
	}

	public void setTop(String top) {
		this.top = top;
	}
	private int poseNum, aliveLevel, timeout;
	private float threshold, nativeThreshold;
	private boolean alive, voice, quality;
	private int appTheme, recogTheme;
	private String motionsStr;
	private String host;
	private String pwd;
	
	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param pwd the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	private ArrayList<Integer> posteList = new ArrayList<Integer>() {
	};

	private static SettingData mInstance = null;

	public static final String SECRET_KEY = "phonetest";

	public static SettingData getInstance() {
		if (mInstance == null) {
			mInstance = new SettingData();
		}
		return mInstance;
	}

	private SettingData() {
		updateData(MyApplication.getApp());
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}

	public int getPoseNum() {
		return poseNum;
	}

	public ArrayList<Integer> getPoseList() {
		return posteList;
	}

	public void setPoseNum(int poseNum) {
		this.poseNum = poseNum;
	}

	public int getAliveLevel() {
		return aliveLevel;
	}

	public void setAliveLevel(int aliveLevel) {
		this.aliveLevel = aliveLevel;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public float getNativeThreshold() {
		return nativeThreshold;
	}

	public void setNativeThreshold(float nativeThreshold) {
		this.nativeThreshold = nativeThreshold;
	}

	public int getAppTheme() {
		return appTheme;
	}

	public void setAppTheme(int appTheme) {
		this.appTheme = appTheme;
	}

	public int getRecogTheme() {
		return recogTheme;
	}

	public void setRecogTheme(int recogTheme) {
		this.recogTheme = recogTheme;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isVoice() {
		return voice;
	}

	public void setVoice(boolean voice) {
		this.voice = voice;
	}

	public boolean isQuality() {
		return quality;
	}

	public void setQuality(boolean quality) {
		this.quality = quality;
	}

	public boolean saveData(Context context) {
		SharedPreferences share = context.getSharedPreferences("DB",
				Context.MODE_PRIVATE);
		Editor edit = share.edit();
		edit.putString("serverAddr", serverAddr);
		edit.putInt("poseNum", poseNum);
		edit.putInt("aliveLevel", aliveLevel);
		edit.putInt("timeout", timeout);
		edit.putInt("recogTheme", recogTheme);
		edit.putInt("appTheme", appTheme);
		edit.putBoolean("alive", alive);
		edit.putBoolean("voice", voice);
		edit.putBoolean("quality", quality);
		edit.putFloat("threshold", threshold);
		edit.putFloat("nativeThreshold", nativeThreshold);
		edit.putString("motionsStr", getmotionsStr(posteList));
		edit.putString("host", host);
		edit.putString("top", top);
		edit.putString("pwd", pwd);
		return edit.commit();
	}

	private String getmotionsStr(List<Integer> mlist) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mlist.size(); i++) {
			sb.append(mlist.get(i)+",");
		}
		return sb.toString();
	}

	private void updateData(Context context) {
		SharedPreferences share = context.getSharedPreferences("DB",
				Context.MODE_PRIVATE);
		serverAddr = share.getString("serverAddr", "192.168.10.245:7031");
		alive = share.getBoolean("alive", true);
		voice = share.getBoolean("voice", true);
		quality = share.getBoolean("quality", true);
		threshold = share.getFloat("threshold", 0.882f);
		nativeThreshold = share.getFloat("nativeThreshold", 0.782f);
		poseNum = share.getInt("poseNum", 4);
		aliveLevel = share.getInt("aliveLevel", 5);
		timeout = share.getInt("timeout", 30);
		appTheme = share.getInt("appTheme", 0);
		recogTheme = share.getInt("recogTheme", 0);
		motionsStr = share.getString("motionsStr", "6");
		host=share.getString("host", URLs.HOST);
		top=share.getString("top", "3");
		pwd = share.getString("pwd", "1");
		setPosteList(motionsStr);
	}
	private void setPosteList(String motionsStr){
		posteList.clear();
		String[] motions=motionsStr.split(",");
		for(int i=0;i<motions.length;i++){
			posteList.add(Integer.valueOf(motions[i]));
		}
	}

}
