package com.zsp.today.module.dangerous.database;

import com.zsp.today.base.BaseDataBaseTable;

import litepal.annotation.Column;

/**
 * Created on 2025/8/28.
 *
 * @author 郑少鹏
 * @desc 险情数据库表
 */
public class DangerousDataBaseTable extends BaseDataBaseTable {
    /**
     * 险情通知
     */
    @Column(nullable = false)
    private String dangerousNotice;
    /**
     * 紧急联系人手机号
     */
    @Column(nullable = false)
    private String emergencyContactPhoneNumber;

    /**
     * constructor
     * <p>
     * Update needs a default constructor.
     */
    public DangerousDataBaseTable() {
        super();
    }

    /**
     * constructor
     *
     * @param phoneNumber                 手机号
     * @param date                        日期
     * @param dangerousNotice             险情通知
     * @param emergencyContactPhoneNumber 紧急联系人手机号
     */
    public DangerousDataBaseTable(String phoneNumber, String date, String dangerousNotice, String emergencyContactPhoneNumber) {
        super(phoneNumber, date);
        this.dangerousNotice = dangerousNotice;
        this.emergencyContactPhoneNumber = emergencyContactPhoneNumber;
    }

    public String getDangerousNotice() {
        return dangerousNotice;
    }

    public String getEmergencyContactPhoneNumber() {
        return emergencyContactPhoneNumber;
    }
}