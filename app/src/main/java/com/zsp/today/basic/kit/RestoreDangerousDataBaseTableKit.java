package com.zsp.today.basic.kit;

import android.animation.ValueAnimator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zsp.today.R;
import com.zsp.today.basic.value.Folder;
import com.zsp.today.module.dangerous.database.DangerousDataBaseTable;

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
 * @desc 恢复险情数据库表配套原件
 */
public class RestoreDangerousDataBaseTableKit {
    public static RestoreDangerousDataBaseTableKit getInstance() {
        return RestoreDangerousDataBaseTableKit.InstanceHolder.INSTANCE;
    }

    /**
     * 恢复险情数据库表
     *
     * @param appCompatActivity     活动
     * @param bocLottieCommonDialog BOC Lottie 普通对话框
     */
    public void restoreDangerousDataBaseTable(AppCompatActivity appCompatActivity, BocLottieCommonDialog bocLottieCommonDialog) {
        if (LitePalKit.getInstance().count(DangerousDataBaseTable.class) != 0) {
            // 险情数据库表有数据
            // 结束
            BocDialogKit.getInstance(appCompatActivity).end();
            return;
        }
        if (null == bocLottieCommonDialog) {
            bocLottieCommonDialog = BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogTwo(BocLottieDialogEnum.LOADING_TWO, appCompatActivity.getString(R.string.restoreDangerousConfig), ValueAnimator.INFINITE, null, null);
        } else {
            bocLottieCommonDialog.update(BocLottieDialogEnum.LOADING_TWO, appCompatActivity.getString(R.string.restoreDangerousConfig), ValueAnimator.INFINITE, null);
        }
        // 险情数据库表文件地址
        String dangerousDataBaseTableFilePath = Folder.EXTERNAL_BACKUP + File.separator + DangerousDataBaseTable.class.getSimpleName() + ".json";
        if (FileUtils.areFileExistByPath(dangerousDataBaseTableFilePath)) {
            // 险情数据库表文件存在
            // 获取并转化险情数据库表文件数据
            List<DangerousDataBaseTable> dangerousDataBaseTables = new Gson().fromJson(JsonTransform.transformJsonFromFile(dangerousDataBaseTableFilePath), new TypeToken<List<DangerousDataBaseTable>>() {
            }.getType());
            if (ListUtils.listIsEmpty(dangerousDataBaseTables)) {
                // 无险情数据库表文件数据
                // 下一步
                next(appCompatActivity, bocLottieCommonDialog, BocLottieDialogEnum.FIRST_SET_RESULT_FAIL, appCompatActivity.getString(R.string.noDangerousConfigAvailable));
            } else {
                // 有险情数据库表文件数据
                // 转化存储恢复
                List<DangerousDataBaseTable> dangerousDataBaseTableList = new ArrayList<>(dangerousDataBaseTables.size());
                for (DangerousDataBaseTable dangerousDataBaseTable : dangerousDataBaseTables) {
                    dangerousDataBaseTableList.add(new DangerousDataBaseTable(dangerousDataBaseTable.getPhoneNumber(), dangerousDataBaseTable.getDate(), dangerousDataBaseTable.getDangerousNotice(), dangerousDataBaseTable.getEmergencyContactPhoneNumber()));
                }
                if (LitePalKit.getInstance().multiSave(dangerousDataBaseTableList)) {
                    // 下一步
                    next(appCompatActivity, bocLottieCommonDialog, BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.dangerousConfigHasBeenRestored));
                }
            }
        } else {
            // 险情数据库表文件不存在
            // 下一步
            next(appCompatActivity, bocLottieCommonDialog, BocLottieDialogEnum.FIRST_SET_RESULT_FAIL, appCompatActivity.getString(R.string.noDangerousConfigAvailable));
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
            // 结束
            BocDialogKit.getInstance(appCompatActivity).end();
        }));
    }

    private static final class InstanceHolder {
        static final RestoreDangerousDataBaseTableKit INSTANCE = new RestoreDangerousDataBaseTableKit();
    }
}