package com.cetnaline.findproperty.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.AbstractViewPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.cetnaline.findproperty.R.id.intent_bg_next;

/**
 * Created by fanxl2 on 2016/11/3.
 */

public class HouseChoiceFragment extends BaseFragment {

	public static HouseChoiceFragment getInstance(int houseType){
		HouseChoiceFragment fragment = new HouseChoiceFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(MapFragment.HOUSE_TYPE_KEY, houseType);
		fragment.setArguments(bundle);
		return fragment;
	}

	@BindView(R.id.choice_house_vp)
	ViewPager choice_house_vp;

	private List<HouseItemBean> datas;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_house_choice;
	}

	@Override
	protected void init() {
		houseType  = getArguments().getInt(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
		choice_house_vp.setOffscreenPageLimit(1);
		choice_house_vp.setPageMargin(100);

//		choice_house_vp.setPageTransformer(true, new ScalePageTransformer());

		datas = new ArrayList<>();
		datas.add(new HouseItemBean("二手房", R.drawable.ic_second_house_img, MapFragment.HOUSE_TYPE_SECOND));
		datas.add(new HouseItemBean("租房", R.drawable.ic_rent_house_img, MapFragment.HOUSE_TYPE_RENT));

		GuideAdapter adapter = new GuideAdapter(datas);
		choice_house_vp.setAdapter(adapter);
		choice_house_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				houseType = datas.get(position).getHouseType();
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		choice_house_vp.setCurrentItem(houseType);
	}

	private int houseType;

	@OnClick(intent_bg_next)
	public void nextClick(){
		HashMap<String, SearchParam> data = new HashMap<>();
		addFragment(IntentConditionFragment.getInstance(data, houseType, IntentConditionFragment.INTENT_FOR_PRICE));
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	public class HouseItemBean{

		private String title;

		private int img;

		private int houseType;

		public HouseItemBean(String title, int img, int houseType) {
			this.title = title;
			this.img = img;
			this.houseType = houseType;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getImg() {
			return img;
		}

		public void setImg(int img) {
			this.img = img;
		}

		public int getHouseType() {
			return houseType;
		}
	}

	class GuideAdapter extends AbstractViewPagerAdapter<HouseItemBean> {


		public GuideAdapter(List<HouseItemBean> data) {
			super(data);
		}

		@Override
		public View newView(int position) {
			View view = View.inflate(getActivity(), R.layout.item_house_type, null);
			TextView item_house_name = (TextView) view.findViewById(R.id.item_house_name);
			ImageView item_house_img = (ImageView) view.findViewById(R.id.item_house_img);
			item_house_name.setText(mData.get(position).getTitle());
			item_house_img.setImageResource(mData.get(position).getImg());
			item_house_name.setText(mData.get(position).getTitle());
			return view;
		}
	}
}
