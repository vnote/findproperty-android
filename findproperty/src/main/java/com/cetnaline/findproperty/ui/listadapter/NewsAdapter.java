package com.cetnaline.findproperty.ui.listadapter;

import android.content.Context;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.bean.CollectInfoChangeBean;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;
import com.cetnaline.findproperty.utils.DateUtil;
import com.cetnaline.findproperty.utils.MyUtils;

import java.util.List;

/**
 * Created by diaoqf on 2016/8/23.
 */
public class NewsAdapter extends CommonAdapter<CollectInfoChangeBean> {

    private Context mContext;
    private int color,color_normal;
    public NewsAdapter(Context context, List<CollectInfoChangeBean> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        mContext = context;
        color = mContext.getResources().getColor(R.color.list_hint_color);
        color_normal = mContext.getResources().getColor(R.color.black);
    }

    @Override
    public void convert(ViewHolder helper, CollectInfoChangeBean item) {
        TextView title = helper.getView(R.id.title);
        TextView time = helper.getView(R.id.time);
        if (item.IsRead) {
            title.setTextColor(color);
            time.setTextColor(color);
        } else {
            title.setTextColor(color_normal);
            time.setTextColor(color_normal);
        }

        String tag_string = item.postTitle;
        if (tag_string == null) {
            tag_string = "房源";
        }

        if ("chengjiao".equals(item.ChangeType)) {
            title.setText("您关注的"+tag_string+"有成交信息,请注意查看");
        } else if ("online".equals(item.ChangeType)) {
            if ("0".equals(item.ChangeMsg1) || "false".equals(item.ChangeMsg1)) {
                title.setText("您关注的"+tag_string+"已下架");
            } else {
                title.setText("您关注的"+tag_string+"有新的上架信息");
            }
        } else if ("pricechange".equals(item.ChangeType)) {
            int price = Integer.parseInt(item.ChangeMsg1);
            int price_change = Integer.parseInt(item.ChangeMsg2);
            if(price_change > 0) {
                title.setText("您收藏的"+tag_string+"已涨价" + MyUtils.format2String(price_change) + "元，点击查看！");
            } else {
                title.setText("您收藏的"+tag_string+"已降价" + MyUtils.format2String(Math.abs(price_change)) + "元，点击查看！");
            }
        } else if ("estcount".equals(item.ChangeType)) {
            int count1 = Integer.parseInt(item.ChangeMsg1);
            int count2 = Integer.parseInt(item.ChangeMsg2);
            int resultCount = count2-count1;
            if (resultCount > 0) {
                title.setText("您收藏的" + tag_string + "有" + resultCount + "个新房源上架，点击查看！");
            } else {
                title.setText("您收藏的" + tag_string + "有" + -resultCount + "个新房源下架，点击查看！");
            }
        }
        time.setText(DateUtil.timeRule(item.UpdateTime2 * 1000));
    }
}
