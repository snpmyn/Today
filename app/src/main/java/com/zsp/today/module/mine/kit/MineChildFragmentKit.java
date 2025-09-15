package com.zsp.today.module.mine.kit;

import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.basic.restore.kit.RestoreKit;
import com.zsp.today.basic.restore.value.RestoreConstant;
import com.zsp.today.basic.value.PublicConstant;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.module.dangerous.database.DangerousDataBaseTable;
import com.zsp.today.module.function.database.FunctionDataBaseTable;
import com.zsp.today.module.login.UserDataBaseTable;
import com.zsp.today.module.mine.fragment.MineChildFragment;
import com.zsp.today.module.mine.fragment.SplashAnimationHomeFragment;

import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import pool.module.login.LoginActivity;
import util.cache.CacheManager;
import util.intent.IntentJump;
import util.mmkv.MmkvKit;
import util.rxbus.RxBus;
import util.timer.TimerKit;
import widget.adapttemplate.bean.MenuBean;
import widget.adapttemplate.kit.MenuAdapterKit;
import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.dialog.bocdialog.lottie.BocLottieCommonDialog;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;
import widget.dialog.materialalertdialog.kit.MaterialAlertDialogBuilderKit;
import widget.dialog.materialalertdialog.kit.UseGuideMaterialAlertDialogKit;
import widget.dialog.materialalertdialog.listener.UseGuideMaterialAlertDialogKitListener;
import widget.toast.ToastKit;

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
     * @param textViewNickName 昵称
     * @param materialToolbar  MaterialToolbar
     * @param init             初始化否
     */
    public void setNickName(TextView textViewNickName, MaterialToolbar materialToolbar, boolean init) {
        String nickName = App.getAppInstance().getPhoneNumber();
        if (init) {
            textViewNickName.setText(nickName);
        } else {
            materialToolbar.setTitle(nickName);
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
        List<MenuBean> moduleBeanList = new ArrayList<>(4);
        moduleBeanList.add(new MenuBean(1, R.drawable.ic_start_animation_ccn_24dp, appCompatActivity.getString(R.string.startAnimation)));
        moduleBeanList.add(new MenuBean(2, R.drawable.ic_reset_ccn_24dp, appCompatActivity.getString(R.string.resetData)));
        moduleBeanList.add(new MenuBean(3, R.drawable.ic_clean_cache_ccn_24dp, appCompatActivity.getString(R.string.cleanCache)));
        moduleBeanList.add(new MenuBean(4, R.drawable.ic_author_ccn_24dp, appCompatActivity.getString(R.string.author)));
        moduleBeanList.add(new MenuBean(5, R.drawable.ic_donate_ccn_24dp, appCompatActivity.getString(R.string.donate)));
        moduleBeanList.add(new MenuBean(6, R.drawable.ic_log_out_ccn_24dp, appCompatActivity.getString(R.string.logOut)));
        // 模块适配器配套元件
        MenuAdapterKit menuAdapterKit = new MenuAdapterKit();
        menuAdapterKit.display(appCompatActivity, recyclerView, moduleBeanList, 3, 48, 192, false, (view, menuBean) -> distribute(appCompatActivity, mineChildFragment, menuBean.getMenuId()));
    }

    /**
     * 分发
     *
     * @param appCompatActivity 活动
     * @param mineChildFragment 我的子碎片
     * @param functionId        功能 ID
     */
    private void distribute(AppCompatActivity appCompatActivity, MineChildFragment mineChildFragment, int functionId) {
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
            // 作者
            case 4:
                author(appCompatActivity);
                break;
            // 赞助
            case 5:
                author(appCompatActivity);
                break;
            // 退出
            case 6:
                loginOut(appCompatActivity);
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
        UseGuideMaterialAlertDialogKit useGuideMaterialAlertDialogKit = new UseGuideMaterialAlertDialogKit();
        useGuideMaterialAlertDialogKit.prepareData("步骤一", "\uD83D\uDD25 删除数据 \uD83D\uDD25\n\n删除账目数据\n删除险情配置数据\n删除主页菜单数据", "等下", "下一步");
        useGuideMaterialAlertDialogKit.prepareData("步骤二", "\uD83D\uDD25 恢复数据 \uD83D\uDD25\n\nDocuments 文件夹下数据备份文件\n\n恢复账目数据\n恢复险情配置数据\n恢复主页菜单数据", "上一步", "去重置");
        useGuideMaterialAlertDialogKit.show(appCompatActivity, 0, false, new UseGuideMaterialAlertDialogKitListener() {
            @Override
            public void start() {

            }

            @Override
            public void end() {
                bocLottieCommonDialog = BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogTwo(BocLottieDialogEnum.LOADING_ONE, appCompatActivity.getString(R.string.deleteData), ValueAnimator.INFINITE, null, null);
                // 重置恢复常量值
                MmkvKit.defaultMmkv().encode(RestoreConstant.RESTORE_$_ACCOUNT_DATA_BASE_TABLE, false);
                MmkvKit.defaultMmkv().encode(RestoreConstant.RESTORE_$_FUNCTION_DATA_BASE_TABLE, false);
                MmkvKit.defaultMmkv().encode(RestoreConstant.RESTORE_$_DANGEROUS_DATA_BASE_TABLE, false);
                // 重置数据库
                LitePalKit.getInstance().allDelete(AccountDataBaseTable.class);
                LitePalKit.getInstance().allDelete(FunctionDataBaseTable.class);
                LitePalKit.getInstance().allDelete(DangerousDataBaseTable.class);
                TimerKit.getInstance().execute(appCompatActivity, PublicConstant.DELAY_DURATION, () -> bocLottieCommonDialog.update(BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.dataHasBeenDeleted), 0, () -> {
                    bocLottieCommonDialog = null;
                    BocDialogKit.getInstance(appCompatActivity).end();
                    // 恢复
                    RestoreKit.getInstance().restore(appCompatActivity);
                }));
            }
        });
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
            CacheManager.clearAllCache(appCompatActivity);
            ToastKit.showShort(String.format(appCompatActivity.getString(R.string.formatCleanCacheSuccessful), totalCacheSize));
        }
    }

    /**
     * 作者
     *
     * @param appCompatActivity 活动
     */
    private void author(AppCompatActivity appCompatActivity) {
        new MaterialAlertDialogBuilderKit(appCompatActivity, com.zsp.core.R.style.ThemeOverlay_Catalog_MaterialAlertDialog_Centered_FullWidthButtons).setView(R.layout.donate).show();
    }

    /**
     * 退出
     *
     * @param appCompatActivity 活动
     */
    private void loginOut(AppCompatActivity appCompatActivity) {
        new MaterialAlertDialogBuilderKit(appCompatActivity).setTitle(com.zsp.core.R.string.hint).setMessage(R.string.needToInputPhoneNumberToLoginAgainAfterLogOut).setPositiveButton(R.string.logOut, (dialog, which) -> {
            dialog.dismiss();
            LitePalKit.getInstance().allDelete(UserDataBaseTable.class);
            IntentJump.getInstance().jump(null, appCompatActivity, true, LoginActivity.class);
        }).setNegativeButton(R.string.wait, (dialog, which) -> dialog.dismiss()).show();
    }
}