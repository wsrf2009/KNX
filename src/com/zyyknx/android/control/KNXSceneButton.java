package com.zyyknx.android.control;

public class KNXSceneButton extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
	//按钮描述
	private String Description;
	public String getDescription() {
		return Description;
	} 
	public void setDescription(String description) {
		Description = description;
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
