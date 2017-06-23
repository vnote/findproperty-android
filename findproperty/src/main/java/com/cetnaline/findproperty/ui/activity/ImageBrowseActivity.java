package com.cetnaline.findproperty.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.ImageBean;
import com.cetnaline.findproperty.api.bean.ImageTags;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.event.EmptyEvent;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.adapter.ImagePagerAdapter;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;


/**
 * Created by fanxl2 on 2016/8/21.
 */
public class ImageBrowseActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

	@BindView(R.id.browse_img_vp)
	ViewPager browse_img_vp;

	@BindView(R.id.browse_tv_index)
	TextView browse_tv_index;

	@BindView(R.id.browse_toolbar)
	Toolbar browse_toolbar;

	@BindView(R.id.image_rv_tags)
	RecyclerView image_rv_tags;

	public static final String DEFAULT_POSITION_KEY = "DEFAULT_POSITION_KEY";
	public static final String IMAGE_DATA_KEY = "IMAGE_DATA_KEY";

	private ImagePagerAdapter imagePagerAdapter;

	private List<ImageBean> imageList;

	private int currentPosition;

	public static final int FINISH_CODE = 104;
	public static final String IMAGE_POSITION_KEY = "IMAGE_POSITION_KEY";
	private int tagSelectPos;
	private TagAdapter tagAdapter;

	@Override
	protected int getContentViewId() {
		return R.layout.act_image_browse;
	}

	@Override
	protected void init(Bundle savedInstanceState) {

		currentPosition = getIntent().getIntExtra(DEFAULT_POSITION_KEY, 0);

		List<ImageBean> sources = getIntent().getParcelableArrayListExtra(IMAGE_DATA_KEY);

		List<ImageTags> tags = new ArrayList<>();
		int houseType = getIntent().getIntExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
		if (houseType==MapFragment.HOUSE_TYPE_NEW){
			imageList = orderNewHouseImage(sources, tags);
		}else {
			imageList = orderImage(sources, tags);
		}

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
		image_rv_tags.setLayoutManager(linearLayoutManager);

		tagAdapter = new TagAdapter(this, R.layout.item_image_tag, tags);
		tagAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
				tagSelectPos = position;
				currentPosition = tags.get(position).getIndex();
				browse_img_vp.setCurrentItem(currentPosition);
			}

			@Override
			public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
				return false;
			}
		});
		image_rv_tags.setAdapter(tagAdapter);

		if (imageList==null)return;
		imagePagerAdapter = new ImagePagerAdapter(browse_img_vp, imageList, this);
		browse_img_vp.setAdapter(imagePagerAdapter);
		browse_img_vp.addOnPageChangeListener(this);
		browse_img_vp.setPageMargin(getResources().getDimensionPixelSize(R.dimen.view_pager_margin));
		browse_img_vp.setCurrentItem(currentPosition);

		browse_tv_index.setText(imageList.get(0).getIndex()+"/"+imageList.get(0).getCount());
		browse_toolbar.setTitle(imageList.get(0).getTitle());

		setSupportActionBar(browse_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		browse_toolbar.setNavigationOnClickListener(v -> {
			Intent intent = new Intent(ImageBrowseActivity.this, HouseDetail.class);
			intent.putExtra(IMAGE_POSITION_KEY, currentPosition);
			ImageBrowseActivity.this.setResult(FINISH_CODE, intent);
			ImageBrowseActivity.this.finish();
		});

		sb = RxBus.getDefault().toObservable(EmptyEvent.class)
				.subscribe(emptyEvent -> {
					Intent intent = new Intent(ImageBrowseActivity.this, HouseDetail.class);
					intent.putExtra(IMAGE_POSITION_KEY, currentPosition);
					ImageBrowseActivity.this.setResult(FINISH_CODE, intent);
					ImageBrowseActivity.this.finish();
				});

	}

	Subscription sb;

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	protected void initToolbar() {
		StatusBarCompat.setStatusBarColor(this, Color.BLACK);
		showToolbar(false);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		ImageBean imageBean = imageList.get(position);
		browse_tv_index.setText(imageBean.getIndex()+"/"+imageBean.getCount());
		browse_toolbar.setTitle(imageBean.getTitle());
		tagSelectPos = imageBean.getTagIndex();
		tagAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	private List<ImageBean> orderImage(List<ImageBean> images, List<ImageTags> tags){

		List<ImageBean> livingRoom = new ArrayList<>();
		List<ImageBean> houseType = new ArrayList<>();
		List<ImageBean> bedRoom = new ArrayList<>();
		List<ImageBean> kitchen = new ArrayList<>();
		List<ImageBean> toilet = new ArrayList<>();
		List<ImageBean> other = new ArrayList<>();

		List<ImageBean> newList = new ArrayList<>();

		for (int i=0; i<images.size(); i++){
			ImageBean item = images.get(i);
			switch (item.getImageTitle()){
				case "客厅":
					livingRoom.add(item);
					break;
				case "户型图":
					houseType.add(item);
				case "平面图":
					houseType.add(item);
				case "房型图":
					houseType.add(item);
					break;
				case "主卧":
					bedRoom.add(item);
					break;
				case "次卧":
					bedRoom.add(item);
					break;
				case "卧室":
					bedRoom.add(item);
					break;
				case "厨房":
					kitchen.add(item);
					break;
				case "卫生间":
					toilet.add(item);
					break;
				default:
					other.add(item);
					break;
			}
		}

		if (livingRoom.size()>0){
			setIndex(tags, livingRoom, newList, "客厅");
		}

		if (houseType.size()>0){
			setIndex(tags, houseType, newList, "户型图");
		}

		if (bedRoom.size()>0){
			setIndex(tags, bedRoom, newList, "卧室");
		}

		if (kitchen.size()>0){
			setIndex(tags, kitchen, newList, "厨房");
		}

		if (toilet.size()>0){
			setIndex(tags, toilet, newList, "卫生间");
		}

		if (other.size()>0){
			setIndex(tags, other, newList, "其他");
		}
		return newList;
	}

	private List<ImageBean> orderNewHouseImage(List<ImageBean> images, List<ImageTags> tags){

		List<ImageBean> designRoom = new ArrayList<>();
		List<ImageBean> houseType = new ArrayList<>();
		List<ImageBean> model = new ArrayList<>();
		List<ImageBean> real = new ArrayList<>();
		List<ImageBean> location = new ArrayList<>();
		List<ImageBean> other = new ArrayList<>();

		List<ImageBean> newList = new ArrayList<>();

		for (int i=0; i<images.size(); i++){
			ImageBean item = images.get(i);
			switch (item.getImageTitle()){
				case "封面图":
					designRoom.add(item);
					break;
				case "效果图":
					designRoom.add(item);
					break;
				case "户型图":
					houseType.add(item);
				case "样板房":
					model.add(item);
					break;
				case "实景图":
					real.add(item);
					break;
				case "项目现场":
					real.add(item);
					break;
				case "区位图":
					location.add(item);
					break;
				case "平面图":
					location.add(item);
					break;
				default:
					other.add(item);
					break;
			}
		}

		if (designRoom.size()>0){
			setIndex(tags, designRoom, newList, "效果图");
		}

		if (houseType.size()>0){
			setIndex(tags, houseType, newList, "户型图");
		}

		if (model.size()>0){
			setIndex(tags, model, newList, "样板房");
		}

		if (real.size()>0){
			setIndex(tags, real, newList, "实景图");
		}

		if (location.size()>0){
			setIndex(tags, location, newList, "区位图");
		}

		if (other.size()>0){
			setIndex(tags, other, newList, "其他");
		}
		return newList;
	}

	private void setIndex(List<ImageTags> tags, List<ImageBean> itemList, List<ImageBean> newList, String key) {
		int index = newList.size();
		if (index<0)index=0;
		ImageTags tag = new ImageTags(key, index, itemList.size());
		tags.add(tag);

		for(int i=0; i<itemList.size(); i++){
			ImageBean imageBean = itemList.get(i);
			imageBean.setIndex(i+1);
			imageBean.setTagIndex(tags.size()-1);
			imageBean.setCount(itemList.size());
		}
		newList.addAll(itemList);
	}

	public class TagAdapter extends CommonAdapter<ImageTags> {

		public TagAdapter(Context context, int layoutId, List<ImageTags> datas) {
			super(context, layoutId, datas);
		}

		@Override
		protected void convert(ViewHolder holder, ImageTags imageTags, int position) {
			AppCompatCheckedTextView tv_image_tag = holder.getView(R.id.tv_image_tag);
			if (position==tagSelectPos){
				tv_image_tag.setChecked(true);
			}else {
				tv_image_tag.setChecked(false);
			}
			tv_image_tag.setText(imageTags.getText());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sb!=null && sb.isUnsubscribed()){
			sb.unsubscribe();
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		return "房源图片浏览";
	}
}
