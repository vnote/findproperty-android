package com.cetnaline.findproperty.api.bean;

/**
 * 优惠
 * Created by fanxl2 on 2016/8/28.
 */
public class Discount {


	/**
	 * id : topic29
	 * group : 专题
	 * tags : 购房指南 投资
	 * url : http://sh.centanet.com/abl_wap/zhuanti/20160728/index.html
	 * img : upload/images/2016/8/518261513.jpg
	 * title : 相约奥运 拼金夺赢
	 * subtitle : 金牌品质楼盘排行
	 */

	private String id;
	private String group;
	private String tags;
	private String url;
	private String img;
	private String title;
	private String subtitle;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	@Override
	public String toString() {
		return "Discount{" +
				"id='" + id + '\'' +
				", group='" + group + '\'' +
				", tags='" + tags + '\'' +
				", url='" + url + '\'' +
				", img='" + img + '\'' +
				", title='" + title + '\'' +
				", subtitle='" + subtitle + '\'' +
				'}';
	}
}
