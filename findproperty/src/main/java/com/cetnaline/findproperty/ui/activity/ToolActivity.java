package com.cetnaline.findproperty.ui.activity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.ui.LoanCalculateBo;
import com.cetnaline.findproperty.ui.fragment.ToolFragment1;
import com.cetnaline.findproperty.ui.listadapter.MyFragmentPagerAdapter;
import com.cetnaline.findproperty.utils.CalculateUtil;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.chart.LoanPieChart;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.BaseToolFragment;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.CustomTabEntity;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by diaoqf on 2016/8/13.
 */
public class ToolActivity extends BaseActivity {

    public static final String TOTAL_PRICE = "total_price";

    private final int Color1 = Color.rgb(255, 225, 85);
    private final int Color2 = Color.rgb(255, 54, 21);
    private final int Color3 = Color.rgb(69, 176, 237);


    @BindView(R.id.tab_bar)
    CommonTabLayout tab_bar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.overView)
    View overView;
    @BindView(R.id.ll_result)
    LinearLayout ll_result;
    @BindView(R.id.loanPieChart)
    LoanPieChart loanPieChart;
    @BindView(R.id.atv_title1)
    AppCompatTextView atv_title1;
    @BindView(R.id.atv_des1)
    AppCompatTextView atv_des1;
    @BindView(R.id.atv_title2)
    AppCompatTextView atv_title2;
    @BindView(R.id.atv_des2)
    AppCompatTextView atv_des2;
    @BindView(R.id.atv_title3)
    AppCompatTextView atv_title3;
    @BindView(R.id.atv_des3)
    AppCompatTextView atv_des3;
    @BindView(R.id.atv_title4)
    AppCompatTextView atv_title4;
    @BindView(R.id.atv_des4)
    AppCompatTextView atv_des4;
    @BindView(R.id.atv_title5)
    AppCompatTextView atv_title5;
    @BindView(R.id.atv_des5)
    AppCompatTextView atv_des5;

    private BottomSheetBehavior bottomSheetBehavior;

    private ArrayList<CustomTabEntity> mTabEntities;
    private Resources resources;
    private ArrayList<Fragment> fragments;
    private double total_price;

    private BaseToolFragment.LoanCalculateCallback loanCalculateCallback = new BaseToolFragment.LoanCalculateCallback() {
        @Override
        public void callBack(LoanCalculateBo loanCalculateBo) {
            calculateResult(loanCalculateBo);
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.act_tool;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        resources = getResources();
        total_price = getIntent().getDoubleExtra(TOTAL_PRICE,0);
        initTab();
        initPageView();

        bottomSheetBehavior = BottomSheetBehavior.from(ll_result);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                overView.setAlpha(slideOffset);
                overView.setVisibility(slideOffset > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * 初始化tab
     */
    private void initTab() {
        mTabEntities = new ArrayList(){
            {
                add(new TabEntity(resources.getString(R.string.tool_tab_bar_1),R.drawable.benefit_sel,R.drawable.benefit));
                add(new TabEntity(resources.getString(R.string.tool_tab_bar_2),R.drawable.pan_sel,R.drawable.pan));
                add(new TabEntity(resources.getString(R.string.tool_tab_bar_3),R.drawable.location_sel,R.drawable.location));
//                add(new TabEntity(resources.getString(R.string.tool_tab_bar_4),R.drawable.chat_sel,R.drawable.chat));
            }
        };

        tab_bar.setTabData(mTabEntities);
        tab_bar.setCurrentTab(0);
        tab_bar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    /**
     * 初始化pagerview
     */
    private void initPageView(){
        fragments = new ArrayList<>();
        if (total_price > 0) {
            Bundle bundle = new Bundle();
            bundle.putDouble(ToolFragment1.LOAN_PRICE, total_price);
            fragments.add(Fragment.instantiate(this,"com.cetnaline.findproperty.ui.fragment.ToolFragment1",bundle));
            fragments.add(Fragment.instantiate(this,"com.cetnaline.findproperty.ui.fragment.ToolFragment2",bundle));
        } else {
            fragments.add(Fragment.instantiate(this,"com.cetnaline.findproperty.ui.fragment.ToolFragment1"));
            fragments.add(Fragment.instantiate(this,"com.cetnaline.findproperty.ui.fragment.ToolFragment2"));
        }
        fragments.add(Fragment.instantiate(this,"com.cetnaline.findproperty.ui.fragment.ToolFragment3"));

        for (Fragment fragment:fragments) {
            ((BaseToolFragment)fragment).setLoanCalculateCallback(loanCalculateCallback);
        }

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tab_bar.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        toolbar.setTitle("贷款计算");
    }

    @OnClick({R.id.overView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.overView:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            default:
                break;
        }
    }


    /**
     * 计算结果
     */
    private void calculateResult(LoanCalculateBo loanCalculateBo) {
        ArrayList<String> xValues = new ArrayList<>();  //xValues用来表示每个饼块上的内容
        ArrayList<Entry> yValues = new ArrayList<>();  //yValues用来表示封装每个饼块的实际数据
        ArrayList<Integer> colors = new ArrayList<>();//饼图颜色

        int modeType = loanCalculateBo.modeType;
        int loanType = loanCalculateBo.loanType;
        int month = loanCalculateBo.month;
        boolean businessRateEnable = loanCalculateBo.businessRate != 0;
        double rate = businessRateEnable ?
                loanCalculateBo.businessRate : loanCalculateBo.fundRate;
        switch (modeType) {
            case 0://贷款总额
                if (loanType == 0) {//等额本息
                    double per = CalculateUtil.countBenXi(
                            loanCalculateBo.businessPrice * 10000,
                            rate / 100,
                            month);
                    double daikuan = loanCalculateBo.businessPrice;
                    double totalLixi = per * month - daikuan * 10000;
                    double total = totalLixi + daikuan * 10000;
                    xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "贷款总额", daikuan));
                    if (totalLixi > 10000) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "利息总额", totalLixi / 10000));
                    } else {
                        xValues.add(String.format(Locale.CHINA, "%s : %.0f元", "利息总额", totalLixi));
                    }
                    yValues.add(new Entry((float) (daikuan * 10000 / total), 0));
                    yValues.add(new Entry(totalLixi / total < 0.01 ? 0.01f : (float) (totalLixi / total), 1));
                    colors.add(Color1);
                    colors.add(Color2);

                    setPieChartDefault(xValues, yValues, colors, per, "参考月供");

                    atv_title1.setText("贷款年数");
                    atv_des1.setText(String.format(Locale.CHINA, "%d年(%d期)", month / 12, month));
                    atv_title2.setText(businessRateEnable ? "商贷利率" : "公积金利率");
                    atv_des2.setText(String.format(Locale.CHINA, "%.2f%%", rate));
                    atv_title3.setText("");
                    atv_des3.setText("");
                    atv_title4.setText("");
                    atv_des4.setText("");
                    atv_title5.setText("");
                    atv_des5.setText("");
                } else {//等额本金
                    double daikuan = loanCalculateBo.businessPrice;
                    double benjin = CalculateUtil.countBenJin(
                            daikuan * 10000,
                            rate / 100,
                            month);
                    double start = CalculateUtil.countBenJin4Month(
                            daikuan * 10000,
                            rate / 100,
                            month,
                            1);
                    double end = CalculateUtil.countBenJin4Month(
                            daikuan * 10000,
                            rate / 100,
                            month,
                            month);
                    double perBenjin = benjin / month;
                    double totalLixi = benjin - daikuan * 10000;
                    double total = totalLixi + daikuan * 10000;
                    xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "贷款总额", daikuan));
                    if (totalLixi > 10000) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "利息总额", totalLixi / 10000));
                    } else {
                        xValues.add(String.format(Locale.CHINA, "%s : %.0f元", "利息总额", totalLixi));
                    }
                    yValues.add(new Entry((float) (daikuan * 10000 / total), 0));
                    yValues.add(new Entry(totalLixi / total < 0.01 ? 0.01f : (float) (totalLixi / total), 1));
                    colors.add(Color1);
                    colors.add(Color2);

                    setPieChartDefault(xValues, yValues, colors, perBenjin, "平均月供");

                    atv_title1.setText("贷款年数");
                    atv_des1.setText(String.format(Locale.CHINA, "%d年(%d期)", month / 12, month));
                    atv_title2.setText(businessRateEnable ? "商贷利率" : "公积金利率");
                    atv_des2.setText(String.format(Locale.CHINA, "%.2f%%", rate));
                    atv_title3.setText("首月还贷");
                    atv_des3.setText(String.format(Locale.CHINA, "%.2f元", start));
                    atv_title4.setText("末月还贷");
                    atv_des4.setText(String.format(Locale.CHINA, "%.2f元", end));
                    atv_title5.setText("");
                    atv_des5.setText("");
                }
                break;
            case 1://按揭
                if (loanType == 0) {//等额本息
                    double per = CalculateUtil.countBenXi(
                            loanCalculateBo.totalPrice * (10 - loanCalculateBo.ratio) * 1000,
                            rate / 100,
                            month);
                    double shoufu = loanCalculateBo.totalPrice * loanCalculateBo.ratio / 10;
                    double daikuan = loanCalculateBo.totalPrice * (10 - loanCalculateBo.ratio) / 10;
                    double totalLixi = per * month - daikuan * 10000;
                    double total = totalLixi + shoufu * 10000 + daikuan * 10000;
                    xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "首付", shoufu));
                    xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "贷款总额", daikuan));
                    if (totalLixi > 10000) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "利息总额", totalLixi / 10000));
                    } else {
                        xValues.add(String.format(Locale.CHINA, "%s : %.0f元", "利息总额", totalLixi));
                    }
                    yValues.add(new Entry((float) (shoufu * 10000 / total), 0));
                    yValues.add(new Entry((float) (daikuan * 10000 / total), 1));
                    yValues.add(new Entry(totalLixi / total < 0.01 ? 0.01f : (float) (totalLixi / total), 2));
                    colors.add(Color1);
                    colors.add(Color2);
                    colors.add(Color3);

                    setPieChartDefault(xValues, yValues, colors, per, "参考月供");

                    atv_title1.setText("房屋总价");
                    atv_des1.setText(String.format(Locale.CHINA, "%.0f万", loanCalculateBo.totalPrice));
                    atv_title2.setText("贷款年数");
                    atv_des2.setText(String.format(Locale.CHINA, "%d年(%d期)", month / 12, month));
                    atv_title3.setText(businessRateEnable ? "商贷利率" : "公积金利率");
                    atv_des3.setText(String.format(Locale.CHINA, "%.2f%%", rate));
                    atv_title4.setText("");
                    atv_des4.setText("");
                    atv_title5.setText("");
                    atv_des5.setText("");
                } else {//等额本金
                    double benjin = CalculateUtil.countBenJin(
                            loanCalculateBo.totalPrice * (10 - loanCalculateBo.ratio) * 1000,
                            rate / 100,
                            month);
                    double start = CalculateUtil.countBenJin4Month(
                            loanCalculateBo.totalPrice * (10 - loanCalculateBo.ratio) * 1000,
                            rate / 100,
                            month,
                            1);
                    double end = CalculateUtil.countBenJin4Month(
                            loanCalculateBo.totalPrice * (10 - loanCalculateBo.ratio) * 1000,
                            rate / 100,
                            month,
                            month);
                    double perBenjin = benjin / month;
                    double shoufu = loanCalculateBo.totalPrice * loanCalculateBo.ratio / 10;
                    double daikuan = loanCalculateBo.totalPrice * (10 - loanCalculateBo.ratio) / 10;
                    double totalLixi = benjin - daikuan * 10000;
                    double total = totalLixi + shoufu * 10000 + daikuan * 10000;
                    xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "首付", shoufu));
                    xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "贷款总额", daikuan));
                    if (totalLixi > 10000) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "利息总额", totalLixi / 10000));
                    } else {
                        xValues.add(String.format(Locale.CHINA, "%s : %.0f元", "利息总额", totalLixi));
                    }
                    yValues.add(new Entry((float) (shoufu * 10000 / total), 0));
                    yValues.add(new Entry((float) (daikuan * 10000 / total), 1));
                    yValues.add(new Entry((float) totalLixi / total < 0.01 ? 0.01f : (float) (totalLixi / total), 2));
                    colors.add(Color1);
                    colors.add(Color2);
                    colors.add(Color3);

                    setPieChartDefault(xValues, yValues, colors, perBenjin, "平均月供");

                    atv_title1.setText("房屋总价");
                    atv_des1.setText(String.format(Locale.CHINA, "%.0f万", loanCalculateBo.totalPrice));
                    atv_title2.setText("贷款年数");
                    atv_des2.setText(String.format(Locale.CHINA, "%d年(%d期)", month / 12, month));
                    atv_title3.setText(businessRateEnable ? "商贷利率" : "公积金利率");
                    atv_des3.setText(String.format(Locale.CHINA, "%.2f%%", rate));
                    atv_title4.setText("首月还贷");
                    atv_des4.setText(String.format(Locale.CHINA, "%.2f元", start));
                    atv_title5.setText("末月还贷");
                    atv_des5.setText(String.format(Locale.CHINA, "%.2f元", end));
                }
                break;
            case 2:
                double bPrice = loanCalculateBo.businessPrice;
                double bRate = loanCalculateBo.businessRate / 100;
                double fPrice = loanCalculateBo.fundPrice;
                double fRate = loanCalculateBo.fundRate / 100;
                if (loanType == 0) {//等额本息
                    double per = CalculateUtil.countBenXi(bPrice * 10000,
                            bRate, month) +
                            CalculateUtil.countBenXi(fPrice * 10000,
                                    fRate, month);
                    double lixi = per * month - bPrice * 10000 - fPrice * 10000;
                    double total = lixi + bPrice * 10000 + fPrice * 10000;
                    if (fPrice > 0) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "公积金贷款", fPrice));
                    }
                    if (bPrice > 0) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "商业贷款", bPrice));
                    }
                    if (lixi > 10000) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "利息总额", lixi / 10000));
                    } else {
                        xValues.add(String.format(Locale.CHINA, "%s : %.0f元", "利息总额", lixi));
                    }
                    if (fPrice > 0) {
                        yValues.add(new Entry((float) (fPrice * 10000 / total), 0));
                        if (bPrice > 0) {
                            yValues.add(new Entry((float) (bPrice * 10000 / total), 1));
                            yValues.add(new Entry((float) (lixi / total), 2));
                        } else {
                            yValues.add(new Entry((float) (lixi / total), 1));
                        }
                    } else {
                        yValues.add(new Entry((float) (bPrice * 10000 / total), 0));
                        yValues.add(new Entry((float) (lixi / total), 1));
                    }

                    colors.add(Color1);
                    colors.add(Color2);
                    if (yValues.size() > 2) {
                        colors.add(Color3);
                    }

                    setPieChartDefault(xValues, yValues, colors, per, "参考月供");

                    atv_title1.setText("贷款年数");
                    atv_des1.setText(String.format(Locale.CHINA, "%d年(%d期)", month / 12, month));
                    if (bPrice > 0) {
                        atv_title2.setText("商贷利率");
                        atv_des2.setText(String.format(Locale.CHINA, "%.2f%%", bRate * 100));
                        if (fPrice > 0) {
                            atv_title3.setText("公积金利率");
                            atv_des3.setText(String.format(Locale.CHINA, "%.2f%%", fRate * 100));
                        } else {
                            atv_title3.setText("");
                            atv_des3.setText("");
                        }
                    } else {
                        atv_title2.setText("公积金利率");
                        atv_des2.setText(String.format(Locale.CHINA, "%.2f%%", fRate * 100));
                        atv_title3.setText("");
                        atv_des3.setText("");
                    }
                    atv_title4.setText("");
                    atv_des4.setText("");
                    atv_title5.setText("");
                    atv_des5.setText("");
                } else {//等额本金
                    double benjin = CalculateUtil.countBenJin(bPrice * 10000, bRate, month) +
                            CalculateUtil.countBenJin(fPrice * 10000, fRate, month);
                    double start = CalculateUtil.countBenJin4Month(bPrice * 10000, bRate, month, 1) +
                            CalculateUtil.countBenJin4Month(fPrice * 10000, fRate, month, 1);
                    double end = CalculateUtil.countBenJin4Month(bPrice * 10000, bRate, month, month) +
                            CalculateUtil.countBenJin4Month(fPrice * 10000, fRate, month, month);
                    double per = benjin / month;
                    double lixi = benjin - bPrice * 10000 - fPrice * 10000;
                    double total = lixi + bPrice * 10000 + fPrice * 10000;
                    if (fPrice > 0) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "公积金贷款", fPrice));
                    }
                    if (bPrice > 0) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "商业贷款", bPrice));
                    }
                    if (lixi > 10000) {
                        xValues.add(String.format(Locale.CHINA, "%s : %.2f万", "利息总额", lixi / 10000));
                    } else {
                        xValues.add(String.format(Locale.CHINA, "%s : %.0f元", "利息总额", lixi));
                    }
                    if (fPrice > 0) {
                        yValues.add(new Entry((float) (fPrice * 10000 / total), 0));
                        if (bPrice > 0) {
                            yValues.add(new Entry((float) (bPrice * 10000 / total), 1));
                            yValues.add(new Entry((float) (lixi / total), 2));
                        } else {
                            yValues.add(new Entry((float) (lixi / total), 1));
                        }
                    } else {
                        yValues.add(new Entry((float) (bPrice * 10000 / total), 0));
                        yValues.add(new Entry((float) (lixi / total), 1));
                    }
                    colors.add(Color1);
                    colors.add(Color2);
                    if (yValues.size() > 2) {
                        colors.add(Color3);
                    }

                    setPieChartDefault(xValues, yValues, colors, per, "平均月供");

                    atv_title1.setText("贷款年数");
                    atv_des1.setText(String.format(Locale.CHINA, "%d年(%d期)", month / 12, month));
                    if (bPrice > 0) {
                        atv_title2.setText("商贷利率");
                        atv_des2.setText(String.format(Locale.CHINA, "%.2f%%", bRate * 100));
                        if (fPrice > 0) {
                            atv_title3.setText("公积金利率");
                            atv_des3.setText(String.format(Locale.CHINA, "%.2f%%", fRate * 100));
                            atv_title4.setText("首月还贷");
                            atv_des4.setText(String.format(Locale.CHINA, "%.2f元", start));
                            atv_title5.setText("末月还贷");
                            atv_des5.setText(String.format(Locale.CHINA, "%.2f元", end));
                        } else {
                            atv_title3.setText("首月还贷");
                            atv_des3.setText(String.format(Locale.CHINA, "%.2f元", start));
                            atv_title4.setText("末月还贷");
                            atv_des4.setText(String.format(Locale.CHINA, "%.2f元", end));
                            atv_title5.setText("");
                            atv_des5.setText("");
                        }
                    } else {
                        atv_title2.setText("公积金利率");
                        atv_des2.setText(String.format(Locale.CHINA, "%.2f%%", fRate * 100));
                        atv_title3.setText("首月还贷");
                        atv_des3.setText(String.format(Locale.CHINA, "%.2f元", start));
                        atv_title4.setText("末月还贷");
                        atv_des4.setText(String.format(Locale.CHINA, "%.2f元", end));
                        atv_title5.setText("");
                        atv_des5.setText("");
                    }

                }
                break;
            default:
                break;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * 设置饼图默认值
     */
    private void setPieChartDefault(ArrayList<String> xValues, ArrayList<Entry> yValues,
                                    ArrayList<Integer> colors, double perMonth, String centerText) {
        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(2f); //设置个饼状图之间的距离
        pieDataSet.setDrawValues(false);//不显示值
        pieDataSet.setColors(colors);
        pieDataSet.setSelectionShift(5f); // 选中时态溢出的长度

        loanPieChart.setData(new PieData(xValues, pieDataSet));
        loanPieChart.setCenterTextSize(12);
        loanPieChart.setCenterTextColor(Color.rgb(102, 102, 102));
        loanPieChart.setCenterText(String.format(Locale.CHINA, "%s\n%.2f元/月", centerText, perMonth));
        loanPieChart.invalidate();
    }

    @Override
    protected String getTalkingDataPageName() {
        return "贷款计算";
    }
}
