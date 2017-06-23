package com.cetnaline.findproperty.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.entity.ui.LoanCalculateBo;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.widgets.MyBottomDialog;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by diaoqf on 2016/8/15.
 */
public abstract class BaseToolFragment extends BaseFragment {

    protected final String[] ratios = new String[]{"2成", "2.5成", "3成", "3.5成" ,"4成", "5成", "6成", "7成", "8成", "9成"};
    protected final double[] ratiosDouble = new double[]{2, 2.5, 3, 3.5,4, 5, 6, 7, 8, 9};
    protected final String[] loanTypes = new String[]{"等额本息", "等额本金"};

    protected ArrayList<DropBo> bizRateArrayList = new ArrayList<>();//商业利率
    protected ArrayList<DropBo> fundRateArrayList = new ArrayList<>();//公积金利率

    protected MyBottomDialog ratioDialog;//首付比例
    protected MyBottomDialog loanTypeDialog;//贷款方式

    protected int mode_calc_type = 1;//计算方式,默认按揭
    protected double ratio;//按揭比例
    protected int loanType;//贷款方式
    protected double lastRate;//最终计算出的利率

    protected LoanCalculateCallback loanCalculateCallback;

    public void setLoanCalculateCallback(LoanCalculateCallback loanCalculateCallback) {
        this.loanCalculateCallback = loanCalculateCallback;
    }

    /**
     * 商业贷款利率初始化
     */
    protected void initBizRate() {
    }

    /**
     * 公积金贷款利率初始化
     */
    protected void initFundRate() {
    }

    /**
     * 设置计算点击
     */
    protected void setCalcClick(View view) {
        RxView.clicks(view)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startCalculate();
                    }
                });
    }

    /**
     * 开始计算
     */
    protected abstract void startCalculate();

    /**
     * 首付比例
     */
    protected void showRatioDialog() {
        hideKeyboard();
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(aLong -> {
                    if (ratioDialog == null) {
                        ratioDialog = new MyBottomDialog(getActivity());
                        ListView listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_list, null);
                        ratioDialog.setContentView(listView);
                        listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_bottom_dialog, ratios));
                        listView.setSelection(5);
                        listView.setOnItemClickListener((adapterView, view12, i, l) -> {
                            ratio = ratiosDouble[i];
                            ratioItemClick(ratios[i]);
                            ratioDialog.dismiss();
                        });
                    }
                    ratioDialog.show();
                });
    }

    private void hideKeyboard(){
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 首付比例
     */
    protected void ratioItemClick(String text) {

    }

    /**
     * 贷款方式
     */
    protected void showLoanTypeDialog() {
        hideKeyboard();
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(aLong -> {
                    if (loanTypeDialog == null) {
                        loanTypeDialog = new MyBottomDialog(getActivity());
                        ListView listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_list, null);
                        loanTypeDialog.setContentView(listView);
                        listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_bottom_dialog, loanTypes));
                        listView.setOnItemClickListener((adapterView, view12, i, l) -> {
                            loanType = i;
                            loanTypeItemClick(loanTypes[i]);
                            loanTypeDialog.dismiss();
                        });
                    }
                    loanTypeDialog.show();
                });
    }

    /**
     * 贷款方式
     */
    protected void loanTypeItemClick(String text) {

    }

    /**
     * 商贷利率
     */
    protected void bizRate() {
        Observable.just("BizRate")
                .map((s)->DbUtil.getSearchDataByName(s))
                .compose(this.<List<DropBo>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<DropBo>>() {
                    @Override
                    public void call(List<DropBo> dropBos) {
                        bizRateArrayList.clear();
                        bizRateArrayList.addAll(dropBos);
                        initBizRate();
                    }
                });
    }

    /**
     * 公积金利率
     */
    protected void fundRate() {
        Observable.just("FundRate")
                .map((s)->DbUtil.getSearchDataByName(s))
                .compose(this.<List<DropBo>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<DropBo>>() {
                    @Override
                    public void call(List<DropBo> dropBos) {
                        fundRateArrayList.clear();
                        fundRateArrayList.addAll(dropBos);
                        initFundRate();
                    }
                });
    }

    /**
     * 房贷计算器回调
     * Created by guilin on 16/4/26.
     */
    public interface LoanCalculateCallback {

        void callBack(LoanCalculateBo loanCalculateBo);
    }
}
