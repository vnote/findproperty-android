package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.LookAboutNumBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.entity.bean.CollectInfoChangeBean;
import com.cetnaline.findproperty.entity.bean.SystemMessageBean;
import com.cetnaline.findproperty.entity.event.AppLoginEvent;
import com.cetnaline.findproperty.entity.event.NewsEvent;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.event.UserInfoEvent;
import com.cetnaline.findproperty.entity.result.BaseResult;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.BookListActivity;
import com.cetnaline.findproperty.ui.activity.CollectionActivity;
import com.cetnaline.findproperty.ui.activity.CommentActivity;
import com.cetnaline.findproperty.ui.activity.DeputeActivity;
import com.cetnaline.findproperty.ui.activity.EntrustActivity;
import com.cetnaline.findproperty.ui.activity.LookAbout;
import com.cetnaline.findproperty.ui.activity.MySubscribeActivity;
import com.cetnaline.findproperty.ui.activity.NewsActivity;
import com.cetnaline.findproperty.ui.activity.SettingActivity;
import com.cetnaline.findproperty.ui.activity.TaxActivity;
import com.cetnaline.findproperty.ui.activity.ToolActivity;
import com.cetnaline.findproperty.ui.activity.UserInfoSettingActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.ExternalCacheDirUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.cetnaline.findproperty.widgets.MineMenuItem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

/**
 * 我的
 * Created by fanxl2 on 2016/7/21.
 */
public class MineFragment extends BaseFragment implements MineMenuItem.OnItemClick {

	@BindView(R.id.mine_setting)
	ImageView mine_setting;//设置按钮
	@BindView(R.id.mine_name)
	TextView mine_name;    //显示用户名
	@BindView(R.id.tv_wait_1)
	TextView tv_wait_1;    //约看单
	@BindView(R.id.tv_wait_2)
	TextView tv_wait_2;    //待约看
	@BindView(R.id.tv_wait_3)
	TextView tv_wait_3;    //约看记录
	@BindView(R.id.mine_img)
	CircleImageView mine_img; //用户头像

	@BindViews({
			R.id.menu_my_msg,
			R.id.menu_my_require,
			R.id.menu_my_collection,
			R.id.menu_my_active,
			R.id.menu_my_counter,
			R.id.menu_my_tax,
			R.id.menu_my_power,
			R.id.menu_my_sale,
			R.id.menu_my_rant,
			R.id.menu_my_entrust
	})
	List<MineMenuItem> menus;    //菜单

	private CompositeSubscription mCompositeSubscription;

	@Override
	protected int getLayoutId() {
		return R.layout.frg_mine;
	}

	/**
	 * 设置用户头像、昵称
	 */
	private void loadUserInformation(){
		if (TextUtils.isEmpty(DataHolder.getInstance().getRcUserId())) {
			return;
		}
		RongIM.getInstance().setCurrentUserInfo(new UserInfo(DataHolder.getInstance().getRcUserId(),DataHolder.getInstance().getRcUsername(), Uri.parse(DataHolder.getInstance().getRcPortrait())));
		GlideLoad.initRound(this)
				.error(R.drawable.user_default_portrait)
				.load(DataHolder.getInstance().getRcPortrait())
				.placeholder(R.drawable.user_default_portrait)
				.into(mine_img);

		if(DataHolder.getInstance().getUserInfo() != null) {
			String name = DataHolder.getInstance().getUserInfo().NickName;
			if (name.length() > 11) {
				mine_name.setText(name.substring(0,11) + "***");
			} else {
				mine_name.setText(name);
			}
		}
	}

	@Override
	protected void init() {
		mCompositeSubscription = new CompositeSubscription();
		EventBus.getDefault().register(this);
		mine_setting.setOnClickListener((v)->{
			Intent intent = new Intent(getActivity(), SettingActivity.class);
			getActivity().startActivity(intent);

//			Intent intent = new Intent(getActivity(), MainTabActivity.class);
//			intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_MAP);
//			startActivity(intent);
		});

		//注册菜单事件
		for (MineMenuItem item:menus) {
			item.setOnItemClick(this);
		}

		mine_img.setOnClickListener((v)->{
			Intent intent = new Intent(getActivity(), UserInfoSettingActivity.class);
			getActivity().startActivity(intent);
		});

		if (DataHolder.getInstance().isUserLogin()) {
			loadUserHeader();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//读取消息数量
		if (DataHolder.getInstance().isUserLogin()) {
			loadMessageCount();
			loadOrderCount();
		}
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.menu_my_msg:
				intent.setClass(getActivity(),NewsActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.menu_my_require:
				intent.setClass(getActivity(),MySubscribeActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.menu_my_collection:
				intent.setClass(getActivity(),CollectionActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.menu_my_active:
				intent.setClass(getActivity(),BookListActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.menu_my_counter:
				intent.setClass(getActivity(),ToolActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.menu_my_tax:
				intent.setClass(getActivity(),TaxActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.menu_my_power:
				intent.setClass(getActivity(), CommentActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.menu_my_sale:
				intent.setClass(getActivity(), DeputeActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.menu_my_rant:
				mCompositeSubscription.add(
						ApiRequest.entrustCountRequest(
								DataHolder.getInstance().getUserId())
								.compose(SchedulersCompat.applyIoSchedulers())
								.subscribe(integer -> {
									if (integer < AppContents.DEPUTE_COUNT) {
										Intent intent1 = new Intent(getActivity(), DeputeActivity.class);
										startActivity(intent1);
									} else {
										MyUtils.showDiloag(getActivity(), R.layout.dialog_alert_single, 250, -1, true, (layout, dialog) -> {
											TextView title = (TextView) layout.findViewById(R.id.title);
											title.setText("您的委托数量已达上限");
											TextView submit = (TextView) layout.findViewById(R.id.submit);
											submit.setOnClickListener(v1-> dialog.dismiss());
										});
									}
								}, throwable -> {
									throwable.printStackTrace();
									toast("连接服务器失败，请稍后再试");
								}));
				break;
			case R.id.menu_my_entrust:
				intent.setClass(getActivity(),EntrustActivity.class);
				intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, EntrustActivity.ENTRUST_TYPE_MY);
				getActivity().startActivity(intent);
				break;
		}
	}

	/**
	 * 监听登录，更新用户信息
	 * @param event
	 */
	public void onEventMainThread(AppLoginEvent event){
		if (event.isLogin) {
			//下载用户头像
			loadUserHeader();
             //加载信息数量
			loadMessageCount();
			//加载约看单数量
			loadOrderCount();
		} else {
			tv_wait_1.setText("约看单");
			tv_wait_2.setText("待约看");
		}
	}

	/**
	 * 下载用户头像
	 */
	private void loadUserHeader() {
		if (DataHolder.getInstance().getUserId() == null) {
			return;
		}
		//读取用户头像
		String path = ExternalCacheDirUtil.getImageDownloadCacheDir();
		String fileName = "portrait_"+System.currentTimeMillis()+".jpg";
		mCompositeSubscription.add(ApiRequest.getUserImg(DataHolder.getInstance().getUserId())
				.subscribe(result->{
					if (result.Result != null && result.Result.Img != null) {
						byte[] data = Base64.decode(result.Result.Img,0);
						FileOutputStream fos = null;
						BufferedOutputStream bos = null;
						MyUtils.deleteFiles(path);
						try {
							File file = new File(path ,fileName);
							if (!file.exists()) {file.createNewFile();}

							fos = new FileOutputStream(file);
							bos = new BufferedOutputStream(fos);
							bos.write(data);
							DataHolder.getInstance().setRcPortrait(path + File.separator + fileName);
						} catch (Exception e) {e.printStackTrace();}
						finally {
							if (bos != null)
							{try {bos.close();}
							catch (IOException e) {
								e.printStackTrace();}}
							if (fos != null)
							{try {fos.close();}
							catch (IOException e)
							{e.printStackTrace();}}
						}
					}
					loadUserInformation();
				},throwable -> {
					throwable.printStackTrace();
					loadUserInformation();
				}));
	}

	/**
	 * 消息数量更新事件
	 * @param event
	 */
	public void onEventMainThread(NewsEvent event) {
		int count = menus.get(0).getHintCount();
		menus.get(0).setHint(count + event.updateCount);
	}

	/**
	 * 用户信息更新监听
	 * @param event
	 */
	public void onEventMainThread(UserInfoEvent event) {
		loadUserInformation();
	}

	/**
	 * 监听约看单/网络连接
	 */
	public void onEventMainThread(NormalEvent event) {
		if (DataHolder.getInstance().isUserLogin()) {
			switch (event.type) {
				case NormalEvent.ORDER_UPDATE:
					loadOrderCount();
					break;
				case NormalEvent.NETWORK:
					loadUserInformation();
					loadMessageCount();
					loadOrderCount();
					break;
				default:
					break;
			}
		}
	}

	private void loadMessageCount(){
		Observable.combineLatest(ApiRequest.getCollectInfoChangeList(new HashMap() {
			{
				put("FirstIndex", "0");
				put("Count", "1000");
				put("UserId", DataHolder.getInstance().getUserId());
				put("CityCode", "021");
				put("IsDel", "false");
				put("IsRead","false");
			}
		}), ApiRequest.getSystemMessageList(new HashMap() {
			{
				put("FirstIndex", "0");
				put("Count", "1000");
				put("UserId", DataHolder.getInstance().getUserId());
				put("CityCode", "021");
				put("IsDel", "0");
				put("IsRead","false");
			}
		}), new Func2<BaseResult<CollectInfoChangeBean>, List<SystemMessageBean>, Integer>() {
			@Override
			public Integer call(BaseResult<CollectInfoChangeBean> collectInfoChangeBeanBaseResult, List<SystemMessageBean> systemMessageBeen) {
				int count = 0;
				if (collectInfoChangeBeanBaseResult.Result != null) {
					count += collectInfoChangeBeanBaseResult.Result.size();
				}
				if (systemMessageBeen != null) {
					count += systemMessageBeen.size();
				}
				return count;
			}
		}).subscribe(new Action1<Integer>() {
			@Override
			public void call(Integer integer) {
				menus.get(0).setHint(integer);
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				throwable.printStackTrace();
			}
		});

//		mCompositeSubscription.add(ApiRequest.getCollectInfoChangeList(new HashMap(){
//			{
//				put("FirstIndex", "0");
//				put("Count","1000");
//				put("UserId", DataHolder.getInstance().getUserId());
//				put("CityCode", "021");
//				put("IsDel","false");
//				put("IsRead","false");
////				put("ChangeType","");
//			}
//		}).subscribe(new Action1<BaseResult<CollectInfoChangeBean>>() {
//			@Override
//			public void call(BaseResult<CollectInfoChangeBean> result) {
//				if (result.Result != null) {
//					menus.get(0).setHint(result.Result.size());
//				} else {
//					menus.get(0).setHint(0);
//				}
//			}
//		}, new Action1<Throwable>() {
//			@Override
//			public void call(Throwable throwable) {
//				throwable.printStackTrace();
////				toast(ErrorHanding.handleError(throwable));
//			}
//		}));
	}

	/**
	 * 加载约看单数量
	 */
	private void loadOrderCount() {
		mCompositeSubscription.add(ApiRequest.getLookedAboutNumber(DataHolder.getInstance().getUserId(), 0)
				.subscribe(new Action1<LookAboutNumBo>() {
					@Override
					public void call(LookAboutNumBo lookAboutNumBo) {
						if (lookAboutNumBo != null && lookAboutNumBo.getStatusCount() > 0) {
							tv_wait_1.setText(tv_wait_1.getText().toString().replaceAll("\\(.*\\)","") + "("+lookAboutNumBo.getStatusCount()+")");
						}
					}
				}, throwable -> {
					throwable.printStackTrace();
					tv_wait_1.setText(tv_wait_1.getText().toString().replaceAll("\\(.*\\)", ""));
				}));

		mCompositeSubscription.add(ApiRequest.getLookedAboutNumber(DataHolder.getInstance().getUserId(), 1)
				.subscribe(new Action1<LookAboutNumBo>() {
					@Override
					public void call(LookAboutNumBo lookAboutNumBo) {
						if (lookAboutNumBo != null && lookAboutNumBo.getStatusCount() > 0) {
							tv_wait_2.setText(tv_wait_2.getText().toString().replaceAll("\\(.*\\)","") + "("+lookAboutNumBo.getStatusCount()+")");
						}
					}
				}, throwable -> {
					throwable.printStackTrace();
					tv_wait_2.setText(tv_wait_2.getText().toString().replaceAll("\\(.*\\)", ""));
				}));
	}

	@OnClick(R.id.tv_wait_1)
	public void lookAboutList(){
		Intent intent = new Intent(getActivity(), LookAbout.class);
		intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, LookAbout.LOOK_TYPE_LIST);
		startActivity(intent);
//		toast("敬请期待");
	}

	@OnClick(R.id.tv_wait_2)
	public void toLookAbout(){
		Intent intent = new Intent(getActivity(), LookAbout.class);
		intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, LookAbout.LOOK_TYPE_TO);
		startActivity(intent);
//		toast("敬请期待");
	}

	@OnClick(R.id.tv_wait_3)
	public void lookAboutRecord(){
		Intent intent = new Intent(getActivity(), LookAbout.class);
		intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, LookAbout.LOOK_TYPE_RECORD);
		startActivity(intent);
//		toast("敬请期待");
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
			mCompositeSubscription.unsubscribe();
		}
		super.onDestroy();
	}
}
