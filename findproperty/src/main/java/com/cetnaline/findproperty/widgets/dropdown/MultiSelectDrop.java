package com.cetnaline.findproperty.widgets.dropdown;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.widgets.FlowTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多选下拉框
 * Created by fanxl2 on 2016/8/5.
 */
public class MultiSelectDrop extends AbsDrop<DropBo> implements View.OnClickListener {

	private LinearLayout fl_parent;
	private ListView drop_lv_multi;
	private List<DropBo> datas;
	private LayoutInflater inflate;
	private Context mContext;
	private MultiDropAdapter adapter;
	private int itemWidth;

	public MultiSelectDrop(View anchor, Activity context, Drawable bg) {
		super(anchor, context);
		this.mContext=context;
		inflate = LayoutInflater.from(context);
		View view;

		int phoneWidth = MyUtils.getPhoneWidth(context);

		if (bg==null){
			view = inflate.inflate(R.layout.layout_drop_multi, null);
			itemWidth = (phoneWidth - MyUtils.dip2px(context, 50))/3;
		}else {
			view = inflate.inflate(R.layout.layout_drop_multi_map, null);
			itemWidth = (phoneWidth - MyUtils.dip2px(context, 90))/3;  //10 10 10 10 4*6
		}
		fl_parent = (LinearLayout) view.findViewById(R.id.fl_parent);
		view.findViewById(R.id.drop_bt_clear).setOnClickListener(this);
		view.findViewById(R.id.drop_bt_ok).setOnClickListener(this);
		fl_parent.setOnClickListener(this);
		drop_lv_multi = (ListView) view.findViewById(R.id.drop_lv_multi);
		datas = new ArrayList<>();
		selectedParams = new HashMap<>();
		tempParams = new HashMap<>();
		adapter = new MultiDropAdapter(mContext, datas, R.layout.item_drop_multi_select);
		drop_lv_multi.setAdapter(adapter);
		initPopWindow(view, bg);
	}

	@Override
	public void init(List<DropBo> arrayList) {
		this.datas=arrayList;
		adapter.setData(datas);
	}

	public class MultiDropAdapter extends CommonAdapter<DropBo> {

		public MultiDropAdapter(Context context, List<DropBo> mDatas, int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
		}

		@Override
		public void convert(ViewHolder holder, DropBo menuBean) {
			holder.setText(R.id.item_tv_title, menuBean.getText());

			FlowTag item_my_cb = holder.getView(R.id.item_my_cb);
			item_my_cb.removeAllViews();
			List<DropBo> list = menuBean.getChildrenList();

			if (menuBean.getType()==1){
				for(DropBo item : list){
					createCheckBox(item.getText(), item_my_cb, item);
				}
			}else {
				for(DropBo item : list){
					createCheckBoxSingle(item.getText(), item_my_cb, item);
				}
			}
		}
	}

	private Map<String, DropBo> selectedParams, tempParams;

	private void createCheckBoxSingle(String name, final FlowTag parent, DropBo item){
		final CheckBox cb = (CheckBox) inflate.inflate(R.layout.check_box, parent, false);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (!buttonView.isPressed()){
					return;
				}

				DropBo item = (DropBo) buttonView.getTag();
				if (item==null)return;
				if(isChecked){
					tempParams.put(item.getName(), item);
					parent.clearSelectedStatus(cb);
				}else{
					tempParams.remove(item.getName());
					selectedParams.remove(item.getName());
				}
			}
		});
		if (selectedParams.get(item.getName())!=null){
			DropBo db = selectedParams.get(item.getName());
			if (db.getText().equals(item.getText())){
				cb.setChecked(true);
			}else {
				cb.setChecked(false);
			}
		}else {
			cb.setChecked(false);
		}
		cb.setText(name);
		cb.setTag(item);

		cb.setWidth(itemWidth);
		parent.addView(cb);
	}

	private void createCheckBox(String name, final FlowTag parent, DropBo item){
		final CheckBox cb = (CheckBox) inflate.inflate(R.layout.check_box, parent, false);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				DropBo item = (DropBo) buttonView.getTag();
				if (item==null)return;
				if(isChecked){
					tempParams.put(item.getText(), item);
				}else{
					tempParams.remove(item.getText());
					selectedParams.remove(item.getText());
				}
			}
		});
		if (selectedParams.get(item.getText())!=null){
			cb.setChecked(true);
		}else {
			cb.setChecked(false);
		}
		cb.setText(name);
		cb.setTag(item);
		cb.setWidth(itemWidth);
		parent.addView(cb);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.fl_parent:
				dismiss();
				break;
			case R.id.drop_bt_ok:
				if (tempParams.size()>0 || selectedParams.size()>0){
					selectedParams.putAll(tempParams);
					DropBo[] results = new DropBo[selectedParams.size()];
					int i=0;
					for (DropBo dropBo : selectedParams.values()){
						results[i] = dropBo;
						i++;
					}
					dropComplete(true, 3, results);
				}else {
					dropComplete(true, 3, new DropBo[0]);
				}
//				dismiss();
				break;
			case R.id.drop_bt_clear:
				selectedParams.clear();
				tempParams.clear();
				adapter.notifyDataSetChanged();
				dropComplete(true, 3, new DropBo[0]);
				break;
		}
	}

	@Override
	protected void doBeforeShow() {
		super.doBeforeShow();
		adapter.notifyDataSetChanged();
	}

	public void resetSelectStatus(){
		selectedParams.clear();
		tempParams.clear();
	}

	public void resetSelectStatus(String name){
		selectedParams.remove(name);
		tempParams.remove(name);
	}
}
