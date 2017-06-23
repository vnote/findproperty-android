package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.inter.ImgViewPagerClickListener;
import com.cetnaline.findproperty.utils.glide.GlideLoad;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 详情顶部View
 * Created by guilin on 16/1/29.
 */
public class DetailImgLayout extends FrameLayout {

    private ViewPager viewPager;
    private AppCompatTextView atv_img_indicator, atv_title_ad;
    private FrameLayout fl_title_ad;
    private ArrayList<String> list = new ArrayList<>();
    private DrawableRequestBuilder<String> requestBuilder;
    private ImgAdapter imgAdapter;

    private ImgViewPagerClickListener imgViewPagerClickListener;

    public DetailImgLayout(Context context) {
        this(context, null);
    }

    public DetailImgLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailImgLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_detail_imglist, this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        atv_img_indicator = (AppCompatTextView) findViewById(R.id.atv_img_indicator);
        atv_title_ad = (AppCompatTextView) findViewById(R.id.atv_title_ad);
        fl_title_ad = (FrameLayout) findViewById(R.id.fl_title_ad);

        imgAdapter = new ImgAdapter();
        viewPager.setAdapter(imgAdapter);
    }

    /**
     * 设置图片加载
     */
    public void setRequestBuilder(DrawableRequestBuilder<String> requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    public void setImgViewPagerClickListener(ImgViewPagerClickListener imgViewPagerClickListener) {
        this.imgViewPagerClickListener = imgViewPagerClickListener;
    }

    public void showText(int visibility){
        atv_img_indicator.setVisibility(visibility);
    }

    /**
     * 设置标题
     */
    public void setTitle(CharSequence title) {
        if (TextUtils.isEmpty(title)) {
            fl_title_ad.setVisibility(GONE);
        } else {
            fl_title_ad.setVisibility(VISIBLE);
            atv_title_ad.setText(title);
        }
    }

    public void setImagePosition(int position){
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置图片列表
     */
    public void setImgList(List<String> arrayList) {
        if (arrayList == null || arrayList.size() == 0)
            return;
        list.clear();
        list.addAll(arrayList);
        imgAdapter.notifyDataSetChanged();
        setIndicator(0);
    }

    private void setIndicator(int position) {
        atv_img_indicator.setText(String.format(Locale.CHINA, "%d/%d", position + 1, list.size()));
    }

    private void itemClick(int position) {
        if (imgViewPagerClickListener != null)
            imgViewPagerClickListener.imgItemClick(position);
    }

    class ImgAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick(position);
                }
            });

            GlideLoad.load(new GlideLoad.Builder(requestBuilder, list.get(position))
                    .into(imageView));

            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
