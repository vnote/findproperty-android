package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 房源列表——大图模式Adapter
 * Created by fanxl2 on 2016/8/2.
 */
public class BigNewListAdapter extends CommonAdapter<NewHouseListBo> {

	private DrawableRequestBuilder<String> requestBuilder;

	public BigNewListAdapter(Activity context, int layoutId, List<NewHouseListBo> datas) {
		super(context, layoutId, datas);
		requestBuilder = GlideLoad.init(context);
//		inflater = LayoutInflater.from(context);
	}

	@Override
	protected void convert(ViewHolder holder, NewHouseListBo house, int position) {

		String iconUrl = house.getIconUrl();
		iconUrl = iconUrl.substring(0, iconUrl.indexOf("."))+"_"+NetContents.HOUSE_BIG_IMAGE_LIST_WIDTH+"x"+NetContents.HOUSE_BIG_IMAGE_LIST_HEIGHT+"_f.jpg";

		ImageView item_big_img = holder.getView(R.id.item_big_img);
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.NEW_HOUSE_IMG+iconUrl)
				.into(item_big_img));


		holder.setText(R.id.item_big_title, house.getAdName());

		holder.setText(R.id.item_big_house, house.getEstType()+" | "+("认购".equals(house.getStatus()) || "持销".equals(house.getStatus()) || "尾盘".equals(house.getStatus()) ? "在售" : "待售"));

		if (house.getAveragePrice()>0){
			holder.setText(R.id.item_big_money, "均价: "+ MyUtils.format2String(house.getAveragePrice())+"元/㎡");
		}else {
			holder.setText(R.id.item_big_money, "均价: 暂无");
		}

		LinearLayout item_big_keys = holder.getView(R.id.item_big_keys);
		item_big_keys.removeAllViews();

//		if (house.getKeyWords()!=null){
//			String[] keyWords = house.getKeyWords().split(",");
//
//			int length = keyWords.length;
//			if (length>5){
//				length = 5;
//			}
//
//			for (int i=0; i<length; i++){
//				TextView key = (TextView) inflater.inflate(R.layout.item_key_text, item_big_keys, false);
//				key.setText(keyWords[i]);
//				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) key.getLayoutParams();
//				params.setMargins(0, 0, 10, 0);
//				item_big_keys.addView(key);
//			}
//		}
	}
}
