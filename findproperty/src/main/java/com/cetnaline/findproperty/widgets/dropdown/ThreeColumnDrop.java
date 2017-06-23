package com.cetnaline.findproperty.widgets.dropdown;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.SchoolBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.db.entity.RailLine;
import com.cetnaline.findproperty.db.entity.RailWay;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.ui.adapter.ChildAdapter;
import com.cetnaline.findproperty.ui.adapter.ParentAdapter;
import com.cetnaline.findproperty.utils.DbUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * 三列单选
 * Created by fanxl2
 */
public class ThreeColumnDrop extends AbsDrop<DropBo> {

    private LinearLayout prl_parent;
    private ListView lv_left, lv_right, lv_center;
    private ParentAdapter leftAdapter;
    private ParentAdapter centerAdapter;
    private ChildAdapter rightAdapter;
    private int leftSelectedPos, centerSelectedPos;
    private Subscription subscription;

    public ThreeColumnDrop(View anchor, Activity context) {
        super(anchor, context);

        View view;
        view = LayoutInflater.from(context).inflate(R.layout.layout_drop_three, null);
        prl_parent = (LinearLayout) view.findViewById(R.id.prl_parent);
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

                if (position==0 || position==2){
                    centerAdapter.setData(regionDrops);
                }else if (position==1){
                    centerAdapter.setData(railLineDrops);
                }else {
                    centerAdapter.setData(new ArrayList<>());
                    dropComplete(false, 0, leftAdapter.getItem(position));
                }
                centerAdapter.setSelectedPosition(-1);
                leftAdapter.setSelectedPosition(leftSelectedPos);
                rightAdapter.setData(new ArrayList<>());
                rightAdapter.setSelectedPosition(-1);
            }
        });

        lv_center = (ListView) view.findViewById(R.id.lv_center);
        lv_center.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                centerSelectedPos = position;

                DropBo region = centerAdapter.getItem(position);
                List<DropBo> childDrops = new ArrayList<>();

                if (leftSelectedPos==0){
                    List<GScope> childs = DbUtil.getGScopeChild(Integer.parseInt(region.getValue()));
                    for (GScope item : childs){
                        DropBo drop = new DropBo(item.getGScopeName(), item.getGScopeId()+"", 0);
                        drop.setName(NetContents.GSCOPE_NAME);
                        drop.setKey("GScopeId");
                        drop.setPara(item.getFullPY());
                        childDrops.add(drop);
                    }

                    DropBo noDrop = new DropBo("不限", region.getText(), -1);
                    noDrop.setName(NetContents.REGION_NAME);
                    noDrop.setKey("RegionId");
                    noDrop.setID(Integer.parseInt(region.getValue()));
                    noDrop.setPara(region.getPara());
                    childDrops.add(0, noDrop);

                    rightAdapter.setData(childDrops);
                }else if (leftSelectedPos==1){
                    List<RailWay> railWays = DbUtil.getRailWayByRailLineId(region.getValue());
                    for (RailWay railWay : railWays) {
                        DropBo child = new DropBo(railWay.getRailWayName(), railWay.getRailWayID() + "", 0);
                        child.setName(NetContents.RAILWAY_NAME);
                        child.setKey("RailWayId");
                        childDrops.add(child);
                    }

                    DropBo noDrop = new DropBo("不限", region.getText(), -1);
                    noDrop.setName(NetContents.RAILLINE_NAME);
                    noDrop.setKey("RailLineId");
                    noDrop.setID(Integer.parseInt(region.getValue()));
                    noDrop.setPara(region.getPara());
                    childDrops.add(0, noDrop);

                    rightAdapter.setData(childDrops);
                }else if (leftSelectedPos==2){
                    unSub();
                    subscription = ApiRequest.getSchoolList(region.getValue())
                            .subscribe(schoolBoList -> {

                                if (schoolBoList != null) {
                                    for (SchoolBo school : schoolBoList) {
                                        DropBo child = new DropBo(school.getSchoolShortName(), school.getSchoolId() + "", 0);
                                        child.setName(NetContents.SCHOOL_NAME);
                                        child.setKey("SchoolId");
                                        childDrops.add(child);
                                    }
                                    rightAdapter.setData(childDrops);
                                }else {
                                    Toast.makeText(context, "该区域没有学校", Toast.LENGTH_SHORT).show();
                                }

                            }, throwable -> {

                                String error = ErrorHanding.handleError(throwable);
                                if ("数据为空".equals(error)){
                                    Toast.makeText(context, "该区域没有学校", Toast.LENGTH_SHORT).show();
                                    rightAdapter.setData(new ArrayList<>());
                                }

                            });
                }

                centerAdapter.setSelectedPosition(centerSelectedPos);
                rightAdapter.setSelectedPosition(-1);
            }
        });

        lv_right = (ListView) view.findViewById(R.id.lv_right);
        lv_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rightAdapter.setSelectedPosition(position);

                DropBo item = rightAdapter.getItem(position);
                if (leftSelectedPos==1 && position!=0){
                    DropBo line = rightAdapter.getItem(0);
                    dropComplete(true, 0, line, item);
                }else {
                    dropComplete(false, 0, item);
                }
            }
        });

        leftAdapter = new ParentAdapter(context, arrayList, R.layout.item_text);
        centerAdapter = new ParentAdapter(context, new ArrayList<>(), R.layout.item_text);
        rightAdapter = new ChildAdapter(context, new ArrayList<>(), R.layout.item_text);

        lv_left.setAdapter(leftAdapter);
        lv_center.setAdapter(centerAdapter);
        lv_right.setAdapter(rightAdapter);
        initPopWindow(view, null);
    }

    private List<DropBo> regionDrops;
    private List<DropBo> railLineDrops;


    @Override
    public void init(List<DropBo> dropBos) {
        arrayList.clear();
        arrayList.addAll(dropBos);

        leftSelectedPos = 0;
        leftAdapter.setSelectedPosition(leftSelectedPos);

        regionDrops = new ArrayList<>();
        List<GScope> areas = DbUtil.getGScopeChild(21);
        for (GScope item : areas){
            DropBo drop = new DropBo(item.getGScopeName(), item.getGScopeId()+"", 0);
            drop.setName(NetContents.REGION_NAME);
            drop.setKey("RegionId");
            drop.setPara(item.getFullPY());
            regionDrops.add(drop);
        }

        centerAdapter.setData(regionDrops);


        if (dropBos.size()>2){
            railLineDrops = new ArrayList<>();
            List<RailLine> railLines = DbUtil.getRailLines();
            for (RailLine railLine : railLines){
                DropBo drop = new DropBo(railLine.getRailLineName(), railLine.getRailLineID()+"", 0);
                drop.setName(NetContents.RAILLINE_NAME);
                drop.setKey("RailLineId");
                railLineDrops.add(drop);
            }
        }
    }

    public void resetSelectStatus(){
        leftSelectedPos = 0;
        leftAdapter.setSelectedPosition(leftSelectedPos);
        centerAdapter.setData(new ArrayList<>());
        rightAdapter.setData(new ArrayList<>());
    }

    private void unSub(){
        if (subscription !=null && subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }
}
