package com.cetnaline.findproperty.widgets.chart;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.cetnaline.findproperty.widgets.chart.AbsPieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 房贷计算饼图
 * Created by diaoqf on 2016/8/15.
 */
public class LoanPieChart extends AbsPieChart {

    private final int Color1 = Color.rgb(255, 225, 85);
    private final int Color2 = Color.rgb(255, 54, 21);
    private final int Color3 = Color.rgb(69, 176, 237);

    public LoanPieChart(Context context) {
        this(context, null);
    }

    public LoanPieChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoanPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setData(double price1, double price2, double price3, double price4, String... texts) {

        ArrayList<String> xValues = new ArrayList<>();  //xValues用来表示每个饼块上的内容
        ArrayList<Entry> yValues = new ArrayList<>();  //yValues用来表示封装每个饼块的实际数据
        ArrayList<Integer> colors = new ArrayList<>();//饼图颜色
        double total = price1 + price2 + price3;

        Locale locale;
        if (SharedPreferencesUtil.getInt(AppContents.LANGUAGE) == AppContents.LANGUAGE_CHINESE) {
            locale = Locale.CHINA;
        } else {
            locale = Locale.ENGLISH;
        }
        xValues.add(String.format(locale, "%s : %.0f万", texts[0], price1 / 10000));
        xValues.add(String.format(locale, "%s : %.0f万", texts[1], price2 / 10000));
        yValues.add(new Entry((float) (price1 / total), 0));
        yValues.add(new Entry((float) (price2 / total), 1));
        colors.add(Color1);
        colors.add(Color2);
        if (texts.length > 2) {
            xValues.add(String.format(Locale.CHINA, "%s : %.0f万", texts[2], price3 / 10000));
            yValues.add(new Entry((float) (price3 / total), 2));
            colors.add(Color3);
        }

        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(2f); //设置个饼状图之间的距离
        pieDataSet.setDrawValues(false);//不显示值
        pieDataSet.setColors(colors);
        pieDataSet.setSelectionShift(5f); // 选中时态溢出的长度

        setData(new PieData(xValues, pieDataSet));
        setCenterTextSize(12);
        setCenterTextColor(Color.rgb(102, 102, 102));
        setCenterText(String.format(locale, "月供\n%.0f元/月", price4));
        invalidate();
    }
}
