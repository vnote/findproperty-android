package com.cetnaline.findproperty.api.bean;

import java.io.Serializable;

/**
 * 启动广告
 * Created by fanxl2 on 2016/8/25.
 */
public class AppAdBo implements Serializable {


	/**
	 * ImgUrl : http://act.centanet.com/www/images/bf18730b662f8ca5d514659388ada75c.png
	 * AdvertUrl : http://act.centanet.com/www/delivery/ck.php?oaparams=2__bannerid=442__zoneid=210__cb=f939068ef9__oadest=http%3A%2F%2Fsh.centanet.com%2Fm%2Fact
	 */

	private String ImgUrl;
	private String AdvertUrl;

	public String getImgUrl() {
		return ImgUrl;
	}

	public void setImgUrl(String ImgUrl) {
		this.ImgUrl = ImgUrl;
	}

	public String getAdvertUrl() {
		return AdvertUrl;
	}

	public void setAdvertUrl(String AdvertUrl) {
		this.AdvertUrl = AdvertUrl;
	}
}
