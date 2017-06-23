package com.cetnaline.findproperty.widgets.dropdown;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.util.SparseBooleanArray;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;

import java.util.List;

/**
 * 多选Adapter
 * Created by fanxl2 on 2016/8/5.
 */
public class DropMultiAdapter extends CommonAdapter<DropBo> {

	private SparseBooleanArray sparseArray;

	public DropMultiAdapter(Context context, List<DropBo> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	public void select(int position) {
		if (sparseArray!=null){
			sparseArray.put(position, !sparseArray.get(position, false));
			notifyDataSetChanged();
		}
	}

	public void selectSingle(int position){
		sparseArray.clear();
		sparseArray.put(position, !sparseArray.get(position, false));
		notifyDataSetChanged();
	}

	public void clear(){
		sparseArray.clear();
		notifyDataSetChanged();
	}

	public void setData(List<DropBo> datas, SparseBooleanArray sparseArray){
		setData(datas);
		this.sparseArray=sparseArray;
	}

	public boolean positionIsSelected(int position){
		if (sparseArray==null)return false;
		return sparseArray.get(position);
	}

	private boolean isSelected;

	@Override
	public void convert(ViewHolder helper, DropBo item) {
		helper.setText(R.id.item_drop_tv, item.getText());
		AppCompatCheckedTextView item_drop_tv = helper.getView(R.id.item_drop_tv);

		if (sparseArray==null){
			isSelected = false;
		}else {
			isSelected = sparseArray.get(helper.getPosition(), false);
		}
		item_drop_tv.setChecked(isSelected);
	}
}
