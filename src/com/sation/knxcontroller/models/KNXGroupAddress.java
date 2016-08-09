package com.sation.knxcontroller.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sation.knxcontroller.util.Log;

public class KNXGroupAddress implements Serializable, Cloneable {
	private final String TAG = "KNXGroupAddress";
	private static final long serialVersionUID = 1L;
	
	//ETS中设备的ID， ETS自动分配
	private String Id;  
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	
	//ETS中设备用户指定的名称
	private String Name;  
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	
	//ETS 地址
	private Short KnxAddress;  
	public Short getKnxAddress() {
		return KnxAddress;
	}
	public void setKnxAddress(Short mKnxAddress) {
		KnxAddress = mKnxAddress;
	}
	
	private String KnxMainNumber;
	public String getKnxMainNumber() {
		return KnxMainNumber;
	}
	public void setKnxMainNumber(String number) {
		KnxMainNumber = number;
	}
	
	private String KnxSubNumber;
	public String getKnxSubNumber() {
		return KnxSubNumber;
	}
	public void setKnxSubNumber(String number) {
		KnxSubNumber = number;
	}
	
	//ETS 数据类型
	private int Type; 
	public int getType() {
		return Type;
	} 
	public void setType(int type) {
		Type = type;
	}
	 
	private String DPTName;
	public String getDPTName() {
		return DPTName;
	}
	public void setDPTName(String dptName) {
		DPTName = dptName;
	}
 
	private boolean IsCommunication;
	public boolean getIsCommunication() {
		return IsCommunication;
	} 
	public void setIsCommunication(boolean isCommunication) {
		IsCommunication = isCommunication;
	}
	
	private boolean IsRead; 
	public boolean getIsRead() {
		return IsRead;
	} 
	public void setIsRead(boolean isRead) {
		IsRead = isRead;
	} 
	
	private boolean IsWrite; 
	public boolean getIsWrite() {
		return IsWrite;
	} 
	public void setIsWrite(boolean isWrite) {
		IsWrite = isWrite;
	}
	
	private boolean IsTransmit; 
	public boolean getIsTransmit() {
		return IsTransmit;
	} 
	public void setIsTransmit(boolean isTransmit) {
		IsTransmit = isTransmit;
	} 
	
	private boolean IsUpgrade; 
	public boolean getIsUpgrade() {
		return IsUpgrade;
	}
	public void setIsUpgrade(boolean isUpgrade) {
		IsUpgrade = isUpgrade;
	}
	
	private int Priority; 
	public int getPriority() {
		return Priority;
	} 
	public void setPriority(int priority) {
		Priority = priority;
	} 
	
	private String DefaultValue ;  
	public String getDefaultValue () {
		return DefaultValue ;
	}
	public void setDefaultValue(String defaultValue ) {
		DefaultValue  = defaultValue ;
	}
	
	private short ReadTimeSpan; 
	public short getReadTimeSpan() {
		return ReadTimeSpan;
	} 
	public void setReadTimeSpan(short readTimeSpan) {
		ReadTimeSpan = readTimeSpan;
	}
	
	private String Tip;
	public String getTip() {
		return this.Tip;
	}
	public void setTip(String tip) {
		this.Tip = tip;
	}
	
	private List<KNXGroupAddressAction> Actions;
	public List<KNXGroupAddressAction> getAddressAction() {
		return Actions;
	}
	public void setAddressAction(List<KNXGroupAddressAction> actions) {
		Actions = actions;
	}
	
	@Override  
    public KNXGroupAddress clone() {  
		KNXGroupAddress address = null;
		
		try {
			address = (KNXGroupAddress)super.clone();
			List<KNXGroupAddressAction> newActions = new ArrayList<KNXGroupAddressAction>();
			for(KNXGroupAddressAction action : address.getAddressAction()) {
				newActions.add(action.clone());
			}
			
			address.setAddressAction(newActions);
		} catch (CloneNotSupportedException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		
		return address;
	}
}
