package com.sation.knxcontroller.models;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.util.StringUtil;

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

	private String Title;
	public String getTitle() {
		return Title;
	}

	/**
	 * Title的字体
	 */
	public KNXFont TitleFont;

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

	private KNXPadding Padding;
	public KNXPadding getPadding() {
		return this.Padding;
	}
	
	private int DisplayBorder;
	public EBool getDisplayBorder() {
		return EBool.values()[this.DisplayBorder];
	}
	
	public String BorderColor;
	
	/**
	 * 控件的不透明度
	 * */
	public float Alpha;
	
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
	
	/**
	 * 控件的背景色
	 * */
	public String BackgroundColor;

	private String BackgroundImage;
	public String getBackgroundImage() {
		return BackgroundImage;
	}

//	/**
//	* 获得控件图片目录的相对路径
//	* */
//	protected String getImagePath() {
//		return STKNXControllerConstant.PrefixResImg + this.getId() + File.separator;
//	}
}
