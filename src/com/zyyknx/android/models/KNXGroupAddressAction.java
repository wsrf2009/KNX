/**
 * 
 */
package com.zyyknx.android.models;

/**
 * @author wangchunfeng
 *
 */
public class KNXGroupAddressAction {

	/**
	 * 
	 */
	public KNXGroupAddressAction() {

	}
	
	private String Name;
	public void setName(String name) {
		this.Name = name;
	}
	public String getName() {
		return this.Name;
	}
	
	public class Bit1Action extends KNXGroupAddressAction {
		
		private int defaultValue;
		public void setDefaultValue(int value) {
			this.defaultValue = value;
		}
		public int getDefaultValue() {
			return this.defaultValue;
		}
	}
}


