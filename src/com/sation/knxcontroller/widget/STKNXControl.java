package com.sation.knxcontroller.widget;

import java.util.Map;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXControlBase;
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
		String sendData = pData;
		if(mETSID != null && mETSID.size() > 0) {
			Map<String, KNXGroupAddress> mGroupAddressIdMap = STKNXControllerApp.getInstance().getGroupAddressIdMap();
			
			for(Map.Entry<String, KNXSelectedAddress> entry:mETSID.entrySet()) {
			     KNXSelectedAddress mKNXSelectedAddress = entry.getValue();
			     String addressId = mKNXSelectedAddress.getId();
			     KNXGroupAddress address = mGroupAddressIdMap.get(addressId);
			     
			     if(isSendDefaultValue) {  
			    	 sendData = String.valueOf(mKNXSelectedAddress.getDefaultValue());
				 }

			     sendDataToAddress(address, sendData);

			}   
		}
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
						byteArray = DPT9.getBytes(Float.parseFloat(data));
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
					 
			Log.e("STKNXControl", "pData==>"+data+" add type:"+KNXDataType.values()[type]);

			int index = mGroupAddressIndexMap.get(addressId);
			int len = KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length, 1);
			
//			Log.e("STKNXControl", "pData==>"+data+" add type:"+KNXDataType.values()[type]+" len:"+len);
		}
	}
	
	final public static void readDataFromAddress(final KNXGroupAddress address) {
		if(null == address) {
			return;
		}
		
		Map<String, Integer> mGroupAddressIndexMap = STKNXControllerApp.getInstance().getGroupAddressIndexMap();
		if(null != mGroupAddressIndexMap) {
			int index = mGroupAddressIndexMap.get(address.getId());
		
			Log.i(STKNXControllerConstant.DEBUG, "read address <<====== "+address.getName());
			
			KNX0X01Lib.USetAndRequestObject(index);
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
		 * @param iBack 接口类型
		 */
		public void setonClick(ICallBack mICallBack)
		{
			icallBack = mICallBack;
		}
}
