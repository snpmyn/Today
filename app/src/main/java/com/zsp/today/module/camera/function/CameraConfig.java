package com.zsp.today.module.camera.function;

import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * @decs: 相机配置
 * @author: 郑少鹏
 * @date: 2026/9/18 15:28
 * @version: v 1.0
 */
public class CameraConfig {
    /**
     * 相机 ID
     */
    private final String cameraId;
    /**
     * 分辨率
     */
    private final Size resolution;
    /**
     * 目标旋转角度
     */
    private final int targetRotation;

    /**
     * constructor
     *
     * @param builder 构建器
     */
    @Contract(pure = true)
    private CameraConfig(@NonNull Builder builder) {
        this.cameraId = builder.cameraId;
        this.resolution = builder.resolution;
        this.targetRotation = builder.targetRotation;
    }

    /**
     * 获取相机 ID
     *
     * @return 相机 ID
     */
    public String getCameraId() {
        return cameraId;
    }

    /**
     * 获取分辨率
     *
     * @return 分辨率
     */
    public Size getResolution() {
        return resolution;
    }

    /**
     * 获取目标旋转角度
     *
     * @return 目标旋转角度
     */
    public int getTargetRotation() {
        return targetRotation;
    }

    /**
     * 构建器
     */
    public static class Builder {
        /**
         * 相机 ID
         */
        private String cameraId = null;
        /**
         * 分辨率
         */
        private Size resolution = null;
        /**
         * 目标旋转角度
         * <p>
         * 默认 Surface.ROTATION_90
         */
        private int targetRotation = Surface.ROTATION_90;

        /**
         * 设置相机 ID
         *
         * @param cameraId 相机 ID
         * @return 构建器实例
         */
        public Builder setCameraId(String cameraId) {
            this.cameraId = cameraId;
            return this;
        }

        /**
         * 设置分辨率
         *
         * @param resolution 分辨率
         * @return 构建器实例
         */
        public Builder setResolution(Size resolution) {
            this.resolution = resolution;
            return this;
        }

        /**
         * 设置目标旋转角度
         *
         * @param targetRotation 目标旋转角度
         *                       [Surface.ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270]
         * @return 构建器实例
         */
        public Builder setTargetRotation(int targetRotation) {
            this.targetRotation = targetRotation;
            return this;
        }

        /**
         * 构建
         *
         * @return 相机配置实例
         */
        public CameraConfig build() {
            return new CameraConfig(this);
        }
    }
}