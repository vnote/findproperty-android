package com.cetnaline.findproperty.widgets.dropdown;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckedTextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;

import java.util.List;

/**
 * 单选的适配器
 * Created by fanxl2 on 2016/8/5.
 */
public class DropAreaAdapter extends CommonAdapter<GScope> {

	private int selectedPos = -1;
	private boolean isRight;

	public DropAreaAdapter(Context context, List<GScope> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	public void setRight(boolean right) {
		isRight = right;
	}

	@Override
	public void convert(ViewHolder helper, GScope item) {
		helper.setText(R.id.item_drop_tv, item.getGScopeName());
		AppCompatCheckedTextView item_drop_tv = helper.getView(R.id.item_drop_tv);
		if (isRight){
			item_drop_tv.setBackgroundColor(Color.parseColor("#eeeeee"));
		}else {
			item_drop_tv.setBackgroundResource(R.drawable.area_selector_bg);
		}
		item_drop_tv.setChecked(selectedPos==helper.getPosition());
	}

	public void select(int position) {
		selectedPos = position;
		notifyDataSetChanged();
	}

	public void setData(List<GScope> datas, int selectedPos){
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
