package com.cetnaline.findproperty.widgets.dropdown;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.percent.PercentRelativeLayout;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.ArrayList;
import java.util.List;

/**
 * 两列单选
 * Created by fanxl2
 */
public class DoubleSingleDrop extends AbsDrop<DropBo> {

    private PercentRelativeLayout prl_parent;
    private ListView lv_left, lv_right;
    private DropTextAdapter leftAdapter;
    private DropTextAdapter rightAdapter;
    private SparseIntArray sparseIntArray = new SparseIntArray();
    private int leftSelectedPos;

    public DoubleSingleDrop(View anchor, Activity context, Drawable bg) {
        super(anchor, context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_drop_double, null);
        prl_parent = (PercentRelativeLayout) view.findViewById(R.id.prl_parent);
        prl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        lv_left = (ListView) view.findViewById(R.id.lv_left);
        lv_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                leftSelectedPos = position;
                leftAdapter.select(position);
                int rightSelectedPos = sparseIntArray.get(position, -1);
                rightAdapter.setData(arrayList.get(position).getChildrenList(), rightSelectedPos);

//                if (arrayList.get(position).isSelected()) {
//
//                    SparseBooleanArray selectArray = hashMap.get(position);
//                    rightAdapter.setData(arrayList.get(position).getChildrenList(), selectArray);
//
//                } else {
//                    SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
//                    hashMap.put(position, sparseBooleanArray);
//                    rightAdapter.setData(arrayList.get(position).getChildrenList(), sparseBooleanArray);
////                    lv_right.smoothScrollToPosition(sparseIntArray.get(position, 0));
//                }
            }
        });

        lv_right = (ListView) view.findViewById(R.id.lv_right);
        lv_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sparseIntArray.put(leftSelectedPos, position);
                rightAdapter.select(position);
                dismiss();

//                if (arrayList.get(leftAdapter.getSelectedPos()).isSelected()) {
//                    rightAdapter.select(position);
//                } else {
//                    sparseIntArray.put(leftAdapter.getSelectedPos(), position);
//                    rightAdapter.select(position);
//                }
            }
        });

        leftAdapter = new DropTextAdapter(context, arrayList, R.layout.item_drop_single);
        rightAdapter = new DropTextAdapter(context, new ArrayList<DropBo>(), R.layout.item_drop_single);
        lv_left.setAdapter(leftAdapter);
        lv_right.setAdapter(rightAdapter);
        initPopWindow(view, bg);
    }

    @Override
    public void init(List<DropBo> dropBos) {
        arrayList.clear();
        arrayList.addAll(dropBos);
        leftAdapter.select(0);
        if (arrayList.size() > 0) {
            rightAdapter.setData(arrayList.get(0).getChildrenList());
        }
    }

    @Override
    public void onDismiss() {

    }
}
