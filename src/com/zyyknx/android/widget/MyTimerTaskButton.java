/**
 * 
 */
package com.zyyknx.android.widget;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.zyyknx.android.R;
import com.zyyknx.android.control.KNXTimerButton;
import com.zyyknx.android.control.TimingTaskItem;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author wangchunfeng
 *
 */
public class MyTimerTaskButton extends ControlView implements Parcelable {
	private Context mContext;
	private KNXTimerButton mKNXTimerTaskButton;
	private Button button;

	/**
	 * @param context
	 */
	public MyTimerTaskButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attr
	 */
	public MyTimerTaskButton(Context context, AttributeSet attr, KNXTimerButton mKNXTimerTaskButton) {
		super(context, attr);
		this.setKNXControl(mKNXTimerTaskButton);
		this.mKNXTimerTaskButton = mKNXTimerTaskButton;
		this.setId(mKNXTimerTaskButton.getId());
		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.icon_timer_task_button_widge, this, true);
		 
		button = (Button)v.findViewById(R.id.buttonIconTimerTaskWidgeButton);
		button.setText(mKNXTimerTaskButton.getText());
	}

	/**
	 * @param context
	 * @param attr
	 * @param defStyle
	 */
	public MyTimerTaskButton(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void setOnClickListener(OnClickListener mOnClickListener) {
		button.setOnClickListener(mOnClickListener);
	}
	
	private ArrayList<TimingTaskItem> timingTaskList;
	
	/**  
     * 保存文件  
     * @param fileName 文件名称  
     * @param content  文件内容  
     * @throws IOException  
     */  
    public void saveTimerTaskList() throws IOException {  
        //以私有方式读写数据，创建出来的文件只能被该应用访问  
        FileOutputStream fileOutputStream = mContext.openFileOutput(String.valueOf(this.getId()), Context.MODE_PRIVATE);  
        ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);  
        oos.writeObject(timingTaskList);//timingTaskList is an Instance of TableData;   
    } 
    
    /**  
     * 读取文件内容  
     * @param fileName 文件名称  
     * @return 文件内容  
     * @throws IOException  
     * @throws ClassNotFoundException 
     */  
    @SuppressWarnings("unchecked")
	public void readTimerTaskList() throws IOException, ClassNotFoundException {  
        FileInputStream fileInputStream = mContext.openFileInput(String.valueOf(this.getId()));  
        ObjectInputStream ois = new ObjectInputStream(fileInputStream);  
        try {
			timingTaskList = (ArrayList<TimingTaskItem>)ois.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}  

}
