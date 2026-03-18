package com.zsp.today.module.zhilin.history

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.zsp.today.databinding.ActivityHistoryBinding
import com.zsp.today.module.zhilin.history.kit.HistoryActivityKit
import pool.base.BasePoolActivity
import widget.floatingactionbutton.kit.DraggableFloatingActionButtonKit
import widget.transition.kit.TransitionKit

/**
 * @decs: 历史页
 * @author: 郑少鹏
 * @date: 2026/2/27 19:32
 * @version: v 1.0
 */
class HistoryActivity : BasePoolActivity() {
    private lateinit var activityHistoryBinding: ActivityHistoryBinding
    private lateinit var historyActivityKit: HistoryActivityKit
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
        activityHistoryBinding = ActivityHistoryBinding.inflate(layoutInflater)
        return activityHistoryBinding;
    }

    /**
     * 初始控件
     */
    override fun stepUi() {
        DraggableFloatingActionButtonKit.execute(activityHistoryBinding.historyActivityDfab)
    }

    /**
     * 初始配置
     */
    override fun initConfiguration() {
        historyActivityKit = HistoryActivityKit()
    }

    /**
     * 设置监听
     */
    override fun setListener() {
        activityHistoryBinding.historyActivityMt.setOnMenuItemClickListener {
            historyActivityKit.showHistoryPointList(this, activityHistoryBinding.historyActivityRv)
            true
        }
        activityHistoryBinding.historyActivityDfab.setOnClickListener {
            historyActivityKit.showHistoryPointList(this, activityHistoryBinding.historyActivityRv)
        }
    }

    /**
     * 开始逻辑
     */
    override fun startLogic() {
        historyActivityKit.carousel(activityHistoryBinding.historyActivityRv)
    }
}