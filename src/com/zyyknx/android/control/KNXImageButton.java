package com.zyyknx.android.control;

public class KNXImageButton  extends KNXControlBase { 

	//按钮图片
	private String Image;
	public String getImage() {
		return Image;
	} 
	public void setImage(String image) {
		Image = image;
	}
	
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
