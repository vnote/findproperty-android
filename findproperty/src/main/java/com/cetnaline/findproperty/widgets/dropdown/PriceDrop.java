package com.cetnaline.findproperty.widgets.dropdown;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.widgets.RangeSeekBar;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * 价格单选
 * Created by fanxl2 on 2016/8/4.
 */
public class PriceDrop extends AbsDrop<DropBo> {

	private LinearLayout fl_parent;
	private ListView listView;
	private DropTextAdapter dropTextAdapter;
	private RangeSeekBar drop_price_rb;
	private int houseType;
	private int minPrice, maxPrice;
	private TextView drop_price_range;

	public PriceDrop(View anchor, Activity context, Drawable bg) {
		super(anchor, context);
		View view;
		if (bg==null){
			view = LayoutInflater.from(context).inflate(R.layout.layout_drop_price, null);
		}else {
			view = LayoutInflater.from(context).inflate(R.layout.layout_drop_price_map, null);
		}
		fl_parent = (LinearLayout) view.findViewById(R.id.fl_parent);
		fl_parent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dropTextAdapter.select(position);
//				dropComplete(false, arrayList.get(position).getType(), arrayList.get(position));
				dropComplete(false, 0, arrayList.get(position));
			}
		});

		view.findViewById(R.id.drop_price_ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DropBo dropBo = new DropBo();
				dropBo.setPara("p0");
				if (houseType==MapFragment.HOUSE_TYPE_SECOND){
					dropBo.setValue(minPrice*10000+","+maxPrice*10000);
					dropBo.setText(minPrice+"-"+maxPrice+"万");
					dropBo.setName("Sell");
				}else if(houseType==MapFragment.HOUSE_TYPE_RENT){
					dropBo.setValue(minPrice+","+maxPrice);
					dropBo.setText(minPrice+"-"+maxPrice+"元");
					dropBo.setName("Rent");
				}else {
					dropBo.setValue(minPrice+","+maxPrice);
					dropBo.setText(minPrice+"-"+maxPrice+"元");
					dropBo.setName("NewHousePriceN");
				}
				dropComplete(false, 0, dropBo);
			}
		});

		drop_price_range = (TextView) view.findViewById(R.id.drop_price_range);

		drop_price_rb = (RangeSeekBar) view.findViewById(R.id.drop_price_rb);
		drop_price_rb.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
			@Override
			public void onRangeChanged(RangeSeekBar view, float min, float max) {
				minPrice = (int)min;
				maxPrice = (int)max;
				if (houseType== MapFragment.HOUSE_TYPE_SECOND){
					drop_price_range.setText("总价:"+ minPrice+"万 —— "+maxPrice+"万");
				}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
					drop_price_range.setText("租金:"+ minPrice+"元 —— "+maxPrice+"元");
				}else {
					drop_price_range.setText("均价:"+ minPrice+"元 —— "+maxPrice+"元");
				}
			}
		});

		view.findViewById(R.id.price_ll_bottom).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Logger.i("消耗点事件");
			}
		});

		dropTextAdapter = new DropTextAdapter(context, arrayList, R.layout.item_drop_single);
		listView.setAdapter(dropTextAdapter);
		initPopWindow(view, bg);
	}

	public void init(List<DropBo> dropBos, int houseType){
		this.houseType = houseType;
		if (houseType==MapFragment.HOUSE_TYPE_SECOND){
			drop_price_rb.setRules(0, 2000, 100, 1);
			drop_price_rb.setValue(0, 2000);
			maxPrice = 2000;
			drop_price_range.setText("总价: 0万 —— 2000万");
		}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
			drop_price_rb.setRules(0, 20000, 100, 1);
			drop_price_rb.setValue(0, 20000);
			maxPrice = 20000;
			drop_price_range.setText("租金: 0元 —— 20000元");
		}else if (houseType==MapFragment.HOUSE_TYPE_ESTATE){
			drop_price_rb.setRules(0, 100000, 100, 1);
			drop_price_rb.setValue(0, 100000);
			maxPrice = 100000;
			drop_price_range.setText("均价: 0元 —— 100000元");
		}else {
			drop_price_rb.setRules(0, 10000, 100, 1);
			drop_price_rb.setValue(0, 10000);
			maxPrice = 10000;
			drop_price_range.setText("均价: 0元 —— 10000元");
		}
		init(dropBos);
	}

	@Override
	public void init(List<DropBo> dropBos) {
		arrayList.clear();
		arrayList.addAll(dropBos);
		dropTextAdapter.select(-1);
	}


	public void resetSelectStatus(){
		dropTextAdapter.select(-1);
	}
}
