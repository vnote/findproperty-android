package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 小区
 * Created by guilin on 16/1/11.
 */
public class EsfEstateDo implements Parcelable {

    private String Name;
    private int SaleCount;
    private int RentCount;
    private String Pc_Addr;
    private String CestCode;
    private double CestAvgPrice;
    private double CestAvgPriceR;
    private double Lng;
    private double Lat;
    private String DefaultImg;
    private String Scope;
    private String Region;
    @SerializedName("TencentVistaUrl")
    private String tencentVistaUrl;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getSaleCount() {
        return SaleCount;
    }

    public void setSaleCount(int saleCount) {
        SaleCount = saleCount;
    }

    public int getRentCount() {
        return RentCount;
    }

    public void setRentCount(int rentCount) {
        RentCount = rentCount;
    }

    public String getPc_Addr() {
        return Pc_Addr;
    }

    public void setPc_Addr(String pc_Addr) {
        Pc_Addr = pc_Addr;
    }

    public String getCestCode() {
        return CestCode;
    }

    public void setCestCode(String cestCode) {
        CestCode = cestCode;
    }

    public double getCestAvgPrice() {
        return CestAvgPrice;
    }

    public void setCestAvgPrice(double cestAvgPrice) {
        CestAvgPrice = cestAvgPrice;
    }

    public double getCestAvgPriceR() {
        return CestAvgPriceR;
    }

    public void setCestAvgPriceR(double cestAvgPriceR) {
        CestAvgPriceR = cestAvgPriceR;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public String getDefaultImg() {
        return DefaultImg;
    }

    public void setDefaultImg(String defaultImg) {
        DefaultImg = defaultImg;
    }

    public String getScope() {
        return Scope;
    }

    public void setScope(String scope) {
        Scope = scope;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getTencentVistaUrl() {
        return tencentVistaUrl;
    }

    public void setTencentVistaUrl(String tencentVistaUrl) {
        this.tencentVistaUrl = tencentVistaUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Name);
        dest.writeInt(this.SaleCount);
        dest.writeInt(this.RentCount);
        dest.writeString(this.Pc_Addr);
        dest.writeString(this.CestCode);
        dest.writeDouble(this.CestAvgPrice);
        dest.writeDouble(this.CestAvgPriceR);
        dest.writeDouble(this.Lng);
        dest.writeDouble(this.Lat);
        dest.writeString(this.DefaultImg);
        dest.writeString(this.Scope);
        dest.writeString(this.Region);
        dest.writeString(this.tencentVistaUrl);
    }

    public EsfEstateDo() {
    }

    protected EsfEstateDo(Parcel in) {
        this.Name = in.readString();
        this.SaleCount = in.readInt();
        this.RentCount = in.readInt();
        this.Pc_Addr = in.readString();
        this.CestCode = in.readString();
        this.CestAvgPrice = in.readDouble();
        this.CestAvgPriceR = in.readDouble();
        this.Lng = in.readDouble();
        this.Lat = in.readDouble();
        this.DefaultImg = in.readString();
        this.Scope = in.readString();
        this.Region = in.readString();
        this.tencentVistaUrl = in.readString();
    }

    public static final Creator<EsfEstateDo> CREATOR = new Creator<EsfEstateDo>() {
        @Override
        public EsfEstateDo createFromParcel(Parcel source) {
            return new EsfEstateDo(source);
        }

        @Override
        public EsfEstateDo[] newArray(int size) {
            return new EsfEstateDo[size];
        }
    };
}
