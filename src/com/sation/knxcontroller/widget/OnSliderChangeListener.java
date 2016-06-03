package com.sation.knxcontroller.widget;

public interface OnSliderChangeListener {
	public void onProgressChanged(Slider slider, int progress, boolean fromUser); 
	public void onStartTrackingTouch(Slider slider); 
	public void onStopTrackingTouch(Slider slider);
}
