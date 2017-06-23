package com.cetnaline.findproperty.entity.ui;

import com.cetnaline.findproperty.R;

import java.io.Serializable;

/**
 * Created by diaoqf on 2016/10/19.
 */

public class StoremapEntity implements Serializable {

    private static final long serialVersionUID = 8633299996744734593L;

    private double latitude;//纬度
    private double longitude;//经度

    private String name;//店铺名字
    private int imgId;//图片

    private String description;//描述

    public StoremapEntity() {}
    public StoremapEntity(double latitude, double longitude, String name, String description) {
        this(latitude, longitude, name, R.drawable.ic_staff_store,description);
    }
    public StoremapEntity(double latitude, double longitude, String name, int imgId, String description) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.imgId = imgId;
        this.description = description;
    }

    //toString方法
    @Override
    public String toString() {
        return "StoremapEntity [latitude=" + latitude + ", longitude=" + longitude + ", name=" + name + ", imgId="
                + imgId + ", description=" + description + "]";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
