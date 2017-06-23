package com.cetnaline.findproperty.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.StoreBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.inter.IRecycleViewListener;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.listadapter.StoreListAdapter;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.MRecyclerView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/10/21.
 */
public class StoreSearchDetailActivity extends BaseActivity {
    @BindView(R.id.back_tx)
    TextView back_tx;

    @BindView(R.id.search_type)
    TextView search_type;
    @BindView(R.id.search_edt)
    EditText search_edt;
    @BindView(R.id.list)
    MRecyclerView recyclerView;
    @BindView(R.id.estate_list)
    ListView estate_list;
    @BindView(R.id.no_data_layout)
    LinearLayout no_data_layout;
    @BindView(R.id.back_map)
    TextView back_map;

    private int type;  //0-门店 1-小区

    private int current_page;
    private boolean isAllLoad;

    private CompositeSubscription mCompositeSubscription;

    private EstateBo estateBo;
    private StoreListAdapter mAdapter;
    private List<EstateBo> estateBos;
    private EstateAdapter estateAdapter;

    public EstateBo getEstateBo() {
        return estateBo;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_store_search_detail;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        mCompositeSubscription = new CompositeSubscription();
        estateBos = new ArrayList<>();
        current_page = 1;
        isAllLoad = false;
        type = 0;
        back_tx.setOnClickListener(v->onBackPressed());
        back_map.setOnClickListener(v->onBackPressed());

        estateAdapter = new EstateAdapter();
        estate_list.setAdapter(estateAdapter);
        //选择小区搜索
        estate_list.setOnItemClickListener((parent, view, position, id) -> {
            estateBo = estateBos.get(position);
            loadData(true);
        });
        search_type.setOnClickListener(v->{
            setKeyboardModeHidden();
            showEstateList(false);
            View pView = LayoutInflater.from(this).inflate(R.layout.menu_type_dialog, null);
            Dialog dialog = new Dialog(this, R.style.FullHeightDialog); //
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(pView);
            Window win = dialog.getWindow();
            //win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));   //win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.width = MyUtils.dip2px(this, 100);
            lp.height = MyUtils.dip2px(this, 90);
            lp.gravity = Gravity.LEFT | Gravity.TOP;

            TextView menu1 = (TextView) pView.findViewById(R.id.menu1);
            TextView menu2 = (TextView) pView.findViewById(R.id.menu2);
            menu1.setOnClickListener(vv->{
                type = 0;
                search_type.setText(menu1.getText());
                search_edt.setHint("请输入小区名称");
                dialog.dismiss();
            });

            menu2.setOnClickListener(vv->{
                type = 1;
                search_type.setText(menu2.getText());
                search_edt.setHint("请输入门店名称");
                dialog.dismiss();
            });

            int[] loc = new int[2];
            search_type.getLocationOnScreen(loc);
            lp.x = MyUtils.dip2px(this, 5);
            lp.y = loc[1];
            win.setAttributes(lp);
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.menu_type_pop_bg);
        });

        search_edt.setOnEditorActionListener((textView,i,keyEvent)-> {
            if (i== EditorInfo.IME_ACTION_SEARCH ||(keyEvent!=null&&keyEvent.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
                if (type == 0) {
                    setKeyboardModeHidden();
                } else {
                    loadData(true);
                    mCompositeSubscription.add(
                            ApiRequest.wordFrequency("mendian", search_edt.getText().toString())
                            .subscribe(result -> {
                                if (result == 1) {
                                    Logger.i("保存词频成功");
                                }
                            }, throwable -> {
                                throwable.printStackTrace();
                            }));
                }
                return true;
            }
            return false;
        });

        search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Logger.i("afterTextChanged:"+s.toString());
                if (s == null || s.toString().equals("")) {
                    showEstateList(false);
                    return;
                }
                if (type == 0){
                    mCompositeSubscription.add(ApiRequest.getEstateList(new HashMap(){
                        {
                            put("EstateName",s.toString());
                            put("ImageWidth","1");
                            put("ImageHeight","1");
                            put("PageIndex","1");
                            put("PageCount","1000");
                        }
                    }).subscribe(new Action1<List<EstateBo>>() {
                        @Override
                        public void call(List<EstateBo> estateBos1) {
                            estateBos = estateBos1;
                            showEstateList(true);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                            showEstateList(false);
                        }
                    }));
                }

            }
        });

        recyclerView.setIRecycleViewListener(new IRecycleViewListener() {
            @Override
            public void downRefresh() {
                loadData(true);
            }

            @Override
            public void upRefresh() {
                if (type == 1) {
                    loadData(false);
                }
            }

            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onScroll() {

            }

            @Override
            public void loadDataAgain() {

            }
        });
        recyclerView.setDefaultText("");
        mAdapter = new StoreListAdapter(new ArrayList<StoreBo>(),R.layout.list_store_item, this);
        recyclerView.setAdapter(mAdapter,"已显示全部门店");
//        recyclerView.startRefresh();
    }

    /**
     * 设置键盘不自动弹出
     */
    private void setKeyboardModeHidden() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(search_edt.getWindowToken(), 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void showEstateList(boolean show){
        if (show) {
            estate_list.setVisibility(View.VISIBLE);
            estateAdapter.notifyDataSetChanged();
        } else {
            estate_list.setVisibility(View.GONE);
        }
    }

    private void showNoDataLayout(boolean show) {
        if (show) {
            no_data_layout.setVisibility(View.VISIBLE);
        } else {
            no_data_layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        showToolbar(false);
    }

    private void loadData(boolean isReload) {
        showNoDataLayout(false);
        showEstateList(false);
        if (isReload) {
            current_page = 1;
            isAllLoad = false;
            mAdapter.clear();
        }

        if (isAllLoad) {
            return;
        }
        showLoadingDialog();

        Map<String, String> params = new HashMap<>();
        if (type == 0) {
            //小区搜索门店 不分页
            if (estateBo != null) {
                params.put("PageIndex","1");
                params.put("PageCount","1000");
                params.put("Lng",estateBo.getLng()+"");
                params.put("Lat",estateBo.getLat()+"");
                params.put("Round","3000");

                mCompositeSubscription.add(Observable.combineLatest(ApiRequest.getStoreToEstatesSingle(new HashMap(){
                    {
                        put("CestCode",estateBo.getEstateCode());
                    }
                }),ApiRequest.searchStoreSingle(params), new Func2<List<StoreBo>, List<StoreBo>, List<StoreBo>>() {
                    @Override
                    public List<StoreBo> call(List<StoreBo> storeBos, List<StoreBo> storeBos2) {
                        if (storeBos == null) {
                            storeBos = new ArrayList<StoreBo>();
                        }
                        if (storeBos2 == null) {
                            storeBos2 = new ArrayList<StoreBo>();
                        }

                        if (storeBos2.size() > 1) {
                            for (int i=storeBos2.size()-1;i>0;i--) {
                                for (int j=0;j<i;j++) {
                                    double distance = MyUtils.getDistance(new LatLng(estateBo.getLat(), estateBo.getLng()),
                                            new LatLng(storeBos2.get(j).getLat(), storeBos2.get(j).getLng()));
                                    double distance1 = MyUtils.getDistance(new LatLng(estateBo.getLat(), estateBo.getLng()),
                                            new LatLng(storeBos2.get(j+1).getLat(), storeBos2.get(j+1).getLng()));
                                    if (distance > distance1) {
                                        StoreBo bo = storeBos2.get(j);
                                        storeBos2.set(j,storeBos2.get(j+1));
                                        storeBos2.set(j+1, bo);
                                    }
                                }
                            }
                        }

                        List<StoreBo> tmp = new ArrayList<StoreBo>();
                        tmp.addAll(storeBos);
                        for (StoreBo bo:storeBos2) {
                            boolean flag = true;
                            for (StoreBo bo1: storeBos) {
                                if (bo.getStoreName().equals(bo1.getStoreName())) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                tmp.add(bo);
                            }
                        }
                        return tmp;
                    }
                }).subscribe(new Action1<List<StoreBo>>() {
                    @Override
                    public void call(List<StoreBo> storeBos) {
                        cancelLoadingDialog();
                        if (storeBos.size() > 0) {
                            showNoDataLayout(false);
                            mAdapter.add(storeBos);
                            recyclerView.stopRefresh(false);
                        } else {
                            showNoDataLayout(true);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        showNoDataLayout(true);
                    }
                }));
            } else {
                cancelLoadingDialog();
                showNoDataLayout(true);
                recyclerView.stopRefresh(false);
                return;
            }
        } else {
            //门店名称搜索 分页
            params.put("PageIndex",current_page + "");
            params.put("PageCount","10");
            params.put("StoreName",search_edt.getText().toString());
            mCompositeSubscription.add(ApiRequest.searchStore(params).subscribe(new Action1<List<StoreBo>>() {
                @Override
                public void call(List<StoreBo> storeBos) {
                    cancelLoadingDialog();
                    showNoDataLayout(false);
                    if (storeBos != null && storeBos.size() > 0) {
                        current_page++;
                        mAdapter.add(storeBos);
                        recyclerView.stopRefresh(false);
                        if (storeBos.size() < 10) {
                            isAllLoad = true;
                        }
                    } else {
                        isAllLoad = true;
                    }
                    if (mAdapter.getItemCount() == 0) {
                        showNoDataLayout(true);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    cancelLoadingDialog();
                    showNoDataLayout(true);
                    throwable.printStackTrace();
                }
            }));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    public class EstateAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return StoreSearchDetailActivity.this.estateBos.size();
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
            Holder holder = null;
            if(convertView == null)
            {
                holder = new Holder();
                convertView = StoreSearchDetailActivity.this.getLayoutInflater().inflate(R.layout.estate_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            String search_str = search_edt.getText().toString();
            SpannableString styledText = new SpannableString(estateBos.get(position).getEstateName());
            int index = estateBos.get(position).getEstateName().indexOf(search_str);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
            if (index >= 0) {
                styledText.setSpan(span, index, index + search_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            index = estateBos.get(position).getEstateName().indexOf(MyUtils.numberForWord(search_str));
            if (index >= 0) {
                styledText.setSpan(span, index, index + search_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            holder.title.setText(styledText);
            return convertView;
        }

        public class Holder {
            public TextView title;
        }

    }

    @Override
    protected String getTalkingDataPageName() {
        return "门店搜索详情页";
    }
}
