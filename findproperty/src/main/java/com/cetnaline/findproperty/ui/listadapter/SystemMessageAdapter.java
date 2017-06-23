package com.cetnaline.findproperty.ui.listadapter;

import android.content.Context;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.bean.SystemMessageBean;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;
import com.cetnaline.findproperty.utils.DateUtil;

import java.util.List;

/**
 * Created by diaoqf on 2017/2/22.
 */

public class SystemMessageAdapter extends CommonAdapter<SystemMessageBean> {
    private Context mContext;
    private int color,color_normal;

    public SystemMessageAdapter(Context context, List<SystemMessageBean> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        mContext = context;
        color = mContext.getResources().getColor(R.color.list_hint_color);
        color_normal = mContext.getResources().getColor(R.color.black);
    }

    @Override
    public void convert(ViewHolder helper, SystemMessageBean item) {
        TextView title = helper.getView(R.id.title);
        TextView time = helper.getView(R.id.time);

        if (item.isIsRead()) {
            title.setTextColor(color);
            time.setTextColor(color);
        } else {
            title.setTextColor(color_normal);
            time.setTextColor(color_normal);
        }

        title.setText(item.getContent());
        long timestamp = Long.parseLong(item.getCreateTime().substring(6,item.getCreateTime().length()-7));
        time.setText(DateUtil.timeRule(timestamp));

    }
}
