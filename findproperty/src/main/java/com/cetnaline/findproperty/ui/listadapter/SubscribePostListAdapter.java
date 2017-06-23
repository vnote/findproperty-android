package com.cetnaline.findproperty.ui.listadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.ApiResponse;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.IntentionBo;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.RailLine;
import com.cetnaline.findproperty.db.entity.RailWay;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.activity.HouseList;
import com.cetnaline.findproperty.ui.fragment.HouseDetailFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.ui.fragment.SubscribePostFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.FlowTag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by diaoqf on 2016/12/12.
 */

public class SubscribePostListAdapter extends RecyclerView.Adapter<SubscribePostListAdapter.ViewHolder>  {
    private String type;
    private List<IntentionBo> intentList;
    private DrawableRequestBuilder<String> requestBuilder;
    private OnCancelSubscribe onCancelSubscribe;
    /**
     * item 布局
     */
    private int rowLayout;
    private Context mContext;

    private HashMap<String,String> params = new HashMap<>();

    public void setOnCancelSubscribe(OnCancelSubscribe onCancelSubscribe) {
        this.onCancelSubscribe = onCancelSubscribe;
    }

    /**
     * 添加数据
     * @param list
     */
    public void add(List<IntentionBo> list) {
        if (list != null && list.size() > 0) {
            this.intentList.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    /**
     * 清除数据
     */
    public void clear() {
        intentList.clear();
        this.notifyDataSetChanged();
    }

    public SubscribePostListAdapter(List<IntentionBo> intentList, int rowLayout, String type ,Context context) {
        this.intentList = intentList;
        this.rowLayout = rowLayout;
        this.type = type;
        mContext = context;
        requestBuilder = GlideLoad.init((Activity) context);
    }

    @Override
    public SubscribePostListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new SubscribePostListAdapter.ViewHolder(v);
    }

    /**
     * 房龄参数计算
     * @param dropType
     * @return
     */
    private SearchParam createHouseAge(SearchParam dropType){
        SearchParam search = new SearchParam();
        search.setId(dropType.getId());
        search.setText(dropType.getText());
        search.setTitle(dropType.getName());
        search.setValue("0,0");
        search.setPara(dropType.getPara());
        search.setName(dropType.getName());
        search.setKey(dropType.getKey());
        search.setParamKey(dropType.getParamKey());

        String[] yearStr = dropType.getValue().split(",");
        if (yearStr.length>1){
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            int minYear = year - Integer.parseInt(yearStr[1]);
            int maxYear = year - Integer.parseInt(yearStr[0]);

            try {
                Date minDate = format.parse(minYear+"-1-1");
                Date maxDate = format.parse(maxYear+"-12-31");

                long minTime = minDate.getTime()/1000;
                long maxTime = maxDate.getTime()/1000;

                search.setValue(minTime+","+maxTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return search;
    }

    @Override
    public void onBindViewHolder(SubscribePostListAdapter.ViewHolder holder, int position) {
        IntentionBo bean = intentList.get(position);
        getTages(holder.tags,bean);
        holder.search.setOnClickListener(v->{
            List<SearchParam> params = bean.getSearchPara();
            HashMap<String, SearchParam> send_param = new HashMap<>();
            for (SearchParam param:params) {
                if (param.getName().equalsIgnoreCase("HouseAge")) {
                    param = createHouseAge(param);
                }
                send_param.put(param.getKey(), param);
            }

            Intent intent = new Intent(mContext, HouseList.class);
            int index = 0;  //二手房
            if (type.equals(SubscribePostFragment.RENT)) {
                index = 1;
            } else if (type.equals(SubscribePostFragment.XINFANG)) {
                index = 2;
            }
            intent.putExtra(MapFragment.HOUSE_TYPE_KEY, index);
            intent.putExtra(HouseList.SEARCH_PARAM_KEY, send_param);
            ((Activity)mContext).startActivityForResult(intent,SubscribePostFragment.REQUEST_HOUSE_LIST_CODE);
        });

        if (type.equals(SubscribePostFragment.XINFANG)) {
            loadNewHouse(holder.items);
        } else {
            loadPost(holder.items);
        }

        holder.cancel.setOnClickListener(v->{
            ApiRequest.deleteIntent(bean.getIntentionID())
                    .subscribe(flag -> {
                        if (flag) {
                            DataHolder.getInstance().setChangeIntent(true);
                            if (onCancelSubscribe != null) {
                                onCancelSubscribe.cancelSubscribe(position,true);
                            }
                        }
                    }, throwable -> {
                        if (onCancelSubscribe != null) {
                            onCancelSubscribe.cancelSubscribe(position, false);
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return intentList == null ? 0:intentList.size();
    }

    /**
     * 无数据显示child
     * @param parent
     * @param message
     */
    private void showNoDataLayout(LinearLayout parent, String message) {
        ((LinearLayout)parent.findViewById(R.id.container)).removeAllViews();
        TextView textView = (TextView) parent.findViewById(R.id.load_message);
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
    }

    /**
     * 二手房、租房数据
     * @param items
     */
    private void loadPost(LinearLayout items){
        Observable<ApiResponse<List<HouseBo>>> observable = null;
        if (params.get("SchoolId") != null) {
            observable = ApiRequest.getHouseBySchool4AllResult(params);
        } else if (params.get("RailWayId") != null) {
            observable = ApiRequest.getHouseByMetroAllResult(params);
        } else {
            observable = ApiRequest.getHouseList4AllResult(params);
        }
        observable.subscribe(
                response -> {
                    LinearLayout container = (LinearLayout) items.findViewById(R.id.container);
                    container.removeAllViews();
                    if (response.isSuccess()) {
                        if (response.getResult() != null && response.getResult().size() > 0) {
                            List<HouseBo> houseBos = response.getResult();
                            items.findViewById(R.id.load_message).setVisibility(View.GONE);
                            for (int i = 0; i < houseBos.size() && i < 3; i++) {
                                HouseBo bean = houseBos.get(i);
                                View child = LayoutInflater.from(items.getContext()).inflate(R.layout.item_post, null);
                                child.setOnClickListener(v -> {
                                    Intent intent = new Intent(mContext, HouseDetail.class);
                                    if (type.equals(SubscribePostFragment.POST)) {
                                        intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
                                    } else if (type.equals(SubscribePostFragment.RENT)) {
                                        intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
                                    }
                                    intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, bean.getPostId());
                                    mContext.startActivity(intent);
                                });
                                ImageView post_img = (ImageView) child.findViewById(R.id.post_img);
                                AppCompatTextView post_title = (AppCompatTextView) child.findViewById(R.id.post_title);
                                post_title.setText(bean.getTitle());

                                TextView item_small_house = (TextView) child.findViewById(R.id.item_small_house);
                                item_small_house.setText(bean.getRoomCount() + "室" + bean.getHallCount() + "厅 | " + MyUtils.formatHouseArea(bean.getGArea()) + "㎡ | " + bean.getDirection());

                                TextView post_price = (TextView) child.findViewById(R.id.item_small_money);
                                TextView unit = (TextView) child.findViewById(R.id.unit);
                                if (type.equals(SubscribePostFragment.POST)) {
                                    post_price.setText((MyUtils.format2String(bean.getSalePrice() / 10000)));
                                    unit.setText("万");
                                } else if (type.equals(SubscribePostFragment.RENT)) {
                                    post_price.setText(MyUtils.format2String(bean.getRentPrice()));
                                    unit.setText("元/月");
                                }
                                container.addView(child);
                                String imgUri = bean.getDefaultImage();
                                if (TextUtils.isEmpty(imgUri)) {
                                    imgUri = AppContents.POST_DEFAULT_IMG_URL;
                                }
                                GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUri)
                                        .into(post_img));
                            }
                        } else {
                            showNoDataLayout(items, "暂无符合意向房源");
                        }
                    } else {
                        showNoDataLayout(items, "暂无符合意向房源");
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    /**
     * 新房数据
     * @param items
     */
    private void loadNewHouse(LinearLayout items) {
        ApiRequest.getNewHouses(params).subscribe(new Subscriber<List<NewHouseListBo>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                showNoDataLayout(items,"暂无符合意向房源");
            }

            @Override
            public void onNext(List<NewHouseListBo> houseBos) {
                LinearLayout container = (LinearLayout) items.findViewById(R.id.container);
                container.removeAllViews();
                if (houseBos != null && houseBos.size() > 0 ) {
                    items.findViewById(R.id.load_message).setVisibility(View.GONE);
                    for (int i=0; i<houseBos.size() && i < 3; i++) {
                        NewHouseListBo bean = houseBos.get(i);
                        View child = LayoutInflater.from(items.getContext()).inflate(R.layout.item_post, null);
                        child.setOnClickListener(v->{
                            Intent intent = new Intent(mContext, HouseDetail.class);
                            intent.putExtra(HouseDetailFragment.ESTEXT_ID_KEY, bean.getEstExtId());
                            intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
                            mContext.startActivity(intent);
                        });
                        ImageView post_img = (ImageView)child.findViewById(R.id.post_img);
                        AppCompatTextView post_title = (AppCompatTextView) child.findViewById(R.id.post_title);
                        post_title.setText(bean.getAdName());
                        TextView post_types = (TextView) child.findViewById(R.id.item_small_house);
                        post_types.setText(bean.getEstType());
                        AppCompatTextView post_price = (AppCompatTextView) child.findViewById(R.id.item_small_money);
                        if (bean.getAveragePrice() > 0) {
                            post_price.setText("均价:"+(MyUtils.format2String(bean.getAveragePrice())) + "元/㎡");
                        } else {
                            post_price.setText("均价:暂无");
                        }

                        post_price.setTextSize(12);
                        container.addView(child);
                        String iconUrl = bean.getIconUrl();
                        if (TextUtils.isEmpty(iconUrl)) {
                            iconUrl = AppContents.POST_DEFAULT_IMG_URL;
                        } else {
                            iconUrl = iconUrl.substring(0, iconUrl.indexOf(".")) + "_" + 400 + "x" + 300 + "_f.jpg";
                        }
                        GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.NEW_HOUSE_IMG+iconUrl)
                                .into(post_img));
                    }
                } else {
                    showNoDataLayout(items,"暂无符合意向房源");
                }
            }
        });
    }


    /**
     * 创建tag
     * @param text
     */
    private void createTag(FlowTag parent,String text) {
        final CheckBox cb = (CheckBox) LayoutInflater.from(parent.getContext()).inflate(R.layout.check_box_1, parent, false);
        cb.setEnabled(false);
        if (text.length() < 15) {
            cb.setText(text);
        } else {
            cb.setText(text.substring(0,14));
        }
        parent.addView(cb);
    }

    private void getTages(FlowTag group,IntentionBo intentionBo) {
        params.clear();
        group.removeAllViews();
        if (!type.equals(SubscribePostFragment.XINFANG)) {
            if (type.equals(SubscribePostFragment.POST)) {
                params.put("PostType", "S");
            } else {
                params.put("PostType", "R");
            }
        }

        List<SearchParam> ps = intentionBo.getSearchPara();
        for (SearchParam p:ps) {
            if ("RegionId".equalsIgnoreCase(p.getParamKey())
                    || "GScopeId".equalsIgnoreCase(p.getParamKey())) {
                Observable.just(Integer.parseInt(p.getValue()))
                        .map(integer -> DbUtil.getGScopeById(Integer.parseInt(p.getValue())))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(gScope -> {
                            if (gScope != null) {
                                createTag(group, gScope.getGScopeName());
                                p.setText(gScope.getGScopeName());
                            }
                        });
            }else if("RailWayId".equalsIgnoreCase(p.getParamKey())){
                RailWay railWay = DbUtil.getRailWayById(p.getValue());
                if (railWay!=null){
                    createTag(group, railWay.getRailWayName());
                    p.setText(railWay.getRailWayName());
                }
            }else if ("RailLineId".equalsIgnoreCase(p.getParamKey())){
                RailLine railLine = DbUtil.getRailLineById(p.getValue());
                if (railLine!=null){
                    createTag(group, railLine.getRailLineName());
                    p.setText(railLine.getRailLineName());
                }
            }else {
                createTag(group,p.getText());
            }

            String[] paramKeys = p.getParamKey().split(",");
            if (paramKeys!=null && paramKeys.length>1){
                String[] values = p.getValue().split(",");

                if ("MinOpdate,MaxOpdate".equals(p.getParamKey())){
                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    int minYear = year - Integer.parseInt(values[1]);
                    int maxYear = year - Integer.parseInt(values[0]);

                    try {
                        Date minDate = format.parse(minYear+"-1-1");
                        Date maxDate = format.parse(maxYear+"-12-31");

                        long minTime = minDate.getTime()/1000;
                        long maxTime = maxDate.getTime()/1000;

                        params.put(paramKeys[0], minTime+"");
                        params.put(paramKeys[1], maxTime+"");
//                        p.setValue(minTime+","+maxTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else if ("OpDateBegin,OpDateEnd".equals(p.getParamKey())){
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    int startMonth = Integer.parseInt(values[0]);
                    int endMonth = Integer.parseInt(values[1]);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    calendar.add(Calendar.MONTH,  startMonth);
                    String startDate = format.format(calendar.getTime());

                    calendar.setTimeInMillis(System.currentTimeMillis());

                    calendar.add(Calendar.MONTH,  endMonth);
                    String endDate = format.format(calendar.getTime());
                    params.put(paramKeys[0], startDate);
                    params.put(paramKeys[1], endDate);
//                    p.setValue(startDate+","+endDate);
                }else {
                    params.put(paramKeys[0], values[0]);
                    params.put(paramKeys[1], values[1]);
                }
            }else {
                if (p.getParamKey().equals("Tags")){
                    if (params.get("Tags")!=null){
                        params.put("Tags", params.get("Tags")+"_"+p.getId());
                    }else {
                        params.put("Tags", p.getId()+"");
                    }
                }else if (p.getParamKey().equals("Feature")){
                    if (params.get("Feature")!=null){
                        params.put("Feature", params.get("Feature")+"_"+p.getId());
                    }else {
                        params.put("Feature", p.getId()+"");
                    }
                } else if (p.getParamKey().equals("Property")){
                    params.put("PropertyTypeList", p.getValue());
                }else {
                    params.put(p.getParamKey(), p.getValue());
                }
            }
        }

        params.put("ImageWidth","600");
        params.put("ImageHeight","400");

        if (type.equals(SubscribePostFragment.XINFANG)) {
            params.put("length","3");
            params.put("StartIndex","0");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FlowTag tags;
        public ImageView search;
        public LinearLayout items;
//        public TextView load_message;
        public TextView cancel;

        public ViewHolder(View itemView) {
            super(itemView);
            tags = (FlowTag) itemView.findViewById(R.id.tags);
            search = (ImageView) itemView.findViewById(R.id.search);
            items = (LinearLayout) itemView.findViewById(R.id.items);
//            load_message = (TextView) itemView.findViewById(R.id.load_message);
            cancel = (TextView) itemView.findViewById(R.id.cancel);
        }

    }

    public interface OnCancelSubscribe{
        void cancelSubscribe(int position, boolean optionSuccess);
    }

}
