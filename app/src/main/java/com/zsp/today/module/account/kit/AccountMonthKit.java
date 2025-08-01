package com.zsp.today.module.account.kit;

import com.zsp.today.module.account.bean.AccountMonthListBean;
import com.zsp.today.module.account.database.AccountDataBaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2021/11/4
 *
 * @author zsp
 * @desc 账目月配套元件
 */
public class AccountMonthKit {
    public static AccountMonthKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 通过按序去重后月数据集合获取账目月列表数据集合
     *
     * @param appointYear 指定年
     * @param asc         升序否
     * @return 账目月列表数据集合
     */
    public List<AccountMonthListBean> getAccountMonthListBeanListByMonthRemoveDuplicationWithSort(String appointYear, boolean asc) {
        List<AccountMonthListBean> accountMonthListBeanResultList = new ArrayList<>();
        // 根据按序去重后月数据集合查询处理每月下账目数据
        for (String month : AccountBasicKit.getInstance().monthRemoveDuplicationWithSort(appointYear, asc)) {
            List<AccountDataBaseTable> accountDataBaseTableList = AccountBasicKit.getInstance().getAccountDataBaseTableListByYearMonth(appointYear, month);
            String totalAmount = AccountBasicKit.getInstance().totalAmountBaseOnAccountDataBaseTable(accountDataBaseTableList);
            accountMonthListBeanResultList.add(new AccountMonthListBean(appointYear, month, totalAmount));
        }
        return accountMonthListBeanResultList;
    }

    private static final class InstanceHolder {
        static final AccountMonthKit INSTANCE = new AccountMonthKit();
    }
}
