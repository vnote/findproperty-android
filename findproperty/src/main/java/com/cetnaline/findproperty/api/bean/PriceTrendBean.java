package com.cetnaline.findproperty.api.bean;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/31.
 */
public class PriceTrendBean {

	private List<GscopeDealPriceBo> gscopeDealPriceBos;

	private List<EstateDealPriceBo> estateDealPriceBos;

	private List<GscopeDealPriceBo> cityDealPriceBos;

	public PriceTrendBean(List<GscopeDealPriceBo> gscopeDealPriceBos, List<EstateDealPriceBo> estateDealPriceBos, List<GscopeDealPriceBo> cityDealPriceBos) {
		this.gscopeDealPriceBos = gscopeDealPriceBos;
		this.estateDealPriceBos = estateDealPriceBos;
		this.cityDealPriceBos = cityDealPriceBos;
	}

	public List<GscopeDealPriceBo> getGscopeDealPriceBos() {
		return gscopeDealPriceBos;
	}

	public void setGscopeDealPriceBos(List<GscopeDealPriceBo> gscopeDealPriceBos) {
		this.gscopeDealPriceBos = gscopeDealPriceBos;
	}

	public List<EstateDealPriceBo> getEstateDealPriceBos() {
		return estateDealPriceBos;
	}

	public void setEstateDealPriceBos(List<EstateDealPriceBo> estateDealPriceBos) {
		this.estateDealPriceBos = estateDealPriceBos;
	}

	public List<GscopeDealPriceBo> getCityDealPriceBos() {
		return cityDealPriceBos;
	}

	public void setCityDealPriceBos(List<GscopeDealPriceBo> cityDealPriceBos) {
		this.cityDealPriceBos = cityDealPriceBos;
	}
}
