package com.cetnaline.findproperty.ui.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.utils.MyUtils;
import com.orhanobut.logger.Logger;

import io.rong.eventbus.EventBus;

/**
 * App状态监听
 * Created by diaoqf on 2016/9/5.
 */
public class StateBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!MyUtils.checkNetwork(context)) {
            Logger.e("Network is validate");
            Toast.makeText(context,"当前没有可用网络",Toast.LENGTH_SHORT).show();
            return;
        }
        Logger.i("Network is ok");
        //连接网络时自动加载数据
        EventBus.getDefault().post(new NormalEvent(NormalEvent.NETWORK));
    }
}
