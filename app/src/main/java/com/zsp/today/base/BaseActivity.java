package com.zsp.today.base;

import android.os.Bundle;
import android.view.KeyEvent;

import com.zsp.today.value.Magic;

import pool.base.BasePoolActivity;
import util.activity.ActivitySuperviseManager;

/**
 * Created on 2021/3/12
 *
 * @author zsp
 * @desc BaseActivity
 * 启应用后 {@link com.zsp.today.MainActivity} 直存至应用杀死时销毁。
 */
public abstract class BaseActivity extends BasePoolActivity {
    /**
     * 加载视图
     *
     * @param savedInstanceState 状态保存
     * @param layoutResId        布局资源 ID
     */
    @Override
    protected void initContentView(Bundle savedInstanceState, int layoutResId) {
        setContentView(layoutResId);
    }

    /**
     * onKeyUp
     * <p>
     * {@link com.zsp.today.MainActivity} 于 {@link pool.base.BasePoolFragment#onCreate(Bundle)} 或 {@link pool.base.BasePoolFragment#onBackPressedSupport} 处理。
     * SplashActivity 与 {@link pool.login.LoginActivity} 于该法处理。
     *
     * @param keyCode 键码值
     * @param event   键事件
     * @return boolean
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 闪屏页（点击事件消耗，不向下分发，点击无反应）
            // 该分支无 break(详看 break、continue、return 区别)
            if (Magic.STRING_SPLASH_ACTIVITY.equals(ActivitySuperviseManager.getInstance().getCurrentRunningActivityName(this))) {
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
