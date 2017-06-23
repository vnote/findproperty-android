package com.cetnaline.findproperty;

/**
 * Created by diaoqf on 2016/7/8.
 */
public class AppContents {

    //加密公钥私钥
    public final static String PUBLICSECRET = "F53DB798524C44AF8DE106DB744EC07";   //
    public final static String PRIVATESECRET = "2Fo1URzMx2qw3zV2juhcvM7LfjG";

    //sp文件名称
    public final static String SP_NAME = "find_house_contents_file";

    //纬度存储标识
    public final static String LATITUDE = "latitude";
    //经度存储标识
    public final static String LONGITUDE = "longitude";
    //定位半径
    public final static String LOCATION_REDUIS = "location_reduis";
    //当前定位地址标识
    public final static String LOCATION_ADDRESS = "location_address";
    //当前定位城市标识
    public final static String LOCATION_CITY = "location_city";
    //当前定位城市码标识
    public final static String LOCATION_CITY_CODE = "location_city_code";
    //融云token
    public final static String RONG_CLOUD_TOKEN = "rong_cloud_token";
    //用户名
    public final static String RONG_CLOUD_USERNAME = "rong_cloud_username";
    //用户id
    public final static String RONG_CLOUD_USERID = "rong_cloud_userid";
    //用户头像
    public final static String RONG_CLOUD_PORTRAIT = "rong_cloud_portrait";
    //即时消息未读数量
    public final static String RONG_CLOUD_UNREAD_COUNT = "rong_cloud_unread_count";

    //app语言设置
    public final static String LANGUAGE = "language";
    //语言类别
    public final static int LANGUAGE_CHINESE = 0;
    public final static int LANGUAGE_ENGLISH = 1;

    //rxbus messages
    public final static String LANGUAGE_MESSAGE = "language_message";

    //中原短信服务号
    public final static String SMS_SERVER_1 = "10690983325526";
    public final static String SMS_SERVER_2 = "10690487325526";

    //委托数量限制
    public final static int DEPUTE_COUNT = 5000;



    //保存用户登录信息
    public final static String USER_IS_LOGIN = "user_is_login";
    public final static String USER_INFO_USERID = "user_info_userid";
    public final static String USER_INFO_NICKNAME = "user_info_nickname";
    public final static String USER_INFO_PASSWORD = "user_info_password";
    public final static String USER_INFO_EMAIL = "user_info_email";
    public final static String USER_INFO_PHONE = "user_info_phone";
    public final static String USER_INFO_SINAACCOUNT = "user_info_sinaaccount";
    public final static String USER_INFO_QQACCOUNT = "user_info_qqaccount";
    public final static String USER_INFO_WEIXINACCOUNT = "user_info_weixinaccount";
    public final static String USER_INFO_USERPHOTOURL = "user_info_userphotourl";
    public final static String USER_INFO_SEX = "user_info_sex";

    //缓存短信验证码
    public final static String SMS_CODE = "sms_code";
    //获取验证码时间
    public final static String SMS_GET_TIME = "sms_get_time";

    //微信分享类型
    public final static String WX_LOGIN_TAG = "wx_login_tag";
    public final static String WX_BIND_TAG = "wx_bind_tag";
    public final static String WX_SHARE_FINISH = "WX_SHARE_FINISH";
    public final static String WX_SOURCE_SHARE = "wx_source_share";

    //数据库版本
    public final static String DATABASE_VERSION = "database_version";

    //推送设置
    public final static String CLOSE_NOTICE = "close_notice"; //关闭通知栏
    public final static String CLOSE_VIBRATE = "close_vibrate"; //关闭震动
    public final static String CLOSE_SOUND = "close_sound";  //关闭声音

    public final static String ADV_SHOW = "adv_show";    //是否显示广告

    public static boolean isOutGoingCall;
    public static String callNumber;

    //用户登录凭证
    public final static String SH_CK_USER = "sh_ck_user";
    public final static String SH_CK_USER_AUK = "sh_ck_user_auk";

    public final static int RONG_MESSAGE_NOTIFICATION_ID = 999;
    public static int JPUSH_MESSAGE_NOTIFICATION_ID = 1000;

    //流量活动
    // TODO: 2017/3/1 流量活动参数
    public final static String ACTIVE_URL = "http://passportsh.centanet.com/m2/ggk/index.aspx";
    public final static String ACTIVE_SHARE = "https://passportsh.centanet.com/m/ggk/share.html";

    //房源默认图片链接
    public final static String POST_DEFAULT_IMG_URL = "http://imgsh.centanet.com/shanghai/staticfile/web/post/defaultForAndroid.png";
    public final static String EST_DEFAULT_IMG_URL = "http://imgsh.centanet.com/shanghai/staticfile/web/estate/defaultForAndroid.png";


    public final static String YAO_QING_MA = "yao_qing_ma";  //邀请码登录flag

    //百度统计事件
    public final static String DRAW_SEARCH_COUNTER = "001";
    public final static String SUBWAY_SEARCH_COUNTER = "002";
    public final static String SCHOOL_SEARCH_COUNTER = "003";

    //默认城市经纬度
    public final static double CITY_LAT = 31.236301;
    public final static double CITY_LNG = 121.480236;

    public final static String[] rc_define_code = new String[]{
            "\uD83D\uDE0A", "\uD83D\uDE28", "\uD83D\uDE0D", "\uD83D\uDE33", "\uD83D\uDE0E", "\uD83D\uDE2D", "\uD83D\uDE0C", "\uD83D\uDE35",
            "\uD83D\uDE34", "\uD83D\uDE22", "\uD83D\uDE05", "\uD83D\uDE21", "\uD83D\uDE1C", "\uD83D\uDE00", "\uD83D\uDE32", "\uD83D\uDE1F",
            "\uD83D\uDE24", "\uD83D\uDE1E", "\uD83D\uDE2B", "\uD83D\uDE23", "\uD83D\uDE08", "\uD83D\uDE09", "\uD83D\uDE2F",

            "\uD83D\uDE15", "\uD83D\uDE30", "\uD83D\uDE0B", "\uD83D\uDE1D", "\uD83D\uDE13", "\uD83D\uDE03", "\uD83D\uDE02", "\uD83D\uDE18",
            "\uD83D\uDE12", "\uD83D\uDE0F", "\uD83D\uDE36", "\uD83D\uDE31", "\uD83D\uDE16", "\uD83D\uDE29", "\uD83D\uDE14", "\uD83D\uDE11",
            "\uD83D\uDE1A", "\uD83D\uDE2A", "\uD83D\uDE07", "\uD83D\uDE4A", "\uD83D\uDC4A", "\uD83D\uDC4E", "☝",

            "\uD83D\uDC4C","\uD83D\uDC4D"
    };

    public final static String[] ext_s = new String[]{
            "✌","\uD83D\uDE2C","\uD83D\uDE37","\uD83D\uDE48","\uD83D\uDC4C","\uD83D\uDC4F","✊","\uD83D\uDCAA",
            "\uD83D\uDE06","☺","\uD83D\uDE49","\uD83D\uDC4D","\uD83D\uDE4F","✋","☀","☕",
            "⛄","\uD83D\uDCDA","\uD83C\uDF81","\uD83C\uDF89","\uD83C\uDF66","☁","❄",

            "⚡", "\uD83D\uDCB0","\uD83C\uDF82","\uD83C\uDF93","\uD83C\uDF56","☔","⛅","✏",
            "\uD83D\uDCA9", "\uD83C\uDF84", "\uD83C\uDF77","\uD83C\uDFA4","\uD83C\uDFC0","\uD83C\uDC04","\uD83D\uDCA3","\uD83D\uDCE2",
            "\uD83C\uDF0F", "\uD83C\uDF6B","\uD83C\uDFB2","\uD83C\uDFC2","\uD83D\uDCA1","\uD83D\uDCA4","\uD83D\uDEAB",

            "\uD83C\uDF3B", "\uD83C\uDF7B", "\uD83C\uDFB5","\uD83C\uDFE1","\uD83D\uDCA2","\uD83D\uDCDE","\uD83D\uDEBF","\uD83C\uDF5A",
            "\uD83D\uDC6A","\uD83D\uDC7C","\uD83D\uDC8A","\uD83D\uDD2B","\uD83C\uDF39","\uD83D\uDC36","\uD83D\uDC84","\uD83D\uDC6B",
            "\uD83D\uDC7D","\uD83D\uDC8B","\uD83C\uDF19","\uD83C\uDF49","\uD83D\uDC37","\uD83D\uDC94","\uD83D\uDC7B",

            "\uD83D\uDC7F", "\uD83D\uDC8D","\uD83C\uDF32","\uD83D\uDC34","\uD83D\uDC51","\uD83D\uDD25","⭐","⚽",
            "\uD83D\uDD56","⏰","\uD83D\uDE01", "\uD83D\uDE80","⏳"
    };

    public final static String[] wx_define_code = new String[]{
            "/::)",  "/::~",    "/::B",   "/::|",       "/:8-)",  "/::<",    "/::$",  "/::X",
            "/::Z",  "/::\'(",  "/::-|",  "/::@",       "/::P",   "/::D",    "/::O",  "/::(",
            "/::+",  "/:–b",   "/::Q",   "/::T",       "/:,@P",  "/:,@-D",  "/::d",

            "/:,@o", "/::g",    "/:|-)",  "/::!",       "/::L",   "/::>",    "/::,@", "/:,@f",
            "/::-S", "/:?",     "/:,@x",  "/:,@@",      "/::8",   "/:,@!",   "/:!!!", "/:xx",
            "/:bye", "/:wipe",  "/:dig",  "/:handclap","/:&-(",   "/:B-)",   "/:<@",

            "/:ok",  "/:strong"
    };
//            ,"/:@>","/::-O","/:>-|","/:P-(","/::\'|","/:X-)","/::*","/:@x","/:8*",
//            "/:pd","/:<W>","/:beer","/:basketb","/:oo", "/:coffee","/:eat","/:pig","/:rose","/:fade","/:showlove","/:heart","/:break","/:cake","/:li"
}
