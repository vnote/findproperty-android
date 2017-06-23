package com.cetnaline.findproperty.api.bean;

import java.util.List;

/**
 * Created by fanxl2 on 2016/9/9.
 */
public class LookListDeleteRequest {

	private String userId;

	private List<String> looklistids;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getLooklistids() {
		return looklistids;
	}

	public void setLooklistids(List<String> looklistids) {
		this.looklistids = looklistids;
	}
}
