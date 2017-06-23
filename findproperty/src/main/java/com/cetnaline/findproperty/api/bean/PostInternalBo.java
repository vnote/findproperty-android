package com.cetnaline.findproperty.api.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * 均价走势
 * Created by Ruan on 2016/2/16.
 */
public class PostInternalBo {

    private String type;
    private String key;
    private String Name;
    private int ZIndex;
    @SerializedName("DealList")
    private ArrayList<DealListBo> dealListBos;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getZIndex() {
        return ZIndex;
    }

    public void setZIndex(int ZIndex) {
        this.ZIndex = ZIndex;
    }

    public ArrayList<DealListBo> getDealListBos() {
        return dealListBos;
    }

    public void setDealListBos(ArrayList<DealListBo> dealListBos) {
        this.dealListBos = dealListBos;
    }
}
