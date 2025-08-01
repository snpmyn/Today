package com.zsp.today.widget.mpandroidchart.linechart.valueformatter;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

/**
 * Created on 2019/7/10.
 *
 * @author 郑少鹏
 * @desc StringValueFormatter
 */
public class StringValueFormatter extends ValueFormatter {
    private final List<String> values;

    public StringValueFormatter(List<String> values) {
        this.values = values;
    }

    /**
     * Called when drawing any label, used to change numbers into formatted strings.
     *
     * @param value float to be formatted
     * @return formatted string label
     */
    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        if ((index < 0) || (index >= values.size())) {
            return "";
        }
        return values.get(index);
    }
}
