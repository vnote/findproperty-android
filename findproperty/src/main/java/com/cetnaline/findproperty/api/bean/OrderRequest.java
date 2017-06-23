package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/8/22.
 */
public class OrderRequest {

	private int OrderType = 1;

	private String NickName;

	private String Mobile;

	public OrderRequest(String nickName, String mobile) {
		NickName = nickName;
		Mobile = mobile;
	}

	public int getOrderType() {
		return OrderType;
	}

	public void setOrderType(int orderType) {
		OrderType = orderType;
	}

	public String getNickName() {
		return NickName;
	}

	public void setNickName(String nickName) {
		NickName = nickName;
	}

	public String getMobile() {
		return Mobile;
	}

	public void setMobile(String mobile) {
		Mobile = mobile;
	}

}
