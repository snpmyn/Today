package com.zsp.today.basic.restore.kit;

import android.animation.ValueAnimator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zsp.today.R;
import com.zsp.today.basic.restore.value.RestoreConstant;
import com.zsp.today.basic.value.PublicConstant;
import com.zsp.today.module.account.database.AccountDataBaseTable;

import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.file.FileUtils;
import util.json.JsonTransform;
import util.list.ListUtils;
import util.mmkv.MmkvKit;
import util.timer.TimerKit;
import widget.dialog.bocdialog.kit.BocDialogKit;
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
     * @param appCompatActivity  活动
     * @param restoreKitListener 恢复配套元件监听
     */
    public void restoreAccountDataBaseTable(AppCompatActivity appCompatActivity, RestoreKit.RestoreKitListener restoreKitListener) {
        if (MmkvKit.defaultMmkv().decodeBool(RestoreConstant.RESTORE_$_ACCOUNT_DATA_BASE_TABLE)) {
            // 恢复险情数据库表
            RestoreDangerousDataBaseTableKit.getInstance().restoreDangerousDataBaseTable(appCompatActivity, restoreKitListener);
            return;
        }
        RestoreKit.getInstance().bocLottieCommonDialog = BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogTwo(BocLottieDialogEnum.LOADING_ONE, appCompatActivity.getString(R.string.restoreAccount), ValueAnimator.INFINITE, null, null);
        if (FileUtils.areFileExistByPath(RestoreKit.getInstance().accountDataBaseTableFileAbsolutePath)) {
            // 账目数据库表文件存在
            // 获取并转化账目数据库表文件数据
            List<AccountDataBaseTable> accountDataBaseTables = new Gson().fromJson(JsonTransform.transformJsonFromFile(RestoreKit.getInstance().accountDataBaseTableFileAbsolutePath), new TypeToken<List<AccountDataBaseTable>>() {
            }.getType());
            if (ListUtils.listIsEmpty(accountDataBaseTables)) {
                // 无账目数据库表文件数据
                // 下一步
                next(appCompatActivity, restoreKitListener, BocLottieDialogEnum.EMPTY_ONE, appCompatActivity.getString(R.string.noAccountAvailable));
            } else {
                // 有账目数据库表文件数据
                // 转化存储恢复
                List<AccountDataBaseTable> accountDataBaseTableList = new ArrayList<>(accountDataBaseTables.size());
                for (AccountDataBaseTable accountDataBaseTable : accountDataBaseTables) {
                    accountDataBaseTableList.add(new AccountDataBaseTable(accountDataBaseTable.getPhoneNumber(), accountDataBaseTable.getDate(), accountDataBaseTable.getCategory(), accountDataBaseTable.getAmount()));
                }
                if (LitePalKit.getInstance().multiSave(accountDataBaseTableList)) {
                    // 下一步
                    next(appCompatActivity, restoreKitListener, BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.accountHasBeenRestored));
                }
            }
        } else {
            // 账目数据库表文件不存在
            // 下一步
            next(appCompatActivity, restoreKitListener, BocLottieDialogEnum.EMPTY_ONE, appCompatActivity.getString(R.string.noAccountAvailable));
        }
    }

    /**
     * 下一步
     *
     * @param appCompatActivity   活动
     * @param restoreKitListener  恢复配套元件监听
     * @param bocLottieDialogEnum BOC Lottie 对话框枚举
     * @param hint                提示
     */
    private void next(AppCompatActivity appCompatActivity, RestoreKit.RestoreKitListener restoreKitListener, BocLottieDialogEnum bocLottieDialogEnum, String hint) {
        MmkvKit.defaultMmkv().encode(RestoreConstant.RESTORE_$_ACCOUNT_DATA_BASE_TABLE, true);
        TimerKit.getInstance().execute(appCompatActivity, PublicConstant.DELAY_DURATION, () -> RestoreKit.getInstance().bocLottieCommonDialog.update(bocLottieDialogEnum, hint, 0, () -> {
            // 恢复险情数据库表
            RestoreDangerousDataBaseTableKit.getInstance().restoreDangerousDataBaseTable(appCompatActivity, restoreKitListener);
        }));
    }

    private static final class InstanceHolder {
        static final RestoreAccountDataBaseTableKit INSTANCE = new RestoreAccountDataBaseTableKit();
    }
}