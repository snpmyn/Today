package com.zsp.today.module.camera.function.config;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;

/**
 * Created on 2026/9/22.
 *
 * @author 郑少鹏
 * @desc CameraX 配置配套原件
 * <p>
 * 提供全局 CameraX 初始配置
 * 解决在外接 UVC 高拍仪、单摄像头或非标 Android 工控设备上，CameraX 默认初始化流程因找不到标准前置 / 后置镜头而频繁抛出 CameraValidator 警告日志问题。
 */
public class CameraXConfigKit {
    /**
     * constructor
     * <p>
     * 私有构造函数 + 防止实例化
     */
    private CameraXConfigKit() {

    }

    /**
     * 获取默认 CameraX 配置
     * <p>
     * 1. 使用 Camera2Config 作为底层核心驱动实现
     * 2. 通过 setAvailableCamerasLimiter 允许所有摄像头，解除默认前后置镜头强校验，并提升日志过滤级别至 Log.INFO 级别，彻底屏蔽 CameraValidator 警告日志。
     * 3. 完美兼容标准手机 (前后置) 与外接 UVC 高拍仪 (LENS_FACING_EXTERNAL / UNKNOWN)
     *
     * @return 默认 CameraX 配置 [供 Application 实现 CameraXConfig.Provider 时调]
     */
    @NonNull
    public static CameraXConfig getDefaultCameraXConfig() {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
                // 指定相机选择器匹配规则为接受所有相机类型
                // 跳过对前后置镜头的强行校验
                .setAvailableCamerasLimiter(new CameraSelector.Builder().build())
                // 提高日志过滤级别
                // 屏蔽非必要的内部异常 / 警告 (如 Camera LENS_FACING_BACK / FRONT verification failed)
                .setMinimumLoggingLevel(Log.INFO).build();
    }
}