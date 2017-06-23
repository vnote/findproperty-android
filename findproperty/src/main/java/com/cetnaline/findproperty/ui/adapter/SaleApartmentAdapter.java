package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.ApartmentBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/11.
 */
public class SaleApartmentAdapter extends CommonAdapter<ApartmentBo> {

	private DrawableRequestBuilder<String> requestBuilder;

	public SaleApartmentAdapter(Activity context, int layoutId, List<ApartmentBo> datas) {
		super(context, layoutId, datas);
		requestBuilder = GlideLoad.init(context);
	}

	@Override
	protected void convert(ViewHolder holder, ApartmentBo houseBo, int position) {

		ImageView nearby_item_img = holder.getView(R.id.nearby_item_img);

		String imageUrl = houseBo.getThumbImage();
		if (TextUtils.isEmpty(imageUrl)){
			ApiRequest.getNewHouseImages(houseBo.getHouseTypeId()+"")
					.subscribe(imageBo -> {

						String fileUrl = imageBo.get(0).getFileUrl();
						String url = NetContents.NEW_HOUSE_IMG+fileUrl.substring(0, fileUrl.indexOf("."))+"_1920x1080_f.jpg";
						String thumb = NetContents.NEW_HOUSE_IMG+fileUrl.substring(0, fileUrl.indexOf("."))+"_300x200_f.jpg";
						houseBo.setImageUrl(url);
						houseBo.setThumbImage(thumb);
						GlideLoad.load(new GlideLoad.Builder(requestBuilder, thumb)
								.into(nearby_item_img));

					}, throwable -> {

					});
		}else {
			GlideLoad.load(new GlideLoad.Builder(requestBuilder, imageUrl)
					.into(nearby_item_img));
		}

		holder.setText(R.id.nearby_item_address, houseBo.getHouseTypeName());
		holder.setText(R.id.nearby_item_info, houseBo.getRoomCnt()+"室"+houseBo.getHallCnt()+"厅 | "+ MyUtils.format2String(houseBo.getArea())+"㎡ | "+houseBo.getFitment() );
	}
}
