package widget.view.kit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import util.theme.ThemeUtils;
import widget.view.NutritionChartView;

/**
 * Created on 2025/10/3.
 *
 * @author 郑少鹏
 * @desc 营养图表视图配套原件
 */
public class NutritionChartViewKit {
    /**
     * 执行
     *
     * @param appCompatActivity  活动
     * @param nutritionChartView 营养图表视图
     * @param nutritionPartList  NutritionPart 集
     * @param unit               单位
     * @param centerSubTitle     中心副标题
     */
    public static void execute(AppCompatActivity appCompatActivity, @NonNull NutritionChartView nutritionChartView, List<NutritionChartView.NutritionPart> nutritionPartList, String unit, String centerSubTitle) {
        // 设置弧段间距
        nutritionChartView.setGapAngle(2.0F);
        // 设置弧线宽度
        nutritionChartView.setStrokeWidth(80.0F);
        // 设置标签文本尺寸
        nutritionChartView.setLabelTextSize(30.0F);
        // 设置标签文本颜色
        nutritionChartView.setLabelTextColor(ThemeUtils.getColorOnSurfaceColorFromAttrResIdWithTypedArray(appCompatActivity));
        // 设置标签粗体否
        nutritionChartView.setLabelBold(true);
        // 设置标签内边距
        nutritionChartView.setLabelPadding(12.0F);
        // 设置标签距弧线间距
        nutritionChartView.setLabelArcMargin(10.0F);
        // 设置标签模式
        nutritionChartView.setLabelMode(NutritionChartView.LabelMode.PERCENT);
        // 设置标签背景色
        nutritionChartView.setLabelBackgroundColor(ThemeUtils.getColorSecondaryContainerColorFromAttrResIdWithTypedArray(appCompatActivity));
        // 设置显示中心文本否
        nutritionChartView.setShowCenterText(true);
        // 设置中心值粗体否
        nutritionChartView.setCenterValueBold(true);
        // 设置中心值尺寸
        nutritionChartView.setCenterValueSize(40.0F);
        // 设置中心值颜色
        nutritionChartView.setCenterValueColor(ThemeUtils.getColorPrimaryColorFromAttrResIdWithTypedArray(appCompatActivity));
        // 设置单位
        nutritionChartView.setUnit(unit);
        // 设置中心单位粗体否
        nutritionChartView.setCenterUnitBold(true);
        // 设置中心单位尺寸
        nutritionChartView.setCenterUnitSize(40.0F);
        // 设置中心单位颜色
        nutritionChartView.setCenterUnitColor(ThemeUtils.getColorPrimaryColorFromAttrResIdWithTypedArray(appCompatActivity));
        // 设置中心单位位置
        // 0 右侧 1 下方
        nutritionChartView.setCenterUnitPosition(0);
        // 设置中心单位外边距
        nutritionChartView.setCenterUnitMargin(10.0F);
        // 设置中心副标题
        nutritionChartView.setCenterSubTitle(centerSubTitle);
        // 设置中心副标题粗体否
        nutritionChartView.setCenterSubTitleBold(true);
        // 设置中心副标题尺寸
        nutritionChartView.setCenterSubTitleSize(40.0F);
        // 设置中心副标题外边距
        nutritionChartView.setCenterSubTitleMargin(10.0F);
        // 设置中心副标题颜色
        nutritionChartView.setCenterSubTitleColor(ThemeUtils.getColorPrimaryColorFromAttrResIdWithTypedArray(appCompatActivity));
        // 设置数据
        // 设置后自动播放动画
        nutritionChartView.setData(nutritionPartList);
    }
}