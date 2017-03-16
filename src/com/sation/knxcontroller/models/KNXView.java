package com.sation.knxcontroller.models;

import com.sation.knxcontroller.STKNXControllerConstant;

import java.io.File;
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
	
	public enum EBool
    {
        No,

        Yes
    }

	public enum EOrientation
	{
		// 摘要:
		//     水平放置控件或元素。
		Horizontal,
		//
		// 摘要:
		//     垂直放置控件或元素。
		Vertical,
	}
	
	/**
	 * 界面元素的ID，整个应用唯一
	 */
	private int Id;  
	public int getId() {
		return Id;
	} 
//	public void setId(int id) {
//		Id = id;
//	}  
	
	/**
	 * 界面元素需要显示在前端的文字
	 */
	private String Text; 
	public String getText() {
		return Text;
	}
//	public void setText(String text) {
//		Text = text;
//	}
	
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
	
	private int DisplayBorder;
	public EBool getDisplayBorder() {
		return EBool.values()[this.DisplayBorder];
	}
	
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
	private int FlatStyle;
	public EFlatStyle getFlatStyle() {
		return EFlatStyle.values()[this.FlatStyle];
	}
//	public void setFlatStyle(int style) {
//		this.FlatStyle = style;
//	}
	
	/**
	 * 控件的背景色
	 * */
	public String BackgroundColor;
	
	/**
	 * 控件的背景图片
	 * */
	private String BackgroundImage;
	public String getBackgroundImage() {
		if (null != this.BackgroundImage) {
			return this.getImagePath() + this.BackgroundImage;
		} else {
			return null;
		}
	}
	
	/**
	 * 控件的字体颜色
	 * */
	public String FontColor;
	
	/**
	 * 控件的字体大小
	 * */
	public int FontSize;

	/*
	* 获得控件图片目录的相对路径
	* */
	protected String getImagePath() {
		return STKNXControllerConstant.PrefixResImg + this.getId() + File.separator;
	}
}
