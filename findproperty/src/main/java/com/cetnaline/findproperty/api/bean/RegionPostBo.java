package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 租售区域板块房源数量
 * Created by guilin on 16/2/16.
 */
public class RegionPostBo implements Parcelable {


    /**
     * GScopeCode : H
     * GScopeId : 2172
     * GScopeLevel : 2
     * GScopeName : 浦东
     * GScopeAlias : 浦东
     * FullPY : pudongxinqu
     * FirstPY : PDXQ
     * ParentId : 21
     * OrderBy : 1
     * Lng : 121.560796
     * Lat : 31.218296
     * SaleCount : 19024
     * RentCount : 0
     * EstateCount : 2268
     * SaleAvgPrice : 46313.0163887997
     * SaleAvgPriceRise : 0.33
     */

    private String GScopeCode;
    private int GScopeId;
    private int GScopeLevel;
    private String GScopeName;
    private String GScopeAlias;
    private String FullPY;
    private String FirstPY;
    private int ParentId;
    private int OrderBy;
    private double Lng;
    private double Lat;
    private int SaleCount;
    private int RentCount;
    private int EstateCount;
    private double SaleAvgPrice;
    private double SaleAvgPriceRise;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.GScopeCode);
        dest.writeInt(this.GScopeId);
        dest.writeInt(this.GScopeLevel);
        dest.writeString(this.GScopeName);
        dest.writeString(this.GScopeAlias);
        dest.writeString(this.FirstPY);
        dest.writeString(this.FullPY);
        dest.writeInt(this.ParentId);
        dest.writeInt(this.OrderBy);
        dest.writeDouble(this.Lng);
        dest.writeDouble(this.Lat);
        dest.writeInt(this.RentCount);
        dest.writeInt(this.SaleCount);
        dest.writeInt(this.EstateCount);
        dest.writeDouble(this.SaleAvgPrice);
        dest.writeDouble(this.SaleAvgPriceRise);
    }

    public RegionPostBo() {
    }

    protected RegionPostBo(Parcel in) {
        this.GScopeCode = in.readString();
        this.GScopeId = in.readInt();
        this.GScopeLevel = in.readInt();
        this.GScopeName = in.readString();
        this.GScopeAlias = in.readString();
        this.FirstPY = in.readString();
        this.FullPY = in.readString();
        this.GScopeCode = in.readString();
        this.ParentId = in.readInt();
        this.OrderBy = in.readInt();
        this.Lng = in.readDouble();
        this.Lat = in.readDouble();
        this.RentCount = in.readInt();
        this.SaleCount = in.readInt();
        this.SaleAvgPrice = in.readDouble();
        this.SaleAvgPriceRise = in.readDouble();
    }

    public static final Creator<RegionPostBo> CREATOR = new Creator<RegionPostBo>() {
        public RegionPostBo createFromParcel(Parcel source) {
            return new RegionPostBo(source);
        }

        public RegionPostBo[] newArray(int size) {
            return new RegionPostBo[size];
        }
    };

    public String getGScopeCode() {
        return GScopeCode;
    }

    public void setGScopeCode(String GScopeCode) {
        this.GScopeCode = GScopeCode;
    }

    public int getGScopeId() {
        return GScopeId;
    }

    public void setGScopeId(int GScopeId) {
        this.GScopeId = GScopeId;
    }

    public int getGScopeLevel() {
        return GScopeLevel;
    }

    public void setGScopeLevel(int GScopeLevel) {
        this.GScopeLevel = GScopeLevel;
    }

    public String getGScopeName() {
        return GScopeName;
    }

    public void setGScopeName(String GScopeName) {
        this.GScopeName = GScopeName;
    }

    public String getGScopeAlias() {
        return GScopeAlias;
    }

    public void setGScopeAlias(String GScopeAlias) {
        this.GScopeAlias = GScopeAlias;
    }

    public String getFullPY() {
        return FullPY;
    }

    public void setFullPY(String FullPY) {
        this.FullPY = FullPY;
    }

    public String getFirstPY() {
        return FirstPY;
    }

    public void setFirstPY(String FirstPY) {
        this.FirstPY = FirstPY;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setParentId(int ParentId) {
        this.ParentId = ParentId;
    }

    public int getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(int OrderBy) {
        this.OrderBy = OrderBy;
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

    public int getSaleCount() {
        return SaleCount;
    }

    public void setSaleCount(int SaleCount) {
        this.SaleCount = SaleCount;
    }

    public int getRentCount() {
        return RentCount;
    }

    public void setRentCount(int RentCount) {
        this.RentCount = RentCount;
    }

    public int getEstateCount() {
        return EstateCount;
    }

    public void setEstateCount(int EstateCount) {
        this.EstateCount = EstateCount;
    }

    public double getSaleAvgPrice() {
        return SaleAvgPrice;
    }

    public void setSaleAvgPrice(double SaleAvgPrice) {
        this.SaleAvgPrice = SaleAvgPrice;
    }

    public double getSaleAvgPriceRise() {
        return SaleAvgPriceRise;
    }

    public void setSaleAvgPriceRise(double SaleAvgPriceRise) {
        this.SaleAvgPriceRise = SaleAvgPriceRise;
    }
}
