package com.cetnaline.findproperty.db.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Entity mapped to table "HISTORY_HOUSE_TAG".
 */
@Entity(
   nameInDb = "house_search_history"
)
public class HistoryHouseTag {

    @Id(autoincrement = true)
    private Long id;
    private String UserId;
    private String HouseType;
    private String TagCode;
    private String PN1;
    private String PN2;
    private String Tag;
    private String TagPY;
    private String TagCategory;
    private Integer SNum;
    private Integer RNum;
    private Double EstateAvgPriceSale;
    private Double EstateAvgPriceRent;
   @Generated(hash = 1683863762)
   public HistoryHouseTag(Long id, String UserId, String HouseType, String TagCode, String PN1,
           String PN2, String Tag, String TagPY, String TagCategory, Integer SNum, Integer RNum,
           Double EstateAvgPriceSale, Double EstateAvgPriceRent) {
       this.id = id;
       this.UserId = UserId;
       this.HouseType = HouseType;
       this.TagCode = TagCode;
       this.PN1 = PN1;
       this.PN2 = PN2;
       this.Tag = Tag;
       this.TagPY = TagPY;
       this.TagCategory = TagCategory;
       this.SNum = SNum;
       this.RNum = RNum;
       this.EstateAvgPriceSale = EstateAvgPriceSale;
       this.EstateAvgPriceRent = EstateAvgPriceRent;
   }
   @Generated(hash = 2028465429)
   public HistoryHouseTag() {
   }
   public Long getId() {
       return this.id;
   }
   public void setId(Long id) {
       this.id = id;
   }
   public String getUserId() {
       return this.UserId;
   }
   public void setUserId(String UserId) {
       this.UserId = UserId;
   }
   public String getHouseType() {
       return this.HouseType;
   }
   public void setHouseType(String HouseType) {
       this.HouseType = HouseType;
   }
   public String getTagCode() {
       return this.TagCode;
   }
   public void setTagCode(String TagCode) {
       this.TagCode = TagCode;
   }
   public String getPN1() {
       return this.PN1;
   }
   public void setPN1(String PN1) {
       this.PN1 = PN1;
   }
   public String getPN2() {
       return this.PN2;
   }
   public void setPN2(String PN2) {
       this.PN2 = PN2;
   }
   public String getTag() {
       return this.Tag;
   }
   public void setTag(String Tag) {
       this.Tag = Tag;
   }
   public String getTagPY() {
       return this.TagPY;
   }
   public void setTagPY(String TagPY) {
       this.TagPY = TagPY;
   }
   public String getTagCategory() {
       return this.TagCategory;
   }
   public void setTagCategory(String TagCategory) {
       this.TagCategory = TagCategory;
   }
   public Integer getSNum() {
       return this.SNum;
   }
   public void setSNum(Integer SNum) {
       this.SNum = SNum;
   }
   public Integer getRNum() {
       return this.RNum;
   }
   public void setRNum(Integer RNum) {
       this.RNum = RNum;
   }
   public Double getEstateAvgPriceSale() {
       return this.EstateAvgPriceSale;
   }
   public void setEstateAvgPriceSale(Double EstateAvgPriceSale) {
       this.EstateAvgPriceSale = EstateAvgPriceSale;
   }
   public Double getEstateAvgPriceRent() {
       return this.EstateAvgPriceRent;
   }
   public void setEstateAvgPriceRent(Double EstateAvgPriceRent) {
       this.EstateAvgPriceRent = EstateAvgPriceRent;
   }



}