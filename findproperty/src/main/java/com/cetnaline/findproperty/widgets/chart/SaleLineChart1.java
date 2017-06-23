package com.cetnaline.findproperty.widgets.chart;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.DealListBo;
import com.cetnaline.findproperty.api.bean.PostInternalBo;
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
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * 二手房房价走势
 * Created by guilin on 16/4/21.
 */
public class SaleLineChart1 extends LineChart {

    private final int estateColor = Color.rgb(255, 54, 21);//小区色值
    private final int gscopColor = Color.rgb(56, 222, 103);//板块色值

    private PostInternalBo estatePostInternalBo, gscopPostInternalBo;
    private SparseBooleanArray estateArray = new SparseBooleanArray();
    private SparseBooleanArray gscopArray = new SparseBooleanArray();

    private OnChartValueSelectedListener onChartValueSelectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        }

        @Override
        public void onNothingSelected() {
        }
    };

    public SaleLineChart1(Context context) {
        this(context, null);
    }

    public SaleLineChart1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SaleLineChart1(Context context, AttributeSet attrs, int defStyle) {
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
        setDragEnabled(true);//可拖拽
        setScaleEnabled(false);//禁止手动缩放
        setPinchZoom(true);
        zoom(2f, 1f, 0, 0);//x轴放大2倍
        getAxisLeft().setEnabled(false);//去掉左侧y轴

        setOnChartValueSelectedListener(onChartValueSelectedListener);
        setMarkerView(new SaleLineMarkView(getContext(), R.layout.layout_sale_line_chart_marker_view));

        /*x坐标*/
        XAxis xAxis = getXAxis();
        xAxis.setTextColor(Color.rgb(102, 102, 102));//x轴色值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴放置底部
        xAxis.setLabelsToSkip(0);//不忽略x轴点
        xAxis.setAxisMinValue(-0.5f);
        xAxis.setAxisMaxValue(14);
        xAxis.setValueFormatter(new XAxisValueFormatter() {
            @Override
            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int currentYear = calendar.get(Calendar.YEAR);
                calendar.add(Calendar.MONTH, index - 13);
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
    public void setPostInternalBos(String estateName, List<PostInternalBo> postInternalBos) {
        int postInternalBoSize = postInternalBos.size();
        if (postInternalBoSize == 1) {
            if ("gscope".equalsIgnoreCase(postInternalBos.get(0).getKey())) {
                gscopPostInternalBo = postInternalBos.get(0);
            } else {
                estatePostInternalBo = postInternalBos.get(0);

            }
        } else if (postInternalBoSize == 2) {
            for (PostInternalBo postInternalBo : postInternalBos) {
                if ("gscope".equalsIgnoreCase(postInternalBo.getType())) {
                    gscopPostInternalBo = postInternalBo;
                } else {
                    estatePostInternalBo = postInternalBo;
                }
            }
        } else {//不符合条件
            Logger.d("房价走势数据不符合调价[小区走势index = 0,板块走势index =1,只有2组数据]");
            return;
        }

        //小区数据
        ArrayList<DealListBo> estateDealListBos = estatePostInternalBo == null ?
                null : estatePostInternalBo.getDealListBos();
        //板块数据
        ArrayList<DealListBo> gscopDealListBos = gscopPostInternalBo == null ?
                null : gscopPostInternalBo.getDealListBos();


        // x轴显示的数据，这里默认使用数字下标显示
        //当前月份往前13个月
        List<String> xValues = new ArrayList<>();

        //y轴数据
        ArrayList<Entry> yValues1 = new ArrayList<>();
        ArrayList<Entry> yValues2 = new ArrayList<>();

        int j = 0, k = 0;
        int estateDealSize = estateDealListBos == null ? 0 : estateDealListBos.size();
        int gscopDealSize = gscopDealListBos == null ? 0 : gscopDealListBos.size();
        for (int i = 0; i < 14; i++) {
            xValues.add(String.valueOf(i));
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.MONTH, i - 13);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            if (estateDealSize > 2 &&
                    j < estateDealSize) {
                if (year == estateDealListBos.get(j).getDealYear() &&
                        month == estateDealListBos.get(j).getDealMonth()) {
                    yValues1.add(new Entry((float) estateDealListBos.get(j).getDealAvgPrice(), i));
                    j++;
                    estateArray.put(i, true);
                } else {
                    estateArray.put(i, false);
                }
            } else {
                estateArray.put(i, false);
            }
            if (gscopDealSize > 2 &&
                    k < gscopDealSize) {
                if (year == gscopDealListBos.get(k).getDealYear() &&
                        month == gscopDealListBos.get(k).getDealMonth()) {
                    yValues2.add(new Entry((float) gscopDealListBos.get(k).getDealAvgPrice(), i));
                    k++;
                    gscopArray.put(i, true);
                } else {
                    gscopArray.put(i, false);
                }
            } else {
                gscopArray.put(i, false);
            }
        }

        //添加到集合中
        List<ILineDataSet> lineDataSets = new ArrayList<>();
        if (yValues1.size() > 0) {
            LineDataSet lineDataSet = new LineDataSet(yValues1,
                    TextUtils.isEmpty(estatePostInternalBo.getName()) ? estateName : estatePostInternalBo.getName());
            lineDataSet.setLineWidth(3f); // 线宽
            lineDataSet.setDrawValues(false);//表格不显示数据
            lineDataSet.setDrawCircles(false);//点为圆形
            lineDataSet.setColor(estateColor);// 显示颜色
            lineDataSet.setHighLightColor(estateColor); // 高亮的线的颜色
            lineDataSet.setHighlightLineWidth(1f);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);//水平高亮不显示
            lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            lineDataSets.add(lineDataSet);
        }
        if (yValues2.size() > 0) {
            LineDataSet lineDataSet = new LineDataSet(yValues2, gscopPostInternalBo.getName());
            lineDataSet.setLineWidth(3f); // 线宽
            lineDataSet.setDrawValues(false);//表格不显示数据
            lineDataSet.setDrawCircles(false);//点为圆形
            lineDataSet.setColor(gscopColor);// 显示颜色
            lineDataSet.setHighlightEnabled(yValues1.size() < yValues2.size());
            lineDataSet.setHighLightColor(estateColor); // 高亮的线的颜色
            lineDataSet.setHighlightLineWidth(1f);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);//水平高亮不显示
            lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            lineDataSets.add(lineDataSet);
        }

        setData(new LineData(xValues, lineDataSets));
        moveViewToX(14);
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
            int xIndex = e.getXIndex();
            StringBuilder text = new StringBuilder();
            List<ILineDataSet> iLineDataSets = getLineData().getDataSets();
            if (iLineDataSets != null &&
                    iLineDataSets.size() > 0) {
                int size = iLineDataSets.size();
                for (int i = 0; i < size; i++) {
                    ILineDataSet iLineDataSet = iLineDataSets.get(i);
                    Entry currentEntry = iLineDataSet.getEntryForXIndex(xIndex);
                    Entry preEntry = xIndex > 0 ? iLineDataSet.getEntryForXIndex(xIndex - 1) : null;
                    if (i == 0 &&
                            estateArray.get(xIndex)) {
                        if (currentEntry != null) {
                            text.append(String.format(Locale.CHINA, "%s : %.0f元/平",
                                    iLineDataSet.getLabel(), currentEntry.getVal()));
                            if (preEntry != null &&
                                    estateArray.get(xIndex - 1)) {
                                float change = currentEntry.getVal() - preEntry.getVal();
                                text.append(String.format(Locale.CHINA, " 环比%.2f%%%s",
                                        change > 0 ? change * 100 / preEntry.getVal() : -change * 100 / preEntry.getVal(),
                                        change > 0 ? "↑" : change == 0 ? "-" : "↓"));
                            }
                        }
                    } else if (i == 1 &&
                            gscopArray.get(xIndex)) {
                        if (text.length() > 0) {
                            text.append("\n");
                        }
                        if (currentEntry != null &&
                                gscopArray.get(xIndex)) {
                            text.append(String.format(Locale.CHINA, "%s : %.0f元/平",
                                    iLineDataSet.getLabel(), currentEntry.getVal()));
                            if (preEntry != null &&
                                    estateArray.get(xIndex - 1)) {
                                float change = currentEntry.getVal() - preEntry.getVal();
                                text.append(String.format(Locale.CHINA, " 环比%.2f%%%s",
                                        change > 0 ? change * 100 / preEntry.getVal() : -change * 100 / preEntry.getVal(),
                                        change > 0 ? "↑" : change == 0 ? "" : "↓"));
                            }
                        }
                    }
                }
            }
            atv_content.setText(text);
        }

        @Override
        public int getXOffset(float xpos) {
            if (chartWidth == 0) {
                chartWidth = SaleLineChart1.this.getWidth();
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
                chartHeight = SaleLineChart1.this.getHeight();
            }
            if (ypos > chartHeight / 2) {//显示在下半部分,进行调整
                return -chartHeight / 2;
            } else {
                return -getHeight();
            }
        }
    }
}
