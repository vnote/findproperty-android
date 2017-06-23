package com.cetnaline.findproperty.ui.broadcastreceiver;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.utils.RxBus;
import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by diaoqf on 2017/3/15.
 */

public class SMSContent extends ContentObserver {
    private Cursor cursor = null;
    private String last_id = "";
    private Activity activity;
    private Uri uri;
    private String verifyText;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SMSContent(Handler handler) {
        super(handler);
    }

    public SMSContent(Handler handler, Activity activity, String verifyText) {
        super(handler);
        this.activity = activity;
        this.verifyText = verifyText;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        // 读取收件箱中指定号码的短信
        cursor = activity.managedQuery(Uri.parse("content://sms/inbox"),
                new String[] { "_id", "address", "read", "body" }, "address in (?,?)", new String[] {AppContents.SMS_SERVER_1,AppContents.SMS_SERVER_2 }, "date desc");
//                new String[] { "_id", "address", "read", "body" }, null,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String phone = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String _id = cursor.getString(cursor.getColumnIndex("_id"));
                // 缓存上一次信息
                if (!last_id.equals(_id)) {
                    Logger.i("1111未接短信---->" + "电话号码：" + phone + "内容：" + body);
                    last_id = _id;
                    ContentValues values = new ContentValues();
                    values.put("read", "1"); // 修改短信为已读模式
                }

                Pattern pattern= Pattern.compile("\\d{6}");
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    Logger.i("code:"+matcher.group(0));
                    RxBus.getDefault().send(new NormalEvent(NormalEvent.SMS_CODE, matcher.group(0)));
                }
            }

        }
        cursor.close();
    }

//    @Override
//    public void onChange(boolean selfChange, Uri uri) {
//        super.onChange(selfChange, uri);
//        Logger.i(uri.getPath());
//
//        cursor = activity.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[] { "*" }, "read=? and address in (?,?)", new String[] { "0", AppContents.SMS_SERVER_1,AppContents.SMS_SERVER_2 }, "date desc");
////                new String[] { "_id", "address", "read", "body" }, null,null,null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                String phone = cursor.getString(cursor.getColumnIndex("address"));
//                String body = cursor.getString(cursor.getColumnIndex("body"));
//                String _id = cursor.getString(cursor.getColumnIndex("_id"));
//                // 缓存上一次信息
//                if (!last_id.equals(_id)) {
//                    Logger.i("1111未接短信---->" + "电话号码：" + phone + "内容：" + body);
//                    last_id = _id;
//                    ContentValues values = new ContentValues();
//                    values.put("read", "1"); // 修改短信为已读模式
//                }
//
//                Pattern pattern= Pattern.compile(verifyText);
//                Matcher matcher = pattern.matcher(body);
//                if (matcher.find()) {
//                    Logger.i("code:"+matcher.group(0));
//                    RxBus.getDefault().send(new NormalEvent(NormalEvent.SMS_CODE, matcher.group(0)));
//                }
//            }
//
//        }
//        cursor.close();
//
//    }
}
