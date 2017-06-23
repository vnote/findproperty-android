package com.cetnaline.findproperty.widgets.dropdown;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckedTextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;

import java.util.List;

/**
 * 单选的适配器
 * Created by fanxl2 on 2016/8/5.
 */
public class DropTextAdapter extends CommonAdapter<DropBo> {

	private int selectedPos = -1;

	public DropTextAdapter(Context context, List<DropBo> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, DropBo item) {
		helper.setText(R.id.item_drop_tv, item.getText());
		AppCompatCheckedTextView item_drop_tv = helper.getView(R.id.item_drop_tv);
		item_drop_tv.setChecked(selectedPos==helper.getPosition());
	}

	public void select(int position) {
		selectedPos = position;
		notifyDataSetChanged();
	}

	public void setData(List<DropBo> datas, int selectedPos){
		this.selectedPos=selectedPos;
		setData(datas);
	}

	public void setSelected(int position){
		mDatas.get(position).setSelected(true);
	}

	public int getSelectedPos() {
		return selectedPos;
	}

}
