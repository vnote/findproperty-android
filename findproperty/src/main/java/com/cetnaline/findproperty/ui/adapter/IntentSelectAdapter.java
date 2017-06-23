package com.cetnaline.findproperty.ui.adapter;

import android.content.Context;

import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.widgets.SelectTagLayout;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/27.
 */
public class IntentSelectAdapter extends com.zhy.adapter.recyclerview.CommonAdapter<DropBo> {

	private int selectedP;

	public IntentSelectAdapter(Context context, int layoutId, List<DropBo> datas) {
		super(context, layoutId, datas);
	}

	public void setSelectedP(int selectedP) {
		this.selectedP = selectedP;
	}

	@Override
	protected void convert(ViewHolder holder, DropBo dropBo, int position) {
		SelectTagLayout intent_select_tag = (SelectTagLayout) holder.getConvertView();
		if (selectedP == position){
			intent_select_tag.select(true);
		}else {
			intent_select_tag.select(false);
		}
		intent_select_tag.setText(dropBo.getText());
	}
}
