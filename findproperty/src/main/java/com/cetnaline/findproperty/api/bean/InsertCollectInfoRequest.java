package com.cetnaline.findproperty.api.bean;

/**
 * 收藏的对象
 * Created by fanxl2 on 2016/8/22.
 */
public class InsertCollectInfoRequest {

	private String CollectValue;
	private String UserId;
	private String CityCode = "021";
	private String Source;
	private String AppName = "APP";
	private String CollectUrl;

	public String getCollectValue() {
		return CollectValue;
	}

	public void setCollectValue(String collectValue) {
		CollectValue = collectValue;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getCityCode() {
		return CityCode;
	}

	public void setCityCode(String cityCode) {
		CityCode = cityCode;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public String getAppName() {
		return AppName;
	}

	public void setAppName(String appName) {
		AppName = appName;
	}

	public String getCollectUrl() {
		return CollectUrl;
	}

	public void setCollectUrl(String collectUrl) {
		CollectUrl = collectUrl;
	}
}
