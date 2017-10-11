package com.aeye.db;

import java.io.Serializable;
/**
 * 指静脉的模板DB
 * @author Administrator
 *
 */
public class ModelFingerDB implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**唯一标识�?*/
	String idCard;
	String name;
	String phone;
	/**指静脉特征1*/
	String featureFinger1;
	/**指静脉特�?*/
	String featureFinger;
	
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the featureFinger1
	 */
	public String getFeatureFinger1() {
		return featureFinger1;
	}
	/**
	 * @param featureFinger1 the featureFinger1 to set
	 */
	public void setFeatureFinger1(String featureFinger1) {
		this.featureFinger1 = featureFinger1;
	}
	/**
	 * @return the idCard
	 */
	public String getIdCard() {
		return idCard;
	}
	/**
	 * @param idCard the idCard to set
	 */
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the featureFinger
	 */
	public String getFeatureFinger() {
		return featureFinger;
	}
	/**
	 * @param featureFinger the featureFinger to set
	 */
	public void setFeatureFinger(String featureFinger) {
		this.featureFinger = featureFinger;
	}
	
}
