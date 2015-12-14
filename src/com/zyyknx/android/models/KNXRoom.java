package com.zyyknx.android.models;

import java.io.Serializable; 
import java.util.List;

//房间，或者一个指定的场所，
public class KNXRoom extends KNXView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String Symbol; 
	public String getSymbol() {
		return Symbol;
	} 
	public void setSymbol(String symbol) {
		Symbol = symbol;
	}
	 
	private String PinCode;
	public String getPinCode() {
		return PinCode;
	} 
	public void setPinCode(String pinCode) {
		PinCode = pinCode;
	}

	private List<KNXPage> Pages; 
	public List<KNXPage> getPages() {
		return Pages;
	} 
	public void setRoomPages(List<KNXPage> pages) {
		Pages = pages;
	}
}
