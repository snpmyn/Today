package pool.module.splash;

import android.view.KeyEvent;

import com.airbnb.lottie.LottieAnimationView;
import com.zsp.core.R;

import pool.base.BasePoolActivity;
import pool.module.splash.kit.SplashActivityKit;
import pool.module.splash.kit.SplashTwoActivityKit;
import util.screen.ScreenUtils;

/**
 * @desc: 闪屏二页
 * @author: zsp
 * @date: 2022/4/2 11:03 上午
 */
public class SplashTwoActivity extends BasePoolActivity {
    private LottieAnimationView splashTwoActivityLav;
    /**
     * 闪屏页配套元件
     */
    private SplashActivityKit splashActivityKit;
    /**
     * 闪屏二页配套元件
     */
    private SplashTwoActivityKit splashTwoActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_splash_two;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        ScreenUtils.hideNavigationWithoutCanShowInScroll(this);
        splashTwoActivityLav = findViewById(R.id.splashTwoActivityLav);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 闪屏页配套元件
        splashActivityKit = new SplashActivityKit();
        // 闪屏二页配套元件
        splashTwoActivityKit = new SplashTwoActivityKit();
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
        splashTwoActivityKit.execute(this, splashTwoActivityLav, splashActivityKit);
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