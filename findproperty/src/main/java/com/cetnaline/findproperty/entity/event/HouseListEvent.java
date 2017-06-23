package com.cetnaline.findproperty.entity.event;

import com.cetnaline.findproperty.api.bean.HouseBo;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class HouseListEvent {

	private List<HouseBo> houseBos;

	public HouseListEvent(List<HouseBo> houseBos) {
		this.houseBos = houseBos;
	}

	public List<HouseBo> getHouseBos() {
		return houseBos;
	}

	public void setHouseBos(List<HouseBo> houseBos) {
		this.houseBos = houseBos;
	}
}
