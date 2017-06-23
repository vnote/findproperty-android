package com.cetnaline.findproperty.widgets.chart;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

/**
 * 饼图基类
 * Created by guilin on 16/4/20.
 */
public class AbsPieChart extends PieChart {

    public AbsPieChart(Context context) {
        this(context, null);
    }

    public AbsPieChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initStyle();
        initLegend();
    }

    /**
     * 自定义饼图样式
     */
    protected void initStyle() {
        setDescription("");//饼图描述文字
        setNoDataTextDescription("没有数据");//空数据时默认文字
        setExtraOffsets(10, 20, 10, 20);
        setDrawHoleEnabled(true);//设置空心
        setHoleRadius(75f);//空心半径
        setTransparentCircleRadius(0f);//透明层半径
        setDrawCenterText(true);//中心文字
        setDrawSliceText(false);//x轴文字不显示在饼图上
        setRotationAngle(-90);//设置开始旋转角度
        setRotationEnabled(true);//手动旋转

        //增加动画
//        animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    /**
     * 自定义标签样式
     */
    protected void initLegend() {
        Legend mLegend = getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);  //右侧中间显示
        mLegend.setForm(Legend.LegendForm.CIRCLE);  //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(5f);
        mLegend.setYEntrySpace(12f);
        mLegend.setTextSize(14f);
    }

}
