package com.cetnaline.findproperty.entity.event;

/**
 * Created by diaoqf on 2016/8/11.
 */
public class AppLoginEvent {
    public static final int CALL_CHAT_FRAGMENT = 0;
    public static final int CALL_MINE_FRAGMENT = 1;

    public boolean isLogin;
    public int callType;

    public AppLoginEvent(boolean isLogin, int callType) {
        this.isLogin = isLogin;
        this.callType = callType;
    }
}
