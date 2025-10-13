package com.zsp.today.module.account.bean;

import java.util.Locale;

/**
 * Created on 2021/11/4
 *
 * @author zsp
 * @desc 账目月列表
 */
public class AccountMonthListBean {
    /**
     * 年
     */
    private final String year;
    /**
     * 月
     */
    private final String month;
    /**
     * 总金额
     */
    private final String totalAmount;
    /**
     * 数量
     */
    private final int count;
    /**
     * 月度环比
     */
    private final Double monthOnMonth;
    /**
     * 最大类目和金额
     */
    private final String maxCategoryAndAmount;

    /**
     * constructor
     *
     * @param year                 年
     * @param month                月
     * @param totalAmount          总金额
     * @param count                数量
     * @param monthOnMonth         月度环比
     * @param maxCategoryAndAmount 最大类目和金额
     */
    public AccountMonthListBean(String year, String month, String totalAmount, int count, Double monthOnMonth, String maxCategoryAndAmount) {
        this.year = year;
        this.month = month;
        this.totalAmount = totalAmount;
        this.count = count;
        this.monthOnMonth = monthOnMonth;
        this.maxCategoryAndAmount = maxCategoryAndAmount;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public int getCount() {
        return count;
    }

    public String getMaxCategoryAndAmount() {
        return maxCategoryAndAmount;
    }

    /**
     * 月度环比大于 0
     *
     * @return 月度环比大于 0 否
     */
    public boolean monthOnMonthGreaterThanZero() {
        return ((null != monthOnMonth) && (monthOnMonth > 0));
    }

    /**
     * 月度环比小于 0
     *
     * @return 月度环比小于 0 否
     */
    public boolean monthOnMonthLessThanZero() {
        return ((null != monthOnMonth) && (monthOnMonth < 0));
    }

    /**
     * 获取月度环比描述
     *
     * @return 月度环比描述
     */
    public String getMonthOnMonthDescribe() {
        if (null == monthOnMonth) {
            // 头月或无法计算
            return "--";
        }
        if (monthOnMonthGreaterThanZero()) {
            return String.format(Locale.CHINA, "%.1f%%", monthOnMonth);
        } else if (monthOnMonthLessThanZero()) {
            return String.format(Locale.CHINA, "%.1f%%", Math.abs(monthOnMonth));
        } else {
            return "持平";
        }
    }
}