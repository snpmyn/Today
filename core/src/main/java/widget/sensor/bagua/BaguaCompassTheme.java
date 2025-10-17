package widget.sensor.bagua;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

import util.system.SystemUtils;

/**
 * @decs: 八卦罗盘视图主题
 * @author: 郑少鹏
 * @date: 2025/10/16 12:18
 * @version: v 1.0
 */
public class BaguaCompassTheme {
    /**
     * 通过自定义时间段切换主题
     *
     * @param baguaCompassView 八卦罗盘视图
     */
    public static void switchThemeByCustomTimePeriod(BaguaCompassView baguaCompassView) {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if ((hour >= 6) && (hour < 18)) {
            // 日 6:00 - 18:00
            applyDayTheme(baguaCompassView);
        } else {
            // 夜 18:00 - 6:00
            applyNightTheme(baguaCompassView);
        }
    }

    /**
     * 通过系统切换主题
     *
     * @param context          上下文
     * @param baguaCompassView 八卦罗盘视图
     */
    public static void switchThemeBySystem(Context context, BaguaCompassView baguaCompassView) {
        if (SystemUtils.areDark(context)) {
            BaguaCompassTheme.applyNightTheme(baguaCompassView);
        } else {
            BaguaCompassTheme.applyDayTheme(baguaCompassView);
        }
    }

    /**
     * 应用日主题
     *
     * @param baguaCompassView 八卦罗盘视图
     */
    private static void applyDayTheme(@NonNull BaguaCompassView baguaCompassView) {
        // 设置背景色
        // 古旧黄纸色
        /*baguaCompassView.setBackgroundColor(Color.parseColor("#FAECD8"));*/
        // 设置圆圈色
        // 深棕色 - 模拟古铜器锈蚀色
        baguaCompassView.setCircleColor(Color.parseColor("#7C5030"));
        baguaCompassView.setCircleStrokes(6.0F, 5.0F, 4.0F);
        // 设置圆圈半径
        // 外圈 90%
        // 中圈 70%
        // 内圈 50%
        /*baguaCompassView.setCircleRadius(0.9.0F, 0.7.0F, 0.5f);*/
        // 设置网格线
        // 赭石色 - 古画中常用矿物颜料色
        baguaCompassView.setGridLine(2.5F, Color.parseColor("#B25A30"));
        // 第一层
        // 设置方位
        // 朱砂红 - 道教符咒专用色
        baguaCompassView.setDirectionText(36.0F, Color.parseColor("#D63031"), true, 0.88F);
        // 第二层
        // 设置卦符
        // 古金色 - 青铜器纹饰色
        baguaCompassView.setSymbolText(56.0F, Color.parseColor("#D4A017"), true, 0.72F);
        // 第三层
        // 设置卦名
        // 墨色 - 传统水墨色
        baguaCompassView.setBaguaText(48.0F, Color.parseColor("#2C1B13"), true, 0.56F);
        // 第四层
        // 设置卦义
        // 赭石色
        baguaCompassView.setMeaningText(38.0F, Color.parseColor("#CC7A3B"), true, 0.36F);
        // 设置太极图色
        // 外圈 - 深棕
        // 中圈 - 金黄
        // 内圈 - 朱砂红
        baguaCompassView.setTaJiColor(Color.parseColor("#7C5030"), Color.parseColor("#E0B63F"), Color.parseColor("#D63031"));
        baguaCompassView.setTaiJiRadius(0.15F, 0.10F, 0.05F);
        // 设置指南针色
        // 南针 - 米黄
        // 北针 - 朱砂红
        // 中心外圈 - 墨色
        // 中心内圈 - 金色
        baguaCompassView.setNeedleColor(Color.parseColor("#D63031"), Color.parseColor("#F6E6B3"), Color.parseColor("#1E0F09"), Color.parseColor("#E0B63F"));
        baguaCompassView.setNeedleSize(0.42F, 12.0F, 8.0F);
    }

    /**
     * 应用夜主题
     *
     * @param baguaCompassView 八卦罗盘视图
     */
    private static void applyNightTheme(@NonNull BaguaCompassView baguaCompassView) {
        // 设置背景色
        // 深邃的玄青色 - 夜空色
        /*baguaCompassView.setBackgroundColor(Color.parseColor("#0F1419"));*/
        // 设置圆圈色
        // 古铜绿锈色 - 玄铁氧化色
        baguaCompassView.setCircleColor(Color.parseColor("#4A7C7E"));
        baguaCompassView.setCircleStrokes(6.0F, 5.0F, 4.0F);
        // 设置圆圈半径
        // 外圈 90%
        // 中圈 70%
        // 内圈 50%
        /*baguaCompassView.setCircleRadius(0.9f, 0.7f, 0.5f);*/
        // 设置网格线
        // 月光青色
        baguaCompassView.setGridLine(2.5F, Color.parseColor("#5D8AA8"));
        // 第一层
        // 设置方位
        // 赤金色 - 月光下金属光泽
        baguaCompassView.setDirectionText(36.0F, Color.parseColor("#FFD700"), true, 0.88F);
        // 第二层
        // 设置卦符
        // 翠玉绿 - 夜明珠般荧光
        baguaCompassView.setSymbolText(56.0F, Color.parseColor("#20B2AA"), true, 0.72F);
        // 第三层
        // 设置卦名
        // 玄霜银 - 月华色
        baguaCompassView.setBaguaText(48.0F, Color.parseColor("#C0C0C0"), true, 0.56F);
        // 第四层
        // 设置卦义
        // 古铜青 - 青铜锈色
        baguaCompassView.setMeaningText(38.0F, Color.parseColor("#8FBC8F"), true, 0.36F);
        // 设置太极图色
        // 外圈 - 墨绿
        // 中圈 - 钢青
        // 内圈 - 金色
        baguaCompassView.setTaJiColor(Color.parseColor("#2F4F4F"), Color.parseColor("#4682B4"), Color.parseColor("#FFD700"));
        baguaCompassView.setTaiJiRadius(0.15F, 0.10F, 0.05F);
        // 设置指南针色
        // 南针 - 青绿
        // 北针 - 烈焰红
        // 中心外圈 - 纯黑
        // 中心内圈 - 金色
        baguaCompassView.setNeedleColor(Color.parseColor("#C41E3A"), Color.parseColor("#F5DEB3"), Color.parseColor("#3E2723"), Color.parseColor("#DAA520"));
        baguaCompassView.setNeedleSize(0.42F, 12.0F, 8.0F);
    }
}