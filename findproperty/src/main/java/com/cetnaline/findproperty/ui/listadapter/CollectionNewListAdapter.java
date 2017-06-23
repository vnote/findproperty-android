package com.cetnaline.findproperty.ui.listadapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.utils.MyUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by diaoqf on 2016/8/27.
 */
public class CollectionNewListAdapter extends CommonAdapter<NewHouseListBo> {
    private DrawableRequestBuilder<String> requestBuilder;
    private LayoutInflater inflater;
    public static final int IMG_WIDTH = 400;
    public static final int IMG_HEIGHT = 300;

    public CollectionNewListAdapter(Activity context, int layoutId, List<NewHouseListBo> datas) {
        super(context,datas ,layoutId);
        requestBuilder = GlideLoad.init(context);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void convert(ViewHolder helper, NewHouseListBo item) {
        String iconUrl = item.getIconUrl();
        iconUrl = iconUrl.substring(0, iconUrl.indexOf("."))+"_"+IMG_WIDTH+"x"+IMG_HEIGHT+".jpg";
        Logger.i("image url:"+iconUrl);

        ImageView item_small_img = helper.getView(R.id.post_img);
        GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.NEW_HOUSE_IMG+iconUrl)
                .into(item_small_img));
        helper.setText(R.id.post_title, item.getAdName());
        helper.setText(R.id.item_small_house, item.getEstType());
        if(item.getAveragePrice() <= 0) {
            helper.setText(R.id.item_small_money, "均价:暂无");
        } else {
            helper.setText(R.id.item_small_money, "均价:"+MyUtils.format2String((int) item.getAveragePrice()) + "元/㎡");
        }
    }
}
