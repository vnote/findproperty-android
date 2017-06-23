package com.cetnaline.findproperty.entity.bean;

import java.util.List;

/**
 * Created by diaoqf on 2016/8/8.
 */
public class GScopeBean extends BaseBean {

	public int GScopeId;
	public String GScopeCode;
	public int GScopeLevel;
	public String GScopeName;
	public String GScopeAlias;
	public String FullPY;
	public String FirstPY;
	public int ParentId;
	public int OrderBy;
	public double Lng;
	public Double Lat;
	public int PostCount;
	public List<GScopeBean> GScopeList;

	public int getGScopeId() {
		return GScopeId;
	}

	public void setGScopeId(int GScopeId) {
		this.GScopeId = GScopeId;
	}

	public String getGScopeCode() {
		return GScopeCode;
	}

	public void setGScopeCode(String GScopeCode) {
		this.GScopeCode = GScopeCode;
	}

	public int getGScopeLevel() {
		return GScopeLevel;
	}

	public void setGScopeLevel(int GScopeLevel) {
		this.GScopeLevel = GScopeLevel;
	}

	public String getGScopeName() {
		return GScopeName;
	}

	public void setGScopeName(String GScopeName) {
		this.GScopeName = GScopeName;
	}

	public String getGScopeAlias() {
		return GScopeAlias;
	}

	public void setGScopeAlias(String GScopeAlias) {
		this.GScopeAlias = GScopeAlias;
	}

	public String getFullPY() {
		return FullPY;
	}

	public void setFullPY(String fullPY) {
		FullPY = fullPY;
	}

	public String getFirstPY() {
		return FirstPY;
	}

	public void setFirstPY(String firstPY) {
		FirstPY = firstPY;
	}

	public int getParentId() {
		return ParentId;
	}

	public void setParentId(int parentId) {
		ParentId = parentId;
	}

	public int getOrderBy() {
		return OrderBy;
	}

	public void setOrderBy(int orderBy) {
		OrderBy = orderBy;
	}

	public double getLng() {
		return Lng;
	}

	public void setLng(double lng) {
		Lng = lng;
	}

	public Double getLat() {
		return Lat;
	}

	public void setLat(Double lat) {
		Lat = lat;
	}

	public int getPostCount() {
		return PostCount;
	}

	public void setPostCount(int postCount) {
		PostCount = postCount;
	}

	public List<GScopeBean> getGScopeList() {
		return GScopeList;
	}

	public void setGScopeList(List<GScopeBean> GScopeList) {
		this.GScopeList = GScopeList;
	}


}
