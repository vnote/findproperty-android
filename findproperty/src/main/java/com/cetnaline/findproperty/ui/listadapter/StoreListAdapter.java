package com.cetnaline.findproperty.ui.listadapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.StoreBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.ui.activity.StoreSearchDetailActivity;
import com.cetnaline.findproperty.ui.activity.StoreStaffListActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;

import java.util.HashMap;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by diaoqf on 2016/10/26.
 */

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {

    private List<StoreBo> storeList;
    private int rowLayout;
    private StoreSearchDetailActivity mAct;

    public StoreListAdapter(List<StoreBo> storeList, int rowLayout, StoreSearchDetailActivity act) {
        this.storeList = storeList;
        this.rowLayout = rowLayout;
        this.mAct = act;
    }

    public List<StoreBo> getStoreList() {
        return storeList;
    }

    /**
     * 清除数据
     */
    public void clear() {
        storeList.clear();
        this.notifyDataSetChanged();
    }

    /**
     * 添加数据
     * @param list
     */
    public void add(List<StoreBo> list) {
        if (list != null && list.size() > 0) {
            this.storeList.addAll(list);
//            this.notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new StoreListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StoreBo storeBo = storeList.get(position);
        holder.name.setText(storeBo.getStoreName());
        holder.location.setText(storeBo.getStoreAddr());
        double distance = 0;
        if (mAct.getEstateBo() != null) {
            //搜索的小区到门店的距离
//            distance = MyUtils.getDistance(new LatLng(mAct.getEstateBo().getLat(), mAct.getEstateBo().getLng()),
//                    new LatLng(storeBo.getLat(), storeBo.getLng()));

            //搜索的小区到当前人的距离
            distance = MyUtils.getDistance(new LatLng(DataHolder.getInstance().getLatitude(),DataHolder.getInstance().getLongitude()),new LatLng(storeBo.getLat(), storeBo.getLng()));
        }
        if (distance < 1) {
            holder.distance.setText(MyUtils.format2(distance*1000) + "m");
        } else {
            holder.distance.setText(MyUtils.format2(distance) + "km");
        }

        holder.phoneImage.setOnClickListener(v->{
            String phone = storeBo.getStore400Tel();
            if (phone == null || "".equals(phone)) {
                ApiRequest.searchStore(new HashMap(){
                    {
                        put("StoreID", storeBo.getStoreId()+"");
                    }
                }).subscribe(new Action1<List<StoreBo>>() {
                    @Override
                    public void call(List<StoreBo> storeBos) {
                        if (storeBos != null) {
                            storeBo.setStore400Tel(storeBos.get(0).getStore400Tel());
                            MyUtils.toCall400(mAct,storeBo.getStore400Tel(),storeBo.getStoreName());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            } else {
                MyUtils.toCall400(mAct,storeBo.getStore400Tel(),storeBo.getStoreName());
            }


        });
        holder.locImage.setOnClickListener(v->{
            NormalEvent event = new NormalEvent(NormalEvent.SHOW_STORE_LOCATION);
            event.data = storeBo.getLat()+";"+storeBo.getLng()+";"+storeBo.getStoreName();
            RxBus.getDefault().send(event);
            mAct.onBackPressed();
        });
        holder.staffImage.setOnClickListener(v->{
            Intent intent = new Intent(mAct, StoreStaffListActivity.class);
            intent.putExtra(StoreStaffListActivity.STORE_NAME, storeBo.getStoreName());
            intent.putExtra(StoreStaffListActivity.STORE_ID, storeBo.getStoreId()+"");
            mAct.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return storeList == null ? 0 : storeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView location;
        public TextView distance;
        public ImageView phoneImage;
        public ImageView locImage;
        public ImageView staffImage;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.store_name);
            location = (TextView) itemView.findViewById(R.id.store_location);
            distance = (TextView) itemView.findViewById(R.id.store_distance);
            phoneImage = (ImageView) itemView.findViewById(R.id.store_phone);
            locImage = (ImageView) itemView.findViewById(R.id.store_loc);
            staffImage = (ImageView) itemView.findViewById(R.id.store_staff);
        }

    }

}
