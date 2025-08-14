package com.zsp.today.module.function.database;

import com.zsp.today.base.BaseDataBaseTable;

import litepal.annotation.Column;

/**
 * Created on 2022/1/29
 *
 * @author zsp
 * @desc 功能数据库表
 */
public class FunctionDataBaseTable extends BaseDataBaseTable {
    /**
     * 功能 ID
     */
    @Column(nullable = false)
    private int functionId;
    /**
     * 功能名称
     */
    @Column(nullable = false)
    private String functionName;
    /**
     * 功能显示
     */
    @Column(nullable = false)
    private Boolean functionShow;

    /**
     * constructor
     * <p>
     * Update needs a default constructor.
     */
    public FunctionDataBaseTable() {
        super();
    }

    /**
     * constructor
     *
     * @param phoneNumber  手机号
     * @param date         日期
     * @param functionId   功能 ID
     * @param functionName 功能名称
     * @param functionShow 功能显示
     */
    public FunctionDataBaseTable(String phoneNumber, String date, int functionId, String functionName, Boolean functionShow) {
        super(phoneNumber, date);
        this.functionId = functionId;
        this.functionName = functionName;
        this.functionShow = functionShow;
    }

    public int getFunctionId() {
        return functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Boolean getFunctionShow() {
        return functionShow;
    }

    public void setFunctionShow(Boolean functionShow) {
        this.functionShow = functionShow;
    }
}