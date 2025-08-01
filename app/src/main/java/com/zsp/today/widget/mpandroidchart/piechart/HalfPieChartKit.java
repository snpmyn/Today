package com.zsp.today.widget.mpandroidchart.piechart;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

import util.typeface.TypefaceUtils;

/**
 * Created on 2021/4/15
 *
 * @author zsp
 * @desc 半圆饼图表配套元件
 */
public class HalfPieChartKit {
    private PieChart pieChart;

    /**
     * PieChart 配置
     *
     * @param context    上下文
     * @param pieChart   圆饼图表
     * @param centerText 中央文本
     */
    private void pieChartConfigure(Context context, @NotNull PieChart pieChart, String centerText) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        this.pieChart = pieChart;
        // Sets the background color for this view.
        /*pieChart.setBackgroundColor(Color.WHITE);*/
        // Set this to true to enable the rotation / spinning of the chart by touch.
        // Set it to false to disable it.
        // Default: true
        pieChart.setRotationEnabled(false);
        // If this is enabled, values inside the PieChart are drawn in percent and not with their original value.
        // Values provided for the IValueFormatter to format are then provided in percent.
        pieChart.setUsePercentValues(true);
        // Set this to false to prevent values from being highlighted by tap gesture.
        // Values can still be highlighted via drag or programmatically.
        // Default: true
        /*pieChart.setHighlightPerTapEnabled(true);*/
        // Set this to true if this component should be enabled (should be drawn), false if not. If disabled, nothing of this component will be drawn.
        // Default: true
        pieChart.getDescription().setEnabled(false);
        // hole
        /*pieChart.setDrawHoleEnabled(true);*/
        /*pieChart.setHoleRadius(50.0F);*/
        pieChart.setHoleColor(Color.WHITE);
        // TransparentCircle
        /*pieChart.setTransparentCircleRadius(55.0F);*/
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        // CenterText
        /*pieChart.setDrawCenterText(true);*/
        pieChart.setCenterText(centerText);
        /*pieChart.setCenterTextOffset(0, -20);*/
        pieChart.setCenterTextTypeface(TypefaceUtils.sansLight(weakReference.get()));
        // angle
        /*pieChart.setMaxAngle(180.0F);*/
        pieChart.setRotationAngle(180.0F);
        // label
        pieChart.setEntryLabelTextSize(12.0F);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTypeface(TypefaceUtils.sansRegular(weakReference.get()));
        pieChart.animateY(1500, Easing.EaseInOutQuad);
        pieChart.invalidate();
    }

    /**
     * Legend 配置
     */
    private void legendConfigure() {
        Legend legend = pieChart.getLegend();
        legend.setWordWrapEnabled(true);
        /*legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);*/
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        /*legend.setXEntrySpace(6.0F);*/
        legend.setXOffset(0.0F);
        /*legend.setYEntrySpace(0.0F);*/
        /*legend.setYOffset(0.0F);*/
        /*legend.setDrawInside(false);*/
        legend.setEnabled(false);
    }

    /**
     * 设数据
     *
     * @param context    上下文
     * @param pieChart   圆饼图表
     * @param pieEntries 数据集合
     * @param label      标签
     */
    private void setData(Context context, PieChart pieChart, List<PieEntry> pieEntries, String label) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
        pieDataSet.setSliceSpace(3.0F);
        pieDataSet.setSelectionShift(5.0F);
        // Sets the distance the highlighted PieChart-slice of this DataSet is "shifted" away from the center of the chart, default 12F.
        /*pieDataSet.setSelectionShift(12.0F);*/
        // ColorTemplate(颜色模板)
        int[] colors = {rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db"), rgb("#A5DC86"), rgb("#F8BB86")};
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10.0F);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueTypeface(TypefaceUtils.sansLight(weakReference.get()));
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        // 指引线
        /*pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);*/
        /*pieDataSet.setValueLineColor(ContextCompat.getColor(context, R.color.fontHint));*/
        /*pieDataSet.setValueTextColor(ContextCompat.getColor(context, R.color.fontInput));*/
        pieChart.setData(pieData);
    }

    /**
     * 无数据
     *
     * @param context              上下文
     * @param pieChart             PieChart
     * @param noDataText           无数据文本
     * @param noDataTextColorResId 无数据文本颜色资源 ID
     */
    private void noData(Context context, @NotNull PieChart pieChart, String noDataText, int noDataTextColorResId) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        // Clears the chart from all data (sets it to null) and refreshes it (by calling invalidate()).
        pieChart.clear();
        pieChart.notifyDataSetChanged();
        // Sets the text that informs the user that there is no data available with which to draw the chart.
        pieChart.setNoDataText(noDataText);
        // Sets the color of the no data text.
        pieChart.setNoDataTextColor(ContextCompat.getColor(weakReference.get(), noDataTextColorResId));
        // refresh
        pieChart.invalidate();
    }

    /**
     * 执行
     *
     * @param context              上下文
     * @param pieChart             圆饼图表
     * @param centerText           中央文本
     * @param pieEntries           数据集合
     * @param label                标签
     * @param noData               无数据否
     * @param noDataText           无数据文本
     * @param noDataTextColorResId 无数据文本颜色资源 ID
     */
    public void execute(Context context, PieChart pieChart, String centerText, List<PieEntry> pieEntries, String label, boolean noData, String noDataText, int noDataTextColorResId) {
        if (noData) {
            noData(context, pieChart, noDataText, noDataTextColorResId);
            return;
        }
        pieChartConfigure(context, pieChart, centerText);
        legendConfigure();
        setData(context, pieChart, pieEntries, label);
    }
}
