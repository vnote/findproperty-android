package com.cetnaline.findproperty.ui.listadapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.ui.fragment.ConversationListFragment;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.ProviderContainerView;
import io.rong.imkit.widget.adapter.BaseAdapter;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import rx.functions.Action1;

/**
 * Created by diaoqf on 2016/7/26.
 */
public class ConversationListFragmentAdapter  extends BaseAdapter<UIConversation> {
    private static final String TAG = "ConversationListFragmentAdapter";
    LayoutInflater mInflater;
    Context mContext;
    ConversationListFragment fragment;
    private DrawableRequestBuilder<String> requestBuilder;

    private OnPortraitItemClick mOnPortraitItemClick;

    public long getItemId(int position) {
        UIConversation conversation = this.getItem(position);
        return conversation == null?0L:(long)conversation.hashCode();
    }

    public ConversationListFragmentAdapter(Context context,ConversationListFragment fragment) {
        this.mContext = context;
        this.fragment = fragment;
        this.mInflater = LayoutInflater.from(this.mContext);
        requestBuilder = GlideLoad.init(fragment);
    }

    public int findGatherPosition(Conversation.ConversationType type) {
        int index = this.getCount();
        int position = -1;
        if(RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
            while(index-- > 1) {
                if(((UIConversation)this.getItem(index)).getConversationType().equals(type)) {
                    position = index;
                    break;
                }
            }
        }

        return position;
    }

    public int findPosition(Conversation.ConversationType type, String targetId) {
        int index = this.getCount();
        int position = -1;
        if(RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
            while(index-- > 0) {
                if(((UIConversation)this.getItem(index)).getConversationType().equals(type)) {
                    position = index;
                    break;
                }
            }
        } else {
            while(index-- > 0) {
                if(((UIConversation)this.getItem(index)).getConversationType().equals(type) && ((UIConversation)this.getItem(index)).getConversationTargetId().equals(targetId)) {
                    position = index;
                    break;
                }
            }
        }

        return position;
    }

    @Override
    public UIConversation getItem(int position) {
        return super.getItem(position);
    }

    protected View newView(Context context, int position, ViewGroup group) {
        View result = this.mInflater.inflate(R.layout.list_conversation_item, (ViewGroup)null);
        ViewHolder holder = new ViewHolder();
        holder.layout = this.findViewById(result, R.id.rc_item_conversation);
        holder.leftImageLayout = this.findViewById(result, R.id.rc_item1);
        holder.rightImageLayout = this.findViewById(result, R.id.rc_item2);
        holder.leftImageView = this.findViewById(result, R.id.rc_left);
        holder.rightImageView = this.findViewById(result, R.id.rc_right);
        holder.contentView = this.findViewById(result, R.id.rc_content);
        holder.unReadMsgCount = this.findViewById(result, R.id.rc_unread_message);
        holder.unReadMsgCountRight = this.findViewById(result, R.id.rc_unread_message_right);
        holder.unReadMsgCountIcon = this.findViewById(result, R.id.rc_unread_message_icon);
        holder.unReadMsgCountRightIcon = this.findViewById(result, R.id.rc_unread_message_icon_right);
        result.setTag(holder);
        return result;
    }

    private void loadView(ViewHolder holder, int position,UIConversation data) {
        if(data.isTop()) {
            holder.layout.setBackgroundDrawable(this.mContext.getResources().getDrawable(io.rong.imkit.R.drawable.rc_item_top_list_selector));
        } else {
            holder.layout.setBackgroundDrawable(this.mContext.getResources().getDrawable(io.rong.imkit.R.drawable.rc_item_list_selector));
        }

        ConversationProviderTag tag = RongContext.getInstance().getConversationProviderTag(data.getConversationType().getName());
        boolean defaultId = false;
        int defaultId1;

        if(tag.portraitPosition() == 1) {
            holder.leftImageLayout.setVisibility(View.VISIBLE);
            if(data.getConversationType().equals(Conversation.ConversationType.GROUP)) {
                defaultId1 = io.rong.imkit.R.drawable.rc_default_group_portrait;
            } else if(data.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) {
                defaultId1 = io.rong.imkit.R.drawable.rc_default_discussion_portrait;
            } else {
                defaultId1 = io.rong.imkit.R.drawable.rc_default_portrait;
            }

            holder.leftImageLayout.setOnClickListener(v -> {
                if(ConversationListFragmentAdapter.this.mOnPortraitItemClick != null && data.getConversationTargetId().indexOf("|") < 0) {
                    ConversationListFragmentAdapter.this.mOnPortraitItemClick.onPortraitItemClick(v, data);
                }

            });
            holder.leftImageLayout.setOnLongClickListener(v -> {
                //去除法务 投诉处理等在线客服的详情页链接
                if(ConversationListFragmentAdapter.this.mOnPortraitItemClick != null ) {
                    ConversationListFragmentAdapter.this.mOnPortraitItemClick.onPortraitItemLongClick(v, data);
                }

                return true;
            });

            if(data.getConversationGatherState()) {
            } else if(data.getIconUrl() != null) {
                GlideLoad.load(new GlideLoad.Builder(requestBuilder, data.getIconUrl().toString(),defaultId1,defaultId1)
//                        .placeHolder(defaultId1)
//                        .error(defaultId1)
                        .into(holder.leftImageView));
            } else {
                //头像加载
                if (data.getMessageContent()!= null && data.getMessageContent().getUserInfo() != null && data.getMessageContent().getUserInfo().getPortraitUri() != null) {
                    GlideLoad.load(new GlideLoad.Builder(requestBuilder, data.getMessageContent().getUserInfo().getPortraitUri().toString(),defaultId1,defaultId1)
                            .into(holder.leftImageView));
                } else {
                    GlideLoad.load(new GlideLoad.Builder(requestBuilder, data.getIconUrl()+"",defaultId1,defaultId1)
                            .into(holder.leftImageView));
                }
            }

            if(data.getUnReadMessageCount() > 0) {
                holder.unReadMsgCountIcon.setVisibility(View.VISIBLE);
                if(data.getUnReadType().equals(UIConversation.UnreadRemindType.REMIND_WITH_COUNTING)) {
                    if(data.getUnReadMessageCount() > 99) {
                        holder.unReadMsgCount.setText(this.mContext.getResources().getString(io.rong.imkit.R.string.rc_message_unread_count));
                    } else {
                        holder.unReadMsgCount.setText(Integer.toString(data.getUnReadMessageCount()));
                    }

                    holder.unReadMsgCount.setVisibility(View.VISIBLE);
                    holder.unReadMsgCountIcon.setImageResource(io.rong.imkit.R.drawable.rc_unread_count_bg);
                } else {
                    holder.unReadMsgCount.setVisibility(View.GONE);
                    holder.unReadMsgCountIcon.setImageResource(io.rong.imkit.R.drawable.rc_unread_remind_list_count);
                }
            } else {
                holder.unReadMsgCountIcon.setVisibility(View.GONE);
                holder.unReadMsgCount.setVisibility(View.GONE);
            }

            holder.rightImageLayout.setVisibility(View.GONE);
        } else if(tag.portraitPosition() == 2) {
            holder.rightImageLayout.setVisibility(View.VISIBLE);
            holder.rightImageLayout.setOnClickListener(v -> {
                if(ConversationListFragmentAdapter.this.mOnPortraitItemClick != null) {
                    ConversationListFragmentAdapter.this.mOnPortraitItemClick.onPortraitItemClick(v, data);
                }

            });
            holder.rightImageLayout.setOnLongClickListener(v -> {
                if(ConversationListFragmentAdapter.this.mOnPortraitItemClick != null) {
                    ConversationListFragmentAdapter.this.mOnPortraitItemClick.onPortraitItemLongClick(v, data);
                }

                return true;
            });
            if(data.getConversationType().equals(Conversation.ConversationType.GROUP)) {
                defaultId1 = io.rong.imkit.R.drawable.rc_default_group_portrait;
            } else if(data.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) {
                defaultId1 = io.rong.imkit.R.drawable.rc_default_discussion_portrait;
            } else {
                defaultId1 = io.rong.imkit.R.drawable.rc_default_portrait;
            }

            GlideLoad.load(new GlideLoad.Builder(requestBuilder, data.getIconUrl().toString(),defaultId1,defaultId1)
                    .into(holder.rightImageView));

            if(data.getUnReadMessageCount() > 0) {
                holder.unReadMsgCountRightIcon.setVisibility(View.VISIBLE);
                if(data.getUnReadType().equals(UIConversation.UnreadRemindType.REMIND_WITH_COUNTING)) {
                    holder.unReadMsgCount.setVisibility(View.VISIBLE);
                    if(data.getUnReadMessageCount() > 99) {
                        holder.unReadMsgCountRight.setText(this.mContext.getResources().getString(io.rong.imkit.R.string.rc_message_unread_count));
                    } else {
                        holder.unReadMsgCountRight.setText(Integer.toString(data.getUnReadMessageCount()));
                    }

                    holder.unReadMsgCountRightIcon.setImageResource(io.rong.imkit.R.drawable.rc_unread_count_bg);
                } else {
                    holder.unReadMsgCount.setVisibility(View.GONE);
                    holder.unReadMsgCountRightIcon.setImageResource(io.rong.imkit.R.drawable.rc_unread_remind_without_count);
                }
            } else {
                holder.unReadMsgCountIcon.setVisibility(View.GONE);
                holder.unReadMsgCount.setVisibility(View.GONE);
            }

            holder.leftImageLayout.setVisibility(View.GONE);
        } else {
            if(tag.portraitPosition() != 3) {
                throw new IllegalArgumentException("the portrait position is wrong!");
            }

            holder.rightImageLayout.setVisibility(View.GONE);
            holder.leftImageLayout.setVisibility(View.GONE);
        }
    }

    protected void bindView(View v, int position, final UIConversation data) {
        ViewHolder holder = (ViewHolder)v.getTag();
        if(data != null) {
            IContainerItemProvider.ConversationProvider provider = RongContext.getInstance().getConversationTemplate(data.getConversationType().getName());
            if(provider == null) {
                RLog.e("ConversationListFragmentAdapter", "provider is null");
            } else {
                if (holder == null) {
                    return;
                }
                View view = holder.contentView.inflate(provider);
                if (data.getMessageContent()!= null && data.getMessageContent().getUserInfo() != null) {
                    provider.bindView(view, position, data);
                    loadView(holder,position,data);
                } else {
                    String id = data.getConversationTargetId().substring(data.getConversationTargetId().lastIndexOf("_")+1,data.getConversationTargetId().length());
                    fragment.getCompositeSubscription().add(
                            ApiRequest.getStaffDetail(id)
                                .subscribe(new Action1<StaffListBean>() {
                                    @Override
                                    public void call(StaffListBean staffListBean) {
                                        Staff staff1 = new Staff();
                                        staff1.setName(staffListBean.CnName);
                                        staff1.setImageUrl(staffListBean.StaffImg);
                                        if (id.indexOf("|") < 0) {
                                            staff1.setDepartmentName(staffListBean.StoreName);
                                            staff1.setMobile(staffListBean.Mobile);
                                            staff1.setUId(staffListBean.StaffNo.toLowerCase());
                                            String number = staffListBean.MobileBy400 != null ? staffListBean.MobileBy400 : staffListBean.Staff400Tel;
                                            staff1.setStaff400Tel(number);
                                            DbUtil.addStaff(staff1);
                                        }

                                        UserInfo userInfo = new UserInfo(id, staff1.getName(), Uri.parse(staff1.getImageUrl()));
                                        RongUserInfoManager.getInstance().setUserInfo(userInfo);
                                        data.getMessageContent().setUserInfo(userInfo);
                                        data.setUIConversationTitle(userInfo.getName());
                                        provider.bindView(view, position, data);
                                        loadView(holder,position,data);
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                        provider.bindView(view, position, data);
                                        loadView(holder,position,data);
                                    }
                                }));
                }
            }
        }
    }

    public void setOnPortraitItemClick(OnPortraitItemClick onPortraitItemClick) {
        this.mOnPortraitItemClick = onPortraitItemClick;
    }

    public interface OnPortraitItemClick {
        void onPortraitItemClick(View var1, UIConversation var2);

        boolean onPortraitItemLongClick(View var1, UIConversation var2);
    }

    class ViewHolder {
        View layout;
        View leftImageLayout;
        View rightImageLayout;
        CircleImageView leftImageView;
        TextView unReadMsgCount;
        ImageView unReadMsgCountIcon;
        CircleImageView rightImageView;
        TextView unReadMsgCountRight;
        ImageView unReadMsgCountRightIcon;
        ProviderContainerView contentView;
        TextView delete;

        ViewHolder() {
        }
    }
}