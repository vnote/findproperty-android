package com.cetnaline.findproperty.api.bean;

import com.cetnaline.findproperty.entity.bean.BaseBean;

/**
 * Created by diaoqf on 2016/10/25.
 */

public class StoreBo extends BaseBean {


    /**
     * StoreId : 215
     * StoreName : 陆家嘴凯旋分行
     * RegionId : 2172
     * GscopeId : 217212
     * StoreAddr : 浦电路35号
     * StoreTel : 02151787618
     * Store400Tel : 4008188808,970062
     * StoreHonor :
     * Lng : 121.521916
     * Lat : 31.222875
     * PaNo :
     * StaffCount : 84
     * TencentVistaUrl :
     */

    private int StoreId;
    private String StoreName;
    private int RegionId;
    private int GscopeId;
    private String StoreAddr;
    private String StoreTel;
    private String Store400Tel;
    private String StoreHonor;
    private double Lng;
    private double Lat;
    private String PaNo;
    private int StaffCount;
    private String TencentVistaUrl;

    public int getStoreId() {
        return StoreId;
    }

    public void setStoreId(int StoreId) {
        this.StoreId = StoreId;
    }

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String StoreName) {
        this.StoreName = StoreName;
    }

    public int getRegionId() {
        return RegionId;
    }

    public void setRegionId(int RegionId) {
        this.RegionId = RegionId;
    }

    public int getGscopeId() {
        return GscopeId;
    }

    public void setGscopeId(int GscopeId) {
        this.GscopeId = GscopeId;
    }

    public String getStoreAddr() {
        return StoreAddr;
    }

    public void setStoreAddr(String StoreAddr) {
        this.StoreAddr = StoreAddr;
    }

    public String getStoreTel() {
        return StoreTel;
    }

    public void setStoreTel(String StoreTel) {
        this.StoreTel = StoreTel;
    }

    public String getStore400Tel() {
        return Store400Tel;
    }

    public void setStore400Tel(String Store400Tel) {
        this.Store400Tel = Store400Tel;
    }

    public String getStoreHonor() {
        return StoreHonor;
    }

    public void setStoreHonor(String StoreHonor) {
        this.StoreHonor = StoreHonor;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double Lng) {
        this.Lng = Lng;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double Lat) {
        this.Lat = Lat;
    }

    public String getPaNo() {
        return PaNo;
    }

    public void setPaNo(String PaNo) {
        this.PaNo = PaNo;
    }

    public int getStaffCount() {
        return StaffCount;
    }

    public void setStaffCount(int StaffCount) {
        this.StaffCount = StaffCount;
    }

    public String getTencentVistaUrl() {
        return TencentVistaUrl;
    }

    public void setTencentVistaUrl(String TencentVistaUrl) {
        this.TencentVistaUrl = TencentVistaUrl;
    }
}
