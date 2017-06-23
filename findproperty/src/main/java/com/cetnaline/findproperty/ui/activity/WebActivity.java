package com.cetnaline.findproperty.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.event.ShareEvent;
import com.cetnaline.findproperty.presenter.impl.WebPresenter;
import com.cetnaline.findproperty.presenter.ui.WebContract;
import com.cetnaline.findproperty.ui.fragment.HouseDetailFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.ui.fragment.VillageDetailFragment;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.sharedialog.ShareDialog;
import com.orhanobut.logger.Logger;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 *
 * Created by diaoqf on 2016/8/8.
 */
public class WebActivity extends BaseActivity<WebPresenter> implements WebContract.View {

    public static final String TARGET_URL = "target_url";
    public static final String WEB_TYPE_KEY = "WEB_TYPE_KEY";
    //是否显示分享按钮
    public static final String WEB_SHARE_KEY = "WEB_SHARE_KEY";
    public static final String TITLE_HIDDEN_KEY = "TITLE_HIDDEN_KEY";
    //流量活动标识
    public static final String IS_ACTIVE_URL = "is_active_url";

    public static final int WEB_TYPE_NORMAL = 0;
    public static final int WEB_TO_HOME = 1;

    public static final int LOGIN_REQ = 8801;
    public static final int CHECK_REQ = 8802;


    private int webType;

    @BindView(R.id.web_view)
    WebView web_view;

    private boolean showShare;

    @Override
    protected int getContentViewId() {
        return R.layout.act_web;
    }

    private String currentUrl;

    @Override
    protected void init(Bundle savedInstanceState) {
        Intent intent = getIntent();
        webType = intent.getIntExtra(WEB_TYPE_KEY, WEB_TYPE_NORMAL);
        showShare = intent.getBooleanExtra(WEB_SHARE_KEY, false);

        web_view.getSettings().setJavaScriptEnabled(true);

        web_view.getSettings().setPluginState(WebSettings.PluginState.ON);
        web_view.getSettings().setDomStorageEnabled(true);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
            web_view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        web_view.setWebChromeClient(new WebChromeClient());
        web_view.removeJavascriptInterface("searchBoxJavaBridge_");
        web_view.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);


                    return true; // we handled the url loading
                }

                boolean result = urlDone(url);
                if (result){
                    view.loadUrl(currentUrl);
                }else {
                    currentUrl = url;
                }
                return result;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                return super.shouldOverrideUrlLoading(view, request);
                return urlDone(request.getUrl().getPath());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                if (!web_view.getSettings().getLoadsImagesAutomatically()) {
                    web_view.getSettings().setLoadsImagesAutomatically(true);
                }
                //调H5页面JS方法
                web_view.loadUrl("javascript:appOpConfig()");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        Logger.i("UserAgent:"+web_view.getSettings().getUserAgentString());

        web_view.getSettings().setUseWideViewPort(true);
        web_view.getSettings().setLoadWithOverviewMode(true);
//        web_view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式

        if(Build.VERSION.SDK_INT >= 19) { //对加载的优化
            web_view.getSettings().setLoadsImagesAutomatically(true);
        } else {
            web_view.getSettings().setLoadsImagesAutomatically(false);
        }

        web_view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口

        web_view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); //滑动条的样式

        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setBuiltInZoomControls(true);
        web_view.setHorizontalScrollBarEnabled(false);  //取消Horizontal ScrollBar显示

        web_view.setWebChromeClient(new WebChromeClient() {
            //网页进度加载提示
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }

            @Override
            public void onReceivedTitle(WebView view, String webTitle) {
                super.onReceivedTitle(view, webTitle);
                //设置标题
                if (webTitle.length() > 10) {
                    center_title.setText(webTitle.substring(0,10)+"...");
                } else {
                    center_title.setText(webTitle);
                }

                shareTitle = webTitle;

                center_title.setVisibility(View.VISIBLE);
            }
        });

        web_view.addJavascriptInterface(new Object(){

            @JavascriptInterface
            public void toBack(){
                WebActivity.this.finish();
            }

            @JavascriptInterface
            public void goBuyHouse(){
                WebActivity.this.finish();
                Intent intent = new Intent(WebActivity.this, MainTabActivity.class);
                intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_MAP);
                startActivity(intent);
            }

            @JavascriptInterface
            public void goSaleHouse(){
                WebActivity.this.finish();
            }

            //流量活动方法
            // TODO: 2017/3/1 流量活动代码
            /**
             * 到登录页面
             */
            @JavascriptInterface
            public void gotoLogin(){
                Bundle bundle = new Bundle();
                bundle.putInt(LoginActivity.OPEN_TYPE, 0);
                Intent intent = new Intent(WebActivity.this, LoginActivity.class);
                if (bundle != null) {
                    intent.putExtras(bundle);
                }
                WebActivity.this.startActivityForResult(intent, LOGIN_REQ);
            }

            /**
             * 到手机绑定页面
             */
            @JavascriptInterface
            public void gotoBindPhone(){
                Intent intent = new Intent(WebActivity.this,ExchangePhoneActivity.class);
                intent.putExtra(ExchangePhoneActivity.CALL_TYPE,ExchangePhoneActivity.UPDATE);
                WebActivity.this.startActivityForResult(intent, CHECK_REQ);
            }

            /**
             * 分享后调用服务端接口传递分享ID
             * @param
             */
            @JavascriptInterface
            public void putServerId(String value) {
                Logger.i("buobao:分享结果:"+value);
                if (value != null && !value.equals("0")) {
                    mPresenter.updateShareStateRequest(value);
                }
            }

            @JavascriptInterface
            public void shareDone() {

            }

        }, "findProperty");

        shareUrl = intent.getExtras().getString(TARGET_URL);


        //设置cookies,这里流量活动需要使用cookies
        // TODO: 2017/3/1 流量活动代码
        if (intent.getBooleanExtra(IS_ACTIVE_URL, false)) {
            web_view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);     //设置无缓存模式
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
            cookieManager.setCookie(shareUrl, "sh_ck_user_auk_wap="+SharedPreferencesUtil.getString(AppContents.SH_CK_USER_AUK)+"; domain=.centanet.com; path=/");//+ SharedPreferencesUtil.getString(AppContents.SH_CK_USER_AUK)
            Logger.i("buobao;"+SharedPreferencesUtil.getString(AppContents.SH_CK_USER_AUK));
            //web_view.loadUrl(AppContents.ACTIVE_URL);
            //7beIS%2fVTjmVp3sNbwnZvrmyuH15Vuw5NklcdzMWoE67nstzGU71sgrm9HghX4vkpvSlFgf3Cd79A5lfaTfQaXNN2AOI9%2b6mn8pwZTtog2Zdxe3fkm5qvM2iBCwIIf2dusez2RJjsBD2f%2bihVWl1z5lLnZnrwDETbQm%2fUeoLYsiMM8OJn1m2g4pefH36rkA%2bo
            web_view.loadUrl(shareUrl);
            shareUrl = AppContents.ACTIVE_SHARE;
        } else {
            web_view.loadUrl(shareUrl);
        }

        /**
         * 微信分享回调
         */
        RxBus.getDefault().toObservable(ShareEvent.class)
                .subscribe(shareEvent -> {
                    switch (shareEvent.getEventType()){
                        case ShareEvent.EVENT_TYPE_SUCCESS:
                            shareDialog.dismiss();
                            toast("分享成功");
                            //web_view.loadUrl("javascript:openMsgBox('对不起', '<span>您提交的信息有误，<br />请核实后再次尝试！</span>', true)");  //openMsgBox('对不起', '<span>您提交的信息有误，<br />请核实后再次尝试！</span>', true)  gjObj.shareAct()
//                            web_view.loadUrl("javascript:findProperty.putServerId(gjObj.shareAct())");
                            break;
                        case ShareEvent.EVENT_TYPE_CANCLE:
                            toast("分享被取消");
                            break;
                        case ShareEvent.EVENT_TYPE_FAIL:
                            toast("分享失败");
                            break;
                    }
                });

    }


    /**
     * 登录或绑定之后刷新cookie
     * @param requestCode
     * @param resultCode
     * @param data
     */
    // TODO: 2017/3/1 流量活动代码
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOGIN_REQ:
            case CHECK_REQ:
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeSessionCookie();
                cookieManager.setCookie(AppContents.ACTIVE_URL, "sh_ck_user_auk_wap="+SharedPreferencesUtil.getString(AppContents.SH_CK_USER_AUK)+"; domain=.centanet.com; path=/");//+ SharedPreferencesUtil.getString(AppContents.SH_CK_USER_AUK)
                Logger.i("buobao;cookie:"+SharedPreferencesUtil.getString(AppContents.SH_CK_USER_AUK));
                web_view.reload();
                break;
        }

        /**
         * QQ分享回调
         */
        if(requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.handleResultData(data, new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    shareDialog.dismiss();
                    toast("分享成功");
                    //web_view.loadUrl("javascript:findProperty.putServerId(gjObj.shareAct())");
                }

                @Override
                public void onError(UiError uiError) {
                    toast("分享失败");
                }

                @Override
                public void onCancel() {
                    toast("分享被取消");
                }
            });
        }
    }

    @Override
    protected WebPresenter createPresenter() {
        return new WebPresenter();
    }

    @Override
    protected void initToolbar() {

        boolean isHideTitle = getIntent().getBooleanExtra(TITLE_HIDDEN_KEY, false);

        if (isHideTitle){
            showToolbar(false);
            StatusBarCompat.translucentStatusBar(this, true);
        }else {
            StatusBarCompat.setStatusBarColor(this, Color.WHITE);
            toolbar.setNavigationOnClickListener((v) -> {
                if (webType==WEB_TO_HOME){
                    startActivity(new Intent(this, MainTabActivity.class));
                    finish();
                }else {
                    onBackPressed();
                }
            });
            toolbar.setTitle("");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (webType==WEB_TO_HOME){
                startActivity(new Intent(this, MainTabActivity.class));
                finish();
            }else {
                if (web_view.canGoBack()) {
                    web_view.goBack();
                } else {
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private ShareDialog shareDialog;
    private String shareTitle;
    private String shareImage;
    private String shareSumtitle;
    private String shareUrl;

    @Override
    protected void onResume() {
        super.onResume();
        //流量活动刷新页面以获取cookie
//        if (getIntent().getBooleanExtra(IS_ACTIVE_URL, false)) {
//            web_view.reload();
//        }

        web_view.onResume();
        web_view.resumeTimers();
//        try {
//            web_view.getClass().getMethod("onResume").invoke(web_view,(Object[])null);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (showShare){
            getMenuInflater().inflate(R.menu.web_share, menu);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.web_item_share){
            Map<String, String> shareParam = new HashMap<>();
            shareParam.put("title", shareTitle);
            shareParam.put("summary", shareSumtitle);
            if (!TextUtils.isEmpty(shareImage)){
                shareParam.put("imageUrl", shareImage);
            }
            shareParam.put("url", shareUrl);

//            web_view.loadUrl("javascript:alert(gjObj.shareAct())"); //openMsgBox('对不起', '<span>您提交的信息有误，<br />请核实后再次尝试！</span>', true)
//            web_view.loadUrl("javascript:findProperty.putServerId(gjObj.shareAct())");
            shareDialog = new ShareDialog(this, shareParam, new ShareDialog.ShareProListener() {
                @Override
                public void proper(int position) {
                    web_view.loadUrl("javascript:findProperty.putServerId(gjObj.shareAct())");
                }
            });
            shareDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean urlDone(String url){
        if (!url.startsWith("http")){
            return true;
        }
        Logger.i("加载url:"+url);
        //http://sh.centanet.com/m/ershoufang/shpdx10647278.html
        if ((url.contains("http://sh.centanet.com/m/ershoufang")|| url.contains("https://sh.centanet.com/m/ershoufang"))
                && url.contains(".html")){
            String postId = url.substring(url.indexOf("ershoufang/")+11, url.indexOf(".html"));

            if (postId.length()<30){
                mPresenter.getHouseByAdsNo(postId);
            }else {
                Intent intent = new Intent(this, HouseDetail.class);
                intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
                intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, postId);
                startActivity(intent);
            }
            return true;
        }else if (url.contains("http://sh.centanet.com/m/xinfang/lp")||url.contains("https://sh.centanet.com/m/xinfang/lp")){
            String estExtId = url.substring(url.indexOf("lp-")+3, url.lastIndexOf("/"));
            Intent intent = new Intent(this, HouseDetail.class);
            intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
            intent.putExtra(HouseDetailFragment.ESTEXT_ID_KEY, estExtId);
            startActivity(intent);
            return true;
        }else if ((url.contains("http://sh.centanet.com/m/zufang")||url.contains("https://sh.centanet.com/m/zufang"))
                && url.contains(".html")){
            String postId = url.substring(url.indexOf("zufang/")+7, url.indexOf(".html"));
            if (postId.length()<30){
                mPresenter.getHouseByAdsNo(postId);
            }else {
                Intent intent = new Intent(this, HouseDetail.class);
                intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
                intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, postId);
                startActivity(intent);
            }
            return true;
        }else if (url.equals("http://sh.centanet.com/m/ershoufang/")||url.equals("https://sh.centanet.com/m/ershoufang/")){
            //到二手房列表
            Intent intent = new Intent(this, HouseList.class);
            intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
            startActivity(intent);
            finish();
            return true;
        }else if (url.equals("http://sh.centanet.com/m/zufang/")||url.equals("https://sh.centanet.com/m/zufang/")){
            Intent intent = new Intent(this, HouseList.class);
            intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
            startActivity(intent);
            return true;
        }else if (url.equals("http://sh.centanet.com/m/xinfang/")||url.equals("https://sh.centanet.com/m/xinfang/")){
            Intent intent = new Intent(this, HouseList.class);
            intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
            startActivity(intent);
            return true;
        }else if (url.contains("http://sh.centanet.com/m/jingjiren/") ||
                url.contains("https://sh.centanet.com/m/jingjiren/") ||
                url.contains("http://sh.centanet.com/jingjiren")){
            String staffNo = url.substring(url.indexOf("-a")+1, url.lastIndexOf("/"));
            mPresenter.getStaffInfo(staffNo);
            return true;
        }else if ("http://sh.centanet.com/m/".equalsIgnoreCase(url) || "https://sh.centanet.com/m/".equalsIgnoreCase(url)){
            Intent intent = new Intent(this, MainTabActivity.class);
            intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_HOME);
            startActivity(intent);
            this.finish();
            return true;
        }else if (url.equals("http://sh.centanet.com/m/xiaoqu/") ||
                url.equals("http://sh.centanet.com/xiaoqu/")||
                url.equals("https://sh.centanet.com/m/xiaoqu/") ||
                url.equals("https://sh.centanet.com/xiaoqu/")){

            Intent intent = new Intent(this, EstateList.class);
            startActivity(intent);
            this.finish();
            return true;
        }else if (url.contains("http://sh.centanet.com/m/xiaoqu/") ||
                url.contains("http://sh.centanet.com/xiaoqu/")||
                url.contains("https://sh.centanet.com/m/xiaoqu/") ||
                url.contains("https://sh.centanet.com/xiaoqu/")){
            String estateCode = url.substring(url.indexOf("xq-")+3, url.lastIndexOf("/"));
            Intent intent = new Intent(this, VillageDetail.class);
            intent.putExtra(VillageDetailFragment.ESTATE_CODE_KEY, estateCode);
            startActivity(intent);
            this.finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        //暂停WebView在后台的所有活动
        web_view.onPause();
        //暂停WebView在后台的JS活动
        web_view.pauseTimers();

//        try {
//            web_view.getClass().getMethod("onPause").invoke(web_view,  (Object[])null);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        web_view.removeJavascriptInterface("findProperty");
        web_view.destroy();
        web_view = null;
    }

    @Override
    public void finish() {
        //解决三星手机闪退问题
        if (Build.MANUFACTURER.equalsIgnoreCase("SAMSUN")) {
            ViewGroup view = (ViewGroup) getWindow().getDecorView();
            view.removeAllViews();
        }
        //评价业务员
        if (shareUrl.contains("lookrecommendhouse")) {
            RxBus.getDefault().send(new NormalEvent(NormalEvent.REFRESH_LOOK_LIST));
        }
        super.finish();
    }

    @Override
    public void setHouse(String postId, int houseType) {
        Intent intent = new Intent(this, HouseDetail.class);
        intent.putExtra(MapFragment.HOUSE_TYPE_KEY, houseType);
        intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, postId);
        startActivity(intent);
    }

    @Override
    public void setStaff(String staffNo, String staffName) {
        MyUtils.toStoreHome(this, staffNo, staffName);
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void dismissLoading() {
        cancelLoadingDialog();
    }

    @Override
    public void showError(String msg) {
        toast(msg);
    }

    @Override
    protected String getTalkingDataPageName() {
        return "网页浏览";
    }
}
