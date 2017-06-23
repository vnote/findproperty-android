package com.cetnaline.findproperty.entity.bean;

import java.util.List;

/**
 * Created by mamz4 on 2016/8/24.
 */
public class HomeListBean {

    /**
     * ResultNo : 0
     * Result : [{"id":"topic24","group":"活动","tags":"娱乐运动","url":"http://sh.centanet.com/abl_wap/huodong/20160614/index.html","img":"upload/images/2016/8/11103554833.jpg","title":"在中原，健身一夏！","subtitle":"免费享受一兆韦德运动体验"},{"id":"topic20","group":"活动","tags":"娱乐运动","url":"http://sh.centanet.com/abl_wap/huodong/20160512/index.html","img":"upload/images/2016/5/12151057675.jpg","title":"挑战麦克风 谁与我争锋","subtitle":"免费领取好乐迪优惠劵"},{"id":"topic16","group":"活动","tags":"美容教育","url":"http://sh.centanet.com/abl_wap/huodong/20160506/index.html","img":"upload/images/2016/5/613538138.jpg","title":"和中原一起发现美","subtitle":"雪纤廋高品质护理"}]
     * Total : 8
     */
    private int ResultNo;
    private int Total;
    /**
     * id : topic24
     * group : 活动
     * tags : 娱乐运动
     * url : http://sh.centanet.com/abl_wap/huodong/20160614/index.html
     * img : upload/images/2016/8/11103554833.jpg
     * title : 在中原，健身一夏！
     * subtitle : 免费享受一兆韦德运动体验
     */

    private List<ResultBean> Result;

    public int getResultNo() {
        return ResultNo;
    }

    public void setResultNo(int ResultNo) {
        this.ResultNo = ResultNo;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public List<ResultBean> getResult() {
        return Result;
    }

    public void setResult(List<ResultBean> Result) {
        this.Result = Result;
    }

    public static class ResultBean {
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
            return "ResultBean{" +
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

    @Override
    public String toString() {
        return "HomeListBean{" +
                "ResultNo=" + ResultNo +
                ", Total=" + Total +
                ", Result=" + Result +
                '}';
    }
}
