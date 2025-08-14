package pool.module.splash;

import android.animation.Animator;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zsp.core.R;

import pool.base.BasePoolActivity;
import pool.module.splash.kit.SplashActivityKit;
import util.animation.AnimationManager;
import util.screen.ScreenUtils;

/**
 * @desc: 闪屏页
 * @author: zsp
 * @date: 2021/9/16 3:40 下午
 */
public class SplashActivity extends BasePoolActivity {
    /**
     * 控件
     */
    TextView splashActivityTv;
    /**
     * 闪屏页配套元件
     */
    private SplashActivityKit splashActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_splash;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        ScreenUtils.hideNavigationWithoutCanShowInScroll(this);
        splashActivityTv = findViewById(R.id.splashActivityTv);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        splashActivityKit = new SplashActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {

    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        AnimationManager.xyScaleAlphaShow(splashActivityTv, 1000, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                new Handler(getMainLooper()).postDelayed(() -> splashActivityKit.execute(SplashActivity.this), 500);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }

    /**
     * onKeyUp
     *
     * @param keyCode 键码值
     * @param event   键事件
     * @return boolean
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 闪屏页（点击事件消耗，不向下分发，点击无反应）
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 处理 PreviewWindow 背景避图直占内存（过渡绘制）
        // 原于 initContentView 处理。过早处理致闪屏页执行过程偶现背景无效，故于闪屏页销毁时处理。
        getWindow().setBackgroundDrawable(null);
    }
}