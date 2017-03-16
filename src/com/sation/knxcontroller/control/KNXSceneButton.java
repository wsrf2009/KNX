package com.sation.knxcontroller.control;

public class KNXSceneButton extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
//	private Map<String ,KNXSelectedAddress> ReadAddressId; 
//	public Map<String ,KNXSelectedAddress> getReadAddressId() {
//		return ReadAddressId;
//	}
//
//	private Map<String ,KNXSelectedAddress> WriteAddressIds; 
//	public Map<String ,KNXSelectedAddress> getWriteAddressIds() {
//		return WriteAddressIds;
//	}

	private String ImageOn;
	public String getImageOn() {
		if(null != this.ImageOn) {
			return this.getImagePath() + this.ImageOn;
		} else {
			return null;
		}
	}
	
	public String ColorOn;

	private String ImageOff;
	public String getImageOff() {
		if(null != this.ImageOff) {
			return this.getImagePath() + this.ImageOff;
		} else {
			return null;
		}
	}
	
	public String ColorOff;
	
	public int IsGroup;
	public EBool getIsGroup(){
		return EBool.values()[this.IsGroup];
	}
	
	public int DefaultValue;
	
//	public boolean isGroup;
//	//按钮描述
//	private String Description;
//	public String getDescription() {
//		return Description;
//	} 
//	public void setDescription(String description) {
//		Description = description;
//	}
//	//是否有长按事件
//	private boolean HasLongClickCommand;
//	public boolean getHasLongClickCommand() {
//		return HasLongClickCommand;
//	} 
//	public void HasLongClickCommand(boolean hasLongClickCommand) {
//		HasLongClickCommand = hasLongClickCommand;
//	}
}
