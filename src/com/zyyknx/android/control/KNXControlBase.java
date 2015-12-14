package com.zyyknx.android.control;

import java.io.Serializable;
import java.util.Map;

import android.content.Context;

import com.zyyknx.android.models.KNXDataType;
import com.zyyknx.android.models.KNXSelectedAddress;
import com.zyyknx.android.models.KNXView;
import com.zyyknx.android.widget.MyBlinds;
import com.zyyknx.android.widget.ControlView;
import com.zyyknx.android.widget.IconButton;
import com.zyyknx.android.widget.MySceneButton;
import com.zyyknx.android.widget.MySnapper;
import com.zyyknx.android.widget.MySnapperSwitch;
import com.zyyknx.android.widget.MySwitchButton;
import com.zyyknx.android.widget.MyButton;
import com.zyyknx.android.widget.MyColorLight;
import com.zyyknx.android.widget.MyImageButton;
import com.zyyknx.android.widget.MyLabel;
import com.zyyknx.android.widget.MyMediaButton;
import com.zyyknx.android.widget.MySliderSwitch;
import com.zyyknx.android.widget.MySwitch;
import com.zyyknx.android.widget.MyValueDisplay;
import com.zyyknx.android.widget.Slider;
import com.zyyknx.android.widget.SwitchButton;

public class KNXControlBase extends KNXView implements Serializable { 
	private static final long serialVersionUID = 1L;
	 
	private int Column;
	public int getColumn() {
		return Column;
	} 
	public void setColumn(int column) {
		Column = column;
	} 
	
	private int Row;
	public int getRow() {
		return Row;
	} 
	public void setRow(int row) {
		Row = row;
	} 

	private int ColumnSpan;
	public int getColumnSpan() {
		return ColumnSpan;
	} 
	public void setColumnSpan(int columnSpan) {
		ColumnSpan = columnSpan;
	} 
	
	private int RowSpan;
	public int getRowSpan() {
		return RowSpan;
	} 
	public void setRowSpan(int rowSpan) {
		RowSpan = rowSpan;
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
	
	///控件当前的值
	public int ControlCurrentValue;
	public int getControlCurrentValue() {
		return ControlCurrentValue;
	}
	public void setControlCurrentValue(int mControlCurrentValue) {
		this.ControlCurrentValue = mControlCurrentValue;
	}
	
	/*
	private String ETSName; 
	public String getETSName() {
		return ETSName;
	} 
	public void setETSName(String mETSName) {
		ETSName = mETSName;
	}  
	
	private String WriteAddress; 
	public String getWriteAddress() {
		return WriteAddress;
	} 
	public void setWriteAddress(String writeAddress) {
		WriteAddress = writeAddress;
	}
	
	private String ReadAddress; 
	public String getReadAddress() {
		return ReadAddress;
	} 
	public void setReadAddress(String readAddress) {
		ReadAddress = readAddress;
	}
	
	private KNXDataType DataType; 
	public KNXDataType getDataType() {
		return DataType;
	} 
	public void setDataType(KNXDataType dataType) {
		DataType = dataType;
	} 
	*/
	
	//根据 KNXControlBase生成控件
	public static ControlView buildWithControl(Context context, KNXControlBase mKNXControlBase) {
	  ControlView controlView = null;
	  if (mKNXControlBase instanceof KNXLabel) {
	      controlView = new MyLabel(context, (KNXLabel)mKNXControlBase);
	  } else if (mKNXControlBase instanceof KNXButton) {
         controlView = new MyButton(context, (KNXButton) mKNXControlBase); 
      } else if (mKNXControlBase instanceof KNXSceneButton) {  
          controlView = new MySceneButton(context, (KNXSceneButton) mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXImageButton) {
         controlView = new MyImageButton(context, (KNXImageButton) mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXBlinds) {
    	 controlView = new MyBlinds(context, null, (KNXBlinds)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSwitch) {
    	  //controlView = new MySwitch(context, (KNXSwitch)mKNXControlBase);
    	  controlView = new MySwitchButton(context, null, (KNXSwitch)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSlider) {
    	  //controlView = new MySlider(context, (KNXSlider)mKNXControlBase);
      }else if (mKNXControlBase instanceof KNXMediaButton) {
    	  controlView = new MyMediaButton(context, (KNXMediaButton)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXValueDisplay) {
    	  controlView = new MyValueDisplay(context, null, (KNXValueDisplay)mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSliderSwitch) {
    	  controlView = new MySliderSwitch(context, (KNXSliderSwitch)mKNXControlBase); 
      } else if (mKNXControlBase instanceof KNXColorLight) {
    	  controlView = new MyColorLight(context, (KNXColorLight) mKNXControlBase);
      } else if (mKNXControlBase instanceof KNXSnapper) {
    	  controlView = new MySnapper(context, (KNXSnapper)mKNXControlBase); 
      } else if (mKNXControlBase instanceof KNXSnapperSwitch) {
    	  controlView = new MySnapperSwitch(context, (KNXSnapperSwitch)mKNXControlBase); 
      } else if (mKNXControlBase instanceof KNXSIPCall) {
    	  
      } 
	  
      return controlView; 
   } 
	
	public void setCurrentValue(ControlView controlView, String value) {
	  if (controlView instanceof MyButton) {
		  
      } else if (controlView instanceof MySwitchButton) {
		  
      } else if (controlView instanceof MySliderSwitch) {
		  
      }  else if (controlView instanceof MySnapperSwitch) {
		  
      }  
	}
}
