package com.sation.knxcontroller.control;

import java.util.Map;

import com.sation.knxcontroller.models.KNXSelectedAddress;

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
	public String ImageOn;
	
	public String ColorOn;

	public String ImageOff;
	
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
