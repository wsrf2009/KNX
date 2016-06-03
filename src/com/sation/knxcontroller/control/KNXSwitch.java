package com.sation.knxcontroller.control;

public class KNXSwitch extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
	//开关开启时图片
	public String ImageOn;
	
	public String ColorOn;

	public String ImageOff;
	
	public String ColorOff;
	

	//指令发送延迟时间(单位毫秒)
	private int SendInterval;
	public int getSendInterval() {
		return SendInterval;
	}
	public void setSendInterval(int sendInterval) {
		SendInterval = sendInterval;
	}
}
