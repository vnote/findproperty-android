package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.ui.activity.HouseList;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.cetnaline.findproperty.widgets.MyText;
import com.cetnaline.findproperty.widgets.fullList.NestFullListView;
import com.cetnaline.findproperty.widgets.fullList.NestFullListViewAdapter;
import com.cetnaline.findproperty.widgets.fullList.NestFullViewHolder;
import com.jakewharton.rxbinding.view.RxView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 房源列表——大图模式Adapter
 * Created by fanxl2 on 2016/8/2.
 */
public class BigPicListAdapter extends CommonAdapter<HouseBo> {

	private DrawableRequestBuilder<String> requestBuilder;
	private int houseType;
	private Activity activity;

	public BigPicListAdapter(Activity context, int layoutId, List<HouseBo> datas, int houseType) {
		super(context, layoutId, datas);
		this.houseType=houseType;
		this.activity=context;
		requestBuilder = GlideLoad.init(context);
	}

	@Override
	protected void convert(ViewHolder holder, HouseBo house, int position) {

		ImageView item_big_img = holder.getView(R.id.item_big_img);
		String imgUri = house.getDefaultImage();
		if (TextUtils.isEmpty(imgUri)) {
			imgUri = AppContents.POST_DEFAULT_IMG_URL;
		}
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUri)
				.into(item_big_img));

		CircleImageView item_big_head = holder.getView(R.id.item_big_head);
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.STAFF_HEAD_HOST + house.getStaffNo()+".jpg")
				.error(R.drawable.mine_default)
				.placeHolder(R.drawable.mine_default)
				.into(item_big_head));

		RxView.clicks(item_big_head)
				.throttleFirst(1000, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					if (mContext instanceof HouseList) {
						HouseList activity = (HouseList) mContext;
						if (!activity.isShadeShow()) {
							activity.setSelectItem(house);
							activity.showShade(new View[]{item_big_head});
						} else {
							activity.hideShade();
						}
					}
				});

		holder.setText(R.id.item_big_title, house.getTitle());
		holder.setText(R.id.item_big_house, house.getRoomCount()+"室"+house.getHallCount()+"厅 | "+MyUtils.formatHouseArea(house.getGArea())+"㎡ | "+house.getDirection());

		MyText item_big_money = holder.getView(R.id.item_big_money);

		if (houseType== MapFragment.HOUSE_TYPE_SECOND){
			item_big_money.setLeftAndRight(MyUtils.format2String(house.getSalePrice()/10000), "万");
			holder.setText(R.id.item_big_est, house.getDisplayEstName());
			holder.setText(R.id.item_big_price, MyUtils.format2String(house.getUnitSalePrice())+"元/㎡");

		}else {
			holder.setText(R.id.item_big_fitment, house.getFitment());
//			holder.setText(R.id.item_big_est, house.getRegionName()+"  "+house.getGscopeName()+"  |  "+house.getEstateName());
			item_big_money.setLeftAndRight(MyUtils.format2String(house.getRentPrice()), "元/月");
		}

		NestFullListView item_big_keys = holder.getView(R.id.item_big_keys);

		if (house.getKeyWords()!=null && !TextUtils.isEmpty(house.getKeyWords())){
			String[] keyWords = house.getKeyWords().trim().split(",");
			if (keyWords==null || keyWords.length<1)return;

			int index = keyWords.length;

			List<String> keyWordList = new ArrayList<>();

			StringBuffer sb = new StringBuffer();
			for (int i=0; i<index; i++){
				sb.append(keyWords[i]);
				if (sb.length()<16){
					keyWordList.add(keyWords[i]);
				}
			}

			item_big_keys.setAdapter(new NestFullListViewAdapter<String>(R.layout.item_key_text, keyWordList) {

				@Override
				public void onBind(int pos, String item, NestFullViewHolder holder) {
					TextView key = (TextView) holder.getConvertView();
					key.setText(item);
				}
			});


//				for (int i=0; i<length; i++){
//					TextView key = (TextView) inflater.inflate(R.layout.item_key_text, item_big_keys, false);
//					key.setText(keyWords[i]);
//					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) key.getLayoutParams();
//					params.setMargins(0, 0, 10, 0);
//					item_big_keys.addView(key);
//				}

		}else {
			item_big_keys.setVisibility(View.INVISIBLE);
		}
	}
}
