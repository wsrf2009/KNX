package com.sation.knxcontroller.control;

import com.sation.knxcontroller.models.KNXFont;
import com.sation.knxcontroller.util.StringUtil;

public class KNXBlinds extends KNXControlBase {
	private static final long serialVersionUID = 1L;

	private String LeftImage;
	public String getLeftImage() {
		return LeftImage;
	}
	
	public String LeftText;

	public KNXFont LeftTextFont;

	private String RightImage;
	public String getRightImage() {
		return RightImage;
	}
	
	public String RightText;

	public KNXFont RightTextFont;
}
