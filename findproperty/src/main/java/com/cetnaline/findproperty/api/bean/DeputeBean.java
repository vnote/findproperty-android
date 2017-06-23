package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by diaoqf on 2017/5/8.
 */

public class DeputeBean implements Parcelable {

    /**
     * HouseID : 4403556
     * ProcessStatus : 1
     * EntrustID : 20213
     * UserID : U100144423
     * EntrustType : 1
     */

    private String HouseID;
    private int ProcessStatus;
    private String EntrustID;
    private String UserID;
    private int EntrustType;

    public String getHouseID() {
        return HouseID;
    }

    public void setHouseID(String HouseID) {
        this.HouseID = HouseID;
    }

    public int getProcessStatus() {
        return ProcessStatus;
    }

    public void setProcessStatus(int ProcessStatus) {
        this.ProcessStatus = ProcessStatus;
    }

    public String getEntrustID() {
        return EntrustID;
    }

    public void setEntrustID(String EntrustID) {
        this.EntrustID = EntrustID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public int getEntrustType() {
        return EntrustType;
    }

    public void setEntrustType(int EntrustType) {
        this.EntrustType = EntrustType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.HouseID);
        dest.writeInt(this.ProcessStatus);
        dest.writeString(this.EntrustID);
        dest.writeString(this.UserID);
        dest.writeInt(this.EntrustType);
    }

    public DeputeBean() {
    }

    protected DeputeBean(Parcel in) {
        this.HouseID = in.readString();
        this.ProcessStatus = in.readInt();
        this.EntrustID = in.readString();
        this.UserID = in.readString();
        this.EntrustType = in.readInt();
    }

    public static final Parcelable.Creator<DeputeBean> CREATOR = new Parcelable.Creator<DeputeBean>() {
        @Override
        public DeputeBean createFromParcel(Parcel source) {
            return new DeputeBean(source);
        }

        @Override
        public DeputeBean[] newArray(int size) {
            return new DeputeBean[size];
        }
    };
}
