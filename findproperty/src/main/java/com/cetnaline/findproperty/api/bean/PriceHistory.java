package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/8/12.
 */
public class PriceHistory {

	/**
	 * EstateCode : PEWSWYWAPS
	 * DealAvgPrice : 90
	 * PostType : R
	 * RoomCount : -1
	 * DataYear : 2014
	 * DataMonth : 8
	 */

	private String EstateCode;
	private double DealAvgPrice;
	private String PostType;
	private int RoomCount;
	private int DataYear;
	private int DataMonth;

	public String getEstateCode() {
		return EstateCode;
	}

	public void setEstateCode(String EstateCode) {
		this.EstateCode = EstateCode;
	}

	public double getDealAvgPrice() {
		return DealAvgPrice;
	}

	public void setDealAvgPrice(double DealAvgPrice) {
		this.DealAvgPrice = DealAvgPrice;
	}

	public String getPostType() {
		return PostType;
	}

	public void setPostType(String PostType) {
		this.PostType = PostType;
	}

	public int getRoomCount() {
		return RoomCount;
	}

	public void setRoomCount(int RoomCount) {
		this.RoomCount = RoomCount;
	}

	public int getDataYear() {
		return DataYear;
	}

	public void setDataYear(int DataYear) {
		this.DataYear = DataYear;
	}

	public int getDataMonth() {
		return DataMonth;
	}

	public void setDataMonth(int DataMonth) {
		this.DataMonth = DataMonth;
	}
}
