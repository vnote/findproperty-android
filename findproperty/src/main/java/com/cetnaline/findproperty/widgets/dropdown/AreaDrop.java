package com.cetnaline.findproperty.widgets.dropdown;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.percent.PercentRelativeLayout;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.db.entity.GScope;

import java.util.ArrayList;
import java.util.List;

/**
 * 两列单选
 * Created by fanxl2
 */
public class AreaDrop extends AbsDrop<GScope> {

    private PercentRelativeLayout prl_parent;
    private ListView lv_left, lv_right;
    private DropAreaAdapter leftAdapter;
    private DropAreaAdapter rightAdapter;
    private SparseIntArray sparseIntArray = new SparseIntArray();
    private int leftSelectedPos;

    public AreaDrop(View anchor, Activity context, Drawable bg) {
        super(anchor, context);

        View view;
        if (bg==null){
            view = LayoutInflater.from(context).inflate(R.layout.layout_drop_double, null);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.layout_drop_double_map, null);
        }
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
                rightAdapter.setData(arrayList.get(position).getgScopeList(), rightSelectedPos);
                if (position==0){
                    GScope gScope = leftAdapter.getItem(position);
                    DropBo dropBo = new DropBo(gScope.getGScopeAlias(), gScope.getGScopeLevel()+"", -1);
                    dropComplete(false, 0, dropBo);
                }
            }
        });

        lv_right = (ListView) view.findViewById(R.id.lv_right);
        lv_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sparseIntArray.put(leftSelectedPos, position);
                rightAdapter.select(position);

                GScope gScope = rightAdapter.getItem(position);

                DropBo dropBo;
                if (gScope.getParentId()==0){
                    //不限，选择的是区域
                    dropBo = new DropBo(gScope.getGScopeAlias(), gScope.getGScopeLevel()+"", gScope.getParentId());
                    GScope parentGscope = leftAdapter.getItem(leftSelectedPos);
                    dropBo.setLng(parentGscope.getLng());
                    dropBo.setLat(parentGscope.getLat());
                    dropBo.setParentId(parentGscope.getParentId());
                    dropBo.setPara(parentGscope.getFullPY());
                }else {
                    dropBo = new DropBo(gScope.getGScopeName(), gScope.getGScopeLevel()+"", gScope.getGScopeId());
                    dropBo.setLng(gScope.getLng());
                    dropBo.setLat(gScope.getLat());
                    dropBo.setParentId(gScope.getParentId());
                    dropBo.setPara(gScope.getFullPY());
                }

                dropBo.setID(gScope.getGScopeId());
                dropComplete(false, 0, dropBo);

//                dismiss();

//                if (arrayList.get(leftAdapter.getSelectedPos()).isSelected()) {
//                    rightAdapter.select(position);
//                } else {
//                    sparseIntArray.put(leftAdapter.getSelectedPos(), position);
//                    rightAdapter.select(position);
//                }
            }
        });

        leftAdapter = new DropAreaAdapter(context, arrayList, R.layout.item_drop_single);
        rightAdapter = new DropAreaAdapter(context, new ArrayList<>(), R.layout.item_drop_single);
        rightAdapter.setRight(true);
        lv_left.setAdapter(leftAdapter);
        lv_right.setAdapter(rightAdapter);
        initPopWindow(view, bg);
    }

    @Override
    public void init(List<GScope> dropBos) {
        arrayList.clear();
        arrayList.addAll(dropBos);
        leftAdapter.select(0);
        if (arrayList.size() > 0) {
            rightAdapter.setData(arrayList.get(0).getgScopeList());
        }
    }

    public void clearMenu(){
        leftAdapter.select(0);
        leftSelectedPos = 0;
        sparseIntArray.clear();
        rightAdapter.setData(new ArrayList<>());
    }
}
