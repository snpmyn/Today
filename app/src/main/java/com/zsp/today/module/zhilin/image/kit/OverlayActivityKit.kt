package com.zsp.today.module.zhilin.image.kit

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import widget.image.ImageViewerOverlay
import widget.toast.showToast

/**
 * Created on 2026/6/11.
 * @author 郑少鹏
 * @desc 浮层页配套原件
 */
class OverlayActivityKit {
    /**
     * 显示
     */
    fun show(appCompatActivity: AppCompatActivity) {
        val imageViewerOverlay = ImageViewerOverlay(appCompatActivity)
        imageViewerOverlay.show(
            appCompatActivity.window.decorView as ViewGroup,
            "https://gips0.baidu.com/it/u=1690853528,2506870245&fm=3028&app=3028&f=JPEG&fmt=auto?w=1024&h=1024"
        )
        imageViewerOverlay.setOnCloseListener { "关闭".showToast() }
    }
}