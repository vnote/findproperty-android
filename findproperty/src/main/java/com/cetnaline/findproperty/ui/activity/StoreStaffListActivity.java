package com.cetnaline.findproperty.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.result.BaseResult;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/10/20.
 */

public class StoreStaffListActivity extends BaseActivity {

    public static final String STORE_NAME = "store_name";
    public static final String STORE_ID = "store_id";

    private String storeId;

    private CompositeSubscription mCompositeSubscription;

    @BindView(R.id.list)
    ListView list;

    private StoreStaffListAdapter mAdapter;
    private List<Map<String, String>> datas;

    @Override
    protected int getContentViewId() {
        return R.layout.act_store_staff_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mCompositeSubscription = new CompositeSubscription();
        storeId = getIntent().getStringExtra(STORE_ID);
        center_title.setText(getIntent().getStringExtra(STORE_NAME));

        datas = new ArrayList<>();
        mAdapter = new StoreStaffListAdapter(this);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(StoreStaffListActivity.this, AdviserDetailActivity.class);
                StaffListBean bean = new StaffListBean();
                bean.setCnName(datas.get(position).get("name"));
                bean.setStaffImg(datas.get(position).get("img"));
                bean.setStaffNo(datas.get(position).get("no"));
                bean.setMobileBy400(datas.get(position).get("MobileBy400"));
                i.putExtra(AdviserDetailActivity.ADVISER, bean);
                startActivity(i);
            }
        });

        loadData();
    }

    private void loadData(){
        showLoadingDialog();
        if (storeId != null) {
            //测试数据
            mCompositeSubscription.add(ApiRequest.getStaffs(new HashMap(){
                {
                    put("StoreID",storeId);
                    put("PageIndex","1");
                    put("PageCount","1000");
                }
            }).flatMap(new Func1<BaseResult<StaffListBean>, Observable<List<Map<String, String>>>>() {
                @Override
                public Observable<List<Map<String, String>>> call(BaseResult<StaffListBean> staffListBeanBaseResult) {
                    List<Map<String, String>> tmp = new ArrayList<Map<String, String>>();
                    if (staffListBeanBaseResult.Result != null && staffListBeanBaseResult.Result.size() > 0) {
                        for (StaffListBean bo : staffListBeanBaseResult.Result) {
                            GScope sub = DbUtil.getGScopeById(Integer.parseInt(bo.getGscopeID()));
                            GScope par = DbUtil.getGScopeById(sub.getParentId());
                            tmp.add(new HashMap() {
                                {
                                    put("no",bo.getStaffNo());
                                    put("name", bo.getCnName());
                                    put("img", bo.getStaffImg());
                                    put("number",bo.getMobileBy400());
                                    put("content", bo.getStoreName()+ "["+par.getGScopeName()+" "+sub.getGScopeName()+"]");
                                }
                            });
                        }
                    }
                    return Observable.just(tmp);
                }
            }).subscribe(new Action1<List<Map<String, String>>>() {
                @Override
                public void call(List<Map<String, String>> maps) {
                    datas = maps;
                    mAdapter.notifyDataSetChanged();
                    cancelLoadingDialog();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    cancelLoadingDialog();
                }
            }));
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v)->{
            StoreStaffListActivity.this.onBackPressed();
        });
        toolbar.setTitle("");
        center_title.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    public class StoreStaffListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public StoreStaffListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();

                convertView = mInflater.inflate(R.layout.list_adviser_item, null);
                holder.adviser_img = (CircleImageView) convertView.findViewById(R.id.adviser_img);
                holder.adviser_name = (TextView)convertView.findViewById(R.id.adviser_name);
                holder.adviser_content = (TextView)convertView.findViewById(R.id.adviser_content);
                holder.adviser_phone = (ImageView) convertView.findViewById(R.id.adviser_phone);
                holder.adviser_connect = (ImageView) convertView.findViewById(R.id.adviser_connect);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            GlideLoad.initRound(StoreStaffListActivity.this)
                    .load(datas.get(position).get("img"))
                    .placeholder(io.rong.imkit.R.drawable.rc_default_portrait)
                    .error(io.rong.imkit.R.drawable.rc_default_portrait)
                    .fitCenter()
                    .into(holder.adviser_img);

            holder.adviser_name.setText(datas.get(position).get("name"));
            holder.adviser_content.setText(datas.get(position).get("content"));

            holder.adviser_phone.setOnClickListener(v-> MyUtils.toCall400(
                    StoreStaffListActivity.this,
                    datas.get(position).get("number"),
                    datas.get(position).get("name")));

            holder.adviser_connect.setOnClickListener(v->{
                String targetId = "s_021_" + datas.get(position).get("no").toLowerCase();
                String title = datas.get(position).get("name");
                RongIM.getInstance().startConversation(StoreStaffListActivity.this, Conversation.ConversationType.PRIVATE, targetId, title);  //s_021_aa75795 s_021_aa76088 s_021_aa76027
            });

            return convertView;
        }

        public final class ViewHolder{
            public CircleImageView adviser_img;
            public TextView adviser_name;
            public TextView adviser_content;

            public ImageView adviser_phone;
            public ImageView adviser_connect;
        }
    }

    @Override
    protected String getTalkingDataPageName() {
        return "门店经纪人列表";
    }
}
