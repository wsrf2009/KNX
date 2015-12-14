package com.zyyknx.android.models;

import java.io.Serializable; 
import java.util.List;

///楼层，区域或者空间： 比如 1楼，二楼，室外等
public class KNXArea extends KNXView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<KNXRoom> Rooms;
	public List<KNXRoom> getRooms() {
		return Rooms;
	} 
	public void setRooms(List<KNXRoom> rooms) {
		Rooms = rooms;
	} 

}
