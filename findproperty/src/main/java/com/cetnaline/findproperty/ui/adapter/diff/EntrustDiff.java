package com.cetnaline.findproperty.ui.adapter.diff;

import android.support.v7.util.DiffUtil;

import com.cetnaline.findproperty.api.bean.MyEntrustBo;

import java.util.List;

/**
 * Created by fanxl2 on 2016/10/14.
 */

public class EntrustDiff extends DiffUtil.Callback {

	private List<MyEntrustBo> mOldDatas, mNewDatas;

	public EntrustDiff(List<MyEntrustBo> mOldDatas, List<MyEntrustBo> mNewDatas) {
		this.mOldDatas = mOldDatas;
		this.mNewDatas = mNewDatas;
	}

	@Override
	public int getOldListSize() {
		return mOldDatas==null?0:mOldDatas.size();
	}

	@Override
	public int getNewListSize() {
		return mNewDatas==null?0:mNewDatas.size();
	}

	@Override
	public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
		return mOldDatas.get(oldItemPosition).getEntrustID()==mNewDatas.get(newItemPosition).getEntrustID();
	}

	@Override
	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

		MyEntrustBo oldItem = mOldDatas.get(oldItemPosition);
		MyEntrustBo newItem = mNewDatas.get(newItemPosition);

		if (!oldItem.getEstateName().equals(newItem.getEstateName())){
			return false;
		}

		if (!oldItem.getCustomerMobile().equals(newItem.getCustomerMobile())){
			return false;
		}

		if (!oldItem.getCustomerName().equals(newItem.getCustomerName())){
			return false;
		}

		return true;
	}
}
