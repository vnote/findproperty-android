package com.cetnaline.findproperty.entity.event;

/**
 * 文件上传事件
 * Created by diaoqf on 2017/5/25.
 */

public class UploadEvent {
    public static final String DEPUTE_UPLOAD_1 = "DEPUTE_UPLOAD_1";
    public static final String DEPUTE_UPLOAD_2 = "DEPUTE_UPLOAD_2";
    public static final String DEPUTE_UPLOAD_3 = "DEPUTE_UPLOAD_3";

    private String tag;

    private int percent;

    public UploadEvent(String tag, int percent) {
        this.tag = tag;
        this.percent = percent;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
