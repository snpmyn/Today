package com.zsp.today.widget.mpandroidchart.linechart.valueformatter;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created on 2019/7/11.
 *
 * @author 郑少鹏
 * @desc FloatValueFormatter
 */
public class FloatValueFormatter extends ValueFormatter {
    /**
     * Called when drawing any label, used to change numbers into formatted strings.
     *
     * @param value float to be formatted
     * @return formatted string label
     */
    @Override
    public String getFormattedValue(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(value);
    }
}