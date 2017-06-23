package com.cetnaline.findproperty.api.bean;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/22.
 */
public class BatchCollectRequest {

	private String UserId;

	private List<InsertCollectInfoRequest> Batch;

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public List<InsertCollectInfoRequest> getBatch() {
		return Batch;
	}

	public void setBatch(List<InsertCollectInfoRequest> batch) {
		Batch = batch;
	}
}
