package com.cetnaline.findproperty.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.ExerciseListBo;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.ExercisePresenter;
import com.cetnaline.findproperty.presenter.ui.ExerciseContract;
import com.cetnaline.findproperty.ui.activity.LoginActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DateUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.ClearableEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 新房 活动页面
 * Created by fanxl2 on 2016/8/11.
 */
public class ExerciseFragment extends BaseFragment<ExercisePresenter> implements ExerciseContract.View {

	public static ExerciseFragment getInstance(ExerciseListBo apartmentBo){
		ExerciseFragment fragment = new ExerciseFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("apartmentData", apartmentBo);
		fragment.setArguments(bundle);
		return fragment;
	}

	@BindView(R.id.exercise_iv_img)
	ImageView exercise_iv_img;

	@BindView(R.id.exercise_tv_title)
	TextView exercise_tv_title;

	@BindView(R.id.exercise_tv_desc)
	TextView exercise_tv_desc;

	@BindView(R.id.exercise_tv_time)
	TextView exercise_tv_time;

	@BindView(R.id.exercise_tv_address)
	TextView exercise_tv_address;

	@BindView(R.id.exercise_join_number)
	TextView exercise_join_number;

	@BindView(R.id.exercise_end_time)
	TextView exercise_end_time;

	private MyCount myCount;

	private DrawableRequestBuilder<String> requestBuilder;


	@Override
	protected int getLayoutId() {
		return R.layout.frag_exercise;
	}

	private Dialog joinDialog;
	private ExerciseListBo apartmentBo;

	@Override
	protected void init() {

		requestBuilder = GlideLoad.init(this);

		apartmentBo = (ExerciseListBo) getArguments().getSerializable("apartmentData");
		List<ExerciseListBo.ActImgsBean> images = apartmentBo.getActImgs();
		if (images!=null && images.size()>0){
			ExerciseListBo.ActImgsBean imgeItem = images.get(0);
			String imgeStr = imgeItem.getFileUrl();
			GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.NEW_HOUSE_IMG+imgeStr.substring(0, imgeStr.indexOf("."))+"_500x400_f.jpg")
					.into(exercise_iv_img));
		}

		exercise_tv_title.setText(apartmentBo.getActTitle());
		exercise_tv_desc.setText(apartmentBo.getDescription().replace("\n", "").replace("\r", ""));
		exercise_tv_time.setText(apartmentBo.getStartDate()+"--"+apartmentBo.getEndDate());
		exercise_tv_address.setText(apartmentBo.getActAddress());
		exercise_join_number.setText(apartmentBo.getShowBookCnt()+"人");

		if (!TextUtils.isEmpty(apartmentBo.getEndDate())) {
			long time = DateUtil.currentDateDiff(apartmentBo.getEndDate());
			myCount = new MyCount(time, 1000);
			myCount.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (myCount != null) {
			myCount.cancel();
			myCount = null;
		}
	}

	private ClearableEditText exercise_username, exercise_phone, exercise_code;
	private TextView exercise_send_code;
	private String phoneNumber;

	@OnClick(R.id.exercise_to_join)
	public void joinExercise(){

		joinDialog = new Dialog(getActivity(), R.style.Theme_dialog);
		View view = inflater.inflate(R.layout.layout_join_exercise, null);

		exercise_username = (ClearableEditText) view.findViewById(R.id.exercise_username);
		exercise_phone = (ClearableEditText) view.findViewById(R.id.exercise_phone);
		exercise_code = (ClearableEditText) view.findViewById(R.id.exercise_code);

		String phone = DataHolder.getInstance().getUserPhone();
		if (!TextUtils.isEmpty(phone)){
			exercise_phone.setText(phone);
		}

		exercise_send_code = (TextView) view.findViewById(R.id.exercise_send_code);
		exercise_send_code.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String phone = exercise_phone.getText().toString().trim();
				if (!MyUtils.checkPhoneNumber(phone)){
					toast("联系电话填写有误");
					return;
				}
				mPresenter.getVerifyCode(phone);
			}
		});

		view.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				joinDialog.dismiss();
			}
		});

		view.findViewById(R.id.bt_commit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				phoneNumber = exercise_phone.getText().toString().trim();
				if (!MyUtils.checkPhoneNumber(phoneNumber)){
					toast("联系电话填写有误，请检查");
					return;
				}

				String code = exercise_code.getText().toString().trim();
				if (TextUtils.isEmpty(code)){
					toast("验证码不能为空");
					return;
				}

				mPresenter.verificationCode(phoneNumber, code);

			}
		});

		int[] whs = MyUtils.getPhoneWidthAndHeight(getActivity());

		joinDialog.setContentView(view);
		Window win = joinDialog.getWindow();
		win.setGravity(Gravity.CENTER);
		joinDialog.setCanceledOnTouchOutside(true);
		win.setLayout((int) (whs[0] * 0.8), (int) (whs[1] * 0.4));

		if (DataHolder.getInstance().isUserLogin()){
			joinDialog.show();
		}else {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra(LoginActivity.LOGIN_INTENT_KEY, LoginActivity.LOGIN_INTENT_JOIN);
			startActivityForResult(intent, 109);
		}


	}

	//倒计时操作
	private void countDown() {
		exercise_send_code.setEnabled(false);
		new CountDownTimer(60 * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				exercise_send_code.setText("重发(" + millisUntilFinished / 1000 + "秒)");
			}

			@Override
			public void onFinish() {
				exercise_send_code.setText("发送验证码");
				exercise_send_code.setEnabled(true);
			}
		}.start();
	}

	@Override
	protected ExercisePresenter createPresenter() {
		return new ExercisePresenter();
	}

	@Override
	public void setVerifyCodeResult(boolean result) {
		if (result){
			countDown();
			toast("验证码已发送!");
		}else {
			toast("验证码发送失败，请稍后重试!");
		}
	}

	@Override
	public void checkVerifyCodeResult(boolean result) {
		if (result){
			Map<String, String> param = new HashMap<>();
			param.put("ActId", apartmentBo.getActId());
			param.put("EstId", apartmentBo.getEstId());
			param.put("EstExtId", apartmentBo.getEstExtId());
			param.put("UserId", DataHolder.getInstance().getUserId());
			param.put("CustomerName", exercise_username.getText().toString());
			param.put("CustomerMobile", phoneNumber);
			param.put("CityCode", "021");
			param.put("AppName", "APP");
			param.put("Source", "xinfang");
			mPresenter.insertUserBooking(param);
		}else {
			toast("验证码错误，请重新输入!");
		}
	}

	@Override
	public void setInsertBookResult(boolean result) {
		if (result){
			mPresenter.addBookingCount(apartmentBo.getActId(), apartmentBo.getShowBookCnt()+1);
		}else {
			toast("报名失败");
		}
	}

	@Override
	public void setAddBookingResult(boolean result) {
		if (result){
			toast("报名成功");
			joinDialog.dismiss();
			apartmentBo.setShowBookCnt(apartmentBo.getShowBookCnt()+1);
			apartmentBo.setBookCnt(apartmentBo.getShowBookCnt());
			exercise_join_number.setText(apartmentBo.getShowBookCnt()+"人");
		}else {
			toast("报名失败");
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

	class MyCount extends CountDownTimer{

		/**
		 * @param millisInFuture    The number of millis in the future from the call
		 *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
		 *                          is called.
		 * @param countDownInterval The interval along the way to receive
		 *                          {@link #onTick(long)} callbacks.
		 */
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			exercise_end_time.setText(DateUtil.dateDiff(millisUntilFinished));
		}

		@Override
		public void onFinish() {

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==LoginActivity.LOGIN_INTENT_JOIN){
			joinDialog.show();
		}
	}
}
