package com.cetnaline.findproperty.ui.fragment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.LookEvaluatePresenter;
import com.cetnaline.findproperty.presenter.ui.LookEvaluateContract;
import com.cetnaline.findproperty.ui.activity.LookAbout;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by fanxl2 on 2016/8/20.
 */
public class LookEvaluateFragment extends BaseFragment<LookEvaluatePresenter> implements LookEvaluateContract.View {

	public static final String LOOK_ID_KEY = "LOOK_ID_KEY";
	public static final String STAFF_DATA_KEY = "STAFF_DATA_KEY";
	public static final String ESTATE_NAME_KEY = "ESTATE_NAME_KEY";

	public static LookEvaluateFragment getInstance(String lookId, String estateName, StaffListBean staff) {
		LookEvaluateFragment fragment = new LookEvaluateFragment();
		Bundle bundle = new Bundle();
		bundle.putString(LOOK_ID_KEY, lookId);
		bundle.putString(ESTATE_NAME_KEY, estateName);
		bundle.putSerializable(STAFF_DATA_KEY, staff);
		fragment.setArguments(bundle);
		return fragment;
	}

	@BindView(R.id.evaluate_rb)
	RatingBar evaluate_rb;

	@BindView(R.id.evaluate_bt_commit)
	Button evaluate_bt_commit;

	@BindView(R.id.evaluate_et_input)
	EditText evaluate_et_input;

	@BindView(R.id.evaluate_tv_num)
	TextView evaluate_tv_num;

	@BindView(R.id.evaluate_staff_head)
	CircleImageView evaluate_staff_head;

	@BindView(R.id.evaluate_staff_name)
	TextView evaluate_staff_name;

	@BindView(R.id.evaluate_staff_group)
	TextView evaluate_staff_group;

	private int rating;
	private String lookId;

	private DrawableRequestBuilder<String> requestBuilder;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_look_evaluate;
	}

	private String estateName, staffName, staffNo, domainAccount;

	@Override
	protected void init() {

		((LookAbout) getActivity()).setLeftText("服务评价");

		requestBuilder = GlideLoad.init(this);

		lookId = getArguments().getString(LOOK_ID_KEY);
		estateName = getArguments().getString(ESTATE_NAME_KEY);

		StaffListBean staffDetail = (StaffListBean) getArguments().getSerializable(STAFF_DATA_KEY);
		if (staffDetail!=null){
			GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.STAFF_HEAD_HOST + staffDetail.getStaffNo()+".jpg")
					.into(evaluate_staff_head));

			evaluate_staff_name.setText(staffDetail.getCnName());
			evaluate_staff_group.setText(staffDetail.getStoreName());
			staffName = staffDetail.getCnName();
			staffNo = staffDetail.getStaffNo();
			domainAccount = staffDetail.getDomainAccount();
		}

		evaluate_rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
				rating = (int)v;
			}
		});

		RxView.clicks(evaluate_bt_commit)
				.throttleFirst(2, TimeUnit.SECONDS)
				.subscribe( aVoid -> {
					if (rating==0){
						toast("请为本次服务打分");
						return;
					}

					Map<String, String> param = new HashMap<String, String>();
					String comment = evaluate_et_input.getText().toString().trim();

					param.put("UserId", DataHolder.getInstance().getUserId());
					param.put("LookPlanID", lookId);
					param.put("Comment", comment);
					param.put("rating", rating+"");
					param.put("EstateName", estateName);
					param.put("StaffName", staffName);
					param.put("StaffNo", staffNo);
					param.put("DomainAccount", domainAccount);

					mPresenter.lookAboutComment(param);
				});

		RxTextView.textChanges(evaluate_et_input).subscribe(new Action1<CharSequence>() {
			@Override
			public void call(CharSequence charSequence) {
				evaluate_tv_num.setText(charSequence.length() + "/140");
			}
		});
	}

	@Override
	protected LookEvaluatePresenter createPresenter() {
		return new LookEvaluatePresenter();
	}

	@Override
	public void setCommentResult(boolean result) {
		if (result){
			toast("评论成功");
			removeFragment();
			addFragment(ToLookAboutFragment.getInstance(LookAbout.LOOK_TYPE_RECORD));
		}else {
			toast("评论失败");
		}
	}

	@Override
	public void showLoading() {
		showLoadingDialog();
	}

	@Override
	public void dismissLoading() {
		cancelLoadingDialog();
	}

	@Override
	public void showError(String msg) {
		toast(msg);
	}
}
