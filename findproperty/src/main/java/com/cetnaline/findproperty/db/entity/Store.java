package com.cetnaline.findproperty.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by diaoqf on 2016/12/2.
 */
@Entity(
        nameInDb = "house_store"
)
public class Store {
    @Id
    private long StoreId;
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
@Generated(hash = 954550866)
public Store(long StoreId, String StoreName, int RegionId, int GscopeId,
        String StoreAddr, String StoreTel, String Store400Tel,
        String StoreHonor, double Lng, double Lat, String PaNo, int StaffCount,
        String TencentVistaUrl) {
    this.StoreId = StoreId;
    this.StoreName = StoreName;
    this.RegionId = RegionId;
    this.GscopeId = GscopeId;
    this.StoreAddr = StoreAddr;
    this.StoreTel = StoreTel;
    this.Store400Tel = Store400Tel;
    this.StoreHonor = StoreHonor;
    this.Lng = Lng;
    this.Lat = Lat;
    this.PaNo = PaNo;
    this.StaffCount = StaffCount;
    this.TencentVistaUrl = TencentVistaUrl;
}
@Generated(hash = 770513066)
public Store() {
}
public long getStoreId() {
    return this.StoreId;
}
public void setStoreId(int StoreId) {
    this.StoreId = StoreId;
}
public String getStoreName() {
    return this.StoreName;
}
public void setStoreName(String StoreName) {
    this.StoreName = StoreName;
}
public int getRegionId() {
    return this.RegionId;
}
public void setRegionId(int RegionId) {
    this.RegionId = RegionId;
}
public int getGscopeId() {
    return this.GscopeId;
}
public void setGscopeId(int GscopeId) {
    this.GscopeId = GscopeId;
}
public String getStoreAddr() {
    return this.StoreAddr;
}
public void setStoreAddr(String StoreAddr) {
    this.StoreAddr = StoreAddr;
}
public String getStoreTel() {
    return this.StoreTel;
}
public void setStoreTel(String StoreTel) {
    this.StoreTel = StoreTel;
}
public String getStore400Tel() {
    return this.Store400Tel;
}
public void setStore400Tel(String Store400Tel) {
    this.Store400Tel = Store400Tel;
}
public String getStoreHonor() {
    return this.StoreHonor;
}
public void setStoreHonor(String StoreHonor) {
    this.StoreHonor = StoreHonor;
}
public double getLng() {
    return this.Lng;
}
public void setLng(double Lng) {
    this.Lng = Lng;
}
public double getLat() {
    return this.Lat;
}
public void setLat(double Lat) {
    this.Lat = Lat;
}
public String getPaNo() {
    return this.PaNo;
}
public void setPaNo(String PaNo) {
    this.PaNo = PaNo;
}
public int getStaffCount() {
    return this.StaffCount;
}
public void setStaffCount(int StaffCount) {
    this.StaffCount = StaffCount;
}
public String getTencentVistaUrl() {
    return this.TencentVistaUrl;
}
public void setTencentVistaUrl(String TencentVistaUrl) {
    this.TencentVistaUrl = TencentVistaUrl;
}
public void setStoreId(long StoreId) {
    this.StoreId = StoreId;
}
}
