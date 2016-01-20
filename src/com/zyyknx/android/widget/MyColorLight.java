package com.zyyknx.android.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View; 
import android.view.ViewGroup;
import android.widget.Button;

import com.zyyknx.android.R; 
import com.zyyknx.android.control.KNXColorLight;

public class MyColorLight  extends ControlView {

	public final static long REPEAT_CMD_INTERVAL = 200;
	
	private KNXColorLight mKNXColorLight;
	 
	public MyColorLight(Context context) {
		super(context);
	}

	public MyColorLight(Context context, KNXColorLight mKNXColorLight) {
		super(context);
		this.mKNXColorLight = mKNXColorLight;
		setKNXControl(mKNXColorLight); 
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.color_light_widge, this, true);
		
		ColorPickerPanelView mWhite = (ColorPickerPanelView) findViewById(R.id.white_panel); 
	    ColorPickerPanelView mCyan = (ColorPickerPanelView) findViewById(R.id.cyan_panel);
	    ColorPickerPanelView mRed = (ColorPickerPanelView)  findViewById(R.id.red_panel);
	    ColorPickerPanelView mGreen = (ColorPickerPanelView)  findViewById(R.id.green_panel);
	    ColorPickerPanelView mYellow = (ColorPickerPanelView)  findViewById(R.id.yellow_panel);
	    mWhite.setColor(Color.WHITE);
	    mCyan.setColor(0xff33b5e5);
	    mRed.setColor(Color.RED);
	    mGreen.setColor(Color.GREEN);
	    mYellow.setColor(Color.YELLOW);  
	
		
		
		
		
		if (mKNXColorLight != null) { 
			initButton(mKNXColorLight);
		}
	}

	private void initButton(final KNXColorLight mKNXColorLight) { 
		  
	}

	private void sendCommand() {
		//sendCommandRequest("click");
	}
}