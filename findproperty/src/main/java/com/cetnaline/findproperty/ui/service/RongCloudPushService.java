package com.cetnaline.findproperty.ui.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import io.rong.push.common.RLog;
import io.rong.push.core.PushConnectivityManager;

/**
 * Created by diaoqf on 2016/12/13.
 */

public class RongCloudPushService extends Service {
    private static final String TAG = "RongCloudPushService";

    public RongCloudPushService() {
    }

    public void onCreate() {
        super.onCreate();
        Logger.i("buobao:create service");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i("buobao:start service");
        SharedPreferences sp = this.getSharedPreferences("RongPush", 0);
        String appKey = sp.getString("appKey", "");
        String deviceId = sp.getString("deviceId", "");
        String pushTypes = sp.getString("enabledPushTypes", "");
        String pushDomain = sp.getString("pushDomain", "");
        if(!TextUtils.isEmpty(pushDomain)) {
            PushConnectivityManager.getInstance().setServerDomain(pushDomain);
        }

        if(!TextUtils.isEmpty(appKey) && !TextUtils.isEmpty(deviceId) && !PushConnectivityManager.getInstance().isInitialized()) {
            PushConnectivityManager.getInstance().init(this, deviceId, appKey, pushTypes);
            PushConnectivityManager.getInstance().connect();
        }

        if(intent != null && intent.getAction() != null) {
            RLog.d("buobao", "onStartCommand, action = " + intent.getAction());
            if(intent.getAction().equals("io.rong.push.intent.action.INIT") && !PushConnectivityManager.getInstance().isInitialized()) {
                deviceId = intent.getStringExtra("deviceId");
                pushTypes = intent.getStringExtra("enabledPushTypes");
                appKey = intent.getStringExtra("appKey");
                pushDomain = intent.getStringExtra("pushDomain");
                if(!TextUtils.isEmpty(pushDomain)) {
                    PushConnectivityManager.getInstance().setServerDomain(pushDomain);
                }

                if(!TextUtils.isEmpty(appKey) && !TextUtils.isEmpty(deviceId)) {
                    sp.edit().putString("deviceId", deviceId).apply();
                    sp.edit().putString("appKey", appKey).apply();
                    sp.edit().putString("enabledPushTypes", pushTypes).apply();
                    PushConnectivityManager.getInstance().init(this, deviceId, appKey, pushTypes);
                    PushConnectivityManager.getInstance().connect();
                } else {
                    RLog.e("buobao", "appKey or deviceId is null.");
                }
            } else {
                String last;
                if(intent.getAction().equals("io.rong.push.intent.action.REGISTRATION_INFO")) {
                    last = intent.getStringExtra("regInfo");
                    String[] wifi_state = last.split("\\|");
                    String mobile_state = this.getSharedPreferences("RongPush", 0).getString("pushTypeUsed", "");
                    RLog.i("buobao", "received info:" + last + ",pushType cached:" + mobile_state);
                    if(wifi_state[0].equals(mobile_state)) {
                        PushConnectivityManager.getInstance().sendRegistrationIDToServer(last);
                    } else {
                        RLog.e("buobao", "Push type received is different from the one cached. So ignore this event.");
                    }
                } else if(intent.getAction().equals("io.rong.push.intent.action.HEART_BEAT")) {
                    last = intent.getStringExtra("PING");
                    if(last == null) {
                        PushConnectivityManager.getInstance().ping();
                    } else if(last.equals("PING")) {
                        PushConnectivityManager.getInstance().onPingTimeout();
                    }
                } else if(intent.getAction().equals("io.rong.push.intent.action.STOP_PUSH")) {
                    PushConnectivityManager.getInstance().disconnect();
                } else if("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                    PushConnectivityManager.NetworkType last1 = PushConnectivityManager.getInstance().getNetworkType();
                    ConnectivityManager ConnManager = (ConnectivityManager)this.getSystemService("connectivity");
                    NetworkInfo.State wifi_state1;
                    if(ConnManager.getNetworkInfo(1) == null) {
                        wifi_state1 = null;
                    } else {
                        wifi_state1 = ConnManager.getNetworkInfo(1).getState();
                    }

                    NetworkInfo.State mobile_state1;
                    if(ConnManager.getNetworkInfo(0) == null) {
                        mobile_state1 = null;
                    } else {
                        mobile_state1 = ConnManager.getNetworkInfo(0).getState();
                    }

                    if(wifi_state1 != null && wifi_state1 == NetworkInfo.State.CONNECTED) {
                        PushConnectivityManager.getInstance().setNetworkType(PushConnectivityManager.NetworkType.WIFI);
                    } else if(mobile_state1 != null && mobile_state1 == NetworkInfo.State.CONNECTED) {
                        PushConnectivityManager.getInstance().setNetworkType(PushConnectivityManager.NetworkType.MOBILE);
                    } else {
                        PushConnectivityManager.getInstance().setNetworkType(PushConnectivityManager.NetworkType.ERROR);
                    }

                    PushConnectivityManager.NetworkType current = PushConnectivityManager.getInstance().getNetworkType();
                    RLog.d("buobao", "wifi = " + wifi_state1 + ", mobile = " + mobile_state1 + ", last = " + last1 + ", current = " + current);
                    if(current.equals(PushConnectivityManager.NetworkType.ERROR)) {
                        PushConnectivityManager.getInstance().stopPingTimer();
                        PushConnectivityManager.getInstance().disconnect();
                    } else if(last1.equals(PushConnectivityManager.NetworkType.ERROR)) {
                        PushConnectivityManager.getInstance().connect();
                    } else {
                        PushConnectivityManager.getInstance().disconnect();
                        PushConnectivityManager.getInstance().connect();
                    }
                } else if("android.intent.action.USER_PRESENT".equals(intent.getAction()) || "android.intent.action.ACTION_POWER_CONNECTED".equals(intent.getAction()) || "android.intent.action.ACTION_POWER_DISCONNECTED".equals(intent.getAction()) || "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                    PushConnectivityManager.getInstance().connect();
                }
            }

            return 1;
        } else {
            return 1;
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Logger.i("buobao:destroy service");
        super.onDestroy();
    }
}
