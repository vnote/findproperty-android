package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.ui.adapter.WrapAdapter;


/**
 * 自定义RecyclerView
 * Created by shengl on 2015/11/11.
 */
public class MRecyclerView extends AbstractRecyclerView {

    public MRecyclerView(Context context) {
        this(context, null);
    }

    public MRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 添加适配器
     * @param adapter RecyclerView.Adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        setWrapAdapter(new WrapAdapter(adapter, "没有更多了"));
    }

    public void setAdapter(RecyclerView.Adapter adapter, String moreText) {
        setWrapAdapter(new WrapAdapter(adapter, moreText));
    }

    public void setAdapter(RecyclerView.Adapter adapter, String moreText, int marginBottom) {
        setWrapAdapter(new WrapAdapter(adapter, moreText, marginBottom));
    }

    @Override
    protected int getLogo() {
        return R.drawable.ic_splash_logo;
    }

    @Override
    protected int getSwipeRefreshLayoutColorSchemeResources() {
        return R.color.appBaseColor;
    }
}
