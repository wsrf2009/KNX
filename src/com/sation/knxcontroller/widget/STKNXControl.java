package com.sation.knxcontroller.widget;

import java.util.Map;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.knxdpt.DPT9;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXDataType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.widget.ControlView.ICallBack;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;

public class STKNXControl extends STKNXView {
	private KNXControlBase mKNXControlBase;
	private Context mContext;
	
	public STKNXControl(Context context, KNXControlBase control) {
		super(context, control);

		this.mKNXControlBase = control;
	}

	protected boolean sendCommandRequest(final Map<String ,KNXSelectedAddress> mETSID,final String pData,final boolean isSendDefaultValue,final ICallBack icallBack) {
		try  {
			
			if(this.mKNXControlBase.getHasTip()) { 
				new PromptDialog.Builder(this.mContext)
				.setTitle("提示") 
				.setIcon(R.drawable.launcher)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				.setMessage(this.mKNXControlBase.getTip()) 
				.setButton1("取消",  new PromptDialog.OnClickListener() {
							
					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.dismiss();  
					}
				})
				.setButton2("确认", new PromptDialog.OnClickListener() {
							
					@Override
					public void onClick(Dialog dialog, int which) { 
						dialog.dismiss(); 
						if(icallBack != null) {
							icallBack.onCallBack();
						}
								 
						sendCommand(mETSID, pData, isSendDefaultValue);
					}
				})
				.show(); 
				
				return false;
			} else {
				 if(icallBack != null) {
					icallBack.onCallBack();
				 }
			
				 sendCommand(mETSID, pData, isSendDefaultValue);
				 
				 return true;
			}
		} catch (Exception e) {
			Log.e("STKNXControl", e.getLocalizedMessage());
		   return false;
	    }
	}
  
	final protected static void sendCommand(final Map<String ,KNXSelectedAddress> mETSID, final String pData, final boolean isSendDefaultValue) {
		String sendData = pData;
		if(mETSID != null && mETSID.size() > 0) {
			Map<String, Integer> mGroupAddressIndexMap = STKNXControllerApp.getInstance().getGroupAddressIndexMap();
			Map<String, KNXGroupAddress> mGroupAddressIdMap = STKNXControllerApp.getInstance().getGroupAddressIdMap();
			
			for(Map.Entry<String, KNXSelectedAddress> entry:mETSID.entrySet()) {
			     KNXSelectedAddress mKNXSelectedAddress = entry.getValue();
			     String addressId = mKNXSelectedAddress.getId();

			     if(isSendDefaultValue) {  
			    	 sendData = String.valueOf(mKNXSelectedAddress.getDefaultValue());
				 }
//			     transmitAddressIndexAndData(mKNXSelectedAddress.getId(), sendData);

				if((null != mGroupAddressIndexMap) && (mGroupAddressIndexMap.containsKey(addressId))) {
					int index = mGroupAddressIndexMap.get(addressId);
					KNXGroupAddress address = mGroupAddressIdMap.get(addressId);
					int type = Integer.parseInt(mKNXSelectedAddress.getType());
					byte[] byteArray = null;
					switch(KNXDataType.values()[type]){
					case Bit1:
					case Bit2:
					case Bit3:
					case Bit4:
					case Bit5:
					case Bit6:
					case Bit7:
					case Bit8:
						byteArray = new byte[1];
						byteArray[0] = (byte)(0xff & Integer.valueOf(sendData));
						break;
					case Bit16:
						if(address.getKnxMainNumber().equals(KNXDatapointType.DPT_9)) {
							byteArray = DPT9.getBytes(Float.parseFloat(sendData));
						} else {
							byteArray = new byte[2];
							String valString = sendData;
							int i = 2;
							while(--i >=0) {
								if(valString.length() > 2) {
									byteArray[i] = (byte)Integer.parseInt(valString.substring(valString.length()-2, sendData.length()));
									valString = valString.substring(0, sendData.length()-2);
								} else if (valString.length() > 0) {
									byteArray[i] = (byte)Integer.parseInt(valString);
									valString = "";
								} else {
									byteArray[i] = 0;
								}
							}
						}
						break;
							
						case Bit24:
							
							break;
							
						case Bit32:
							
							break;
							
						case Bit48:
							
							break;
							
						case Bit64:
							
							break;
							
						case Bit80:
							
							break;
							
						case Bit112:
							
							break;
							
						default:
							break;
						}
						if(KNXDataType.Bit1 == KNXDataType.values()[type]){
							
						}
						
						Log.e("STKNXControl", "pData==>"+pData+" isSendDefaultValue==>"+isSendDefaultValue +" add type:"+KNXDataType.values()[type]);

						KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length, 0);
					}
			}   
		}
	}
	
	final protected static void transmitAddressIndexAndData(String addressId, String data) {
		
		Log.i(STKNXControllerConstant.DEBUG, "addressId： "+addressId+" with value:"+data);
		
		if((null == addressId) || (null == data)) {
			return;
		}
		
		Map<String, Integer> mGroupAddressIndexMap = STKNXControllerApp.getInstance().getGroupAddressIndexMap();
		if((null != mGroupAddressIndexMap) && (mGroupAddressIndexMap.containsKey(addressId))) {
			int index = mGroupAddressIndexMap.get(addressId);
			byte[] byteArray = new byte[2];
			byteArray[0] = 0;
			byteArray[1] = 20;
//			byteArray[0] = (byte)(0xff & Integer.valueOf(data));
			KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length, 0);
		}
	}
}
