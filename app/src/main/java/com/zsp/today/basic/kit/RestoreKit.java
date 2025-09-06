package com.zsp.today.basic.kit;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created on 2024/8/14.
 *
 * @author 郑少鹏
 * @desc 恢复配套元件
 */
public class RestoreKit {
    /**
     * 延时
     */
    public long delay = 1000L;

    public static RestoreKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 恢复
     *
     * @param appCompatActivity 活动
     */
    public void restore(AppCompatActivity appCompatActivity) {
        RestoreAccountDataBaseTableKit.getInstance().restoreAccountDataBaseTable(appCompatActivity);
    }

    private static final class InstanceHolder {
        static final RestoreKit INSTANCE = new RestoreKit();
    }
}