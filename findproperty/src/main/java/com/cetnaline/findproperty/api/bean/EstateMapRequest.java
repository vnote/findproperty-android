package com.cetnaline.findproperty.api.bean;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanxl2 on 2016/9/5.
 */
public class EstateMapRequest {

	private String MinLng;
	private String MaxLng;
	private String MinLat;
	private String MaxLat;

	private PostFilterBean PostFilter;

	public EstateMapRequest(){
		PostFilter = new PostFilterBean();
	}

	public static class PostFilterBean{
		/**
		 * RegionId : 2188
		 * GScopeId : 218801
		 * MinGArea : 70
		 * MaxGArea : 90
		 * MinSalePrice : 350000
		 * MaxSalePrice : 3800000
		 * Direction : 南北
		 * Fitment : 中装
		 * FloorDisplay : 中层
		 * PostType : S
		 * MinRoomCnt : 3
		 * MaxRoomCnt : 3
		 * MinOpdate : 1262304000
		 * MaxOpdate : 1420070400
		 * PostLabel : {"IsHot":false,"IsManWu":false,"IsManEr":false,"IsOnly":false,"IsAnyTimeSee":false,"IsDropPrice":false,"IsMetro":false,"IsSole":false}
		 * OrderByCriteria : DefaultOrder
		 */

		private String RegionId;
		private String GScopeId;
		private String MinGArea;
		private String MaxGArea;
		private String MinSalePrice;
		private String MaxSalePrice;
		private String Direction;
		private String Fitment;
		private String FloorDisplay;
		private String PostType = "S";
		private String MinRoomCnt;
		private String MaxRoomCnt;
		private String MinOpdate;
		private String MaxOpdate;
		private String MinRentPrice;
		private String MaxRentPrice;
		private String PropertyTypeList;

		/**
		 * IsHot : false
		 * IsManWu : false
		 * IsManEr : false
		 * IsOnly : false
		 * IsAnyTimeSee : false
		 * IsDropPrice : false
		 * IsMetro : false
		 * IsSole : false
		 */

		private PostLabelBean PostLabel;
		private String OrderByCriteria;

		private String DrawCircle;

		public String getPropertyTypeList() {
			return PropertyTypeList;
		}

		public void setPropertyTypeList(String propertyTypeList) {
			PropertyTypeList = propertyTypeList;
		}

		public String getDrawCircle() {
			return DrawCircle;
		}

		public void setDrawCircle(String drawCircle) {
			DrawCircle = drawCircle;
		}

		public String getRegionId() {
			return RegionId;
		}

		public void setRegionId(String regionId) {
			RegionId = regionId;
		}

		public String getGScopeId() {
			return GScopeId;
		}

		public void setGScopeId(String GScopeId) {
			this.GScopeId = GScopeId;
		}

		public String getMinGArea() {
			return MinGArea;
		}

		public void setMinGArea(String minGArea) {
			MinGArea = minGArea;
		}

		public String getMaxGArea() {
			return MaxGArea;
		}

		public void setMaxGArea(String maxGArea) {
			MaxGArea = maxGArea;
		}

		public String getMinRentPrice() {
			return MinRentPrice;
		}

		public void setMinRentPrice(String minRentPrice) {
			MinRentPrice = minRentPrice;
		}

		public String getMaxRentPrice() {
			return MaxRentPrice;
		}

		public void setMaxRentPrice(String maxRentPrice) {
			MaxRentPrice = maxRentPrice;
		}

		public String getMinSalePrice() {
			return MinSalePrice;
		}

		public void setMinSalePrice(String minSalePrice) {
			MinSalePrice = minSalePrice;
		}

		public String getMaxSalePrice() {
			return MaxSalePrice;
		}

		public void setMaxSalePrice(String maxSalePrice) {
			MaxSalePrice = maxSalePrice;
		}

		public String getMinRoomCnt() {
			return MinRoomCnt;
		}

		public void setMinRoomCnt(String minRoomCnt) {
			MinRoomCnt = minRoomCnt;
		}

		public String getMaxRoomCnt() {
			return MaxRoomCnt;
		}

		public void setMaxRoomCnt(String maxRoomCnt) {
			MaxRoomCnt = maxRoomCnt;
		}

		public String getMinOpdate() {
			return MinOpdate;
		}

		public void setMinOpdate(String minOpdate) {
			MinOpdate = minOpdate;
		}

		public String getMaxOpdate() {
			return MaxOpdate;
		}

		public void setMaxOpdate(String maxOpdate) {
			MaxOpdate = maxOpdate;
		}

		public String getDirection() {
			return Direction;
		}

		public void setDirection(String Direction) {
			this.Direction = Direction;
		}

		public String getFitment() {
			return Fitment;
		}

		public void setFitment(String Fitment) {
			this.Fitment = Fitment;
		}

		public String getFloorDisplay() {
			return FloorDisplay;
		}

		public void setFloorDisplay(String FloorDisplay) {
			this.FloorDisplay = FloorDisplay;
		}

		public String getPostType() {
			return PostType;
		}

		public void setPostType(String PostType) {
			this.PostType = PostType;
		}

		public PostLabelBean getPostLabel() {
			return PostLabel;
		}

		public void setPostLabel(PostLabelBean PostLabel) {
			this.PostLabel = PostLabel;
		}

		public String getOrderByCriteria() {
			return OrderByCriteria;
		}

		public void setOrderByCriteria(String OrderByCriteria) {
			this.OrderByCriteria = OrderByCriteria;
		}

		public static class PostLabelBean {
			private boolean IsHot;
			private boolean IsManWu;
			private boolean IsManEr;
			private boolean IsOnly;
			private boolean IsAnyTimeSee;
			private boolean IsDropPrice;
			private boolean IsMetro;
			private boolean IsSole;
			private String value;

			public boolean isIsHot() {
				return IsHot;
			}

			public void setIsHot(boolean IsHot) {
				this.IsHot = IsHot;
			}

			public boolean isIsManWu() {
				return IsManWu;
			}

			public void setIsManWu(boolean IsManWu) {
				this.IsManWu = IsManWu;
			}

			public boolean isIsManEr() {
				return IsManEr;
			}

			public void setIsManEr(boolean IsManEr) {
				this.IsManEr = IsManEr;
			}

			public boolean isIsOnly() {
				return IsOnly;
			}

			public void setIsOnly(boolean IsOnly) {
				this.IsOnly = IsOnly;
			}

			public boolean isIsAnyTimeSee() {
				return IsAnyTimeSee;
			}

			public void setIsAnyTimeSee(boolean IsAnyTimeSee) {
				this.IsAnyTimeSee = IsAnyTimeSee;
			}

			public boolean isIsDropPrice() {
				return IsDropPrice;
			}

			public void setIsDropPrice(boolean IsDropPrice) {
				this.IsDropPrice = IsDropPrice;
			}

			public boolean isIsMetro() {
				return IsMetro;
			}

			public void setIsMetro(boolean IsMetro) {
				this.IsMetro = IsMetro;
			}

			public boolean isIsSole() {
				return IsSole;
			}

			public void setIsSole(boolean IsSole) {
				this.IsSole = IsSole;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}
		}
	}

	public String getMinLng() {
		return MinLng;
	}

	public void setMinLng(String minLng) {
		MinLng = minLng;
	}

	public String getMaxLng() {
		return MaxLng;
	}

	public void setMaxLng(String maxLng) {
		MaxLng = maxLng;
	}

	public String getMinLat() {
		return MinLat;
	}

	public void setMinLat(String minLat) {
		MinLat = minLat;
	}

	public String getMaxLat() {
		return MaxLat;
	}

	public void setMaxLat(String maxLat) {
		MaxLat = maxLat;
	}

	public PostFilterBean getPostFilter() {
		return PostFilter;
	}

	public void setPostFilter(PostFilterBean postFilter) {
		PostFilter = postFilter;
	}



	public void put(String key, String value){

		if (key.equalsIgnoreCase("MinLng")){
			MinLng = value;
		}else if (key.equalsIgnoreCase("MaxLng")){
			MaxLng = value;
		}else if (key.equalsIgnoreCase("MinLat")){
			MinLat = value;
		}else if (key.equalsIgnoreCase("MaxLat")){
			MaxLat = value;
		}else if (key.equalsIgnoreCase("RegionId")){
			PostFilter.RegionId = value;
		}else if (key.equalsIgnoreCase("GScopeId")){
			PostFilter.GScopeId = value;
		}else if (key.equalsIgnoreCase("MinRentPrice")){
			PostFilter.MinRentPrice = value;
		}else if (key.equalsIgnoreCase("MaxRentPrice")){
			PostFilter.MaxRentPrice = value;
		}else if (key.equalsIgnoreCase("MinSalePrice")){
			PostFilter.MinSalePrice = value;
		}else if (key.equalsIgnoreCase("MaxSalePrice")){
			PostFilter.MaxSalePrice = value;
		}else if (key.equalsIgnoreCase("MinGArea")){
			PostFilter.MinGArea = value;
		}else if (key.equalsIgnoreCase("MaxGArea")){
			PostFilter.MaxGArea = value;
		}else if (key.equalsIgnoreCase("Direction")){
			PostFilter.Direction = value;
		}else if (key.equalsIgnoreCase("Fitment")){
			PostFilter.Fitment = value;
		}else if (key.equalsIgnoreCase("FloorDisplay")){
			PostFilter.FloorDisplay = value;
		}else if (key.equalsIgnoreCase("PostType")){
			PostFilter.PostType = value;
		}else if (key.equalsIgnoreCase("MinRoomCnt")){
			PostFilter.MinRoomCnt = value;
		}else if (key.equalsIgnoreCase("MaxRoomCnt")){
			PostFilter.MaxRoomCnt = value;
		}else if (key.equalsIgnoreCase("MinOpdate")){
			PostFilter.MinOpdate = value;
		}else if (key.equalsIgnoreCase("MaxOpdate")){
			PostFilter.MaxOpdate = value;
		}else if (key.equalsIgnoreCase("PropertyTypeList")) {
			PostFilter.PropertyTypeList = value;
		}else if (key.equalsIgnoreCase("Feature")){
			if (PostFilter.PostLabel==null){
				PostFilter.PostLabel = new PostFilterBean.PostLabelBean();
			}

			if (value.equals("1")){
				PostFilter.PostLabel.setIsHot(true);
			}else if (value.equals("2")){
				PostFilter.PostLabel.setIsManEr(true);
				PostFilter.PostLabel.setIsManWu(true);
			}else if (value.equals("3")){
				PostFilter.PostLabel.setIsMetro(true);
			}else if (value.equals("5")){
				PostFilter.PostLabel.setIsAnyTimeSee(true);
			}else if (value.equals("6")){
				PostFilter.PostLabel.setIsSole(true);
			}else if (value.equals("7")){
				PostFilter.PostLabel.setIsDropPrice(true);
			}else if (value.equals("8")){
				PostFilter.PostLabel.setIsOnly(true);
			}

			if (TextUtils.isEmpty(PostFilter.PostLabel.getValue())){
				PostFilter.PostLabel.setValue(value);
			}else {
				PostFilter.PostLabel.setValue(PostFilter.PostLabel.getValue()+"_"+value);
			}
		}
	}

	public String get(String key){
		if (key.equalsIgnoreCase("MinLng")){
			return MinLng;
		}else if (key.equalsIgnoreCase("MaxLng")){
			return MaxLng;
		}else if (key.equalsIgnoreCase("MinLat")){
			return MinLat;
		}else if (key.equalsIgnoreCase("MaxLat")){
			return MaxLat;
		}else if (key.equalsIgnoreCase("RegionId")){
			return PostFilter.RegionId;
		}else if (key.equalsIgnoreCase("GScopeId")){
			return PostFilter.GScopeId;
		}else if (key.equalsIgnoreCase("MinRentPrice")){
			return PostFilter.MinRentPrice;
		}else if (key.equalsIgnoreCase("MaxRentPrice")){
			return PostFilter.MaxRentPrice;
		}else if (key.equalsIgnoreCase("MinSalePrice")){
			return PostFilter.MinSalePrice;
		}else if (key.equalsIgnoreCase("MaxSalePrice")){
			return PostFilter.MaxSalePrice;
		}else if (key.equalsIgnoreCase("MinGArea")){
			return PostFilter.MinGArea;
		}else if (key.equalsIgnoreCase("MaxGArea")){
			return PostFilter.RegionId;
		}else if (key.equalsIgnoreCase("Direction")){
			return PostFilter.Direction;
		}else if (key.equalsIgnoreCase("Fitment")){
			return PostFilter.Fitment;
		}else if (key.equalsIgnoreCase("FloorDisplay")){
			return PostFilter.FloorDisplay;
		}else if (key.equalsIgnoreCase("PostType")){
			return PostFilter.PostType;
		}else if (key.equalsIgnoreCase("MinRoomCnt")){
			return PostFilter.MinRoomCnt;
		}else if (key.equalsIgnoreCase("MaxRoomCnt")){
			return PostFilter.MaxRoomCnt;
		}else if (key.equalsIgnoreCase("MinOpdate")){
			return PostFilter.MinOpdate;
		}else if (key.equalsIgnoreCase("MaxOpdate")){
			return PostFilter.MaxOpdate;
		}else if (key.equalsIgnoreCase("Feature")){
			return PostFilter.PostLabel.getValue();
		}else if (key.equalsIgnoreCase("PropertyTypeList")) {
			return PostFilter.PropertyTypeList;
		}else {
			return null;
		}
	}

	public void remove(String key){
		if (key.equalsIgnoreCase("MinLng")){
			MinLng = null;
		}else if (key.equalsIgnoreCase("MaxLng")){
			MaxLng = null;
		}else if (key.equalsIgnoreCase("MinLat")){
			MinLat = null;
		}else if (key.equalsIgnoreCase("MaxLat")){
			MaxLat = null;
		}else if (key.equalsIgnoreCase("RegionId")){
			PostFilter.RegionId = null;
		}else if (key.equalsIgnoreCase("GScopeId")){
			PostFilter.GScopeId = null;
		}else if (key.equalsIgnoreCase("MinRentPrice")){
			PostFilter.MinRentPrice = null;
		}else if (key.equalsIgnoreCase("MaxRentPrice")){
			PostFilter.MaxRentPrice = null;
		}else if (key.equalsIgnoreCase("MinSalePrice")){
			PostFilter.MinSalePrice = null;
		}else if (key.equalsIgnoreCase("MaxSalePrice")){
			PostFilter.MaxSalePrice = null;
		}else if (key.equalsIgnoreCase("MinGArea")){
			PostFilter.MinGArea = null;
		}else if (key.equalsIgnoreCase("MaxGArea")){
			PostFilter.MaxGArea = null;
		}else if (key.equalsIgnoreCase("Direction")){
			PostFilter.Direction = null;
		}else if (key.equalsIgnoreCase("Fitment")){
			PostFilter.Fitment = null;
		}else if (key.equalsIgnoreCase("FloorDisplay")){
			PostFilter.FloorDisplay = null;
		}else if (key.equalsIgnoreCase("PostType")){
			PostFilter.PostType = null;
		}else if (key.equalsIgnoreCase("MinRoomCnt")){
			PostFilter.MinRoomCnt = null;
		}else if (key.equalsIgnoreCase("MaxRoomCnt")){
			PostFilter.MaxRoomCnt = null;
		}else if (key.equalsIgnoreCase("MinOpdate")){
			PostFilter.MinOpdate = null;
		}else if (key.equalsIgnoreCase("MaxOpdate")){
			PostFilter.MaxOpdate = null;
		}else if (key.equalsIgnoreCase("Feature")){
			PostFilter.PostLabel = null;
		}else if (key.equalsIgnoreCase("PropertyTypeList")) {
			PostFilter.PropertyTypeList = null;
		}
	}

	public void clear(){
		MinLng = null;
		MaxLng = null;
		MinLat = null;
		MaxLat = null;
		PostFilter.RegionId = null;
		PostFilter.GScopeId = null;
		PostFilter.MinRentPrice = null;
		PostFilter.MaxRentPrice = null;
		PostFilter.MinSalePrice = null;
		PostFilter.MaxSalePrice = null;
		PostFilter.MaxGArea = null;
		PostFilter.MinGArea = null;
		PostFilter.Direction = null;
		PostFilter.Fitment = null;
		PostFilter.FloorDisplay = null;
		PostFilter.MinRoomCnt = null;
		PostFilter.MaxRoomCnt = null;
		PostFilter.MinOpdate = null;
		PostFilter.MaxOpdate = null;
		PostFilter.PostLabel = null;
		PostFilter.DrawCircle = null;
		PostFilter.PropertyTypeList = null;
	}

	public Map<String, String> getEstateParam(){
		Map<String, String> param = new HashMap<>();
		if (PostFilter.MinRentPrice!=null){
			param.put("MinRentPrice", PostFilter.MinRentPrice);
		}
		if (PostFilter.MaxRentPrice!=null){
			param.put("MaxRentPrice", PostFilter.MaxRentPrice);
		}
		if (PostFilter.MinSalePrice!=null){
			param.put("MinSalePrice", PostFilter.MinSalePrice);
		}
		if (PostFilter.MaxSalePrice!=null){
			param.put("MaxSalePrice", PostFilter.MaxSalePrice);
		}
		if (PostFilter.MaxGArea!=null){
			param.put("MaxGArea", PostFilter.MaxGArea);
		}
		if (PostFilter.MinGArea!=null){
			param.put("MinGArea", PostFilter.MinGArea);
		}
		if (PostFilter.Direction!=null){
			param.put("Direction", PostFilter.Direction);
		}
		if (PostFilter.Fitment!=null){
			param.put("Fitment", PostFilter.Fitment);
		}
		if (PostFilter.FloorDisplay!=null){
			param.put("FloorDisplay", PostFilter.FloorDisplay);
		}
		if (PostFilter.MinRoomCnt!=null){
			param.put("MinRoomCnt", PostFilter.MinRoomCnt);
		}
		if (PostFilter.MaxRoomCnt!=null){
			param.put("MaxRoomCnt", PostFilter.MaxRoomCnt);
		}
		if (PostFilter.MinOpdate!=null){
			param.put("MinOpdate", PostFilter.MinOpdate);
		}
		if (PostFilter.MaxOpdate!=null){
			param.put("MaxOpdate", PostFilter.MaxOpdate);
		}
		if (PostFilter.PostLabel!=null){
			param.put("Feature", PostFilter.PostLabel.getValue());
		}
		if (PostFilter.PropertyTypeList != null) {
			param.put("PropertyTypeList", PostFilter.PropertyTypeList);
		}

		return param;
	}

}
