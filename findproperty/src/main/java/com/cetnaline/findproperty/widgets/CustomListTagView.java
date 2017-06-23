package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.cetnaline.findproperty.R;

import java.util.ArrayList;

/**
 * 列表标签
 */
public class CustomListTagView extends LinearLayout {

    private AppCompatTextView atv_list_tag_fir, atv_list_tag_sec, atv_list_tag_thi;

    public CustomListTagView(Context context) {
        this(context, null);
    }

    public CustomListTagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomListTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.layout_tags_list, this);
        atv_list_tag_fir = (AppCompatTextView) findViewById(R.id.atv_list_tag_fir);
        atv_list_tag_sec = (AppCompatTextView) findViewById(R.id.atv_list_tag_sec);
        atv_list_tag_thi = (AppCompatTextView) findViewById(R.id.atv_list_tag_thi);
    }

    /**
     * 设置标签
     *
     * @param tags  标签
     * @param split 分隔符
     */
    public void setTags(String tags, String split) {
        if (TextUtils.isEmpty(tags)) {
            atv_list_tag_fir.setVisibility(GONE);
            atv_list_tag_sec.setVisibility(GONE);
            atv_list_tag_thi.setVisibility(GONE);
        } else if (tags.contains(split)) {
            String[] tagStrings = tags.split(split);
            final int size = tagStrings.length;
            for (int i = 0; i < 3; i++) {
                setTag(i, i < size ? tagStrings[i] : "");
            }
        } else {
            atv_list_tag_fir.setText(tags);
            atv_list_tag_fir.setVisibility(VISIBLE);
            atv_list_tag_sec.setVisibility(GONE);
            atv_list_tag_thi.setVisibility(GONE);
        }
    }

    /**
     * 设置标签
     *
     * @param arrayList 标签
     */
    public void setTags(ArrayList<String> arrayList) {
        setTags(arrayList, true);
    }

    /**
     * 设置标签
     *
     * @param arrayList 标签
     */
    public void setTags(ArrayList<String> arrayList, boolean online) {
        if (arrayList == null || arrayList.size() == 0) {
            atv_list_tag_fir.setVisibility(GONE);
            atv_list_tag_sec.setVisibility(GONE);
            atv_list_tag_thi.setVisibility(GONE);
            setVisibility(GONE);
        } else {
            atv_list_tag_fir.setEnabled(online);
            atv_list_tag_sec.setEnabled(online);
            atv_list_tag_thi.setEnabled(online);
            final int size = arrayList.size();
            for (int i = 0; i < 3; i++) {
                setTag(i, i < size ? arrayList.get(i) : "");
            }
            setVisibility(VISIBLE);
        }
    }

    /**
     * 设置标签
     */
    private void setTag(int position, String tag) {
        switch (position) {
            case 0:
                atv_list_tag_fir.setText(tag);
                atv_list_tag_fir.setVisibility(TextUtils.isEmpty(tag) ? GONE : VISIBLE);
                break;
            case 1:
                atv_list_tag_sec.setText(tag);
                atv_list_tag_sec.setVisibility(TextUtils.isEmpty(tag) ? GONE : VISIBLE);
                break;
            case 2:
                atv_list_tag_thi.setText(tag);
                atv_list_tag_thi.setVisibility(TextUtils.isEmpty(tag) ? GONE : VISIBLE);
                break;
            default:
                break;
        }
    }

}