package com.zsp.today.basic.restore.kit;

import androidx.appcompat.app.AppCompatActivity;

import widget.dialog.bocdialog.lottie.BocLottieCommonDialog;

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
    public long delay = 500L;
    /**
     * BOC Lottie 普通对话框
     */
    public BocLottieCommonDialog bocLottieCommonDialog;

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