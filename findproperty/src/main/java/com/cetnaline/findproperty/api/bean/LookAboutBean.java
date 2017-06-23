package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/10/13.
 */

public class LookAboutBean {


	/**
	 * ListID :
	 * PostID : 908625c9-8f57-44ef-9996-cb87afe28892
	 * PlanCode : 161013041720260
	 * StaffNo : AA74045
	 * Status : 1
	 * PlanID : 6DDC55EB-CBC6-430E-882D-9B56D8E708C7
	 * LookTime :
	 */

	private String ListID;
	private String PostID;
	private String PlanCode;
	private String LookStaff;
	private int Status;
	private String PlanID;
	private String LookTime;

	public String getListID() {
		return ListID;
	}

	public void setListID(String ListID) {
		this.ListID = ListID;
	}

	public String getPostID() {
		return PostID;
	}

	public void setPostID(String PostID) {
		this.PostID = PostID;
	}

	public String getPlanCode() {
		return PlanCode;
	}

	public void setPlanCode(String PlanCode) {
		this.PlanCode = PlanCode;
	}

	public String getLookStaff() {
		return LookStaff;
	}

	public void setLookStaff(String lookStaff) {
		LookStaff = lookStaff;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int Status) {
		this.Status = Status;
	}

	public String getPlanID() {
		return PlanID;
	}

	public void setPlanID(String PlanID) {
		this.PlanID = PlanID;
	}

	public String getLookTime() {
		return LookTime;
	}

	public void setLookTime(String LookTime) {
		this.LookTime = LookTime;
	}
}
