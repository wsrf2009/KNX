/**
 * 
 */
package com.sation.knxcontroller.models;

import com.sation.knxcontroller.widget.ControlView;

/**
 * @author wangchunfeng
 *
 */
public class AddressBit1Task {
	private KNXGroupAddress address;
	/**
	 * @param context
	 */
	public AddressBit1Task(KNXGroupAddress address) {
		
		this.address = address;
	}
	
	public void toggleSwitchOpen() {
		ControlView.sendDataToAddress(address, String.valueOf(1));
	}

	public void toggleSwitchClose() {
		ControlView.sendDataToAddress(address, String.valueOf(0));
	}
}
