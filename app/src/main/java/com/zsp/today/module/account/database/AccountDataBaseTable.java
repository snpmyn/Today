package com.zsp.today.module.account.database;

import com.zsp.today.base.BaseDataBaseTable;

import litepal.annotation.Column;

/**
 * Created on 2020/12/17
 *
 * @author zsp
 * @desc 账目数据库表
 */
public class AccountDataBaseTable extends BaseDataBaseTable {
    /**
     * 类目
     */
    @Column(nullable = false)
    private String category;
    /**
     * 金额
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * constructor
     * <p>
     * Update needs a default constructor.
     */
    public AccountDataBaseTable() {
        super();
    }

    /**
     * constructor
     *
     * @param phoneNumber 手机号
     * @param date        日期
     * @param category    类目
     * @param amount      金额
     */
    public AccountDataBaseTable(String phoneNumber, String date, String category, Double amount) {
        super(phoneNumber, date);
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}