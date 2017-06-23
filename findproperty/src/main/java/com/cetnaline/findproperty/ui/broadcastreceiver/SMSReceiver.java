package com.cetnaline.findproperty.ui.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.utils.RxBus;
import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短信广播监听
 * Created by diaoqf on 2017/3/15.
 */

public class SMSReceiver extends BroadcastReceiver {
    private String verifyCode="";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)){
            SmsMessage[] messages = getMessagesFromIntent(intent);
            for (SmsMessage message : messages){
                Logger.i(message.getOriginatingAddress() + " : " +
                        message.getDisplayOriginatingAddress() + " : " +
                        message.getDisplayMessageBody() + " : " +
                        message.getTimestampMillis());
                String smsContent=message.getDisplayMessageBody();
                Pattern pattern= Pattern.compile("\\d{6}");
                Matcher matcher = pattern.matcher(smsContent);
                if (matcher.find()) {
                    Logger.i("code:"+matcher.group(0));
                    RxBus.getDefault().send(new NormalEvent(NormalEvent.SMS_CODE, matcher.group(0)));
                }
                if ((AppContents.SMS_SERVER_1.equals(message.getDisplayOriginatingAddress()) ||
                        AppContents.SMS_SERVER_2.equals(message.getDisplayOriginatingAddress())) &&
                        smsContent.contains("您的验证码是")) {
                    RxBus.getDefault().send(new NormalEvent(NormalEvent.SMS_CODE, smsContent.substring(smsContent.indexOf("：")+1,smsContent.indexOf("，"))));
                }
            }
        }
    }

    private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];
        for (int i = 0; i < messages.length; i++)
        {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++)        {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }
}
