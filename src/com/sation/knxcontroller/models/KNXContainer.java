package com.sation.knxcontroller.models;

import java.io.Serializable;
import java.util.List;

import com.sation.knxcontroller.control.KNXControlBase;

public class KNXContainer  extends KNXControlBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//摆放在界面上的子控件 
	private List<KNXControlBase> Controls;
    public List<KNXControlBase> getControls() {
		return Controls;
	}
}
