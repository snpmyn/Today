package com.zsp.today.module.login;

import com.zsp.today.base.BaseDataBaseTable;

/**
 * Created on 2019-12-10.
 *
 * @author 郑少鹏
 * @desc 用户数据库表
 */
public class UserDataBaseTable extends BaseDataBaseTable {
    /**
     * constructor
     *
     * @param phoneNumber 手机号
     * @param date        日期
     */
    public UserDataBaseTable(String phoneNumber, String date) {
        super(phoneNumber, date);
    }
}