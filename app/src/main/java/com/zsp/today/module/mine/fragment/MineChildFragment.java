package com.zsp.today.module.mine.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.module.mine.kit.MineChildFragmentKit;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolFragment;
import widget.appbarlayout.listener.BaseAppBarLayoutStateChangeListener;
import widget.toast.ToastKit;

/**
 * Created on 2020/12/17
 *
 * @author zsp
 * @desc 我的子碎片
 */
public class MineChildFragment extends BasePoolFragment implements View.OnClickListener {
    private LottieAnimationView mineChildFragmentLavHeadPortrait;
    private TextView mineChildFragmentTvNickName;
    private MaterialToolbar mineChildFragmentMt;
    private AppBarLayout mineChildFragmentAbl;
    private RecyclerView mineChildFragmentRv;
    /**
     * 我的子碎片配套元件
     */
    private MineChildFragmentKit mineChildFragmentKit;

    public static @NotNull MineChildFragment newInstance() {
        Bundle bundle = new Bundle();
        MineChildFragment fragment = new MineChildFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.fragment_mine_child;
    }

    /**
     * 第一 Fragment 否
     *
     * @return 第一 Fragment 否
     */
    @Override
    protected boolean areFirstFragment() {
        return false;
    }

    /**
     * EventBus 注册
     * <p>
     * onDestroyView 反注册。
     */
    @Override
    protected void eventBusRegister() {

    }

    /**
     * 初始控件
     * <p>
     * 此处仅设 Toolbar 标题、返回箭头等轻量 UI 操作。
     *
     * @param view 视图
     */
    @Override
    protected void stepUi(@NonNull View view) {
        mineChildFragmentLavHeadPortrait = view.findViewById(R.id.mineChildFragmentLavHeadPortrait);
        mineChildFragmentTvNickName = view.findViewById(R.id.mineChildFragmentTvNickName);
        mineChildFragmentMt = view.findViewById(R.id.mineChildFragmentMt);
        mineChildFragmentAbl = view.findViewById(R.id.mineChildFragmentAbl);
        mineChildFragmentRv = view.findViewById(R.id.mineChildFragmentRv);
    }

    /**
     * Fragment 对用户可见时调
     */
    @Override
    protected void visibleToUser() {

    }

    /**
     * 开始加载
     * <p>
     * 某些场景需懒加载，如 FragmentAdapter 懒加载、同级 Fragment 切换懒加载。
     * 库自 0.8 提供 onLazyInitView(Bundle saveInstanceState) 使用。
     */
    @Override
    protected void startLoadOnLazyInitView() {

    }

    /**
     * 开始加载
     * <p>
     * 此处设 Listener、各 Adapter、请求数据等。
     * onDestroyView 释放。
     */
    @Override
    protected void startLoadOnEnterAnimationEnd() {
        initConfiguration();
        setListener();
        startLogic();
    }

    /**
     * Fragment 对用户不可见时调
     */
    @Override
    protected void invisibleToUser() {

    }

    /**
     * EventBus 反注册
     * <p>
     * onCreateView 注册。
     */
    @Override
    protected void eventBusUnregister() {

    }

    private void initConfiguration() {
        mineChildFragmentKit = new MineChildFragmentKit();
    }

    private void setListener() {
        mineChildFragmentAbl.addOnOffsetChangedListener(new BaseAppBarLayoutStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    // 折叠
                    mineChildFragmentKit.setNickName(mineChildFragmentTvNickName, mineChildFragmentMt, false);
                } else {
                    // 中间
                    mineChildFragmentMt.setTitle("");
                }
            }
        });
        mineChildFragmentLavHeadPortrait.setOnClickListener(this);
    }

    private void startLogic() {
        // 设置昵称
        mineChildFragmentKit.setNickName(mineChildFragmentTvNickName, mineChildFragmentMt, true);
        // 展示
        mineChildFragmentKit.display(fragmentationSupportActivity, this, mineChildFragmentRv);
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.mineChildFragmentLavHeadPortrait) {
            ToastKit.showShort(getString(R.string.app_name));
        }
    }
}