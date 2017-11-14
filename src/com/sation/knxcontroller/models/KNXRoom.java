package com.sation.knxcontroller.models;

import java.io.Serializable; 
import java.util.List;

//房间，或者一个指定的场所，
public class KNXRoom extends KNXView implements Serializable {
	private static final long serialVersionUID = 1L;
	
//	private String Symbol;
//	public String getSymbol() {
//		if (null != this.Symbol) {
//			return this.getImagePath() + this.Symbol;
//		} else {
//			return null;
//		}
//	}

	private String Symbol;
	public String getSymbol() {
		return Symbol;
	}
//	public String getSymbol() {
//		return this.getImagePath() + "Symbol.png";
//	}
	 
	private String PinCode;
	public String getPinCode() {
		return PinCode;
	}
	
	private int DefaultRoom;
	public EBool getDefaultRoom(){
		return EBool.values()[this.DefaultRoom];
	}

	private List<KNXPage> Pages; 
	public List<KNXPage> getPages() {
		return Pages;
	}
}
