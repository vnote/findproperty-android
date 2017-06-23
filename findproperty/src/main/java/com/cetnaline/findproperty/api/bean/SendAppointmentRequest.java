package com.cetnaline.findproperty.api.bean;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/22.
 */
public class SendAppointmentRequest {

	private String UserId;

	private List<StaffNoRequest> StaffNoParam;

	private OrderRequest OrderParam;

	public SendAppointmentRequest(String userId, List<StaffNoRequest> staffNoParam, OrderRequest orderParam) {
		UserId = userId;
		StaffNoParam = staffNoParam;
		OrderParam = orderParam;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public List<StaffNoRequest> getStaffNoParam() {
		return StaffNoParam;
	}

	public void setStaffNoParam(List<StaffNoRequest> staffNoParam) {
		StaffNoParam = staffNoParam;
	}

	public OrderRequest getOrderParam() {
		return OrderParam;
	}

	public void setOrderParam(OrderRequest orderParam) {
		OrderParam = orderParam;
	}
}
