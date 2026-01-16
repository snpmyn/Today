package com.zsp.today.module.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.module.mine.kit.MineFragmentKit;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolFragment;
import widget.appbarlayout.listener.BaseAppBarLayoutStateChangeListener;
import widget.toast.ToastKit;

/**
 * Created on 2020/12/17
 *
 * @author zsp
 * @desc 我的碎片
 */
public class MineFragment extends BasePoolFragment implements View.OnClickListener {
    private LottieAnimationView mineFragmentLavHeadPortrait;
    private TextView mineFragmentTvNickName;
    private MaterialToolbar mineFragmentMt;
    private AppBarLayout mineFragmentAbl;
    private RecyclerView mineFragmentRv;
    /**
     * 我的碎片配套元件
     */
    private MineFragmentKit mineFragmentKit;

    public static @NotNull MineFragment newInstance() {
        Bundle bundle = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 获取布局 ID
     *
     * @return 布局 ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    /**
     * 初始视图
     *
     * @param view 视图
     */
    @Override
    protected void initView(@NonNull View view) {
        mineFragmentLavHeadPortrait = view.findViewById(R.id.mineFragmentLavHeadPortrait);
        mineFragmentTvNickName = view.findViewById(R.id.mineFragmentTvNickName);
        mineFragmentMt = view.findViewById(R.id.mineFragmentMt);
        mineFragmentAbl = view.findViewById(R.id.mineFragmentAbl);
        mineFragmentRv = view.findViewById(R.id.mineFragmentRv);
    }

    /**
     * 头次可见
     * <p>
     * 只调一次
     * <p>
     * 网络请求
     * 耗时任务
     * 初始数据
     */
    @Override
    protected void onFragmentFirstVisible() {
        initConfiguration();
        setListener();
        startLogic();
    }

    private void initConfiguration() {
        mineFragmentKit = new MineFragmentKit();
    }

    private void setListener() {
        mineFragmentAbl.addOnOffsetChangedListener(new BaseAppBarLayoutStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    // 折叠
                    mineFragmentKit.setNickName(baseLazyFragmentAppCompatActivity, mineFragmentTvNickName, mineFragmentMt, false);
                } else {
                    // 中间
                    mineFragmentMt.setTitle("");
                }
            }
        });
        mineFragmentLavHeadPortrait.setOnClickListener(this);
    }

    private void startLogic() {
        // 设置昵称
        mineFragmentKit.setNickName(baseLazyFragmentAppCompatActivity, mineFragmentTvNickName, mineFragmentMt, true);
        // 展示
        mineFragmentKit.display(baseLazyFragmentAppCompatActivity, mineFragmentRv);
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.mineFragmentLavHeadPortrait) {
            ToastKit.showShort(getString(R.string.app_name));
        }
    }
}