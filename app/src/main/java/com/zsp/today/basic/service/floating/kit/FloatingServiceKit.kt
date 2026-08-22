package com.zsp.today.basic.service.floating.kit

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.zsp.today.basic.service.floating.FloatingService
import util.app.AppListener
import util.overlay.OverlayPermissionKit

/**
 * Created on 2026/6/10.
 * @author 郑少鹏
 * @desc 悬浮服务配套原件
 */
class FloatingServiceKit {
    companion object {
        /**
         * 初始化悬浮服务
         *
         * @param appCompatActivity 活动
         */
        fun initFloatService(appCompatActivity: AppCompatActivity) {
            if (OverlayPermissionKit.requestOverlayPermission(appCompatActivity)) {
                // 1. 先确保服务已经启动（如果是首次启动）
                startFloatService(appCompatActivity)
                AppListener.getInstance().registerCallback { areForeground: Boolean ->
                    // 2. 根据前后台状态控制显示或隐藏
                    if (areForeground) {
                        // App 回到前台
                        // 显示悬浮视图
                        val showIntent = Intent(appCompatActivity, FloatingService::class.java)
                        showIntent.setAction(FloatingService.ACTION_SHOW_FLOATING)
                        appCompatActivity.startService(showIntent)
                    } else {
                        // App 退到后台
                        // 隐藏悬浮视图
                        val hideIntent = Intent(appCompatActivity, FloatingService::class.java)
                        hideIntent.setAction(FloatingService.ACTION_HIDE_FLOATING)
                        appCompatActivity.startService(hideIntent)
                    }
                }
            }
        }

        /**
         * 开始悬浮服务
         *
         * @param appCompatActivity 活动
         */
        private fun startFloatService(appCompatActivity: AppCompatActivity) {
            val intent = Intent(appCompatActivity, FloatingService::class.java)
            intent.setAction(FloatingService.ACTION_SHOW_FLOATING)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appCompatActivity.startForegroundService(intent)
            } else {
                appCompatActivity.startService(intent)
            }
        }
    }
}