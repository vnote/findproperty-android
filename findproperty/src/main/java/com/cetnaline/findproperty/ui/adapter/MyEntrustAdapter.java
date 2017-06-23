package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.MyEntrustBo;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/17.
 */
public class MyEntrustAdapter extends RecyclerView.Adapter<MyEntrustAdapter.ViewHolder> {

	private MyEntrustAdapter.BtClickListener btClickListener;

	private List<MyEntrustBo> datas;

	private DrawableRequestBuilder<String> requestBuilder;

	private LayoutInflater inflater;

	public MyEntrustAdapter(Activity context, List<MyEntrustBo> datas){
		requestBuilder = GlideLoad.init(context);
		inflater = LayoutInflater.from(context);
		this.datas=datas;
	}

	public void setOnBtClickListener(MyEntrustAdapter.BtClickListener btClickListener){
		this.btClickListener = btClickListener;
	}

	public void setDatas(List<MyEntrustBo> datas){
		this.datas.addAll(datas);
		notifyItemRangeChanged(0, getItemCount());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(inflater.inflate(R.layout.item_my_entrust, parent, false));
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		MyEntrustBo item = datas.get(position);

		holder.entrust_item_name.setText(item.getEstateName());
		holder.entrust_item_tag.setText(item.getEntrustType());

		String tagAddress = (item.getRegionName()==null?"":item.getRegionName())+" "
				+(item.getGscpoeName()==null?"":item.getGscpoeName())+
				" | "+(item.getAddress()==null?"":item.getAddress());
		holder.entrust_item_info.setVisibility(View.VISIBLE);
		if (tagAddress.trim().equals("|")) {
			holder.entrust_item_info.setVisibility(View.GONE);
		} else if (tagAddress.startsWith("|")){
			holder.entrust_item_info.setText(tagAddress.substring(1));
		} else if (tagAddress.endsWith("|")) {
			holder.entrust_item_info.setText(tagAddress.substring(0,tagAddress.length()-1));
		} else {
			holder.entrust_item_info.setText(tagAddress);
		}

		int priceFrom = MyUtils.format2Integer(item.getPriceFrom());
//		int priceTo = MyUtils.format2Integer(item.getPriceTo());
		if ("出售".equals(item.getEntrustType())){
			holder.entrust_item_type.setText("期望售价:");
			if (priceFrom/10000==0){
				holder.entrust_item_price.setText("暂无");
			}else {
				holder.entrust_item_price.setText(priceFrom/10000+"万");
			}
//			if (priceFrom/10000==0){
//				holder.entrust_item_price.setText("暂无");
//			}else if (priceFrom==priceTo){
//				holder.entrust_item_price.setText(priceFrom/10000+"万");
//			}else {
//				holder.entrust_item_price.setText(priceFrom/10000+"万—"+priceTo/10000+"万");
//			}
		}else {
			holder.entrust_item_type.setText("期望租金:");
			if (priceFrom==0){
				holder.entrust_item_price.setText("暂无");
			}else {
				holder.entrust_item_price.setText(priceFrom+"元/月");
			}
		}

		holder.room_type.setText(item.getRoomCnt()+"室"+item.getParlorCnt()+"厅"+item.getToiletCnt()+"卫");
		holder.area.setText(item.getArea()+"㎡");

		//有业务员信息
		if (item.getStaffName() != null && !item.getStaffName().equals(	"")) {
//			holder.bottom_layout.setVisibility(View.VISIBLE);      //底部行
			holder.default_tv.setVisibility(View.GONE);            //底部文字
			holder.agent_info_layout.setVisibility(View.VISIBLE);  //底部经济人信息

			holder.entrust_user_name.setText(item.getStaffName());
//		helper.setText(R.id.entrust_item_gscope, "暂时没有位置信息");

			holder.entrust_item_head.setOnClickListener(view -> {
				if (btClickListener !=null){
					btClickListener.iconClick(position, 3);
				}
			});

			if (item.getStaffNo() != null) {
				GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.STAFF_HEAD_HOST + item.getStaffNo() + ".jpg")
						.error(R.drawable.rc_default_portrait)
						.placeHolder(R.drawable.rc_default_portrait)
						.into(holder.entrust_item_head));
			}

			holder.entrust_item_call.setOnClickListener(view -> {
				if (btClickListener !=null){
					btClickListener.iconClick(position, 0);
				}
			});
			holder.entrust_item_msg.setOnClickListener(view -> {
				if (btClickListener !=null){
					btClickListener.iconClick(position, 1);
				}
			});
		} else {
			//没有业务员信息
			if (item.getStatusID() == -1) { //已取消不显示底部栏
//				holder.bottom_layout.setVisibility(View.GONE);
				holder.default_tv.setVisibility(View.GONE);
				holder.agent_info_layout.setVisibility(View.GONE);
			} else {
//				holder.bottom_layout.setVisibility(View.VISIBLE);
				holder.default_tv.setVisibility(View.VISIBLE);
				holder.agent_info_layout.setVisibility(View.GONE);
			}
		}

		//可取消委托的情况
		if (item.getStatusID()==1 || item.getStatusID()==3 || item.getStatusID()==4) {
			holder.status_label.setVisibility(View.GONE);
			holder.entrust_bt_cancel.setVisibility(View.VISIBLE);
			holder.entrust_bt_cancel.setOnClickListener(view -> {
				if (btClickListener !=null){
					btClickListener.iconClick(position, 2);
				}
			});
		} else {
			holder.status_label.setVisibility(View.VISIBLE);
			holder.entrust_bt_cancel.setVisibility(View.GONE);

			if (item.getStatusID()==5) {
				holder.status_label.setText("取消处理中...");
			} else {
				holder.status_label.setText("委托已取消");
			}
		}

	}

	@Override
	public int getItemCount() {
		return datas==null?0:datas.size();
	}

	public interface BtClickListener {
		void iconClick(int position, int iconPosition);
	}

	public void delOne(MyEntrustBo bo,int pos){
		datas.set(pos, bo);
//		notifyItemRemoved(pos);
		notifyItemRangeChanged(pos, getItemCount());
	}

	public class ViewHolder extends RecyclerView.ViewHolder{

		private TextView entrust_item_name, entrust_item_tag, entrust_user_name,
				entrust_item_info, entrust_item_type, entrust_item_price, entrust_bt_cancel,default_tv,room_type,area, status_label;
		private CircleImageView entrust_item_head;
		private ImageButton entrust_item_call, entrust_item_msg;
		private LinearLayout agent_info_layout;
//		private FrameLayout bottom_layout;

		public ViewHolder(View view) {
			super(view);
			default_tv = (TextView) view.findViewById(R.id.default_tv);
			entrust_item_name = (TextView) view.findViewById(R.id.entrust_item_name);
			entrust_item_tag = (TextView) view.findViewById(R.id.entrust_item_tag);
			entrust_item_info = (TextView) view.findViewById(R.id.entrust_item_info);
			entrust_item_type = (TextView) view.findViewById(R.id.entrust_item_type);
			entrust_item_price = (TextView) view.findViewById(R.id.entrust_item_price);
			entrust_user_name = (TextView) view.findViewById(R.id.entrust_user_name);
			agent_info_layout = (LinearLayout) view.findViewById(R.id.agent_info_layout);
			room_type = (TextView) view.findViewById(R.id.room_type);
			area = (TextView) view.findViewById(R.id.area);
			status_label = (TextView) view.findViewById(R.id.status_label);
//			bottom_layout = (FrameLayout) view.findViewById(R.id.bottom_layout);

			entrust_item_head = (CircleImageView) view.findViewById(R.id.entrust_item_head);

			entrust_item_call = (ImageButton) view.findViewById(R.id.entrust_item_call);
			entrust_item_msg = (ImageButton) view.findViewById(R.id.entrust_item_msg);
			entrust_bt_cancel = (TextView) view.findViewById(R.id.entrust_bt_cancel);
		}
	}

}