package com.cetnaline.findproperty.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
   nameInDb = "house_rc_token"
)
public class RcToken {

    @Id
    private Long id;

    private String userId;

    private String token;

   @Generated(hash = 1211351223)
   public RcToken(Long id, String userId, String token) {
       this.id = id;
       this.userId = userId;
       this.token = token;
   }

   @Generated(hash = 1540598280)
   public RcToken() {
   }

   public Long getId() {
       return this.id;
   }

   public void setId(Long id) {
       this.id = id;
   }

   public String getUserId() {
       return this.userId;
   }

   public void setUserId(String userId) {
       this.userId = userId;
   }

   public String getToken() {
       return this.token;
   }

   public void setToken(String token) {
       this.token = token;
   }

}
