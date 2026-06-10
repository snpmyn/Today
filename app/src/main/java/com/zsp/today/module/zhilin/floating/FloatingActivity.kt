package com.zsp.today.module.zhilin.floating

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.zsp.today.databinding.ActivityFloatingBinding
import com.zsp.today.module.zhilin.floating.kit.FloatingActivityKit
import pool.base.BasePoolActivity
import widget.transition.kit.TransitionKit

/**
 * Created on 2026/6/10.
 * @author 郑少鹏
 * @desc 悬浮页
 */
class FloatingActivity : BasePoolActivity() {
    private lateinit var activityFloatingBinding: ActivityFloatingBinding
    private lateinit var floatingActivityKit: FloatingActivityKit

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
        activityFloatingBinding = ActivityFloatingBinding.inflate(layoutInflater)
        return activityFloatingBinding
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
        floatingActivityKit = FloatingActivityKit()
    }

    /**
     * 设置监听
     */
    override fun setListener() {
        activityFloatingBinding.floatingActivityMb.setOnClickListener {
            floatingActivityKit.execute(this)
        }
    }

    /**
     * 开始逻辑
     */
    override fun startLogic() {

    }
}