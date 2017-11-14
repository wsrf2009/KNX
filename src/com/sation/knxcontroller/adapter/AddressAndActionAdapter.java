/**
 * 
 */
package com.sation.knxcontroller.adapter;

import java.util.List;
import java.util.Map;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.control.TimingTaskItem.KNXGroupAddressAndAction;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXGroupAddressAction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import static java.lang.System.in;

/**
 * @author wangchunfeng
 *
 */
public class AddressAndActionAdapter extends BaseAdapter {
	private List<KNXGroupAddressAndAction> addressAndActionList;
	private Context mContext;

	/**
	 * 
	 */
	public AddressAndActionAdapter(Context context/*, List<KNXGroupAddressAndAction> list*/) {
//		this.addressList = list;
//		this.addressAndActionList = new ArrayList<KNXGroupAddressAndAction>();
		this.mContext = context;
	}
	
	public void setAddressAndActionList(List<KNXGroupAddressAndAction> list) {
		this.addressAndActionList = list;
	}
	
	public void clearAddressAndActionList() {
		if(null != this.addressAndActionList) {
			this.addressAndActionList.clear();
		}
	}
	
//	public void addAddressAndAction(KNXGroupAddressAndAction addressAndAction) {
//		for(KNXGroupAddressAndAction aaa : this.addressAndActionList) {
//			if(aaa.getAddress().getKnxAddress() == addressAndAction.getAddress().getKnxAddress()) {
//				return;
//			}
//		}
//
//		addressAndActionList.add(addressAndAction);
//	}
	
	public List<KNXGroupAddressAndAction> getAddressAndActionList() {
		return this.addressAndActionList;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if(null != this.addressAndActionList) {
			return this.addressAndActionList.size();
		} else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return this.addressAndActionList.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
		final ViewHolder holder;
		final Map<String, KNXGroupAddress> addrMap = STKNXControllerApp.getInstance().getGroupAddressIdMap();

		if(null == convertView) {
			holder = new ViewHolder();

			convertView = mLayoutInflater.inflate(R.layout.listview_item_layout_detail, null);
			holder.textViewAddress = (TextView)convertView.findViewById(R.id.listviewItemLayoutDetailTitle);
			holder.spinnerActions = (Spinner)convertView.findViewById(R.id.listviewItemLayoutDetailSpinnerAction);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		final KNXGroupAddressAndAction addressAndAction = this.addressAndActionList.get(position);
//		KNXGroupAddress address = addressAndAction.getAddress();
		KNXGroupAddress address = addrMap.get(addressAndAction.getAddressId());
		holder.textViewAddress.setText(address.getName()+"-"+address.getStringKnxAddress());
		
		int selectedIndex = -1;
		int count = 0;
		KNXGroupAddressAction currentAction = addressAndAction.getAction();
		
		/* Spinner 默认行为 */
		ArrayAdapter<String> actionSpinnerAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item);
		actionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		List<KNXGroupAddressAction> defaultActions = address.getAddressAction();
		if((null != defaultActions) && (address.getIsCommunication()) && (address.getIsWrite())) { // 该地址有可供选择的默认行为列表
			String actionName = null;
			if(null != currentAction) {
				actionName = currentAction.getName();
			}
			for(int i=0; i<defaultActions.size(); i++) {
				String name = defaultActions.get(i).getName();
				actionSpinnerAdapter.add(name);
				if(null != actionName) {
					if(actionName.equals(name)) { // 该地址设置的行为为默认行为，记录下索引号
						selectedIndex = i;
					}
				}
			}
			count = defaultActions.size();
		}
		
		if((address.getIsCommunication()) && (address.getIsRead())) {
			actionSpinnerAdapter.add(mContext.getResources().getString(R.string.get_status));
			count++;
			
			if(addressAndAction.getIsRead()) {
				selectedIndex = count-1;
			}
		}
		
		holder.spinnerActions.setAdapter(actionSpinnerAdapter);
		
		if(selectedIndex >= 0) {
			holder.spinnerActions.setSelection(selectedIndex, true);
		} else if (null != currentAction) {

		}
		
		holder.spinnerActions.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedActionName = (String)holder.spinnerActions.getSelectedItem();
//				for(KNXGroupAddressAction action : addressAndAction.getAddress().getAddressAction()) {
				for(KNXGroupAddressAction action : addrMap.get(addressAndAction.getAddressId()).getAddressAction()) {
					if (action.getName().equals(selectedActionName)) {
						addressAndAction.setAction(action);

						break;
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
			
		});

		return convertView;
	}
	
	public class ViewHolder {
		TextView textViewAddress;
		Spinner spinnerActions;
	}

}
