package com.cetnaline.findproperty.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/10.
 */
public class ChildAdapter extends CommonAdapter<DropBo> {

	private int selectedPosition;

	public ChildAdapter(Context context, List<DropBo> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		selectedPosition = -1;
	}

	public void setSelectedPosition(int selectedPosition){
		this.selectedPosition=selectedPosition;
		notifyDataSetChanged();
	}

	@Override
	public void convert(ViewHolder helper, DropBo item) {
		TextView item_tv_text = helper.getView(R.id.item_tv_text);
		item_tv_text.setCompoundDrawables(null, null, null, null);
		item_tv_text.setText(item.getText());
		if (selectedPosition==helper.getPosition()){
			item_tv_text.setTextColor(Color.RED);
		}else {
			item_tv_text.setTextColor(Color.BLACK);
		}
	}
}
