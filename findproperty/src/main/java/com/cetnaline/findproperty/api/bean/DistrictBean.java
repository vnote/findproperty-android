package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class DistrictBean implements Parcelable, Serializable {

	private int GScopeId;
	private String GScopeCnName;
	private String FullPY;
	private String FirstPY;
	private int GScopeLevel;
	private int ParentId;
	private double lng;
	private double lat;
	private int OrderBy;

	public int getOrderBy() {
		return OrderBy;
	}

	public void setOrderBy(int orderBy) {
		OrderBy = orderBy;
	}

	public int getGScopeId() {
		return GScopeId;
	}

	public void setGScopeId(int GScopeId) {
		this.GScopeId = GScopeId;
	}

	public String getGScopeCnName() {
		return GScopeCnName;
	}

	public void setGScopeCnName(String GScopeCnName) {
		this.GScopeCnName = GScopeCnName;
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

	public int getGScopeLevel() {
		return GScopeLevel;
	}

	public void setGScopeLevel(int GScopeLevel) {
		this.GScopeLevel = GScopeLevel;
	}

	public int getParentId() {
		return ParentId;
	}

	public void setParentId(int ParentId) {
		this.ParentId = ParentId;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int i) {
		dest.writeInt(this.GScopeId);
		dest.writeInt(this.GScopeLevel);
		dest.writeString(this.GScopeCnName);
		dest.writeDouble(this.lng);
		dest.writeDouble(this.lat);
	}

	protected DistrictBean(Parcel in) {
		this.GScopeId = in.readInt();
		this.GScopeLevel = in.readInt();
		this.GScopeCnName = in.readString();
		this.lng = in.readDouble();
		this.lat = in.readDouble();
	}

	public static final Creator<DistrictBean> CREATOR = new Creator<DistrictBean>() {
		public DistrictBean createFromParcel(Parcel source) {
			return new DistrictBean(source);
		}

		public DistrictBean[] newArray(int size) {
			return new DistrictBean[size];
		}
	};
}