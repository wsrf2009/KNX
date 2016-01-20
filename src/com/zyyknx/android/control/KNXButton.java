package com.zyyknx.android.control;

public class KNXButton  extends KNXControlBase { 
	private static final long serialVersionUID = 1L;
	
	//按钮描述
	private String ButtonDesc;
	public String getButtonDesc() {
		return ButtonDesc;
	} 
	public void setButtonDesc(String buttonDesc) {
		ButtonDesc = buttonDesc;
	}
	//是否有长按事件
	private boolean HasLongClickCommand;
	public boolean getHasLongClickCommand() {
		return HasLongClickCommand;
	} 
	public void HasLongClickCommand(boolean hasLongClickCommand) {
		HasLongClickCommand = hasLongClickCommand;
	} 
}
