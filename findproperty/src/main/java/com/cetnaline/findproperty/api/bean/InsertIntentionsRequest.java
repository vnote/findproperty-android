package com.cetnaline.findproperty.api.bean;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/28.
 */
public class InsertIntentionsRequest {

	private String UserId;

	private String CityCode = "021";

	private String Source;

	private String AppName = "APP";

	private String SearchModel;

	private String SearchModelName;

	private long PostTotalNum;

	private List<SearchParam> SearchPara;

	public long getPostTotalNum() {
		return PostTotalNum;
	}

	public void setPostTotalNum(long postTotalNum) {
		PostTotalNum = postTotalNum;
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

	public String getSearchModel() {
		return SearchModel;
	}

	public void setSearchModel(String searchModel) {
		SearchModel = searchModel;
	}

	public List<SearchParam> getSearchPara() {
		return SearchPara;
	}

	public void setSearchPara(List<SearchParam> searchPara) {
		SearchPara = searchPara;
	}

	public String getSearchModelName() {
		return SearchModelName;
	}

	public void setSearchModelName(String searchModelName) {
		SearchModelName = searchModelName;
	}
}
