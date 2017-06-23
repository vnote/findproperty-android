package com.cetnaline.findproperty.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.StaffComment;
import com.cetnaline.findproperty.ui.activity.ConversationActivity;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fanxl2 on 2016/7/27.
 */
public class HouseDescribeWindow extends DialogFragment {

	public static HouseDescribeWindow getInstance(ArrayList<StaffComment> staffComments, String info, int houseType, String postId, String fullPath, String content){
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("staffData", staffComments);
		bundle.putString("info", info);
		bundle.putString("postId", postId);
		bundle.putString("fullPath", fullPath);
		bundle.putString("content", content);
		bundle.putInt(MapFragment.HOUSE_TYPE_KEY, houseType);
		HouseDescribeWindow myRightMenu = new HouseDescribeWindow();
		myRightMenu.setArguments(bundle);
		return myRightMenu;
	}

	@Override
	public void onStart() {
		super.onStart();

		Dialog dialog = getDialog();
		if (dialog!=null){
			WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
			p.width = (int) (MyUtils.getPhoneWidth(getActivity())*0.9);
			p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			p.gravity = Gravity.CENTER;
			dialog.getWindow().setAttributes(p);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		}
	}

	@BindView(R.id.house_desc_rv)
	RecyclerView house_desc_rv;

	@BindView(R.id.house_desc_vp)
	ViewPager house_desc_vp;

	@BindView(R.id.desc_tv_name)
	TextView desc_tv_name;

	private HeadAdapter1 headAdapter;

	private String info;

	private int houseType;

	private String postId;

	private String fullPath;

	private String content;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.dialog_house_desc, container);
		ButterKnife.bind(this, view);
		datas = getArguments().getParcelableArrayList("staffData");
		info = getArguments().getString("info");
		postId = getArguments().getString("postId");
		fullPath = getArguments().getString("fullPath");
		if (TextUtils.isEmpty(fullPath)) {
			fullPath = AppContents.POST_DEFAULT_IMG_URL;
		}
		content = getArguments().getString("content");
		houseType = getArguments().getInt(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
		requestBuilder = GlideLoad.init(getActivity());
		requestBuilder.error(R.drawable.default_head);
		requestBuilder.placeholder(R.drawable.default_head);
		init();
		return view;
	}

	private LinearLayoutManager linearLayoutManager;
	private List<StaffComment> datas;
	private int selectedPosition = 0;
	private DrawableRequestBuilder<String> requestBuilder;

	private void init() {
		linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		house_desc_rv.setLayoutManager(linearLayoutManager);

		desc_tv_name.setText(datas.get(0).getCnName());

		headAdapter = new HeadAdapter1(getActivity(), datas);
//		headAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
//			@Override
//			public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
//				selectedPosition = position;
//				desc_tv_name.setText(datas.get(position).getStaffName());
//				house_desc_vp.setCurrentItem(position);
//				headAdapter.notifyDataSetChanged();
//			}
//
//			@Override
//			public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
//				return false;
//			}
//		});
		house_desc_rv.setAdapter(headAdapter);
		MyPageAdapter myPageAdapter = new MyPageAdapter(datas);
		house_desc_vp.setAdapter(myPageAdapter);
		house_desc_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				selectedPosition = position;
				desc_tv_name.setText(datas.get(position).getCnName());
				linearLayoutManager.scrollToPositionWithOffset(position, 0);
				headAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	@OnClick(R.id.desc_iv_talk)
	public void toTalk(){
		StaffComment staff = datas.get(selectedPosition);
		MyUtils.toStaffTalk(getActivity(), staff.getStaffNo(), staff.getCnName(),MyUtils.TALK_FROM_SOURCE_LIST, info,
				houseType==MapFragment.HOUSE_TYPE_SECOND? ConversationActivity.ERSHOUFANG:ConversationActivity.ZUFANG,
				postId, fullPath, content);
	}

	@OnClick(R.id.desc_iv_call)
	public void toCall(){
		StaffComment staff = datas.get(selectedPosition);
		MyUtils.toCall400(getActivity(), staff.getPhone400(), staff.getCnName());
	}

	public class MyPageAdapter extends PagerAdapter{

		private List<StaffComment> datas;
		protected SparseArray<View> mViews;

		public MyPageAdapter(List<StaffComment> datas) {
			this.datas = datas;
			mViews = new SparseArray<>();
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			View view = mViews.get(position);
			if (view==null){
				view = LayoutInflater.from(getActivity()).inflate(R.layout.item_desc_info, null);
				mViews.put(position, view);
			}

			String[] postDesc = datas.get(position).getPostDirection().split("Ď");
			if (postDesc==null || postDesc.length<3){
				return view;
			}

//			String str1 = postDesc.substring(0, postDesc.indexOf("户型优势")-1);
//			String str2 = postDesc.substring(postDesc.indexOf("户型优势")+4, postDesc.indexOf("其他亮点")-1);
//			String str3 = postDesc.substring(postDesc.indexOf("其他亮点")+4, postDesc.indexOf("售房原因")-1);
//			String str4 = postDesc.substring(postDesc.indexOf("售房原因")+4, postDesc.length());


			TextView desc_item_price = (TextView) view.findViewById(R.id.desc_item_price);
			desc_item_price.setText(postDesc[0]);

			TextView desc_item_good = (TextView) view.findViewById(R.id.desc_item_good);
			desc_item_good.setText(postDesc[1]);


			TextView desc_item_other = (TextView) view.findViewById(R.id.desc_item_other);
			desc_item_other.setText(postDesc[2]);

			TextView desc_item_reason = (TextView) view.findViewById(R.id.desc_item_reason);
			desc_item_reason.setText(postDesc[3]);

			container.addView(view);
			return view;
		}
	}

	public class HeadAdapter1 extends RecyclerView.Adapter<HeadAdapter1.ViewHolder>{

		private List<StaffComment> datas;

		private int normalWidth, bigWidth;

		public HeadAdapter1(Context context, List<StaffComment> datas) {
			this.datas=datas;
			normalWidth = MyUtils.dip2px(context, 50);
			bigWidth = MyUtils.dip2px(context, 60);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_head_view, parent, false));
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {

			holder.itemView.setOnClickListener(v -> {
				selectedPosition = position;
				desc_tv_name.setText(datas.get(position).getCnName());
				house_desc_vp.setCurrentItem(position);
				headAdapter.notifyDataSetChanged();
			});

			StaffComment comment = datas.get(position);
			CircleImageView head = holder.desc_item_head;
			ViewGroup.LayoutParams params = head.getLayoutParams();
			if (selectedPosition == position){
				params.height = bigWidth;
				params.width = bigWidth;

			}else {
				params.height = normalWidth;
				params.width = normalWidth;
			}
			head.setLayoutParams(params);

			GlideLoad.load(new GlideLoad.Builder(requestBuilder, comment.getStaffImage())
					.into(head));
		}

		@Override
		public int getItemCount() {
			return datas.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder{

			CircleImageView desc_item_head;

			public ViewHolder(View itemView) {
				super(itemView);
				desc_item_head = (CircleImageView)itemView.findViewById(R.id.desc_item_head);
			}
		}

	}


	public class HeadAdapter extends CommonAdapter<StaffComment>{

		private int normalWidth, bigWidth;

		public HeadAdapter(Context context, int layoutId, List<StaffComment> datas) {
			super(context, layoutId, datas);
			normalWidth = MyUtils.dip2px(context, 50);
			bigWidth = MyUtils.dip2px(context, 60);
		}

		@Override
		protected void convert(ViewHolder holder, StaffComment comment, int position) {

			CircleImageView head = holder.getView(R.id.desc_item_head);
			ViewGroup.LayoutParams params = head.getLayoutParams();
			if (selectedPosition == position){
				params.height = bigWidth;
				params.width = bigWidth;

			}else {
				params.height = normalWidth;
				params.width = normalWidth;
			}
			head.setLayoutParams(params);

			GlideLoad.load(new GlideLoad.Builder(requestBuilder, comment.getStaffImage())
					.into(head));
		}
	}
}
