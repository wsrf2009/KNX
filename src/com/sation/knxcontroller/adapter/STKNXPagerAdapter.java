package com.sation.knxcontroller.adapter;

import java.util.ArrayList;

import com.sation.knxcontroller.widget.STKNXPage;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class STKNXPagerAdapter extends PagerAdapter {
	private final String TAG = "STKNXPagerAdapter";

	private ArrayList<STKNXPage> mPagerList;

	public STKNXPagerAdapter() {

	}
	
	public STKNXPagerAdapter(Context c) {
		this();
	}
	
//	public STKNXPagerAdapter(ArrayList<STKNXPage> list) {
//		this();
//
//		this.mPagerList = list;
//	}
	
	public void setAdapterSource(ArrayList<STKNXPage> list) {
		this.mPagerList = list;
	}

	@Override
	public int getCount() {
		if(null != this.mPagerList) {
			return this.mPagerList.size();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		
		if(null != this.mPagerList) {
			container.removeView(this.mPagerList.get(position));
		}
	}
	
	@Override  
    public CharSequence getPageTitle(int position) {
		/* 直接用适配器来完成标题的显示，所以从上面
		 * 可以看到，我们没有使用PagerTitleStrip。
		 * 当然你可以使用。 */
		if(null != this.mPagerList) {
			STKNXPage page = this.mPagerList.get(position);
			return page.getKNXPage().getText(); 
		} else {
			return null;
		}
    }
	
	@Override  
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);  
    }

	@Override  
    public Object instantiateItem(ViewGroup container, int position) {
		if(null != this.mPagerList) {
			STKNXPage page = this. mPagerList.get(position);
			container.addView(page, 0);
			return page;
		} else {
			return null;
		}
    }
}
