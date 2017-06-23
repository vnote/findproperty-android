package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.api.bean.StaffComment;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanxl2 on 2016/8/20.
 */
public class LookWordAdapter extends CommonAdapter<LookAboutListDetailBo> {

	private DrawableRequestBuilder<String> requestBuilder;

	public LookWordAdapter(Activity context, int layoutId, List<LookAboutListDetailBo> datas) {
		super(context, layoutId, datas);
		requestBuilder = GlideLoad.init(context);
	}

	@Override
	protected void convert(ViewHolder holder, LookAboutListDetailBo item, int position) {

		if (item.getStaffs()==null){
			ApiRequest.getStaffDetail(item.getStaffNo())
					.subscribe(staffDetail -> {

						StaffComment staffComment = new StaffComment();
						staffComment.setCnName(staffDetail.getCnName());
						staffComment.setStoreName(staffDetail.getStoreName());
						staffComment.setStaffNo(staffDetail.getStaffNo());
						staffComment.setStaff400Tel(staffDetail.getStaff400Tel());

						List<StaffComment> staffComments = new ArrayList<>();
						staffComments.add(staffComment);

						item.setStaffs(staffComments);
						setStaffInfo(staffComment, holder);

						// TODO: 2017/2/16 账户用于测试，正式发版隐藏
						//---------------------------------测试--------------------------
//						if (BuildConfig.DEBUG){
//							List<StaffComment> testList = new ArrayList<StaffComment>();
//							//添加测试经纪人
//							StaffComment test1 = new StaffComment();
//							test1.setStaffNo("AA21071");
//							test1.setStaffName("测试1");
//							testList.add(test1);
//
//							StaffComment test2 = new StaffComment();
//							test2.setStaffNo("AB36821");
//							test2.setStaffName("测试2");
//							testList.add(test2);
//
//							StaffComment test3 = new StaffComment();
//							test3.setStaffNo("AA30994");
//							test3.setStaffName("测试3");
//							testList.add(test3);
//
//							StaffComment test4 = new StaffComment();
//							test4.setStaffNo("AA75203");
//							test4.setStaffName("测试4");
//							testList.add(test4);
//
//							StaffComment test5 = new StaffComment();
//							test5.setStaffNo("AA66695");
//							test5.setStaffName("测试5");
//							testList.add(test5);
//
//							StaffComment test6 = new StaffComment();
//							test6.setStaffNo("AA74365");
//							test6.setStaffName("测试6");
//							testList.add(test6);
//							staffComments.addAll(testList);
//						}
						//---------------------------------测试--------------------------

						ApiRequest.getPostStaffs(item.getEstateCode(), item.getPostID(),"6","2")
								.subscribe(staffs -> {
//									staffComments.addAll(staffs);
									if (staffComments.size() > 0) {
										for (StaffComment s:staffs) {
											if (!s.getCnName().equals(staffComments.get(0).getCnName())) {
                                                staffComments.add(s);
                                            }
										}
									}

								}, throwable -> {
									Logger.i(ErrorHanding.handleError(throwable));
								});

					}, throwable -> {
						Logger.i(ErrorHanding.handleError(throwable));
					});
		}else {
			StaffComment staffComment = item.getStaffs().get(item.getSelectedP());
			setStaffInfo(staffComment, holder);
		}

		holder.getView(R.id.item_staff_change).setOnClickListener(view -> {
			if (buttonClickListener!=null){
				buttonClickListener.rightButtonClick(position);
			}
		});


		holder.setText(R.id.look_house_info, item.getRoomCount()+"室"+item.getHallCount()+"厅 | "+
				MyUtils.format2String(item.getGArea())+"㎡ | "+ item.getEstateName());
		if (item.getPostType().equalsIgnoreCase("S")){
			holder.setText(R.id.look_house_price, MyUtils.format2String(item.getSalePrice()/10000)+"万");
		}else {
			holder.setText(R.id.look_house_price, MyUtils.format2String(item.getRentPrice())+"元/月");
		}
	}

	private void setStaffInfo(StaffComment staffComment, ViewHolder holder){
		CircleImageView look_staff_head = holder.getView(R.id.look_staff_head);
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.STAFF_HEAD_HOST + staffComment.getStaffNo()+".jpg")
				.into(look_staff_head));
		holder.setText(R.id.look_staff_name, staffComment.getCnName());
		holder.setText(R.id.look_staff_store, staffComment.getStoreName()==null?"暂无":staffComment.getStoreName());
	}

	private RightButtonClickListener buttonClickListener;

	public void setRightButtonClick(RightButtonClickListener buttonClickListener){
		this.buttonClickListener=buttonClickListener;
	}

	public interface RightButtonClickListener{
		void rightButtonClick(int position);
	}

}
