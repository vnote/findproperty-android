package com.cetnaline.findproperty.entity.event;

/**
 * Created by diaoqf on 2016/8/19.
 */
public class NormalEvent {
    //99 微信绑定
    public static final int NETWORK=98;
    public static final int WX_BIND=99;
    public static final int ORDER_UPDATE=100;
    public static final int SHOW_NO_CONVERSATION_LAYOUT=101;
    public static final int SHOW_STORE_LOCATION = 102;
    public static final int INTENT_CHANGE = 103;
    public static final int SMS_CODE = 104;
    public static final int REFRESH_ENTRUST_LIST = 105; //刷新委托列表
    public static final int REFRESH_LOOK_LIST = 106;  //刷新约看列表

    public NormalEvent() {}

    public NormalEvent(int type) {
        this.type = type;
    }

    public NormalEvent(int type, String data) {
        this.type = type;
        this.data = data;
    }

    public int type;
    public boolean result;
    public String data;
}
