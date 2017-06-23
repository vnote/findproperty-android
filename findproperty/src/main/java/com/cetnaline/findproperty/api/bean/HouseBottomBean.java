package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/8/15.
 */
public class HouseBottomBean {

	private String postId;

	private String estExtId;

	private String title;

	private int houseType;

	public HouseBottomBean(int houseType) {
		this.houseType = houseType;
	}

	public int getHouseType() {
		return houseType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setHouseType(int houseType) {
		this.houseType = houseType;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getEstExtId() {
		return estExtId;
	}

	public void setEstExtId(String estExtId) {
		this.estExtId = estExtId;
	}
}
