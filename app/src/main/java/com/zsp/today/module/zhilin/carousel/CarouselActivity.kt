package com.zsp.today.module.zhilin.carousel

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.zsp.today.databinding.ActivityCarouselBinding
import com.zsp.today.module.zhilin.carousel.kit.CarouselActivityKit
import pool.base.BasePoolActivity
import widget.transition.kit.TransitionKit

/**
 * Created on 2026/6/12.
 * @author 郑少鹏
 * @desc 轮播页
 */
class CarouselActivity : BasePoolActivity() {
    private lateinit var activityCarouselBinding: ActivityCarouselBinding
    private lateinit var carouselActivityKit: CarouselActivityKit

    override fun onCreate(savedInstanceState: Bundle?) {
        TransitionKit.getInstance().startPageSetting(this)
        super.onCreate(savedInstanceState)
    }

    /**
     * ViewBinding
     *
     * Java 动态绑定
     * Java 运行时多态
     * Java 动态分派机制
     *
     * 如果子类重写 viewBinding()
     * 那么 onCreate() 中调用时会优先执行子类的方法
     *
     * @return ViewBinding
     */
    override fun viewBinding(): ViewBinding {
        activityCarouselBinding = ActivityCarouselBinding.inflate(layoutInflater)
        return activityCarouselBinding
    }

    /**
     * 初始控件
     */
    override fun stepUi() {

    }

    /**
     * 初始配置
     */
    override fun initConfiguration() {
        carouselActivityKit = CarouselActivityKit()
    }

    /**
     * 设置监听
     */
    override fun setListener() {

    }

    /**
     * 开始逻辑
     */
    override fun startLogic() {
        carouselActivityKit.execute(activityCarouselBinding)
    }

    override fun onResume() {
        super.onResume()
        activityCarouselBinding.carouselActivityCv.resumeAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        activityCarouselBinding.carouselActivityCv.pauseAutoScroll()
    }
}