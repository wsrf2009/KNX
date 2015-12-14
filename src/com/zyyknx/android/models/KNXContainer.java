package com.zyyknx.android.models;

import java.io.Serializable;
import java.util.List;

import com.zyyknx.android.control.KNXControlBase;

public class KNXContainer  extends KNXView implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	//当前的格子划分为几行
	private int RowCount;
	public int getRowCount() {
		return RowCount;
	} 
	public void setRowCount(int rowCount) {
		RowCount = rowCount;
	}
	
	//当前的格子划分为几列
	private int ColumnCount;
	public int getColumnCount() {
		return ColumnCount;
	} 
	public void setColumnCount(int columnCount) {
		ColumnCount = columnCount;
	}
	
	 //摆放在界面上的子控件 
	private List<KNXControlBase> Controls;
    public List<KNXControlBase> getControls() {
		return Controls;
	} 
	public void setControls(List<KNXControlBase> controls) {
		Controls = controls;
	}
}
