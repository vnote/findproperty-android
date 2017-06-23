package com.cetnaline.findproperty.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/10.
 */
public class ParentAdapter extends CommonAdapter<DropBo> {

	private int selectedPosition;
	private Drawable selectedRightArrow;
	private Drawable normalRightArrow;

	public ParentAdapter(Context context, List<DropBo> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		selectedPosition = -1;
		selectedRightArrow = ContextCompat.getDrawable(context, R.drawable.ic_right_arrow_red_24dp);
		normalRightArrow = ContextCompat.getDrawable(context, R.drawable.ic_right_arrow_gray_24dp);
	}

	public void setSelectedPosition(int selectedPosition){
		this.selectedPosition=selectedPosition;
		notifyDataSetChanged();
	}

	@Override
	public void convert(ViewHolder helper, DropBo item) {
		TextView item_tv_text = helper.getView(R.id.item_tv_text);
		if (selectedPosition==helper.getPosition()){
			item_tv_text.setTextColor(Color.RED);
			item_tv_text.setCompoundDrawablesWithIntrinsicBounds(null, null, selectedRightArrow, null);
		}else {
			item_tv_text.setCompoundDrawablesWithIntrinsicBounds(null, null, normalRightArrow, null);
			item_tv_text.setTextColor(Color.BLACK);
		}
		item_tv_text.setText(item.getText());
	}

}
