package com.cetnaline.findproperty.db.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;


@Entity(
    nameInDb = "house_staff"
)
public class Staff {

    @Id
    private Long id;

    @NotNull
    private String uId;

    @NotNull
    private String name;

    private String imageUrl;

    private String departmentName;

    @NotNull
    private String mobile;

    private String staff400Tel;

    private String serviceEstates;

    private String staffRemark;

    @Generated(hash = 615839530)
    public Staff(Long id, @NotNull String uId, @NotNull String name,
            String imageUrl, String departmentName, @NotNull String mobile,
            String staff400Tel, String serviceEstates, String staffRemark) {
        this.id = id;
        this.uId = uId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.departmentName = departmentName;
        this.mobile = mobile;
        this.staff400Tel = staff400Tel;
        this.serviceEstates = serviceEstates;
        this.staffRemark = staffRemark;
    }

    @Generated(hash = 1774984890)
    public Staff() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUId() {
        return this.uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStaff400Tel() {
        return this.staff400Tel;
    }

    public void setStaff400Tel(String staff400Tel) {
        this.staff400Tel = staff400Tel;
    }

    public String getServiceEstates() {
        return this.serviceEstates;
    }

    public void setServiceEstates(String serviceEstates) {
        this.serviceEstates = serviceEstates;
    }

    public String getStaffRemark() {
        return this.staffRemark;
    }

    public void setStaffRemark(String staffRemark) {
        this.staffRemark = staffRemark;
    }
}
