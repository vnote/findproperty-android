package com.cetnaline.findproperty.widgets.dropdown;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.List;

/**
 * 单列单选
 * Created by fanxl2.
 */
public class SingleDrop extends AbsDrop<DropBo> {

    private FrameLayout fl_parent;
    private ListView drop_single_lv;
    private DropTextAdapter dropTextAdapter;

    public SingleDrop(View anchor, Activity context, Drawable bg) {
        super(anchor, context);
        View view;
        if (bg==null){
            view = LayoutInflater.from(context).inflate(R.layout.layout_drop_single, null);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.layout_drop_single_map, null);
        }
        fl_parent = (FrameLayout) view.findViewById(R.id.fl_parent);
        fl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        drop_single_lv = (ListView) view.findViewById(R.id.drop_single_lv);
        drop_single_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dropTextAdapter.select(position);
//                DropBo dropOne = arrayList.get(position);
//                if ("不限".equals(dropOne.getName())){
//
//                }

                dropComplete(false, 0, arrayList.get(position));
            }
        });
        dropTextAdapter = new DropTextAdapter(context, arrayList, R.layout.item_drop_single);
        drop_single_lv.setAdapter(dropTextAdapter);
        initPopWindow(view, bg);
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
