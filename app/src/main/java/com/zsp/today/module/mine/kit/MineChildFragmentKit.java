package com.zsp.today.module.mine.kit;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.BuildConfig;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.basic.restore.kit.RestoreKit;
import com.zsp.today.basic.restore.value.RestoreConstant;
import com.zsp.today.basic.value.PublicConstant;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.basic.version.kit.VersionKit;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.module.dangerous.database.DangerousDataBaseTable;
import com.zsp.today.module.homecome.database.HomeComeDataBaseTable;
import com.zsp.today.module.login.UserDataBaseTable;
import com.zsp.today.module.mine.fragment.MineChildFragment;
import com.zsp.today.module.mine.fragment.SplashAnimationHomeFragment;
import com.zsp.today.module.setting.SettingActivity;

import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import pool.module.login.LoginActivity;
import util.cache.CacheManager;
import util.mmkv.MmkvKit;
import util.rxbus.RxBus;
import util.timer.TimerKit;
import widget.adapttemplate.bean.MenuBean;
import widget.adapttemplate.kit.MenuAdapterKit;
import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.dialog.bocdialog.lottie.BocLottieCommonDialog;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;
import widget.dialog.materialalertdialog.DebugMaterialAlertDialogKit;
import widget.dialog.materialalertdialog.HintMaterialAlertDialogKit;
import widget.dialog.materialalertdialog.MaterialAlertDialogBuilderKit;
import widget.dialog.materialalertdialog.UseGuideMaterialAlertDialogKit;
import widget.emoji.MoodEmojiKit;
import widget.media.audio.AudioPlayKit;
import widget.toast.ToastKit;
import widget.transition.kit.TransitionKit;

/**
 * Created on 2021/2/1
 *
 * @author zsp
 * @desc 我的子碎片配套元件
 */
public class MineChildFragmentKit {
    /**
     * BOC Lottie 普通对话框
     */
    private BocLottieCommonDialog bocLottieCommonDialog;

    /**
     * 设置昵称
     *
     * @param appCompatActivity 活动
     * @param textViewNickName  昵称
     * @param materialToolbar   MaterialToolbar
     * @param init              初始化否
     */
    public void setNickName(AppCompatActivity appCompatActivity, TextView textViewNickName, MaterialToolbar materialToolbar, boolean init) {
        if (init) {
            textViewNickName.setText(App.getAppInstance().getPhoneNumber());
        } else {
            materialToolbar.setTitle(appCompatActivity.getString(R.string.app_name));
        }
    }

    /**
     * 展示
     *
     * @param appCompatActivity 活动
     * @param mineChildFragment 我的子碎片
     * @param recyclerView      控件
     */
    public void display(@NonNull AppCompatActivity appCompatActivity, MineChildFragment mineChildFragment, RecyclerView recyclerView) {
        // 数据
        List<MenuBean> moduleBeanList = new ArrayList<>(7);
        moduleBeanList.add(new MenuBean(1, R.drawable.ic_start_animation_cos_24dp, appCompatActivity.getString(R.string.startAnimation)));
        moduleBeanList.add(new MenuBean(2, R.drawable.ic_reset_cos_24dp, appCompatActivity.getString(R.string.resetData)));
        moduleBeanList.add(new MenuBean(3, R.drawable.ic_clean_cache_cos_24dp, appCompatActivity.getString(R.string.cleanCache)));
        moduleBeanList.add(new MenuBean(4, R.drawable.ic_system_update_alt_cos_24dp, appCompatActivity.getString(com.zsp.core.R.string.versionUpdate)));
        moduleBeanList.add(new MenuBean(5, R.drawable.ic_author_cos_24dp, appCompatActivity.getString(R.string.author)));
        moduleBeanList.add(new MenuBean(6, R.drawable.ic_settings_applications_cos_24dp, appCompatActivity.getString(R.string.applicationSetting)));
        moduleBeanList.add(new MenuBean(7, R.drawable.ic_log_out_cos_24dp, appCompatActivity.getString(R.string.logOut)));
        // 模块适配器配套元件
        MenuAdapterKit menuAdapterKit = new MenuAdapterKit();
        menuAdapterKit.display(appCompatActivity, recyclerView, moduleBeanList, 3, 12, 48, false, (view, menuBean) -> distribute(appCompatActivity, view, mineChildFragment, menuBean.getMenuId()));
    }

    /**
     * 分发
     *
     * @param appCompatActivity 活动
     * @param view              视图
     * @param mineChildFragment 我的子碎片
     * @param functionId        功能 ID
     */
    private void distribute(AppCompatActivity appCompatActivity, View view, MineChildFragment mineChildFragment, int functionId) {
        switch (functionId) {
            // 启动动画
            case 1:
                RxBus.get().post(RxBusConstant.MAIN_ACTIVITY_$_BOTTOM_NAVIGATION_VIEW, RxBusConstant.MAIN_ACTIVITY_$_HIDE_BOTTOM_NAVIGATION_VIEW_CODE);
                mineChildFragment.start(SplashAnimationHomeFragment.newInstance());
                break;
            // 重置数据
            case 2:
                resetData(appCompatActivity);
                break;
            // 清理缓存
            case 3:
                cleanCache(appCompatActivity);
                break;
            // 版本更新
            case 4:
                VersionKit.check(appCompatActivity, true);
                break;
            // 作者
            case 5:
                author(appCompatActivity);
                break;
            // 应用设置
            case 6:
                Intent fromThisToSettingActivityIntent = new Intent(appCompatActivity, SettingActivity.class);
                TransitionKit.getInstance().jumpWithTransition(appCompatActivity, view, fromThisToSettingActivityIntent, false);
                break;
            // 退出
            case 7:
                loginOut(appCompatActivity, view);
                break;
            default:
                break;
        }
    }

    /**
     * 重置数据
     *
     * @param appCompatActivity 活动
     */
    private void resetData(AppCompatActivity appCompatActivity) {
        UseGuideMaterialAlertDialogKit useGuideMaterialAlertDialogKit = getUseGuideMaterialAlertDialogKit();
        useGuideMaterialAlertDialogKit.show(appCompatActivity, 0, false, new UseGuideMaterialAlertDialogKit.UseGuideMaterialAlertDialogKitListener() {
            @Override
            public void start() {

            }

            @Override
            public void end() {
                bocLottieCommonDialog = BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogTwo(BocLottieDialogEnum.LOADING_ONE, appCompatActivity.getString(R.string.deleteData), ValueAnimator.INFINITE, null, null);
                // 重置恢复常量值
                MmkvKit.defaultMmkv().encode(RestoreConstant.RESTORE_$_ACCOUNT_DATA_BASE_TABLE, false);
                MmkvKit.defaultMmkv().encode(RestoreConstant.RESTORE_$_DANGEROUS_DATA_BASE_TABLE, false);
                MmkvKit.defaultMmkv().encode(RestoreConstant.RESTORE_$_HOME_COME_DATA_BASE_TABLE, false);
                // 重置数据库
                LitePalKit.getInstance().allDelete(AccountDataBaseTable.class);
                LitePalKit.getInstance().allDelete(DangerousDataBaseTable.class);
                LitePalKit.getInstance().allDelete(HomeComeDataBaseTable.class);
                TimerKit.getInstance().execute(appCompatActivity, PublicConstant.DELAY_DURATION, () -> bocLottieCommonDialog.update(BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.dataHasBeenDeleted), 0, () -> {
                    bocLottieCommonDialog = null;
                    BocDialogKit.getInstance(appCompatActivity).end();
                    // 恢复
                    RestoreKit.getInstance().restore(appCompatActivity, null);
                }));
            }
        });
    }

    /**
     * 获取使用指南材料对话框配套元件
     *
     * @return 使用指南材料对话框配套元件
     */
    @NonNull
    private static UseGuideMaterialAlertDialogKit getUseGuideMaterialAlertDialogKit() {
        UseGuideMaterialAlertDialogKit useGuideMaterialAlertDialogKit = new UseGuideMaterialAlertDialogKit();
        useGuideMaterialAlertDialogKit.prepareData("步骤一", MoodEmojiKit.Mood.FOLDER.getEmoji() + " 删除数据 " + MoodEmojiKit.Mood.FOLDER.getEmoji() + "\n\n删除账目数据\n删除险情数据\n删除归心数据", "等下", "下一步");
        useGuideMaterialAlertDialogKit.prepareData("步骤二", MoodEmojiKit.Mood.FOLDER.getEmoji() + " 恢复数据 " + MoodEmojiKit.Mood.FOLDER.getEmoji() + "\n\nDocuments 文件夹下数据备份文件\n\n恢复账目数据\n恢复险情数据\n恢复归心数据", "上一步", "去重置");
        return useGuideMaterialAlertDialogKit;
    }

    /**
     * 清理缓存
     *
     * @param appCompatActivity 活动
     */
    private void cleanCache(AppCompatActivity appCompatActivity) {
        String totalCacheSize = CacheManager.totalCacheSize(appCompatActivity);
        if (TextUtils.equals(CacheManager.STRING_ZERO_K, totalCacheSize)) {
            ToastKit.showShort(appCompatActivity.getString(R.string.noCacheAvailable));
        } else {
            AudioPlayKit.CacheHelper.clearCache(appCompatActivity);
            CacheManager.clearAllCache(appCompatActivity);
            ToastKit.showShort(String.format("已清理 %1$s", totalCacheSize));
            DebugMaterialAlertDialogKit.getInstance().show(appCompatActivity, String.format("已清理 %1$s", totalCacheSize), BuildConfig.DEBUG);
        }
    }

    /**
     * 作者
     *
     * @param appCompatActivity 活动
     */
    private void author(AppCompatActivity appCompatActivity) {
        new MaterialAlertDialogBuilderKit(appCompatActivity, com.zsp.core.R.style.ThemeOverlay_Catalog_MaterialAlertDialog_Centered_FullWidthButtons).setView(R.layout.author).show();
    }

    /**
     * 退出
     *
     * @param appCompatActivity 活动
     * @param view              视图
     */
    private void loginOut(AppCompatActivity appCompatActivity, View view) {
        HintMaterialAlertDialogKit.getInstance().show(appCompatActivity, null, appCompatActivity.getString(R.string.needToInputPhoneNumberToLoginAgainAfterLogOut), appCompatActivity.getString(R.string.logOut), appCompatActivity.getString(R.string.wait), null);
        HintMaterialAlertDialogKit.getInstance().setHintMaterialAlertDialogKitOnPositiveClickListener(alertDialog -> {
            alertDialog.dismiss();
            LitePalKit.getInstance().allDelete(UserDataBaseTable.class);
            Intent fromThisToLoginActivityIntent = new Intent(appCompatActivity, LoginActivity.class);
            TransitionKit.getInstance().jumpWithTransition(appCompatActivity, view, fromThisToLoginActivityIntent, true);
        });
        HintMaterialAlertDialogKit.getInstance().setHintMaterialAlertDialogKitOnNegativeClickListener(AppCompatDialog::dismiss);
    }
}