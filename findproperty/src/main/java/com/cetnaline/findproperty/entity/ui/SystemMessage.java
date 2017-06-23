package com.cetnaline.findproperty.entity.ui;

import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * Created by diaoqf on 2016/9/29.
 */
@MessageTag(value = "RC:SysMsg", flag = 3)
public class SystemMessage extends MessageContent {
    private static final String TAG = "SystemMessage";
    private String content;
    protected String extra;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("content", this.getContent());
            if(!TextUtils.isEmpty(this.getExtra())) {
                jsonObj.put("extra", this.getExtra());
            }

            if(this.getJSONUserInfo() != null) {
                jsonObj.putOpt("user", this.getJSONUserInfo());
            }
        } catch (JSONException var4) {
            RLog.e("TextMessage", "JSONException " + var4.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public SystemMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
        }

        try {
            JSONObject e = new JSONObject(jsonStr);
            if(e.has("content")) {
                this.setContent(e.optString("content"));
            }

            if(e.has("extra")) {
                this.setExtra(e.optString("extra"));
            }

            if(e.has("user")) {
                this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
            }
        } catch (JSONException var4) {
            RLog.e("TextMessage", "JSONException " + var4.getMessage());
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.extra);
    }

    public SystemMessage() {
    }

    protected SystemMessage(Parcel in) {
        this.content = in.readString();
        this.extra = in.readString();
    }

    public static final Creator<SystemMessage> CREATOR = new Creator<SystemMessage>() {
        @Override
        public SystemMessage createFromParcel(Parcel source) {
            return new SystemMessage(source);
        }

        @Override
        public SystemMessage[] newArray(int size) {
            return new SystemMessage[size];
        }
    };
}
