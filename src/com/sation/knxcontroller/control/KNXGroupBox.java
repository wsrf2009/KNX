package com.sation.knxcontroller.control;

import java.io.Serializable;

import com.sation.knxcontroller.models.KNXContainer; 

//布局
public class KNXGroupBox extends KNXContainer implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	// grid 边框样式
	private String BorderStyle;
	public String getBorderStyle() {
		return BorderStyle;
	} 
	public void setBorderStyle(String borderStyle) {
		BorderStyle = borderStyle;
	} 
}
