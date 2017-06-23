package com.cetnaline.findproperty.widgets.sharedialog;

import android.content.Context;

import com.cetnaline.findproperty.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;


/**
 * Created by diaoqf on 2016/8/9.
 */
public class ShareAdapter extends CommonAdapter<ShareDialog.ShareItem> {

    public ShareAdapter(Context context, int layoutId, List<ShareDialog.ShareItem> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, ShareDialog.ShareItem item, int position) {
        holder.setText(R.id.atv_title, item.getTitle());
        holder.setImageResource(R.id.img_icon, item.getImage());
    }
}
