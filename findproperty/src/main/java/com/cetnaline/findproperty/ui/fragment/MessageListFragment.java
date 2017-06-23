package com.cetnaline.findproperty.ui.fragment;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.cetnaline.findproperty.entity.event.SystemMessageEvent;
import com.cetnaline.findproperty.ui.activity.ConversationActivity;
import com.cetnaline.findproperty.ui.listadapter.MessageListAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.common.SystemUtils;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.model.EmojiMessageAdapter;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.InputView;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.ReadReceiptMessage;
import io.rong.message.VoiceMessage;


/**
 * Created by diaoqf on 2016/8/2.
 */
public class MessageListFragment extends UriFragment implements AbsListView.OnScrollListener {
    private static final String TAG = "MessageListFragment";
    MessageListAdapter mAdapter;
    GestureDetector mGestureDetector;
    ListView mList;
    Conversation mConversation;
    int mUnreadCount;
    int mNewMessageCount;
    int mLastVisiblePosition;
    Button mUnreadBtn;
    ImageButton mNewMessageBtn;
    TextView mNewMessageTextView;
    boolean isShowUnreadMessageState;
    boolean isShowNewMessageState;
    int mMessageleft = -1;
    boolean needEvaluateForRobot = false;
    boolean robotMode = true;
    static final int REQ_LIST = 1;
    static final int RENDER_LIST = 2;
    static final int REFRESH_LIST_WHILE_RECEIVE_MESSAGE = 3;
    static final int REFRESH_ITEM = 4;
    static final int REQ_HISTORY = 5;
    static final int RENDER_HISTORY = 6;
    static final int REFRESH_ITEM_READ_RECEIPT = 7;
    static final int REQ_REMOTE_HISTORY = 8;
    static final int NOTIFY_LIST = 9;
    static final int RESET_LIST_STACK = 10;
    static final int DELETE_MESSAGE = 11;
    static final int REQ_UNREAD = 12;
    private static final int LISTVIEW_SHOW_COUNT = 5;
    View mHeaderView;
    private boolean isOnClickBtn;
    private boolean isShowWithoutConnected = false;
    AbsListView.OnScrollListener onScrollListener;
    boolean mHasMoreLocalMessages = true;
    boolean mHasMoreRemoteMessages = true;
    long mLastRemoteMessageTime = 0L;
    boolean isLoading = false;

    /**
     * 标识当前人有没有聊天记录
     */
    private boolean hasConversation;

    public MessageListFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasConversation = false;
        RongContext.getInstance().getEventBus().register(this);
        this.isShowUnreadMessageState = RongContext.getInstance().getUnreadMessageState();
        this.isShowNewMessageState = RongContext.getInstance().getNewMessageState();
        if(EmojiMessageAdapter.getInstance() == null) {
            EmojiMessageAdapter.init(RongContext.getInstance());
        }

        mAdapter = new MessageListAdapter(this.getActivity());
        this.mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(distanceY > 0.0F && MessageListFragment.this.mNewMessageCount >= 0 && MessageListFragment.this.mList.getLastVisiblePosition() >= MessageListFragment.this.mList.getCount() - MessageListFragment.this.mNewMessageCount) {
                    MessageListFragment.this.mNewMessageTextView.setText(MessageListFragment.this.mList.getCount() - MessageListFragment.this.mList.getLastVisiblePosition() + "");
                    MessageListFragment.this.mNewMessageCount = MessageListFragment.this.mList.getCount() - MessageListFragment.this.mList.getLastVisiblePosition() - 1;
                    if(MessageListFragment.this.mNewMessageCount > 99) {
                        MessageListFragment.this.mNewMessageTextView.setText("99+");
                    } else {
                        MessageListFragment.this.mNewMessageTextView.setText(MessageListFragment.this.mNewMessageCount + "");
                    }
                }

                if(MessageListFragment.this.mNewMessageCount == 0) {
                    MessageListFragment.this.mNewMessageBtn.setVisibility(View.GONE);
                    MessageListFragment.this.mNewMessageTextView.setVisibility(View.GONE);
                }

                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    protected void initFragment(Uri uri) {
        RLog.d("MessageListFragment", "initFragment " + uri);
        String typeStr = uri.getLastPathSegment().toUpperCase();
        Conversation.ConversationType type = Conversation.ConversationType.valueOf(typeStr);
        String targetId = uri.getQueryParameter("targetId");
        String title = uri.getQueryParameter("title");

        if(!TextUtils.isEmpty(targetId) && type != null) {
            this.mConversation = Conversation.obtain(type, targetId, title);
            if(mAdapter != null) {
                this.getHandler().post(new Runnable() {
                    public void run() {
                        MessageListFragment.this.mAdapter.clear();
                        MessageListFragment.this.mAdapter.notifyDataSetChanged();
                    }
                });
            }

            this.mNewMessageBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MessageListFragment.this.getHandler().postDelayed(MessageListFragment.this.new ScrollRunnable(), 500L);
                    MessageListFragment.this.mList.smoothScrollToPosition(MessageListFragment.this.mList.getCount() + 1);
                    MessageListFragment.this.mNewMessageCount = 0;
                    MessageListFragment.this.mNewMessageBtn.setVisibility(View.GONE);
                    MessageListFragment.this.mNewMessageTextView.setVisibility(View.GONE);
                }
            });
            if(RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                RLog.e("MessageListFragment", "initFragment Not connected yet.");
                this.isShowWithoutConnected = true;
            } else {
                this.getConversation();
                if(this.mConversation.getConversationType() != Conversation.ConversationType.CHATROOM) {
                    RongIM.getInstance().clearMessagesUnreadStatus(this.mConversation.getConversationType(), this.mConversation.getTargetId(), null);
                }

            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(io.rong.imkit.R.layout.rc_fr_messagelist, container, false);
        this.mUnreadBtn = (Button)this.findViewById(view, io.rong.imkit.R.id.rc_unread_message_count);
        this.mNewMessageBtn = (ImageButton)this.findViewById(view, io.rong.imkit.R.id.rc_new_message_count);
        this.mNewMessageTextView = (TextView)this.findViewById(view, io.rong.imkit.R.id.rc_new_message_number);
        this.mList = (ListView)this.findViewById(view, io.rong.imkit.R.id.rc_list);
        this.mHeaderView = inflater.inflate(io.rong.imkit.R.layout.rc_item_progress, (ViewGroup)null);
        this.mList.addHeaderView(this.mHeaderView);
        this.mList.setOnScrollListener(this);
        this.mList.setSelectionAfterHeaderView();
        mAdapter.setOnItemHandlerListener(new MessageListAdapter.OnItemHandlerListener() {
            public void onWarningViewClick(int position, final Message data, View v) {
                RongIM.getInstance().deleteMessages(new int[]{data.getMessageId()}, new RongIMClient.ResultCallback() {
                    public void onSuccess(Object o) {
                        Boolean aBoolean = (Boolean) o;
                        if(aBoolean.booleanValue()) {
                            data.setMessageId(0);
                            if(data.getContent() instanceof ImageMessage) {
                                RongIM.getInstance().sendImageMessage(data, "", "", new RongIMClient.SendImageMessageCallback() {
                                    public void onAttached(Message message) {
                                    }

                                    public void onError(Message message, RongIMClient.ErrorCode code) {
                                    }

                                    public void onSuccess(Message message) {
                                    }

                                    public void onProgress(Message message, int progress) {
                                    }
                                });
                            } else if(data.getContent() instanceof LocationMessage) {
                                RongIM.getInstance().sendLocationMessage(data, (String)null, (String)null, (IRongCallback.ISendMessageCallback)null);
                            } else {
                                RongIM.getInstance().sendMessage(data, (String)null, (String)null, new IRongCallback.ISendMessageCallback() {
                                    public void onAttached(Message message) {
                                    }

                                    public void onSuccess(Message message) {
                                    }

                                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                                    }
                                });
                            }
                        }

                    }

                    public void onError(RongIMClient.ErrorCode e) {
                    }
                });
            }
        });
        return view;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch(scrollState) {
            case 0:
                if(view.getFirstVisiblePosition() == 0 && mAdapter.getCount() > 0 && this.mHasMoreLocalMessages && !this.isLoading) {
                    this.isLoading = true;
                    this.getHandler().sendEmptyMessage(5);
                } else if(view.getFirstVisiblePosition() == 0 && !this.mHasMoreLocalMessages && this.mHasMoreRemoteMessages && !this.isLoading && this.mConversation.getConversationType() != Conversation.ConversationType.CHATROOM && this.mConversation.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE && this.mConversation.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE) {
                    try {
                        boolean e = view.getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_get_remote_history_message);
                        if(e) {
                            this.isLoading = true;
                            this.getHandler().sendEmptyMessage(8);
                        }
                    } catch (Resources.NotFoundException var4) {
                        this.mHasMoreRemoteMessages = false;
                        RLog.e("MessageListFragment", "get_remote_history_message disabled.");
                    }
                }
            default:
                if(this.onScrollListener != null) {
                    this.onScrollListener.onScrollStateChanged(view, scrollState);
                }

        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(this.onScrollListener != null) {
            this.onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if(firstVisibleItem + visibleItemCount >= totalItemCount - this.mNewMessageCount) {
            this.mNewMessageCount = 0;
            this.mNewMessageBtn.setVisibility(View.GONE);
            this.mNewMessageTextView.setVisibility(View.GONE);
        }

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(this.getActionBarHandler() != null) {
            this.getActionBarHandler().onTitleChanged(this.mConversation.getConversationTitle());
        }

        this.mList.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == 2 || event.getAction() == 0) {
                    EventBus.getDefault().post(Event.InputViewEvent.obtain(false));
                    if(event.getAction() == 2 && MessageListFragment.this.mList.getCount() == 0 && MessageListFragment.this.mHasMoreRemoteMessages && MessageListFragment.this.mConversation.getConversationType() != Conversation.ConversationType.CHATROOM && MessageListFragment.this.mConversation.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE && MessageListFragment.this.mConversation.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE) {
                        try {
                            boolean e = MessageListFragment.this.getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_get_remote_history_message);
                            if(e) {
                                MessageListFragment.this.isLoading = true;
                                MessageListFragment.this.getHandler().sendEmptyMessage(8);
                            }
                        } catch (Resources.NotFoundException var4) {
                            MessageListFragment.this.mHasMoreRemoteMessages = false;
                            RLog.e("MessageListFragment", "get_remote_history_message disabled.");
                        }
                    }
                }

                MessageListFragment.this.mGestureDetector.onTouchEvent(event);
                return false;
            }
        });
        this.mList.setAdapter(mAdapter);
        this.mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RongContext.getInstance().getPrimaryInputProvider().onInactive(view.getContext());
                RongContext.getInstance().getSecondaryInputProvider().onInactive(view.getContext());
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean onBackPressed() {
        return false;
    }

    private List<UIMessage> filterMessage(List<UIMessage> srcList) {
        Object destList = null;
        if(this.mAdapter.getCount() > 0) {
            destList = new ArrayList();

            for(int i = 0; i < this.mAdapter.getCount(); ++i) {
                Iterator i$ = srcList.iterator();

                while(i$.hasNext()) {
                    UIMessage msg = (UIMessage)i$.next();
                    if(!((List)destList).contains(msg) && msg.getMessageId() != ((UIMessage)this.mAdapter.getItem(i)).getMessageId()) {
                        ((List)destList).add(msg);
                    }
                }
            }
        } else {
            destList = srcList;
        }

        return (List)destList;
    }

    public boolean handleMessage(android.os.Message msg) {
        RLog.d("MessageListFragment", "MessageListFragment msg : " + msg.what);
        List position;
        int pos;
        UIMessage message;
        switch(msg.what) {
            case 1:
                this.mAdapter.clear();
                this.mAdapter.notifyDataSetChanged();
                EmojiMessageAdapter.getInstance().getLatestMessages(this.mConversation.getConversationType(), this.mConversation.getTargetId(), 30, new RongIMClient.ResultCallback() {
                    public void onSuccess(Object o) {
                        List<UIMessage> messages = (List<UIMessage>) o;
                        RLog.d("MessageListFragment", "getLatestMessages, onSuccess " + messages.size());
                        MessageListFragment.this.mHasMoreLocalMessages = messages.size() == 30;
                        MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
                        MessageListFragment.this.isLoading = false;
                        MessageListFragment.this.getHandler().obtainMessage(2, messages).sendToTarget();
                    }

                    public void onError(RongIMClient.ErrorCode e) {
                        RLog.e("MessageListFragment", "getLatestMessages, " + e.toString());
                        MessageListFragment.this.mHasMoreLocalMessages = false;
                        MessageListFragment.this.isLoading = false;
                        MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
                    }
                });
                break;
            case 2:
                if(msg.obj != null && msg.obj instanceof List) {
                    position = (List)msg.obj;
                    this.mAdapter.clear();
                    this.mAdapter.addCollection(this.filterMessage(position));
                    if(position.size() <= 5) {
                        this.mList.setStackFromBottom(false);
                        this.mList.setTranscriptMode(2);
                    } else {
                        this.mList.setStackFromBottom(true);
                        this.mList.setTranscriptMode(2);
                    }

                    this.mAdapter.notifyDataSetChanged();
                    this.getHandler().sendEmptyMessage(10);
                }

                if(this.mUnreadCount >= 10) {
                    TranslateAnimation position2 = new TranslateAnimation(300.0F, 0.0F, 0.0F, 0.0F);
                    AlphaAnimation pos2 = new AlphaAnimation(0.0F, 1.0F);
                    position2.setDuration(1000L);
                    pos2.setDuration(2000L);
                    AnimationSet message1 = new AnimationSet(true);
                    message1.addAnimation(position2);
                    message1.addAnimation(pos2);
                    this.mUnreadBtn.setVisibility(View.VISIBLE);
                    this.mUnreadBtn.startAnimation(message1);
                    message1.setAnimationListener(new Animation.AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            MessageListFragment.this.getHandler().postDelayed(new Runnable() {
                                public void run() {
                                    if(!MessageListFragment.this.isOnClickBtn) {
                                        TranslateAnimation animation = new TranslateAnimation(0.0F, 700.0F, 0.0F, 0.0F);
                                        animation.setDuration(700L);
                                        animation.setFillAfter(true);
                                        MessageListFragment.this.mUnreadBtn.startAnimation(animation);
                                    }

                                }
                            }, 4000L);
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            case 3:
            default:
                break;
            case 4:
                int position1 = ((Integer)msg.obj).intValue();
                if(position1 >= this.mList.getFirstVisiblePosition() && position1 <= this.mList.getLastVisiblePosition()) {
                    RLog.d("MessageListFragment", "REFRESH_ITEM Index:" + position1);
                    this.mAdapter.getView(position1, this.mList.getChildAt(position1 - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
                }
                break;
            case 5:
                message = (UIMessage)this.mAdapter.getItem(0);
                this.mList.addHeaderView(this.mHeaderView);
                EmojiMessageAdapter.getInstance().getHistoryMessages(this.mConversation.getConversationType(), this.mConversation.getTargetId(), message.getMessageId(), 30, new RongIMClient.ResultCallback() {
                    public void onSuccess(Object o) {
                        List<UIMessage> messages = (List<UIMessage>) o;
                        RLog.d("MessageListFragment", "getHistoryMessages, onSuccess " + messages.size());
                        MessageListFragment.this.mHasMoreLocalMessages = messages.size() == 30;
                        MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
                        MessageListFragment.this.isLoading = false;
                        MessageListFragment.this.getHandler().obtainMessage(6, messages).sendToTarget();
                    }

                    public void onError(RongIMClient.ErrorCode e) {
                        MessageListFragment.this.mHasMoreLocalMessages = false;
                        MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
                        MessageListFragment.this.isLoading = false;
                        RLog.e("MessageListFragment", "getHistoryMessages, " + e.toString());
                    }
                });
                break;
            case 6:
                if(msg.obj instanceof List) {
                    position = (List)msg.obj;
                    Iterator pos1 = position.iterator();

                    while(pos1.hasNext()) {
                        message = (UIMessage)pos1.next();
                        this.mAdapter.add(message, 0);
                    }

                    this.mList.setTranscriptMode(0);
                    this.mList.setStackFromBottom(false);
                    pos = this.mList.getFirstVisiblePosition();
                    this.mAdapter.notifyDataSetChanged();
                    if(pos == 0) {
                        this.mList.setSelection(position.size());
                    }
                }
                break;
            case 7:
                pos = ((Integer)msg.obj).intValue();
                if(pos >= this.mList.getFirstVisiblePosition() && pos <= this.mList.getLastVisiblePosition()) {
                    RLog.d("MessageListFragment", "REFRESH_ITEM Index:" + pos);
                    this.mAdapter.getView(pos, this.mList.getChildAt(pos - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
                }
                break;
            case 8:
                this.mList.addHeaderView(this.mHeaderView);
                EmojiMessageAdapter.getInstance().getRemoteHistoryMessages(this.mConversation.getConversationType(), this.mConversation.getTargetId(), this.mLastRemoteMessageTime, 10, new RongIMClient.ResultCallback() {
                    public void onSuccess(Object o) {
                        List<UIMessage> uiMessages = (List<UIMessage>) o;
                        MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
                        if(uiMessages != null && uiMessages.size() != 0) {
                            RLog.d("MessageListFragment", "getRemoteHistoryMessages, onSuccess " + uiMessages.size());
                            MessageListFragment.this.mLastRemoteMessageTime = ((UIMessage)uiMessages.get(uiMessages.size() - 1)).getSentTime();
                            MessageListFragment.this.mHasMoreRemoteMessages = uiMessages.size() >= 10;
                            ArrayList filterMsg = new ArrayList();
                            Iterator i$ = uiMessages.iterator();

                            while(i$.hasNext()) {
                                UIMessage m = (UIMessage)i$.next();
                                String uid = m.getUId();
                                int count = MessageListFragment.this.mAdapter.getCount();
                                boolean result = true;

                                for(int i = 0; i < count; ++i) {
                                    UIMessage item = MessageListFragment.this.mAdapter.getItem(i);
                                    String targetUid = item.getUId();
                                    if(uid != null && targetUid != null && uid.equals(targetUid)) {
                                        result = false;
                                        break;
                                    }
                                }

                                if(result) {
                                    filterMsg.add(m);
                                }
                            }

                            RLog.d("MessageListFragment", "getRemoteHistoryMessages, src: " + uiMessages.size() + " dest: " + filterMsg.size());
                            MessageListFragment.this.getHandler().obtainMessage(6, filterMsg).sendToTarget();
                        } else {
                            MessageListFragment.this.mHasMoreRemoteMessages = false;
                        }

                        MessageListFragment.this.isLoading = false;
                    }

                    public void onError(RongIMClient.ErrorCode e) {
                        MessageListFragment.this.mHasMoreRemoteMessages = false;
                        MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
                        MessageListFragment.this.isLoading = false;
                        RLog.e("MessageListFragment", "getRemoteHistoryMessages, " + e.toString());
                    }
                });
                break;
            case 9:
                if(this.mAdapter != null) {
                    this.mAdapter.notifyDataSetChanged();
                }
                break;
            case 10:
                this.resetListViewStack();
                this.mAdapter.notifyDataSetChanged();
                break;
            case 11:
                this.mAdapter.notifyDataSetChanged();
                this.getHandler().post(new Runnable() {
                    public void run() {
                        if(MessageListFragment.this.mList.getCount() > 0) {
                            View firstView = MessageListFragment.this.mList.getChildAt(MessageListFragment.this.mList.getFirstVisiblePosition());
                            View lastView = MessageListFragment.this.mList.getChildAt(MessageListFragment.this.mList.getLastVisiblePosition());
                            if(firstView != null && lastView != null) {
                                int listViewPadding = MessageListFragment.this.mList.getListPaddingBottom() + MessageListFragment.this.mList.getListPaddingTop();
                                int childViewsHeight = lastView.getBottom() - (firstView.getTop() == -1?0:firstView.getTop());
                                int listViewHeight = MessageListFragment.this.mList.getBottom() - listViewPadding;
                                if(childViewsHeight < listViewHeight) {
                                    MessageListFragment.this.mList.setTranscriptMode(2);
                                    MessageListFragment.this.mList.setStackFromBottom(false);
                                } else {
                                    MessageListFragment.this.mList.setTranscriptMode(1);
                                }

                                MessageListFragment.this.mAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });
                break;
            case 12:
                message = this.mAdapter.getItem(0);
                EmojiMessageAdapter.getInstance().getHistoryMessages(this.mConversation.getConversationType(), this.mConversation.getTargetId(), message.getMessageId(), this.mUnreadCount - 29, new RongIMClient.ResultCallback() {
                    public void onSuccess(Object o) {
                        List<UIMessage> messages = (List<UIMessage>) o;
                        RLog.d("MessageListFragment", "getHistoryMessages unread, onSuccess " + messages.size());
                        MessageListFragment.this.mHasMoreLocalMessages = messages.size() == MessageListFragment.this.mUnreadCount - 29;
                        MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
                        Iterator i$ = messages.iterator();

                        while(i$.hasNext()) {
                            UIMessage item = (UIMessage)i$.next();
                            MessageListFragment.this.mAdapter.add(item, 0);
                        }

                        MessageListFragment.this.mAdapter.notifyDataSetChanged();
                        MessageListFragment.this.mList.setStackFromBottom(false);
                        MessageListFragment.this.mList.smoothScrollToPosition(0);
                        MessageListFragment.this.isLoading = false;
                    }

                    public void onError(RongIMClient.ErrorCode e) {
                        RLog.e("MessageListFragment", "getHistoryMessages, " + e.toString());
                        MessageListFragment.this.mHasMoreLocalMessages = false;
                        MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
                        MessageListFragment.this.isLoading = false;
                    }
                });
        }

        return false;
    }

    private void resetListViewStack() {
        int count = this.mList.getChildCount();
        View firstView = this.mList.getChildAt(0);
        View lastView = this.mList.getChildAt(count - 1);
        if(firstView != null && lastView != null) {
            int listViewPadding = this.mList.getListPaddingBottom() + this.mList.getListPaddingTop();
            int childViewsHeight = lastView.getBottom() - (firstView.getTop() == -1?0:firstView.getTop());
            int listViewHeight = this.mList.getBottom() - listViewPadding;
            if(childViewsHeight < listViewHeight) {
                this.mList.setTranscriptMode(2);
                this.mList.setStackFromBottom(false);
            } else {
                this.mList.setTranscriptMode(2);
            }
        }

    }

    public void onEventMainThread(Event.ReadReceiptEvent event) {
        if(this.mConversation != null && this.mConversation.getTargetId().equals(event.getMessage().getTargetId()) && this.mConversation.getConversationType() == event.getMessage().getConversationType()) {
            if(event.getMessage().getConversationType() != Conversation.ConversationType.PRIVATE) {
                return;
            }

            ReadReceiptMessage content = (ReadReceiptMessage)event.getMessage().getContent();
            long ntfTime = content.getLastMessageSendTime();

            for(int i = this.mAdapter.getCount() - 1; i >= 0 && ((UIMessage)this.mAdapter.getItem(i)).getSentStatus() != Message.SentStatus.READ; --i) {
                if(((UIMessage)this.mAdapter.getItem(i)).getSentStatus() == Message.SentStatus.SENT && ((UIMessage)this.mAdapter.getItem(i)).getMessageDirection().equals(Message.MessageDirection.SEND) && ntfTime >= ((UIMessage)this.mAdapter.getItem(i)).getSentTime()) {
                    ((UIMessage)this.mAdapter.getItem(i)).setSentStatus(Message.SentStatus.READ);
                    this.getHandler().obtainMessage(7, Integer.valueOf(i)).sendToTarget();
                }
            }
        }

    }

    private void refreshListWhileReceiveMessage(UIMessage model) {
        model.setIsHistoryMessage(false);
        this.mAdapter.setEvaluateForRobot(this.needEvaluateForRobot);
        this.mAdapter.setRobotMode(this.robotMode);
        this.mAdapter.add(model);
        if(this.isShowNewMessageState && this.mList.getLastVisiblePosition() < this.mList.getCount() - 1 && Message.MessageDirection.SEND != model.getMessageDirection() && SystemUtils.isAppRunningOnTop(RongContext.getInstance(), RongContext.getInstance().getPackageName()) && model.getConversationType() != Conversation.ConversationType.CHATROOM && model.getConversationType() != Conversation.ConversationType.CUSTOMER_SERVICE && model.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE && model.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE) {
            ++this.mNewMessageCount;
            if(this.mNewMessageCount > 0) {
                this.mNewMessageBtn.setVisibility(View.VISIBLE);
                this.mNewMessageTextView.setVisibility(View.VISIBLE);
            }

            if(this.mNewMessageCount > 99) {
                this.mNewMessageTextView.setText("99+");
            } else {
                this.mNewMessageTextView.setText(this.mNewMessageCount + "");
            }
        }

        int last = this.mList.getLastVisiblePosition();
        int count = this.mList.getCount();
        if(last == count - 1) {
            this.mList.setTranscriptMode(2);
        } else if(last < this.mList.getCount() - 1) {
            this.mList.setTranscriptMode(1);
        }

        this.mAdapter.notifyDataSetChanged();
        if(last == count - 1) {
            this.mNewMessageBtn.setVisibility(View.GONE);
            this.mNewMessageTextView.setVisibility(View.GONE);
        }

    }

    /**
     * 发送/接收信息监听
     * @param msg
     */
    public void onEventMainThread(Message msg) {
        //发送默认信息给客户
//        if (!hasConversation && msg.getObjectName().equals("RC:TxtMsg")) {
//            hasConversation = true;
//            ((ConversationActivity)getActivity()).sendDefaultMessage();
//        }

        //这里接收到系统消息后通知发送默认消息
//        if (msg.getObjectName().equals("RC:SysMsg")) {
//            EventBus.getDefault().post(new SystemMessageEvent());
//        }

        UIMessage message = UIMessage.obtain(msg);
        boolean readRec = RongIMClient.getInstance().getReadReceipt();
        RLog.d("MessageListFragment", "onEventMainThread message : " + message.getMessageId() + " " + message.getSentStatus());
        if(this.mConversation != null && this.mConversation.getTargetId().equals(message.getTargetId()) && this.mConversation.getConversationType() == message.getConversationType()) {
            int position = this.mAdapter.findPosition((long)message.getMessageId());
            if(message.getMessageId() > 0) {
                Message.ReceivedStatus status = message.getReceivedStatus();
                status.setRead();
                message.setReceivedStatus(status);
                RongIMClient.getInstance().setMessageReceivedStatus(msg.getMessageId(), status, null);
            }

            if(position == -1) {
                if(this.mMessageleft <= 0 && readRec && message.getMessageDirection().equals(Message.MessageDirection.RECEIVE) && message.getConversationType() == Conversation.ConversationType.PRIVATE && SystemUtils.isAppRunningOnTop(RongContext.getInstance(), RongContext.getInstance().getPackageName())) {
                    RongIMClient.getInstance().sendReadReceiptMessage(message.getConversationType(), message.getTargetId(), message.getSentTime());
                }

                this.mConversation.setSentTime(message.getSentTime());
                this.mConversation.setSenderUserId(message.getSenderUserId());
                this.refreshListWhileReceiveMessage(message);
            } else {
                (this.mAdapter.getItem(position)).setSentStatus(message.getSentStatus());
                (this.mAdapter.getItem(position)).setExtra(message.getExtra());
                (this.mAdapter.getItem(position)).setSentTime(message.getSentTime());
                (this.mAdapter.getItem(position)).setUId(message.getUId());
                (this.mAdapter.getItem(position)).setContent(message.getContent());
                this.getHandler().obtainMessage(4, Integer.valueOf(position)).sendToTarget();
            }
        }

    }

    public void onEventMainThread(Event.GroupUserInfoEvent event) {
        GroupUserInfo userInfo = event.getUserInfo();
        if(userInfo != null && userInfo.getNickname() != null) {
            if(this.mList != null && this.isResumed()) {
                int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
                int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
                int index = first - 1;

                while(true) {
                    Message message;
                    do {
                        do {
                            ++index;
                            if(index > last || index < 0 || index >= this.mAdapter.getCount()) {
                                return;
                            }

                            message = (Message)this.mAdapter.getItem(index);
                        } while(message == null);
                    } while(!TextUtils.isEmpty(message.getSenderUserId()) && !userInfo.getUserId().equals(message.getSenderUserId()));

                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
                }
            }
        }
    }

    public void onEventMainThread(Event.OnMessageSendErrorEvent event) {
        Message msg = event.getMessage();
        this.onEventMainThread(msg);
    }

    public void onEventMainThread(Event.OnReceiveMessageEvent event) {
        this.mMessageleft = event.getLeft();
        this.onEventMainThread(event.getMessage());
    }

    public void onEventMainThread(MessageContent messageContent) {
        if(this.mList != null && this.isResumed()) {
            int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
            int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
            int index = first - 1;

            while(true) {
                ++index;
                if(index > last || index < 0 || index >= this.mAdapter.getCount()) {
                    break;
                }

                if((this.mAdapter.getItem(index)).getContent().equals(messageContent)) {
                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
                    break;
                }
            }
        }

    }

    public void onEventMainThread(Event.PlayAudioEvent event) {
        MessageContent messageContent = event.getContent();
        if(this.mList != null && this.isResumed()) {
            int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
            int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
            int index = first;
            boolean continuously = false;

            while(index <= last && index >= 0 && index < this.mAdapter.getCount()) {
                UIMessage uiMessage = this.mAdapter.getItem(index);
                if(uiMessage.getContent().equals(messageContent)) {
                    this.mAdapter.getView(index, this.mList.getChildAt(index - first), this.mList);
                    if(uiMessage.getMessageDirection().equals(Message.MessageDirection.RECEIVE) && !event.isListened()) {
                        continuously = true;
                    }
                }

                if(continuously) {
                    try {
                        continuously = RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_play_audio_continuous);
                    } catch (Resources.NotFoundException var9) {
                        RLog.e("MessageListFragment", "PlayAudioEvent rc_play_audio_continuous not configure in rc_config.xml");
                        var9.printStackTrace();
                    }
                }

                ++index;
                if(continuously && event.isFinished() && index <= last && index < this.mAdapter.getCount()) {
                    uiMessage = this.mAdapter.getItem(index);
                    if(uiMessage.getContent() instanceof VoiceMessage && uiMessage.getMessageDirection().equals(Message.MessageDirection.RECEIVE) && !uiMessage.getReceivedStatus().isListened()) {
                        View view = this.mList.getChildAt(index - first);
                        if(view != null) {
                            this.mAdapter.getView(index, view, this.mList);
                            this.mAdapter.playNextAudioIfNeed(uiMessage, index);
                            break;
                        }
                    }
                }
            }
        }

    }

    public void onEventMainThread(Event.OnReceiveMessageProgressEvent event) {
        if(this.mList != null && this.isResumed()) {
            int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
            int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
            int index = first - 1;

            while(true) {
                ++index;
                if(index > last || index < 0 || index >= this.mAdapter.getCount()) {
                    break;
                }

                UIMessage uiMessage = this.mAdapter.getItem(index);
                if(uiMessage.getMessageId() == event.getMessage().getMessageId()) {
                    uiMessage.setProgress(event.getProgress());
                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
                    break;
                }
            }
        }

    }

    public void onEventMainThread(InputView.Event event) {
        if(this.mAdapter != null) {
            if(event == InputView.Event.ACTION) {
                this.getHandler().sendEmptyMessage(10);
            }

        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        if(this.mList != null) {
            int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
            int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
            int index = first - 1;

            while(true) {
                UIMessage uiMessage;
                do {
                    do {
                        ++index;
                        if(index > last || index < 0 || index >= this.mAdapter.getCount()) {
                            return;
                        }

                        uiMessage = this.mAdapter.getItem(index);
                    } while(uiMessage == null);
                } while(!TextUtils.isEmpty(uiMessage.getSenderUserId()) && !userInfo.getUserId().equals(uiMessage.getSenderUserId()));

                uiMessage.setUserInfo(userInfo);
                this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
            }
        }
    }

    public void onEventMainThread(PublicServiceProfile publicServiceProfile) {
        if(this.mList != null && this.isResumed() && this.mAdapter != null) {
            int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
            int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
            int index = first - 1;

            while(true) {
                Message message;
                do {
                    do {
                        ++index;
                        if(index > last || index < 0 || index >= this.mAdapter.getCount()) {
                            return;
                        }

                        message = this.mAdapter.getItem(index);
                    } while(message == null);
                } while(!TextUtils.isEmpty(message.getTargetId()) && !publicServiceProfile.getTargetId().equals(message.getTargetId()));

                this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
            }
        }
    }

    private void getConversation() {
        RongIM.getInstance().getConversation(this.mConversation.getConversationType(), this.mConversation.getTargetId(), new RongIMClient.ResultCallback() {
            public void onSuccess(Object o) {
                Conversation conversation = (Conversation) o;
                if(conversation != null) {
                    hasConversation = true;
                    if(!TextUtils.isEmpty(MessageListFragment.this.mConversation.getConversationTitle())) {
                        conversation.setConversationTitle(MessageListFragment.this.mConversation.getConversationTitle());
                    }

                    MessageListFragment.this.mConversation = conversation;
                    if(MessageListFragment.this.isShowUnreadMessageState && conversation.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE && conversation.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE && conversation.getConversationType() != Conversation.ConversationType.CUSTOMER_SERVICE && conversation.getConversationType() != Conversation.ConversationType.CHATROOM) {
                        MessageListFragment.this.mUnreadCount = MessageListFragment.this.mConversation.getUnreadMessageCount();
                    }

                    if(MessageListFragment.this.mUnreadCount > 150) {
                        MessageListFragment.this.mUnreadBtn.setText("150+条新消息");
                    } else {
                        MessageListFragment.this.mUnreadBtn.setText(MessageListFragment.this.mUnreadCount + "条新消息");
                    }

                    boolean readRec = RongIMClient.getInstance().getReadReceipt();
                    if(readRec && conversation.getConversationType() == Conversation.ConversationType.PRIVATE && (!conversation.getSenderUserId().equals(RongIMClient.getInstance().getCurrentUserId()) || MessageListFragment.this.mUnreadCount > 0)) {
                        RongIMClient.getInstance().sendReadReceiptMessage(conversation.getConversationType(), conversation.getTargetId(), conversation.getSentTime());
                    }

                    MessageListFragment.this.mUnreadBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            MessageListFragment.this.isOnClickBtn = true;
                            MessageListFragment.this.mUnreadBtn.setClickable(false);
                            TranslateAnimation animation = new TranslateAnimation(0.0F, 500.0F, 0.0F, 0.0F);
                            animation.setDuration(500L);
                            MessageListFragment.this.mUnreadBtn.startAnimation(animation);
                            animation.setFillAfter(true);
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                public void onAnimationStart(Animation animation) {
                                }

                                public void onAnimationEnd(Animation animation) {
                                    MessageListFragment.this.mUnreadBtn.setVisibility(View.GONE);
                                    if(MessageListFragment.this.mUnreadCount <= 30) {
                                        MessageListFragment.this.mList.smoothScrollToPosition(31 - MessageListFragment.this.mUnreadCount);
                                    } else if(MessageListFragment.this.mUnreadCount >= 30) {
                                        MessageListFragment.this.mUnreadCount = 150;
                                        MessageListFragment.this.getHandler().sendEmptyMessage(12);
                                    }

                                }

                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                        }
                    });
                    EventBus.getDefault().post(new SystemMessageEvent());
                } else {
                    //发送系统消息
//                    ((ConversationActivity)getActivity()).sendSystemMessage();
                }

                MessageListFragment.this.getHandler().sendEmptyMessage(1);
            }

            public void onError(RongIMClient.ErrorCode e) {
                RLog.e("MessageListFragment", "fail, " + e.toString());
            }
        });
    }

    public void onEventMainThread(Event.ConnectEvent event) {
        RLog.d("MessageListFragment", "onEventMainThread Event.ConnectEvent: isListRetrieved = " + this.isShowWithoutConnected);
        if(this.isShowWithoutConnected) {
            this.getConversation();
            if(this.mConversation.getConversationType() != Conversation.ConversationType.CHATROOM) {
                RongIM.getInstance().clearMessagesUnreadStatus(this.mConversation.getConversationType(), this.mConversation.getTargetId(), (RongIMClient.ResultCallback)null);
            }

            this.isShowWithoutConnected = false;
        }
    }

    public void onPause() {
        super.onPause();
        RongContext.getInstance().getEventBus().post(InputView.Event.DESTROY);
    }

    public void onResume() {
        super.onResume();
        if(RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
            this.isShowWithoutConnected = true;
            RLog.e("MessageListFragment", "onResume Not connected yet.");
        }

        if(this.mList.getLastVisiblePosition() == this.mList.getCount() - 1) {
            this.mNewMessageCount = 0;
            this.mNewMessageTextView.setVisibility(View.GONE);
            this.mNewMessageBtn.setVisibility(View.GONE);
        }

        if(this.mConversation != null && this.mConversation.getSenderUserId() != null) {
            boolean readRec = RongIMClient.getInstance().getReadReceipt();
            if(readRec && this.mConversation.getConversationType() == Conversation.ConversationType.PRIVATE && !this.mConversation.getSenderUserId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                RongIMClient.getInstance().sendReadReceiptMessage(this.mConversation.getConversationType(), this.mConversation.getTargetId(), this.mConversation.getSentTime());
            }
        }
    }

    public void onEventMainThread(Event.MessageDeleteEvent deleteEvent) {
        if(deleteEvent.getMessageIds() != null) {
            boolean hasChanged = false;
            boolean position = false;
            Iterator i$ = deleteEvent.getMessageIds().iterator();

            while(i$.hasNext()) {
                long item = (long)((Integer)i$.next()).intValue();
                int position1 = this.mAdapter.findPosition(item);
                if(position1 >= 0) {
                    this.mAdapter.remove(position1);
                    hasChanged = true;
                }
            }

            if(hasChanged) {
                this.mAdapter.notifyDataSetChanged();
                this.getHandler().obtainMessage(11).sendToTarget();
            }
        }

    }

    public void onEventMainThread(Event.PublicServiceFollowableEvent event) {
        if(event != null && !event.isFollow()) {
            this.getActivity().finish();
        }

    }

    public void onEventMainThread(Event.MessagesClearEvent clearEvent) {
        if(clearEvent.getTargetId().equals(this.mConversation.getTargetId()) && clearEvent.getType().equals(this.mConversation.getConversationType())) {
            this.mAdapter.removeAll();
            this.getHandler().post(new Runnable() {
                public void run() {
                    MessageListFragment.this.mList.setTranscriptMode(1);
                    MessageListFragment.this.mList.setStackFromBottom(false);
                    MessageListFragment.this.mAdapter.notifyDataSetChanged();
                }
            });
            this.mAdapter.notifyDataSetChanged();
        }

    }

    public void onDestroy() {
        if(this.mConversation != null && this.mConversation.getConversationType() != Conversation.ConversationType.CHATROOM) {
            RongIM.getInstance().clearMessagesUnreadStatus(this.mConversation.getConversationType(), this.mConversation.getTargetId(), null);
        }

        RongContext.getInstance().getEventBus().unregister(this);
        super.onDestroy();
    }

    public void setAdapter(MessageListAdapter adapter) {
        if(this.mAdapter != null) {
            this.mAdapter.clear();
        }

        this.mAdapter = adapter;
        if(this.mList != null && this.getUri() != null) {
            this.mList.setAdapter(adapter);
            this.initFragment(this.getUri());
        }

    }

    public MessageListAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setNeedEvaluateForRobot(boolean needEvaluate) {
        this.needEvaluateForRobot = needEvaluate;
    }

    public void setRobotMode(boolean robotMode) {
        this.robotMode = robotMode;
    }

    public static class Builder {
        private Conversation.ConversationType conversationType;
        private String targetId;
        private Uri uri;

        public Builder() {
        }

        public Conversation.ConversationType getConversationType() {
            return this.conversationType;
        }

        public void setConversationType(Conversation.ConversationType conversationType) {
            this.conversationType = conversationType;
        }

        public String getTargetId() {
            return this.targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }

    public class ScrollRunnable implements Runnable {
        public ScrollRunnable() {
        }

        public void run() {
            if(MessageListFragment.this.mList.getLastVisiblePosition() < MessageListFragment.this.mList.getCount() - 1) {
                MessageListFragment.this.mList.setSelection(MessageListFragment.this.mList.getLastVisiblePosition() + 10);
                MessageListFragment.this.getHandler().postDelayed(MessageListFragment.this.new ScrollRunnable(), 100L);
            }

        }
    }
}