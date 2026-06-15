package com.zsp.today.module.zhilin.image

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.zsp.today.databinding.ActivityOverlayBinding
import com.zsp.today.module.zhilin.image.kit.OverlayActivityKit
import pool.base.BasePoolActivity
import widget.transition.kit.TransitionKit

/**
 * Created on 2026/6/11.
 * @author 郑少鹏
 * @desc 浮层页
 */
class OverlayActivity : BasePoolActivity() {
    private lateinit var activityOverlayBinding: ActivityOverlayBinding
    private lateinit var overlayActivityKit: OverlayActivityKit

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
        activityOverlayBinding = ActivityOverlayBinding.inflate(layoutInflater)
        return activityOverlayBinding
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
        overlayActivityKit = OverlayActivityKit()
    }

    /**
     * 设置监听
     */
    override fun setListener() {
        activityOverlayBinding.overlayActivityMbPortrait.setOnClickListener {
            overlayActivityKit.portraitShow(
                this
            )
        }
        activityOverlayBinding.overlayActivityMbLandscape.setOnClickListener {
            overlayActivityKit.landscapeShow(
                this
            )
        }
    }

    /**
     * 开始逻辑
     */
    override fun startLogic() {

    }
}