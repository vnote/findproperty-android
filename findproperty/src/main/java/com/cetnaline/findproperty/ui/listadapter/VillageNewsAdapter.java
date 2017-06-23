package com.cetnaline.findproperty.ui.listadapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.bean.CollectInfoChangeBean;
import com.cetnaline.findproperty.utils.DateUtil;

import java.util.ArrayList;

/**
 * Created by diaoqf on 2016/8/19.
 */
public class VillageNewsAdapter extends RecyclerView.Adapter<VillageNewsAdapter.EstateNewsVH> {

    final DrawableRequestBuilder<String> requestBuilder;
    final ArrayList<CollectInfoChangeBean> list;
    final RecyclerViewItemOnClickListener<CollectInfoChangeBean> itemOnClickListener;
    protected final LayoutInflater layoutInflater;

    public VillageNewsAdapter(Context context, DrawableRequestBuilder<String> requestBuilder, ArrayList<CollectInfoChangeBean> list,
                          RecyclerViewItemOnClickListener<CollectInfoChangeBean> itemOnClickListener) {
        this.requestBuilder = requestBuilder;
        this.list = list;
        this.itemOnClickListener = itemOnClickListener;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public VillageNewsAdapter.EstateNewsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.item_news_estate, parent, false);
        return new EstateNewsVH(view);
    }

    @Override
    public void onBindViewHolder(VillageNewsAdapter.EstateNewsVH holder, int position) {
//        final CollectInfoChangeBean bean = list.get(position);
//        final EsfEstateDo esfEstateDo = bean.esfEstateDo;
//        holder.img_news_point.setVisibility(bean.IsRead ? View.INVISIBLE : View.VISIBLE);
//        if (esfEstateDo != null) {
//            holder.tv_my_message_content.setText("您关注的【" + esfEstateDo.getName() + "】房源有变化啦，赶快围观吧！");
//            holder.tv_my_message_time.setText(DateUtil.timeRule(bean.UpdateTime2 * 1000));
//        }
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                itemOnClickListener.onItemOnClick(bean, 0);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class EstateNewsVH extends RecyclerView.ViewHolder {
        public TextView tv_my_message_content;
        public TextView tv_my_message_time;
        public ImageView iv_my_message_dian;
        public ImageView img_news_point;
        public TextView tv_my_message_change_type;
        public AppCompatTextView atv_delete;
        public LinearLayout ll_my_message;
        public View view;

        public EstateNewsVH(View itemView) {
            super(itemView);
            view = itemView;
            tv_my_message_content = (TextView) itemView.findViewById(R.id.tv_my_message_content);
            tv_my_message_time = (TextView) itemView.findViewById(R.id.tv_my_message_time);
            img_news_point = (ImageView) itemView.findViewById(R.id.img_news_point);
        }
    }

    public interface RecyclerViewItemOnClickListener<T> {
        void onItemOnClick(T t, int tag);
    }
}
