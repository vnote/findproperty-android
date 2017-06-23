package com.cetnaline.findproperty.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.ui.activity.CommentActivity;
import com.cetnaline.findproperty.ui.activity.ConsultFormActivity;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.SendImageManager;
import io.rong.imkit.fragment.*;
import io.rong.imkit.model.ConversationInfo;
import io.rong.imlib.CustomServiceConfig;
import io.rong.imlib.ICustomServiceListener;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.CustomServiceMode;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceMenu;
import io.rong.message.PublicServiceCommandMessage;
import io.rong.push.RongPushClient;

/**
 * Created by diaoqf on 2016/8/2.
 */
public class ConversationFragment extends DispatchResultFragment implements AbsListView.OnScrollListener {
    MessageListFragment mListFragment;
    MessageInputFragment mInputFragment;
    Conversation.ConversationType mConversationType;
    String mTargetId;
    private CSCustomServiceInfo mCustomUserInfo;
    ConversationInfo mCurrentConversationInfo;
//    private InputView.OnInfoButtonClick onInfoButtonClick;
//    private InputView.IInputBoardListener inputBoardListener;
    private boolean robotType = true;
    private int source = 0;
    private boolean resolved = true;
    private boolean committing = false;
    private long enterTime;
    private boolean evaluate = true;

    public static class CustomServiceListener implements ICustomServiceListener {

        WeakReference<ConversationFragment> conversationFragmentWeakReference;

        public CustomServiceListener(ConversationFragment fragment) {
            conversationFragmentWeakReference = new WeakReference<ConversationFragment>(fragment);
        }

        @Override
        public void onSuccess(CustomServiceConfig customServiceConfig) {
            if (conversationFragmentWeakReference.get() != null) {
                if (customServiceConfig.isBlack) {
                    conversationFragmentWeakReference.get().onCustomServiceWarning(conversationFragmentWeakReference.get().getString(io.rong.imkit.R.string.rc_blacklist_prompt), false);
                }

                if (customServiceConfig.robotSessionNoEva) {
                    conversationFragmentWeakReference.get().evaluate = false;
                    if (conversationFragmentWeakReference.get().mListFragment != null) {
                        conversationFragmentWeakReference.get().mListFragment.setNeedEvaluateForRobot(true);
                    }
                }
            }
        }

        @Override
        public void onError(int i, String s) {
            if (conversationFragmentWeakReference.get() != null) {
                conversationFragmentWeakReference.get().onCustomServiceWarning(s, false);
            }
        }

        @Override
        public void onModeChanged(CustomServiceMode customServiceMode) {
            if (conversationFragmentWeakReference.get() != null) {
                conversationFragmentWeakReference.get().mInputFragment.setInputProviderType(customServiceMode);
                if (!customServiceMode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN) && !customServiceMode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN_FIRST)) {
                    if (customServiceMode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE)) {
                        conversationFragmentWeakReference.get().evaluate = false;
                    }
                } else {
                    conversationFragmentWeakReference.get().robotType = false;
                    conversationFragmentWeakReference.get().evaluate = true;
                }

                if (conversationFragmentWeakReference.get().mListFragment != null) {
                    conversationFragmentWeakReference.get().mListFragment.setRobotMode(conversationFragmentWeakReference.get().robotType);
                }
            }
        }

        @Override
        public void onQuit(String s) {
            if(conversationFragmentWeakReference.get()!=null && !conversationFragmentWeakReference.get().committing) {
                conversationFragmentWeakReference.get().onCustomServiceWarning(s, true);
            }
        }

        @Override
        public void onPullEvaluation(String s) {
            if(conversationFragmentWeakReference.get()!= null && !conversationFragmentWeakReference.get().committing) {
                conversationFragmentWeakReference.get().onCustomServiceEvaluation(true, s, conversationFragmentWeakReference.get().robotType, conversationFragmentWeakReference.get().evaluate);
            }
        }
    }

    public ConversationFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RongPushClient.clearAllPushNotifications(RongContext.getInstance());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_conversation, container, false);
        return view;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void onResume() {
        RongPushClient.clearAllPushNotifications(RongContext.getInstance());
        super.onResume();
    }

    protected void initFragment(Uri uri) {
        RLog.d("ConversationFragment", "initFragment : " + uri);

        if(uri != null) {
            String typeStr = uri.getLastPathSegment().toUpperCase();
            this.mConversationType = Conversation.ConversationType.valueOf(typeStr);
            this.mTargetId = uri.getQueryParameter("targetId");
            if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && this.getActivity() != null && this.getActivity().getIntent() != null && this.getActivity().getIntent().getData() != null) {
                this.mCustomUserInfo = this.getActivity().getIntent().getParcelableExtra("customServiceInfo");
            }

            this.mCurrentConversationInfo = ConversationInfo.obtain(this.mConversationType, this.mTargetId);
            RongContext.getInstance().registerConversationInfo(this.mCurrentConversationInfo);
            this.mListFragment = (MessageListFragment)this.getChildFragmentManager().findFragmentById(16908298);
            this.mInputFragment = (MessageInputFragment)this.getChildFragmentManager().findFragmentById(16908311);
            if(this.mListFragment == null) {
                this.mListFragment = new MessageListFragment();
            }

            if(this.mInputFragment == null) {
                this.mInputFragment = new MessageInputFragment();
            }

            if(this.mListFragment.getUri() == null) {
                this.mListFragment.setUri(uri);
            }

            if(this.mInputFragment.getUri() == null) {
                this.mInputFragment.setUri(uri);
            }

            //政策咨询
            findViewById(mInputFragment.getView(),R.id.policy_btn).setOnClickListener(v->{
                Intent intent = new Intent(getActivity(),ConsultFormActivity.class);
                intent.putExtra(ConsultFormActivity.HAS_SCOPE,true);
                getActivity().startActivityForResult(intent,1001);
            });
            //法律法规
            findViewById(mInputFragment.getView(),R.id.law_btn).setOnClickListener(v->{
                Intent intent = new Intent(getActivity(), ConsultFormActivity.class);
                getActivity().startActivityForResult(intent,1001);
            });
            //投诉建议
            findViewById(mInputFragment.getView(),R.id.complain_btn).setOnClickListener(v->{
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra(CommentActivity.SHOW_TYPE,CommentActivity.CHAT_TYPE);
                getActivity().startActivityForResult(intent,1001);
            });
            //房产百科
            findViewById(mInputFragment.getView(),R.id.wiki_btn).setOnClickListener(v->{
                Toast.makeText(getActivity(),"房产百科",Toast.LENGTH_SHORT).show();
            });

            this.mListFragment.setOnScrollListener(this);
            if(this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
                boolean msg = this.getActivity() != null && this.getActivity().getIntent().getBooleanExtra("createIfNotExist", true);
                int message = this.getResources().getInteger(io.rong.imkit.R.integer.rc_chatroom_first_pull_message_count);
                if(msg) {
                    RongIMClient.getInstance().joinChatRoom(this.mTargetId, message, new RongIMClient.OperationCallback() {
                        public void onSuccess() {
                            RLog.i("ConversationFragment", "joinChatRoom onSuccess : " + ConversationFragment.this.mTargetId);
                        }

                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.e("ConversationFragment", "joinChatRoom onError : " + errorCode);
                            ConversationFragment.this.csWarning(ConversationFragment.this.getString(io.rong.imkit.R.string.rc_join_chatroom_failure));
                        }
                    });
                } else {
                    RongIMClient.getInstance().joinExistChatRoom(this.mTargetId, message, new RongIMClient.OperationCallback() {
                        public void onSuccess() {
                            RLog.i("ConversationFragment", "joinExistChatRoom onSuccess : " + ConversationFragment.this.mTargetId);
                        }

                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.e("ConversationFragment", "joinExistChatRoom onError : " + errorCode);
                            ConversationFragment.this.csWarning(ConversationFragment.this.getString(io.rong.imkit.R.string.rc_join_chatroom_failure));
                        }
                    });
                }
            } else if(this.mConversationType != Conversation.ConversationType.APP_PUBLIC_SERVICE && mConversationType != Conversation.ConversationType.PUBLIC_SERVICE) {
                if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                    this.enterTime = System.currentTimeMillis();
                    this.mInputFragment.setOnRobotSwitcherListener(v -> RongIMClient.getInstance().switchToHumanMode(ConversationFragment.this.mTargetId));
                    RongIMClient.getInstance().startCustomService(mTargetId, new CustomServiceListener(this), mCustomUserInfo);
                }
            } else {
                PublicServiceCommandMessage msg1 = new PublicServiceCommandMessage();
                msg1.setCommand(PublicServiceMenu.PublicServiceMenuItemType.Entry.getMessage());
                Message message1 = Message.obtain(this.mTargetId, this.mConversationType, msg1);
                RongIMClient.getInstance().sendMessage(message1, null, null, new IRongCallback.ISendMessageCallback() {
                    public void onAttached(Message message) {
                    }

                    public void onSuccess(Message message) {
                    }

                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    }
                });
            }

            //权限检测
            findViewById(mInputFragment.getView(), 16908295).setOnTouchListener((v, event) ->{
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 1001);
                    if (event.getAction() == event.ACTION_DOWN && !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
                        new android.support.v7.app.AlertDialog.Builder(getActivity())
                                .setTitle("录音权限")
                                .setMessage("需要录音权限才能发送语音,请在权限管理中为应用打开")
                                .setPositiveButton("确定", (dialog, which) -> {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 1001);
                                }).show();
                    }
                    return true;
                }
                return false;
            });

//            findViewById(mInputFragment.getView(), 16908295).setOnClickListener(v->{
//                Toast.makeText(getActivity(),"switch",Toast.LENGTH_SHORT).show();
//            });

        }

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mInputFragment = (MessageInputFragment)this.getChildFragmentManager().findFragmentById(16908311);
        if(this.mInputFragment != null) {
//            this.mInputFragment.setOnInfoButtonClick(this.onInfoButtonClick);
//            this.mInputFragment.setInputBoardListener(this.inputBoardListener);
        }

    }

    public void onDestroyView() {
        RongContext.getInstance().unregisterConversationInfo(this.mCurrentConversationInfo);
        super.onDestroyView();
    }

    public void onDestroy() {
        RongContext.getInstance().getEventBus().unregister(this);
        if(mConversationType != null) {
            if(mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
                SendImageManager.getInstance().cancelSendingImages(mConversationType, mTargetId);
            }

            RongContext.getInstance().executorBackground(() -> RongIM.getInstance().quitChatRoom(ConversationFragment.this.mTargetId, new RongIMClient.OperationCallback() {
                public void onSuccess() {
                }

                public void onError(RongIMClient.ErrorCode errorCode) {
                }
            }));
            if(mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                boolean needToQuit = true;

                try {
                    needToQuit = RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_stop_custom_service_when_quit);
                } catch (Resources.NotFoundException var3) {
                    var3.printStackTrace();
                }

                if(needToQuit) {
                    RongIMClient.getInstance().stopCustomService(mTargetId);
                }
            }
        }

        super.onDestroy();
    }

    public boolean onBackPressed() {
        return mConversationType != null && mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)?this.onCustomServiceEvaluation(false, "", robotType, evaluate):false;
    }

    public boolean handleMessage(android.os.Message msg) {
        return false;
    }

    private void csWarning(String msg) {
        if(getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_warning);
            TextView tv = (TextView)window.findViewById(io.rong.imkit.R.id.rc_cs_msg);
            tv.setText(msg);
            window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(v -> {
                alertDialog.dismiss();
                FragmentManager fm = ConversationFragment.this.getChildFragmentManager();
                if(fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    ConversationFragment.this.getActivity().finish();
                }

            });
        }
    }

    public void onCustomServiceWarning(String msg, final boolean evaluate) {
        if(this.getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_warning);
            TextView tv = (TextView)window.findViewById(io.rong.imkit.R.id.rc_cs_msg);
            tv.setText(msg);
            window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(v -> {
                alertDialog.dismiss();
                if(evaluate) {
                    ConversationFragment.this.onCustomServiceEvaluation(false, "", ConversationFragment.this.robotType, evaluate);
                } else {
                    FragmentManager fm = ConversationFragment.this.getChildFragmentManager();
                    if(fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    } else {
                        ConversationFragment.this.getActivity().finish();
                    }
                }

            });
        }
    }

    public boolean onCustomServiceEvaluation(boolean isPullEva, final String dialogId, final boolean robotType, boolean evaluate) {
        if(!evaluate) {
            return false;
        } else {
            long currentTime = System.currentTimeMillis();
            int interval = 60;

            try {
                interval = RongContext.getInstance().getResources().getInteger(io.rong.imkit.R.integer.rc_custom_service_evaluation_interval);
            } catch (Resources.NotFoundException var14) {
                var14.printStackTrace();
            }

            if(currentTime - this.enterTime < (long)(interval * 1000) && !isPullEva) {
                return false;
            } else {
                this.committing = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setCancelable(false);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                final LinearLayout linearLayout;
                int i;
                View child;
                if(robotType) {
                    window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_robot_evaluation);
                    linearLayout = (LinearLayout)window.findViewById(io.rong.imkit.R.id.rc_cs_yes_no);
                    if(this.resolved) {
                        linearLayout.getChildAt(0).setSelected(true);
                        linearLayout.getChildAt(1).setSelected(false);
                    } else {
                        linearLayout.getChildAt(0).setSelected(false);
                        linearLayout.getChildAt(1).setSelected(true);
                    }

                    for(i = 0; i < linearLayout.getChildCount(); ++i) {
                        child = linearLayout.getChildAt(i);
                        child.setOnClickListener(v -> {
                            v.setSelected(true);
                            int index = linearLayout.indexOfChild(v);
                            if(index == 0) {
                                linearLayout.getChildAt(1).setSelected(false);
                                ConversationFragment.this.resolved = true;
                            } else {
                                ConversationFragment.this.resolved = false;
                                linearLayout.getChildAt(0).setSelected(false);
                            }

                        });
                    }
                } else {
                    window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_human_evaluation);
                    linearLayout = (LinearLayout)window.findViewById(io.rong.imkit.R.id.rc_cs_stars);

                    for(i = 0; i < linearLayout.getChildCount(); ++i) {
                        child = linearLayout.getChildAt(i);
                        if(i < this.source) {
                            child.setSelected(true);
                        }

                        child.setOnClickListener(v -> {
                            int index = linearLayout.indexOfChild(v);
                            int count = linearLayout.getChildCount();
                            ConversationFragment.this.source = index + 1;
                            if(!v.isSelected()) {
                                while(index >= 0) {
                                    linearLayout.getChildAt(index).setSelected(true);
                                    --index;
                                }
                            } else {
                                ++index;

                                while(index < count) {
                                    linearLayout.getChildAt(index).setSelected(false);
                                    ++index;
                                }
                            }

                        });
                    }
                }

                window.findViewById(io.rong.imkit.R.id.rc_btn_cancel).setOnClickListener(v -> {
                    ConversationFragment.this.committing = false;
                    alertDialog.dismiss();
                    FragmentManager fm = ConversationFragment.this.getChildFragmentManager();
                    if(fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    } else {
                        ConversationFragment.this.getActivity().finish();
                    }

                });
                window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(v -> {
                    if(robotType) {
                        RongIMClient.getInstance().evaluateCustomService(ConversationFragment.this.mTargetId, ConversationFragment.this.resolved, "");
                    } else if(ConversationFragment.this.source > 0) {
                        RongIMClient.getInstance().evaluateCustomService(ConversationFragment.this.mTargetId, ConversationFragment.this.source, null, dialogId);
                    }

                    alertDialog.dismiss();
                    ConversationFragment.this.committing = false;
                    FragmentManager fm = ConversationFragment.this.getChildFragmentManager();
                    if(fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    } else {
                        ConversationFragment.this.getActivity().finish();
                    }

                });
                return true;
            }
        }
    }
}