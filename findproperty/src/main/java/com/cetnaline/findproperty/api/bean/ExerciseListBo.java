package com.cetnaline.findproperty.api.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fanxl2 on 2016/8/13.
 */
public class ExerciseListBo implements Serializable {


	/**
	 * ActId : 25
	 * EstId : 10157
	 * EstExtId : 10193
	 * ActTitle : 高尚领域盛大开盘！
	 * StartDate : 2016-05-26
	 * EndDate : 2016-05-27
	 * Description : 26日&27日：成功選購4房，优惠大追加
	 客戶
	 享20000元交通卡;
	 享30000元物业抵用券;
	 额外赠送20000元购物卡
	 * ActAddress : 宝华万豪酒店2楼
	 * ActType :
	 * BookCnt : 0
	 * ShowBookCnt : 3
	 * ActTags :
	 * IsOnline : true
	 * CreateTime : 2016-04-16 12:38:00
	 * AdName : 高尚领域-柏韦行政公馆11
	 * EstType : 酒店式公寓
	 * DistrictId : 2176
	 * District : {"GScopeId":2176,"GScopeCnName":"普陀区","FullPY":"putuoqu","FirstPY":"PTQ","GScopeLevel":2,"ParentId":21,"lng":121.404059,"lat":31.256775}
	 * GScopeId : 217606
	 * GScope : {"GScopeId":217606,"GScopeCnName":"真如","FullPY":"zhenru","FirstPY":"ZR","GScopeLevel":3,"ParentId":2176,"lng":121.410653,"lat":31.260866}
	 * lng : 121.413457
	 * lat : 31.258981
	 * ActImgs : [{"ImgId":11244,"FileNo":"a89220363aa31771442d476b9db865a6","ImgType":"","ImgTitle":"","ImgDescription":"","FileUrl":"20160416/a89220363aa31771442d476b9db865a6.png","ContentLength":840464,"Width":647,"Height":873}]
	 */

	private String ActId;
	private String EstId;
	private String EstExtId;
	private String ActTitle;
	private String StartDate;
	private String EndDate;
	private String Description;
	private String ActAddress;
	private String ActType;
	private int BookCnt;
	private int ShowBookCnt;
	private String ActTags;
	private boolean IsOnline;
	private String CreateTime;
	private String AdName;
	private String EstType;
	private int DistrictId;
	/**
	 * GScopeId : 2176
	 * GScopeCnName : 普陀区
	 * FullPY : putuoqu
	 * FirstPY : PTQ
	 * GScopeLevel : 2
	 * ParentId : 21
	 * lng : 121.404059
	 * lat : 31.256775
	 */

	private DistrictBean District;
	private int GScopeId;
	/**
	 * GScopeId : 217606
	 * GScopeCnName : 真如
	 * FullPY : zhenru
	 * FirstPY : ZR
	 * GScopeLevel : 3
	 * ParentId : 2176
	 * lng : 121.410653
	 * lat : 31.260866
	 */

	private DistrictBean GScope;
	private double lng;
	private double lat;
	/**
	 * ImgId : 11244
	 * FileNo : a89220363aa31771442d476b9db865a6
	 * ImgType :
	 * ImgTitle :
	 * ImgDescription :
	 * FileUrl : 20160416/a89220363aa31771442d476b9db865a6.png
	 * ContentLength : 840464
	 * Width : 647
	 * Height : 873
	 */

	private List<ActImgsBean> ActImgs;

	public String getActId() {
		return ActId;
	}

	public void setActId(String ActId) {
		this.ActId = ActId;
	}

	public String getEstId() {
		return EstId;
	}

	public void setEstId(String EstId) {
		this.EstId = EstId;
	}

	public String getEstExtId() {
		return EstExtId;
	}

	public void setEstExtId(String EstExtId) {
		this.EstExtId = EstExtId;
	}

	public String getActTitle() {
		return ActTitle;
	}

	public void setActTitle(String ActTitle) {
		this.ActTitle = ActTitle;
	}

	public String getStartDate() {
		return StartDate;
	}

	public void setStartDate(String StartDate) {
		this.StartDate = StartDate;
	}

	public String getEndDate() {
		return EndDate;
	}

	public void setEndDate(String EndDate) {
		this.EndDate = EndDate;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String Description) {
		this.Description = Description;
	}

	public String getActAddress() {
		return ActAddress;
	}

	public void setActAddress(String ActAddress) {
		this.ActAddress = ActAddress;
	}

	public String getActType() {
		return ActType;
	}

	public void setActType(String ActType) {
		this.ActType = ActType;
	}

	public int getBookCnt() {
		return BookCnt;
	}

	public void setBookCnt(int BookCnt) {
		this.BookCnt = BookCnt;
	}

	public int getShowBookCnt() {
		return ShowBookCnt;
	}

	public void setShowBookCnt(int ShowBookCnt) {
		this.ShowBookCnt = ShowBookCnt;
	}

	public String getActTags() {
		return ActTags;
	}

	public void setActTags(String ActTags) {
		this.ActTags = ActTags;
	}

	public boolean isIsOnline() {
		return IsOnline;
	}

	public void setIsOnline(boolean IsOnline) {
		this.IsOnline = IsOnline;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(String CreateTime) {
		this.CreateTime = CreateTime;
	}

	public String getAdName() {
		return AdName;
	}

	public void setAdName(String AdName) {
		this.AdName = AdName;
	}

	public String getEstType() {
		return EstType;
	}

	public void setEstType(String EstType) {
		this.EstType = EstType;
	}

	public int getDistrictId() {
		return DistrictId;
	}

	public void setDistrictId(int DistrictId) {
		this.DistrictId = DistrictId;
	}

	public DistrictBean getDistrict() {
		return District;
	}

	public void setDistrict(DistrictBean District) {
		this.District = District;
	}

	public int getGScopeId() {
		return GScopeId;
	}

	public void setGScopeId(int GScopeId) {
		this.GScopeId = GScopeId;
	}

	public DistrictBean getGScope() {
		return GScope;
	}

	public void setGScope(DistrictBean GScope) {
		this.GScope = GScope;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public List<ActImgsBean> getActImgs() {
		return ActImgs;
	}

	public void setActImgs(List<ActImgsBean> ActImgs) {
		this.ActImgs = ActImgs;
	}

	public static class ActImgsBean implements Serializable {
		private int ImgId;
		private String FileNo;
		private String ImgType;
		private String ImgTitle;
		private String ImgDescription;
		private String FileUrl;
		private int ContentLength;
		private int Width;
		private int Height;

		public int getImgId() {
			return ImgId;
		}

		public void setImgId(int ImgId) {
			this.ImgId = ImgId;
		}

		public String getFileNo() {
			return FileNo;
		}

		public void setFileNo(String FileNo) {
			this.FileNo = FileNo;
		}

		public String getImgType() {
			return ImgType;
		}

		public void setImgType(String ImgType) {
			this.ImgType = ImgType;
		}

		public String getImgTitle() {
			return ImgTitle;
		}

		public void setImgTitle(String ImgTitle) {
			this.ImgTitle = ImgTitle;
		}

		public String getImgDescription() {
			return ImgDescription;
		}

		public void setImgDescription(String ImgDescription) {
			this.ImgDescription = ImgDescription;
		}

		public String getFileUrl() {
			return FileUrl;
		}

		public void setFileUrl(String FileUrl) {
			this.FileUrl = FileUrl;
		}

		public int getContentLength() {
			return ContentLength;
		}

		public void setContentLength(int ContentLength) {
			this.ContentLength = ContentLength;
		}

		public int getWidth() {
			return Width;
		}

		public void setWidth(int Width) {
			this.Width = Width;
		}

		public int getHeight() {
			return Height;
		}

		public void setHeight(int Height) {
			this.Height = Height;
		}
	}
}
