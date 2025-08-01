package com.zsp.today.module.account.bean;

/**
 * Created on 2021/11/4
 *
 * @author zsp
 * @desc 账目日列表
 */
public class AccountDateListBean {
    /**
     * 日期
     */
    private final String date;
    /**
     * 总金额
     */
    private final String totalAmount;

    /**
     * constructor
     *
     * @param date        日期
     * @param totalAmount 总金额
     */
    public AccountDateListBean(String date, String totalAmount) {
        this.date = date;
        this.totalAmount = totalAmount;
    }

    public String getDate() {
        return date;
    }

    public String getTotalAmount() {
        return totalAmount;
    }
}
