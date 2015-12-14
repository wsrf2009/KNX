package com.zyyknx.android.models;

import java.io.Serializable; 

public class KNXView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 界面元素的ID，整个应用唯一
	 */
	private int Id;  
	public int getId() {
		return Id;
	} 
	public void setId(int id) {
		Id = id;
	}  
	
	/**
	 * 界面元素需要显示在前端的文字
	 */
	private String Text; 
	public String getText() {
		return Text;
	}
	public void setText(String text) {
		Text = text;
	}
	
	/*
	//背景颜色 
	private Color BackgroudColor;
	public Color getBackgroudColor() {
		return BackgroudColor;
	}
	public void setBackgroudColor(Color backgroudColor) {
		BackgroudColor = backgroudColor;
	}
	
	//前景颜色 
	private Color ForeColor;
	public Color getForeColor() {
		return ForeColor;
	}
	public void setForeColor(Color foreColor) {
		ForeColor = foreColor;
	}
	
	//背景图片，如果有图片，则优先显示 
	private String BackgroudImage;   
	public String getBackgroudImage() {
		return BackgroudImage;
	}
	public void setBackgroudImage(String backgroudImage) {
		BackgroudImage = backgroudImage;
	}
	
	//图标 
	private String Symbol;
	public String getSymbol() {
		return Symbol;
	}
	public void setSymbol(String symbol) {
		Symbol = symbol;
	}
	*/
}
