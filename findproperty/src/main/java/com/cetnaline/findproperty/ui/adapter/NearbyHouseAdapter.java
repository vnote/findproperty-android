package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.MyText;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/11.
 */
public class NearbyHouseAdapter extends CommonAdapter<HouseBo> {

	private DrawableRequestBuilder<String> requestBuilder;
	private int houseType;

	public NearbyHouseAdapter(Activity context, int layoutId, List<HouseBo> datas, int houseType) {
		super(context, layoutId, datas);
		requestBuilder = GlideLoad.init(context);
		this.houseType=houseType;
	}

	@Override
	protected void convert(ViewHolder holder, HouseBo houseBo, int position) {

		ImageView nearby_item_img = holder.getView(R.id.nearby_item_img);
		String imgUri;
		if (TextUtils.isEmpty(houseBo.getDefaultImage())) {
			imgUri = AppContents.POST_DEFAULT_IMG_URL;
		} else {
			imgUri = NetContents.IMG_BASE_URL +houseBo.getDefaultImage()+"_300x200_f"+houseBo.getDefaultImageExt();
		}

		GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUri)
				.into(nearby_item_img));

		holder.setText(R.id.nearby_item_title, houseBo.getTitle());
		String desString = houseBo.getRoomCount()+"室"+houseBo.getHallCount()+"厅 | "+ MyUtils.formatHouseArea(houseBo.getGArea())+"㎡ ";
		if (!TextUtils.isEmpty(houseBo.getDirection())) {
			desString += "|" + houseBo.getDirection();
		}
		holder.setText(R.id.nearby_item_info, desString);

		MyText nearby_item_price = holder.getView(R.id.nearby_item_price);

		if (houseType== MapFragment.HOUSE_TYPE_SECOND){
			nearby_item_price.setLeftAndRight(MyUtils.format2String(houseBo.getSalePrice()/10000), "万");
		}else {
			nearby_item_price.setLeftAndRight(MyUtils.format2String(houseBo.getRentPrice()), "元/月");
		}

	}
}
