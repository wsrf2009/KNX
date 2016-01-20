package com.zyyknx.android.models;

import java.io.Serializable;

public class KNXGroupAddress implements Serializable {
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
	
	/*
	//ETS 读取地址
	private Short ReadAddress;  
	public Short getReadAddress() {
		return ReadAddress;
	}
	public void setReadAddress(Short readAddress) {
		ReadAddress = readAddress;
	}
	*/
	
	//Type
	/*
	private KNXDataType Type; 
	public KNXDataType getType() {
		return Type;
	} 
	public void setType(KNXDataType type) {
		Type = type;
	}
	*/
	//ETS 数据类型
	private int Type; 
	public int getType() {
		return Type;
	} 
	public void setType(int type) {
		Type = type;
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
	
	private int Priority; 
	public int getPriority() {
		return Priority;
	} 
	public void setPriority(int priority) {
		Priority = priority;
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
	
	//电缆编号
	private String WireNumber;  
	public String getWireNumber() {
		return WireNumber;
	}
	public void setWireNumber(String wireNumber) {
		WireNumber = wireNumber;
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
	
}
