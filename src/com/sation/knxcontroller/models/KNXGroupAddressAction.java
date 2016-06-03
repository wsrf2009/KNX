/**
 * 
 */
package com.sation.knxcontroller.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.util.Log;

/**
 * @author wangchunfeng
 *
 */
public class KNXGroupAddressAction implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	@Override  
    public KNXGroupAddressAction clone() {  
		KNXGroupAddressAction action = null;
		
		try {
			action = (KNXGroupAddressAction)super.clone();

		} catch (CloneNotSupportedException e) {
			Log.e(STKNXControllerConstant.DEBUG, e.getLocalizedMessage());
		}
		
		return action;
	}

	/**
	 * 
	 */
	public KNXGroupAddressAction(String name, int value) {
		this.Name = name;
		this.Value = value;
	}
	
	private String Name;
	public void setName(String name) {
		this.Name = name;
	}
	public String getName() {
		return this.Name;
	}

	private String Encoding;
	public void setEncoding(String encoding){
		this.Encoding = encoding;
	}
	public String getEncoding() {
		return this.Encoding;
	}
	
	private int Value;
	public void setValue(int value) {
		this.Value = value;
	}
	public int getValue() {
		return this.Value;
	}
	
	private boolean CanBeDelete;
	public void setCanBeDelete(boolean delete) {
		this.CanBeDelete = delete;
	}
	public boolean getCanBeDelete() {
		return this.CanBeDelete;
	}
}


