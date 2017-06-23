package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/10.
 */
public class NewHousePropBo implements Parcelable {

	private int DistrictId;
	private int NewPropCount;

	private DistrictBean District;

	private List<NewHousePropBo> GscopeEsts;

	public int getDistrictId() {
		return DistrictId;
	}

	public void setDistrictId(int DistrictId) {
		this.DistrictId = DistrictId;
	}

	public int getNewPropCount() {
		return NewPropCount;
	}

	public void setNewPropCount(int NewPropCount) {
		this.NewPropCount = NewPropCount;
	}

	public DistrictBean getDistrict() {
		return District;
	}

	public void setDistrict(DistrictBean District) {
		this.District = District;
	}

	public List<NewHousePropBo> getGscopeEsts() {
		return GscopeEsts;
	}

	public void setGscopeEsts(List<NewHousePropBo> gscopeEsts) {
		GscopeEsts = gscopeEsts;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int i) {
		dest.writeInt(this.NewPropCount);
		dest.writeInt(this.DistrictId);
		dest.writeParcelable(this.District, i);
	}

	protected NewHousePropBo(Parcel in) {
		this.NewPropCount = in.readInt();
		this.DistrictId = in.readInt();
		this.District = in.readParcelable(DistrictBean.class.getClassLoader());
	}

	public static final Creator<NewHousePropBo> CREATOR = new Creator<NewHousePropBo>() {
		public NewHousePropBo createFromParcel(Parcel source) {
			return new NewHousePropBo(source);
		}

		public NewHousePropBo[] newArray(int size) {
			return new NewHousePropBo[size];
		}
	};


}
