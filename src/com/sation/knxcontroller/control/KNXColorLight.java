package com.sation.knxcontroller.control;

public class KNXColorLight extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	 
	//快捷颜色选择组合
	private String ShortcutColor;
	public String getShortcutColor() {
		return ShortcutColor;
	} 
	public void setShortcutColor(String shortcutColor) {
		ShortcutColor = shortcutColor;
	}
}

