package com.cetnaline.findproperty.ui.adapter.diff;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.List;

/**
 * Created by fanxl2 on 2016/10/14.
 */

public class TestStringDiff extends DiffUtil.Callback {

	private List<String> mOldDatas, mNewDatas;

	public TestStringDiff(List<String> mOldDatas, List<String> mNewDatas) {
		this.mOldDatas = mOldDatas;
		this.mNewDatas = mNewDatas;
	}

	private static final String TAG = "TestStringDiff";

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
		boolean same = mOldDatas.get(oldItemPosition).equals(mNewDatas.get(newItemPosition));
		Log.i(TAG, "areItemsTheSame: "+same);
		return same;
	}

	@Override
	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

		String oldItem = mOldDatas.get(oldItemPosition);
		String newItem = mNewDatas.get(newItemPosition);

		if (!oldItem.equals(newItem)){
			return false;
		}
		return true;
	}

	@Nullable
	@Override
	public Object getChangePayload(int oldItemPosition, int newItemPosition) {

		String oldP = mOldDatas.get(oldItemPosition);
		String newP = mNewDatas.get(newItemPosition);

		Bundle payload = new Bundle();
		if (!oldP.equals(newP)){
			payload.putString("STR_NAME", newP);
		}

		if (payload.size()==0)return null;
		return payload;
	}
}
