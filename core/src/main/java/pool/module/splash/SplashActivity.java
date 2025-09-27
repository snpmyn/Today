package pool.module.splash;

import android.view.KeyEvent;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.zsp.core.R;

import pool.base.BasePoolActivity;
import pool.module.splash.kit.SplashActivityKit;
import util.screen.ScreenUtils;

/**
 * @desc: 闪屏页
 * @author: zsp
 * @date: 2022/4/2 11:03 上午
 */
public class SplashActivity extends BasePoolActivity {
    private LottieAnimationView splashActivityLav;
    private TextView splashActivityTv;
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
        // LottieAnimationView
        splashActivityLav = findViewById(R.id.splashActivityLav);
        // TextView
        splashActivityTv = findViewById(R.id.splashActivityTv);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 闪屏页配套元件
        splashActivityKit = new SplashActivityKit();
        // 闪屏页配套元件
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
        splashActivityKit.execute(this, splashActivityLav, splashActivityTv);
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