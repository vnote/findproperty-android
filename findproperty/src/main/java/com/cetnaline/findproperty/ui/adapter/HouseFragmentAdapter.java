package com.cetnaline.findproperty.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cetnaline.findproperty.api.bean.HouseBottomBean;
import com.cetnaline.findproperty.ui.fragment.HouseDetailFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanxl2 on 2016/7/29.
 */
public class HouseFragmentAdapter extends FragmentStatePagerAdapter {

	private ArrayList<Fragment> mFragmentList;
	private List<HouseBottomBean> itemDatas;
	private int houseType;

	public HouseFragmentAdapter(FragmentManager fm) {
		super(fm);
		mFragmentList = new ArrayList<>();
		itemDatas = new ArrayList<>();
	}

	public void setItems(List<HouseBottomBean> items, int houseType){
		this.itemDatas = items;
		this.houseType = houseType;
//		ArrayList<Fragment> fragments = new ArrayList<>();
//		for (int i = 0, size = itemDatas.size(); i < size; i++) {
//			HouseBottomBean houseBottomBean = items.get(i);
//			HouseDetailFragment fragment;
//			if (houseType==MapFragment.HOUSE_TYPE_NEW){
//				fragment =  HouseDetailFragment.getInstance(houseBottomBean.getEstExtId(), HouseDetailFragment.FROM_TYPE_MAP);
//			}else {
//				fragment =HouseDetailFragment.getInstance(houseBottomBean.getPostId(), HouseDetailFragment.FROM_TYPE_MAP, houseBottomBean.getHouseType());
//			}
//			fragments.add(fragment);
//		}
//		setFragmentList(fragments);

		notifyDataSetChanged();
	}

	private void setFragmentList(ArrayList<Fragment> fragmentList) {
		if(this.mFragmentList != null){
			mFragmentList.clear();
		}
		this.mFragmentList = fragmentList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.itemDatas.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
			HouseBottomBean houseBottomBean = itemDatas.get(position);
			HouseDetailFragment fragment;
			if (houseType==MapFragment.HOUSE_TYPE_NEW){
				fragment =  HouseDetailFragment.getInstance(houseBottomBean.getEstExtId(), HouseDetailFragment.FROM_TYPE_MAP);
			}else {
				fragment =HouseDetailFragment.getInstance(houseBottomBean.getPostId(), HouseDetailFragment.FROM_TYPE_MAP, houseBottomBean.getHouseType());
			}
//		return mFragmentList.get(position);
		return fragment;
	}

	public List<HouseBottomBean> getDataItems() {
		return itemDatas;
	}
}



