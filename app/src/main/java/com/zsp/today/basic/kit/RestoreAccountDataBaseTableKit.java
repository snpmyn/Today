package com.zsp.today.basic.kit;

import android.animation.ValueAnimator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zsp.today.R;
import com.zsp.today.basic.value.Folder;
import com.zsp.today.module.account.database.AccountDataBaseTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.file.FileUtils;
import util.json.JsonTransform;
import util.list.ListUtils;
import util.timer.TimerKit;
import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.dialog.bocdialog.lottie.BocLottieCommonDialog;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;

/**
 * Created on 2025/9/5.
 *
 * @author 郑少鹏
 * @desc 恢复账目数据库表配套原件
 */
public class RestoreAccountDataBaseTableKit {
    public static RestoreAccountDataBaseTableKit getInstance() {
        return RestoreAccountDataBaseTableKit.InstanceHolder.INSTANCE;
    }

    /**
     * 恢复账目数据库表
     *
     * @param appCompatActivity 活动
     */
    public void restoreAccountDataBaseTable(AppCompatActivity appCompatActivity) {
        if (LitePalKit.getInstance().count(AccountDataBaseTable.class) != 0) {
            // 账目数据库表有数据
            // 预存或恢复功能数据库表
            RestoreFunctionDataBaseTableKit.getInstance().preStoreOrRestoreFunctionDataBaseTable(appCompatActivity, null);
            return;
        }
        BocLottieCommonDialog bocLottieCommonDialog = BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogTwo(BocLottieDialogEnum.LOADING_ONE, appCompatActivity.getString(R.string.restoreAccount), ValueAnimator.INFINITE, null, null);
        // 账目数据库表文件地址
        String accountDataBaseTableFilePath = Folder.EXTERNAL_BACKUP + File.separator + AccountDataBaseTable.class.getSimpleName() + ".json";
        if (FileUtils.areFileExistByPath(accountDataBaseTableFilePath)) {
            // 账目数据库表文件存在
            // 获取并转化账目数据库表文件数据
            List<AccountDataBaseTable> accountDataBaseTables = new Gson().fromJson(JsonTransform.transformJsonFromFile(accountDataBaseTableFilePath), new TypeToken<List<AccountDataBaseTable>>() {
            }.getType());
            if (ListUtils.listIsEmpty(accountDataBaseTables)) {
                // 无账目数据库表文件数据
                // 下一步
                next(appCompatActivity, bocLottieCommonDialog, BocLottieDialogEnum.EMPTY_ONE, appCompatActivity.getString(R.string.noAccountAvailable));
            } else {
                // 有账目数据库表文件数据
                // 转化存储恢复
                List<AccountDataBaseTable> accountDataBaseTableList = new ArrayList<>(accountDataBaseTables.size());
                for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTables) {
                    accountDataBaseTableList.add(new AccountDataBaseTable(accountDataBaseTable.getPhoneNumber(), accountDataBaseTable.getDate(), accountDataBaseTable.getCategory(), accountDataBaseTable.getAmount()));
                }
                if (LitePalKit.getInstance().multiSave(accountDataBaseTableList)) {
                    // 下一步
                    next(appCompatActivity, bocLottieCommonDialog, BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.accountHasBeenRestored));
                }
            }
        } else {
            // 账目数据库表文件不存在
            // 下一步
            next(appCompatActivity, bocLottieCommonDialog, BocLottieDialogEnum.EMPTY_ONE, appCompatActivity.getString(R.string.noAccountAvailable));
        }
    }

    /**
     * 下一步
     *
     * @param appCompatActivity     活动
     * @param bocLottieCommonDialog BOC Lottie 普通对话框
     * @param bocLottieDialogEnum   BOC Lottie 对话框枚举
     * @param hint                  提示
     */
    private void next(AppCompatActivity appCompatActivity, BocLottieCommonDialog bocLottieCommonDialog, BocLottieDialogEnum bocLottieDialogEnum, String hint) {
        TimerKit.getInstance().execute(appCompatActivity, RestoreKit.getInstance().delay, () -> bocLottieCommonDialog.update(bocLottieDialogEnum, hint, 0, () -> {
            // 预存或恢复功能数据库表
            RestoreFunctionDataBaseTableKit.getInstance().preStoreOrRestoreFunctionDataBaseTable(appCompatActivity, bocLottieCommonDialog);
        }));
    }

    private static final class InstanceHolder {
        static final RestoreAccountDataBaseTableKit INSTANCE = new RestoreAccountDataBaseTableKit();
    }
}