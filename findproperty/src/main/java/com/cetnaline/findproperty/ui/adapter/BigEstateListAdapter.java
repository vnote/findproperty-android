package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.utils.DateUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.MyText;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 房源列表——大图模式Adapter
 * Created by fanxl2 on 2016/8/2.
 */
public class BigEstateListAdapter extends CommonAdapter<EstateBo> {

	private DrawableRequestBuilder<String> requestBuilder;

	public BigEstateListAdapter(Activity context, int layoutId, List<EstateBo> datas) {
		super(context, layoutId, datas);
		requestBuilder = GlideLoad.init(context);
	}

	@Override
	protected void convert(ViewHolder holder, EstateBo estate, int position) {

		ImageView item_estate_img = holder.getView(R.id.item_estate_img);
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, estate.getImageFullPath())
				.into(item_estate_img));

		holder.setText(R.id.item_estate_name, estate.getEstateName());

		StringBuffer sb = new StringBuffer();
		if (estate.getSaleNumber()>0){
			sb.append("在售: "+estate.getSaleNumber()+"套  ");
		}
		if (estate.getRentNumber()>0){
			sb.append("在租: "+estate.getRentNumber()+"套  ");
		}
		holder.setText(R.id.item_estate_info, sb.toString());

		long time = estate.getOpDate()*1000L;

		holder.setText(R.id.item_estate_location, estate.getRegionName()+" "+ estate.getGscopeName() + " | "
			+ DateUtil.format(time, DateUtil.FORMAT3)+"年建成");

		MyText item_estate_price = holder.getView(R.id.item_estate_price);
		item_estate_price.setLeftText(MyUtils.format2String(estate.getSaleAvgPrice()));
	}
}
