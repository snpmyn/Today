package com.zsp.today.widget.mpandroidchart.linechart.kit;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.zsp.today.widget.mpandroidchart.linechart.valueformatter.FloatValueFormatter;
import com.zsp.today.widget.mpandroidchart.linechart.valueformatter.StringValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019/7/10.
 *
 * @author 郑少鹏
 * @desc 线图表配套元件
 */
public class LineChartKit implements OnChartValueSelectedListener {
    private LineChart lineChart;

    /**
     * 执行
     *
     * @param context              上下文
     * @param lineChart            线图表
     * @param label                标签
     * @param mode                 模式
     * @param yFloats              Y 值
     * @param noData               无数据否
     * @param noDataText           无数据文本
     * @param noDataTextColorResId 无数据文本颜色资源 ID
     * @param description          描述
     * @param values               值
     */
    public void execute(Context context, LineChart lineChart, String label, LineDataSet.Mode mode, List<Float> yFloats, boolean noData, String noDataText, int noDataTextColorResId, String description, List<String> values) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        if (noData) {
            noData(context, lineChart, noDataText, noDataTextColorResId);
            return;
        }
        lineChartConfigure(context, lineChart, description);
        legendConfigure(context);
        xAxisConfigure(context, values);
        yAxisConfigure(context);
        setData(weakReference.get(), label, mode, yFloats);
    }

    /**
     * LineChart 配置
     *
     * @param context     上下文
     * @param lineChart   线图表
     * @param description 描述
     */
    private void lineChartConfigure(Context context, @NonNull LineChart lineChart, String description) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        this.lineChart = lineChart;
        // Sets the background color for this view.
        /*lineChart.setBackgroundColor(ContextCompat.getColor(context, com.zsp.core.R.color.white));*/
        // Set this to true if this component should be enabled (should be drawn), false if not. If disabled, nothing of this component will be drawn.
        // Default: true
        /*lineChart.getDescription().setEnabled(true);*/
        // Sets the text to be shown as the description.
        // Never set this to null as this will cause NullPointer exception when drawing with Android Canvas.
        lineChart.getDescription().setText(description);
        // Sets the size of the label text in density pixels min = 6F, max = 24F, default 10F.
        /*lineChart.getDescription().setTextSize(10.0F);*/
        // Sets the text color to use for the labels.
        // Make sure to use getResources().getColor(...) when using a color from the resources.
        lineChart.getDescription().setTextColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.basic));
        // Set this to false to disable all gestures and touches on the chart.
        // Default: true
        /*lineChart.setTouchEnabled(true);*/
        // If set to true, both x and y axis can be scaled simultaneously with 2 fingers, if false, x and y axis can be scaled separately.
        // Default: false
        lineChart.setPinchZoom(true);
        // Set this to true to enable dragging (moving the chart with the finger) for the chart (this does not effect scaling).
        /*lineChart.setDragEnabled(true);*/
        // Sets this to false to prohibit scaling (zooming in and out by gesture) for the chart (this does not effect dragging) on both X- Axis.
        lineChart.setScaleXEnabled(false);
        // Set this to true to draw the grid background, false if not.
        /*lineChart.setDrawGridBackground(false);*/
        // Set this to true to allow highlighting per dragging over the chart surface when it is fully zoomed out.
        // Default: true
        /*lineChart.setHighlightPerDragEnabled(true);*/
        // Sets the maximum distance in screen dp a touch can be away from an entry to cause it to get highlighted.
        // Default: 500dp
        lineChart.setMaxHighlightDistance(300.0F);
        // Deceleration friction coefficient in [0 ; 1] interval, higher values indicate that speed will decrease slowly, for example if it set to 0, it will stop immediately.
        // 1 is an invalid value, and will be converted to 0.999F automatically.
        /*lineChart.setDragDecelerationFrictionCoef(0.9F);*/
        // Sets extra offsets (around the chart view) to be appended to the auto-calculated offsets.
        lineChart.setExtraOffsets(0.0F, 6.0F, 0.0F, 0.0F);
        // Sets the size of the area (range on the x-axis) that should be maximum visible at once (no further zooming out allowed).
        // If this is e.g. set to 10, no more than a range of 10 on the x-axis can be viewed at once without scrolling.
        lineChart.setVisibleXRangeMaximum(6.0F);
        // Animates the drawing / rendering of the chart on both x- and y-axis with the specified animation time. If animate(...) is called, no further calling of invalidate() is necessary to refresh the chart.
        // ANIMATIONS ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
        lineChart.animateXY(1500, 1500);
        // Set a selection listener for the chart.
        lineChart.setOnChartValueSelectedListener(this);
    }

    /**
     * Legend 配置
     *
     * @param context 上下文
     */
    private void legendConfigure(Context context) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        // Returns the Legend object of the chart.
        // This method can be used to get an instance of the legend in order to customize the automatically generated Legend.
        Legend legend = lineChart.getLegend();
        // Sets the form/shape of the legend forms.
        /*legend.setForm(Legend.LegendForm.SQUARE);*/
        // Sets the size of the label text in density pixels min = 6F, max = 24F, default 10F.
        legend.setTextSize(12.0F);
        // Sets the text color to use for the labels.
        // Make sure to use getResources().getColor(...) when using a color from the resources.
        legend.setTextColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.color_FFF86C09));
        // Sets the orientation of the legend.
        /*legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);*/
        // Sets the vertical alignment of the legend.
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        // Sets the horizontal alignment of the legend.
        /*legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);*/
    }

    /**
     * X 轴配置
     *
     * @param context 上下文
     * @param values  值
     */
    private void xAxisConfigure(Context context, List<String> values) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        // Returns the object representing all x-labels, this method can be used to acquire the XAxis object and modify it (e.g. change the position of the labels, styling, etc.).
        XAxis xAxis = lineChart.getXAxis();
        // Set this to true if this component should be enabled (should be drawn), false if not.
        // If disabled, nothing of this component will be drawn.
        // Default: true
        /*xAxis.setEnabled(true);*/
        // Set this to true if the line alongside the axis should be drawn or not.
        xAxis.setDrawAxisLine(false);
        // Set this to true to enable drawing the grid lines for this axis.
        xAxis.setDrawGridLines(false);
        // Enabled/disable granularity control on axis value intervals.
        // If enabled, the axis interval is not allowed to go below a certain granularity.
        // Default: false
        xAxis.setGranularityEnabled(true);
        // Set a minimum interval for the axis when zooming in.
        // The axis is not allowed to go below that limit.
        // This can be used to avoid label duplicating when zooming in.
        /*xAxis.setGranularity(1.0F);*/
        // Sets the position of the x-labels.
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // Sets the text color to use for the labels.
        // Make sure to use getResources().getColor(...) when using a color from the resources.
        xAxis.setTextColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.basic));
        // Sets the color of the border surrounding the chart.
        xAxis.setAxisLineColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.basic));
        if (null != values) {
            xAxis.setValueFormatter(new StringValueFormatter(values));
        }
    }

    /**
     * Y 轴配置
     *
     * @param context 上下文
     */
    private void yAxisConfigure(Context context) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        // Returns the left y-axis object. In the horizontal bar-chart, this is the top axis.
        YAxis yLeftAxis = lineChart.getAxisLeft();
        // Set this to true if this component should be enabled (should be drawn), false if not.
        // If disabled, nothing of this component will be drawn.
        // Default: true
        /*yLeftAxis.setEnabled(true);*/
        // Sets the position of the y-labels.
        yLeftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        // Sets the number of label entries for the y-axis max = 25, min = 2, default: 6, be aware that this number is not fixed.
        yLeftAxis.setLabelCount(6, false);
        // Set this to true to draw the zero-line regardless of weather other grid-lines are enabled or not.
        // Default: false
        /*yLeftAxis.setDrawZeroLine(false);*/
        // Set this to true to enable drawing the grid lines for this axis.
        yLeftAxis.setDrawGridLines(false);
        // Enabled/disable granularity control on axis value intervals.
        // If enabled, the axis interval is not allowed to go below a certain granularity.
        // Default: false
        yLeftAxis.setGranularityEnabled(true);
        // Set a minimum interval for the axis when zooming in.
        // The axis is not allowed to go below that limit.
        // This can be used to avoid label duplicating when zooming in.
        /*yLeftAxis.setGranularity(1.0F);*/
        // Sets the text color to use for the labels.
        // Make sure to use getResources().getColor(...) when using a color from the resources.
        yLeftAxis.setTextColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.basic));
        // Sets the color of the border surrounding the chart.
        yLeftAxis.setAxisLineColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.basic));
        // Returns the right y-axis object. In the horizontal bar-chart, this is the bottom axis.
        YAxis yRightAxis = lineChart.getAxisRight();
        // Set this to true if this component should be enabled (should be drawn), false if not.
        // If disabled, nothing of this component will be drawn.
        // Default: true
        /*yRightAxis.setEnabled(true);*/
        // Sets the position of the y-labels.
        yRightAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        // Sets the number of label entries for the y-axis max = 25, min = 2, default: 6, be aware that this number is not fixed.
        yRightAxis.setLabelCount(6, false);
        // Set this to true to draw the zero-line regardless of weather other grid-lines are enabled or not.
        // Default: false
        /*yRightAxis.setDrawZeroLine(false);*/
        // Set this to true to enable drawing the grid lines for this axis.
        yRightAxis.setDrawGridLines(false);
        // Enabled/disable granularity control on axis value intervals.
        // If enabled, the axis interval is not allowed to go below a certain granularity.
        // Default: false
        yRightAxis.setGranularityEnabled(true);
        // Set a minimum interval for the axis when zooming in.
        // The axis is not allowed to go below that limit.
        // This can be used to avoid label duplicating when zooming in.
        /*yRightAxis.setGranularity(1.0F);*/
        // Sets the text color to use for the labels.
        // Make sure to use getResources().getColor(...) when using a color from the resources.
        yRightAxis.setTextColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.basic));
        // Sets the color of the border surrounding the chart.
        yRightAxis.setAxisLineColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.basic));
    }

    /**
     * 设数据
     *
     * @param context 上下文
     * @param label   标签
     * @param mode    模式
     * @param yFloats Y 值
     */
    private void setData(Context context, String label, LineDataSet.Mode mode, @NonNull List<Float> yFloats) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < yFloats.size(); i++) {
            entries.add(new Entry((float) i, yFloats.get(i)));
        }
        LineDataSet lineDataSet;
        LineData lineData = lineChart.getData();
        if ((null != lineData) && (lineData.getDataSetCount() > 0)) {
            lineDataSet = (LineDataSet) lineData.getDataSetByIndex(0);
            lineDataSet.setLabel(label);
            lineDataSet.setValues(entries);
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // Create a lineDataSet.
            lineDataSet = new LineDataSet(entries, label);
            // Returns the drawing mode for this LineDataSet.
            lineDataSet.setMode(mode);
            // Sets axis dependency.
            /*lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);*/
            // Set the line width of the chart (min = 0.2F, max = 10F); default 1f NOTE: thinner line == better performance, thicker line == worse performance.
            lineDataSet.setLineWidth(1.0F);
            // Sets the intensity for cubic lines (if enabled). Max = 1f = very cubic, Min = 0.05f = low cubic effect, Default: 0.2F.
            /*lineDataSet.setCubicIntensity(0.2F);*/
            // If true, the data will also be drawn filled.
            lineDataSet.setDrawFilled(true);
            // Set this to true to enable the drawing of circle indicators for this DataSet, default true.
            /*lineDataSet.setDrawCircles(true);*/
            // Sets the radius of the drawn circles.
            // Default radius = 4F, Min = 1F
            lineDataSet.setCircleRadius(2.0F);
            // Sets the one and ONLY color that should be used for this DataSet.
            // Internally, this recreates the colors array and adds the specified color.
            lineDataSet.setCircleColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.color_FFF86C09));
            // Set this to true to allow drawing a hole in each data circle.
            /*lineDataSet.setDrawCircleHole(true);*/
            // Sets the one and ONLY color that should be used for this DataSet.
            // Internally, this recreates the colors array and adds the specified color.
            lineDataSet.setColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.color_FFF86C09));
            // Sets the color that is used for drawing the highlight indicators.
            // Do not forget to resolve the color using getResources().getColor(...) or Color.rgb(...).
            lineDataSet.setHighLightColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.basic));
            // Sets the alpha value (transparency) that is used for filling the line surface (0-255), default: 85.
            lineDataSet.setFillAlpha(100);
            // Sets the color that is used for filling the area below the line.
            // Resets an eventually set "fillDrawable".
            lineDataSet.setFillColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.color_FFF86C09));
            // Enables / disables the horizontal highlight-indicator. If disabled, the indicator is not drawn.
            /*lineDataSet.setDrawHorizontalHighlightIndicator(true);*/
            // Sets the color that is used for drawing the highlight indicators.
            // Do not forget to resolve the color using getResources().getColor(...) or Color.rgb(...).
            lineDataSet.setHighLightColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.color_FFF86C09));
            // Sets value formatter.
            lineDataSet.setValueFormatter(new FloatValueFormatter());
            // Sets a custom IFillFormatter to the chart that handles the position of the filled-line for each DataSet.
            // Set this to null to use the default logic.
            lineDataSet.setFillFormatter((dataSet, dataProvider) -> lineChart.getAxisLeft().getAxisMinimum());
            // Create a lineData object with the lineDataSet.
            LineData lineDataNew = new LineData(lineDataSet);
            // Enables / disables drawing values (value-text) for all DataSets this data object contains.
            lineDataNew.setDrawValues(true);
            // Sets the color of the value-text (color in which the value-labels are drawn) for all DataSets this data object contains.
            lineDataNew.setValueTextColor(ContextCompat.getColor(weakReference.get(), com.zsp.core.R.color.color_FFF86C09));
            // Sets the Typeface for all value-labels for all DataSets this data object contains.
            lineDataNew.setValueTypeface(Typeface.DEFAULT_BOLD);
            // Sets the size (in dp) of the value-text for all DataSets this data object contains.
            lineDataNew.setValueTextSize(10.0F);
            // Sets a new data object for the chart. The data object contains all values and information needed for displaying.
            lineChart.setData(lineDataNew);
        }
    }

    /**
     * 无数据
     *
     * @param context              上下文
     * @param lineChart            线图表
     * @param noDataText           无数据文本
     * @param noDataTextColorResId 无数据文本颜色资源 ID
     */
    private void noData(Context context, @NonNull LineChart lineChart, String noDataText, int noDataTextColorResId) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        // Clears the chart from all data (sets it to null) and refreshes it (by calling invalidate()).
        lineChart.clear();
        lineChart.notifyDataSetChanged();
        // Sets the text that informs the user that there is no data available with which to draw the chart.
        lineChart.setNoDataText(noDataText);
        // Sets the color of the no data text.
        lineChart.setNoDataTextColor(ContextCompat.getColor(weakReference.get(), noDataTextColorResId));
        // refresh
        lineChart.invalidate();
    }

    /**
     * Called when a value has been selected inside the chart.
     *
     * @param e the selected Entry
     * @param h the corresponding highlight object that contains information
     */
    @Override
    public void onValueSelected(@NotNull Entry e, @NotNull Highlight h) {
        // This will move the center of the current viewport to the specified x and y value animated.
        lineChart.centerViewToAnimated(e.getX(), e.getY(), lineChart.getData().getDataSetByIndex(h.getDataSetIndex()).getAxisDependency(), 500);
    }

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    @Override
    public void onNothingSelected() {

    }
}