package com.cetnaline.findproperty.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.presenter.IView;
import com.cetnaline.findproperty.utils.TalkingDataUtil;
import com.cetnaline.findproperty.widgets.MenuPopWindow;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;

/**
 * Fragment的基类
 * Created by fanxl2 on 2016/7/21.
 */
public abstract class BaseFragment<T extends IPresenter> extends RxFragment implements IView {

	protected T mPresenter;

	//获取布局文件ID
	protected abstract int getLayoutId();
	//初始化方法
	protected abstract void init();

	protected abstract T createPresenter();

	protected LayoutInflater inflater;

	private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

	private boolean isNetworkDialogShow=false;

	public void showNetworkDialog(String msg) {
		if (!isNetworkDialogShow) {
			isNetworkDialogShow = true;
			MenuPopWindow window = new MenuPopWindow(getActivity(), R.layout.network_dialog, (contentView, window1) -> {
				TextView textView = (TextView) contentView.findViewById(R.id.msg);
				textView.setText(msg);
			});
			new Handler().postDelayed(() -> {window.dismiss();isNetworkDialogShow = false;}, 3000);
			window.showAtLocation(rootView, Gravity.CENTER,0,0);
		}

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState!=null){
			boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (isSupportHidden) {
				ft.hide(this);
			} else {
				ft.show(this);
			}
			ft.commit();
			Logger.i("Fragment恢复");
		}else {
			Logger.i("Fragment创建");
		}
		mPresenter = createPresenter();
	}

//	@Override
//	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		if (mPresenter != null)mPresenter.attachView(this);
//		ButterKnife.bind(this, view);
//	}

	protected View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		if (rootView!=null){
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent!=null){
				parent.removeView(rootView);
			}
		}else {
			rootView = inflater.inflate(getLayoutId(), container, false);
			if (mPresenter != null)mPresenter.attachView(this);
			ButterKnife.bind(this, rootView);
			init();
		}
		return rootView;
	}

//	@Override
//	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		init();
//	}

	protected BaseFragmentActivity mActivity;

	//获取宿主Activity
	protected BaseFragmentActivity getHoldingActivity() {
		return mActivity;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof BaseFragmentActivity){
			this.mActivity = (BaseFragmentActivity) context;
		}
	}

	//添加fragment
	protected void addFragment(BaseFragment fragment) {
		if (null != fragment) {
			getHoldingActivity().addFragment(fragment);
		}
	}

	protected void addFragmentNoBack(BaseFragment fragment){
		if (null != fragment) {
			getHoldingActivity().addFragmentNoBack(fragment);
		}
	}

	//移除fragment
	protected void removeFragment() {
		getHoldingActivity().toBack();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mPresenter != null) mPresenter.detachView();
	}

	protected void showLoadingDialog(){
		if (mActivity==null){
			((BaseActivity)getActivity()).showLoadingDialog();
		}else {
			((BaseActivity)mActivity).showLoadingDialog();
		}
	}

	protected void cancelLoadingDialog(){
		if (mActivity==null) {
			((BaseActivity) getActivity()).cancelLoadingDialog();
		}else {
			((BaseActivity)mActivity).cancelLoadingDialog();
		}
	}

	public void toast(CharSequence text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
	}

	@Override
	public void onResume() {
		if (!getTalkingDataPageName().equals("")) {
			TalkingDataUtil.onPageStart(getActivity(),getTalkingDataPageName());
			StatService.onPageStart(getActivity(), getTalkingDataPageName());
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		if (!getTalkingDataPageName().equals("")) {
			TalkingDataUtil.onPageEnd(getActivity(), getTalkingDataPageName());
			StatService.onPageEnd(getActivity(), getTalkingDataPageName());
		}
		super.onPause();
	}

	protected String getTalkingDataPageName() {
		return "";
	}
}
