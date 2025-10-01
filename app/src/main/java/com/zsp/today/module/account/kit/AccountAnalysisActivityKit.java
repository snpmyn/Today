package com.zsp.today.module.account.kit;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineDataSet;
import com.zsp.today.R;
import com.zsp.today.module.account.bean.AccountDateListBean;
import com.zsp.today.widget.mpandroidchart.linechart.kit.LineChartKit;

import java.util.ArrayList;
import java.util.List;

import util.list.ListUtils;
import util.theme.ThemeUtils;
import util.view.ViewUtils;
import widget.dialog.materialalertdialog.kit.SingleChooseMaterialAlertDialogKit;
import widget.toast.ToastKit;

/**
 * Created on 2021/6/13 0013
 *
 * @author zsp
 * @desc 账目分析页配套元件
 */
public class AccountAnalysisActivityKit {
    /**
     * 选择日期并显示月度分析
     *
     * @param appCompatActivity      活动
     * @param textView               TextView
     * @param lineChartMonthAnalysis 月度分析线图表
     */
    public void chooseDateAndShowMonthAnalysis(@NonNull AppCompatActivity appCompatActivity, @NonNull TextView textView, LineChart lineChartMonthAnalysis) {
        textView.setText(appCompatActivity.getString(R.string.monthAnalysis));
        List<String> yearRemoveDuplicationWithSortList = AccountBasicKit.getInstance().yearRemoveDuplicationWithSort(false);
        if (ListUtils.listIsNotEmpty(yearRemoveDuplicationWithSortList)) {
            SingleChooseMaterialAlertDialogKit.getInstance().show(appCompatActivity, yearRemoveDuplicationWithSortList.toArray(new CharSequence[0]), value -> execute(appCompatActivity, lineChartMonthAnalysis, LineDataSet.Mode.CUBIC_BEZIER, appCompatActivity.getString(R.string.noMonthAnalysisDataAvailable), AccountDateKit.getInstance().getAccountDateListBeanListByMonthRemoveDuplicationWithSort(value, true)));
        } else {
            ToastKit.showShort(appCompatActivity.getString(R.string.noYearDataAvailable));
        }
    }

    /**
     * 月度分析
     *
     * @param appCompatActivity      活动
     * @param textView               TextView
     * @param lineChartMonthAnalysis 月度分析线图表
     * @param lineChartYearAnalysis  年度分析线图表
     */
    public void monthAnalysis(@NonNull AppCompatActivity appCompatActivity, @NonNull TextView textView, LineChart lineChartMonthAnalysis, LineChart lineChartYearAnalysis) {
        textView.setText(appCompatActivity.getString(R.string.monthAnalysis));
        ViewUtils.hideView(lineChartYearAnalysis, View.GONE);
        ViewUtils.showView(lineChartMonthAnalysis);
        List<String> yearRemoveDuplicationWithSortList = AccountBasicKit.getInstance().yearRemoveDuplicationWithSort(false);
        String appointYear = ListUtils.listIsNotEmpty(yearRemoveDuplicationWithSortList) ? yearRemoveDuplicationWithSortList.get(0) : "";
        execute(appCompatActivity, lineChartMonthAnalysis, LineDataSet.Mode.CUBIC_BEZIER, appCompatActivity.getString(R.string.noMonthAnalysisDataAvailable), AccountDateKit.getInstance().getAccountDateListBeanListByMonthRemoveDuplicationWithSort(appointYear, true));
    }

    /**
     * 年度分析
     *
     * @param appCompatActivity      活动
     * @param textView               TextView
     * @param lineChartYearAnalysis  年度分析线图表
     * @param lineChartMonthAnalysis 月度分析线图表
     */
    public void yearAnalysis(@NonNull AppCompatActivity appCompatActivity, @NonNull TextView textView, LineChart lineChartYearAnalysis, LineChart lineChartMonthAnalysis) {
        textView.setText(appCompatActivity.getString(R.string.yearAnalysis));
        ViewUtils.hideView(lineChartMonthAnalysis, View.GONE);
        ViewUtils.showView(lineChartYearAnalysis);
        execute(appCompatActivity, lineChartYearAnalysis, LineDataSet.Mode.HORIZONTAL_BEZIER, appCompatActivity.getString(R.string.noYearAnalysisDataAvailable), AccountDateKit.getInstance().getAccountDateListBeanListByYearRemoveDuplicationWithSort(true));
    }

    /**
     * 执行
     *
     * @param appCompatActivity       活动
     * @param lineChart               线图表
     * @param mode                    模式
     * @param noDataText              无数据文本
     * @param accountDateListBeanList 账目日列表数据集合
     */
    private void execute(AppCompatActivity appCompatActivity, LineChart lineChart, LineDataSet.Mode mode, String noDataText, @NonNull List<AccountDateListBean> accountDateListBeanList) {
        // 数据
        List<String> values = new ArrayList<>(accountDateListBeanList.size());
        List<Float> floats = new ArrayList<>(accountDateListBeanList.size());
        for (AccountDateListBean accountDateListBean : accountDateListBeanList) {
            values.add(accountDateListBean.getDate());
            floats.add(Float.parseFloat(accountDateListBean.getTotalAmount()));
        }
        // 显示
        LineChartKit lineChartKit = new LineChartKit();
        lineChartKit.execute(appCompatActivity, lineChart, appCompatActivity.getString(R.string.yuan), mode, floats, ListUtils.listIsEmpty(accountDateListBeanList), noDataText, ThemeUtils.getColorPrimaryColorFromAttrResIdWithTypedArray(appCompatActivity), "", values);
    }
}