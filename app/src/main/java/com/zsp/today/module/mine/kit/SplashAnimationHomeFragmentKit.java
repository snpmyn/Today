package com.zsp.today.module.mine.kit;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.R;
import com.zsp.today.module.mine.adapter.SplashAnimationListAdapter;
import com.zsp.today.module.mine.bean.SplashAnimationListBean;
import com.zsp.today.module.mine.fragment.SplashAnimationDetailFragment;
import com.zsp.today.module.mine.fragment.SplashAnimationHomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pool.application.BasePoolApp;
import pool.value.PoolConstant;
import util.mmkv.MmkvKit;
import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.dialog.bocdialog.lottie.bean.BocLottieDialogEnum;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.recyclerview.controller.RecyclerViewDisplayController;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;

/**
 * Created on 2022/5/7
 *
 * @author zsp
 * @desc 闪屏动画主页碎片配套元件
 */
public class SplashAnimationHomeFragmentKit {
    /**
     * 展示闪屏动画
     *
     * @param appCompatActivity           活动
     * @param recyclerView                控件
     * @param statusManager               状态管理器
     * @param splashAnimationHomeFragment 闪屏动画主页碎片
     */
    public void displaySplashAnimation(AppCompatActivity appCompatActivity, RecyclerView recyclerView, @NonNull StatusManager statusManager, SplashAnimationHomeFragment splashAnimationHomeFragment) {
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, true, null);
        // 数据
        List<SplashAnimationListBean> splashAnimationListBeanList = new ArrayList<>(4);
        splashAnimationListBeanList.add(new SplashAnimationListBean("lottie/splash/lottie_animation_splash_one"));
        splashAnimationListBeanList.add(new SplashAnimationListBean("lottie/splash/lottie_animation_splash_two"));
        splashAnimationListBeanList.add(new SplashAnimationListBean("lottie/splash/lottie_animation_splash_three"));
        splashAnimationListBeanList.add(new SplashAnimationListBean("lottie/splash/lottie_animation_splash_four"));
        // 控件
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(appCompatActivity, recyclerView);
        recyclerViewConfigure.gridLayout(2, 48, true, true, false);
        // 适配器
        SplashAnimationListAdapter splashAnimationListAdapter = getSplashAnimationListAdapter(appCompatActivity, splashAnimationHomeFragment, splashAnimationListBeanList);
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, false, splashAnimationListBeanList);
        // 展示
        RecyclerViewDisplayController.display(recyclerView, splashAnimationListAdapter);
    }

    /**
     * 获取闪屏动画列表适配器
     *
     * @param appCompatActivity           活动
     * @param splashAnimationHomeFragment 闪屏动画主页碎片
     * @param splashAnimationListBeanList 闪屏动画列表数据集
     * @return 闪屏动画列表适配器
     */
    @NonNull
    private SplashAnimationListAdapter getSplashAnimationListAdapter(AppCompatActivity appCompatActivity, SplashAnimationHomeFragment splashAnimationHomeFragment, List<SplashAnimationListBean> splashAnimationListBeanList) {
        SplashAnimationListAdapter splashAnimationListAdapter = new SplashAnimationListAdapter(appCompatActivity, 2, 144);
        splashAnimationListAdapter.setSplashAnimationData(splashAnimationListBeanList);
        splashAnimationListAdapter.setOnRecyclerViewOnItemClickFullScreenListener(new OnRecyclerViewOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T t) {
                SplashAnimationListBean splashAnimationListBean = (SplashAnimationListBean) t;
                splashAnimationHomeFragment.start(SplashAnimationDetailFragment.newInstance(splashAnimationListBean.getResName()));
            }
        });
        return splashAnimationListAdapter;
    }

    /**
     * 使用默认闪屏动画
     *
     * @param appCompatActivity 活动
     */
    public void useDefaultSplashAnimation(AppCompatActivity appCompatActivity) {
        if (TextUtils.equals(MmkvKit.defaultMmkv().decodeString(PoolConstant.SPLASH_$_ANIMATION), Objects.requireNonNull(BasePoolApp.getConfigMap().get(1)).get(0))) {
            BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogOne(BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.alreadyIsDefaultAnimation), 0, () -> BocDialogKit.getInstance(appCompatActivity).end(), null);
        } else {
            MmkvKit.defaultMmkv().encode(PoolConstant.SPLASH_$_ANIMATION, Objects.requireNonNull(BasePoolApp.getConfigMap().get(1)).get(0));
            BocDialogKit.getInstance(appCompatActivity).bocLottieCommonDialogOne(BocLottieDialogEnum.SUCCESS_ONE, appCompatActivity.getString(R.string.restoreAnimationSuccessful), 0, () -> BocDialogKit.getInstance(appCompatActivity).end(), null);
        }
    }
}