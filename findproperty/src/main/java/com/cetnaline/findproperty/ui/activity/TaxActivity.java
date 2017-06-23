package com.cetnaline.findproperty.ui.activity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.entity.ui.TaxCalculateBo;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.BaseTaxCalFragment;
import com.cetnaline.findproperty.ui.fragment.HouseColcFragment;
import com.cetnaline.findproperty.ui.listadapter.MyFragmentPagerAdapter;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.CustomTabEntity;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by diaoqf on 2016/8/16.
 */
public class TaxActivity extends BaseActivity {
    public static final String TOTAL_AREA = "total_area";
    public static final String UNIT_PRICE = "unit_price";
    public static final String TOTAL_PRICE = "total_price";

    public static final String TAX_BUNDLE = "tax_bundle";  //税费
    public static final double BUSINESS_TAX_RATE = 5.38;

    @BindView(R.id.tab_bar)
    CommonTabLayout tab_bar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.overView)
    View overView;
    BottomSheetBehavior bottomSheetBehavior;

    @BindView(R.id.ll_result)
    LinearLayout ll_result;
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
    @BindView(R.id.atv_title6)
    AppCompatTextView atv_title6;
    @BindView(R.id.atv_des6)
    AppCompatTextView atv_des6;
    @BindView(R.id.atv_title7)
    AppCompatTextView atv_title7;
    @BindView(R.id.atv_des7)
    AppCompatTextView atv_des7;

    private Resources resources;
    private ArrayList<CustomTabEntity> mTabEntities;
    private ArrayList<Fragment> fragments;

    private double business_tax;     //增值税（含附加税)
    private double ge_tax;     //个税

    private double total_area;
    private double unit_price;
    private double total_price;

    private BaseTaxCalFragment.TaxCalculateCallback taxCalculateCallback = new BaseTaxCalFragment.TaxCalculateCallback() {
        @Override
        public void callBack(TaxCalculateBo taxCalculateBo) {
            calculateResult(taxCalculateBo);
        }
    };


    @Override
    protected int getContentViewId() {
        return R.layout.act_tax;
    }


    /**
     * 初始化tab
     */
    private void initTab() {
        mTabEntities = new ArrayList(){
            {
                add(new TabEntity(resources.getString(R.string.map_query_1_1),R.drawable.benefit_sel,R.drawable.benefit));
                add(new TabEntity(resources.getString(R.string.map_query_1_3),R.drawable.pan_sel,R.drawable.pan));
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
        fragments.add(Fragment.instantiate(this,"com.cetnaline.findproperty.ui.fragment.HouseColcFragment"));
        fragments.add(Fragment.instantiate(this,"com.cetnaline.findproperty.ui.fragment.NewHouseCalcFragment"));

        if (total_area > 0.0 || unit_price > 0.0) {
            Bundle bundle = new Bundle();
            bundle.putDouble(HouseColcFragment.AREA, total_area);
            bundle.putDouble(HouseColcFragment.TAX_DAN_PRICE, unit_price);
            bundle.putDouble(HouseColcFragment.TAX_TOTAL_PRICE, total_price);
            fragments.get(0).setArguments(bundle);
        }

        for (Fragment fragment:fragments) {
            ((BaseTaxCalFragment)fragment).setTaxCalculateCallback(taxCalculateCallback);
        }

        Bundle bundle = getIntent().getBundleExtra(TAX_BUNDLE);
        if (bundle != null) {
            fragments.get(0).setArguments(bundle);
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


    /**
     * 计算结果
     */
    private void calculateResult(TaxCalculateBo taxCalculateBo) {

        double houseTotal;
        int modeType = taxCalculateBo.modeType;
        int houseType = taxCalculateBo.houseType;  //1."普通住宅",2."非普通住宅"
        double houseArea = taxCalculateBo.houseArea;
        int buyYear = taxCalculateBo.buyYear;   // 房屋年限  0:"2年以内"  ; 1:"2-5年" ; 2:"5年以上"
        double houseUnitPrice = taxCalculateBo.houseUnitPrice;
        boolean buyerFirst = taxCalculateBo.buyerFirst;
        boolean sellerOnly = taxCalculateBo.sellerOnly;
        int levyType = taxCalculateBo.levyType;   //征收类型  1,总价 2,差价
        double originPrice = taxCalculateBo.originPrice * 10000;    //原价
        switch (modeType) {
            //二手房
            case 1:
                double qi_tax;     //契税
                houseTotal = taxCalculateBo.houseTotal * 10000;    //房屋总额
                //契税
                if (buyerFirst) {
                    if (houseArea > 90) {
                        qi_tax = houseTotal * 1.5 / 100;
                    } else {
                        qi_tax = houseTotal / 100;
                    }
                } else {
                    qi_tax = houseTotal * 3 / 100;
                }
                //增值税（含附加税)
                if (!sellerOnly && levyType == 2 && buyYear == 1 && houseType == 2) {
                    business_tax = (houseTotal - originPrice) * BUSINESS_TAX_RATE / 100;
                } else if (buyYear == 0 || (buyYear == 1 && houseType == 2) || (buyYear == 2 && houseType == 2 && !sellerOnly)) {
                    business_tax = houseTotal * BUSINESS_TAX_RATE / 100;
                } else {
                    business_tax = 0;
                }
                //个税
                if (buyYear == 2 && sellerOnly) {
                    ge_tax = 0;
                } else if (houseTotal < originPrice && levyType == 2) {
                    ge_tax = 0;
                } else {
                    if (levyType == 1) {
                        if (houseType == 1)
                            ge_tax = houseTotal / 100;
                        else
                            ge_tax = houseTotal * 2 / 100;
                    } else {
                        ge_tax = (houseTotal - originPrice) * 20 / 100;
                    }
                }

                int qiiTax = doubleToInt(qi_tax);
                int businessTax = doubleToInt(business_tax);
                int geTax = doubleToInt(ge_tax);
                int total_tax = qiiTax + businessTax + 5 + geTax;

                atv_title1.setText("契税");
                atv_title2.setText("增值税（含附加税)");
                atv_title3.setText("印花税");
                atv_title4.setText("个人所得税");
                atv_title5.setText("总计");
                atv_title6.setText("");
                atv_title7.setText("");
                atv_des1.setText(String.format(Locale.CHINA, "%d元", qiiTax));
                atv_des2.setText(String.format(Locale.CHINA, "%d元", businessTax));
                atv_des3.setText("5元");
                atv_des4.setText(String.format(Locale.CHINA, "%d元", geTax));
                atv_des5.setText(String.format(Locale.CHINA, "%d元", total_tax));
                atv_des6.setText("");
                atv_des7.setText("");

                break;
            //新房
            case 2:
                double newQiTax;
                houseTotal = houseArea * houseUnitPrice;    //房屋总额
                //印花税
                double yh_tax = houseTotal * 5 / 10000;
                //契税
                if (houseArea <= 90) {
                    newQiTax = houseTotal / 100;
                } else if (houseArea > 144) {
                    newQiTax = houseTotal * 3 / 100;
                } else {
                    newQiTax = houseTotal * 1.5 / 100;
                }
                //公证费
                double gz_tax = newQiTax / 10;
                //委托办理产权手续费
                double wtbl_tax = houseTotal * 3 / 1000;
                //房屋买卖手续费
                double mm_tax;
                if (houseArea <= 120) {
                    mm_tax = 500;
                } else if (houseArea > 120 && houseArea < 5001) {
                    mm_tax = 1500;
                } else {
                    mm_tax = 5000;
                }
                int hTotal = doubleToInt(houseTotal);
                int yhTax = doubleToInt(yh_tax);
                int gzTax = doubleToInt(gz_tax);
                int qiTax = doubleToInt(newQiTax);
                int wtblTax = doubleToInt(wtbl_tax);
                int mmTax = doubleToInt(mm_tax);
                //合计
                int total = hTotal + yhTax + gzTax + qiTax + wtblTax + mmTax;

                atv_title1.setText("房屋总额");
                atv_title2.setText("印花税");
                atv_title3.setText("公证费");
                atv_title4.setText("契税");
                atv_title5.setText("委托办理产权手续费");
                atv_title6.setText("房屋买卖手续费");
                atv_title7.setText("合计");
                atv_des1.setText(String.format(Locale.CHINA, "%d元", hTotal));
                atv_des2.setText(String.format(Locale.CHINA, "%d元", yhTax));
                atv_des3.setText(String.format(Locale.CHINA, "%d元", gzTax));
                atv_des4.setText(String.format(Locale.CHINA, "%d元", qiTax));
                atv_des5.setText(String.format(Locale.CHINA, "%d元", wtblTax));
                atv_des6.setText(String.format(Locale.CHINA, "%d元", mmTax));
                atv_des7.setText(String.format(Locale.CHINA, "%d元", total));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                break;

            default:
                break;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * 四舍五入取整
     */
    public int doubleToInt(Double d) {
        BigDecimal inter = new BigDecimal(d).setScale(0, BigDecimal.ROUND_HALF_UP);
        return inter.intValue();
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        resources = getResources();

        total_area = getIntent().getDoubleExtra(TOTAL_AREA, 0.0);
        unit_price = getIntent().getDoubleExtra(UNIT_PRICE, 0.0);
        total_price = getIntent().getDoubleExtra(TOTAL_PRICE, 0.0);

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

        initTab();
        initPageView();

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

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        toolbar.setTitle("税费计算");
    }

    @Override
    protected String getTalkingDataPageName() {
        return "税费计算";
    }
}
