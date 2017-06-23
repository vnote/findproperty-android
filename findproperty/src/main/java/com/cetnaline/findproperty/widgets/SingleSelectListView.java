package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cetnaline.findproperty.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by diaoqf on 2016/12/9.
 */

public class SingleSelectListView extends ListView {
    private SingleSelectListViewAdapter adapter;
    private OnSelectItemListener onSelectItemListener;

    private boolean hasImage = true;

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public void setOnSelectItemListener(OnSelectItemListener onSelectItemListener) {
        this.onSelectItemListener = onSelectItemListener;
    }

    public SingleSelectListView(Context context) {
        super(context);
        init(context,null);
    }

    public SingleSelectListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        adapter = new SingleSelectListViewAdapter(context, new ArrayList<>());
        setAdapter(adapter);
    }

    public void setData(List<String> data) {
        adapter.setHasImage(hasImage);
        adapter.setSelectedItem(-1);
        adapter.setData(data);
        setOnItemClickListener((parent, view, position, id) -> {
            adapter.setSelectedItem(position);
            if (onSelectItemListener != null) {
                onSelectItemListener.selectedItem(position);
            }
        });
    }

    public interface OnSelectItemListener{
        void selectedItem(int position);
    }


    public static class SingleSelectListViewAdapter extends BaseAdapter {
        private List<String> data;
        private Context context;
        private boolean hasImage;

        private int lastPosition = -1;

        public void setHasImage(boolean hasImage) {
            this.hasImage = hasImage;
        }

        public void setData(List<String> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        public void setSelectedItem(int index) {
            lastPosition = index;
            notifyDataSetChanged();;
        }

        public SingleSelectListViewAdapter(Context context, List<String> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder = null;
            if (convertView == null) {
                holder = new ItemHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.single_select_listview_item,null);
                holder.textView = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }

            holder.textView.setText(data.get(position));
            if (position == lastPosition) {
                holder.textView.setTextColor(context.getResources().getColor(R.color.appBaseColor));
                if (hasImage) {
                    holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_right_arrow_red_sel), null);
                }
            } else {
                holder.textView.setTextColor(context.getResources().getColor(R.color.normalText));
                if (hasImage) {
                    holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_right_arrow_gray_sel), null);
                }
            }

            return convertView;
        }

        public static class ItemHolder {
            public TextView textView;
        }
    }
}
