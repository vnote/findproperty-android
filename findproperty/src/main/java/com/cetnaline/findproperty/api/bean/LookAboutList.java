package com.cetnaline.findproperty.api.bean;

import com.cetnaline.findproperty.entity.bean.StaffListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanxl2 on 2016/8/16.
 */
public class LookAboutList {

	private String estateName;
	private String estateCode;
	private String LookStaff;
	private StaffListBean staffDetail;
	private String PlanID;
	private String LookTime;
	private String PlanCode;
	private int status;

	public String getLookTime() {
		return LookTime;
	}

	public void setLookTime(String lookTime) {
		LookTime = lookTime;
	}

	public String getPlanID() {
		return PlanID;
	}

	public void setPlanID(String planID) {
		PlanID = planID;
	}

	public String getPlanCode() {
		return PlanCode;
	}

	public void setPlanCode(String planCode) {
		PlanCode = planCode;
	}

	private List<LookAboutListDetailBo> list;


	public String getEstateName() {
		return estateName;
	}

	public void setEstateName(String estateName) {
		this.estateName = estateName;
	}

	public List<LookAboutListDetailBo> getList() {
		if (list==null){
			list = new ArrayList<>();
		}
		return list;
	}

	public void setList(List<LookAboutListDetailBo> list) {
		this.list = list;
	}

	public String getEstateCode() {
		return estateCode;
	}

	public void setEstateCode(String estateCode) {
		this.estateCode = estateCode;
	}

	public StaffListBean getStaffDetail() {
		return staffDetail;
	}

	public void setStaffDetail(StaffListBean staffDetail) {
		this.staffDetail = staffDetail;
	}

	public String getLookStaff() {
		return LookStaff;
	}

	public void setLookStaff(String lookStaff) {
		LookStaff = lookStaff;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
