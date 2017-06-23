package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.ui.activity.LookAbout;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 待约看 和 约看记录
 * Created by fanxl2 on 2016/8/20.
 */
public class LookAboutRecordAdapter extends CommonAdapter<LookAboutListDetailBo> {

	private DrawableRequestBuilder<String> requestBuilder;
	private int lookType;

	public static final int BT_TYPE_EVALUATE = 0;
	public static final int BT_TYPE_CALL = 1;
	public static final int BT_TYPE_MSG = 2;
	public static final int BT_TYPE_STAFF = 3;
	public static final int BT_TYPE_HOUSE = 4;

	public LookAboutRecordAdapter(Activity context, int layoutId, List<LookAboutListDetailBo> datas, int lookType) {
		super(context, layoutId, datas);
		this.lookType=lookType;
		requestBuilder = GlideLoad.init(context);
	}

	@Override
	protected void convert(ViewHolder holder, LookAboutListDetailBo childItem, int position) {

		if (childItem.getStaffDetail()==null){
			ApiRequest.getStaffDetail(childItem.getStaffNo())
					.subscribe(staffDetail -> {

						childItem.setStaffDetail(staffDetail);
						setStaffInfo(staffDetail, holder, position);

					});
		}else {
			setStaffInfo(childItem.getStaffDetail(), holder, position);
		}

		Button item_look_evaluate = holder.getView(R.id.item_look_evaluate);
		if (lookType== LookAbout.LOOK_TYPE_TO){

			if (childItem.getLookTime()==null || TextUtils.isEmpty(childItem.getLookTime())){
				holder.setText(R.id.item_look_time, "约看时间: 待定");
				item_look_evaluate.setVisibility(View.INVISIBLE);
			}else {
				holder.setText(R.id.item_look_time, "约看时间:"+childItem.getLookTime());
				item_look_evaluate.setVisibility(View.VISIBLE);
			}

			item_look_evaluate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (buttonClickListener!=null){
						buttonClickListener.rightButtonClick(position, BT_TYPE_EVALUATE);
					}
				}
			});
		}else {
			item_look_evaluate.setVisibility(View.INVISIBLE);
			holder.setText(R.id.item_look_time, "约看时间:"+childItem.getLookTime());
		}

		holder.getView(R.id.item_house_ll).setOnClickListener(view -> {
			if (buttonClickListener!=null){
				buttonClickListener.rightButtonClick(position, BT_TYPE_HOUSE);
			}
		});

		holder.getView(R.id.entrust_item_call).setOnClickListener(view -> {
			if (buttonClickListener!=null){
				buttonClickListener.rightButtonClick(position, BT_TYPE_CALL);
			}
		});

		holder.getView(R.id.entrust_item_msg).setOnClickListener(view -> {
			if (buttonClickListener!=null){
				buttonClickListener.rightButtonClick(position, BT_TYPE_MSG);
			}
		});

		ImageView item_small_img = holder.getView(R.id.item_small_img);
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.IMG_BASE_URL +childItem.getDefaultImage()+"_400x300_f.jpg")
				.into(item_small_img));

		holder.setText(R.id.item_small_title, childItem.getTitle());

		holder.setText(R.id.item_small_house, childItem.getRoomCount()+"室"+childItem.getHallCount()+"厅 | "+ MyUtils.format2Integer(childItem.getGArea())+"㎡ | "+childItem.getDirection());
		if (childItem.getPostType().equalsIgnoreCase("S")){
			holder.setText(R.id.item_small_money, MyUtils.format2Integer(childItem.getSalePrice()/10000) + "万");
		}else {
			holder.setText(R.id.item_small_money, MyUtils.format2Integer(childItem.getRentPrice())+"元/月");
		}

		if (childItem.isIsOnline()){
			holder.getView(R.id.off_view_bg).setVisibility(View.GONE);
			holder.getView(R.id.off_view_text).setVisibility(View.GONE);
		}else {
			holder.getView(R.id.off_view_bg).setVisibility(View.VISIBLE);
			holder.getView(R.id.off_view_text).setVisibility(View.VISIBLE);

		}


	}

	private void setStaffInfo(StaffListBean staffDetail, ViewHolder holder, int position){
		CircleImageView entrust_item_head = holder.getView(R.id.entrust_item_head);
		entrust_item_head.setOnClickListener(view -> {
			if (buttonClickListener!=null){
				buttonClickListener.rightButtonClick(position, BT_TYPE_STAFF);
			}
		});
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.STAFF_HEAD_HOST + staffDetail.getStaffNo()+".jpg")
				.into(entrust_item_head));
		holder.setText(R.id.entrust_user_name, staffDetail.getCnName());
//		holder.setText(R.id.entrust_item_gscope, staffDetail.getStoreName()==null?"暂无":staffDetail.getStoreName());
	}

	private RightButtonClickListener buttonClickListener;

	public void setRightButtonClick(RightButtonClickListener buttonClickListener){
		this.buttonClickListener=buttonClickListener;
	}

	public interface RightButtonClickListener{
		void rightButtonClick(int position, int iconPosition);
	}

}
