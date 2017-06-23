package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.MyText;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 房源列表——小图模式Adapter
 * Created by fanxl2 on 2016/8/2.
 */
public class SmallPicListAdapter extends CommonAdapter<HouseBo> {

	private DrawableRequestBuilder<String> requestBuilder;
	private LayoutInflater inflater;
	private int houseType;

	public SmallPicListAdapter(Activity context, int layoutId, List<HouseBo> datas, int houseType) {
		super(context, layoutId, datas);
		this.houseType=houseType;
		requestBuilder = GlideLoad.init(context);
		inflater = LayoutInflater.from(context);
	}

	@Override
	protected void convert(ViewHolder holder, HouseBo house, int position) {

		ImageView item_small_img = holder.getView(R.id.item_small_img);
		String imgUri = house.getDefaultImage();
		if (TextUtils.isEmpty(imgUri)) {
			imgUri = AppContents.POST_DEFAULT_IMG_URL;
		}
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUri)
				.into(item_small_img));

		holder.setText(R.id.item_small_title, house.getTitle());
		holder.setText(R.id.item_small_house, house.getRoomCount()+"室"+house.getHallCount()+"厅 | "+MyUtils.formatHouseArea(house.getGArea())+"㎡ | "+house.getDirection());

		MyText item_small_money = holder.getView(R.id.item_small_money);

		if (houseType== MapFragment.HOUSE_TYPE_SECOND){
			item_small_money.setLeftAndRight(MyUtils.format2String(house.getSalePrice()/10000), "万");

//			holder.setText(R.id.item_small_est, house.getDisplayEstName());
//			holder.setText(R.id.item_small_price, MyUtils.format2String(house.getUnitSalePrice())+"元/㎡");

		}else {
			MyText item_rent_price = holder.getView(R.id.item_rent_price);
			item_rent_price.setLeftAndRight(MyUtils.format2String(house.getRentPrice()), "元/月");
//			holder.setText(R.id.item_small_fitment, house.getFitment());
//			holder.setText(R.id.item_small_keys, house.getRegionName()+"  "+house.getGscopeName()+"  |  "+house.getEstateName());
		}
		LinearLayout item_big_keys = holder.getView(R.id.item_small_keys);
		item_big_keys.removeAllViews();

		if (house.getKeyWords()!=null && !TextUtils.isEmpty(house.getKeyWords())){
			String[] keyWords = house.getKeyWords().split(",");

			int length = keyWords.length;
			if (length>3){
				length = 3;
			}

			for (int i=0; i<length; i++){
				TextView key = (TextView) inflater.inflate(R.layout.item_key_text, item_big_keys, false);
				key.setText(keyWords[i]);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) key.getLayoutParams();
				params.setMargins(0, 0, 20, 0);
				item_big_keys.addView(key);
			}
		}


	}
}
