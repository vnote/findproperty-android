package com.cetnaline.findproperty.api.bean;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/10.
 */
public class SchoolBo {


	/**
	 * SchoolId : 865
	 * SchoolName : 君莲学校
	 * GScopeId : 216512
	 * GScopeName : 东颛桥
	 * RegionId : 2165
	 * RegionName : 闵行
	 * SchoolType : 小学
	 * SchoolGrade : 普通
	 * SchoolFeature :
	 * SchoolShortName : 君莲学校
	 * SaleNumber : 115
	 * RentNumber : 0
	 * MaxSalePrice : 4800000
	 * MinSalePrice : 2100000
	 * MaxUnitSalePrice : 37063.8859729951
	 * MinUnitSalePrice : 37063.8859729951
	 * MaxRentPrice : 0
	 * MinRentPrice : 0
	 * Address : 沪光路120号
	 * Lng : 121.425428
	 * Lat : 31.086754
	 * Phone : 021-34970520
	 * Remark :
	 * ShoolImgUrl : http://img.sh.centanet.com/shanghai/staticfile/schoolimg/865/0.jpg
	 * AvgPrice : 0
	 * SchoolToEstateInfo : {"EstateTotal":0,"SchoolId":0,"Estates":[]}
	 */

	private int SchoolId;
	private String SchoolName;
	private int GScopeId;
	private String GScopeName;
	private int RegionId;
	private String RegionName;
	private String SchoolType;
	private String SchoolGrade;
	private String SchoolFeature;
	private String SchoolShortName;
	private int SaleNumber;
	private int RentNumber;
	private double MaxSalePrice;
	private double MinSalePrice;
	private double MaxUnitSalePrice;
	private double MinUnitSalePrice;
	private double MaxRentPrice;
	private double MinRentPrice;
	private String Address;
	private double Lng;
	private double Lat;
	private String Phone;
	private String Remark;
	private String ShoolImgUrl;
	private int AvgPrice;
	/**
	 * EstateTotal : 0
	 * SchoolId : 0
	 * Estates : []
	 */

	private SchoolToEstateInfoBean SchoolToEstateInfo;

	public int getSchoolId() {
		return SchoolId;
	}

	public void setSchoolId(int SchoolId) {
		this.SchoolId = SchoolId;
	}

	public String getSchoolName() {
		return SchoolName;
	}

	public void setSchoolName(String SchoolName) {
		this.SchoolName = SchoolName;
	}

	public int getGScopeId() {
		return GScopeId;
	}

	public void setGScopeId(int GScopeId) {
		this.GScopeId = GScopeId;
	}

	public String getGScopeName() {
		return GScopeName;
	}

	public void setGScopeName(String GScopeName) {
		this.GScopeName = GScopeName;
	}

	public int getRegionId() {
		return RegionId;
	}

	public void setRegionId(int RegionId) {
		this.RegionId = RegionId;
	}

	public String getRegionName() {
		return RegionName;
	}

	public void setRegionName(String RegionName) {
		this.RegionName = RegionName;
	}

	public String getSchoolType() {
		return SchoolType;
	}

	public void setSchoolType(String SchoolType) {
		this.SchoolType = SchoolType;
	}

	public String getSchoolGrade() {
		return SchoolGrade;
	}

	public void setSchoolGrade(String SchoolGrade) {
		this.SchoolGrade = SchoolGrade;
	}

	public String getSchoolFeature() {
		return SchoolFeature;
	}

	public void setSchoolFeature(String SchoolFeature) {
		this.SchoolFeature = SchoolFeature;
	}

	public String getSchoolShortName() {
		return SchoolShortName;
	}

	public void setSchoolShortName(String SchoolShortName) {
		this.SchoolShortName = SchoolShortName;
	}

	public int getSaleNumber() {
		return SaleNumber;
	}

	public void setSaleNumber(int SaleNumber) {
		this.SaleNumber = SaleNumber;
	}

	public int getRentNumber() {
		return RentNumber;
	}

	public void setRentNumber(int RentNumber) {
		this.RentNumber = RentNumber;
	}

	public double getMaxSalePrice() {
		return MaxSalePrice;
	}

	public void setMaxSalePrice(double MaxSalePrice) {
		this.MaxSalePrice = MaxSalePrice;
	}

	public double getMinSalePrice() {
		return MinSalePrice;
	}

	public void setMinSalePrice(double MinSalePrice) {
		this.MinSalePrice = MinSalePrice;
	}

	public double getMaxUnitSalePrice() {
		return MaxUnitSalePrice;
	}

	public void setMaxUnitSalePrice(double MaxUnitSalePrice) {
		this.MaxUnitSalePrice = MaxUnitSalePrice;
	}

	public double getMinUnitSalePrice() {
		return MinUnitSalePrice;
	}

	public void setMinUnitSalePrice(double MinUnitSalePrice) {
		this.MinUnitSalePrice = MinUnitSalePrice;
	}

	public double getMaxRentPrice() {
		return MaxRentPrice;
	}

	public void setMaxRentPrice(double MaxRentPrice) {
		this.MaxRentPrice = MaxRentPrice;
	}

	public double getMinRentPrice() {
		return MinRentPrice;
	}

	public void setMinRentPrice(double MinRentPrice) {
		this.MinRentPrice = MinRentPrice;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String Address) {
		this.Address = Address;
	}

	public double getLng() {
		return Lng;
	}

	public void setLng(double Lng) {
		this.Lng = Lng;
	}

	public double getLat() {
		return Lat;
	}

	public void setLat(double Lat) {
		this.Lat = Lat;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String Phone) {
		this.Phone = Phone;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String Remark) {
		this.Remark = Remark;
	}

	public String getShoolImgUrl() {
		return ShoolImgUrl;
	}

	public void setShoolImgUrl(String ShoolImgUrl) {
		this.ShoolImgUrl = ShoolImgUrl;
	}

	public int getAvgPrice() {
		return AvgPrice;
	}

	public void setAvgPrice(int AvgPrice) {
		this.AvgPrice = AvgPrice;
	}

	public SchoolToEstateInfoBean getSchoolToEstateInfo() {
		return SchoolToEstateInfo;
	}

	public void setSchoolToEstateInfo(SchoolToEstateInfoBean SchoolToEstateInfo) {
		this.SchoolToEstateInfo = SchoolToEstateInfo;
	}

	public static class SchoolToEstateInfoBean {
		private int EstateTotal;
		private int SchoolId;
		private List<EstateBo> Estates;

		public int getEstateTotal() {
			return EstateTotal;
		}

		public void setEstateTotal(int EstateTotal) {
			this.EstateTotal = EstateTotal;
		}

		public int getSchoolId() {
			return SchoolId;
		}

		public void setSchoolId(int SchoolId) {
			this.SchoolId = SchoolId;
		}

		public List<EstateBo> getEstates() {
			return Estates;
		}

		public void setEstates(List<EstateBo> Estates) {
			this.Estates = Estates;
		}
	}
}
