package com.cetnaline.findproperty.ui.listadapter;

/**
 * Created by diaoqf on 2016/9/6.
 */


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.orhanobut.logger.Logger;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.R.drawable;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.model.Event.InputViewEvent;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utils.RongDateUtils;
import io.rong.imkit.widget.ProviderContainerView;
import io.rong.imkit.widget.adapter.BaseAdapter;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UnknownMessage;
import io.rong.imlib.model.UserInfo;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageListAdapter extends BaseAdapter<UIMessage> {
    LayoutInflater mInflater;
    Context mContext;
    Drawable mDefaultDrawable;
    OnItemHandlerListener mOnItemHandlerListener;
    View subView;
    boolean evaForRobot = false;
    boolean robotMode = true;
    private boolean timeGone = false;
    private DrawableRequestBuilder<String> requestBuilder;

    public MessageListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mDefaultDrawable = context.getResources().getDrawable(drawable.rc_ic_def_msg_portrait);
        requestBuilder = GlideLoad.init((Activity) mContext);
    }

    public void setOnItemHandlerListener(OnItemHandlerListener onItemHandlerListener) {
        this.mOnItemHandlerListener = onItemHandlerListener;
    }

    public long getItemId(int position) {
        Message message = (Message)this.getItem(position);
        return message == null?-1L:(long)message.getMessageId();
    }

    protected View newView(Context context, int position, ViewGroup group) {
        View result = this.mInflater.inflate(R.layout.rc_item_message, null);
        ViewHolder holder = new ViewHolder();
        holder.leftIconView = this.findViewById(result, R.id.rc_left);
        holder.rightIconView = this.findViewById(result, R.id.rc_right);
        holder.nameView = this.findViewById(result, R.id.rc_title);
        holder.contentView = this.findViewById(result, R.id.rc_content);
        holder.layout = this.findViewById(result, R.id.rc_layout);
        holder.progressBar = this.findViewById(result, R.id.rc_progress);
        holder.warning = this.findViewById(result, R.id.rc_warning);
        holder.readReceipt = this.findViewById(result, R.id.rc_read_receipt);
        holder.time = this.findViewById(result, R.id.rc_time);
        holder.sentStatus = this.findViewById(result, R.id.rc_sent_status);
        if(holder.time.getVisibility() == View.GONE) {
            this.timeGone = true;
        } else {
            this.timeGone = false;
        }

        result.setTag(holder);
        return result;
    }

    public void playNextAudioIfNeed(UIMessage data, int position) {
        MessageProvider provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
        if(provider != null && this.subView != null) {
            provider.onItemClick(this.subView, position, data.getContent(), data);
        }

    }

    private boolean getNeedEvaluate(UIMessage data) {
        String extra = "";
        String robotEva = "";
        String sid = "";
        if(data != null && data.getConversationType() != null && data.getConversationType().equals(ConversationType.CUSTOMER_SERVICE)) {
            if(data.getContent() instanceof TextMessage) {
                extra = ((TextMessage)data.getContent()).getExtra();
                if(TextUtils.isEmpty(extra)) {
                    return false;
                }

                try {
                    JSONObject e = new JSONObject(extra);
                    robotEva = e.optString("robotEva");
                    sid = e.optString("sid");
                } catch (JSONException var6) {
                    ;
                }
            }

            if(data.getMessageDirection() == MessageDirection.RECEIVE && data.getContent() instanceof TextMessage && this.evaForRobot && this.robotMode && !TextUtils.isEmpty(robotEva) && !TextUtils.isEmpty(sid) && !data.getIsHistoryMessage()) {
                return true;
            }
        }

        return false;
    }

    protected void bindView(View v, final int position, final UIMessage data) {
        if(data != null) {
            ViewHolder holder = (ViewHolder)v.getTag();
            Object provider = null;
            ProviderTag tag = null;
            if(this.getNeedEvaluate(data)) {
                provider = RongContext.getInstance().getEvaluateProvider();
                tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
            } else {
                if(RongContext.getInstance() == null || data == null || data.getContent() == null) {
                    RLog.e("MessageListAdapter", "Message is null !");
                    return;
                }

                provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                if(provider == null) {
                    provider = RongContext.getInstance().getMessageTemplate(UnknownMessage.class);
                    tag = RongContext.getInstance().getMessageProviderTag(UnknownMessage.class);
                } else {
                    tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
                }

                if(provider == null) {
                    RLog.e("MessageListAdapter", data.getObjectName() + " message provider not found !");
                    return;
                }
            }

            View view = holder.contentView.inflate((IContainerItemProvider)provider);
            ((IContainerItemProvider)provider).bindView(view, position, data);
            this.subView = view;
            if(tag == null) {
                RLog.e("MessageListAdapter", "Can not find ProviderTag for " + data.getObjectName());
            } else {
                if(tag.hide()) {
                    holder.contentView.setVisibility(View.GONE);
                    holder.time.setVisibility(View.GONE);
                    holder.nameView.setVisibility(View.GONE);
                    holder.leftIconView.setVisibility(View.GONE);
                    holder.rightIconView.setVisibility(View.GONE);
                } else {
                    holder.contentView.setVisibility(View.VISIBLE);
                }

                final UserInfo[] time1 = new UserInfo[1];
                if(data.getMessageDirection() == MessageDirection.SEND) {
                    if(tag.showPortrait()) {
                        holder.rightIconView.setVisibility(View.VISIBLE);
                        holder.leftIconView.setVisibility(View.GONE);
                    } else {
                        holder.leftIconView.setVisibility(View.GONE);
                        holder.rightIconView.setVisibility(View.GONE);
                    }

                    if(!tag.centerInHorizontal()) {
                        this.setGravity(holder.layout, 5);
                        holder.contentView.containerViewRight();
                        holder.nameView.setGravity(5);
                    } else {
                        this.setGravity(holder.layout, 17);
                        holder.contentView.containerViewCenter();
                        holder.nameView.setGravity(1);
                        holder.contentView.setBackgroundColor(0);
                    }

                    boolean time = RongIMClient.getInstance().getReadReceipt();
                    if(data.getSentStatus() == SentStatus.SENDING) {
                        if(tag.showProgress()) {
                            holder.progressBar.setVisibility(View.VISIBLE);
                        } else {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        holder.warning.setVisibility(View.GONE);
                        holder.readReceipt.setVisibility(View.GONE);
                    } else if(data.getSentStatus() == SentStatus.FAILED) {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.warning.setVisibility(View.VISIBLE);
                        holder.readReceipt.setVisibility(View.GONE);
                    } else if(data.getSentStatus() == SentStatus.SENT) {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.warning.setVisibility(View.GONE);
                        holder.readReceipt.setVisibility(View.GONE);
                    } else if(time && data.getSentStatus() == SentStatus.READ) {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.warning.setVisibility(View.GONE);
                        MessageContent pre = data.getMessage().getContent();
                        if(!(pre instanceof InformationNotificationMessage)) {
                            holder.readReceipt.setVisibility(View.VISIBLE);
                        } else {
                            holder.readReceipt.setVisibility(View.GONE);
                        }
                    } else {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.warning.setVisibility(View.GONE);
                        holder.readReceipt.setVisibility(View.GONE);
                    }

                    if(data.getObjectName().equals("RC:VSTMsg")) {
                        holder.readReceipt.setVisibility(View.GONE);
                    }

                    holder.nameView.setVisibility(View.GONE);
                    holder.rightIconView.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if(RongContext.getInstance().getConversationBehaviorListener() != null) {
                                UserInfo userInfo = null;
                                if(!TextUtils.isEmpty(data.getSenderUserId())) {
                                    userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                    userInfo = userInfo == null?new UserInfo(data.getSenderUserId(), (String)null, (Uri)null):userInfo;
                                }

                                RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(MessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                            }

                        }
                    });
                    holder.rightIconView.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            if(RongContext.getInstance().getConversationBehaviorListener() != null) {
                                UserInfo userInfo = null;
                                if(!TextUtils.isEmpty(data.getSenderUserId())) {
                                    userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                    userInfo = userInfo == null?new UserInfo(data.getSenderUserId(), (String)null, (Uri)null):userInfo;
                                }

                                return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(MessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                            } else {
                                return true;
                            }
                        }
                    });
                    if(!tag.showWarning()) {
                        holder.warning.setVisibility(View.GONE);
                    }
                } else {
                    if(tag.showPortrait()) {
                        holder.rightIconView.setVisibility(View.GONE);
                        holder.leftIconView.setVisibility(View.VISIBLE);
                    } else {
                        holder.leftIconView.setVisibility(View.GONE);
                        holder.rightIconView.setVisibility(View.GONE);
                    }

                    if(!tag.centerInHorizontal()) {
                        this.setGravity(holder.layout, 3);
                        holder.contentView.containerViewLeft();
                        holder.nameView.setGravity(3);
                    } else {
                        this.setGravity(holder.layout, 17);
                        holder.contentView.containerViewCenter();
                        holder.nameView.setGravity(1);
                        holder.contentView.setBackgroundColor(0);
                    }

                    holder.progressBar.setVisibility(View.GONE);
                    holder.warning.setVisibility(View.GONE);
                    holder.readReceipt.setVisibility(View.GONE);
                    holder.nameView.setVisibility(View.GONE);
                    if(data.getConversationType() != ConversationType.PRIVATE && tag.showPortrait() && data.getConversationType() != ConversationType.PUBLIC_SERVICE && data.getConversationType() != ConversationType.APP_PUBLIC_SERVICE) {
                        if(data.getConversationType().equals(ConversationType.CUSTOMER_SERVICE) && data.getUserInfo() != null && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                            time1[0] = data.getUserInfo();
                            holder.nameView.setText(time1[0].getName());
                        } else if(data.getConversationType() == ConversationType.GROUP) {
                            GroupUserInfo pre1 = RongUserInfoManager.getInstance().getGroupUserInfo(data.getTargetId(), data.getSenderUserId());
                            if(pre1 != null) {
                                holder.nameView.setText(pre1.getNickname());
                            } else {
                                time1[0] = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                if(time1[0] == null) {
                                    holder.nameView.setText(data.getSenderUserId());
                                } else {
                                    holder.nameView.setText(time1[0].getName());
                                }
                            }
                        } else {
                            time1[0] = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                            if(time1[0] == null) {
                                holder.nameView.setText(data.getSenderUserId());
                            } else {
                                holder.nameView.setText(time1[0].getName());
                            }
                        }
                    } else {
                        holder.nameView.setVisibility(View.GONE);
                    }

                    holder.leftIconView.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if(RongContext.getInstance().getConversationBehaviorListener() != null) {
                                UserInfo userInfo = null;
                                if(!TextUtils.isEmpty(data.getSenderUserId())) {
                                    userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                    userInfo = userInfo == null?new UserInfo(data.getSenderUserId(), (String)null, (Uri)null):userInfo;
                                }

                                RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(MessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                            }

                            EventBus.getDefault().post(InputViewEvent.obtain(false));
                        }
                    });
                }

                holder.leftIconView.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        if(RongContext.getInstance().getConversationBehaviorListener() != null) {
                            UserInfo userInfo = null;
                            if(!TextUtils.isEmpty(data.getSenderUserId())) {
                                userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                userInfo = userInfo == null?new UserInfo(data.getSenderUserId(), (String)null, (Uri)null):userInfo;
                            }

                            return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(MessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                        } else {
                            return false;
                        }
                    }
                });
                PublicServiceProfile publicServiceProfile;
                ConversationKey mKey;
                Uri pre2;
                if(holder.rightIconView.getVisibility() == View.VISIBLE) {
                    if(data.getConversationType().equals(ConversationType.CUSTOMER_SERVICE) && data.getUserInfo() != null && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                        time1[0] = data.getUserInfo();
                        pre2 = time1[0].getPortraitUri();
                        if(pre2 != null) {
//                            holder.rightIconView.setAvatar(pre2.toString(), 0);
                            GlideLoad.load(new GlideLoad.Builder(requestBuilder, pre2.toString(),R.drawable.user_default_portrait,R.drawable.user_default_portrait)
//                                    .placeHolder(R.drawable.mine_default)
//                                    .error(R.drawable.mine_default)
                                    .into(holder.rightIconView));
                        }
                    } else if((data.getConversationType().equals(ConversationType.PUBLIC_SERVICE) || data.getConversationType().equals(ConversationType.APP_PUBLIC_SERVICE)) && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                        time1[0] = data.getUserInfo();
                        if(time1[0] != null) {
                            pre2 = time1[0].getPortraitUri();
                            if(pre2 != null) {
//                                holder.leftIconView.setAvatar(pre2.toString(), 0);
                                GlideLoad.load(new GlideLoad.Builder(requestBuilder, pre2.toString(),R.drawable.mine_default,R.drawable.mine_default)
                                        .into(holder.leftIconView));
                            }
                        } else {
                            mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
                            publicServiceProfile = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
                            pre2 = publicServiceProfile.getPortraitUri();
                            if(pre2 != null) {
//                                holder.rightIconView.setAvatar(pre2.toString(), 0);
                                GlideLoad.load(new GlideLoad.Builder(requestBuilder, pre2.toString(),R.drawable.user_default_portrait,R.drawable.user_default_portrait)
                                        .into(holder.rightIconView));
                            }
                        }
                    } else if(!TextUtils.isEmpty(data.getSenderUserId())) {
                        time1[0] = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                        if(time1[0] != null && time1[0].getPortraitUri() != null) {
//                            holder.rightIconView.setAvatar(time1.getPortraitUri().toString(), 0);
                            GlideLoad.load(new GlideLoad.Builder(requestBuilder, time1[0].getPortraitUri().toString(),R.drawable.user_default_portrait,R.drawable.user_default_portrait)
                                    .into(holder.rightIconView));
                        }
                    }
                } else if(holder.leftIconView.getVisibility() == View.VISIBLE) {
                    if(data.getConversationType().equals(ConversationType.CUSTOMER_SERVICE) && data.getUserInfo() != null && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                        time1[0] = data.getUserInfo();
                        pre2 = time1[0].getPortraitUri();
                        if(pre2 != null) {
//                            holder.leftIconView.setAvatar(pre2.toString(), 0);
                            GlideLoad.load(new GlideLoad.Builder(requestBuilder, pre2.toString(),R.drawable.mine_default,R.drawable.mine_default)
                                    .into(holder.leftIconView));
                        }
                    } else if((data.getConversationType().equals(ConversationType.PUBLIC_SERVICE) || data.getConversationType().equals(ConversationType.APP_PUBLIC_SERVICE)) && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                        time1[0] = data.getUserInfo();
                        if(time1[0] != null) {
                            pre2 = time1[0].getPortraitUri();
                            if(pre2 != null) {
//                                holder.leftIconView.setAvatar(pre2.toString(), 0);
                                GlideLoad.load(new GlideLoad.Builder(requestBuilder, pre2.toString(),R.drawable.mine_default,R.drawable.mine_default)
                                        .into(holder.leftIconView));
                            }
                        } else {
                            mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
                            publicServiceProfile = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
                            if(publicServiceProfile != null && publicServiceProfile.getPortraitUri() != null) {
//                                holder.leftIconView.setAvatar(publicServiceProfile.getPortraitUri().toString(), 0);
                                GlideLoad.load(new GlideLoad.Builder(requestBuilder, publicServiceProfile.getPortraitUri().toString(),R.drawable.mine_default,R.drawable.mine_default)
                                        .into(holder.leftIconView));
                            }
                        }
                    } else if(!TextUtils.isEmpty(data.getSenderUserId())) {
                        time1[0] = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                        //这里处理未获取经纪人头像的情况
                        if (time1[0] == null) {
                            String no = data.getSenderUserId().substring(data.getSenderUserId().lastIndexOf("_") + 1, data.getSenderUserId().length());
                            Staff staff = DbUtil.getStaffByUid(no);
                            if (staff == null) {
                                ApiRequest.getStaffDetail(no)
                                        .subscribe(bean -> {
                                            Staff staff1 = new Staff();
                                            staff1.setName(bean.CnName);
                                            staff1.setImageUrl(bean.StaffImg);
                                            if (no.indexOf("|") < 0) {
                                                staff1.setDepartmentName(bean.StoreName);
                                                staff1.setMobile(bean.Mobile);
                                                staff1.setUId(bean.StaffNo.toLowerCase());
                                                DbUtil.addStaff(staff1);
                                            }
                                            time1[0] = new UserInfo(data.getSenderUserId(),bean.CnName,Uri.parse(bean.StaffImg));
                                            RongUserInfoManager.getInstance().setUserInfo(time1[0]);
                                            Logger.i("get userinfo from network");
                                        },throwable -> throwable.printStackTrace());
                            } else {
                                time1[0] = new UserInfo(data.getSenderUserId(),staff.getName(),Uri.parse(staff.getImageUrl()));
                                RongUserInfoManager.getInstance().setUserInfo(time1[0]);
                            }
                        }
                        if(time1[0] != null && time1[0].getPortraitUri() != null) {
//                            holder.leftIconView.setAvatar(time1.getPortraitUri().toString(), 0);
//                            Logger.i("time1-pre2:"+ time1[0].getPortraitUri().toString());
                            GlideLoad.load(new GlideLoad.Builder(requestBuilder, time1[0].getPortraitUri().toString(),R.drawable.mine_default,R.drawable.mine_default)
                                    .into(holder.leftIconView));
                        }
                    }
                }

                if(view != null) {
                    view.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if(RongContext.getInstance().getConversationBehaviorListener() == null || !RongContext.getInstance().getConversationBehaviorListener().onMessageClick(MessageListAdapter.this.mContext, v, data)) {
                                Object provider;
                                if(MessageListAdapter.this.getNeedEvaluate(data)) {
                                    provider = RongContext.getInstance().getEvaluateProvider();
                                } else {
                                    provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                                }

                                if(provider != null) {
                                    ((MessageProvider)provider).onItemClick(v, position, data.getContent(), data);
                                }

                            }
                        }
                    });
                    view.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            if(RongContext.getInstance().getConversationBehaviorListener() != null && RongContext.getInstance().getConversationBehaviorListener().onMessageLongClick(MessageListAdapter.this.mContext, v, data)) {
                                return true;
                            } else {
                                Object provider;
                                if(MessageListAdapter.this.getNeedEvaluate(data)) {
                                    provider = RongContext.getInstance().getEvaluateProvider();
                                } else {
                                    provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                                }

                                if(provider != null) {
                                    ((MessageProvider)provider).onItemLongClick(v, position, data.getContent(), data);
                                }

                                return true;
                            }
                        }
                    });
                }

                holder.warning.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if(MessageListAdapter.this.mOnItemHandlerListener != null) {
                            MessageListAdapter.this.mOnItemHandlerListener.onWarningViewClick(position, data, v);
                        }

                    }
                });
                if(tag.hide()) {
                    holder.time.setVisibility(View.GONE);
                } else {
                    if(!this.timeGone) {
                        String time2 = RongDateUtils.getConversationFormatDate(new Date(data.getSentTime()));
                        holder.time.setText(time2);
                        if(position == 0) {
                            holder.time.setVisibility(View.VISIBLE);
                        } else {
                            Message pre3 = (Message)this.getItem(position - 1);
                            if(data.getSentTime() - pre3.getSentTime() > 60000L) {
                                holder.time.setVisibility(View.VISIBLE);
                            } else {
                                holder.time.setVisibility(View.GONE);
                            }
                        }
                    }

                }
            }
        }
    }

    private final void setGravity(View view, int gravity) {
        LayoutParams params = (LayoutParams)view.getLayoutParams();
        params.gravity = gravity;
    }

    public void setEvaluateForRobot(boolean needEvaluate) {
        this.evaForRobot = needEvaluate;
    }

    public void setRobotMode(boolean robotMode) {
        this.robotMode = robotMode;
    }

    public interface OnItemHandlerListener {
        void onWarningViewClick(int var1, Message var2, View var3);
    }

    class ViewHolder {
        CircleImageView leftIconView;
        CircleImageView rightIconView;
        TextView nameView;
        ProviderContainerView contentView;
        ProgressBar progressBar;
        ImageView warning;
        ImageView readReceipt;
        ViewGroup layout;
        TextView time;
        TextView sentStatus;

        ViewHolder() {
        }
    }
}
