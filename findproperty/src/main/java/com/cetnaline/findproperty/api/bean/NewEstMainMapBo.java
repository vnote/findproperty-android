package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;

/**
 * 新盘地图找房区域板块
 * Created by guilin on 16/4/13.
 */
public class NewEstMainMapBo implements Parcelable {

    private int level;//层级
    private long gscopId;//区域or板块id
    private String name;//区域板块名字
    private LatLng latLng;
    private int count;

    public NewEstMainMapBo(int level, long gscopId) {
        this.level = level;
        this.gscopId = gscopId;
    }

    /**
     * 房源数量递增
     */
    public void add() {
        count++;
    }

    public void setLatLng(LatLng latLng) {
        if (this.latLng == null) {
            this.latLng = latLng;
        }
    }

    public void setName(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }

    public int getLevel() {
        return level;
    }

    public long getGscopId() {
        return gscopId;
    }

    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.level);
        dest.writeLong(this.gscopId);
        dest.writeString(this.name);
        dest.writeParcelable(this.latLng, flags);
        dest.writeInt(this.count);
    }

    protected NewEstMainMapBo(Parcel in) {
        this.level = in.readInt();
        this.gscopId = in.readLong();
        this.name = in.readString();
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.count = in.readInt();
    }

    public static final Parcelable.Creator<NewEstMainMapBo> CREATOR = new Parcelable.Creator<NewEstMainMapBo>() {
        @Override
        public NewEstMainMapBo createFromParcel(Parcel source) {
            return new NewEstMainMapBo(source);
        }

        @Override
        public NewEstMainMapBo[] newArray(int size) {
            return new NewEstMainMapBo[size];
        }
    };
}
