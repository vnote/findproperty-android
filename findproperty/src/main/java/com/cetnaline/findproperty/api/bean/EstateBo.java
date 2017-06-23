package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class EstateBo implements Parcelable {


	/**
	 * EstateCode : PEDPWPWEAS
	 * EstateName : 远中风华园
	 * EstateAlias : 远中风华园
	 * FullPY : Yuan Zhong Feng Hua Yuan
	 * FirstPY : YZFHY
	 * Address : 新闸路1068弄
	 * RegionId : 2168
	 * RegionName : 静安
	 * GscopeId : 216805
	 * GscopeName : 新闸
	 * OpDate : 1167580800
	 * PropertyType : 公寓
	 * Developers : 上海远中静安房地产有限公司
	 * Lng : 121.462747
	 * Lat : 31.239735
	 * ShowInWeb : true
	 * SaleAvgPrice : 104072.398190045
	 * SaleAvgPriceRise : 2.23
	 * RentAvgPrice : 21000
	 * DealAvgPrice : 67395.14
	 * MaxSalePrice : 35000000
	 * MinSalePrice : 7000000
	 * MaxRentPrice : 30000
	 * MinRentPrice : 16000
	 * SaleNumber : 26
	 * RentNumber : 9
	 * DealNumber : 13
	 * Score : 0.2
	 * IsRailWay : true
	 * IsSchool : true
	 * EstateType : normal
	 * ImagePath : ed/12/8414d9184a37b586501fb468cc0c
	 * ImageDestExt : .jpg
	 * Distance : 0
	 * HSaleNumber : 0
	 * HRentNumber : 0
	 * PaNo : 10021013120613134045200
	 * IsVideo : false
	 * RailwayInfos : []
	 * EstateId : 07082111573855A2FA59B56F90388D35
	 * ImageFullPath : http://img.sh.centanet.com/ctpostimage/ed/12/8414d9184a37b586501fb468cc0c_10x10_c.jpg
	 * EstateSimilarCount : 0
	 */

	private String EstateCode;
	private String EstateName;
	private String EstateAlias;
	private String FullPY;
	private String FirstPY;
	private String Address;
	private int RegionId;
	private String RegionName;
	private int GscopeId;
	private String GscopeName;
	private long OpDate;
	private String PropertyType;
	private String Developers;
	private double Lng;
	private double Lat;
	private boolean ShowInWeb;
	private double SaleAvgPrice;
	private double SaleAvgPriceRise;
	private double RentAvgPrice;
	private double DealAvgPrice;
	private double MaxSalePrice;
	private double MinSalePrice;
	private double MaxRentPrice;
	private double MinRentPrice;
	private int SaleNumber;
	private int RentNumber;
	private int DealNumber;
	private double Score;
	private boolean IsRailWay;
	private boolean IsSchool;
	private String EstateType;
	private String ImagePath;
	private String ImageDestExt;
	private int Distance;
	private int HSaleNumber;
	private int HRentNumber;
	private String PaNo;
	private boolean IsVideo;
	private String EstateId;
	private String ImageFullPath;
	private int EstateSimilarCount;
	private List<RailwayInfoBo> RailwayInfos;
	private String SchoolNames;
	private String EstateVideoUrl;
	private String TencentVistaUrl;
	private EstateInfoBean EstateInfo;
	private List<EstateImage> EstateImages;

	private String EstateID;  //有的接口返回字段名字不同（GetEntrustEstateRequest）
	private String EstateAddress; //同上
	private String CestCode;  //这个也是小区code

	public String getCestCode() {
		return CestCode;
	}

	public void setCestCode(String cestCode) {
		CestCode = cestCode;
	}

	public String getEstateID() {
		return EstateID;
	}

	public void setEstateID(String estateID) {
		EstateID = estateID;
	}

	public String getEstateAddress() {
		return EstateAddress;
	}

	public void setEstateAddress(String estateAddress) {
		EstateAddress = estateAddress;
	}

	public String getSchoolNames() {
		return SchoolNames;
	}

	public void setSchoolNames(String schoolNames) {
		SchoolNames = schoolNames;
	}

	public String getEstateVideoUrl() {
		return EstateVideoUrl;
	}

	public void setEstateVideoUrl(String estateVideoUrl) {
		EstateVideoUrl = estateVideoUrl;
	}

	public String getTencentVistaUrl() {
		return TencentVistaUrl;
	}

	public void setTencentVistaUrl(String tencentVistaUrl) {
		TencentVistaUrl = tencentVistaUrl;
	}

	public EstateInfoBean getEstateInfo() {
		return EstateInfo;
	}

	public void setEstateInfo(EstateInfoBean estateInfo) {
		EstateInfo = estateInfo;
	}

	public List<EstateImage> getEstateImages() {
		return EstateImages;
	}

	public void setEstateImages(List<EstateImage> estateImages) {
		EstateImages = estateImages;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int i) {
		dest.writeString(this.EstateCode);
		dest.writeString(this.EstateName);
		dest.writeString(this.EstateAlias);
		dest.writeInt(this.RegionId);
		dest.writeInt(this.GscopeId);
		dest.writeInt(this.SaleNumber);
		dest.writeInt(this.RentNumber);
		dest.writeString(this.Address);
		dest.writeDouble(this.Lng);
		dest.writeDouble(this.Lat);
		dest.writeString(this.ImagePath);
	}


	protected EstateBo(Parcel in) {
		this.EstateCode = in.readString();
		this.EstateName = in.readString();
		this.EstateAlias = in.readString();
		this.RegionId = in.readInt();
		this.GscopeId = in.readInt();
		this.SaleNumber = in.readInt();
		this.RentNumber = in.readInt();
		this.Address = in.readString();
		this.Lng = in.readDouble();
		this.Lat = in.readDouble();
		this.ImagePath = in.readString();
	}

	public static final Creator<EstateBo> CREATOR = new Creator<EstateBo>() {
		@Override
		public EstateBo createFromParcel(Parcel source) {
			return new EstateBo(source);
		}

		@Override
		public EstateBo[] newArray(int size) {
			return new EstateBo[size];
		}
	};

	public String getEstateCode() {
		return EstateCode;
	}

	public void setEstateCode(String EstateCode) {
		this.EstateCode = EstateCode;
	}

	public String getEstateName() {
		return EstateName;
	}

	public void setEstateName(String EstateName) {
		this.EstateName = EstateName;
	}

	public String getEstateAlias() {
		return EstateAlias;
	}

	public void setEstateAlias(String EstateAlias) {
		this.EstateAlias = EstateAlias;
	}

	public String getFullPY() {
		return FullPY;
	}

	public void setFullPY(String FullPY) {
		this.FullPY = FullPY;
	}

	public String getFirstPY() {
		return FirstPY;
	}

	public void setFirstPY(String FirstPY) {
		this.FirstPY = FirstPY;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String Address) {
		this.Address = Address;
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

	public int getGscopeId() {
		return GscopeId;
	}

	public void setGscopeId(int GscopeId) {
		this.GscopeId = GscopeId;
	}

	public String getGscopeName() {
		return GscopeName;
	}

	public void setGscopeName(String GscopeName) {
		this.GscopeName = GscopeName;
	}


	public String getPropertyType() {
		return PropertyType;
	}

	public void setPropertyType(String PropertyType) {
		this.PropertyType = PropertyType;
	}

	public String getDevelopers() {
		return Developers;
	}

	public void setDevelopers(String Developers) {
		this.Developers = Developers;
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

	public boolean isShowInWeb() {
		return ShowInWeb;
	}

	public void setShowInWeb(boolean ShowInWeb) {
		this.ShowInWeb = ShowInWeb;
	}

	public double getSaleAvgPrice() {
		return SaleAvgPrice;
	}

	public void setSaleAvgPrice(double SaleAvgPrice) {
		this.SaleAvgPrice = SaleAvgPrice;
	}

	public double getSaleAvgPriceRise() {
		return SaleAvgPriceRise;
	}

	public void setSaleAvgPriceRise(double SaleAvgPriceRise) {
		this.SaleAvgPriceRise = SaleAvgPriceRise;
	}

	public double getDealAvgPrice() {
		return DealAvgPrice;
	}

	public void setDealAvgPrice(double DealAvgPrice) {
		this.DealAvgPrice = DealAvgPrice;
	}

	public double getMaxSalePrice() {
		return MaxSalePrice;
	}

	public void setMaxSalePrice(double maxSalePrice) {
		MaxSalePrice = maxSalePrice;
	}

	public double getMinSalePrice() {
		return MinSalePrice;
	}

	public void setMinSalePrice(double minSalePrice) {
		MinSalePrice = minSalePrice;
	}

	public double getMaxRentPrice() {
		return MaxRentPrice;
	}

	public void setMaxRentPrice(double maxRentPrice) {
		MaxRentPrice = maxRentPrice;
	}

	public double getMinRentPrice() {
		return MinRentPrice;
	}

	public void setMinRentPrice(double minRentPrice) {
		MinRentPrice = minRentPrice;
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

	public int getDealNumber() {
		return DealNumber;
	}

	public void setDealNumber(int DealNumber) {
		this.DealNumber = DealNumber;
	}

	public double getScore() {
		return Score;
	}

	public void setScore(double Score) {
		this.Score = Score;
	}

	public boolean isIsRailWay() {
		return IsRailWay;
	}

	public void setIsRailWay(boolean IsRailWay) {
		this.IsRailWay = IsRailWay;
	}

	public boolean isIsSchool() {
		return IsSchool;
	}

	public void setIsSchool(boolean IsSchool) {
		this.IsSchool = IsSchool;
	}

	public String getEstateType() {
		return EstateType;
	}

	public void setEstateType(String EstateType) {
		this.EstateType = EstateType;
	}

	public String getImagePath() {
		return ImagePath;
	}

	public void setImagePath(String ImagePath) {
		this.ImagePath = ImagePath;
	}

	public String getImageDestExt() {
		return ImageDestExt;
	}

	public void setImageDestExt(String ImageDestExt) {
		this.ImageDestExt = ImageDestExt;
	}

	public int getDistance() {
		return Distance;
	}

	public void setDistance(int Distance) {
		this.Distance = Distance;
	}

	public int getHSaleNumber() {
		return HSaleNumber;
	}

	public void setHSaleNumber(int HSaleNumber) {
		this.HSaleNumber = HSaleNumber;
	}

	public int getHRentNumber() {
		return HRentNumber;
	}

	public void setHRentNumber(int HRentNumber) {
		this.HRentNumber = HRentNumber;
	}

	public String getPaNo() {
		return PaNo;
	}

	public void setPaNo(String PaNo) {
		this.PaNo = PaNo;
	}

	public boolean isIsVideo() {
		return IsVideo;
	}

	public void setIsVideo(boolean IsVideo) {
		this.IsVideo = IsVideo;
	}

	public String getEstateId() {
		return EstateId;
	}

	public void setEstateId(String EstateId) {
		this.EstateId = EstateId;
	}

	public String getImageFullPath() {
		return ImageFullPath;
	}

	public void setImageFullPath(String ImageFullPath) {
		this.ImageFullPath = ImageFullPath;
	}

	public int getEstateSimilarCount() {
		return EstateSimilarCount;
	}

	public void setEstateSimilarCount(int EstateSimilarCount) {
		this.EstateSimilarCount = EstateSimilarCount;
	}

	public long getOpDate() {
		return OpDate;
	}

	public void setOpDate(long opDate) {
		OpDate = opDate;
	}

	public double getRentAvgPrice() {
		return RentAvgPrice;
	}

	public void setRentAvgPrice(double rentAvgPrice) {
		RentAvgPrice = rentAvgPrice;
	}

	public List<?> getRailwayInfos() {
		return RailwayInfos;
	}

	public void setRailwayInfos(List<RailwayInfoBo> RailwayInfos) {
		this.RailwayInfos = RailwayInfos;
	}

	public class EstateInfoBean{

		String Feature;
		String Description;
		String Park;
		float GreenRatio;
		float FloorRatio;
		int HouseNumber;
		String PropertyCompany;
		String PropertyCharges;
		String Heading;
		String Pitch;

		public String getFeature() {
			return Feature;
		}

		public void setFeature(String feature) {
			Feature = feature;
		}

		public String getDescription() {
			return Description;
		}

		public void setDescription(String description) {
			Description = description;
		}

		public String getPark() {
			return Park;
		}

		public void setPark(String park) {
			Park = park;
		}

		public float getGreenRatio() {
			return GreenRatio;
		}

		public void setGreenRatio(float greenRatio) {
			GreenRatio = greenRatio;
		}

		public float getFloorRatio() {
			return FloorRatio;
		}

		public void setFloorRatio(float floorRatio) {
			FloorRatio = floorRatio;
		}

		public int getHouseNumber() {
			return HouseNumber;
		}

		public void setHouseNumber(int houseNumber) {
			HouseNumber = houseNumber;
		}

		public String getPropertyCompany() {
			return PropertyCompany;
		}

		public void setPropertyCompany(String propertyCompany) {
			PropertyCompany = propertyCompany;
		}

		public String getPropertyCharges() {
			return PropertyCharges;
		}

		public void setPropertyCharges(String propertyCharges) {
			PropertyCharges = propertyCharges;
		}

		public String getHeading() {
			return Heading;
		}

		public void setHeading(String heading) {
			Heading = heading;
		}

		public String getPitch() {
			return Pitch;
		}

		public void setPitch(String pitch) {
			Pitch = pitch;
		}
	}
}
