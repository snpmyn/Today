package com.zsp.today.basic.kit;

import android.animation.ValueAnimator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zsp.today.R;
import com.zsp.today.application.App;
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
import util.rxbus.RxBus;
import util.timer.TimerKit;
import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.dialog.bocdialog.lottie.BocLottieCommonDialog;
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
     * @param appCompatActivity     活动
     * @param bocLottieCommonDialog BOC Lottie 普通对话框
     */
    public void preStoreOrRestoreFunctionDataBaseTable(AppCompatActivity appCompatActivity, BocLottieCommonDialog bocLottieCommonDialog) {
        if (LitePalKit.getInstance().count(FunctionDataBaseTable.class) != 0) {
            // 功能数据库表有数据
            // 恢复险情数据库表
            RestoreDangerousDataBaseTableKit.getInstance().restoreDangerousDataBaseTable(appCompatActivity, null);
            return;
        }
        if (null == bocLottieCommonDialog) {
            bocLottieCommonDialog = BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogTwo(BocLottieDialogEnum.LOADING_TWO, appCompatActivity.getString(R.string.restoreHomePageMenu), ValueAnimator.INFINITE, null, null);
        } else {
            bocLottieCommonDialog.update(BocLottieDialogEnum.LOADING_TWO, appCompatActivity.getString(R.string.restoreHomePageMenu), ValueAnimator.INFINITE, null);
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
                preStoreFunctionDataBaseTable(appCompatActivity, bocLottieCommonDialog);
            } else {
                // 有功能数据库表文件数据
                // 转化存储恢复
                List<FunctionDataBaseTable> functionDataBaseTableList = new ArrayList<>(functionDataBaseTables.size());
                for (FunctionDataBaseTable functionDataBaseTable : functionDataBaseTables) {
                    functionDataBaseTableList.add(new FunctionDataBaseTable(functionDataBaseTable.getPhoneNumber(), functionDataBaseTable.getDate(), functionDataBaseTable.getFunctionId(), functionDataBaseTable.getFunctionName(), functionDataBaseTable.getFunctionShow()));
                }
                if (LitePalKit.getInstance().multiSave(functionDataBaseTableList)) {
                    // 预存或恢复功能数据库表成功
                    preStoreOrRestoreFunctionDataBaseTableSuccessful(appCompatActivity, bocLottieCommonDialog, appCompatActivity.getString(R.string.homePageMenuHasBeenRestored));
                }
            }
        } else {
            // 功能数据库表文件不存在
            // 预存功能数据库表
            preStoreFunctionDataBaseTable(appCompatActivity, bocLottieCommonDialog);
        }
    }

    /**
     * 预存功能数据库表
     *
     * @param appCompatActivity     活动
     * @param bocLottieCommonDialog BOC Lottie 普通对话框
     */
    private void preStoreFunctionDataBaseTable(AppCompatActivity appCompatActivity, BocLottieCommonDialog bocLottieCommonDialog) {
        HomePageMenuEnum[] homePageMenuEnums = HomePageMenuEnum.values();
        List<FunctionDataBaseTable> functionDataBaseTableList = new ArrayList<>(homePageMenuEnums.length);
        for (HomePageMenuEnum homePageMenuEnum : homePageMenuEnums) {
            if (!homePageMenuEnum.getMenuShow()) {
                continue;
            }
            functionDataBaseTableList.add(new FunctionDataBaseTable(App.getAppInstance().getPhoneNumber(), null, homePageMenuEnum.getMenuId(), homePageMenuEnum.getMenuName(), true));
        }
        if (LitePalKit.getInstance().multiSave(functionDataBaseTableList)) {
            // 预存或恢复功能数据库表成功
            preStoreOrRestoreFunctionDataBaseTableSuccessful(appCompatActivity, bocLottieCommonDialog, appCompatActivity.getString(R.string.homePageMenuHasBeenPreStored));
        }
    }

    /**
     * 预存或恢复功能数据库表成功
     *
     * @param appCompatActivity     活动
     * @param bocLottieCommonDialog BOC Lottie 普通对话框
     * @param hint                  提示
     */
    private void preStoreOrRestoreFunctionDataBaseTableSuccessful(AppCompatActivity appCompatActivity, BocLottieCommonDialog bocLottieCommonDialog, String hint) {
        TimerKit.getInstance().execute(appCompatActivity, RestoreKit.getInstance().delay, () -> bocLottieCommonDialog.update(BocLottieDialogEnum.SUCCESS_TWO, hint, 0, () -> {
            // 刷新菜单
            RxBus.get().post(RxBusConstant.HOME_PAGE_CHILD_FRAGMENT_$_REFRESH_MENU, RxBusConstant.HOME_PAGE_CHILD_FRAGMENT_$_REFRESH_MENU_CODE);
            // 恢复险情数据库表
            RestoreDangerousDataBaseTableKit.getInstance().restoreDangerousDataBaseTable(appCompatActivity, bocLottieCommonDialog);
        }));
    }

    private static final class InstanceHolder {
        static final RestoreFunctionDataBaseTableKit INSTANCE = new RestoreFunctionDataBaseTableKit();
    }
}