package com.zyyknx.android.control;

public class KNXSwitch extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
	//开关开启时图片
	private String OnImage;
	public String getOnImage() {
		return OnImage;
	} 
	public void setOnImage(String onImage) {
		OnImage = onImage;
	}
	
	//开关关闭时图片
	private String OffImage;
	public String getOffImage() {
		return OffImage;
	} 
	public void setOffImage(String offImage) {
		OffImage = offImage;
	}
	

	//指令发送延迟时间(单位毫秒)
	private int SendInterval;
	public int getSendInterval() {
		return SendInterval;
	}
	public void setSendInterval(int sendInterval) {
		SendInterval = sendInterval;
	}
	
	
}
