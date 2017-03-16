package com.sation.knxcontroller.control;

public class KNXSwitch extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
//	private Map<String ,KNXSelectedAddress> ReadAddressId; 
//	public Map<String ,KNXSelectedAddress> getReadAddressId() {
//		return ReadAddressId;
//	}
//
//	private Map<String ,KNXSelectedAddress> WriteAddressIds; 
//	public Map<String ,KNXSelectedAddress> getWriteAddressIds() {
//		return WriteAddressIds;
//	}
	
	//开关开启时图片
	private String ImageOn;
	public String getImageOn() {
		if (null != this.ImageOn) {
			return this.getImagePath() + this.ImageOn;
		} else {
			return null;
		}
	}
	
	public String ColorOn;

	private String ImageOff;
	public String getImageOff() {
		if (null != this.ImageOff) {
			return this.getImagePath() + this.ImageOff;
		} else {
			return null;
		}
	}
	
	public String ColorOff;
	

	//指令发送延迟时间(单位毫秒)
//	private int SendInterval;
//	public int getSendInterval() {
//		return SendInterval;
//	}
//	public void setSendInterval(int sendInterval) {
//		SendInterval = sendInterval;
//	}
}
