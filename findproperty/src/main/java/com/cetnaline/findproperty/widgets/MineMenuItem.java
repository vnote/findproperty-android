package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;


/**
 * Created by diaoqf on 2016/8/11.
 */
public class MineMenuItem extends LinearLayout {

    private ImageView item_img;
    private TextView item_title;
    private TextView item_hint;
    private RelativeLayout action_layout;
    private OnItemClick onItemClick;
    private ImageView item_arrow;
    private TextView item_hint_text;
    private Context context;

    private Drawable left_img_normal;
    private Drawable left_img_sel;

    private int hintCount;

    private boolean enable;

    public MineMenuItem(Context context) {
        super(context);
        init(context,null);
    }

    public MineMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        enable = false;
        hintCount = 0;
        View view = LayoutInflater.from(context).inflate(R.layout.item_mine_menu, this, true);
        item_img = (ImageView) findViewById(R.id.item_img);
        item_title = (TextView) findViewById(R.id.item_title);
        item_hint = (TextView) findViewById(R.id.item_hint);
        item_arrow = (ImageView) findViewById(R.id.item_arrow);
        item_hint_text = (TextView) findViewById(R.id.item_hint_text);
        action_layout = (RelativeLayout) findViewById(R.id.action_layout);

        view.setOnClickListener((v)->{
            if (onItemClick != null) {
                onItemClick.onClick(view);
            }
        });

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MineMenuItem);
        left_img_normal = getResources().getDrawable(a.getResourceId(R.styleable.MineMenuItem_left_img,R.drawable.mine_note));
        left_img_sel = getResources().getDrawable(a.getResourceId(R.styleable.MineMenuItem_left_sel_img,R.drawable.mine_note));
        item_img.setImageDrawable(left_img_normal);
        item_title.setText(a.getString(R.styleable.MineMenuItem_title));
        item_hint.setVisibility(a.getBoolean(R.styleable.MineMenuItem_show_hint,false) ? VISIBLE : GONE);
        boolean show_arrow = a.getBoolean(R.styleable.MineMenuItem_show_arrow,true);
        if (show_arrow) {
            item_arrow.setVisibility(VISIBLE);
        } else {
            item_arrow.setVisibility(INVISIBLE);
            item_arrow.getLayoutParams().width = 0;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1,RelativeLayout.LayoutParams.WRAP_CONTENT);
//            item_arrow.setLayoutParams(params);
        }
        a.recycle();
    }

    /**
     * 设置消息数量
     * @param number
     */
    public void setHint(int number) {
        hintCount = number;
        if (number <= 0) {
            item_hint.setVisibility(GONE);
        } else {
            item_hint.setVisibility(VISIBLE);
            if (number > 99) {
                item_hint.setText("...");
            } else {
                item_hint.setText(number + "");
            }
        }
    }

    /**
     * 显示隐藏hint
     * @param flag
     */
    public void showHint(boolean flag) {
        if (flag) {
            item_hint.setVisibility(VISIBLE);
        } else {
            item_hint.setVisibility(GONE);
        }
    }

    public void enableMenu(boolean enable,String msg) {
        this.enable = enable;
        item_hint.setVisibility(GONE);
        item_hint_text.setText(msg);
        item_hint_text.setVisibility(VISIBLE);
        if (enable) {
            item_hint_text.setCompoundDrawables(null,null,null,null);
            item_hint_text.setTextColor(getResources().getColor(R.color.etHintColor));
            item_img.setImageDrawable(left_img_sel);
            item_arrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_ok));
        } else {
            Drawable drawable= getResources().getDrawable(R.drawable.ic_add);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            item_hint_text.setCompoundDrawables(drawable,null,null,null);
            item_hint_text.setTextColor(getResources().getColor(R.color.grayText));
            item_img.setImageDrawable(left_img_normal);
            item_arrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_arrow_gray_24dp));
        }
    }

    /**
     * 是否启用
     * @return
     */
    public boolean isEnable(){
        return enable;
    }

    public int getHintCount() {
        return hintCount;
    }

    /**
     * 是否显示hint
     * @return
     */
    public boolean isShowHint(){
        return item_hint.getVisibility() == VISIBLE;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick{
        void onClick(View v);
    }

}
