package com.zsp.today.module.mine.bean;

/**
 * Created on 2022/5/7
 *
 * @author zsp
 * @desc 闪屏动画列表
 */
public class SplashAnimationListBean {
    /**
     * 资源名
     */
    private final String resName;

    /**
     * constructor
     *
     * @param resName 资源名
     */
    public SplashAnimationListBean(String resName) {
        this.resName = resName;
    }

    public String getResName() {
        return resName;
    }
}