package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.LookAboutList;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.MyViewHolder;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.MyText;

import java.util.HashMap;
import java.util.List;

/**
 * Created by fanxl2 on 2016/8/16.
 */
public class LookAboutAdapter extends BaseExpandableListAdapter {

	private boolean canSelect;

	private List<LookAboutList> datas;
	private LayoutInflater inflater;
	private HashMap<String, LookAboutListDetailBo> selectParam;
	private Context mContext;
	private DrawableRequestBuilder<String> requestBuilder;
	private boolean isLimit;

	public LookAboutAdapter(List<LookAboutList> datas, Activity context){
		canSelect = false;
		this.datas=datas;
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		requestBuilder = GlideLoad.init(context);
		selectParam = new HashMap<>();
		isLimit = true;
	}

	public void setLimit(boolean isLimit){
		this.isLimit=isLimit;
	}

	public void clearSelect(){
		selectParam.clear();
	}

	public HashMap<String, LookAboutListDetailBo> getSelctParam(){
		return selectParam;
	}

	public void setDatas(List<LookAboutList> datas){
		this.datas=datas;
	}

	@Override
	public int getGroupCount() {
		return datas.size();
	}

	@Override
	public int getChildrenCount(int i) {
		return datas.get(i).getList().size();
	}

	@Override
	public LookAboutList getGroup(int i) {
		return datas.get(i);
	}

	@Override
	public LookAboutListDetailBo getChild(int groupPosition, int childP) {

		return datas.get(groupPosition).getList().get(childP);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childP) {
		return childP;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int i, boolean b, View convertView, ViewGroup parent) {

		View view = convertView;
		if (view==null){
			view = inflater.inflate(R.layout.item_el_group, parent, false);
		}

		TextView group_tv_name = MyViewHolder.get(view, R.id.group_tv_name);
		group_tv_name.setText(getGroup(i).getEstateName());

		return view;
	}

	@Override
	public View getChildView(int parentP, int childP, boolean b, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view==null){
			view = inflater.inflate(R.layout.item_el_child, parent, false);
		}
		LookAboutListDetailBo childItem = getChild(parentP, childP);
		TextView item_child_title = MyViewHolder.get(view, R.id.item_child_title);
		item_child_title.setText(childItem.getTitle());

		ImageView item_child_img = MyViewHolder.get(view, R.id.item_child_img);

		String imgUrl = childItem.getDefaultImage();
		if (TextUtils.isEmpty(imgUrl)) {
			imgUrl = AppContents.POST_DEFAULT_IMG_URL;
		}

		GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUrl)
				.into(item_child_img));

		TextView item_child_house = MyViewHolder.get(view, R.id.item_child_house);
		item_child_house.setText(childItem.getRoomCount()+"室"+childItem.getHallCount()+"厅 | "+ MyUtils.formatHouseArea(childItem.getGArea())+"㎡ | "+childItem.getDirection());

		MyText item_child_money = MyViewHolder.get(view, R.id.item_child_money);
		if ("S".equalsIgnoreCase(childItem.getPostType())){
			item_child_money.setLeftAndRight(MyUtils.format2String(childItem.getSalePrice()/10000)+"", "万");
		}else {
			item_child_money.setLeftAndRight(MyUtils.format2String(childItem.getRentPrice())+"", "元/月");
		}

		View off_view_bg = MyViewHolder.get(view, R.id.off_view_bg);
		TextView off_view_text = MyViewHolder.get(view, R.id.off_view_text);

		CheckBox item_child_select = MyViewHolder.get(view, R.id.item_child_select);
		item_child_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

				if (compoundButton.isPressed()){
					checkClick(item_child_select, isChecked, parentP, childP, childItem);
				}else {
					if (isLimit){
						if (isChecked){
							selectParam.put(parentP+"_"+childP, childItem);
						}else {
							selectParam.remove(parentP+"_"+childP);
						}
					}
				}
			}
		});

		item_child_select.setEnabled(true);
		item_child_select.setButtonDrawable(mContext.getResources().getDrawable(R.drawable.cb_bt_selector));
		off_view_bg.setVisibility(View.GONE);
		off_view_text.setVisibility(View.GONE);

		if (childItem.isIsOnline()){
			item_child_select.setEnabled(true);
			item_child_select.setButtonDrawable(mContext.getResources().getDrawable(R.drawable.cb_bt_selector));
			off_view_bg.setVisibility(View.GONE);
			off_view_text.setVisibility(View.GONE);
		}else {
			off_view_bg.setVisibility(View.VISIBLE);
			off_view_text.setVisibility(View.VISIBLE);
			if (canSelect) {
				item_child_select.setEnabled(true);
				item_child_select.setButtonDrawable(mContext.getResources().getDrawable(R.drawable.cb_bt_selector));
			} else {
				item_child_select.setButtonDrawable(mContext.getResources().getDrawable(R.drawable.ck_round_disable));
				item_child_select.setEnabled(false);
			}
		}

		if (selectParam.get(parentP+"_"+childP)==null){
			item_child_select.setChecked(false);
		}else {
			item_child_select.setChecked(true);
		}

		return view;
	}

	@Override
	public boolean isChildSelectable(int i, int i1) {
		return true;
	}

	public void setCanSelect(boolean canSelect) {
		this.canSelect = canSelect;
	}

	public void checkClick(CheckBox item_child_select, boolean isChecked, int parentP, int childP, LookAboutListDetailBo childItem){
		if (selectParam.size()==3 && isLimit){
			if (isChecked && isLimit){
				Toast.makeText(mContext, "最多只能选择三套房源", Toast.LENGTH_SHORT).show();
				item_child_select.setChecked(false);
			}else {
				selectParam.remove(parentP+"_"+childP);
			}
		}else {
			if (isChecked){
				selectParam.put(parentP+"_"+childP, childItem);
			}else {
				selectParam.remove(parentP+"_"+childP);
			}
		}
	}
}
