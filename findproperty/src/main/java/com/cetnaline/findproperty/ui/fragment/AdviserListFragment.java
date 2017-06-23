package com.cetnaline.findproperty.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.StaffComment;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.event.StaffChoiceEvent;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.LookAbout;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by fanxl2 on 2016/8/20.
 */
public class AdviserListFragment extends BaseFragment {

	public static final String STAFF_DATA_KEY = "STAFF_DATA_KEY";
	public static final String STAFF_SELECT_KEY = "STAFF_SELECT_KEY";

	public static AdviserListFragment getInstance(ArrayList<StaffComment> staffs, int selectedP){
		AdviserListFragment fragment = new AdviserListFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(STAFF_DATA_KEY, staffs);
		bundle.putInt(STAFF_SELECT_KEY, selectedP);
		fragment.setArguments(bundle);
		return fragment;
	}

	@BindView(R.id.adviser_rv_list)
	RecyclerView adviser_rv_list;

	private List<StaffComment> datas;

	private StaffAdapter staffAdapter;
	private int selectedPosition;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_adviser_list;
	}

	@Override
	protected void init() {
		((LookAbout)getActivity()).setLeftText("更换看房顾问");

		datas = getArguments().getParcelableArrayList(STAFF_DATA_KEY);
		selectedPosition = getArguments().getInt(STAFF_SELECT_KEY, 0);

		if (datas==null)return;

		staffAdapter = new StaffAdapter(getActivity(), R.layout.item_staff_info, datas);
		staffAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
				selectedPosition = position;
				staffAdapter.notifyDataSetChanged();
				RxBus.getDefault().send(new StaffChoiceEvent(selectedPosition));
				removeFragment();
			}

			@Override
			public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
				return false;
			}
		});

		adviser_rv_list.setLayoutManager(new LinearLayoutManager(getActivity()));
		adviser_rv_list.setAdapter(staffAdapter);
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	public class StaffAdapter extends CommonAdapter<StaffComment>{

		private DrawableRequestBuilder<String> requestBuilder;

		public StaffAdapter(Activity context, int layoutId, List<StaffComment> datas) {
			super(context, layoutId, datas);
			requestBuilder = GlideLoad.init(context);
		}

		@Override
		protected void convert(ViewHolder holder, StaffComment staffComment, int position) {

			CircleImageView staff_list_head = holder.getView(R.id.staff_list_head);
			GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.STAFF_HEAD_HOST + staffComment.getStaffNo()+".jpg")
					.into(staff_list_head));
			holder.setText(R.id.staff_list_name, staffComment.getCnName());
			holder.setText(R.id.staff_list_store, staffComment.getStoreName()==null?"暂无":staffComment.getStoreName());

			if (position==selectedPosition){
				holder.getView(R.id.item_staff_selected).setVisibility(View.VISIBLE);
			}else {
				holder.getView(R.id.item_staff_selected).setVisibility(View.INVISIBLE);
			}
		}
	}
}
