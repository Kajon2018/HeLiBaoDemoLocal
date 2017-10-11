package com.aeye.db;

import java.io.Serializable;
import java.util.Date;

public class RecordOfPunch implements Serializable {
	private static final long serialVersionUID = 1L;
	
	String idCard;
	Date time;
	float  fvScore;
	public  String getIdCard(){
		return idCard;
	}
	public void setIdCard(String id) {
		idCard=id;
	}
	public Date getTime(){
		return time;
	}
	public void  setTime(Date t){
		time=t;
	}
	public  float  getFvScore(){
		return fvScore;
	}
	public  void  setFvScore(float score){
		fvScore=score;
	}

}
