package com.cetnaline.findproperty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.utils.MyUtils;


/**
 * WrapAdapter包装类
 * Created by guilin on 15/12/15.
 */
public class WrapAdapter extends AbstractWrapAdapter {

    private final static int ITEM_MORE = 99;
    private int load_more_status = 0;
    private String moreText;
    private int marginBottom;

    /**
     * @param adapter {@link RecyclerView.Adapter}
     */
    public WrapAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, String moreText) {
        super(adapter);
        this.moreText = moreText;
        marginBottom = 10;
    }

    public WrapAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, String moreText, int marginBottom) {
        super(adapter);
        this.moreText = moreText;
        this.marginBottom=marginBottom;
    }

    @Override
    public void loadMoreError() {
        load_more_status = 1;
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    public void alreadyLoaded(int state) {
        load_more_status = state;
    }

    @Override
    public int getMoreItemId() {
        return ITEM_MORE;
    }

    @Override
    public int getMoreItemViewType() {
        return ITEM_MORE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateMoreViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_universal_more, parent, false);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.setMargins(0, 0, 0, MyUtils.dip2px(parent.getContext(), marginBottom));
        return new MoreViewHolder(view);
    }

    @Override
    public void onBindMoreViewHolder(RecyclerView.ViewHolder holder) {
        if (holder instanceof MoreViewHolder) {
            MoreViewHolder moreViewHolder = (MoreViewHolder) holder;
            if (load_more_status == 0) {
                /*加载中*/
                moreViewHolder.progressBar.setVisibility(View.VISIBLE);
                moreViewHolder.img.setVisibility(View.GONE);
                moreViewHolder.atv_more.setText(holder.itemView.getContext().getString(R.string.item_more_loading));
                moreViewHolder.itemView.setOnClickListener(null);
                moreViewHolder.bottom_line_left.setVisibility(View.GONE);
                moreViewHolder.bottom_line_right.setVisibility(View.GONE);
            } else if (load_more_status == 2){
                moreViewHolder.progressBar.setVisibility(View.GONE);
                moreViewHolder.img.setVisibility(View.GONE);
                moreViewHolder.atv_more.setText(moreText);
                moreViewHolder.itemView.setOnClickListener(null);
                moreViewHolder.bottom_line_left.setVisibility(View.VISIBLE);
                moreViewHolder.bottom_line_right.setVisibility(View.VISIBLE);
            } else {
                /*加载error*/
                moreViewHolder.progressBar.setVisibility(View.GONE);
                moreViewHolder.img.setVisibility(View.GONE);
                moreViewHolder.atv_more.setText(holder.itemView.getContext().getString(R.string.item_more_load_error));
                moreViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        load_more_status = 0;
                        notifyItemChanged(getItemCount() - 1);
                        itemClickListener.itemClick(getItemCount() - 1, false);
                    }
                });
            }
        }
    }
}
