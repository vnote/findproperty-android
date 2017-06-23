package com.cetnaline.findproperty.ui.listadapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.utils.MyUtils;

import java.util.List;

/**
 * Created by diaoqf on 2016/8/20.
 */
public class CollectionEstateAdapter extends CommonAdapter<EstateBo> {
    private List<EstateBo> list;

    private DrawableRequestBuilder<String> requestBuilder;

    private int rowLayout;
    private Activity mContext;

    public CollectionEstateAdapter(Context context, List<EstateBo> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        this.list = mDatas;
        this.rowLayout = itemLayoutId;
        this.mContext = (Activity) context;
        requestBuilder = GlideLoad.init(mContext);
    }

    @Override
    public void convert(ViewHolder helper, EstateBo item) {
        helper.setText(R.id.estate_title,item.getEstateName());
        helper.setText(R.id.estate_scope1,item.getRegionName() + " | ");
        helper.setText(R.id.estate_scope2,item.getGscopeName());
        ImageView img = helper.getView(R.id.estate_img);
        String imgUri = item.getImagePath();
        if (TextUtils.isEmpty(imgUri)) {
            imgUri = AppContents.EST_DEFAULT_IMG_URL;
        } else {
            imgUri = NetContents.IMG_BASE_URL + imgUri + "_200x200" + item.getImageDestExt();
        }
        GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUri)
                .into(img));
        helper.setText(R.id.estate_avg_price, MyUtils.format2String((int)item.getSaleAvgPrice()));
    }
}
