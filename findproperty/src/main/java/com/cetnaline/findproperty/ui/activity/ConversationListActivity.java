package com.cetnaline.findproperty.ui.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.ConversationListFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;

import butterknife.BindView;


/**
 * Created by diaoqf on 2016/11/14.
 */

public class ConversationListActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.act_conversation_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
//        ConversationListFragment fragment = (ConversationListFragment) getSupportFragmentManager().findFragmentById(R.id.conversationlist);
//        fragment.showTitle(false);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ConversationListFragment chat = new ConversationListFragment();
        chat.setUri(Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversationlist").build());
        chat.showTitle(false);
        ft.replace(R.id.frame_layout, chat);
        ft.commit();
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        toolbar.setTitle("咨询");
    }

    @Override
    protected String getTalkingDataPageName() {
        return "会话列表";
    }
}
