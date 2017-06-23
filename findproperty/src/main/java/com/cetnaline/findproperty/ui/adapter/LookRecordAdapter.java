package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.LookAboutList;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.ui.activity.LookAbout;
import com.cetnaline.findproperty.utils.DateUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.MyViewHolder;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.cetnaline.findproperty.widgets.MyText;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/16.
 */
public class LookRecordAdapter extends BaseExpandableListAdapter {

	private List<LookAboutList> datas;
	private LayoutInflater inflater;
	private DrawableRequestBuilder<String> requestBuilder;

	public static final int BT_TYPE_EVALUATE = 0;
	public static final int BT_TYPE_CALL = 1;
	public static final int BT_TYPE_MSG = 2;
	public static final int BT_TYPE_STAFF = 3;
	public static final int BT_TYPE_HOUSE = 4;

	private int lookType;

	public LookRecordAdapter(List<LookAboutList> datas, Activity context, int lookType){
		this.datas=datas;
		this.lookType=lookType;
		inflater = LayoutInflater.from(context);
		requestBuilder = GlideLoad.init(context);
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
	public View getGroupView(int position, boolean b, View convertView, ViewGroup parent) {

		View view = convertView;
		if (view==null){
			view = inflater.inflate(R.layout.item_look_parent, parent, false);
		}

		CircleImageView entrust_item_head = MyViewHolder.get(view, R.id.entrust_item_head);

		TextView entrust_item_code = MyViewHolder.get(view, R.id.entrust_item_code);
		TextView entrust_user_name = MyViewHolder.get(view, R.id.entrust_user_name);

		LookAboutList lookAboutList = getGroup(position);
		if (lookAboutList.getStaffDetail()==null){
			ApiRequest.getStaffDetail(lookAboutList.getLookStaff())
					.subscribe(staffDetail -> {

						lookAboutList.setStaffDetail(staffDetail);
						setStaffInfo(staffDetail, entrust_item_head, entrust_user_name, position);

					});
		}else {
			setStaffInfo(lookAboutList.getStaffDetail(), entrust_item_head, entrust_user_name, position);
		}

		entrust_item_code.setVisibility(View.VISIBLE);
		entrust_item_code.setText("YK"+lookAboutList.getPlanCode());

		MyViewHolder.get(view, R.id.entrust_item_call).setOnClickListener(view1 -> {
			if (buttonClickListener != null) {
				buttonClickListener.rightButtonClick(position, 0, BT_TYPE_CALL);
			}
		});

		MyViewHolder.get(view, R.id.entrust_item_msg).setOnClickListener(view2 -> {
			if (buttonClickListener!=null){
				buttonClickListener.rightButtonClick(position, 0, BT_TYPE_MSG);
			}
		});

		return view;
	}

	private void setStaffInfo(StaffListBean staffDetail, CircleImageView entrust_item_head,
							  TextView entrust_user_name, int position){
		entrust_item_head.setOnClickListener(view -> {
			if (buttonClickListener!=null){
				buttonClickListener.rightButtonClick(position, 0, BT_TYPE_STAFF);
			}
		});
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.STAFF_HEAD_HOST + staffDetail.getStaffNo()+".jpg",
				io.rong.imkit.R.drawable.rc_default_portrait, io.rong.imkit.R.drawable.rc_default_portrait)
				.into(entrust_item_head));
		entrust_user_name.setText(staffDetail.getCnName());
	}

	@Override
	public View getChildView(int parentP, int childP, boolean b, View convertView, ViewGroup parent) {
		View view = convertView;
		if (childP==(getChildrenCount(parentP)-1)){
			view = inflater.inflate(R.layout.item_look_footer, parent, false);

			TextView item_look_time = MyViewHolder.get(view, R.id.item_look_time);
			String timeStr = getGroup(parentP).getLookTime();

			if (TextUtils.isEmpty(timeStr)){
				item_look_time.setText("约看时间：待定");
			}else {
				item_look_time.setText("约看时间："+DateUtil.format(timeStr, DateUtil.FORMAT11, DateUtil.FORMAT1));
			}

			Button item_look_evaluate = MyViewHolder.get(view, R.id.item_look_evaluate);

			item_look_evaluate.setVisibility(View.INVISIBLE);

			if (!TextUtils.isEmpty(timeStr) && lookType==LookAbout.LOOK_TYPE_TO){
				item_look_evaluate.setVisibility(View.VISIBLE);
				MyViewHolder.get(view, R.id.item_look_evaluate).setOnClickListener(view1 ->{
					if (buttonClickListener!=null){
						buttonClickListener.rightButtonClick(parentP, 0, BT_TYPE_EVALUATE);
					}
				});
			}

			if (getGroup(parentP).getStatus()==4){
				item_look_evaluate.setVisibility(View.VISIBLE);
				item_look_evaluate.setText("实看房单");
				MyViewHolder.get(view, R.id.item_look_evaluate).setOnClickListener(view1 ->{
					if (buttonClickListener!=null){
						buttonClickListener.rightButtonClick(parentP, 0, BT_TYPE_EVALUATE);
					}
				});
			}

//			if (TextUtils.isEmpty(timeStr) || lookType== LookAbout.LOOK_TYPE_RECORD){
//				item_look_evaluate.setVisibility(View.INVISIBLE);
//			}else {
//				item_look_evaluate.setVisibility(View.VISIBLE);
//				MyViewHolder.get(view, R.id.item_look_evaluate).setOnClickListener(view1 ->{
//					if (buttonClickListener!=null){
//						buttonClickListener.rightButtonClick(parentP, 0, BT_TYPE_EVALUATE);
//					}
//				});
//			}
		}else {

			view = inflater.inflate(R.layout.item_look_child, parent, false);

			MyViewHolder.get(view, R.id.item_house_ll).setOnClickListener(view2 -> {
				if (buttonClickListener!=null){
					buttonClickListener.rightButtonClick(parentP, childP, BT_TYPE_HOUSE);
				}
			});

			LookAboutListDetailBo childItem = getChild(parentP, childP);
			TextView item_child_title = MyViewHolder.get(view, R.id.item_small_title);
			if (item_child_title==null)return view;
			item_child_title.setText(childItem.getTitle());

			ImageView item_small_img = MyViewHolder.get(view, R.id.item_small_img);

			String imgUrl = childItem.getDefaultImage();
			if (TextUtils.isEmpty(imgUrl)) {
				imgUrl = AppContents.POST_DEFAULT_IMG_URL;
			}
			GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUrl)
					.into(item_small_img));

			if (childItem.isIsOnline()){
				MyViewHolder.get(view, R.id.off_view_bg).setVisibility(View.GONE);
				MyViewHolder.get(view, R.id.off_view_text).setVisibility(View.GONE);
			}else {
				MyViewHolder.get(view, R.id.off_view_bg).setVisibility(View.VISIBLE);
				MyViewHolder.get(view, R.id.off_view_text).setVisibility(View.VISIBLE);
			}

			TextView item_child_house = MyViewHolder.get(view, R.id.item_small_house);
			item_child_house.setText(childItem.getRoomCount()+"室"+childItem.getHallCount()+"厅 | "+ MyUtils.format2Integer(childItem.getGArea())+"㎡ | "+childItem.getDirection());
			MyText item_child_money = MyViewHolder.get(view, R.id.item_small_money);
			if ("S".equalsIgnoreCase(childItem.getPostType()+"")){
				item_child_money.setLeftAndRight(MyUtils.format2String(childItem.getSalePrice()/10000)+"", "万");
			}else {
				item_child_money.setLeftAndRight(MyUtils.format2String(childItem.getRentPrice())+"", "元/月");
			}
		}
		return view;
	}

	@Override
	public boolean isChildSelectable(int i, int i1) {
		return true;
	}

	private RightButtonClickListener buttonClickListener;

	public void setRightButtonClick(RightButtonClickListener buttonClickListener){
		this.buttonClickListener=buttonClickListener;
	}

	public interface RightButtonClickListener{
		void rightButtonClick(int parentP, int childP, int iconPosition);
	}
}
