package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/8/22.
 */
public class StaffNoRequest {

	private String ListID;
	private String PostID;
	private String StaffNo;
	private String PostType;

	public String getListID() {
		return ListID;
	}

	public void setListID(String listID) {
		ListID = listID;
	}

	public String getPostType() {
		return PostType;
	}

	public void setPostType(String postType) {
		PostType = postType;
	}

	public String getPostID() {
		return PostID;
	}

	public void setPostID(String postID) {
		PostID = postID;
	}

	public String getStaffNo() {
		return StaffNo;
	}

	public void setStaffNo(String staffNo) {
		StaffNo = staffNo;
	}
}
