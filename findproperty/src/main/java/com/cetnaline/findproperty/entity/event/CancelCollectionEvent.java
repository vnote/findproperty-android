package com.cetnaline.findproperty.entity.event;

/**
 * Created by diaoqf on 2016/9/19.
 */
public class CancelCollectionEvent {
    public static final String SALE = "ershoufang";
    public static final String RENT = "zufang";
    public static final String NEW = "new";
    public static final String ESTATE = "estate";

    public CancelCollectionEvent(String type, String value, boolean saveCollection) {
        this.type = type;
        this.value = value;
        this.saveCollection = saveCollection;
    }

    public String type;
    public String value;
    public boolean saveCollection;
}
