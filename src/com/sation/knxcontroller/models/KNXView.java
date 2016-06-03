package com.sation.knxcontroller.models;

import java.io.Serializable; 

public class KNXView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public enum EFlatStyle
    {
        /** 扁平化 */
        Flat,

        /** 立体化 */
        Stereo,
    }
	
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
	
	/**
	 * 控件的起始位置x
	 * */
	public int Left;
	
	/**
	 * 控件的起始位置y
	 * */
	public int Top;
	
	/**
	 * 控件的宽度
	 * */
	public int Width;
	
	/**
	 * 控件高度 
	 */
	public int Height;
	
	public boolean DisplayBorder;
	
	public String BorderColor;
	
	/**
	 * 控件的不透明度
	 * */
	public double Alpha;
	
	/**
	 * 控件的圆角半径
	 * */
	public int Radius;
	
	/**
	 * 控件的平面样式
	 * */
	public int FlatStyle;
	public EFlatStyle getFlatStyle() {
		return EFlatStyle.values()[this.FlatStyle];
	}
	public void setFlatStyle(int style) {
		this.FlatStyle = style;
		
	}
	
	/**
	 * 控件的背景色
	 * */
	public String BackgroundColor;
	
	/**
	 * 控件的背景图片
	 * */
	public String BackgroundImage;
	
	/**
	 * 控件的字体颜色
	 * */
	public String FontColor;
	
	/**
	 * 控件的字体大小
	 * */
	public int FontSize;
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
