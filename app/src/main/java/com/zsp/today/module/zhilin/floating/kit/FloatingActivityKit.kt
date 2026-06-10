package com.zsp.today.module.zhilin.floating.kit

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zsp.today.basic.service.floating.FloatingService
import com.zsp.today.basic.service.floating.kit.FloatingServiceKit
import widget.toast.showToast

/**
 * Created on 2026/6/10.
 * @author 郑少鹏
 * @desc 悬浮页配套原件
 */
class FloatingActivityKit {
    /**
     * 执行
     *
     * @param appCompatActivity 活动
     */
    fun execute(appCompatActivity: AppCompatActivity) {
        FloatingServiceKit.initFloatService(appCompatActivity)
        FloatingService.setOnFloatingClickListener(object :
            FloatingService.OnFloatingClickListener {
            /**
             * 单击
             * @param view 视图
             */
            override fun onSingleClick(view: View?) {
                "单击".showToast()
            }

            /**
             * 双击
             * @param view 视图
             */
            override fun onDoubleClick(view: View?) {
                "双击".showToast()
            }
        })
    }
}