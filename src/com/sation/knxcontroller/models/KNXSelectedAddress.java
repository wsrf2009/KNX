package com.sation.knxcontroller.models;

import java.io.Serializable;

public class KNXSelectedAddress implements Serializable {
	private static final long serialVersionUID = 1L;

	private String Id ;  
	public String getId () {
		return Id ;
	}
	public void setId (String id ) {
		Id = id;
	}
	
	private String Name;  
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	
	// 数据类型
	private int Type ;  
	public int getType () {
		return Type ;
	}
	public void setType (int type ) {
		Type = type;
	}
	
	// 默认值
	private String DefaultValue  ;  
	public String getDefaultValue  () {
		return DefaultValue ;
	}
	public void setDefaultValue(String defaultValue  ) {
		DefaultValue = defaultValue;
	}
}
