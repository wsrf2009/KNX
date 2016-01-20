package com.zyyknx.android.models;

import java.io.Serializable;
import java.util.List; 

public class KNXPage extends KNXContainer implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	//界面上摆放的表格，控件容器
	private List<KNXGrid> Grids;
    public List<KNXGrid> getGrids() {
		return Grids;
	} 
	public void setGrids(List<KNXGrid> grids) {
		Grids = grids;
	}
	
	//背景图片，如果有图片，则优先显示
	private String BackgroudImage;
	public String getBackgroudImage() {
		return BackgroudImage;
	} 
	public void setBackgroudImage(String backgroudImage) {
		BackgroudImage = backgroudImage;
	} 
}
