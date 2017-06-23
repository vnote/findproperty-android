package com.cetnaline.findproperty.widgets.dropdown;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.percent.PercentRelativeLayout;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 两列多选
 * Created by fanxl2
 */
public class DoubleMultiDrop extends AbsDrop<DropBo> {

    private PercentRelativeLayout prl_parent;
    private ListView lv_left, lv_right;
    private Button btn_reset, btn_confirm;
    private DropTextAdapter leftAdapter;
    private DropMultiAdapter rightAdapter;
    private HashMap<Integer, SparseBooleanArray> hashMap = new HashMap<>();
    private int leftSelectedPos;

    public DoubleMultiDrop(View anchor, Activity context, Drawable bg) {
        super(anchor, context);
        View view;
        if (bg==null){
            view = LayoutInflater.from(context).inflate(R.layout.layout_drop_double_multi, null);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.layout_drop_double_multi_map, null);
        }

        selectedParams = new HashMap<>();

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

                if (arrayList.get(position).isSelected()) {
                    SparseBooleanArray selectArray = hashMap.get(position);
                    rightAdapter.setData(arrayList.get(position).getChildrenList(), selectArray);
                } else {
                    SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
                    hashMap.put(position, sparseBooleanArray);
                    rightAdapter.setData(arrayList.get(position).getChildrenList(), sparseBooleanArray);
//                    lv_right.smoothScrollToPosition(sparseIntArray.get(position, 0));
                }
            }
        });
        lv_right = (ListView) view.findViewById(R.id.lv_right);
        lv_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                arrayList.get(leftSelectedPos).setSelected(true);

                DropBo leftBo = leftAdapter.getItem(leftSelectedPos);
                if (leftBo.getType()==1){
                    rightAdapter.select(position);
                    DropBo item = rightAdapter.getItem(position);
                    if (rightAdapter.positionIsSelected(position)){
                        selectedParams.put(item.getText(), item);
                    }else {
                        selectedParams.remove(item.getText());
                    }
                }else {
                    rightAdapter.selectSingle(position);
                    DropBo item = rightAdapter.getItem(position);
                    selectedParams.put(item.getName(), item);
                }
            }
        });
        btn_reset = (Button) view.findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DropBo item : arrayList){
                    item.setSelected(false);
                }
                selectedParams.clear();
                hashMap.clear();
                rightAdapter.clear();
            }
        });
        btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedParams.size()>0){
                    DropBo[] results = new DropBo[selectedParams.size()];
                    int i=0;
                    for (DropBo dropBo : selectedParams.values()){
                        results[i] = dropBo;
                        i++;
                    }
                    dropComplete(true, 3, results);
                }else {
                    dropComplete(true, 3, new DropBo[0]);
                }
            }
        });
        leftAdapter = new DropTextAdapter(context, arrayList, R.layout.item_drop_single);
        rightAdapter = new DropMultiAdapter(context, new ArrayList<>(), R.layout.item_drop_single);
        lv_left.setAdapter(leftAdapter);
        lv_right.setAdapter(rightAdapter);
        initPopWindow(view, bg);
    }

    private Map<String, DropBo> selectedParams;

    @Override
    public void init(List<DropBo> dropBos) {
        arrayList.clear();
        arrayList.addAll(dropBos);
//        hashMap.clear();
        leftAdapter.select(0);
        leftAdapter.notifyDataSetChanged();
        if (arrayList.size() > 0) {

            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
            hashMap.put(0, sparseBooleanArray);
            rightAdapter.setData(arrayList.get(0).getChildrenList(), sparseBooleanArray);

//            if (arrayList.get(0).isSelected()) {
//                hashMap.put(0, new SparseBooleanArray());
//                rightAdapter.setData(arrayList.get(0).getChildrenList(), hashMap.get(0));
//            } else {
//                rightAdapter.setData(arrayList.get(0).getChildrenList(), sparseIntArray.get(0, 0));
//                lv_right.smoothScrollToPosition(sparseIntArray.get(0, 0));
//            }
        }
    }

    public void clearMenu(){
        leftSelectedPos = 0;
        leftAdapter.select(0);
        selectedParams.clear();
    }

}
