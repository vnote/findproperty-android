package com.cetnaline.findproperty.entity.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.utils.MyUtils;
import com.orhanobut.logger.Logger;

import java.util.regex.Pattern;

import io.rong.imkit.RongContext;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utils.AndroidEmoji;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * Created by diaoqf on 2016/9/29.
 */
@ProviderTag(messageContent = SystemMessage.class, showPortrait = false, showProgress = false,centerInHorizontal = true, showWarning = false)
public class SystemMessageItemProvider extends IContainerItemProvider.MessageProvider<SystemMessage> {

    class ViewHolder {
        TextView message;
    }

    @Override
    public void bindView(View view, int i, SystemMessage systemMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.message.setText(systemMessage.getContent());
    }

    @Override
    public Spannable getContentSummary(SystemMessage systemMessage) {
        if(systemMessage == null) {
            return null;
        } else {
            String content = systemMessage.getContent();
            if(content != null) {
                if(content.length() > 100) {
                    content = content.substring(0, 100);
                }

                return new SpannableString(content);
            } else {
                return null;
            }
        }
    }

    @Override
    public void onItemClick(View view, int i, SystemMessage systemMessage, UIMessage uiMessage) {
        //打开网页
        if (MyUtils.isWebUrl(systemMessage.getExtra())) {
            if (MyUtils.isWebUrl(systemMessage.getExtra())) {
                Intent intent1 = new Intent(RongContext.getInstance(),WebActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(WebActivity.TARGET_URL,systemMessage.getExtra());
                intent1.putExtras(bundle);
                RongContext.getInstance().startActivity(intent1);
            }
//            String url = systemMessage.getExtra().substring(systemMessage.getExtra().lastIndexOf("=")+1);
//            url = MyUtils.getURLDecoderString(url);
//            if (!MyUtils.openActivityForUrl(RongContext.getInstance(),url,true)){
//                Intent intent1 = new Intent(RongContext.getInstance(),WebActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString(WebActivity.TARGET_URL,url);
//                intent1.putExtras(bundle);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                RongContext.getInstance().startActivity(intent1);
//            }
        }
    }

    @Override
    public void onItemLongClick(View view, int i, SystemMessage systemMessage, UIMessage uiMessage) {
        //长摁事件
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_system_message, null);
        ViewHolder holder = new ViewHolder();
        holder.message = (TextView) view.findViewById(R.id.text);
        view.setTag(holder);
        return view;
    }
}
