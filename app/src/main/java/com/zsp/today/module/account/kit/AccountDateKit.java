package com.zsp.today.module.account.kit;

import com.zsp.today.module.account.bean.AccountDateListBean;
import com.zsp.today.module.account.database.AccountDataBaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2021/11/4
 *
 * @author zsp
 * @desc 账目日配套元件
 */
public class AccountDateKit {
    public static AccountDateKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 通过按序去重后年数据集合获取账目日列表数据集合
     *
     * @param asc 升序否
     * @return 账目日列表数据集合
     */
    public List<AccountDateListBean> getAccountDateListBeanListByYearRemoveDuplicationWithSort(boolean asc) {
        List<AccountDateListBean> accountDateListBeanResultList = new ArrayList<>();
        // 根据按序去重后年数据集合查询处理每年下账目数据
        for (String year : AccountBasicKit.getInstance().yearRemoveDuplicationWithSort(asc)) {
            List<AccountDataBaseTable> accountDataBaseTableList = AccountBasicKit.getInstance().getAccountDataBaseTableListByYear(year);
            String totalAmount = AccountBasicKit.getInstance().totalAmountBaseOnAccountDataBaseTable(accountDataBaseTableList);
            accountDateListBeanResultList.add(new AccountDateListBean(year, totalAmount));
        }
        return accountDateListBeanResultList;
    }

    /**
     * 通过按序去重后月数据集合获取账目日列表数据集合
     *
     * @param appointYear 指定年
     * @param asc         升序否
     * @return 账目日列表数据集合
     */
    public List<AccountDateListBean> getAccountDateListBeanListByMonthRemoveDuplicationWithSort(String appointYear, boolean asc) {
        List<AccountDateListBean> accountDateListBeanResultList = new ArrayList<>();
        // 根据按序去重后月数据集合查询处理每月下账目数据
        for (String month : AccountBasicKit.getInstance().monthRemoveDuplicationWithSort(appointYear, asc)) {
            List<AccountDataBaseTable> accountDataBaseTableList = AccountBasicKit.getInstance().getAccountDataBaseTableListByYearMonth(appointYear, month);
            String totalAmount = AccountBasicKit.getInstance().totalAmountBaseOnAccountDataBaseTable(accountDataBaseTableList);
            accountDateListBeanResultList.add(new AccountDateListBean(month, totalAmount));
        }
        return accountDateListBeanResultList;
    }

    /**
     * 通过按序去重后日期数据集合获取账目日列表数据集合
     *
     * @param appointYear  指定年
     * @param appointMonth 指定月
     * @param asc          升序否
     * @return 账目日列表数据集合
     */
    public List<AccountDateListBean> getAccountDateListBeanListByDateRemoveDuplicationWithSort(String appointYear, String appointMonth, boolean asc) {
        List<AccountDateListBean> accountDateListBeanResultList = new ArrayList<>();
        // 根据按序去重后日期数据集合查询处理每日期下账目数据
        for (String date : AccountBasicKit.getInstance().dateRemoveDuplicationWithSort(appointYear, appointMonth, asc)) {
            List<AccountDataBaseTable> accountDataBaseTableList = AccountBasicKit.getInstance().getAccountDataBaseTableListByDate(date);
            String totalAmount = AccountBasicKit.getInstance().totalAmountBaseOnAccountDataBaseTable(accountDataBaseTableList);
            accountDateListBeanResultList.add(new AccountDateListBean(date, totalAmount));
        }
        return accountDateListBeanResultList;
    }

    private static final class InstanceHolder {
        static final AccountDateKit INSTANCE = new AccountDateKit();
    }
}