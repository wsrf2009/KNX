package com.sation.knxcontroller.widget;

import java.util.Map;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.knxdpt.DPT14;
import com.sation.knxcontroller.knxdpt.DPT9;
import com.sation.knxcontroller.knxdpt.KNXDataType;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;

import android.app.Dialog;
import android.content.Context;

public class STKNXControl extends STKNXView {
	private final static String TAG = "STKNXControl";
	private KNXControlBase mKNXControlBase;
	private Context mContext;
	
	public STKNXControl(Context context, KNXControlBase control) {
		super(context, control);

		this.mContext = context;
		this.mKNXControlBase = control;
	}

	protected boolean sendCommandRequest(final Map<String ,KNXSelectedAddress> mETSID,final String pData,final boolean isSendDefaultValue,final ICallBack icallBack) {
		try  {
			
			if(EBool.Yes == this.mKNXControlBase.getHasTip()) { 
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
		new Thread(new Runnable(){

			@Override
			public void run() {
				String sendData = pData;
				if(mETSID != null && mETSID.size() > 0) {
					Map<String, KNXGroupAddress> mGroupAddressIdMap = STKNXControllerApp.getInstance().getGroupAddressIdMap();
					
					for(Map.Entry<String, KNXSelectedAddress> entry:mETSID.entrySet()) {
					     KNXSelectedAddress mKNXSelectedAddress = entry.getValue();
					     if(null != mKNXSelectedAddress) {
					    	 String addressId = mKNXSelectedAddress.getId();
					    	 if((null != addressId) && (null != mGroupAddressIdMap)) {
					    		 KNXGroupAddress address = mGroupAddressIdMap.get(addressId);
					     
					    		 if(isSendDefaultValue) {  
					    			 sendData = String.valueOf(mKNXSelectedAddress.getDefaultValue());
					    		 }

					    		 sendDataToAddress(address, sendData);
					    	 }
					     }
					}   
				}
			}
		}).start();
	}
	
	final public static void sendDataToAddress(final KNXGroupAddress address, final String data) {
		if((null == address) || (null == data)) {
			return;
		}

		Map<String, Integer> mGroupAddressIndexMap = STKNXControllerApp.getInstance().getGroupAddressIndexMap();
		String addressId = address.getId();
			
		if((null != mGroupAddressIndexMap) && (mGroupAddressIndexMap.containsKey(addressId))) {
			int type = address.getType();
			byte[] byteArray = null;
			switch(KNXDataType.values()[type]) {
				case Bit1:
				case Bit2:
				case Bit3:
				case Bit4:
				case Bit5:
				case Bit6:
				case Bit7:
				case Bit8:
					byteArray = new byte[1];
					byteArray[0] = (byte)(0xff & Integer.valueOf(data));
					break;
				case Bit16:
					if(address.getKnxMainNumber().equals(KNXDatapointType.DPT_9)) {
						byteArray = DPT9.float2bytes(Float.parseFloat(data));
					} else {
						byteArray = new byte[2];
						String valString = data;
						int i = 2;
						while(--i >=0) {
							if(valString.length() > 2) {
								byteArray[i] = (byte)Integer.parseInt(valString.substring(valString.length()-2, data.length()));
								valString = valString.substring(0, data.length()-2);
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
					if(address.getKnxMainNumber().equals(KNXDatapointType.DPT_14)) {
						byteArray = DPT14.float2bytes(Float.parseFloat(data));
					} else {
						
					}
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
			
			if(KNXDataType.Bit1 == KNXDataType.values()[type]) {
						
			}

			int index = mGroupAddressIndexMap.get(addressId);

			try {
				Log.i(TAG, address.getStringKnxAddress() + "===>" + data + " call");
				KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length, 0);
				Log.i(TAG, address.getStringKnxAddress() + "===>" + data);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	final public static void readDataFromAddress(final KNXGroupAddress address) {
		if(null == address) {
			return;
		}
		
		Map<String, Integer> mGroupAddressIndexMap = STKNXControllerApp.getInstance().getGroupAddressIndexMap();
		if(null != mGroupAddressIndexMap) {
			int index = mGroupAddressIndexMap.get(address.getId());
			
			try {
				KNX0X01Lib.USetAndRequestObject(index);
				
				Log.i(TAG, address.getStringKnxAddress());
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 一定一个接口
	 */
	public interface ICallBack{
		public void onCallBack();
	}
		
	/**
	 * 初始化接口变量
	 */
	ICallBack icallBack = null;
		
	/**
	 * 自定义控件的自定义事件
	 * @param
	 */
	public void setonClick(ICallBack mICallBack)
	{
		icallBack = mICallBack;
	}
}
