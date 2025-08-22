package com.zsp.today.module.mine.kit;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.module.login.UserDataBaseTable;
import com.zsp.today.module.mine.fragment.MineChildFragment;
import com.zsp.today.module.mine.fragment.SplashAnimationHomeFragment;
import com.zsp.today.value.RxBusConstant;

import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import pool.module.login.LoginActivity;
import util.cache.CacheManager;
import util.intent.IntentJump;
import util.rxbus.RxBus;
import widget.toast.ToastKit;
import widget.adapttemplate.bean.MenuBean;
import widget.adapttemplate.kit.MenuAdapterKit;
import widget.dialog.materialalertdialog.MyMaterialAlertDialogBuilder;

/**
 * Created on 2021/2/1
 *
 * @author zsp
 * @desc 我的子碎片配套元件
 */
public class MineChildFragmentKit {
    /**
     * 设置姓名
     *
     * @param appCompatActivity 活动
     * @param textViewName      姓名
     */
    public void setName(@NonNull AppCompatActivity appCompatActivity, @NonNull TextView textViewName) {
        textViewName.setText(App.getAppInstance().getPhoneNumber());
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
        List<MenuBean> moduleBeanList = new ArrayList<>(2);
        moduleBeanList.add(new MenuBean(1, R.drawable.ic_start_animation_basic_20dp, appCompatActivity.getString(R.string.startAnimation)));
        moduleBeanList.add(new MenuBean(2, R.drawable.ic_clean_cache_basic_20dp, appCompatActivity.getString(R.string.cleanCache)));
        moduleBeanList.add(new MenuBean(3, R.drawable.ic_log_out_basic_20dp, appCompatActivity.getString(R.string.logOut)));
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
            // 清理缓存
            case 2:
                cleanCache(appCompatActivity);
                break;
            // 退出
            case 3:
                loginOut(appCompatActivity);
                break;
            default:
                break;
        }
    }

    /**
     * 清理缓存
     *
     * @param appCompatActivity 活动
     */
    public void cleanCache(AppCompatActivity appCompatActivity) {
        String totalCacheSize = CacheManager.totalCacheSize(appCompatActivity);
        if (CacheManager.STRING_ZERO_K.equals(totalCacheSize)) {
            ToastKit.showShort(appCompatActivity.getString(R.string.noCacheAvailable));
        } else {
            CacheManager.clearAllCache(appCompatActivity);
            ToastKit.showShort(String.format(appCompatActivity.getString(R.string.formatCleanCacheSuccessful), totalCacheSize));
        }
    }

    /**
     * 退出
     *
     * @param appCompatActivity 活动
     */
    public void loginOut(AppCompatActivity appCompatActivity) {
        new MyMaterialAlertDialogBuilder(appCompatActivity).setTitle(com.zsp.core.R.string.hint).setMessage(R.string.needToInputPhoneNumberToLoginAgainAfterLogOut).setPositiveButton(R.string.logOut, (dialog, which) -> {
            dialog.dismiss();
            LitePalKit.getInstance().allDelete(UserDataBaseTable.class);
            IntentJump.getInstance().jump(null, appCompatActivity, true, LoginActivity.class);
        }).setNegativeButton(R.string.wait, (dialog, which) -> dialog.dismiss()).show();
    }
}