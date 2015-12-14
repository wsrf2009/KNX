package com.zyyknx.android.control;

public class KNXBlinds extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
	//1. 是：只有一个标签，标签将显示在百叶窗控制中心
	//2. 否：在左右各显示的标签，
	private boolean HasSingleLabel;
	public boolean getHasSingleLabel() {
		return HasSingleLabel;
	}
	public void setHasSingleLabel(boolean hasSingleLabel) {
		HasSingleLabel = hasSingleLabel;
	}
	
	//左标签
	private String LeftText;
	public String getLeftText() {
		return LeftText;
	}
	public void setLeftText(String leftText) {
		LeftText = leftText;
	}
	
	//右标签
	private String RightText;
	public String getRightText() {
		return RightText;
	}
	public void setRightText(String rightText) {
		RightText = rightText;
	}
	
	//是否有长按事件
	private boolean HasLongClickCommand;
	public boolean getHasLongClickCommand() {
		return HasLongClickCommand;
	} 
	public void HasLongClickCommand(boolean hasLongClickCommand) {
		HasLongClickCommand = hasLongClickCommand;
	}
	
	//滑块两侧要显示的符号
	private String ControlSymbol; 
	public String getControlSymbol() {
		return ControlSymbol;
	}
	public void setControlSymbol(String controlSymbol) {
		ControlSymbol = controlSymbol;
	}
}
