package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cetnaline.findproperty.R;

/**
 * Created by diaoqf on 2016/8/11.
 */
public class SettingMenuItem extends LinearLayout {

    private TextView item_title;
    private TextView item_hint;
    private CircleImageView image;
    private RelativeLayout act_layout;
    private Context mContext;
    private OnItemClick onItemClick;

    public SettingMenuItem(Context context) {
        super(context);
        init(context,null);
    }

    public SettingMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.item_setting_menu, this, true);
        item_title = (TextView) findViewById(R.id.item_title);
        item_hint = (TextView) findViewById(R.id.item_hint);
        image = (CircleImageView) findViewById(R.id.image);
        act_layout = (RelativeLayout) findViewById(R.id.act_layout);

        act_layout.setOnClickListener((v)->{
            if (onItemClick != null) {
                onItemClick.onClick(view);
            }
        });

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingMenuItem);
        item_title.setText(a.getString(R.styleable.SettingMenuItem_menu_title));
        if (a.getBoolean(R.styleable.SettingMenuItem_show_image, false)) {
            item_hint.setVisibility(GONE);
            image.setVisibility(VISIBLE);
        }
        a.recycle();
    }

    public void setImage(String url) {
        Glide.with(mContext)
                .load(url)
                .error(R.drawable.user_default_portrait)
                .into(image);
    }

    public void setHintText(String text) {
        item_hint.setText(text);
    }
    public void setHintText(int text) {
        item_hint.setText(getResources().getString(text));
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick{
        void onClick(View v);
    }
}