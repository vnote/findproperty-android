package com.cetnaline.findproperty.api;

import com.cetnaline.findproperty.BuildConfig;

/**
 * Created by diaoqf on 2016/7/15.
 */
public class NetContents {
    public static boolean IS_TEST_ENV = false;

    public static final String BASE_HOST = BuildConfig.API_URL;   //正式接口
//    public static final String BASE_HOST = "http://10.4.99.40/zfapi/json/reply/";   //正式接口
//    public static final String BASE_HOST = "http://10.4.99.40/zfapi_formal/json/reply/";   //直聊正式库测试地址接口
    public static final String BASE_HOST_TEST = "http://10.4.99.40/zfapi/json/reply/";   //测试接口
//    public static final String BASE_HOST = "http://10.4.99.40:802/zfapi/json/reply/";   //准正式接口 所有数据指向正式环境
    public static final String RONG_CLOUD_HOST = "https://api.cn.ronghub.com";

//    public static final String RONG_TEST_HOST = "http://10.4.99.12:802/bizcommon/021/json/reply/";
    //新浪host
    public static final String SINA_HOST = "https://api.weibo.com/2/";
    //微信
    public static final String WX_HOST = "https://api.weixin.qq.com/sns/";
    //QQ
    public static final String QQ_HOST = "https://graph.qq.com/user/";

    public static final String STAFF_HEAD_HOST = "https://imgsh.centanet.com/shanghai/staticfile/agent/agentphoto/";

    public static final String IMG_BASE_URL = "https://imgsh.centanet.com/ctpostimage/agency/";

    //新房图片显示前缀
    public static final String NEW_HOUSE_IMG = "http://pic.sh.centanet.com/ctpostimage/xinfang/";

    public static final String SHARE_HOUSE_HOST = "http://sh.centanet.com/m/";
    //首页优惠列表图片前缀
    public static final String HOME_PRIVILEGE = "https://sh.centanet.com/act/";

//    public static final String LOGIN_HOST = "http://10.58.8.232:218/json/reply/";  //  http://10.58.8.48:215/json/reply/  10.58.8.48:215  10.58.8.232:218   //

    public static final String CITY_NAME = "上海";

    public static final int HTTP_CONNECT_TIMEOUT = 3000;
    public static final int HTTP_WRITE_TIMEOUT = 6000;
    public static final int HTTP_READ_TIMEOUT = 8000;

    public static final int IMAGE_BIG_WIDTH = 600;
    public static final int IMAGE_BIG_HEIGHT = 400;

    public static final int HOUSE_SMALL_IMAGE_LIST_WIDTH = 600;
    public static final int HOUSE_SMALL_IMAGE_LIST_HEIGHT = 400;

    public static final int HOUSE_BIG_IMAGE_LIST_WIDTH = 600;
    public static final int HOUSE_BIG_IMAGE_LIST_HEIGHT = 400;

    public static final String DEFAULT_SHARE_IMAGE = "https://staticsh.centanet.com/images/192-192.jpg";

    public static final String CHANNAL_KEY = "BaiduMobAd_CHANNEL";

    public static final String GSCOPE_NAME = "gscopeid";
    public static final String REGION_NAME = "regionid";
    public static final String RAILLINE_NAME = "l";
    public static final String RAILWAY_NAME = "a";
    public static final String SCHOOL_NAME = "schoolid";
    public static final String SEARCH_NAME = "key";
    public static final String ESTATE_NAME = "cestcode";

    public static final String EVALUATE_URL = "http://passportsh.centanet.com/page/lookrecommendhouse.aspx";
//    public static final String EVALUATE_URL = "http://10.58.8.232:8015/page/lookrecommendhouse.aspx";

    public static final String ENTRUST_PRICE = "https://sh.centanet.com/m/entrust/evaluate";
//    public static final String ENTRUST_PRICE = "http://10.4.99.32/m2/page/evaluateHome.aspx";


}
