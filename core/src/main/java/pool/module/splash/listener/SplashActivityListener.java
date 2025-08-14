package pool.module.splash.listener;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created on 2021/9/16
 *
 * @author zsp
 * @desc 闪屏页监听
 */
public interface SplashActivityListener {
    /**
     * 分发
     *
     * @param appCompatActivity 活动
     */
    void distribute(AppCompatActivity appCompatActivity);
}