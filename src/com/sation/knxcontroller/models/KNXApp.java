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
	
	//企业 Logo
//	private String Logo;
//	public String getLogo() {
//		if (null != this.getLogo()) {
//			return this.getImagePath() + this.Logo;
//		} else {
//			return null;
//		}
//	}
	
	//应用程序图标
//	private String Symbol;
//	public String getSymbol() {
//		if (null != this.Symbol) {
//			return this.getImagePath() + this.Symbol;
//		} else {
//			return null;
//		}
//	}

//	public String getAppBackgroundImage() {
//		return this.getImagePath() + "AppBackgroundImage.png";
//	}

	private String Symbol;
	public String getSymbol(){
		return Symbol;
	}
//	public String getSymbol() {
//		return this.getImagePath() + "Symbol.png";
//	}
	
	//空间划分
	private List<KNXArea> Areas;
	public List<KNXArea> getAreas() {
		return Areas;
	}
}
