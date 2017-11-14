<<<<<<< HEAD
package com.sation.knxcontroller.control;

import java.io.Serializable;
import java.util.Map;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView;
import com.sation.knxcontroller.util.MapUtils;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.widget.STKNXBlinds;
import com.sation.knxcontroller.widget.STKNXDigitalAdjustment;
import com.sation.knxcontroller.widget.STKNXDimmer;
import com.sation.knxcontroller.widget.STKNXGroupBox;
import com.sation.knxcontroller.widget.STKNXImageButton;
import com.sation.knxcontroller.widget.STKNXLabel;
import com.sation.knxcontroller.widget.STKNXSceneButton;
import com.sation.knxcontroller.widget.STKNXShutter;
import com.sation.knxcontroller.widget.STKNXSliderSwitch;
import com.sation.knxcontroller.widget.STKNXSwitch;
import com.sation.knxcontroller.widget.STKNXTimerButton;
import com.sation.knxcontroller.widget.STKNXValueDisplay;
import com.sation.knxcontroller.widget.STKNXView;
import com.sation.knxcontroller.widget.STKNXWebCamer;

import android.content.Context;

public class KNXControlBase extends KNXView implements Serializable { 
	private static final long serialVersionUID = 1L;

	// 读地址
	private Map<String ,KNXSelectedAddress> ReadAddressId;
	public Map<String ,KNXSelectedAddress> getReadAddressId() {
		return ReadAddressId;
	} 

	// ETS项目中该控件的ID
	private Map<String ,KNXSelectedAddress> WriteAddressIds; 
	public Map<String ,KNXSelectedAddress> getWriteAddressIds() {
		return WriteAddressIds;
	}
	
	//是否有提示
	private int HasTip ;
	public EBool getHasTip () {
		return EBool.values()[this.HasTip];
	}
	
	//提示
	private String Tip;
	public String getTip() {
		return Tip;
	} 


	public int Clickable;
	public EBool getClickable() {
		return EBool.values()[this.Clickable];
	}
	
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
		  knxView = new STKNXLabel(context, (KNXLabel)mKNXControlBase);
	  } else if (mKNXControlBase instanceof KNXSceneButton) {  
    	  knxView = new STKNXSceneButton(context, (KNXSceneButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXBlinds) {
    	  knxView = new STKNXBlinds(context, (KNXBlinds)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSwitch) {
    	  knxView = new STKNXSwitch(context, (KNXSwitch)mKNXControlBase);
      } if (mKNXControlBase instanceof KNXValueDisplay) {
    	  knxView = new STKNXValueDisplay(context, (KNXValueDisplay)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSliderSwitch) {
    	  knxView = new STKNXSliderSwitch(context, (KNXSliderSwitch)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXTimerButton) {
    	  knxView = new STKNXTimerButton(context, (KNXTimerButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXDigitalAdjustment) {
    	  knxView = new STKNXDigitalAdjustment(context, (KNXDigitalAdjustment)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXGroupBox) {
    	  knxView = new STKNXGroupBox(context, (KNXGroupBox)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXImageButton){
			knxView = new STKNXImageButton(context, (KNXImageButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXShutter) {
			knxView = new STKNXShutter(context, (KNXShutter)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXDimmer) {
			knxView = new STKNXDimmer(context, (KNXDimmer)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXWebCamer) {
			knxView = new STKNXWebCamer(context, (KNXWebCamer)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXMediaButton) {

      } else if (mKNXControlBase instanceof KNXAirCondition) {

      } else if (mKNXControlBase instanceof KNXHVAC) {

      }

      return knxView; 
   }

   public KNXGroupAddress getReadAddress() {
	   try {
		   KNXSelectedAddress addr = MapUtils.getFirstOrNull(this.getReadAddressId());
		   if (null != addr) {
			   String addressId = addr.getId();
			   return STKNXControllerApp.getInstance().getGroupAddressIdMap().get(addressId);
		   }
	   } catch (Exception ex) {
		   ex.printStackTrace();
	   }

	   return null;
   }
}
=======
package com.sation.knxcontroller.control;

import java.io.Serializable;
import java.util.Map;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView;
import com.sation.knxcontroller.util.MapUtils;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.widget.STKNXBlinds;
import com.sation.knxcontroller.widget.STKNXDigitalAdjustment;
import com.sation.knxcontroller.widget.STKNXDimmer;
import com.sation.knxcontroller.widget.STKNXGroupBox;
import com.sation.knxcontroller.widget.STKNXImageButton;
import com.sation.knxcontroller.widget.STKNXLabel;
import com.sation.knxcontroller.widget.STKNXSceneButton;
import com.sation.knxcontroller.widget.STKNXShutter;
import com.sation.knxcontroller.widget.STKNXSliderSwitch;
import com.sation.knxcontroller.widget.STKNXSwitch;
import com.sation.knxcontroller.widget.STKNXTimerButton;
import com.sation.knxcontroller.widget.STKNXValueDisplay;
import com.sation.knxcontroller.widget.STKNXView;
import com.sation.knxcontroller.widget.STKNXWebCamer;

import android.content.Context;

public class KNXControlBase extends KNXView implements Serializable { 
	private static final long serialVersionUID = 1L;

	// 读地址
	private Map<String ,KNXSelectedAddress> ReadAddressId;
	public Map<String ,KNXSelectedAddress> getReadAddressId() {
		return ReadAddressId;
	} 

	// ETS项目中该控件的ID
	private Map<String ,KNXSelectedAddress> WriteAddressIds; 
	public Map<String ,KNXSelectedAddress> getWriteAddressIds() {
		return WriteAddressIds;
	}
	
	//是否有提示
	private int HasTip ;
	public EBool getHasTip () {
		return EBool.values()[this.HasTip];
	}
	
	//提示
	private String Tip;
	public String getTip() {
		return Tip;
	} 


	public int Clickable;
	public EBool getClickable() {
		return EBool.values()[this.Clickable];
	}
	
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
		  knxView = new STKNXLabel(context, (KNXLabel)mKNXControlBase);
	  } else if (mKNXControlBase instanceof KNXSceneButton) {  
    	  knxView = new STKNXSceneButton(context, (KNXSceneButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXBlinds) {
    	  knxView = new STKNXBlinds(context, (KNXBlinds)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSwitch) {
    	  knxView = new STKNXSwitch(context, (KNXSwitch)mKNXControlBase);
      } if (mKNXControlBase instanceof KNXValueDisplay) {
    	  knxView = new STKNXValueDisplay(context, (KNXValueDisplay)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSliderSwitch) {
    	  knxView = new STKNXSliderSwitch(context, (KNXSliderSwitch)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXTimerButton) {
    	  knxView = new STKNXTimerButton(context, (KNXTimerButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXDigitalAdjustment) {
    	  knxView = new STKNXDigitalAdjustment(context, (KNXDigitalAdjustment)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXGroupBox) {
    	  knxView = new STKNXGroupBox(context, (KNXGroupBox)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXImageButton){
			knxView = new STKNXImageButton(context, (KNXImageButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXShutter) {
			knxView = new STKNXShutter(context, (KNXShutter)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXDimmer) {
			knxView = new STKNXDimmer(context, (KNXDimmer)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXWebCamer) {
			knxView = new STKNXWebCamer(context, (KNXWebCamer)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXMediaButton) {

      } else if (mKNXControlBase instanceof KNXAirCondition) {

      } else if (mKNXControlBase instanceof KNXHVAC) {

      }

      return knxView; 
   }

   public KNXGroupAddress getReadAddress() {
	   try {
		   KNXSelectedAddress addr = MapUtils.getFirstOrNull(this.getReadAddressId());
		   if (null != addr) {
			   String addressId = addr.getId();
			   return STKNXControllerApp.getInstance().getGroupAddressIdMap().get(addressId);
		   }
	   } catch (Exception ex) {
		   ex.printStackTrace();
	   }

	   return null;
   }
}
>>>>>>> SationCentralControl(10inch)
