package com.sation.knxcontroller.control;

import java.io.Serializable;
import java.util.Map;

import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView;
import com.sation.knxcontroller.widget.STKNXBlinds;
import com.sation.knxcontroller.widget.STKNXDigitalAdjustment;
import com.sation.knxcontroller.widget.STKNXGroupBox;
import com.sation.knxcontroller.widget.STKNXLabel;
import com.sation.knxcontroller.widget.STKNXSceneButton;
import com.sation.knxcontroller.widget.STKNXSliderSwitch;
import com.sation.knxcontroller.widget.STKNXSwitch;
import com.sation.knxcontroller.widget.STKNXTimerButton;
import com.sation.knxcontroller.widget.STKNXValueDisplay;
import com.sation.knxcontroller.widget.STKNXView;

import android.content.Context;

public class KNXControlBase extends KNXView implements Serializable { 
	private static final long serialVersionUID = 1L;

	// 读地址
	private Map<String ,KNXSelectedAddress> ReadAddressId; 
	public Map<String ,KNXSelectedAddress> getReadAddressId() {
		return ReadAddressId;
	} 
	public void setReadAddressId(Map<String ,KNXSelectedAddress> mReadAddressId) {
		ReadAddressId = mReadAddressId;
	}
		
	// ETS项目中该控件的ID 
	private Map<String ,KNXSelectedAddress> WriteAddressIds; 
	public Map<String ,KNXSelectedAddress> getWriteAddressIds() {
		return WriteAddressIds;
	} 
	public void setWriteAddressIds(Map<String ,KNXSelectedAddress> mWriteAddressIds) {
		WriteAddressIds = mWriteAddressIds;
	}
	
	//是否有提示
	private boolean HasTip ;
	public boolean getHasTip () {
		return HasTip ;
	} 
	public void setHasTip (boolean hasTip ) {
		HasTip  = hasTip;
	}
	
	//提示
	private String Tip  ;
	public String getTip  () {
		return Tip  ;
	} 
	public void setTip (String tip) {
		Tip   = tip;
	}

	public boolean Clickable;
	
	public String Icon;
	
	///控件当前的值
	public int ControlCurrentValue;
	public int getControlCurrentValue() {
		return ControlCurrentValue;
	}
	public void setControlCurrentValue(int mControlCurrentValue) {
		this.ControlCurrentValue = mControlCurrentValue;
	}
	
	//根据 KNXControlBase生成控件
	public static STKNXView buildWithControl(Context context, KNXControlBase mKNXControlBase) {
		STKNXView knxView = null;
	  if (mKNXControlBase instanceof KNXLabel) {
//	      controlView = new MyLabel(context, (KNXLabel)mKNXControlBase);
		  knxView = new STKNXLabel(context, (KNXLabel)mKNXControlBase);
	  } else if (mKNXControlBase instanceof KNXButton) {
//         controlView = new MyButton(context, (KNXButton) mKNXControlBase); 
      } else if (mKNXControlBase instanceof KNXSceneButton) {  
//          controlView = new MySceneButton(context, (KNXSceneButton) mKNXControlBase);
    	  knxView = new STKNXSceneButton(context, (KNXSceneButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXImageButton) {
//         controlView = new MyImageButton(context, (KNXImageButton) mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXBlinds) {
//    	 controlView = new MyBlinds(context, null, (KNXBlinds)mKNXControlBase);
    	  knxView = new STKNXBlinds(context, (KNXBlinds)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSwitch) {
//    	  controlView = new MySwitchButton(context, null, (KNXSwitch)mKNXControlBase);
    	  knxView = new STKNXSwitch(context, (KNXSwitch)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSlider) {
//    	  controlView = new MySlider(context, (KNXSlider)mKNXControlBase);
      }else if (mKNXControlBase instanceof KNXMediaButton) {
//    	  controlView = new MyMediaButton(context, (KNXMediaButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXValueDisplay) {
//    	  controlView = new MyValueDisplay(context, null, (KNXValueDisplay)mKNXControlBase);
    	  knxView = new STKNXValueDisplay(context, (KNXValueDisplay)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSliderSwitch) {
//    	  controlView = new MySliderSwitch(context, (KNXSliderSwitch)mKNXControlBase); 
    	  knxView = new STKNXSliderSwitch(context, (KNXSliderSwitch)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXColorLight) {
//    	  controlView = new MyColorLight(context, (KNXColorLight) mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSnapper) {
//    	  controlView = new MySnapper(context, (KNXSnapper)mKNXControlBase); 
      } else if (mKNXControlBase instanceof KNXSnapperSwitch) {
//    	  controlView = new MySnapperSwitch(context, (KNXSnapperSwitch)mKNXControlBase); 
      } else if (mKNXControlBase instanceof KNXSIPCall) {
    	  
      } else if (mKNXControlBase instanceof KNXTimerButton) {
//    	  controlView = new MyTimerTaskButton(context, null, (KNXTimerButton)mKNXControlBase);
    	  knxView = new STKNXTimerButton(context, (KNXTimerButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXDigitalAdjustment) {
    	  knxView = new STKNXDigitalAdjustment(context, (KNXDigitalAdjustment)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXGroupBox) {
    	  knxView = new STKNXGroupBox(context, (KNXGroupBox)mKNXControlBase);
      }

      return knxView; 
   } 
}
