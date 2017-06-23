package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/8/10.
 */
public class RailwayInfoBo {

	/**
	 * RailWayID : 1
	 * RailLineId : 1
	 * Distance : 538.922089354101
	 * WalkTime : 0
	 * IsAdv : false
	 * RailWayName : 莘庄
	 * RailLineName : 1号线
	 */

	private int RailWayID;
	private int RailLineId;
	private double Distance;
	private int WalkTime;
	private boolean IsAdv;
	private String RailWayName;
	private String RailLineName;

	public int getRailWayID() {
		return RailWayID;
	}

	public void setRailWayID(int RailWayID) {
		this.RailWayID = RailWayID;
	}

	public int getRailLineId() {
		return RailLineId;
	}

	public void setRailLineId(int RailLineId) {
		this.RailLineId = RailLineId;
	}

	public double getDistance() {
		return Distance;
	}

	public void setDistance(double Distance) {
		this.Distance = Distance;
	}

	public int getWalkTime() {
		return WalkTime;
	}

	public void setWalkTime(int WalkTime) {
		this.WalkTime = WalkTime;
	}

	public boolean isIsAdv() {
		return IsAdv;
	}

	public void setIsAdv(boolean IsAdv) {
		this.IsAdv = IsAdv;
	}

	public String getRailWayName() {
		return RailWayName;
	}

	public void setRailWayName(String RailWayName) {
		this.RailWayName = RailWayName;
	}

	public String getRailLineName() {
		return RailLineName;
	}

	public void setRailLineName(String RailLineName) {
		this.RailLineName = RailLineName;
	}
}
