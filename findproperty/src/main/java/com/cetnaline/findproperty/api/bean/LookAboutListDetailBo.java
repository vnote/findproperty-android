package com.cetnaline.findproperty.api.bean;

import com.cetnaline.findproperty.entity.bean.StaffListBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fanxl2 on 2016/8/16.
 */
public class LookAboutListDetailBo implements Serializable {


	/**
	 * PostID : 3b41b6c2-190b-4040-94b0-35791cbc4d92
	 * EstateName : 尚海湾豪庭 一期
	 * EstateCode : PESEWKWESS
	 * RoomCount : 3
	 * HallCount : 2
	 * Direction : 南
	 * PostType : R
	 * SalePrice : 0
	 * RentPrice : 22000
	 * Title : 尚海湾豪庭 高区 豪华装修带地暖 有钥匙
	 * GArea : 170
	 * DefaultImage : ae/54/0d6ea0634a60b0b80baac030dc86
	 * IsOnline : True
	 * StaffNo :
	 * Status : 0
	 * LookTime :
	 * LookDoneTime :
	 */

	private String ListID;
	private String PostID;
	private String EstateName;
	private String EstateCode;
	private int RoomCount;
	private int HallCount;
	private String Direction;
	private String PostType;
	private double SalePrice;
	private double RentPrice;
	private String Title;
	private double GArea;
	private String DefaultImage;
	private boolean IsOnline;
	private String StaffNo;
	private int Status;
	private String PlanID;
	private String LookTime;
	private String PlanCode;

	public String getPlanCode() {
		return PlanCode;
	}

	public void setPlanCode(String planCode) {
		PlanCode = planCode;
	}

	public String getListID() {
		return ListID;
	}

	public void setListID(String listID) {
		ListID = listID;
	}

	public String getPlanID() {
		return PlanID;
	}

	public void setPlanID(String planID) {
		PlanID = planID;
	}

	private int selectedP;

	public int getSelectedP() {
		return selectedP;
	}

	public void setSelectedP(int selectedP) {
		this.selectedP = selectedP;
	}

	private List<StaffComment> staffs;

	public List<StaffComment> getStaffs() {
		return staffs;
	}

	public void setStaffs(List<StaffComment> staffs) {
		this.staffs = staffs;
	}

	public String getPostID() {
		return PostID;
	}

	public void setPostID(String PostID) {
		this.PostID = PostID;
	}

	public String getEstateName() {
		return EstateName;
	}

	public void setEstateName(String EstateName) {
		this.EstateName = EstateName;
	}

	public String getEstateCode() {
		return EstateCode;
	}

	public void setEstateCode(String EstateCode) {
		this.EstateCode = EstateCode;
	}

	public int getRoomCount() {
		return RoomCount;
	}

	public void setRoomCount(int RoomCount) {
		this.RoomCount = RoomCount;
	}

	public int getHallCount() {
		return HallCount;
	}

	public void setHallCount(int HallCount) {
		this.HallCount = HallCount;
	}

	public String getDirection() {
		return Direction;
	}

	public void setDirection(String Direction) {
		this.Direction = Direction;
	}

	public String getPostType() {
		return PostType;
	}

	public void setPostType(String PostType) {
		this.PostType = PostType;
	}

	public double getSalePrice() {
		return SalePrice;
	}

	public void setSalePrice(double SalePrice) {
		this.SalePrice = SalePrice;
	}

	public double getRentPrice() {
		return RentPrice;
	}

	public void setRentPrice(double RentPrice) {
		this.RentPrice = RentPrice;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String Title) {
		this.Title = Title;
	}

	public double getGArea() {
		return GArea;
	}

	public void setGArea(double GArea) {
		this.GArea = GArea;
	}

	public String getDefaultImage() {
		return DefaultImage;
	}

	public void setDefaultImage(String DefaultImage) {
		this.DefaultImage = DefaultImage;
	}

	public boolean isIsOnline() {
		return IsOnline;
	}

	public void setIsOnline(boolean IsOnline) {
		this.IsOnline = IsOnline;
	}

	public String getStaffNo() {
		return StaffNo;
	}

	public void setStaffNo(String StaffNo) {
		this.StaffNo = StaffNo;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int Status) {
		this.Status = Status;
	}

	public String getLookTime() {
		return LookTime;
	}

	public void setLookTime(String LookTime) {
		this.LookTime = LookTime;
	}

	private StaffListBean staffDetail;

	public StaffListBean getStaffDetail() {
		return staffDetail;
	}

	public void setStaffDetail(StaffListBean staffDetail) {
		this.staffDetail = staffDetail;
	}
}
