package com.cetnaline.findproperty.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.event.UnreadEvent;
import com.cetnaline.findproperty.ui.activity.AdviserDetailActivity;
import com.cetnaline.findproperty.ui.activity.AdviserListActivity;
import com.cetnaline.findproperty.ui.activity.CommentActivity;
import com.cetnaline.findproperty.ui.activity.ConsultFormActivity;
import com.cetnaline.findproperty.ui.activity.StoreSearchActivity;
import com.cetnaline.findproperty.ui.listadapter.ConversationListFragmentAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.widgets.MenuPopWindow;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.model.Draft;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.utils.ConversationListUtils;
import io.rong.imkit.widget.ArraysDialogFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ReadReceiptMessage;
import io.rong.message.VoiceMessage;
import io.rong.push.RongPushClient;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/7/26.
 */
public class ConversationListFragment extends UriFragment implements AdapterView.OnItemClickListener, ConversationListFragmentAdapter.OnPortraitItemClick {
    private static String TAG = "ConversationListFragment";
    private ConversationListFragmentAdapter mAdapter;
    private SwipeMenuListView mList;
    private TextView mNotificationBar;
    private boolean isShowWithoutConnected = false;
    private ArrayList<Conversation.ConversationType> mSupportConversationList = new ArrayList();
    private ArrayList<Message> mMessageCache = new ArrayList();
    private RongIMClient.ResultCallback<List<Conversation>> mCallback = new RongIMClient.ResultCallback() {
        public void onSuccess(Object o) {
            List<Conversation> conversations = (List<Conversation>) o;
            RLog.d(ConversationListFragment.TAG, "ConversationListFragment initFragment onSuccess callback : list = " + (conversations != null?Integer.valueOf(conversations.size()):"null"));
            if(ConversationListFragment.this.mAdapter != null && ConversationListFragment.this.mAdapter.getCount() != 0) {
                ConversationListFragment.this.mAdapter.clear();
            }

            if(conversations != null && conversations.size() != 0) {
                if(ConversationListFragment.this.mAdapter == null) {
                    ConversationListFragment.this.mAdapter = new ConversationListFragmentAdapter(RongContext.getInstance(),ConversationListFragment.this);
                }

                ConversationListFragment.this.makeUiConversationList(conversations);
                if(ConversationListFragment.this.mList != null && ConversationListFragment.this.mList.getAdapter() != null) {
                    ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                }

            } else {
                if(ConversationListFragment.this.mAdapter != null) {
                    ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                }

//                showNoConversationLayout(true);
            }
        }

        public void onError(RongIMClient.ErrorCode e) {
            RLog.d(ConversationListFragment.TAG, "ConversationListFragment initFragment onError callback, e=" + e);
            if(e.equals(RongIMClient.ErrorCode.IPC_DISCONNECT)) {
                ConversationListFragment.this.isShowWithoutConnected = true;
            }

        }
    };

    private TextView show_nearby;
    private TextView show_all;
    private RelativeLayout rc_content;
    private LinearLayout view_root;
    private LinearLayout no_conversation_layout;
    private TextView mEmptyView;
    private BaseApplication mApp;
    private boolean is_show_title = true;

    private CompositeSubscription mCompositeSubscription;

    private ImageView menu_btn;

    public ConversationListFragment() {
    }

    public static ConversationListFragment getInstance() {
        return new ConversationListFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        RLog.d(TAG, "ConversationListFragment onCreate");
//        StatService.setOn(getActivity(),StatService.EXCEPTION_LOG );
        mCompositeSubscription = new CompositeSubscription();
        super.onCreate(savedInstanceState);
        this.mSupportConversationList.clear();
        RongPushClient.clearAllPushNotifications(RongContext.getInstance());
        RongContext.getInstance().getEventBus().register(this);
    }

    public void onAttach(Activity activity) {
        RLog.d(TAG, "ConversationListFragment onAttach");
        super.onAttach(activity);
    }

    protected void initFragment(Uri uri) {
        Conversation.ConversationType[] conversationType = new Conversation.ConversationType[]{
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,
                Conversation.ConversationType.DISCUSSION,
                Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.CUSTOMER_SERVICE,
                Conversation.ConversationType.CHATROOM,
                Conversation.ConversationType.PUBLIC_SERVICE,
                Conversation.ConversationType.APP_PUBLIC_SERVICE
        };
        RLog.d(TAG, "ConversationListFragment initFragment");
        if(uri == null) {
            RongIM.getInstance().getConversationList(this.mCallback);
        } else {
            Conversation.ConversationType[] arr$ = conversationType;
            int len$ = conversationType.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Conversation.ConversationType type = arr$[i$];
                if(uri.getQueryParameter(type.getName()) != null) {
                    this.mSupportConversationList.add(type);
                    if("true".equals(uri.getQueryParameter(type.getName()))) {
                        RongContext.getInstance().setConversationGatherState(type.getName(), Boolean.valueOf(true));
                    } else if("false".equals(uri.getQueryParameter(type.getName()))) {
                        RongContext.getInstance().setConversationGatherState(type.getName(), Boolean.valueOf(false));
                    }
                }
            }

            if(RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                RLog.d(TAG, "RongCloud haven\'t been connected yet, so the conversation list display blank !!!");
                this.isShowWithoutConnected = true;
            }

            if(this.mSupportConversationList.size() > 0) {
                RongIM.getInstance().getConversationList(this.mCallback, (Conversation.ConversationType[])this.mSupportConversationList.toArray(new Conversation.ConversationType[this.mSupportConversationList.size()]));
            } else {
                RongIM.getInstance().getConversationList(this.mCallback);
            }

        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RLog.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.frg_conversationlist, container, false);
        if (is_show_title) {
            findViewById(view, R.id.title_bar).setVisibility(View.VISIBLE);
        } else {
            findViewById(view, R.id.title_bar).setVisibility(View.GONE);
        }
        this.mNotificationBar = this.findViewById(view, io.rong.imkit.R.id.rc_status_bar);
        this.mNotificationBar.setVisibility(View.GONE);
        //listview init
        this.mList = this.findViewById(view, io.rong.imkit.R.id.rc_list);
        this.view_root = this.findViewById(view, R.id.view_root);
        initList();

        this.show_nearby = this.findViewById(view,R.id.show_nearby);
        this.show_all = this.findViewById(view,R.id.show_all);
        this.rc_content = this.findViewById(view,R.id.rc_content);
        this.no_conversation_layout = this.findViewById(view, R.id.no_conversation_layout);
        mEmptyView = (TextView)this.findViewById(view, 16908292);

        //init rong cloud
        mApp = (BaseApplication) getContext().getApplicationContext();
        if (BuildConfig.APPLICATION_ID.equals(mApp.getCurProcessName()) &&
                RongIM.getInstance().getCurrentConnectionStatus() != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {

            //登录状态
            RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getCurrentConnectionStatus();
            if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                mEmptyView.setText(RongContext.getInstance().getResources().getString(io.rong.imkit.R.string.rc_conversation_list_not_connected));
            } else {
                mEmptyView.setText(RongContext.getInstance().getResources().getString(io.rong.imkit.R.string.rc_conversation_list_empty_prompt));
            }
            ConversationListFragment.this.setNotificationBarVisibility(status);
        } else {
            Logger.i("already connected!");
        }

//        this.mList.setEmptyView(mEmptyView);
        View footView = inflater.inflate(R.layout.conversation_foot_view,null);
        footView.setOnClickListener((v)->{
            //跳转到顾问列表
            Intent intent = new Intent(getActivity(),AdviserListActivity.class);
            startActivity(intent);
        });
        this.mList.addFooterView(footView);

//        //显示顾问列表
//        this.show_all.setOnClickListener(v->{
//            Intent intent = new Intent(getActivity(), AdviserListActivity.class);
//            startActivity(intent);
//        });
//
//        //显示附近5的顾问
//        this.show_nearby.setOnClickListener(v->{
//            Intent intent = new Intent(getActivity(), AdviserListActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString(AdviserListActivity.RANGE,"3000");
//            intent.putExtras(bundle);
//            startActivity(intent);
//        });

        menu_btn = (ImageView) view.findViewById(R.id.menu_btn);
        menu_btn.setOnClickListener(v->{
            MenuPopWindow window = new MenuPopWindow(getActivity(),R.layout.menu_popup_dialog, (contentView, window1) -> {
                TextView menu1 = (TextView) contentView.findViewById(R.id.menu1);
                TextView menu2 = (TextView) contentView.findViewById(R.id.menu2);
                TextView menu3 = (TextView) contentView.findViewById(R.id.menu3);
                menu1.setOnClickListener(v1 ->{
                    Intent intent = new Intent(getActivity(),AdviserListActivity.class);
                    getActivity().startActivity(intent);
                    window1.dismiss();
                });

                menu2.setOnClickListener(v1 ->{
                    Intent intent = new Intent(getActivity(), AdviserListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(AdviserListActivity.RANGE,"3000");
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                    window1.dismiss();
                });

                menu3.setOnClickListener(v1 ->{
                    Intent intent = new Intent(getActivity(),StoreSearchActivity.class);
                    getActivity().startActivity(intent);
                    window1.dismiss();
                });
            });
            window.showPopupWindow(menu_btn, MyUtils.dip2px(getActivity(), 25),MyUtils.dip2px(getActivity(), 30));
        });


        return view;
    }

    /**
     * 初始化listview
     */
    private void initList() {
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.conversation_list_header,null);
        mList.addHeaderView(header);
        //三个快捷菜单
        header.findViewById(R.id.shortcut1).setOnClickListener(v->{
            //政策咨询
            Intent intent = new Intent(getActivity(), ConsultFormActivity.class);
            intent.putExtra(ConsultFormActivity.HAS_SCOPE,true);
            getActivity().startActivity(intent);
        });

        header.findViewById(R.id.shortcut2).setOnClickListener(v->{
            //法律咨询
            Intent intent = new Intent(getActivity(),ConsultFormActivity.class);
            getActivity().startActivity(intent);
        });

        header.findViewById(R.id.shortcut3).setOnClickListener(v->{
            //举报投诉
            Intent intent = new Intent(getActivity(), CommentActivity.class);
            intent.putExtra(CommentActivity.SHOW_TYPE,CommentActivity.CHAT_TYPE);
            getActivity().startActivity(intent);
        });

        mList.setMenuCreator(menu -> {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
            deleteItem.setBackground(R.color.appBaseColor);
            deleteItem.setWidth(MyUtils.dip2px(getActivity(),70));
            deleteItem.setTitle(R.string.conversationlist_btn);
            deleteItem.setTitleColor(Color.WHITE);
            deleteItem.setTitleSize(16);
            menu.addMenuItem(deleteItem);
        });
//        mList.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
//            @Override
//            public void onSwipeStart(int position) {
//                mList.smoothOpenMenu(position);
//            }
//
//            @Override
//            public void onSwipeEnd(int position) {
//
//            }
//        });
        mList.setOnMenuItemClickListener((position, menu, index) -> {
            mList.smoothCloseMenu();
            switch (index) {
                case 0:
                    UIConversation uiConversation = mAdapter.getItem(position);
                    String type = uiConversation.getConversationType().getName();
                    if(RongContext.getInstance().getConversationListBehaviorListener() != null) {
                        boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationLongClick(getActivity(), mList.getChildAt(position), uiConversation);
                        if(isDealt) {
                            return false;
                        }
                    }
                    RongIM.getInstance().removeConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            //更新未读消息数量
                            DataHolder.getInstance().setMessageCount(DataHolder.getInstance().getMessageCount() - uiConversation.getUnReadMessageCount() );
                            EventBus.getDefault().post(new UnreadEvent());

                            List<Conversation> conversations = RongIMClient.getInstance().getConversationList();
                            if (conversations == null || conversations.size() == 0){
//                                    showNoConversationLayout(true);
                                EventBus.getDefault().post(new NormalEvent(NormalEvent.SHOW_NO_CONVERSATION_LAYOUT));
//                                    mAdapter.clear();
//                                    mAdapter.notifyDataSetChanged();
                            } else {
                                //                    mList.turnToNormal();
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                        }
                    });

                    //删除聊天记录
                    RongIMClient.getInstance().deleteMessages(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                        }
                    });
                    break;
            }
            return false;
        });

        mList.setOnItemClickListener((adapterView, view, i, l) -> {
            UIConversation data = mAdapter.getItem(i-1);
            Conversation.ConversationType type = data.getConversationType();
            if (RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
                RongIM.getInstance().startSubConversationList(getActivity(), type);
            } else {
                if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
                    boolean isDefault = RongContext.getInstance().getConversationListBehaviorListener().onConversationClick(getActivity(), view, data);
                    if (isDefault) {
                        return;
                    }
                }

                RongIM.getInstance().startConversation(getActivity(), type, data.getConversationTargetId(), data.getUIConversationTitle());
                //刷新未读消息数量
                DataHolder.getInstance().setMessageCount(DataHolder.getInstance().getMessageCount()  - data.getUnReadMessageCount());
                EventBus.getDefault().post(new UnreadEvent());
                data.setUnReadMessageCount(0);
            }
        });
    }

    /**
     * 设置是否显示title
     * @param showTitile
     */
    public void showTitle(boolean showTitile) {
        is_show_title = showTitile;
    }

    /**
     * 显示无会话布局
     * @param flag
     */
    private void showNoConversationLayout(boolean flag){
        if (flag) {
            rc_content.setVisibility(View.GONE);
            no_conversation_layout.setVisibility(View.VISIBLE);
        } else {
            rc_content.setVisibility(View.VISIBLE);
            no_conversation_layout.setVisibility(View.GONE);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(this.mAdapter == null) {
            this.mAdapter = new ConversationListFragmentAdapter(RongContext.getInstance(), ConversationListFragment.this);
        }

        this.mList.setAdapter(this.mAdapter);
//        this.mList.setOnItemClickListener(this);
        this.mAdapter.setOnPortraitItemClick(this);
        super.onViewCreated(view, savedInstanceState);
    }

    public void onResume() {
//        StatService.onResume(this);
        super.onResume();
        if(!RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
            RLog.d(TAG, "RongCloud haven\'t been connected yet, so the conversation list display blank !!!");
            this.isShowWithoutConnected = true;
        } else {
            RLog.d(TAG, "onResume current connect status is:" + RongIM.getInstance().getCurrentConnectionStatus());
            RongPushClient.clearAllPushNotifications(RongContext.getInstance());
            RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getCurrentConnectionStatus();
            this.setNotificationBarVisibility(status);
        }

        mAdapter.notifyDataSetChanged();

        if (is_show_title) {
            view_root.setPadding(0,0,0,MyUtils.dip2px(view_root.getContext(),50));
        }
    }

    private void setNotificationBarVisibility(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        if(!this.getResources().getBoolean(io.rong.imkit.R.bool.rc_is_show_warning_notification)) {
            RLog.e(TAG, "rc_is_show_warning_notification is disabled.");
        } else {
            String content = null;
            if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE)) {
                content = this.getResources().getString(io.rong.imkit.R.string.rc_notice_network_unavailable);
            } else if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {
                content = this.getResources().getString(io.rong.imkit.R.string.rc_notice_tick);
            } else if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                this.mNotificationBar.setVisibility(View.GONE);
            } else if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                content = this.getResources().getString(io.rong.imkit.R.string.rc_notice_network_unavailable);
            } else if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
                content = this.getResources().getString(io.rong.imkit.R.string.rc_notice_connecting);
            }

            if(content != null) {
                if(this.mNotificationBar.getVisibility() == View.GONE) {
                    final String finalContent = content;
                    this.getHandler().postDelayed(new Runnable() {
                        public void run() {
                            if(!RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                                ConversationListFragment.this.mNotificationBar.setVisibility(View.GONE);
                                ConversationListFragment.this.mNotificationBar.setText(finalContent);
                            }
                        }
                    }, 4000L);
                } else {
                    this.mNotificationBar.setText(content);
                }
            }

        }
    }

    public void onDestroy() {
        RLog.d(TAG, "onDestroy");
        RongContext.getInstance().getEventBus().unregister(this);
        this.getHandler().removeCallbacksAndMessages((Object)null);

        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    public void onPause() {
        RLog.d(TAG, "onPause");
//        StatService.onPause(this);
        super.onPause();
    }

    public boolean onBackPressed() {
        return false;
    }

    public void setAdapter(ConversationListFragmentAdapter adapter) {
        if(this.mAdapter != null) {
            this.mAdapter.clear();
        }

        this.mAdapter = adapter;
        if(this.mList != null) {
            this.mList.setAdapter(adapter);
        }

    }

    public ConversationListFragmentAdapter getAdapter() {
        return this.mAdapter;
    }

    public void onEventMainThread(NormalEvent event) {
        if (event.type == NormalEvent.SHOW_NO_CONVERSATION_LAYOUT) {
//            showNoConversationLayout(true);
        }
    }

    public void onEventMainThread(Event.ConnectEvent event) {
        RLog.d(TAG, "onEventMainThread Event.ConnectEvent: isListRetrieved = " + this.isShowWithoutConnected);
        if(this.isShowWithoutConnected) {
            if(this.mSupportConversationList.size() > 0) {
                RongIM.getInstance().getConversationList(this.mCallback, (Conversation.ConversationType[])this.mSupportConversationList.toArray(new Conversation.ConversationType[this.mSupportConversationList.size()]));
            } else {
                RongIM.getInstance().getConversationList(this.mCallback);
            }

            TextView mEmptyView = (TextView)this.mList.getEmptyView();
            mEmptyView.setText(RongContext.getInstance().getResources().getString(io.rong.imkit.R.string.rc_conversation_list_empty_prompt));
            this.isShowWithoutConnected = false;
        } else {
            RongIM.getInstance().getConversationList(this.mCallback);
        }
    }

    public void onEventMainThread(Event.ReadReceiptEvent event) {
        if(this.mAdapter == null) {
            Logger.e("the conversation list adapter is null.");
        } else {
            int originalIndex = this.mAdapter.findPosition(event.getMessage().getConversationType(), event.getMessage().getTargetId());
            boolean gatherState = RongContext.getInstance().getConversationGatherState(event.getMessage().getConversationType().getName()).booleanValue();
            if(!gatherState && originalIndex >= 0) {
                UIConversation conversation = (UIConversation)this.mAdapter.getItem(originalIndex);
                ReadReceiptMessage content = (ReadReceiptMessage)event.getMessage().getContent();
                if(content.getLastMessageSendTime() >= conversation.getUIConversationTime() && conversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                    conversation.setSentStatus(Message.SentStatus.READ);
                    this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
                }
            }

        }
    }

    public void onEventMainThread(final Event.OnReceiveMessageEvent event) {
        Logger.i("DQF Receive MessageEvent: id=" + event.getMessage().getTargetId() + ", type=" + event.getMessage().getConversationType());

        //聊天时接收到的系统提示消息不做刷新处理
        if (event.getMessage().getObjectName().equals("RC:SysMsg")) {
            return;
        }

        //将获取到的消息附加发送者信息
        String targetId = event.getMessage().getTargetId();
        getCachedUserInfo(targetId);
        mList.smoothScrollToPosition(0);

//        showNoConversationLayout(false);
        if((this.mSupportConversationList.size() == 0
                || this.mSupportConversationList.contains(event.getMessage().getConversationType())) && (this.mSupportConversationList.size() != 0
                || event.getMessage().getConversationType() != Conversation.ConversationType.CHATROOM && event.getMessage().getConversationType() != Conversation.ConversationType.CUSTOMER_SERVICE)) {
            if(this.mAdapter == null) {
                Logger.i("the conversation list adapter is null. Cache the received message firstly!!!");
                this.mMessageCache.add(event.getMessage());
            } else {
                int originalIndex = this.mAdapter.findPosition(event.getMessage().getConversationType(), event.getMessage().getTargetId());
                UIConversation uiConversation = this.makeUiConversation(event.getMessage(), originalIndex);
//                uiConversation.setUIConversationTitle(event.getMessage().getContent().getUserInfo().getName());     //修改会话名称为用户名称
                int newPosition = ConversationListUtils.findPositionForNewConversation(uiConversation, this.mAdapter);
                if(originalIndex < 0) {
                    this.mAdapter.add(uiConversation, newPosition);
                } else if(originalIndex != newPosition) {
                    this.mAdapter.remove(originalIndex);
                    this.mAdapter.add(uiConversation, newPosition);
                }

                this.mAdapter.notifyDataSetChanged();
                //过滤当前会话时的消息数量刷新
                if(event.getMessage().getMessageId() > 0 && !uiConversation.getConversationTargetId().equals(DataHolder.getInstance().getChatTarget())) {
                    this.refreshUnreadCount(event.getMessage().getConversationType(), event.getMessage().getTargetId());
                }

                if(RongContext.getInstance().getConversationGatherState(event.getMessage().getConversationType().getName()).booleanValue()) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback() {
                        public void onSuccess(Object o) {
                            List<Conversation> conversations = (List<Conversation>) o;
                            if(conversations != null && conversations.size() != 0) {
                                Iterator i$ = conversations.iterator();

                                while(i$.hasNext()) {
                                    Conversation conv = (Conversation)i$.next();
                                    if(conv.getConversationType().equals(event.getMessage().getConversationType()) && conv.getTargetId().equals(event.getMessage().getTargetId())) {
                                        int pos = ConversationListFragment.this.mAdapter.findPosition(conv.getConversationType(), conv.getTargetId());
                                        if(pos >= 0) {
                                            (ConversationListFragment.this.mAdapter.getItem(pos)).setDraft(conv.getDraft());
                                            if(TextUtils.isEmpty(conv.getDraft())) {
                                                (ConversationListFragment.this.mAdapter.getItem(pos)).setSentStatus(null);
                                            } else {
                                                (ConversationListFragment.this.mAdapter.getItem(pos)).setSentStatus(conv.getSentStatus());
                                            }

                                            ConversationListFragment.this.mAdapter.getView(pos, ConversationListFragment.this.mList.getChildAt(pos - ConversationListFragment.this.mList.getFirstVisiblePosition()), ConversationListFragment.this.mList);
                                        }
                                        break;
                                    }
                                }

                            }
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{event.getMessage().getConversationType()});
                }

            }
        } else {
            Logger.i("Not included in conversation list. Return directly!");
        }
    }

    /**
     * 这里监听消息发送
     * @param message
     */
    public void onEventMainThread(Message message) {
        RLog.d(TAG, "onEventMainThread Receive Message: name=" + message.getObjectName() + ", type=" + message.getConversationType());

//        String msg = new String(message.getContent().encode());
//        Gson gson = new Gson();
//        RcSendMessageBean sendMessageBean = gson.fromJson(msg, RcSendMessageBean.class);
//        if (!("").equals(sendMessageBean.content)) { //message.getUId() != null &&
//            //将发送的消息推送到服务器
//            Map<String, String> params = new HashMap<>();
//            params.put("CityCode", "021");
//            params.put("AppName", "APP");
//            params.put("Target", ConversationActivity.CURRENT_TARGET_TYPE);
//            params.put("TargetValue", ConversationActivity.CURRENT_TARGET_VALUE);
//            params.put("RcSender", message.getSenderUserId());
//            params.put("RcReceiver", message.getTargetId());
//            params.put("MsgType", "TextMessage");
//            params.put("MsgContent", sendMessageBean.content);
//
//            mCompositeSubscription.add(ApiRequest.messageRecord(params).subscribe(new Subscriber<ApiResponse>() {
//                @Override
//                public void onCompleted() {}
//
//                @Override
//                public void onError(Throwable e) {}
//
//                @Override
//                public void onNext(ApiResponse apiResponse) {
//                    Logger.i("推送消息成功,target:"+ConversationActivity.CURRENT_TARGET_TYPE+","+ConversationActivity.CURRENT_TARGET_VALUE);
//                }
//            }));
//        }


        if(this.mSupportConversationList.size() != 0 && !this.mSupportConversationList.contains(message.getConversationType()) || this.mSupportConversationList.size() == 0 && (message.getConversationType() == Conversation.ConversationType.CHATROOM || message.getConversationType() == Conversation.ConversationType.CUSTOMER_SERVICE)) {
            RLog.d(TAG, "onEventBackgroundThread Not included in conversation list. Return directly!");
        } else {
            int originalIndex = this.mAdapter.findPosition(message.getConversationType(), message.getTargetId());
            UIConversation uiConversation = this.makeUiConversation(message, originalIndex);
            int newPosition = ConversationListUtils.findPositionForNewConversation(uiConversation, this.mAdapter);
            if(originalIndex >= 0) {
                if(newPosition == originalIndex) {
                    this.mAdapter.getView(newPosition, this.mList.getChildAt(newPosition - this.mList.getFirstVisiblePosition()), this.mList);
                } else {
                    this.mAdapter.remove(originalIndex);
                    this.mAdapter.add(uiConversation, newPosition);
                    this.mAdapter.notifyDataSetChanged();
                }
            } else {
                this.mAdapter.add(uiConversation, newPosition);
                this.mAdapter.notifyDataSetChanged();
            }

        }
    }

    public void onEventMainThread(MessageContent content) {
        RLog.d(TAG, "onEventMainThread: MessageContent");

        for(int index = 0; index < this.mAdapter.getCount(); ++index) {
            UIConversation tempUIConversation = (UIConversation)this.mAdapter.getItem(index);
            if(content != null && tempUIConversation.getMessageContent() != null && tempUIConversation.getMessageContent() == content) {
                tempUIConversation.setMessageContent(content);
                tempUIConversation.setConversationContent(tempUIConversation.buildConversationContent(tempUIConversation));
                if(index >= this.mList.getFirstVisiblePosition()) {
                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
                }
            } else {
                RLog.e(TAG, "onEventMainThread MessageContent is null");
            }
        }

    }

    public void onEventMainThread(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        RLog.d(TAG, "ConnectionStatus, " + status.toString());
        this.setNotificationBarVisibility(status);
        if(this.isShowWithoutConnected && status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
            this.isShowWithoutConnected = false;
        }

    }

    public void onEventMainThread(Event.CreateDiscussionEvent createDiscussionEvent) {
        RLog.d(TAG, "onEventBackgroundThread: createDiscussionEvent");
        UIConversation conversation = new UIConversation();
        conversation.setConversationType(Conversation.ConversationType.DISCUSSION);
        if(createDiscussionEvent.getDiscussionName() != null) {
            conversation.setUIConversationTitle(createDiscussionEvent.getDiscussionName());
        } else {
            conversation.setUIConversationTitle("");
        }

        conversation.setConversationTargetId(createDiscussionEvent.getDiscussionId());
        conversation.setUIConversationTime(System.currentTimeMillis());
        boolean isGather = RongContext.getInstance().getConversationGatherState(Conversation.ConversationType.DISCUSSION.getName()).booleanValue();
        conversation.setConversationGatherState(isGather);
        if(isGather) {
            String gatherPosition = RongContext.getInstance().getGatheredConversationTitle(conversation.getConversationType());
            conversation.setUIConversationTitle(gatherPosition);
        }

        int gatherPosition1 = this.mAdapter.findGatherPosition(Conversation.ConversationType.DISCUSSION);
        if(gatherPosition1 == -1) {
            this.mAdapter.add(conversation, ConversationListUtils.findPositionForNewConversation(conversation, this.mAdapter));
            this.mAdapter.notifyDataSetChanged();
        }

    }

    public void onEventMainThread(Draft draft) {
        if(draft != null) {
            Conversation.ConversationType curType = Conversation.ConversationType.setValue(draft.getType().intValue());
            if(curType != null && this.mSupportConversationList.contains(curType)) {
                RLog.i(TAG, "Draft ConversationType : " + curType.getName());
                int position = this.mAdapter.findPosition(curType, draft.getId());
                UIConversation temp;
                if(position >= 0) {
                    temp = (UIConversation)this.mAdapter.getItem(position);
                    if(temp.getConversationTargetId().equals(draft.getId())) {
                        temp.setDraft(draft.getContent());
                        if(!TextUtils.isEmpty(draft.getContent())) {
                            temp.setSentStatus((Message.SentStatus)null);
                        }

                        this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
                    }
                } else if(!TextUtils.isEmpty(draft.getContent())) {
                    temp = new UIConversation();
                    temp.setConversationType(Conversation.ConversationType.setValue(draft.getType().intValue()));
                    temp.setConversationTargetId(draft.getId());
                    UserInfo curUserInfo = RongContext.getInstance().getUserInfoFromCache(draft.getId());
                    if(curUserInfo != null) {
                        temp.setUIConversationTitle(curUserInfo.getName());
                        if(curUserInfo.getPortraitUri() != null) {
                            temp.setIconUrl(curUserInfo.getPortraitUri());
                        }
                    }

                    temp.setUIConversationTime(System.currentTimeMillis());
                    temp.setConversationSenderId(RongIMClient.getInstance().getCurrentUserId());
                    temp.setDraft(draft.getContent());
                    int newPosition = ConversationListUtils.findPositionForNewConversation(temp, this.mAdapter);
                    if(newPosition >= 0) {
                        this.mAdapter.add(temp, newPosition);
                        this.mAdapter.notifyDataSetChanged();
                    }
                }

            } else {
                RLog.w(TAG, curType + " should not show in conversation list.");
            }
        }
    }

    public void onEventMainThread(Group groupInfo) {
        int count = this.mAdapter.getCount();
        RLog.d(TAG, "onEventMainThread Group: name=" + groupInfo.getName() + ", id=" + groupInfo.getId());
        if(groupInfo.getName() != null) {
            for(int i = 0; i < count; ++i) {
                UIConversation item = (UIConversation)this.mAdapter.getItem(i);
                if(item != null && item.getConversationType().equals(Conversation.ConversationType.GROUP) && item.getConversationTargetId().equals(groupInfo.getId())) {
                    boolean gatherState = RongContext.getInstance().getConversationGatherState(item.getConversationType().getName()).booleanValue();
                    if(gatherState) {
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        Spannable messageData = RongContext.getInstance().getMessageTemplate(item.getMessageContent().getClass()).getContentSummary(item.getMessageContent());
                        if(item.getMessageContent() instanceof VoiceMessage) {
                            boolean isListened = RongIM.getInstance().getConversation(item.getConversationType(), item.getConversationTargetId()).getReceivedStatus().isListened();
                            if(isListened) {
                                messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
                            } else {
                                messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_voice_color)), 0, messageData.length(), 33);
                            }
                        }

                        builder.append(groupInfo.getName()).append(" : ").append(messageData);
                        item.setConversationContent(builder);
                        if(groupInfo.getPortraitUri() != null) {
                            item.setIconUrl(groupInfo.getPortraitUri());
                        }
                    } else {
                        item.setUIConversationTitle(groupInfo.getName());
                        if(groupInfo.getPortraitUri() != null) {
                            item.setIconUrl(groupInfo.getPortraitUri());
                        }
                    }

                    this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                }
            }

        }
    }

    public void onEventMainThread(Discussion discussion) {
        int count = this.mAdapter.getCount();
        RLog.d(TAG, "onEventMainThread Discussion: name=" + discussion.getName() + ", id=" + discussion.getId());

        for(int i = 0; i < count; ++i) {
            UIConversation item = (UIConversation)this.mAdapter.getItem(i);
            if(item != null && item.getConversationType().equals(Conversation.ConversationType.DISCUSSION) && item.getConversationTargetId().equals(discussion.getId())) {
                boolean gatherState = RongContext.getInstance().getConversationGatherState(item.getConversationType().getName()).booleanValue();
                if(gatherState) {
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    Spannable messageData = RongContext.getInstance().getMessageTemplate(item.getMessageContent().getClass()).getContentSummary(item.getMessageContent());
                    if(messageData != null) {
                        if(item.getMessageContent() instanceof VoiceMessage) {
                            boolean isListened = RongIM.getInstance().getConversation(item.getConversationType(), item.getConversationTargetId()).getReceivedStatus().isListened();
                            if(isListened) {
                                messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
                            } else {
                                messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_voice_color)), 0, messageData.length(), 33);
                            }
                        }

                        builder.append(discussion.getName()).append(" : ").append(messageData);
                    } else {
                        builder.append(discussion.getName());
                    }

                    item.setConversationContent(builder);
                } else {
                    item.setUIConversationTitle(discussion.getName());
                }

                this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
            }
        }

    }

    public void onEventMainThread(Event.GroupUserInfoEvent event) {
        int count = this.mAdapter.getCount();
        GroupUserInfo userInfo = event.getUserInfo();
        Log.d("qinxiao", "GroupUserInfoEvent: " + userInfo.getUserId());
        if(userInfo != null && userInfo.getNickname() != null) {
            for(int i = 0; i < count; ++i) {
                UIConversation uiConversation = (UIConversation)this.mAdapter.getItem(i);
                Conversation.ConversationType type = uiConversation.getConversationType();
                boolean gatherState = RongContext.getInstance().getConversationGatherState(uiConversation.getConversationType().getName()).booleanValue();
                boolean isShowName;
                if(uiConversation.getMessageContent() == null) {
                    isShowName = false;
                } else {
                    ProviderTag messageData = RongContext.getInstance().getMessageProviderTag(uiConversation.getMessageContent().getClass());
                    isShowName = messageData != null?messageData.showSummaryWithName():false;
                }

                if(!gatherState && isShowName && type.equals(Conversation.ConversationType.GROUP) && uiConversation.getConversationSenderId().equals(userInfo.getUserId())) {
                    Spannable var12 = RongContext.getInstance().getMessageTemplate(uiConversation.getMessageContent().getClass()).getContentSummary(uiConversation.getMessageContent());
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    if(uiConversation.getMessageContent() instanceof VoiceMessage) {
                        boolean isListened = RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId()).getReceivedStatus().isListened();
                        if(isListened) {
                            var12.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_text_color_secondary)), 0, var12.length(), 33);
                        } else {
                            var12.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_voice_color)), 0, var12.length(), 33);
                        }
                    }

                    if(uiConversation.getConversationTargetId().equals(userInfo.getGroupId())) {
                        uiConversation.addNickname(userInfo.getUserId());
                        builder.append(userInfo.getNickname()).append(" : ").append(var12);
                        uiConversation.setConversationContent(builder);
                    }

                    this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                }
            }

        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        int count = this.mAdapter.getCount();
        if(userInfo.getName() != null) {
            for(int i = 0; i < count; ++i) {
                UIConversation temp = (UIConversation)this.mAdapter.getItem(i);
                String type = temp.getConversationType().getName();
                boolean gatherState = RongContext.getInstance().getConversationGatherState(temp.getConversationType().getName()).booleanValue();
                if(!temp.hasNickname(userInfo.getUserId())) {
                    boolean isShowName;
                    if(temp.getMessageContent() == null) {
                        isShowName = false;
                    } else {
                        ProviderTag messageData = RongContext.getInstance().getMessageProviderTag(temp.getMessageContent().getClass());
                        isShowName = messageData != null?messageData.showSummaryWithName():false;
                    }

                    SpannableStringBuilder builder;
                    boolean isListened;
                    Spannable var11;
                    if(!gatherState && isShowName && (type.equals("group") || type.equals("discussion")) && temp.getConversationSenderId().equals(userInfo.getUserId())) {
                        var11 = RongContext.getInstance().getMessageTemplate(temp.getMessageContent().getClass()).getContentSummary(temp.getMessageContent());
                        builder = new SpannableStringBuilder();
                        if(temp.getMessageContent() instanceof VoiceMessage) {
                            isListened = RongIM.getInstance().getConversation(temp.getConversationType(), temp.getConversationTargetId()).getReceivedStatus().isListened();
                            if(isListened) {
                                var11.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_text_color_secondary)), 0, var11.length(), 33);
                            } else {
                                var11.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_voice_color)), 0, var11.length(), 33);
                            }
                        }

                        builder.append(userInfo.getName()).append(" : ").append(var11);
                        temp.setConversationContent(builder);
                        if (this.mList != null)
                            this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                    } else if(temp.getConversationTargetId().equals(userInfo.getUserId())) {
                        if(!gatherState && (type.equals("private") || type.equals("system") || type.equals("customer_service"))) {
                            temp.setUIConversationTitle(userInfo.getName());
                            temp.setIconUrl(userInfo.getPortraitUri());
                        } else if(isShowName) {
                            var11 = RongContext.getInstance().getMessageTemplate(temp.getMessageContent().getClass()).getContentSummary(temp.getMessageContent());
                            builder = new SpannableStringBuilder();
                            if(var11 != null) {
                                if(temp.getMessageContent() instanceof VoiceMessage) {
                                    isListened = RongIM.getInstance().getConversation(temp.getConversationType(), temp.getConversationTargetId()).getReceivedStatus().isListened();
                                    if(isListened) {
                                        var11.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_text_color_secondary)), 0, var11.length(), 33);
                                    } else {
                                        var11.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(io.rong.imkit.R.color.rc_voice_color)), 0, var11.length(), 33);
                                    }
                                }

                                builder.append(userInfo.getName()).append(" : ").append(var11);
                            } else {
                                builder.append(userInfo.getName());
                            }

                            temp.setConversationContent(builder);
                            if(!type.equals("group")) {
                                temp.setIconUrl(userInfo.getPortraitUri());
                            }
                        }
                        if (this.mList != null) {
                            this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                        }
                    }
                }
            }

        }
    }

    public void onEventMainThread(PublicServiceProfile accountInfo) {
        int count = this.mAdapter.getCount();
        boolean gatherState = RongContext.getInstance().getConversationGatherState(accountInfo.getConversationType().getName()).booleanValue();

        for(int i = 0; i < count; ++i) {
            if(((UIConversation)this.mAdapter.getItem(i)).getConversationType().equals(accountInfo.getConversationType()) && ((UIConversation)this.mAdapter.getItem(i)).getConversationTargetId().equals(accountInfo.getTargetId()) && !gatherState) {
                ((UIConversation)this.mAdapter.getItem(i)).setUIConversationTitle(accountInfo.getName());
                ((UIConversation)this.mAdapter.getItem(i)).setIconUrl(accountInfo.getPortraitUri());
                this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                break;
            }
        }

    }

    public void onEventMainThread(Event.PublicServiceFollowableEvent event) {
        if(event != null && !event.isFollow()) {
            int originalIndex = this.mAdapter.findPosition(event.getConversationType(), event.getTargetId());
            if(originalIndex >= 0) {
                this.mAdapter.remove(originalIndex);
                this.mAdapter.notifyDataSetChanged();
            }
        }

    }

    public void onEventMainThread(final Event.ConversationUnreadEvent unreadEvent) {
        int targetIndex = this.mAdapter.findPosition(unreadEvent.getType(), unreadEvent.getTargetId());
        if(targetIndex >= 0) {
            UIConversation temp = (UIConversation)this.mAdapter.getItem(targetIndex);
            boolean gatherState = temp.getConversationGatherState();
            if(gatherState) {
                RongIM.getInstance().getUnreadCount(new RongIMClient.ResultCallback() {
                    public void onSuccess(Object o) {
                        Integer count = (Integer) o;
                        int pos = ConversationListFragment.this.mAdapter.findPosition(unreadEvent.getType(), unreadEvent.getTargetId());
                        if(pos >= 0) {
                            ((UIConversation)ConversationListFragment.this.mAdapter.getItem(pos)).setUnReadMessageCount(count.intValue());
                            ConversationListFragment.this.mAdapter.getView(pos, ConversationListFragment.this.mList.getChildAt(pos - ConversationListFragment.this.mList.getFirstVisiblePosition()), ConversationListFragment.this.mList);
                        }

                    }

                    public void onError(RongIMClient.ErrorCode e) {
                        System.err.print("Throw exception when get unread message count from ipc remote side!");
                    }
                }, new Conversation.ConversationType[]{unreadEvent.getType()});
            } else {
                temp.setUnReadMessageCount(0);
                RLog.d(TAG, "onEventMainThread ConversationUnreadEvent: set unRead count to be 0");
                this.mAdapter.getView(targetIndex, this.mList.getChildAt(targetIndex - this.mList.getFirstVisiblePosition()), this.mList);
            }
        }

    }

    public void onEventMainThread(final Event.ConversationTopEvent setTopEvent) throws IllegalAccessException {
        int originalIndex = this.mAdapter.findPosition(setTopEvent.getConversationType(), setTopEvent.getTargetId());
        if(originalIndex >= 0) {
            UIConversation temp = (UIConversation)this.mAdapter.getItem(originalIndex);
            boolean originalValue = temp.isTop();
            if(originalValue != setTopEvent.isTop()) {
                if(temp.getConversationGatherState()) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback() {
                        public void onSuccess(Object o) {
                            List<Conversation> conversations = (List<Conversation>) o;
                            if(conversations != null && conversations.size() != 0) {
                                UIConversation newConversation = ConversationListFragment.this.makeUIConversationFromList(conversations);
                                int pos = ConversationListFragment.this.mAdapter.findPosition(setTopEvent.getConversationType(), setTopEvent.getTargetId());
                                if(pos >= 0) {
                                    ConversationListFragment.this.mAdapter.remove(pos);
                                }

                                int newIndex = ConversationListUtils.findPositionForNewConversation(newConversation, ConversationListFragment.this.mAdapter);
                                ConversationListFragment.this.mAdapter.add(newConversation, newIndex);
                                ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                            }
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{temp.getConversationType()});
                } else {
                    int newIndex;
                    if(originalValue) {
                        temp.setTop(false);
                        newIndex = ConversationListUtils.findPositionForCancleTop(originalIndex, this.mAdapter);
                    } else {
                        temp.setTop(true);
                        newIndex = ConversationListUtils.findPositionForSetTop(temp, this.mAdapter);
                    }

                    if(originalIndex == newIndex) {
                        this.mAdapter.getView(newIndex, this.mList.getChildAt(newIndex - this.mList.getFirstVisiblePosition()), this.mList);
                    } else {
                        this.mAdapter.remove(originalIndex);
                        this.mAdapter.add(temp, newIndex);
                        this.mAdapter.notifyDataSetChanged();
                    }
                }

            }
        } else {
            throw new IllegalAccessException("the item has already been deleted!");
        }
    }

    public void onEventMainThread(final Event.ConversationRemoveEvent removeEvent) {
        int removedIndex = this.mAdapter.findPosition(removeEvent.getType(), removeEvent.getTargetId());
        boolean gatherState = RongContext.getInstance().getConversationGatherState(removeEvent.getType().getName()).booleanValue();
        if(!gatherState) {
            if(removedIndex >= 0) {
                this.mAdapter.remove(removedIndex);
                this.mAdapter.notifyDataSetChanged();
            }
        } else if(removedIndex >= 0) {
            RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback() {
                public void onSuccess(Object o) {
                    List<Conversation> conversationList = (List<Conversation>) o;
                    int oldPos = ConversationListFragment.this.mAdapter.findPosition(removeEvent.getType(), removeEvent.getTargetId());
                    if(conversationList != null && conversationList.size() != 0) {
                        UIConversation newConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
                        if(oldPos >= 0) {
                            ConversationListFragment.this.mAdapter.remove(oldPos);
                        }

                        int newIndex = ConversationListUtils.findPositionForNewConversation(newConversation, ConversationListFragment.this.mAdapter);
                        ConversationListFragment.this.mAdapter.add(newConversation, newIndex);
                        ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                    } else {
                        if(oldPos >= 0) {
                            ConversationListFragment.this.mAdapter.remove(oldPos);
                        }

                        ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                    }
                }

                public void onError(RongIMClient.ErrorCode e) {
                }
            }, new Conversation.ConversationType[]{removeEvent.getType()});
        }

    }

    public void onEventMainThread(Event.ClearConversationEvent clearConversationEvent) {
        List typeList = clearConversationEvent.getTypes();

        for(int i = this.mAdapter.getCount() - 1; i >= 0; --i) {
            if(typeList.indexOf(((UIConversation)this.mAdapter.getItem(i)).getConversationType()) >= 0) {
                this.mAdapter.remove(i);
            }
        }

        this.mAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(Event.MessageDeleteEvent event) {
        int count = this.mAdapter.getCount();

        for(int i = 0; i < count; ++i) {
            if(event.getMessageIds().contains(Integer.valueOf(((UIConversation)this.mAdapter.getItem(i)).getLatestMessageId()))) {
                boolean gatherState = ((UIConversation)this.mAdapter.getItem(i)).getConversationGatherState();
                if(gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback() {
                        public void onSuccess(Object o) {
                            List<Conversation> conversationList = (List<Conversation>) o;
                            if(conversationList != null && conversationList.size() != 0) {
                                UIConversation uiConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
                                int oldPos = ConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
                                if(oldPos >= 0) {
                                    ConversationListFragment.this.mAdapter.remove(oldPos);
                                }

                                int newIndex = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
                                ConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
                                ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                            }
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{((UIConversation)this.mAdapter.getItem(i)).getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(((UIConversation)this.mAdapter.getItem(i)).getConversationType(), ((UIConversation)this.mAdapter.getItem(i)).getConversationTargetId(), new RongIMClient.ResultCallback() {
                        public void onSuccess(Object o) {
                            Conversation conversation = (Conversation) o;
                            if(conversation == null) {
                                RLog.d(ConversationListFragment.TAG, "onEventMainThread getConversation : onSuccess, conversation = null");
                            } else {
                                UIConversation temp = UIConversation.obtain(conversation, false);
                                int pos = ConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if(pos >= 0) {
                                    ConversationListFragment.this.mAdapter.remove(pos);
                                }

                                int newPosition = ConversationListUtils.findPositionForNewConversation(temp, ConversationListFragment.this.mAdapter);
                                ConversationListFragment.this.mAdapter.add(temp, newPosition);
                                ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                            }
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    public void onEventMainThread(Event.ConversationNotificationEvent notificationEvent) {
        int originalIndex = this.mAdapter.findPosition(notificationEvent.getConversationType(), notificationEvent.getTargetId());
        if(originalIndex >= 0) {
            this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
        }

    }

    public void onEventMainThread(Event.MessagesClearEvent clearMessagesEvent) {
        int originalIndex = this.mAdapter.findPosition(clearMessagesEvent.getType(), clearMessagesEvent.getTargetId());
        if(originalIndex >= 0) {
            boolean gatherState = RongContext.getInstance().getConversationGatherState(clearMessagesEvent.getType().getName()).booleanValue();
            if(gatherState) {
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback() {
                    public void onSuccess(Object o) {
                        List<Conversation> conversationList = (List<Conversation>) o;
                        if(conversationList != null && conversationList.size() != 0) {
                            UIConversation uiConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
                            int pos = ConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
                            if(pos >= 0) {
                                ConversationListFragment.this.mAdapter.remove(pos);
                            }

                            int newIndex = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
                            ConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
                            ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                        }
                    }

                    public void onError(RongIMClient.ErrorCode e) {
                    }
                }, new Conversation.ConversationType[]{Conversation.ConversationType.GROUP});
            } else {
                RongIMClient.getInstance().getConversation(clearMessagesEvent.getType(), clearMessagesEvent.getTargetId(), new RongIMClient.ResultCallback() {
                    public void onSuccess(Object o) {
                        Conversation conversation = (Conversation) o;
                        if(conversation != null) {
                            UIConversation uiConversation = UIConversation.obtain(conversation, false);
                            int pos = ConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                            if(pos >= 0) {
                                ConversationListFragment.this.mAdapter.remove(pos);
                            }

                            int newPos = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
                            ConversationListFragment.this.mAdapter.add(uiConversation, newPos);
                            ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                        }
                    }

                    public void onError(RongIMClient.ErrorCode e) {
                    }
                });
            }
        }

    }

    public void onEventMainThread(Event.OnMessageSendErrorEvent sendErrorEvent) {
        int index = this.mAdapter.findPosition(sendErrorEvent.getMessage().getConversationType(), sendErrorEvent.getMessage().getTargetId());
        if(index >= 0) {
            UIConversation temp = (UIConversation)this.mAdapter.getItem(index);
            temp.setUIConversationTime(sendErrorEvent.getMessage().getSentTime());
            temp.setMessageContent(sendErrorEvent.getMessage().getContent());
            temp.setConversationContent(temp.buildConversationContent(temp));
            temp.setSentStatus(Message.SentStatus.FAILED);
            this.mAdapter.remove(index);
            int newPosition = ConversationListUtils.findPositionForNewConversation(temp, this.mAdapter);
            this.mAdapter.add(temp, newPosition);
            this.mAdapter.notifyDataSetChanged();
        }

    }

    public void onEventMainThread(Event.QuitDiscussionEvent event) {
        int index = this.mAdapter.findPosition(Conversation.ConversationType.DISCUSSION, event.getDiscussionId());
        if(index >= 0) {
            if(!RongContext.getInstance().getConversationGatherState(Conversation.ConversationType.DISCUSSION.getName()).booleanValue()) {
                this.mAdapter.remove(index);
            }

            this.mAdapter.notifyDataSetChanged();
        }

    }

    public void onEventMainThread(Event.QuitGroupEvent event) {
        final int index = this.mAdapter.findPosition(Conversation.ConversationType.GROUP, event.getGroupId());
        boolean gatherState = RongContext.getInstance().getConversationGatherState(Conversation.ConversationType.GROUP.getName()).booleanValue();
        if(index >= 0 && gatherState) {
            RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback() {
                public void onSuccess(Object o) {
                    List<Conversation> conversationList = (List<Conversation>) o;
                    if(conversationList != null && conversationList.size() != 0) {
                        UIConversation uiConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
                        int pos = ConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
                        if(pos >= 0) {
                            ConversationListFragment.this.mAdapter.remove(pos);
                        }

                        int newIndex = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
                        ConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
                        ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                    } else {
                        if(index >= 0) {
                            ConversationListFragment.this.mAdapter.remove(index);
                        }

                        ConversationListFragment.this.mAdapter.notifyDataSetChanged();
                    }
                }

                public void onError(RongIMClient.ErrorCode e) {
                }
            }, new Conversation.ConversationType[]{Conversation.ConversationType.GROUP});
        } else if(index >= 0) {
            this.mAdapter.remove(index);
            this.mAdapter.notifyDataSetChanged();
        }

    }

    public void onEventMainThread(Event.MessageListenedEvent event) {
        int originalIndex = this.mAdapter.findPosition(event.getConversationType(), event.getTargetId());
        if(originalIndex >= 0) {
            UIConversation temp = (UIConversation)this.mAdapter.getItem(originalIndex);
            if(temp.getLatestMessageId() == event.getLatestMessageId()) {
                temp.setConversationContent(temp.buildConversationContent(temp));
            }

            this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
        }

    }

    /**
     * 点击头像事件
     * @param v
     * @param data
     */
    public void onPortraitItemClick(View v, UIConversation data) {
        String targetId = data.getConversationTargetId();
        mCompositeSubscription.add(
                ApiRequest.getStaffDetail(targetId.substring(targetId.lastIndexOf("_") + 1, targetId.length()))
        .subscribe(staffListBean -> {
            Intent i = new Intent(getActivity(), AdviserDetailActivity.class);
            i.putExtra(AdviserDetailActivity.ADVISER, staffListBean);
            startActivity(i);
        },throwable -> throwable.printStackTrace()));
    }

    public boolean onPortraitItemLongClick(View v, UIConversation data) {
        Conversation.ConversationType type = data.getConversationType();
        if(RongContext.getInstance().getConversationListBehaviorListener() != null) {
            boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitLongClick(this.getActivity(), type, data.getConversationTargetId());
            if(isDealt) {
                return true;
            }
        }

        if(!RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
            this.buildMultiDialog(data);
            return true;
        } else {
            this.buildSingleDialog(data);
            return true;
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    private void buildMultiDialog(final UIConversation uiConversation) {
        String[] items = new String[2];
        if(uiConversation.isTop()) {
            items[0] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_cancel_top);
        } else {
            items[0] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_set_top);
        }

        items[1] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_remove);
        ArraysDialogFragment.newInstance(uiConversation.getUIConversationTitle(), items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener() {
            public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    RongIM.getInstance().setConversationToTop(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), !uiConversation.isTop(), new RongIMClient.ResultCallback() {
                        public void onSuccess(Object o) {
                            Boolean aBoolean = (Boolean) o;
                            if(uiConversation.isTop()) {
                                Toast.makeText(RongContext.getInstance(), ConversationListFragment.this.getString(io.rong.imkit.R.string.rc_conversation_list_popup_cancel_top),Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RongContext.getInstance(), ConversationListFragment.this.getString(io.rong.imkit.R.string.rc_conversation_list_dialog_set_top), Toast.LENGTH_SHORT).show();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                } else if(which == 1) {
                    RongIM.getInstance().removeConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
                }

            }
        }).show(this.getFragmentManager());
    }

    private void buildSingleDialog(final UIConversation uiConversation) {
        String[] items = new String[]{RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_remove)};
        ArraysDialogFragment.newInstance(uiConversation.getUIConversationTitle(), items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener() {
            public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback() {
                    public void onSuccess(Object o) {
                        List<Conversation> conversations = (List<Conversation>) o;
                        if(conversations != null && conversations.size() != 0) {
                            Iterator i$ = conversations.iterator();

                            while(i$.hasNext()) {
                                Conversation conversation = (Conversation)i$.next();
                                RongIM.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId());
                            }

                        }
                    }

                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }
                }, new Conversation.ConversationType[]{uiConversation.getConversationType()});
            }
        }).show(this.getFragmentManager());
    }

    private void makeUiConversationList(List<Conversation> conversationList) {
        UIConversation uiCon;
        for(Iterator i$ = conversationList.iterator(); i$.hasNext(); ) {
            Conversation conversation = (Conversation)i$.next();
            //去除只有系统信息的聊天记录
            List<Message> messages = RongIM.getInstance().getLatestMessages(conversation.getConversationType(),conversation.getTargetId(),100);
            if (messages.size() == 1 && messages.get(0).getObjectName().equals("RC:SysMsg")) {
                RongIM.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }
                });
            } else {
                Conversation.ConversationType conversationType = conversation.getConversationType();
                boolean gatherState = RongContext.getInstance().getConversationGatherState(conversationType.getName()).booleanValue();
                int originalIndex = this.mAdapter.findPosition(conversationType, conversation.getTargetId());
                uiCon = UIConversation.obtain(conversation, gatherState);

                if (conversation.getTargetId().indexOf("s_021") < 0){
                    continue;
                }
                String id = uiCon.getConversationTargetId();
                id = id.substring(id.lastIndexOf("_")+1,id.length());
                Staff staff = DbUtil.getStaffByUid(id);
                UserInfo userInfo;
                if (staff != null) {
                    userInfo = new UserInfo(staff.getUId(), staff.getName(), Uri.parse(staff.getImageUrl()));
                    MessageContent messageContent = uiCon.getMessageContent();
                    uiCon.setUIConversationTitle(userInfo.getName());   //设置会话标题会发信人姓名
                    if (messageContent != null) {
                        messageContent.setUserInfo(userInfo);    //设置发信人信息
                    }
                }

                if(originalIndex < 0) {
                    this.mAdapter.add(uiCon);
                }
                this.refreshUnreadCount(uiCon.getConversationType(), uiCon.getConversationTargetId());
            }



        }

    }

    /**
     * 获取缓存的staff数据
     * @param targetId
     * @return
     */
    private void getCachedUserInfo(String targetId){
        targetId = targetId.substring(targetId.lastIndexOf("_")+1,targetId.length());
        final UserInfo[] userInfo = {null};

        Staff staff = DbUtil.getStaffByUid(targetId);
        if (staff != null) {
            userInfo[0] = new UserInfo(staff.getUId(), staff.getName(), Uri.parse(staff.getImageUrl()));
            RongIM.setUserInfoProvider(s ->  userInfo[0], true);
        } else {
            String finalTargetId = targetId;
            mCompositeSubscription.add(ApiRequest.getStaffDetail(finalTargetId)
                    .subscribe((staffListBeanBaseResult)-> {
                        Staff staff1 = new Staff();
                        staff1.setName(staffListBeanBaseResult.CnName);
                        staff1.setImageUrl(staffListBeanBaseResult.StaffImg);
                        if (finalTargetId.indexOf("|") < 0) {
                            staff1.setDepartmentName(staffListBeanBaseResult.StoreName);
                            staff1.setMobile(staffListBeanBaseResult.Mobile);
                            staff1.setUId(staffListBeanBaseResult.StaffNo.toLowerCase());
                            String number = staffListBeanBaseResult.MobileBy400 != null ? staffListBeanBaseResult.MobileBy400 : staffListBeanBaseResult.Staff400Tel;
                            staff1.setStaff400Tel(number);
                            DbUtil.addStaff(staff1);
                        }
                        userInfo[0] = new UserInfo(finalTargetId, staff1.getName(), Uri.parse(staff1.getImageUrl()));
                        RongIM.setUserInfoProvider(s ->  userInfo[0], true);
                    },throwable -> throwable.printStackTrace()));
        }
    }

    public CompositeSubscription getCompositeSubscription() {
        return mCompositeSubscription;
    }

    private UIConversation makeUiConversation(Message message, int pos) {
        UIConversation uiConversation;
        if(pos >= 0) {
            uiConversation = (UIConversation)this.mAdapter.getItem(pos);
            if(uiConversation != null) {
                uiConversation.setMessageContent(message.getContent());
                if(message.getMessageDirection() == Message.MessageDirection.SEND) {
                    uiConversation.setUIConversationTime(message.getSentTime());
                    uiConversation.setConversationSenderId(RongIM.getInstance().getCurrentUserId());
                } else {
                    uiConversation.setUIConversationTime(message.getSentTime());
                    uiConversation.setConversationSenderId(message.getSenderUserId());
                }

                uiConversation.setConversationTargetId(message.getTargetId());
                uiConversation.setConversationContent(uiConversation.buildConversationContent(uiConversation));
                uiConversation.setSentStatus(message.getSentStatus());
                uiConversation.setLatestMessageId(message.getMessageId());
                String title = "";
                Uri iconUri = null;
                UserInfo userInfo = message.getContent().getUserInfo();
                Conversation.ConversationType conversationType = message.getConversationType();
                if(userInfo != null && message.getTargetId().equals(userInfo.getUserId()) && (conversationType.equals(Conversation.ConversationType.PRIVATE) || conversationType.equals(Conversation.ConversationType.SYSTEM)) && (uiConversation.getUIConversationTitle() != null && userInfo.getName() != null && !userInfo.getName().equals(uiConversation.getUIConversationTitle()) || uiConversation.getIconUrl() != null && userInfo.getPortraitUri() != null && !userInfo.getPortraitUri().equals(uiConversation.getIconUrl()))) {
                    iconUri = userInfo.getPortraitUri();
                    title = userInfo.getName();
                    RongIMClient.getInstance().updateConversationInfo(message.getConversationType(), message.getTargetId(), title, iconUri != null?iconUri.toString():"", (RongIMClient.ResultCallback)null);
                }
            }
        } else {
            uiConversation = UIConversation.obtain(message, RongContext.getInstance().getConversationGatherState(message.getConversationType().getName()).booleanValue());
        }

        return uiConversation;
    }

    private UIConversation makeUIConversationFromList(List<Conversation> conversations) {
        int unreadCount = 0;
        boolean topFlag = false;
        Conversation newest = (Conversation)conversations.get(0);

        Conversation conversation;
        for(Iterator uiConversation = conversations.iterator(); uiConversation.hasNext(); unreadCount += conversation.getUnreadMessageCount()) {
            conversation = (Conversation)uiConversation.next();
            if(newest.isTop()) {
                if(conversation.isTop() && conversation.getSentTime() > newest.getSentTime()) {
                    newest = conversation;
                }
            } else if(conversation.isTop() || conversation.getSentTime() > newest.getSentTime()) {
                newest = conversation;
            }

            if(conversation.isTop()) {
                topFlag = true;
            }
        }

        UIConversation uiConversation1 = UIConversation.obtain(newest, RongContext.getInstance().getConversationGatherState(newest.getConversationType().getName()).booleanValue());
        uiConversation1.setUnReadMessageCount(unreadCount);
        uiConversation1.setTop(topFlag);
        return uiConversation1;
    }

    private void refreshUnreadCount(final Conversation.ConversationType type, final String targetId) {
        if(RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
            RongIM.getInstance().getUnreadCount(new RongIMClient.ResultCallback() {
                public void onSuccess(Object o) {
                    Integer count = (Integer) o;
                    int curPos = ConversationListFragment.this.mAdapter.findPosition(type, targetId);
                    if(curPos >= 0 && ConversationListFragment.this.mAdapter != null) {
                        (ConversationListFragment.this.mAdapter.getItem(curPos)).setUnReadMessageCount(count.intValue());
                        ConversationListFragment.this.mAdapter.getView(curPos, ConversationListFragment.this.mList.getChildAt(curPos - ConversationListFragment.this.mList.getFirstVisiblePosition()), ConversationListFragment.this.mList);
                    }

                }

                public void onError(RongIMClient.ErrorCode e) {
                    System.err.print("Throw exception when get unread message count from ipc remote side!");
                }
            }, new Conversation.ConversationType[]{type});
        } else {
            RongIM.getInstance().getUnreadCount(type, targetId, new RongIMClient.ResultCallback() {
                public void onSuccess(Object o) {
                    Integer integer = (Integer) o;
                    int curPos = ConversationListFragment.this.mAdapter.findPosition(type, targetId);
                    if(curPos >= 0 && mAdapter != null && curPos < mAdapter.getCount() && ConversationListFragment.this.mAdapter != null) {
                        (ConversationListFragment.this.mAdapter.getItem(curPos)).setUnReadMessageCount(integer.intValue());
                        ConversationListFragment.this.mAdapter.getView(curPos, ConversationListFragment.this.mList.getChildAt(curPos - ConversationListFragment.this.mList.getFirstVisiblePosition()), ConversationListFragment.this.mList);
                    }

                }

                public void onError(RongIMClient.ErrorCode e) {
                }
            });
        }

    }
}
