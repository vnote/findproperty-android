package com.cetnaline.findproperty.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cetnaline.findproperty.R;

import java.util.List;

/**
 * Created by sunxl8 on 2016/8/22.
 * 搜索联想词Adapter
 */

public class SearchAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mList;
    private String keyword;

    public SearchAdapter(Context mContext, List<String> mList,String keyword) {
        this.mContext = mContext;
        this.mList = mList;
        this.keyword = keyword;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : (mList.size() > 11 ? 11 : mList.size());
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_autocomplete, parent, false);
            holder = new ViewHolder();
//            holder.textView = (TextView) convertView.findViewById(R.id.tv_search_autocomplete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String str = mList.get(position);
        if (str.contains(keyword)){
            String red = "<font color='red'>"+keyword+"</font>";
            str = str.replace(keyword,red);
        }
        holder.textView.setText(Html.fromHtml(str));
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
