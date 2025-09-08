package com.zsp.today.basic.restore.kit;

import android.animation.ValueAnimator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.basic.backup.BackupKit;
import com.zsp.today.basic.restore.value.RestoreConstant;
import com.zsp.today.basic.value.Folder;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.function.database.FunctionDataBaseTable;
import com.zsp.today.module.homepage.bean.HomePageMenuEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.file.FileUtils;
import util.json.JsonTransform;
import util.list.ListUtils;
import util.mmkv.MmkvKit;
import util.rxbus.RxBus;
import util.timer.TimerKit;
import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;

/**
 * Created on 2025/9/5.
 *
 * @author 郑少鹏
 * @desc 恢复功能数据库表配套原件
 */
public class RestoreFunctionDataBaseTableKit {
    public static RestoreFunctionDataBaseTableKit getInstance() {
        return RestoreFunctionDataBaseTableKit.InstanceHolder.INSTANCE;
    }

    /**
     * 预存或恢复功能数据库表
     *
     * @param appCompatActivity 活动
     */
    public void preStoreOrRestoreFunctionDataBaseTable(AppCompatActivity appCompatActivity) {
        if (MmkvKit.defaultMmkv().decodeBool(RestoreConstant.RESTORE_$_FUNCTION_DATA_BASE_TABLE)) {
            // 恢复险情数据库表
            RestoreDangerousDataBaseTableKit.getInstance().restoreDangerousDataBaseTable(appCompatActivity);
            return;
        }
        if (null == RestoreKit.getInstance().bocLottieCommonDialog) {
            RestoreKit.getInstance().bocLottieCommonDialog = BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogTwo(BocLottieDialogEnum.LOADING_ONE, appCompatActivity.getString(R.string.restoreHomePageMenu), ValueAnimator.INFINITE, null, null);
        } else {
            RestoreKit.getInstance().bocLottieCommonDialog.update(BocLottieDialogEnum.LOADING_ONE, appCompatActivity.getString(R.string.restoreHomePageMenu), ValueAnimator.INFINITE, null);
        }
        // 功能数据库表文件地址
        String functionDataBaseTableFilePath = Folder.EXTERNAL_BACKUP + File.separator + FunctionDataBaseTable.class.getSimpleName() + ".json";
        if (FileUtils.areFileExistByPath(functionDataBaseTableFilePath)) {
            // 功能数据库表文件存在
            // 获取并转化功能数据库表文件数据
            List<FunctionDataBaseTable> functionDataBaseTables = new Gson().fromJson(JsonTransform.transformJsonFromFile(functionDataBaseTableFilePath), new TypeToken<List<FunctionDataBaseTable>>() {
            }.getType());
            if (ListUtils.listIsEmpty(functionDataBaseTables)) {
                // 无功能数据库表文件数据
                // 预存功能数据库表
                preStoreFunctionDataBaseTable(appCompatActivity);
            } else {
                // 有功能数据库表文件数据
                // 转化存储恢复
                List<FunctionDataBaseTable> functionDataBaseTableList = new ArrayList<>(functionDataBaseTables.size());
                for (FunctionDataBaseTable functionDataBaseTable : functionDataBaseTables) {
                    functionDataBaseTableList.add(new FunctionDataBaseTable(functionDataBaseTable.getPhoneNumber(), functionDataBaseTable.getDate(), functionDataBaseTable.getFunctionId(), functionDataBaseTable.getFunctionName(), functionDataBaseTable.getFunctionShow()));
                }
                if (LitePalKit.getInstance().multiSave(functionDataBaseTableList)) {
                    // 下一步
                    next(appCompatActivity, appCompatActivity.getString(R.string.homePageMenuHasBeenRestored));
                }
            }
        } else {
            // 功能数据库表文件不存在
            // 预存功能数据库表
            preStoreFunctionDataBaseTable(appCompatActivity);
        }
    }

    /**
     * 预存功能数据库表
     *
     * @param appCompatActivity 活动
     */
    private void preStoreFunctionDataBaseTable(AppCompatActivity appCompatActivity) {
        HomePageMenuEnum[] homePageMenuEnums = HomePageMenuEnum.values();
        List<FunctionDataBaseTable> functionDataBaseTableList = new ArrayList<>(homePageMenuEnums.length);
        for (HomePageMenuEnum homePageMenuEnum : homePageMenuEnums) {
            if (!homePageMenuEnum.getMenuShow()) {
                continue;
            }
            functionDataBaseTableList.add(new FunctionDataBaseTable(App.getAppInstance().getPhoneNumber(), null, homePageMenuEnum.getMenuId(), homePageMenuEnum.getMenuName(), true));
        }
        if (LitePalKit.getInstance().multiSave(functionDataBaseTableList)) {
            // 备份
            BackupKit.getInstance().backup(appCompatActivity, FunctionDataBaseTable.class, null);
            // 下一步
            next(appCompatActivity, appCompatActivity.getString(R.string.homePageMenuHasBeenPreStored));
        }
    }

    /**
     * 下一步
     *
     * @param appCompatActivity 活动
     * @param hint              提示
     */
    private void next(AppCompatActivity appCompatActivity, String hint) {
        MmkvKit.defaultMmkv().encode(RestoreConstant.RESTORE_$_FUNCTION_DATA_BASE_TABLE, true);
        TimerKit.getInstance().execute(appCompatActivity, RestoreKit.getInstance().delay, () -> RestoreKit.getInstance().bocLottieCommonDialog.update(BocLottieDialogEnum.SUCCESS_ONE, hint, 0, () -> {
            // 刷新菜单
            RxBus.get().post(RxBusConstant.HOME_PAGE_CHILD_FRAGMENT_$_REFRESH_MENU, RxBusConstant.HOME_PAGE_CHILD_FRAGMENT_$_REFRESH_MENU_CODE);
            // 恢复险情数据库表
            RestoreDangerousDataBaseTableKit.getInstance().restoreDangerousDataBaseTable(appCompatActivity);
        }));
    }

    private static final class InstanceHolder {
        static final RestoreFunctionDataBaseTableKit INSTANCE = new RestoreFunctionDataBaseTableKit();
    }
}