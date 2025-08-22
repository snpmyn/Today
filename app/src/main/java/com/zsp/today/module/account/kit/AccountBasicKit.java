package com.zsp.today.module.account.kit;

import androidx.annotation.NonNull;

import com.zsp.today.application.App;
import com.zsp.today.module.account.bean.AccountDetailBean;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.value.AccountCondition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.data.BigDecimalUtils;
import util.list.ListUtils;

/**
 * Created on 2021/2/9
 *
 * @author zsp
 * @desc 账目基础配套元件
 */
public class AccountBasicKit {
    public static AccountBasicKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 年按序去重
     *
     * @param asc 升序否
     * @return 按序去重后年数据集合
     */
    public List<String> yearRemoveDuplicationWithSort(boolean asc) {
        // 按序查询仅包含年数据
        List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhereAndOrderAndSelect(AccountDataBaseTable.class, new String[]{AccountCondition.ACCOUNT_PHONE_NUMBER, App.getAppInstance().getPhoneNumber()}, "year", asc, "year");
        // 按序去重
        List<String> yearList = new ArrayList<>();
        for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
            yearList.add(accountDataBaseTable.getYear());
        }
        return ListUtils.removeDuplicationWithSort(yearList);
    }

    /**
     * 月按序去重
     *
     * @param appointYear 指定年
     * @param asc         升序否
     * @return 按序去重后月数据集合
     */
    public List<String> monthRemoveDuplicationWithSort(String appointYear, boolean asc) {
        // 按序查询仅包含月数据
        List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhereAndOrderAndSelect(AccountDataBaseTable.class, new String[]{AccountCondition.ACCOUNT_PHONE_NUMBER_AND_YEAR, App.getAppInstance().getPhoneNumber(), appointYear}, "month", asc, "month");
        // 按序去重
        List<String> monthList = new ArrayList<>();
        for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
            monthList.add(accountDataBaseTable.getMonth());
        }
        return ListUtils.removeDuplicationWithSort(monthList);
    }

    /**
     * 日期按序去重
     *
     * @param appointYear  指定年
     * @param appointMonth 指定月
     * @param asc          升序否
     * @return 按序去重后日期数据集合
     */
    public List<String> dateRemoveDuplicationWithSort(String appointYear, String appointMonth, boolean asc) {
        // 按序查询仅包含日期数据
        List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhereAndOrderAndSelect(AccountDataBaseTable.class, new String[]{AccountCondition.ACCOUNT_PHONE_NUMBER_AND_YEAR_AND_MONTH, App.getAppInstance().getPhoneNumber(), appointYear, appointMonth}, "date", asc, "date");
        // 按序去重
        List<String> dateStringList = new ArrayList<>();
        for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
            dateStringList.add(accountDataBaseTable.getDate());
        }
        return ListUtils.removeDuplicationWithSort(dateStringList);
    }

    /**
     * 通过年获取账目数据库表数据集合
     *
     * @param appointYear 指定年
     * @return 账目数据库表数据集合
     */
    public List<AccountDataBaseTable> getAccountDataBaseTableListByYear(String appointYear) {
        return LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_YEAR, App.getAppInstance().getPhoneNumber(), appointYear);
    }

    /**
     * 通过年月获取账目数据库表数据集合
     *
     * @param appointYear  指定年
     * @param appointMonth 指定月
     * @return 账目数据库表数据集合
     */
    public List<AccountDataBaseTable> getAccountDataBaseTableListByYearMonth(String appointYear, String appointMonth) {
        return LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_YEAR_AND_MONTH, App.getAppInstance().getPhoneNumber(), appointYear, appointMonth);
    }

    /**
     * 通过日期获取账目数据库表数据集合
     *
     * @param appointDate 指定日期
     * @return 账目数据库表数据集合
     */
    public List<AccountDataBaseTable> getAccountDataBaseTableListByDate(String appointDate) {
        return LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE, App.getAppInstance().getPhoneNumber(), appointDate);
    }

    /**
     * 账目数据库表总金额
     *
     * @param accountDataBaseTableList 账目数据库表数据集合
     * @return 账目数据库表总金额
     */
    public String totalAmountBaseOnAccountDataBaseTable(@NonNull List<AccountDataBaseTable> accountDataBaseTableList) {
        BigDecimal totalAmount = null;
        for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
            BigDecimal amount = BigDecimal.valueOf(accountDataBaseTable.getAmount());
            totalAmount = ((null == totalAmount) ? amount : BigDecimalUtils.add(totalAmount, amount));
        }
        return BigDecimalUtils.bigDecimalToString(totalAmount);
    }

    /**
     * 转化账目数据库表数据集合为账目详情数据集合
     *
     * @param accountDataBaseTableList 账目数据库表数据集合
     * @return 账目详情数据集合
     */
    public List<AccountDetailBean> transformAccountDataBaseTableToAccountDetailBean(@NonNull List<AccountDataBaseTable> accountDataBaseTableList) {
        List<AccountDetailBean> accountDetailBeanList = new ArrayList<>(accountDataBaseTableList.size());
        for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTableList) {
            accountDetailBeanList.add(new AccountDetailBean(accountDataBaseTable.getDate(), accountDataBaseTable.getCategory(), accountDataBaseTable.getAmount()));
        }
        return accountDetailBeanList;
    }

    private static final class InstanceHolder {
        static final AccountBasicKit INSTANCE = new AccountBasicKit();
    }
}