package com.cetnaline.findproperty.entity.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cetnaline.findproperty.utils.MyUtils;

import java.util.regex.Pattern;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.LinkTextView;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utils.AndroidEmoji;
import io.rong.imkit.widget.ArraysDialogFragment;
import io.rong.imkit.widget.LinkTextViewMovementMethod;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by lebro on 2016/8/14.
 */
@ProviderTag(messageContent = TextMessage.class,showProgress = false)
public class MyTextMessageItemProvider extends MessageProvider<TextMessage> {
    private Context mContext;

    public MyTextMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(io.rong.imkit.R.layout.rc_item_text_message, null);
        MyTextMessageItemProvider.ViewHolder holder = new MyTextMessageItemProvider.ViewHolder();
        holder.message = (LinkTextView)view.findViewById(android.R.id.text1);
        view.setTag(holder);
        return view;
    }

    public Spannable getContentSummary(TextMessage data) {
        if(data == null) {
            return null;
        } else {
            String content = data.getContent();
            if(content != null) {
                if(content.length() > 100) {
                    content = content.substring(0, 100);
                }

                return new SpannableString(AndroidEmoji.ensure(MyUtils.translateEmoji(content)));
            } else {
                return null;
            }
        }
    }

    public void onItemClick(View view, int position, TextMessage content, UIMessage message) {
    }

    public void onItemLongClick(final View view, int position, final TextMessage content, final UIMessage message) {
        MyTextMessageItemProvider.ViewHolder holder = (MyTextMessageItemProvider.ViewHolder)view.getTag();
        holder.longClick = true;
        if(view instanceof TextView) {
            CharSequence name = ((TextView)view).getText();
            if(name != null && name instanceof Spannable) {
                Selection.removeSelection((Spannable)name);
            }
        }

        String name1 = null;
        if(!message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName()) && !message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())) {
            if(message.getSenderUserId() != null) {
                UserInfo items1 = message.getUserInfo();
                if(items1 == null || items1.getName() == null) {
                    items1 = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
                }

                if(items1 != null) {
                    name1 = items1.getName();
                }
            }
        } else if(message.getUserInfo() != null) {
            name1 = message.getUserInfo().getName();
        } else {
            Conversation.PublicServiceType items = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
            PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(items, message.getTargetId());
            if(info != null) {
                name1 = info.getName();
            }
        }

        String[] items2 = new String[]{view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_delete)};
        ArraysDialogFragment.newInstance(name1, items2).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener() {
            public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    ClipboardManager clipboard = (ClipboardManager)view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(content.getContent());
                } else if(which == 1) {
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, (RongIMClient.ResultCallback)null);
                }

            }
        }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
    }

    public void bindView(final View v, int position, TextMessage content, final UIMessage data) {
        MyTextMessageItemProvider.ViewHolder holder = (MyTextMessageItemProvider.ViewHolder)v.getTag();
        if(data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.message.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
        } else {
            holder.message.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }

        final LinkTextView textView = holder.message;
        if(data.getTextMessageContent() != null) {
            int len = data.getTextMessageContent().length();
            if(v.getHandler() != null && len > 500) {
                v.getHandler().postDelayed(() -> textView.setText(data.getTextMessageContent()), 50L);
            } else {
                textView.setText(getString(data.getTextMessageContent().toString(),holder));
            }
        }

        holder.message.setMovementMethod(new LinkTextViewMovementMethod(link -> {
            RongIM.ConversationBehaviorListener listener = RongContext.getInstance().getConversationBehaviorListener();
            return listener != null?listener.onMessageLinkClick(v.getContext(), link):false;
        }));
    }


    private SpannableString getString(String content,MyTextMessageItemProvider.ViewHolder holder) {
        String number_pattern = "^\\d{8,11}$";
        Pattern r = Pattern.compile(number_pattern);
        String number_pattern_1 = "^\\d{8,10},\\d{4,6}$";
        Pattern r1 = Pattern.compile(number_pattern_1);

        SpannableString msp = new SpannableString(AndroidEmoji.ensure(MyUtils.translateEmoji(content)));

        if (r.matcher(content).matches() || r1.matcher(content).matches()) {
//            msp.setSpan(new ForegroundColorSpan(Color.RED),0,msp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            msp.setSpan(new UnderlineSpan(),0,content.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            msp.setSpan(new URLSpan("tel:"+content), 0,content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (holder != null) {
                holder.message.setLinkTextColor(Color.RED);
            }
        }  else if (MyUtils.isWebUrl(content)) {
//            msp.setSpan(new ForegroundColorSpan(Color.BLUE),0,msp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            msp.setSpan(new UnderlineSpan(),0,msp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            msp.setSpan(new URLSpan(content), 0,content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.message.setLinkTextColor(Color.BLUE);
        }
        return msp;
    }

    class ViewHolder {
        LinkTextView message;
        boolean longClick;

        ViewHolder() {
        }
    }
}
