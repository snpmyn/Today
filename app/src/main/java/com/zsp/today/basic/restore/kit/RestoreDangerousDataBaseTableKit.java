package com.zsp.today.basic.restore.kit;

import android.animation.ValueAnimator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zsp.today.R;
import com.zsp.today.basic.restore.value.RestoreConstant;
import com.zsp.today.basic.value.PublicConstant;
import com.zsp.today.module.dangerous.database.DangerousDataBaseTable;

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
import widget.dialog.bocdialog.lottie.listener.BocLottieDialogAnimationEndListener;

/**
 * Created on 2025/9/5.
 *
 * @author 郑少鹏
 * @desc 恢复险情数据库表配套原件
 */
public class RestoreDangerousDataBaseTableKit {
    public static RestoreDangerousDataBaseTableKit getInstance() {
        return RestoreDangerousDataBaseTableKit.InstanceHolder.INSTANCE;
    }

    /**
     * 恢复险情数据库表
     *
     * @param appCompatActivity  活动
     * @param restoreKitListener 恢复配套元件监听
     */
    public void restoreDangerousDataBaseTable(AppCompatActivity appCompatActivity, RestoreKit.RestoreKitListener restoreKitListener) {
        if (MmkvKit.defaultMmkv().decodeBool(RestoreConstant.RESTORE_$_DANGEROUS_DATA_BASE_TABLE)) {
            end(appCompatActivity, restoreKitListener);
            return;
        }
        if (null == RestoreKit.getInstance().bocLottieCommonDialog) {
            RestoreKit.getInstance().bocLottieCommonDialog = BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogTwo(BocLottieDialogEnum.LOADING_ONE, appCompatActivity.getString(R.string.restoreDangerousConfig), ValueAnimator.INFINITE, null, null);
        } else {
            RestoreKit.getInstance().bocLottieCommonDialog.update(BocLottieDialogEnum.LOADING_ONE, appCompatActivity.getString(R.string.restoreDangerousConfig), ValueAnimator.INFINITE, null);
        }
        if (FileUtils.areFileExistByPath(RestoreKit.getInstance().dangerousDataBaseTableFileAbsolutePath)) {
            // 险情数据库表文件存在
            // 获取并转化险情数据库表文件数据
            List<DangerousDataBaseTable> dangerousDataBaseTables = new Gson().fromJson(JsonTransform.transformJsonFromFile(RestoreKit.getInstance().dangerousDataBaseTableFileAbsolutePath), new TypeToken<List<DangerousDataBaseTable>>() {
            }.getType());
            if (ListUtils.listIsEmpty(dangerousDataBaseTables)) {
                // 无险情数据库表文件数据
                // 下一步
                next(appCompatActivity, restoreKitListener, BocLottieDialogEnum.EMPTY_ONE, appCompatActivity.getString(R.string.noDangerousConfigAvailable));
            } else {
                // 有险情数据库表文件数据
                // 转化存储恢复
                List<DangerousDataBaseTable> dangerousDataBaseTableList = new ArrayList<>(dangerousDataBaseTables.size());
                for (DangerousDataBaseTable dangerousDataBaseTable : dangerousDataBaseTables) {
                    dangerousDataBaseTableList.add(new DangerousDataBaseTable(dangerousDataBaseTable.getPhoneNumber(), dangerousDataBaseTable.getDate(), dangerousDataBaseTable.getDangerousNotice(), dangerousDataBaseTable.getEmergencyContactPhoneNumber()));
                }
                if (LitePalKit.getInstance().multiSave(dangerousDataBaseTableList)) {
                    // 下一步
                    next(appCompatActivity, restoreKitListener, BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.dangerousConfigHasBeenRestored));
                }
            }
        } else {
            // 险情数据库表文件不存在
            // 下一步
            next(appCompatActivity, restoreKitListener, BocLottieDialogEnum.EMPTY_ONE, appCompatActivity.getString(R.string.noDangerousConfigAvailable));
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
        MmkvKit.defaultMmkv().encode(RestoreConstant.RESTORE_$_DANGEROUS_DATA_BASE_TABLE, true);
        TimerKit.getInstance().execute(appCompatActivity, PublicConstant.DELAY_DURATION, () -> RestoreKit.getInstance().bocLottieCommonDialog.update(bocLottieDialogEnum, hint, 0, new BocLottieDialogAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                end(appCompatActivity, restoreKitListener);
            }
        }));
    }

    /**
     * 结束
     *
     * @param appCompatActivity  活动
     * @param restoreKitListener 恢复配套元件监听
     */
    private void end(AppCompatActivity appCompatActivity, RestoreKit.RestoreKitListener restoreKitListener) {
        // 置空
        if (null != RestoreKit.getInstance().bocLottieCommonDialog) {
            RestoreKit.getInstance().bocLottieCommonDialog = null;
        }
        // 结束
        BocDialogKit.getInstance(appCompatActivity).end();
        if (null != restoreKitListener) {
            // 结束
            restoreKitListener.end();
        }
    }

    private static final class InstanceHolder {
        static final RestoreDangerousDataBaseTableKit INSTANCE = new RestoreDangerousDataBaseTableKit();
    }
}