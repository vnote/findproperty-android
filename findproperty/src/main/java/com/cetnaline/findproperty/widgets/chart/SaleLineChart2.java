package com.cetnaline.findproperty.widgets.chart;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.EstateDealPriceBo;
import com.cetnaline.findproperty.api.bean.GscopeDealPriceBo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * 二手房房价走势
 * Created by guilin on 16/4/21.
 */
public class SaleLineChart2 extends LineChart {

    private final int estateColor = Color.rgb(250, 213, 138);//小区色值
    private final int gscopColor = Color.rgb(254, 77, 99);//板块色值
    private final int cityColor = Color.rgb(135, 198, 251);//板块色值

    private List<EstateDealPriceBo> estateDealPriceBos;
    private List<GscopeDealPriceBo> gscopeDealPriceBos;
    private List<GscopeDealPriceBo> cityDealPriceBos;
    private SparseBooleanArray estateArray = new SparseBooleanArray();
    private SparseBooleanArray gscopArray = new SparseBooleanArray();
    private SparseBooleanArray cityArray = new SparseBooleanArray();

    private int xShow = 6;

    private OnChartValueSelectedListener onChartValueSelectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        }

        @Override
        public void onNothingSelected() {
        }
    };

    public SaleLineChart2(Context context) {
        this(context, null);
    }

    public SaleLineChart2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SaleLineChart2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initChartStyle();
        initLegend();
    }

    /**
     * 自定义图表样式
     */
    protected void initChartStyle() {
        setExtraOffsets(0, 20, 0, 20);
        setDescription("");//图标描述
        setNoDataTextDescription("没有数据");//无数据显示
        setDrawGridBackground(false);//不显示图表颜色
        setDrawBorders(false);//不添加表框
        setTouchEnabled(true);//可触摸
        setDragEnabled(false);//可拖拽
        setScaleEnabled(false);//禁止手动缩放
        setPinchZoom(true);
//        zoom(2f, 1f, 0, 0);//x轴放大2倍
        getAxisLeft().setEnabled(false);//去掉左侧y轴

        setOnChartValueSelectedListener(onChartValueSelectedListener);
        setMarkerView(new SaleLineMarkView(getContext(), R.layout.layout_sale_line_chart_marker_view));

//        animateX(2500);

        /*x坐标*/
        XAxis xAxis = getXAxis();
        xAxis.setTextColor(Color.rgb(102, 102, 102));//x轴色值
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴放置底部
        xAxis.setLabelsToSkip(0);//不忽略x轴点
        xAxis.setAxisMinValue(-0.5f);
        xAxis.setAxisMaxValue(7);
        xAxis.setValueFormatter(new XAxisValueFormatter() {
            @Override
            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int currentYear = calendar.get(Calendar.YEAR);
                calendar.add(Calendar.MONTH, index - xShow);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                if (currentYear == year) {//当前年份
                    return String.format(Locale.CHINA, "%d月", month);
                } else {//跨年
                    return String.format(Locale.CHINA, "%d-%d月", year % 100, month);
                }
            }
        });
        
        /*y坐标*/
        YAxis yAxis = getAxisRight();//右侧y轴
        yAxis.setTextColor(Color.rgb(102, 102, 102));//x轴色值
        yAxis.setTextSize(12f);
        yAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                if (value >= 10000) {
                    return String.format(Locale.CHINA, "%.1f万/平", value / 10000);
                } else {
                    return String.format(Locale.CHINA, "%.0f元/平", value);
                }
            }
        });
    }

    /**
     * 自定义标注样式
     */
    protected void initLegend() {
        Legend mLegend = getLegend();
        mLegend.setForm(Legend.LegendForm.LINE);// 样式
        mLegend.setFormSize(10f);// 字体
        mLegend.setTextColor(Color.rgb(102, 102, 102));// 颜色
        mLegend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
    }

    /**
     * 设置数据
     */
    public void setEstatePriceBo(String estateName, String gscopeName, List<EstateDealPriceBo> estList, List<GscopeDealPriceBo> gscopePriceBos, List<GscopeDealPriceBo> cityPriceBo) {
        estateDealPriceBos = estList;
        gscopeDealPriceBos = gscopePriceBos;
        cityDealPriceBos = cityPriceBo;

        // x轴显示的数据，这里默认使用数字下标显示
        //当前月份往前13个月
        List<String> xValues = new ArrayList<>();

        //y轴数据
        ArrayList<Entry> yValues1 = new ArrayList<>();
        ArrayList<Entry> yValues2 = new ArrayList<>();
        ArrayList<Entry> yValues3 = new ArrayList<>();

        // 0 1 2 3 4 5
        for (int i=0; i<xShow; i++){

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.MONTH, i - xShow);  //-6 -5
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;


            estateArray.put(i, false);

            if (estateDealPriceBos!=null){
                for (int j=0; j<estateDealPriceBos.size(); j++){

                    if (year == estateDealPriceBos.get(j).getDataYear() &&
                            month == estateDealPriceBos.get(j).getDataMonth()) {
//                        Logger.i("add year:"+year+"--month:"+month);
                        yValues1.add(new Entry((float) estateDealPriceBos.get(j).getDealAvgPrice(), i));
                        estateArray.put(i, true);
                    }
                }
            }

            gscopArray.put(i, false);

            if (gscopeDealPriceBos!=null){
                for (int j=0; j<gscopeDealPriceBos.size(); j++){

                    if (year == gscopeDealPriceBos.get(j).getDataYear() &&
                            month == gscopeDealPriceBos.get(j).getDataMonth()) {
                        yValues2.add(new Entry((float) gscopeDealPriceBos.get(j).getDealAvgPrice(), i));
                        gscopArray.put(i, true);
                    }
                }
            }

            cityArray.put(i,false);

            if (cityDealPriceBos != null) {
                for (int j=0; j<cityDealPriceBos.size(); j++){
                    if (year == cityDealPriceBos.get(j).getDataYear() &&
                            month == cityDealPriceBos.get(j).getDataMonth()) {
                        yValues3.add(new Entry((float) cityDealPriceBos.get(j).getDealAvgPrice(), i));
                        cityArray.put(i, true);
                    }
                }
            }

            xValues.add(String.valueOf(i));
        }

        //添加到集合中
        List<ILineDataSet> lineDataSets = new ArrayList<>();
        if (yValues1.size() > 0) {
            LineDataSet lineDataSet = new LineDataSet(yValues1, estateName);
            lineDataSet.setLineWidth(3f); // 线宽
            lineDataSet.setDrawValues(false);//表格不显示数据
            lineDataSet.setDrawCircles(true);//点为圆形
            lineDataSet.setCircleRadius(6f);   //设置小圆的大小
            lineDataSet.setColor(estateColor);// 显示颜色
            lineDataSet.setHighLightColor(estateColor); // 高亮的线的颜色
            lineDataSet.setHighlightLineWidth(1f);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);//水平高亮不显示
            lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            lineDataSets.add(lineDataSet);
        }
        if (yValues2.size() > 0) {
            LineDataSet lineDataSet = new LineDataSet(yValues2, gscopeName);
            lineDataSet.setLineWidth(3f); // 线宽
            lineDataSet.setDrawValues(false);//表格不显示数据
            lineDataSet.setDrawCircles(true);//点为圆形
            lineDataSet.setCircleRadius(6f);   //设置小圆的大小
            lineDataSet.setColor(gscopColor);// 显示颜色
            lineDataSet.setHighlightEnabled(yValues1.size() < yValues2.size());
            lineDataSet.setHighLightColor(gscopColor); // 高亮的线的颜色
            lineDataSet.setHighlightLineWidth(1f);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);//水平高亮不显示
            lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            lineDataSets.add(lineDataSet);
        }
        if (yValues3.size() > 0) {
            LineDataSet lineDataSet = new LineDataSet(yValues3, "上海");
            lineDataSet.setLineWidth(3f); // 线宽
            lineDataSet.setDrawValues(false);//表格不显示数据
            lineDataSet.setDrawCircles(true);//点为圆形
            lineDataSet.setCircleRadius(6f);   //设置小圆的大小
            lineDataSet.setColor(cityColor);// 显示颜色
            lineDataSet.setHighlightEnabled(yValues1.size() < yValues3.size());
            lineDataSet.setHighLightColor(cityColor); // 高亮的线的颜色
            lineDataSet.setHighlightLineWidth(1f);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);//水平高亮不显示
            lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            lineDataSets.add(lineDataSet);
        }

        setData(new LineData(xValues, lineDataSets));
        moveViewToX(xShow-1);
    }

    public class SaleLineMarkView extends MarkerView {

        private AppCompatTextView atv_content;
        private int chartWidth;//图表宽度
        private int chartHeight;//图表宽度

        public SaleLineMarkView(Context context, int layoutResource) {
            super(context, layoutResource);
            atv_content = (AppCompatTextView) findViewById(R.id.atv_content);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            //点击的是第几列
            int xIndex = e.getXIndex();

            StringBuilder text = new StringBuilder();

            List<ILineDataSet> iLineDataSets = getLineData().getDataSets();
            if (iLineDataSets != null &&
                    iLineDataSets.size() > 0) {

                //行数 表示有几行线
                int size = iLineDataSets.size();

                for (int i = 0; i < size; i++) {

                    ILineDataSet iLineDataSet = iLineDataSets.get(i);

                    Entry currentEntry = iLineDataSet.getEntryForXIndex(xIndex);

                    Entry preEntry = xIndex > 0 ? iLineDataSet.getEntryForXIndex(xIndex - 1) : null;

                    if (currentEntry!=null && currentEntry.getXIndex()==xIndex){

                        if (text.length() > 0) {
                            text.append("\n");
                        }

                        text.append(String.format(Locale.CHINA, "%s : %.0f元/平",
                                iLineDataSet.getLabel(), currentEntry.getVal()));

                        if (preEntry!=null && preEntry.getXIndex()==(xIndex-1)){
                            float change = currentEntry.getVal() - preEntry.getVal();
                            text.append(String.format(Locale.CHINA, " 环比%.2f%%%s",
                                    change > 0 ? change * 100 / preEntry.getVal() : -change * 100 / preEntry.getVal(),
                                    change > 0 ? "↑" : change == 0 ? "-" : "↓"));
                        }
                    }

//                    if (i == 0 &&
//                            estateArray.get(xIndex)) {
//                        if (currentEntry != null) {
//                            text.append(String.format(Locale.CHINA, "%s : %.0f元/平",
//                                    iLineDataSet.getLabel(), currentEntry.getVal()));
//                            if (preEntry != null &&
//                                    estateArray.get(xIndex - 1)) {
//                                float change = currentEntry.getVal() - preEntry.getVal();
//                                text.append(String.format(Locale.CHINA, " 环比%.2f%%%s",
//                                        change > 0 ? change * 100 / preEntry.getVal() : -change * 100 / preEntry.getVal(),
//                                        change > 0 ? "↑" : change == 0 ? "-" : "↓"));
//                            }
//                        }
//                    } else if (i == 1 &&
//                            gscopArray.get(xIndex)) {
//                        if (text.length() > 0) {
//                            text.append("\n");
//                        }
//                        if (currentEntry != null &&
//                                gscopArray.get(xIndex)) {
//                            text.append(String.format(Locale.CHINA, "%s : %.0f元/平",
//                                    iLineDataSet.getLabel(), currentEntry.getVal()));
//                            if (preEntry != null &&
//                                    estateArray.get(xIndex - 1)) {
//                                float change = currentEntry.getVal() - preEntry.getVal();
//                                text.append(String.format(Locale.CHINA, " 环比%.2f%%%s",
//                                        change > 0 ? change * 100 / preEntry.getVal() : -change * 100 / preEntry.getVal(),
//                                        change > 0 ? "↑" : change == 0 ? "" : "↓"));
//                            }
//                        }
//                    }else if (size==1){
//                        if (estateArray.get(xIndex)){
//                            if (currentEntry!=null){
//                                text.append(String.format(Locale.CHINA, "%s : %.0f元/平",
//                                        iLineDataSet.getLabel(), currentEntry.getVal()));
//                            }
//                        }else if (gscopArray.get(xIndex)){
//                            if (currentEntry!=null){
//                                text.append(String.format(Locale.CHINA, "%s : %.0f元/平",
//                                        iLineDataSet.getLabel(), currentEntry.getVal()));
//                            }
//                        }
//                    }
                }
            }
            atv_content.setText(text);
        }

        @Override
        public int getXOffset(float xpos) {
            if (chartWidth == 0) {
                chartWidth = SaleLineChart2.this.getWidth();
            }
            if (xpos < getWidth() / 2) {//超出左侧重新计算
                return (int) (-xpos);
            } else if (xpos + getWidth() / 2 > chartWidth) {//超出右侧
                return (int) (chartWidth - xpos - getWidth());
            } else {
                return -(getWidth() / 2);
            }
        }

        @Override
        public int getYOffset(float ypos) {
            if (chartHeight == 0) {
                chartHeight = SaleLineChart2.this.getHeight();
            }
            if (ypos > chartHeight / 2) {//显示在下半部分,进行调整
                return -chartHeight / 2;
            } else {
                return -getHeight();
            }
        }
    }
}
