package com.zsp.today.basic.restore.kit;

import androidx.appcompat.app.AppCompatActivity;

import com.zsp.today.BuildConfig;
import com.zsp.today.basic.value.Folder;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.module.dangerous.database.DangerousDataBaseTable;

import java.io.File;

import widget.dialog.bocdialog.lottie.BocLottieCommonDialog;

/**
 * Created on 2024/8/14.
 *
 * @author 郑少鹏
 * @desc 恢复配套元件
 */
public class RestoreKit {
    /**
     * BOC Lottie 普通对话框
     */
    public BocLottieCommonDialog bocLottieCommonDialog;
    /**
     * 账目数据库表文件绝对路径
     */
    public String accountDataBaseTableFileAbsolutePath = Folder.EXTERNAL_BACKUP + File.separator + AccountDataBaseTable.class.getSimpleName() + BuildConfig.BACKUP_SUFFIX + ".json";
    /**
     * 险情数据库表文件绝对路径
     */
    public String dangerousDataBaseTableFileAbsolutePath = Folder.EXTERNAL_BACKUP + File.separator + DangerousDataBaseTable.class.getSimpleName() + BuildConfig.BACKUP_SUFFIX + ".json";

    public static RestoreKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 恢复
     *
     * @param appCompatActivity  活动
     * @param restoreKitListener 恢复配套元件监听
     */
    public void restore(AppCompatActivity appCompatActivity, RestoreKitListener restoreKitListener) {
        RestoreAccountDataBaseTableKit.getInstance().restoreAccountDataBaseTable(appCompatActivity, restoreKitListener);
    }

    /**
     * 恢复配套元件监听
     */
    public interface RestoreKitListener {
        /**
         * 结束
         */
        void end();
    }

    private static final class InstanceHolder {
        static final RestoreKit INSTANCE = new RestoreKit();
    }
}