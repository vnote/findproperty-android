package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl on 2016/8/14 0014.
 */
public class GscopeDealPriceBo {

	/**
	 * GScopeId : 216516
	 * GScopeLevel : 3
	 * DealAvgPrice : 19866.09
	 * PostType : S
	 * RoomCount : -1
	 * DataYear : 2014
	 * DataMonth : 8
	 */

	private int GScopeId;
	private int GScopeLevel;
	private double DealAvgPrice;
	private String PostType;
	private int RoomCount;
	private int DataYear;
	private int DataMonth;

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
