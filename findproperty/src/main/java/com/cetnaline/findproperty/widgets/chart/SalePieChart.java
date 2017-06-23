package com.cetnaline.findproperty.widgets.chart;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.cetnaline.findproperty.utils.CalculateUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 二手房饼图
 * Created by guilin on 16/4/20.
 */
public class SalePieChart extends AbsPieChart {

    private final int totalPriceColor = Color.rgb(0, 0, 0);//总价
    private final int shoufuColor = Color.rgb(64, 192, 43);//首付
    private final int shangdaiColor = Color.rgb(255, 54, 21);//商贷
    private final int lixiColor = Color.rgb(37, 193, 227);//利息

    public SalePieChart(Context context) {
        this(context, null);
    }

    public SalePieChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SalePieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置总价和利率
     *
     * @param totalPrice 总价
     * @param rate       利率
     */
    public void setTotalPriceAndRate(double totalPrice, double rate) {
        if (totalPrice <= 0 ||
                rate <= 0)
            return;

        double shou = totalPrice * 0.35;//首付
        double dai = totalPrice * 0.65;//贷款部分
        double perMonth = CalculateUtil.countBenXi(dai, rate, 240);//每月还款
        double benXi = perMonth * 240;//等额本息
        double liXi = benXi - dai;//利息


        ArrayList<String> xValues = new ArrayList<>();  //xValues用来表示每个饼块上的内容
        xValues.add(String.format(Locale.CHINA, "总价 : %s万", totalPrice / 10000));
        xValues.add(String.format(Locale.CHINA, "首付 : %s万(3.5成)", MyUtils.format1(shou / 10000f)));
        xValues.add(String.format(Locale.CHINA, "商贷 : %s万", MyUtils.format1(dai / 10000f)));
        xValues.add(String.format(Locale.CHINA, "利息 : %s万", MyUtils.format1(liXi / 10000f)));

        double totalPie = totalPrice + liXi;

        ArrayList<Entry> yValues = new ArrayList<>();  //yValues用来表示封装每个饼块的实际数据
        yValues.add(new Entry(0, 0));
        yValues.add(new Entry((float) (shou / totalPie), 1));
        yValues.add(new Entry((float) (dai / totalPie), 2));
        yValues.add(new Entry((float) (liXi / totalPie), 3));

        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(2f); //设置个饼状图之间的距离
        pieDataSet.setDrawValues(false);//不显示值

        ArrayList<Integer> colors = new ArrayList<>();//饼图颜色
        colors.add(totalPriceColor);
        colors.add(shoufuColor);
        colors.add(shangdaiColor);
        colors.add(lixiColor);
        pieDataSet.setColors(colors);

        pieDataSet.setSelectionShift(5f); // 选中时态溢出的长度

        setData(new PieData(xValues, pieDataSet));
        setCenterTextSize(12);
        setCenterTextColor(Color.rgb(102, 102, 102));
        setCenterText(String.format(Locale.CHINA, "参考月供\n%.0f元/月", perMonth));
        invalidate();
    }
}
