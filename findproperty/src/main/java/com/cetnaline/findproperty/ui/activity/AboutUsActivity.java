package com.cetnaline.findproperty.ui.activity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.IPresenter;

import butterknife.BindView;

/**
 * Created by diaoqf on 2016/8/22.
 */
public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.web_view)
    WebView web_view;

    @Override
    protected int getContentViewId() {
        return R.layout.act_about_us;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                toolbar.setTitle(title);
            }
        };
        web_view.setWebChromeClient(wvcc);
        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                web_view.loadUrl(url);
                return true;
            }
        };
        web_view.setWebViewClient(wvc);

        WebSettings webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);


        web_view.loadUrl("https://sh.centanet.com/abl/huodong/cpqh/AppPage/aboutus.html");
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }


    @Override
    protected void initToolbar() {
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    }

    @Override
    protected String getTalkingDataPageName() {
        return "关于我们";
    }
}
