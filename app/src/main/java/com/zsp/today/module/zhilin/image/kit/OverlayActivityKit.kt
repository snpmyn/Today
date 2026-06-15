package com.zsp.today.module.zhilin.image.kit

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import util.screen.ScreenUtils
import widget.image.ImageViewerOverlay
import widget.toast.showToast

/**
 * Created on 2026/6/11.
 * @author 郑少鹏
 * @desc 浮层页配套原件
 */
class OverlayActivityKit {
    /**
     * 竖屏显示
     */
    fun portraitShow(appCompatActivity: AppCompatActivity) {
        ScreenUtils.switchPortrait(appCompatActivity)
        val imageViewerOverlay = ImageViewerOverlay(appCompatActivity)
        imageViewerOverlay.show(
            appCompatActivity.window.decorView as ViewGroup,
            "https://gips0.baidu.com/it/u=1690853528,2506870245&fm=3028&app=3028&f=JPEG&fmt=auto?w=1024&h=1024"
        )
        imageViewerOverlay.setOnCloseListener { "关闭".showToast() }
    }

    /**
     * 横屏显示
     */
    fun landscapeShow(appCompatActivity: AppCompatActivity) {
        ScreenUtils.switchLandscape(appCompatActivity)
        val imageViewerOverlay = ImageViewerOverlay(appCompatActivity)
        imageViewerOverlay.show(
            appCompatActivity.window.decorView as ViewGroup,
            "https://gips0.baidu.com/it/u=1690853528,2506870245&fm=3028&app=3028&f=JPEG&fmt=auto?w=1024&h=1024"
        )
        imageViewerOverlay.setOnCloseListener { "关闭".showToast() }
    }
}