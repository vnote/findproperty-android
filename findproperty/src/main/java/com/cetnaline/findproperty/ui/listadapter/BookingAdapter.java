package com.cetnaline.findproperty.ui.listadapter;

import android.content.Context;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.ExerciseListBo;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;

import java.util.List;

/**
 * Created by diaoqf on 2016/8/24.
 */
public class BookingAdapter extends CommonAdapter<ExerciseListBo> {

    public BookingAdapter(Context context, List<ExerciseListBo> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, ExerciseListBo item) {
        helper.setText(R.id.title,item.getActTitle());
        helper.setText(R.id.content,item.getAdName());
    }
}
