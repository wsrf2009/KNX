package com.sation.knxcontroller.models;

import java.io.Serializable; 
import java.util.List;

public class KNXApp extends KNXView implements Serializable {
	private static final long serialVersionUID = 1L;
	 
	//产品说明信息 
	private String About;  
	public String getAbout() {
		return About;
	}
	public void setAbout(String about) {
		About = about;
	}
	
	//企业 Logo
	private String Logo;  
	public String getLogo() {
		return Logo;
	}
	public void setLogo(String logo) {
		Logo = logo;
	}
	
	//应用程序图标
	private String Symbol;
	public String getSymbol() {
		return Symbol;
	}
	public void setSymbol(String symbol) {
		Symbol = symbol;
	} 
	
	//空间划分
	private List<KNXArea> Areas;
	public List<KNXArea> getAreas() {
		return Areas;
	}
	public void setAreas(List<KNXArea> areas) {
		Areas = areas;
	}
	 
	//默认语言
//	private Language DefaultLanguage;  
//	public Language getDefaultLanguage() {
//		return DefaultLanguage;
//	}
//	public void setDefaultLanguage(Language defaultLanguage) {
//		DefaultLanguage = defaultLanguage;
//	} 
	
	//屏幕宽度
	private int ScreenWidth;
	public int getScreenWidth() {
		return ScreenWidth;
	} 
	public void setScreenWidth(int screenWidth) {
		ScreenWidth = screenWidth;
	}
	
	//屏幕高度
	private int ScreenHeight;
	public int getScreenHeight() {
		return ScreenHeight;
	} 
	public void setScreenHeight(int screenHeight) {
		ScreenHeight = screenHeight;
	}
	
	 
}
