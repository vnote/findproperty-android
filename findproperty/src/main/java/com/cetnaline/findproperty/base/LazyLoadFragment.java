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

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.presenter.IView;
import com.cetnaline.findproperty.widgets.MenuPopWindow;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;

/**
 * Fragment的基类
 * Created by fanxl2 on 2016/7/21.
 */
public abstract class LazyLoadFragment<T extends IPresenter> extends RxFragment implements IView {

	protected T mPresenter;

	protected boolean isInit = false;
	protected boolean isLoad = false;

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
			isInit = true;
			init();
			isCanLoadData();
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
	protected void addFragment(LazyLoadFragment fragment) {
		if (null != fragment) {
			getHoldingActivity().addFragment(fragment);
		}
	}

	protected void addFragmentNoBack(LazyLoadFragment fragment){
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
		isInit = false;
		isLoad = false;
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
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * 视图是否已经对用户可见，系统的方法
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		isCanLoadData();
	}

	/**
	 * 是否可以加载数据
	 * 可以加载数据的条件：
	 * 1.视图已经初始化
	 * 2.视图对用户可见
	 */
	private void isCanLoadData() {
		if (!isInit) {
			return;
		}

		if (getUserVisibleHint() && !isLoad) {
			lazyLoad();
			isLoad = true;
		} else {
			if (isLoad) {
				stopLoad();
			}
		}
	}

	/**
	 * 当视图初始化并且对用户可见的时候去真正的加载数据
	 */
	protected abstract void lazyLoad();

	/**
	 * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
	 */
	protected void stopLoad() {
	}
}
