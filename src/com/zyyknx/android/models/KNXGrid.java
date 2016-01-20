package com.zyyknx.android.models;

import java.io.Serializable; 

//布局
public class KNXGrid extends KNXContainer implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	//当前格子从哪一行开始
	private int Row;
	public int getRow() {
		return Row;
	} 
	public void setRow(int row) {
		Row = row;
	}
	
	//当前的表格从哪一列开始
	private int Column;
	public int getColumn() {
		return Column;
	} 
	public void setColumn(int column) {
		Column = column;
	}
	
	//占几个行
	private int RowSpan;
	public int getRowSpan() {
		return RowSpan;
	} 
	public void setRowSpan(int rowSpan) {
		RowSpan = rowSpan;
	}
	
	//占几列
	private int ColumnSpan;
	public int getColumnSpan() {
		return ColumnSpan;
	} 
	public void setColumnSpan(int columnSpan) {
		ColumnSpan = columnSpan;
	} 
	
	// grid 边框样式
	private String BorderStyle;
	public String getBorderStyle() {
		return BorderStyle;
	} 
	public void setBorderStyle(String borderStyle) {
		BorderStyle = borderStyle;
	} 
}
