package com.zsp.today.module.mine;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.module.mine.kit.SplashAnimationHomeActivityKit;

import pool.base.BasePoolActivity;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;

/**
 * Created on 2025/12/21.
 *
 * @author 郑少鹏
 * @desc 闪屏动画主页
 */
public class SplashAnimationHomeActivity extends BasePoolActivity {
    private MaterialToolbar splashAnimationHomeActivityMt;
    private RecyclerView splashAnimationHomeActivityRv;
    /**
     * 状态管理器
     */
    private StatusManager statusManager;
    /**
     * 闪屏动画主页配套元件
     */
    private SplashAnimationHomeActivityKit splashAnimationHomeActivityKit;

    /**
     * 布局资源 ID
     * <p>
     * Java 动态绑定
     * Java 运行时多态
     * Java 动态分派机制
     * <p>
     * 如果子类重写 layoutResId()
     * 那么 onCreate() 中调用时会优先执行子类的方法
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_splash_animation_home;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        splashAnimationHomeActivityMt = findViewById(R.id.splashAnimationHomeActivityMt);
        splashAnimationHomeActivityRv = findViewById(R.id.splashAnimationHomeActivityRv);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 状态管理器
        statusManager = StatusManagerKit.generate(this, splashAnimationHomeActivityRv, new StatusManagerKit.StatusManagerKitListener() {
            @Override
            public void noNetwork() {

            }

            @Override
            public void connectFail() {

            }

            @Override
            public void loadFail() {

            }
        });
        // 闪屏动画主页配套元件
        splashAnimationHomeActivityKit = new SplashAnimationHomeActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        splashAnimationHomeActivityMt.setOnMenuItemClickListener(item -> {
            splashAnimationHomeActivityKit.useDefaultSplashAnimation(SplashAnimationHomeActivity.this);
            return true;
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        splashAnimationHomeActivityKit.displaySplashAnimation(this, splashAnimationHomeActivityRv, statusManager);
    }
}