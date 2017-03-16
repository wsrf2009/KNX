package com.sation.knxcontroller.models;

import java.io.Serializable; 
import java.util.List;

//房间，或者一个指定的场所，
public class KNXRoom extends KNXView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String Symbol; 
	public String getSymbol() {
		if (null != this.Symbol) {
			return this.getImagePath() + this.Symbol;
		} else {
			return null;
		}
	} 
//	public void setSymbol(String symbol) {
//		Symbol = symbol;
//	}
	 
	private String PinCode;
	public String getPinCode() {
		return PinCode;
	} 
//	public void setPinCode(String pinCode) {
//		PinCode = pinCode;
//	}
	
	private int DefaultRoom;
	public EBool getDefaultRoom(){
		return EBool.values()[this.DefaultRoom];
	}

	private List<KNXPage> Pages; 
	public List<KNXPage> getPages() {
		return Pages;
	} 
//	public void setRoomPages(List<KNXPage> pages) {
//		Pages = pages;
//	}
}
