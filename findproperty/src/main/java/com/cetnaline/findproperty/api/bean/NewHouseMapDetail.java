package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanxl2 on 2016/8/10.
 */
public class NewHouseMapDetail implements Parcelable {


	private String EstExtId;
	private String AdName;
	private String EstType;
	private int DistrictId;
	private int GScopeId;
	private int AveragePrice;
	private String IconUrl;
	private DistrictBean District;

	private DistrictBean GScope;
	private boolean IsTop;
	private String Address;
	private double lng;
	private double lat;
	private boolean HasBookingActivity;

	public String getEstExtId() {
		return EstExtId;
	}

	public void setEstExtId(String EstExtId) {
		this.EstExtId = EstExtId;
	}

	public String getAdName() {
		return AdName;
	}

	public void setAdName(String AdName) {
		this.AdName = AdName;
	}

	public String getEstType() {
		return EstType;
	}

	public void setEstType(String EstType) {
		this.EstType = EstType;
	}

	public int getDistrictId() {
		return DistrictId;
	}

	public void setDistrictId(int DistrictId) {
		this.DistrictId = DistrictId;
	}

	public int getGScopeId() {
		return GScopeId;
	}

	public void setGScopeId(int GScopeId) {
		this.GScopeId = GScopeId;
	}

	public int getAveragePrice() {
		return AveragePrice;
	}

	public void setAveragePrice(int AveragePrice) {
		this.AveragePrice = AveragePrice;
	}

	public String getIconUrl() {
		return IconUrl;
	}

	public void setIconUrl(String IconUrl) {
		this.IconUrl = IconUrl;
	}

	public DistrictBean getDistrict() {
		return District;
	}

	public void setDistrict(DistrictBean District) {
		this.District = District;
	}

	public DistrictBean getGScope() {
		return GScope;
	}

	public void setGScope(DistrictBean GScope) {
		this.GScope = GScope;
	}

	public boolean isIsTop() {
		return IsTop;
	}

	public void setIsTop(boolean IsTop) {
		this.IsTop = IsTop;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String Address) {
		this.Address = Address;
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

	public boolean isHasBookingActivity() {
		return HasBookingActivity;
	}

	public void setHasBookingActivity(boolean HasBookingActivity) {
		this.HasBookingActivity = HasBookingActivity;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int i) {
		dest.writeInt(this.GScopeId);
		dest.writeString(this.AdName);
		dest.writeDouble(this.lng);
		dest.writeDouble(this.lat);
		dest.writeParcelable(this.District, i);
		dest.writeParcelable(this.GScope, i);
	}


	protected NewHouseMapDetail(Parcel in) {
		this.GScopeId = in.readInt();
		this.AdName = in.readString();
		this.lng = in.readDouble();
		this.lat = in.readDouble();
		this.District = in.readParcelable(DistrictBean.class.getClassLoader());
		this.GScope = in.readParcelable(DistrictBean.class.getClassLoader());
	}

	public static final Creator<NewHouseMapDetail> CREATOR = new Creator<NewHouseMapDetail>() {
		public NewHouseMapDetail createFromParcel(Parcel source) {
			return new NewHouseMapDetail(source);
		}

		public NewHouseMapDetail[] newArray(int size) {
			return new NewHouseMapDetail[size];
		}
	};
}
