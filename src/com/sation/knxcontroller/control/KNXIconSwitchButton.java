package com.sation.knxcontroller.control;

public class KNXIconSwitchButton  extends KNXControlBase { 
	private static final long serialVersionUID = 1L;
	
	//开的图片
	private String SwitchOnIcon;
	public String getSwitchOnIcon() {
		return SwitchOnIcon;
	} 
	public void setSwitchOnIcon(String switchOnIcon) {
		SwitchOnIcon = switchOnIcon;
	}
	
	//关的图片
	private String SwitchOffIcon;
	public String getSwitchOffIcon() {
		return SwitchOffIcon;
	} 
	public void setSwitchOffIcon(String switchOffIcon) {
		SwitchOffIcon = switchOffIcon;
	}
	
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
