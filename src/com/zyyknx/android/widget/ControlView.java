package com.zyyknx.android.widget;

import java.util.Map;
import java.util.Timer;  

import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.models.KNXDataType;
import com.zyyknx.android.models.KNXGroupAddress;
import com.zyyknx.android.models.KNXSelectedAddress;
import com.zyyknx.android.services.RestartService;
import com.zyyknx.android.util.ByteUtil;
import com.zyyknx.android.util.KNX0X01Lib;
import com.zyyknx.android.util.StringUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;

public class ControlView extends ComponentView {
	/** The repeat send command timer. */
	private Timer timer;
	private Context context;
	 
	protected ControlView(Context context) {
		super(context); 
		
		//this.setMinimumWidth(R.dimen.control_item_min_width);
		
		this.context = context;
	}
	
	protected ControlView(Context context, AttributeSet attr) {
		super(context, attr);
		
		//this.setMinimumWidth(R.dimen.control_item_min_width); 
		
		this.context = context;
	}
	
	protected ControlView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
	}
	
	
	public boolean sendCommandRequest(final Map<String ,KNXSelectedAddress> mETSID,final String pData,final boolean isSendDefaultValue,final ICallBack icallBack) {
		try  {
			
			if(this.getKNXControl().getHasTip()) { 
				new PromptDialog.Builder(context)
				.setTitle("提示") 
				.setIcon(R.drawable.launcher)
				//.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				.setMessage(this.getKNXControl().getTip()) 
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
				
				Log.i("SliderSwitch", String.format("%s", Thread.currentThread().getStackTrace()[2].getMethodName()));
				
				 if(icallBack != null) {
					icallBack.onCallBack();
				 }
			
				 sendCommand(mETSID, pData, isSendDefaultValue);
				 
				 return true;
			}
			
			
		} catch (Exception e) {
		   Log.d("TEST", "发送数据出错："+ e.getMessage());  
		   return false;
	    }
	}
	
	/**
    * Send command request to controller by command type.
    * 
    * @param commandType the command type
    * 
    * @return true, if successful
    */
   public boolean sendCommandRequest(final String mETSID,final String pData,final boolean isSendDefaultValue,final ICallBack icallBack) {
	   try  {
		   
		   if(this.getKNXControl().getHasTip()) { 
				new PromptDialog.Builder(context)
				.setTitle("提示") 
				.setIcon(R.drawable.launcher)
				//.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				.setMessage(this.getKNXControl().getTip()) 
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
								//回调实现  
								if(icallBack != null) {
									icallBack.onCallBack();
								}
								
								 if(!StringUtil.isEmpty(mETSID))
									{
										String sendData = pData;

									    Map<String, Integer> mGroupAddressIndexMap = ZyyKNXApp.getInstance().getGroupAddressIndexMap();
									    Map<Integer, KNXGroupAddress> mGroupAddressMap = ZyyKNXApp.getInstance().getGroupAddressMap();
										 
										String[] mETSIDArray;  
									
										if(mETSID.split("\\,").length > 1) { 
											mETSIDArray = mETSID.split("\\,"); 
											for (int i = 0; i < mETSIDArray.length; i++) {
												int index = mGroupAddressIndexMap.get(mETSIDArray[i]);
												
												//if(mKNXGroupAddress.getType() == KNXDataType.Bit1) {  
													//byte[] byteArray = ByteUtil.getBytes(Integer.valueOf(pData)); 
													if(isSendDefaultValue) {
													    KNXGroupAddress mKNXGroupAddress = mGroupAddressMap.get(index);
													    sendData = String.valueOf(mKNXGroupAddress.getDefaultValue());
													}
													Log.d("TEST", "当前发送的索引号为"+ index +";值为："+ sendData); 
												
													byte[] byteArray = new byte[1];
													byteArray[0] = (byte)(0xff & Integer.valueOf(sendData));
													int x = KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length);
												 
													
											}
										} else {
											int index = mGroupAddressIndexMap.get(mETSID);
											//KNXGroupAddress mKNXGroupAddress = mGroupAddressMap.get(index);
											//if(mKNXGroupAddress.getType() == KNXDataType.Bit1) { 
											//byte[] byteArray = ByteUtil.getBytes(Integer.valueOf(pData));
											
											if(isSendDefaultValue) {
											    KNXGroupAddress mKNXGroupAddress = mGroupAddressMap.get(index);
											    sendData = String.valueOf(mKNXGroupAddress.getDefaultValue());
											}
											Log.d("TEST", "当前发送的索引号为"+ index +";值为："+ sendData);  
											
											byte[] byteArray = new byte[1]; 
											byteArray[0] = (byte)(0xff & Integer.valueOf(sendData));
											int x = KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length);
											  
										} 
									}
							}
						})
				.show(); 
				return true;
		   }  else { 
		   
			  //回调实现  
			 if(icallBack != null) {
				icallBack.onCallBack();
			 }
			   
			   if(!StringUtil.isEmpty(mETSID))
				{
					String sendData = pData;

				    Map<String, Integer> mGroupAddressIndexMap = ZyyKNXApp.getInstance().getGroupAddressIndexMap();
				    Map<Integer, KNXGroupAddress> mGroupAddressMap = ZyyKNXApp.getInstance().getGroupAddressMap();
					 
					String[] mETSIDArray;  
				
					if(mETSID.split("\\,").length > 1) { 
						mETSIDArray = mETSID.split("\\,"); 
						for (int i = 0; i < mETSIDArray.length; i++) {
							int index = mGroupAddressIndexMap.get(mETSIDArray[i]);
							
							//if(mKNXGroupAddress.getType() == KNXDataType.Bit1) {  
								//byte[] byteArray = ByteUtil.getBytes(Integer.valueOf(pData)); 
								if(isSendDefaultValue) {
								    KNXGroupAddress mKNXGroupAddress = mGroupAddressMap.get(index);
								    sendData = String.valueOf(mKNXGroupAddress.getDefaultValue());
								}
								Log.d("TEST", "当前发送的索引号为"+ index +";值为："+ sendData); 
							
								byte[] byteArray = new byte[1];
								byteArray[0] = (byte)(0xff & Integer.valueOf(sendData));
								int x = KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length); 
						}
					} else {
						int index = mGroupAddressIndexMap.get(mETSID);
						//KNXGroupAddress mKNXGroupAddress = mGroupAddressMap.get(index);
						//if(mKNXGroupAddress.getType() == KNXDataType.Bit1) { 
						//byte[] byteArray = ByteUtil.getBytes(Integer.valueOf(pData));
						
						if(isSendDefaultValue) {
						    KNXGroupAddress mKNXGroupAddress = mGroupAddressMap.get(index);
						    sendData = String.valueOf(mKNXGroupAddress.getDefaultValue());
						}
						Log.d("TEST", "当前发送的索引号为"+ index +";值为："+ sendData);  
						
						byte[] byteArray = new byte[1]; 
						byteArray[0] = (byte)(0xff & Integer.valueOf(sendData));
						int x = KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length);
						 
		                 
					} 
				}
		      return true;
		   }
	   } catch (Exception e) {
		   return false;
	   }
   }

   /**
    * Cancel repeat send command.
    */
   public void cancelTimer() {
      if (timer != null) {
         timer.cancel();
      }
      timer = null;
   }

   public void setTimer(Timer timer) {
      this.timer = timer;
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
	
	final public static void sendCommand(final Map<String ,KNXSelectedAddress> mETSID, final String pData, final boolean isSendDefaultValue) {
		String sendData = pData;
		if(mETSID != null && mETSID.size() > 0) {
			 Map<String, Integer> mGroupAddressIndexMap = ZyyKNXApp.getInstance().getGroupAddressIndexMap();
			for(Map.Entry<String, KNXSelectedAddress> entry:mETSID.entrySet()) {
			     KNXSelectedAddress mKNXSelectedAddress = entry.getValue();
			     int index = mGroupAddressIndexMap.get(mKNXSelectedAddress.getId());
			     if(isSendDefaultValue) {  
			    	 sendData = String.valueOf(mKNXSelectedAddress.getDefaultValue());
				 }
				 Log.d("TEST", "当前发送的索引号为"+ index +";值为："+ sendData);
				
				 byte[] byteArray = new byte[1]; 
				 byteArray[0] = (byte)(0xff & Integer.valueOf(sendData));
				 int x = KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length); 
				
			}   
		}
	}
	
	final public static void sendDataToAddress(final KNXGroupAddress address, final String data) {
		Map<String, Integer> mGroupAddressIndexMap = ZyyKNXApp.getInstance().getGroupAddressIndexMap();
		int index = mGroupAddressIndexMap.get(address.getId());
		
		byte[] byteArray = new byte[1];
		byteArray[0] = (byte)(0xff & Integer.valueOf(data));
		int x = KNX0X01Lib.USetAndTransmitObject(index, byteArray, byteArray.length);
	}
}
