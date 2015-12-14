package com.zyyknx.android.control;

public class KNXLabel extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
	// 字体大小
	private int FontSize; 
	public int getFontSize() {
		return FontSize;
	} 
	public void setFontSize(int fontSize) {
		this.FontSize = fontSize;
	}

	// 字体颜色
	private String FontColor; 
	public String getFontColor() {
		return FontColor;
	} 
	public void setFontColor(String fontColor) {
		this.FontColor = fontColor;
	}
	 
	private String TextAlign; 
	public String getTextAlign() {
		return TextAlign;
	} 
	public void setTextAlign(String textAlign) {
		this.TextAlign = textAlign;
	}
}
