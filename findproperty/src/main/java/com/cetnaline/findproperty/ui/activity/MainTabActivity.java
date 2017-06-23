package com.cetnaline.findproperty.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.camnter.easyrecyclerview.widget.EasyRecyclerView;
import com.camnter.easyrecyclerviewsidebar.EasyRecyclerViewSidebar;
import com.camnter.easyrecyclerviewsidebar.sections.EasyImageSection;
import com.camnter.easyrecyclerviewsidebar.sections.EasySection;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.AppAdBo;
import com.cetnaline.findproperty.api.bean.AppUpdateBo;
import com.cetnaline.findproperty.api.bean.SchoolBo;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.db.entity.RailLine;
import com.cetnaline.findproperty.db.entity.RailWay;
import com.cetnaline.findproperty.entity.event.AppLoginEvent;
import com.cetnaline.findproperty.entity.event.DownLoadEvent;
import com.cetnaline.findproperty.entity.event.HostChangeEvent;
import com.cetnaline.findproperty.entity.event.UnreadEvent;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.api.NetWorkManager;
import com.cetnaline.findproperty.presenter.impl.MainPresenter;
import com.cetnaline.findproperty.presenter.ui.MainContract;
import com.cetnaline.findproperty.ui.adapter.ChildAdapter;
import com.cetnaline.findproperty.ui.adapter.ParentAdapter;
import com.cetnaline.findproperty.ui.adapter.SchoolListAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewPagerSimpleAdapter;
import com.cetnaline.findproperty.ui.fragment.ConversationListFragment;
import com.cetnaline.findproperty.ui.fragment.FoundFragment;
import com.cetnaline.findproperty.ui.fragment.HomeFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.ui.fragment.MineFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.ExternalCacheDirUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RcUtil;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.AdvertWindow;
import com.cetnaline.findproperty.widgets.LoadingLayout;
import com.cetnaline.findproperty.widgets.MainViewPager;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.Event;
import io.rong.imlib.model.Conversation;
import me.leolin.shortcutbadger.ShortcutBadger;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import zlc.season.rxdownload.RxDownload;
import zlc.season.rxdownload.entity.DownloadStatus;

public class MainTabActivity extends BaseActivity<MainPresenter> implements MainContract.View,AppWakeUpListener {

	private HomeFragment home;
	private FoundFragment found;
	private MapFragment map;
	private ConversationListFragment chat;
	private MineFragment mine;

	@BindView(R.id.bottom_bar)
	CommonTabLayout bottom_bar;

	@BindView(R.id.map_img)
	ImageView map_img;

	@BindView(R.id.main_lv_parent)
	ListView main_lv_parent;

	@BindView(R.id.main_lv_child)
	ListView main_lv_child;

	@BindView(R.id.main_container)
	DrawerLayout main_container;

	@BindView(R.id.main_right_list)
	LinearLayout main_right_list;

	@BindView(R.id.bottom_layout)
	RelativeLayout bottom_layout;

	@BindView(R.id.view_pager)
	MainViewPager view_pager;

	@BindView(R.id.school_list_layout)
	RelativeLayout school_list_layout;
	@BindView(R.id.section_rv)
	EasyRecyclerView section_rv;
	@BindView(R.id.section_sidebar)
	EasyRecyclerViewSidebar section_sidebar;
	@BindView(R.id.section_floating_rl)
	RelativeLayout section_floating_rl;
	@BindView(R.id.section_floating_tv)
	TextView section_floating_tv;
	@BindView(R.id.child_list_layout)
	FrameLayout child_list_layout;
	@BindView(R.id.loading_layout)
	LoadingLayout loading_layout;

	private ArrayList<Fragment> fragments;

	private String imgUrl = null, advertUrl;

	private int shadeType = 0;

	public void setShadeType(int shadeType) {
		this.shadeType = shadeType;
	}

//	private Handler handler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what){
//				case FILE_DOWN_SUCCESS:
//				case FILE_DOWN_FAIL:
//					AdvertWindow.getInstance(imgUrl, advertUrl).
//					show(getSupportFragmentManager(), "AdvertWindow");
//					break;
//			}
//		}
//	};

	private int parentSelectedPosition = -1, childSelectedPosition;

	public static final String SELECTED_TAB_KEY = "SELECTED_TAB_KEY";
	public static final int TAB_HOME = 0;
	public static final int TAB_FOUND = 1;
	public static final int TAB_MAP = 2;
	public static final int TAB_CHAT = 3;
	public static final int TAB_MINE = 4;

	private int current_tab = -1;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		current_tab = intent.getIntExtra(SELECTED_TAB_KEY, TAB_MAP);
		view_pager.setCurrentItem(current_tab, false);

		setIntent(intent);
		//open install
		OpenInstall.getWakeUp(intent, this);
	}

	public MapFragment getMapFragment() {
		return map;
	}

	private TextView parentView, childView;

	@Override
	protected int getContentViewId() {
		return R.layout.act_main_tab;
	}


	@Override
	protected void init(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);

		//open install
		OpenInstall.getWakeUp(getIntent(), this);

		mCompositeSub = new CompositeSubscription();
		DataHolder.getInstance().setChoiceIntent(-1);
		DbUtil.init(BaseApplication.getContext());
		fragments = new ArrayList<>();
		home = new HomeFragment();
		found = new FoundFragment();
		map = new MapFragment();
		chat = new ConversationListFragment();
		chat.setUri(Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversationlist").build());
		mine = new MineFragment();
		fragments.add(home);
		fragments.add(found);
		fragments.add(map);
		fragments.add(chat);
		fragments.add(mine);

		view_pager.setAdapter(new ViewPagerSimpleAdapter(this,getSupportFragmentManager(), fragments,true));
		view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				map_img.setImageResource(R.drawable.ic_map_location_normal);
				bottom_bar.setCurrentTab(position);
				current_tab = position;
				if (position == TAB_MAP) {
					map_img.setImageResource(R.drawable.ic_map_location_selected);
				} else if (position == TAB_CHAT) {
					//检测通知是否打开
//					MyUtils.checkNotificationSetting(MainTabActivity.this);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		bottom_bar.setTabData(new ArrayList(){
			{
				add(new TabEntity("首页",R.drawable.ic_main_home_selected,R.drawable.ic_main_home_normal));
				add(new TabEntity("发现",R.drawable.ic_main_found_selected,R.drawable.ic_main_found_normal));
				add(new TabEntity("找房",R.drawable.ic_blank,R.drawable.ic_blank));
				add(new TabEntity("咨询",R.drawable.ic_main_msg_selected,R.drawable.ic_main_msg_normal));
				add(new TabEntity("我的",R.drawable.ic_main_mine_selected,R.drawable.ic_main_mine_normal));
			}});

		bottom_bar.setCheckListener(position -> {

			switch (position) {
				case 0:
					break;
				case 1:
					break;
				case 2:
//					showNetworkDialog("网络错误");
					break;
				case 3:
					if (!DataHolder.getInstance().isUserLogin()) {
						Intent intent = new Intent(this, LoginActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(LoginActivity.OPEN_TYPE, LoginActivity.HOME_CHAT);
						intent.putExtras(bundle);
						startActivity(intent);
						toast("请先登录");
						return false;
					}
					break;
				case 4:
					if (!DataHolder.getInstance().isUserLogin()) {
						Intent intent = new Intent(this, LoginActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(LoginActivity.OPEN_TYPE, LoginActivity.HOME_MINE);
						intent.putExtras(bundle);
						startActivity(intent);
						toast("请先登录");
						return false;
					}
					break;
			}
			return true;
		});

		bottom_bar.setOnTabSelectListener(new OnTabSelectListener() {

			@Override
			public void onTabSelect(int position) {

				if (map!=null){
					map.setShow(position==TAB_MAP);
				}

				if (position!=TAB_MAP){
					showTab();
				}

				view_pager.setCurrentItem(position,false);
				current_tab = position;

			}

			@Override
			public void onTabReselect(int position) {
			}
		});
		current_tab = getIntent().getIntExtra(SELECTED_TAB_KEY, TAB_MAP);
		view_pager.setCurrentItem(current_tab,false);

		DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) main_right_list.getLayoutParams();
		params.width = AutoUtils.getPercentWidthSize(600);
		main_right_list.setLayoutParams(params);

		//禁止手势滑动
		main_container.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//		mCompositeSub.add(RxBus.getDefault().toObservable(BottomWindowEvent.class)
//				.subscribe(event -> {
//					if (event.isOpen()) {
//						hiddenTab();
//					} else {
//						map.hiddenBottomWindow(false);
//						showTab();
//					}
//				}));

		if (DbUtil.getGscpoeCount()>0){
			areas = DbUtil.getGScopeChild(21);
		}

		if (DbUtil.getRailLineCount()>0){
			railLines = DbUtil.getRailLines();
		}

		parentAdapter = new ParentAdapter(this, parents, R.layout.item_text);
		childAdapter = new ChildAdapter(this, new ArrayList<>(), R.layout.item_text);

		parentView = new TextView(this);
		parentView.setGravity(Gravity.CENTER);

		childView = new TextView(this);
		childView.setGravity(Gravity.CENTER);

		int itemHeight = MyUtils.dip2px(this, 30);
		AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);

		parentView.setLayoutParams(param);
		childView.setLayoutParams(param);

		main_lv_parent.addHeaderView(parentView);
		main_lv_child.addHeaderView(childView);

		main_lv_parent.setAdapter(parentAdapter);

		main_lv_parent.setOnItemClickListener((adapterView, view, i, l) -> {
			if (i==0)return;
			i-=1;
			parentSelectedPosition = i;
			parentAdapter.setSelectedPosition(parentSelectedPosition);
			child_list_layout.setVisibility(View.VISIBLE);
			if (rightType == 0) {
				List<DropBo> childs = new ArrayList<>();
				List<RailWay> railWays = DbUtil.getRailWayByRailLineId(parents.get(i).getValue());
				for (RailWay railWay : railWays) {
					DropBo child = new DropBo(railWay.getRailWayName(), railWay.getRailWayID() + "", 0);
					childs.add(child);
				}

				DropBo nonLimit = new DropBo("不限", "-1", -1);
				childs.add(0, nonLimit);

				childAdapter.setData(childs);
				main_lv_child.setVisibility(View.VISIBLE);
				school_list_layout.setVisibility(View.GONE);
			} else {
				main_lv_child.setVisibility(View.GONE);
				school_list_layout.setVisibility(View.VISIBLE);
				mPresenter.getSchoolList(parents.get(i).getValue());
			}

		});

		main_lv_child.setAdapter(childAdapter);

		//右侧二级菜单点击事件
		main_lv_child.setOnItemClickListener((adapterView, view, i, l) -> {
			if (i==0)return;
			i-=1;
			childSelectedPosition = i;
			DropBo parentItem = parentAdapter.getItem(parentSelectedPosition);
			DropBo childItem = childAdapter.getItem(i);

			if (childItem.getType() == -1) {
				//不限
				closeRightMenu(parentItem, null);
			} else {
				//具体站点
				closeRightMenu(parentItem, childItem);
			}
		});

//		initLocation();
		RcUtil.connectRC();

		//!SharedPreferencesUtil.getBoolean(LIMIT_TO_UPDATE)
		mCompositeSub.add(RxBus.getDefault().toObservable(HostChangeEvent.class)
				.subscribe(hostChangeEvent -> {
					NetWorkManager.refreshRetrofit();
				},throwable -> {
					throwable.printStackTrace();
				}));


		//init school list view
		schoolListAdapter = new SchoolListAdapter();
		section_rv.setAdapter(schoolListAdapter);
		section_sidebar.setFloatView(section_floating_rl);
		section_sidebar.setOnTouchSectionListener(new EasyRecyclerViewSidebar.OnTouchSectionListener() {
			@Override
			public void onTouchImageSection(int sectionIndex, EasyImageSection imageSection) {

			}

			@Override
			public void onTouchLetterSection(int sectionIndex, EasySection letterSection) {
				section_floating_tv.setVisibility(View.VISIBLE);
				section_floating_tv.setText(letterSection.letter);
				section_rv.getLinearLayoutManager().scrollToPositionWithOffset(schoolListAdapter.getPositionForSection(sectionIndex),0);
			}
		});

		schoolListAdapter.setOnItemClick(position->{
			childSelectedPosition = position;
			DropBo parentItem = parentAdapter.getItem(parentSelectedPosition);
			DropBo childItem = schoolListAdapter.getItem(position);
			closeRightMenu(parentItem, childItem);
		});

	}

	public void showTab() {
		if (showBottomWindow){
			showBottomWindow = false;
			startAnimation(bottom_layout, false);
		}
	}

	public void hiddenTab() {
		showBottomWindow = true;
		startAnimation(bottom_layout, true);
	}

	@Override
	protected MainPresenter createPresenter() {
		return new MainPresenter();
	}

	@Override
	protected void initToolbar() {
		showToolbar(false);
		StatusBarCompat.translucentStatusBar(this, true);
	}

	private long lastBackTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (showBottomWindow) {
				if (map != null) {
					map.hiddenBottomWindow(false);
				}
				return false;
			}
			long now = System.currentTimeMillis();
			if (now - lastBackTime < 3000) {
				DbUtil.setNull();
				DataHolder.getInstance().closeAll();
				DataHolder.getInstance().setNull();
				return true;
			} else {
				lastBackTime = now;
				toast("再按一次退出程序");
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
		if (mCompositeSub != null && mCompositeSub.hasSubscriptions()) {
			mCompositeSub.unsubscribe();
		}
		System.exit(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//用户登录，读取维度消息数量
		if (DataHolder.getInstance().isUserLogin()) {
			refreshUnreadCount();
		}
		if (current_tab == TAB_CHAT) {
			//检测通知是否打开
//			MyUtils.checkNotificationSetting(MainTabActivity.this);
		}
		//清除通知栏信息
		NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(AppContents.RONG_MESSAGE_NOTIFICATION_ID);

		//判断是否是通知栏启动
		if (DataHolder.getInstance().isNoticeLunchMode()) {
			DataHolder.getInstance().setNoticeLunchMode(false);
			Timer timer = new Timer();
			TimerTask task = new TimerTask(){
				@Override
				public void run() {
					timer.cancel();
					if (DataHolder.getInstance().getWhichPage().equals("news")
							&& DataHolder.getInstance().getNewType() > 0) {
						//打开我的消息
						Intent intent = new Intent(MainTabActivity.this,NewsActivity.class);
						intent.putExtra("newType",DataHolder.getInstance().getNewType());
						startActivity(intent);
					} else if (DataHolder.getInstance().getWhichPage().equals("subscribe")
							&& DataHolder.getInstance().getSubscribeType() > 0) {
						//打开我的意向
						Intent intent = new Intent(MainTabActivity.this,MySubscribeActivity.class);
						intent.putExtra("subscribeType",DataHolder.getInstance().getSubscribeType());
						startActivity(intent);
					}
				}
			};
			timer.schedule(task, 1000 * 3);
		}

		if (!isUpdating){
			mPresenter.getAppVersion();
		} else {
			if (imgUrl==null)mPresenter.getAppAdvert();
		}
	}

	@Override
	public void setSchoolList(List<SchoolBo> schoolList) {
		List<DropBo> childs = new ArrayList<>();

//		childAdapter.setData(childs);
		mCompositeSub.add(Observable.just(childs)
				.map(dropBos -> {
                    if (schoolList != null) {
                        for (SchoolBo school : schoolList) {
                            DropBo child = new DropBo(school.getSchoolShortName(), school.getSchoolId() + "", 0);
                            child.setLat(school.getLat());
                            child.setLng(school.getLng());
                            child.setHeaderStr(MyUtils.getFirstSpell(child.getText()));
                            boolean flag = false;
                            for (int i=0;i<childs.size();i++) {
                                if (childs.get(i).getHeaderStr().compareTo(child.getHeaderStr()) > 0) {
                                    childs.add(i,child);
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                childs.add(child);
                            }
                        }
                    }
                    return childs;
                })
				.compose(SchedulersCompat.applyIoSchedulers())
				.subscribe(dropBos -> {
					showSchoolLoading(false);
					schoolListAdapter.setList(childs);
					schoolListAdapter.notifyDataSetChanged();
					section_sidebar.setSections(schoolListAdapter.getSections());
                },throwable -> {
					showSchoolLoading(false);
					throwable.printStackTrace();
				}));
	}

	@Override
	public void showError(String msg) {
		toast(msg);
	}

	/**
	 * 添加遮罩层
	 * @return
	 */
	@Override
	protected boolean hasShade() {
		return true;
	}

	/**
	 * 关闭遮罩层事件
	 * @return
	 */
	@Override
	public boolean clickShadeHide() {
		return false;
	}

	/**
	 * 圈画\全屏指引图层初始化
	 * @param vs
	 * @return
	 */
	@Override
	protected boolean initShade(View[] vs) {
		if (shadeType == 0) {
			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

			int imageHeight = (int) (MyUtils.getPhoneWidth(this) * (556.0f/635));

			RelativeLayout.LayoutParams rlp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageHeight);
			rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
			shade_layout.addView(imageView,rlp);

			Glide.with(this)
					.load(R.drawable.hand_draw)
//				.asGif()
					.diskCacheStrategy(DiskCacheStrategy.SOURCE)
					.listener(new RequestListener<Integer, GlideDrawable>() {
						@Override
						public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
							return false;
						}

						@Override
						public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
							int duration = 0;
							GifDrawable drawable = (GifDrawable) resource;
							GifDecoder decoder = drawable.getDecoder();
							for (int i = 0; i < drawable.getFrameCount(); i++) {
								duration = decoder.getDelay(i);
							}

							mCompositeSub.add(
									Observable.timer(duration*70,TimeUnit.MILLISECONDS)
											.compose(SchedulersCompat.applyIoSchedulers()).subscribe(aLong -> {
										ImageView imageView1 = new ImageView(MainTabActivity.this);
										imageView1.setImageDrawable(getResources().getDrawable(R.drawable.point_hand));

										Rect rect = new Rect();
										vs[0].getGlobalVisibleRect(rect);
										RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//											params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                                            params.setMargins(0,0, rect.right,rect.bottom);
										params.setMargins(rect.left+ MyUtils.dip2px(MainTabActivity.this, 100),rect.top + MyUtils.dip2px(MainTabActivity.this, 8),0,0);
										shade_layout.addView(imageView1,params);

										RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
										params1.setMargins(rect.left+ MyUtils.dip2px(MainTabActivity.this, 160),rect.top + MyUtils.dip2px(MainTabActivity.this, 8),0,0);
										TextView textView = new TextView(MainTabActivity.this);
										textView.setText("点击退出圈画");
										textView.setTextColor(Color.WHITE);
										textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
										shade_layout.addView(textView,params1);

										ValueAnimator animator = ObjectAnimator.ofFloat(imageView1,"translationX",imageView1.getTranslationX(), MyUtils.dip2px(MainTabActivity.this, -20),imageView1.getTranslationX());
										animator.setInterpolator(new AccelerateInterpolator());
										animator.setStartDelay(100);
										animator.setDuration(600);
										animator.start();
									}));

							return false;
						}
					})
					.into(new GlideDrawableImageViewTarget(imageView,1));
//				.into(imageView);
		} else {
			AppCompatImageView imageClose = new AppCompatImageView(MainTabActivity.this);
			imageClose.setImageResource(R.drawable.ic_close_white_24dp);

			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params1.setMargins(0,MyUtils.dip2px(MainTabActivity.this, 64),MyUtils.dip2px(MainTabActivity.this, 20),0);
			shade_layout.addView(imageClose,params1);
			imageClose.setOnClickListener(v->MainTabActivity.this.hideShade());

			AppCompatImageView imageCenter = new AppCompatImageView(MainTabActivity.this);
			imageCenter.setImageResource(R.drawable.ic_center_text);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,MyUtils.dip2px(this,100));//RelativeLayout.LayoutParams.WRAP_CONTENT
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			shade_layout.addView(imageCenter,params);

			for (int i=0;i<=vs.length;i++) {
				Rect rect = new Rect();
				RelativeLayout.LayoutParams tmp_params;
				if (i == vs.length) {
					bottom_bar.getGlobalVisibleRect(rect);
					tmp_params = new RelativeLayout.LayoutParams(rect.width(),rect.height());
					tmp_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				} else {
					vs[i].getGlobalVisibleRect(rect);
					tmp_params = new RelativeLayout.LayoutParams(rect.width(),rect.height());
					tmp_params.setMargins(rect.left,rect.top,0,0);
				}
				View view = new View(MainTabActivity.this);
				view.setBackgroundResource(R.drawable.dash_border_bg);
				shade_layout.addView(view,tmp_params);
			}
		}

		return true;
	}

	private boolean isDialogShowing = false;
	private NotificationCompat.Builder builder;
	private NotificationManagerCompat notificationManagerCompat;

	private void downloadApp(String appName,AppUpdateBo appVersion, boolean must) {
		final long[] time = {0};
		mCompositeSub.add(RxDownload.getInstance()
				.context(this)
				.maxRetryCount(3)
				.autoInstall(true)
				.download(appVersion.getAndroidUrl(), appName, null)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(downloadStatus -> {
					if (System.currentTimeMillis() - time[0] > 1000) {
						time[0] = System.currentTimeMillis();
						if (builder == null) {
							notificationManagerCompat = NotificationManagerCompat.from(this);
							builder = new NotificationCompat.Builder(MainTabActivity.this);
							builder.setContentTitle("上海中原APP更新")
									.setContentText("正在下载文件，请稍后...")
									.setSmallIcon(R.mipmap.ic_launcher)
									.setAutoCancel(false)
									.setWhen(System.currentTimeMillis());
						}
						builder.setProgress((int) downloadStatus.getTotalSize(), (int) downloadStatus.getDownloadSize(), false);
						notificationManagerCompat.notify(123, builder.build());
						if (must) {
							RxBus.getDefault().send(new DownLoadEvent((int) downloadStatus.getDownloadSize(), (int) downloadStatus.getTotalSize()));
						}
					}
					if (downloadStatus.getDownloadSize() == downloadStatus.getTotalSize() && must) {
						RxBus.getDefault().send(new DownLoadEvent((int) downloadStatus.getDownloadSize(), (int) downloadStatus.getTotalSize()));
					}
				}, throwable -> {
					new AlertDialog.Builder(this)
							.setCancelable(false)
							.setMessage("连接服务器失败,是否重试?")
							.setPositiveButton("确定", (dialog1, which) -> {
								downloadApp(appName, appVersion,must);
								dialog1.dismiss();
							})
							.setNegativeButton("取消", (dialog12, which) -> {
								dialog12.dismiss();
								setAppVersion(appVersion);
							}).show();
					throwable.printStackTrace();
				}, () -> notificationManagerCompat.cancel(123)));
	}

	private boolean isInstallDialogShow = false;
	@Override
	public void setAppVersion(AppUpdateBo appVersion) {
		//是否需要更新
		if (appVersion.getAndroidVerCode() <= MyUtils.getAppVersionCode(this)) {
			SharedPreferencesUtil.saveBoolean(LIMIT_TO_UPDATE, false);
			isUpdating = true;
			mPresenter.getAppAdvert();
			return;
		}

		String appName = "上海中原_" + appVersion.getAndroidVerCode() + ".apk";
		File apkFile = new File(MyUtils.getApkCacheDir(), appName);

		boolean isDownLoad = true;
		if (apkFile.exists() && apkFile.length() > 0){
			if (appVersion.getAndroidMD5().equalsIgnoreCase(MyUtils.getFileMD5(apkFile))){  //true
				isDownLoad = false;
			}
		}

		if (!isDownLoad) {
			if (!isDialogShowing) {
				isDialogShowing = true;
				int dLayout = R.layout.app_update_install;
				boolean type = SharedPreferencesUtil.getBoolean(LIMIT_TO_UPDATE);
				if (type) {
					dLayout = R.layout.app_update_install_confirm;
				}
				if (type || (!type && !isInstallDialogShow)) {
					isInstallDialogShow = true;
					MyUtils.showDiloag(this, dLayout, 260, -1, !type, (layout, dialog) -> {
						layout.findViewById(R.id.submit).setOnClickListener(v -> {
							isDialogShowing = false;
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
							startActivity(intent);
							if (!type) {
								dialog.dismiss();
							}
						});
						if (!type) {
							layout.findViewById(R.id.cancel).setOnClickListener(v -> {
								isDialogShowing = false;
								dialog.dismiss();
							});
						}
					});
				}
			}
		} else {

			int limit = 0;
			try {
				limit = Integer.parseInt(appVersion.getAndroidLimitCode());
			} catch (Exception e) {
				e.printStackTrace();
			}

			//非强制更新
			if (limit <= BuildConfig.VERSION_CODE) {  //BuildConfig.VERSION_CODE
				SharedPreferencesUtil.saveBoolean(LIMIT_TO_UPDATE,false);
				//不显示更新框
				if (appVersion.getIsShowDiagoAndroid() == 0) {
					return;
				}

				float days = (System.currentTimeMillis() - SharedPreferencesUtil.getLong(UPDATE_LATTER_TIME))/(1000*60*60*24);
				boolean isSetLater = SharedPreferencesUtil.getBoolean(UPDATE_LATTER);
				//手动退出，并大于3天
				if ((isSetLater && days >= 3) || !isSetLater) {
					if (!isDialogShowing) {
						isDialogShowing = true;
						SharedPreferencesUtil.saveBoolean(UPDATE_LATTER, false); //复位
						Dialog dialogP = MyUtils.showDiloag(this, R.layout.app_update_dialog_2, 300, -1, true, (layout, dialog) -> {
							String[] details = appVersion.getAndroidMessage().split("Ď");
							TextView title = (TextView) layout.findViewById(R.id.title);
							title.setText(appVersion.getAndroidTitle());
							LinearLayout contents = (LinearLayout) layout.findViewById(R.id.content);
							for (int i = 0; i < details.length; i++) {
								TextView textView = new TextView(MainTabActivity.this);
								textView.setTextColor(getResources().getColor(R.color.grayText));
								textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
								textView.setText(details[i]);
								textView.setLineSpacing(MyUtils.dip2px(MainTabActivity.this, 3), 1);
								if (i > 0) {
									textView.setPadding(0, MyUtils.dip2px(MainTabActivity.this, 5), 0, 0);
								}
								contents.addView(textView);
							}

//						((TextView) layout.findViewById(R.id.content)).setText(appVersion.getAndroidMessage().replaceAll("Ď", "\n"));
							layout.findViewById(R.id.submit).setOnClickListener(v -> {
								isDialogShowing = false;
//								showDialog();
								dialog.dismiss();
								downloadApp(appName, appVersion,false);
							});
							layout.findViewById(R.id.cancel).setOnClickListener(v -> {
								dialog.dismiss();
								isDialogShowing = false;
								SharedPreferencesUtil.saveBoolean(UPDATE_LATTER, true); //点击按钮退出更新
								SharedPreferencesUtil.saveLong(UPDATE_LATTER_TIME,System.currentTimeMillis());
							});
						});
						dialogP.setOnDismissListener(dialog->mPresenter.getAppAdvert());
					}
				}
			} else {
				//强制更新
				SharedPreferencesUtil.saveBoolean(LIMIT_TO_UPDATE, true);
//				if (limitDialog!=null && limitDialog.isShowing()){
//					return;
//				}

				if (!isDialogShowing) {
					isDialogShowing = true;
					MyUtils.showDiloag(this, R.layout.app_update_dialog_1, 300, -1, false, (layout, dialog) -> {
						String[] details = appVersion.getAndroidMessage().split("Ď");
						TextView title = (TextView) layout.findViewById(R.id.title);
						title.setText(appVersion.getAndroidTitle());
						LinearLayout contents = (LinearLayout) layout.findViewById(R.id.content);
						for (int i=0;i<details.length;i++) {
							TextView textView = new TextView(MainTabActivity.this);
							textView.setTextColor(getResources().getColor(R.color.grayText));
							textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
							textView.setText(details[i]);
							textView.setLineSpacing(MyUtils.dip2px(MainTabActivity.this,3) ,1);
							if (i > 0) {
								textView.setPadding(0, MyUtils.dip2px(MainTabActivity.this, 5), 0, 0);
							}
							contents.addView(textView);
						}
						layout.findViewById(R.id.submit).setOnClickListener(v -> {
							dialog.dismiss();
							downloadApp(appName,appVersion,true);
							showDialog();
							isDialogShowing = false;
						});
					});
				}
			}
		}
	}

	public static final String ADVERT_URL_KEY = "ADVERT_URL_KEY";

	@Override
	public void setAppAdvert(AppAdBo advert) {

		if (advert==null || TextUtils.isEmpty(advert.getImgUrl()))return;

//		imgUrl = advert.getImgUrl();
//		advertUrl = advert.getAdvertUrl();
//		checkFile(advert.getImgUrl());

		String localImageUrl = SharedPreferencesUtil.getString(ADVERT_URL_KEY).trim();

//		imgUrl = advert.getImgUrl();
//		advertUrl = advert.getAdvertUrl();
//		checkFile(advert.getImgUrl());

		//广告更新时
		if (!localImageUrl.equalsIgnoreCase(advert.getImgUrl().trim())
				||!SharedPreferencesUtil.getBoolean(AppContents.ADV_SHOW)){
			imgUrl = advert.getImgUrl();
			advertUrl = advert.getAdvertUrl();
			checkFile(advert.getImgUrl());
			SharedPreferencesUtil.saveString(ADVERT_URL_KEY,imgUrl);
			SharedPreferencesUtil.saveBoolean(AppContents.ADV_SHOW, true);
		}
	}

	@Override
	public void showSchoolLoading(boolean showing) {
		if (showing)
			loading_layout.setVisibility(View.VISIBLE);
		else
			loading_layout.setVisibility(View.GONE);
	}

	public static final String LIMIT_TO_UPDATE = "LIMIT_TO_UPDATE";
	public static final String UPDATE_LATTER = "UPDATE_LATTER";
	public static final String UPDATE_LATTER_TIME = "UPDATE_LATTER_TIME";

	private boolean showBottomWindow = false;

	private CompositeSubscription mCompositeSub;

	private List<DropBo> parents = new ArrayList<>();

	private List<GScope> areas;
	private List<RailLine> railLines;

	private ParentAdapter parentAdapter;   //右侧菜单第一级list adapter
	private ChildAdapter childAdapter;     //右侧菜单第二级list adapter
	private SchoolListAdapter schoolListAdapter; //school list adapter
	private int rightType;


	/**
	 * 显示侧滑菜单
	 *
	 * @param
	 */
	public void showMenu(int type) {
		this.rightType = type;

//		main_lv_child.setVisibility(View.GONE);
		child_list_layout.setVisibility(View.GONE);

		parents.clear();

		if (type == 0) {
			parentView.setText("选择地铁");
			childView.setText("选择站点");
			if (railLines == null) return;
			for (RailLine item : railLines) {
				DropBo parentItem = new DropBo(item.getRailLineName(), item.getRailLineID() + "", 0);
				parents.add(parentItem);
			}
		} else if (type == 1) {
			parentView.setText("选择区域");
			childView.setText("选择学校");
			if (areas == null) return;
			for (GScope item : areas) {
				DropBo parentItem = new DropBo(item.getGScopeName(), item.getGScopeId() + "", 0);
				parents.add(parentItem);
			}
		}

		parentAdapter.setData(parents);

		if (!main_container.isDrawerOpen(Gravity.RIGHT)) {
			main_container.openDrawer(Gravity.RIGHT);
		}

		parentAdapter.setSelectedPosition(parentSelectedPosition);
	}

	//站点筛选
	private void closeRightMenu(DropBo parent, DropBo child) {

		if (rightType == 0) {
			if (child == null) {
				map.getEsfByRailLine(parent.getValue(), parent.getText());  //线路
			} else {
				map.getEsfByRailWay(parent.getValue(), parent.getText(), child.getValue(), child.getText()); //站点
//				map.getEsfByRailWay(parent,child);
			}
		} else {
			map.getEsfBySchool(child.getText(), child.getValue(), child.getLat(), child.getLng());  //学校
		}

		if (main_container.isDrawerOpen(Gravity.RIGHT)) {
			main_container.closeDrawer(Gravity.RIGHT);
		}
	}


	/**
	 * 登录监听
	 *
	 * @param appLoginEvent
	 */
	public void onEventMainThread(AppLoginEvent appLoginEvent) {
		if (appLoginEvent.isLogin) {
			chat = null;
			RcUtil.connectRC();
			refreshUnreadCount();
		} else {
			bottom_bar.hideMsg(3);
			refreshShortcutBadger(0);
		}

		if (appLoginEvent.callType == LoginActivity.LOGIN_INTENT_SHORTCUT) {
			//打开委托表单填写页面
			Intent intent = new Intent(this, DeputeActivity.class);
			startActivity(intent);
		} else if (appLoginEvent.callType != LoginActivity.NO_EXCHANGE) {
			current_tab = appLoginEvent.callType;
			view_pager.setCurrentItem(current_tab, false);
		}
	}

	public void changePager(int tab){
		current_tab = tab;
		view_pager.setCurrentItem(tab,false);
	}

	public void changeFoundFragmentTab(int tab){
		found.changeTab(tab);
	}

	/**
	 * 未读数量刷新
	 *
	 * @param e
	 */
	public void onEventMainThread(UnreadEvent e) {
		refreshUnreadCount();
	}

	/**
	 * 新消息监听
	 *
	 * @param event
	 */
	public void onEventMainThread(final Event.OnReceiveMessageEvent event) {
		refreshUnreadCount();
	}

	private void refreshUnreadCount() {
		//获取未读消息数
		int count = RongIM.getInstance().getUnreadCount(Conversation.ConversationType.PRIVATE);
		DataHolder.getInstance().setMessageCount(count);
		if (count > 0)
			bottom_bar.showMsg(3,count);
		else
			bottom_bar.hideMsg(3);
		refreshShortcutBadger(count);
	}

	/**
	 * 刷新桌面角标
	 * @param count
	 */
	private void refreshShortcutBadger(int count){
		/*
		 桌面角标刷新
		 */
		if (!Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
			//华为手机
			if (MyUtils.isHwPhone(this)) {
//				Bundle extra =new Bundle();
//				extra.putString("package", "com.cetnaline.findproperty");
//				extra.putString("class", "SplashActivity");
//				extra.putInt("badgenumber", count);
//				getContentResolver().call(Uri.parse(
//						"content://com.huawei.android.launcher.settings/badge/"),
//						"change_launcher_badge", null, extra);

				return;
			}

			//三星、索尼桌面角标
			if (Build.MANUFACTURER.equalsIgnoreCase("SAMSUNG") || Build.MANUFACTURER.equalsIgnoreCase("sony")) {
				ShortcutBadger.applyCount(MainTabActivity.this, count);
			}
		}
	}

	@BindView(R.id.fl_map_tips)
	FrameLayout fl_map_tips;

	@OnClick(R.id.ic_info_know)
	public void tipClick(){
		fl_map_tips.setVisibility(View.GONE);
	}

	@OnClick(R.id.fl_map_tips)
	public void tipFragmentClick(){

	}

	public static final String IS_FIRST_UP = "IS_FIRST_UP";

	public void showFirstSwipe(){

		if (!SharedPreferencesUtil.getBoolean(IS_FIRST_UP)){
			Observable.timer(200, TimeUnit.MILLISECONDS)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe( time -> {
						fl_map_tips.setVisibility(View.VISIBLE);
						SharedPreferencesUtil.saveBoolean(IS_FIRST_UP, true);
					});
		}
	}

	public void showFragment(int index){
		view_pager.setCurrentItem(index,false);
	}

	private boolean isUpdating;

	private void showDialog(){
		MyUtils.showDiloag(this, R.layout.app_update_dialog_precess, 260, -1, false, ((layout, dialog) -> {
			ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.process);
			TextView number = (TextView) layout.findViewById(R.id.number);
			mCompositeSub.add(RxBus.getDefault().toObservable(DownLoadEvent.class)
					.subscribe(downLoadEvent -> {
						int progress = downLoadEvent.getProgress();
						if (progress==-1){
							isUpdating = false;
							dialog.dismiss();
							MainTabActivity.this.finish();
							return;
						}

						isUpdating = true;

						float num = (float) progress
								/ (float) downLoadEvent.getTotalSize();
						int result = (int) (num * 100); // 计算进度
						number.setText(result + "%");
						progressBar.setProgress(result);

						if (progress == downLoadEvent.getTotalSize()){
							dialog.dismiss();
							isUpdating = false;
						}

					}));
		}));

	}

	/**
	 * 顶部tab向下隐藏动画
	 * @param view
	 * @param isHide
	 */
	private void startAnimation(View view, boolean isHide) {
		ObjectAnimator alphaObject, translateObject;
		if (isHide){
			alphaObject = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
			translateObject = ObjectAnimator.ofFloat(view, "translationY", 0, 600);
		}else {
			alphaObject = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
			translateObject = ObjectAnimator.ofFloat(view, "translationY", 600, 0);
		}

		AnimatorSet set = new AnimatorSet();
		set.playTogether(alphaObject, translateObject);
		//设置动画时间 (作用到每个动画)
		set.setDuration(500);
		set.setInterpolator(new AccelerateInterpolator());
		set.start();
	}

	private void checkFile(String url) {
		String advertName = url.substring(url.lastIndexOf("/")+1, url.length());
		File file = new File(ExternalCacheDirUtil.getAdvertCacheDir(this), advertName);
		if (file.exists()){
			AdvertWindow.getInstance(imgUrl, advertUrl).
					show(getSupportFragmentManager(), "AdvertWindow");
		}else {
//			FileDownLodUtil.downLoad(file, url, handler);
			mCompositeSub.add(
					RxDownload.getInstance()
							.maxRetryCount(3)
							.download(url, advertName, ExternalCacheDirUtil.getAdvertCacheDir(this))
							.compose(SchedulersCompat.applyIoSchedulers())
							.subscribe(new Subscriber<DownloadStatus>() {
								@Override
								public void onCompleted() {
//										AdvertWindow.getInstance(imgUrl, advertUrl).
//												show(getSupportFragmentManager(), "AdvertWindow");

									FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
									transaction.add(AdvertWindow.getInstance(imgUrl, advertUrl), "AdvertWindow");
									transaction.commitAllowingStateLoss();
								}
								@Override
								public void onError(Throwable e) {
									e.printStackTrace();
								}
								@Override
								public void onNext(DownloadStatus downloadStatus) {
								}
							})
			);
		}
}

	@Override
	public void onWakeUpFinish(AppData appData, Error error) {
		if (error == null) {
			Logger.d("SplashActivity wakeup = " + appData.toString());
		} else {
			Logger.d("SplashActivity error : "+error.toString());
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		return "应用主页";
	}
}
