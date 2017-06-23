package com.cetnaline.findproperty.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.cetnaline.findproperty.api.bean.ImageBean;
import com.cetnaline.findproperty.entity.event.EmptyEvent;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.orhanobut.logger.Logger;

import java.util.List;

public class ImagePagerAdapter extends RecyclePagerAdapter<ImagePagerAdapter.ViewHolder> {

    private final ViewPager viewPager;
    private final List<ImageBean> imageList;

//    private DrawableRequestBuilder<String> requestBuilder;

    public ImagePagerAdapter(ViewPager pager, List<ImageBean> imageList, Activity context) {
        this.viewPager = pager;
        this.imageList = imageList;
//        requestBuilder = GlideLoad.init(context);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup container) {
        ViewHolder holder = new ViewHolder(container);
        holder.image.getController().enableScrollInViewPager(viewPager);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        GlideLoad.loadBig(new GlideLoad.Builder(requestBuilder, imageList.get(position).getUrl())
//                .into(holder.image));
        Logger.i(imageList.get(position).getUrl());
        GlideLoad.loadResource(imageList.get(position).getUrl(), holder.image);
    }

    public static GestureImageView getImage(RecyclePagerAdapter.ViewHolder holder) {
        return ((ViewHolder) holder).image;
    }

    private GestureController.SimpleOnGestureListener listener;


    static class ViewHolder extends RecyclePagerAdapter.ViewHolder {
        final GestureImageView image;

        ViewHolder(ViewGroup container) {
            super(new GestureImageView(container.getContext()));
            image = (GestureImageView) itemView;
            image.getController().setOnGesturesListener(new GestureController.OnGestureListener() {
                @Override
                public void onDown(@NonNull MotionEvent e) {

                }

                @Override
                public void onUpOrCancel(@NonNull MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(@NonNull MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                    RxBus.getDefault().send(new EmptyEvent());
                    return false;
                }

                @Override
                public void onLongPress(@NonNull MotionEvent e) {

                }

                @Override
                public boolean onDoubleTap(@NonNull MotionEvent e) {
                    return false;
                }
            });
        }
    }

}
