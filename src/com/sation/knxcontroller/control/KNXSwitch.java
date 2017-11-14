package com.sation.knxcontroller.control;

import com.sation.knxcontroller.util.StringUtil;

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
//	private String ImageOn;
//	public String getImageOn() {
//		if (!StringUtil.isNullOrEmpty(this.ImageOn)) {
//			return this.getImagePath() + this.ImageOn;
//		} else {
//			return null;
//		}
//	}

	private String ImageOn;
	public String getImageOn() {
		return ImageOn;
	}
//	public String getImageOn() {
//		return this.getImagePath() + "ImageOn.png";
//	}
	
	public String ColorOn;

//	private String ImageOff;
//	public String getImageOff() {
//		if (!StringUtil.isNullOrEmpty(this.ImageOff)) {
//			return this.getImagePath() + this.ImageOff;
//		} else {
//			return null;
//		}
//	}

	private String ImageOff;
	public String getImageOff() {
		return ImageOff;
	}
//	public String getImageOff() {
//		return this.getImagePath() + "ImageOff.png";
//	}
	
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
