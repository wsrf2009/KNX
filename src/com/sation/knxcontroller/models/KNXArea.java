<<<<<<< HEAD
package com.sation.knxcontroller.models;

import java.io.Serializable; 
import java.util.List;

///楼层，区域或者空间： 比如 1楼，二楼，室外等
public class KNXArea extends KNXView implements Serializable {
	private static final long serialVersionUID = 1L;

	private String Symbol;
	public String getSymbol() {
		return Symbol;
	}

	private String PinCode;
	public String getPinCode() {
		return PinCode;
	}

	private List<KNXRoom> Rooms;
	public List<KNXRoom> getRooms() {
		return Rooms;
	}
}
=======
package com.sation.knxcontroller.models;

import java.io.Serializable; 
import java.util.List;

///楼层，区域或者空间： 比如 1楼，二楼，室外等
public class KNXArea extends KNXView implements Serializable {
	private static final long serialVersionUID = 1L;

	private String Symbol;
	public String getSymbol() {
		return Symbol;
	}

	private String PinCode;
	public String getPinCode() {
		return PinCode;
	}

	private List<KNXRoom> Rooms;
	public List<KNXRoom> getRooms() {
		return Rooms;
	}
}
>>>>>>> SationCentralControl(10inch)
