package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.utils.glide.GlideLoad;

import java.util.List;

/**
 * 房源列表——小图模式Adapter
 * Created by fanxl2 on 2016/8/2.
 */
public class SmallNewListAdapter extends CommonAdapter<NewHouseListBo> {

	private DrawableRequestBuilder<String> requestBuilder;
	private LayoutInflater inflater;
	public static final int IMG_WIDTH = 400;
	public static final int IMG_HEIGHT = 300;

	public SmallNewListAdapter(Activity context, int layoutId, List<NewHouseListBo> datas) {
		super(context,datas ,layoutId);
		requestBuilder = GlideLoad.init(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void convert(com.cetnaline.findproperty.ui.adapter.ViewHolder helper, NewHouseListBo item) {
		String iconUrl = item.getIconUrl();
		iconUrl = iconUrl.substring(0, iconUrl.indexOf("."))+"_"+IMG_WIDTH+"x"+IMG_HEIGHT+".jpg";

		ImageView item_small_img = helper.getView(R.id.item_small_img);
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, iconUrl)
				.into(item_small_img));

		helper.setText(R.id.item_small_title, item.getAdName());

		helper.setText(R.id.item_small_house, "酒店式公寓 | "+"在售");

		helper.setText(R.id.item_small_money, "均价: 15485万");

		LinearLayout item_big_keys = helper.getView(R.id.item_small_keys);
		item_big_keys.removeAllViews();
	}
}
