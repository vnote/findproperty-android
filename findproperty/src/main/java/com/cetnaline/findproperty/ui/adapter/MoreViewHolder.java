package com.cetnaline.findproperty.ui.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cetnaline.findproperty.R;


/**
 * MoreViewHolder加载更多
 * Created by shengl on 2015/11/11.
 */
public class MoreViewHolder extends RecyclerView.ViewHolder {

    public ProgressBar progressBar;
    public ImageView img;
    public AppCompatTextView atv_more;
    public View bottom_line_left, bottom_line_right;

    public MoreViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        img = (ImageView) itemView.findViewById(R.id.img);
        atv_more = (AppCompatTextView) itemView.findViewById(R.id.atv_more);
        bottom_line_left = itemView.findViewById(R.id.bottom_line_left);
        bottom_line_right = itemView.findViewById(R.id.bottom_line_right);
    }
}
