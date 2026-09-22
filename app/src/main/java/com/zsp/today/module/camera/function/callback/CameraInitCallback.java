package com.zsp.today.module.camera.function.callback;

/**
 * Created on 2026/9/22.
 *
 * @author 郑少鹏
 * @desc 相机初始回调
 */
public interface CameraInitCallback {
    /**
     * 相机初始成功
     */
    void onCameraInitSuccess();

    /**
     * 相机初始错误
     *
     * @param throwable 异常
     */
    void onCameraInitError(Throwable throwable);
}