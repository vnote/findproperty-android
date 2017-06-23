package com.cetnaline.findproperty.ui.listadapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.utils.MyUtils;

import java.util.List;


/**
 * Created by diaoqf on 2016/8/19.
 */
public class CollectionAdapter extends CommonAdapter<HouseBo> {
    private List<HouseBo> list;

    private DrawableRequestBuilder<String> requestBuilder;

    private int rowLayout;
    private Activity mContext;

    public CollectionAdapter(Context context, List<HouseBo> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        this.list = mDatas;
        this.rowLayout = itemLayoutId;
        this.mContext = (Activity) context;
        requestBuilder = GlideLoad.init(mContext);
    }

    @Override
    public void convert(ViewHolder helper, HouseBo item) {
        ImageView img = helper.getView(R.id.post_img);
        //显示已下架
        if (!item.isIsOnline()) {
            helper.getView(R.id.img_cover).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.img_cover).setVisibility(View.GONE);
        }
        helper.setText(R.id.post_title, item.getTitle());
        String imgUri = item.getDefaultImage();
        if (TextUtils.isEmpty(imgUri)) {
            imgUri = AppContents.POST_DEFAULT_IMG_URL;
        }
        GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUri)
                .into(img));

        helper.setText(R.id.item_small_house, item.getRoomCount()+"室"+item.getHallCount()+"厅 | "+MyUtils.formatHouseArea(item.getGArea())+"㎡ | "+item.getDirection());

        if ("S".equals(item.getPostType())) {
            helper.setText(R.id.item_small_money, (MyUtils.format2String((int) item.getSalePrice() / 10000)));
            helper.setText(R.id.unit,"万");
        } else {
            helper.setText(R.id.item_small_money, MyUtils.format2String((int)item.getRentPrice()));
            helper.setText(R.id.unit,"元/月");
        }
    }
}
