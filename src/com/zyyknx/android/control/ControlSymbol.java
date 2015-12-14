package com.zyyknx.android.control;

import java.io.Serializable;

//滑块两侧要显示的符号
public enum ControlSymbol {
	None(0), 
	DownUp(1),
	DardBright(2),
	SubtractAdd(3),
	Volume(4);
	
	
	 private int value; 
	 private ControlSymbol(int mvalue) {
        value = mvalue;
	 }
	 public void setControlSymbol(int mvalue) {
	      this.value = mvalue;
	 } 
	 
	 public int getControlSymbol() {
	     return value;
	 }  
} 
