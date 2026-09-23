package com.zsp.today.module.camera.function.config;

import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.zsp.today.module.camera.function.value.EnhanceMode;

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
     * 旋转角度
     */
    private final int rotation;
    /**
     * 图像增强模式
     */
    private final EnhanceMode enhanceMode;

    /**
     * constructor
     *
     * @param builder 构建器
     */
    @Contract(pure = true)
    private CameraConfig(@NonNull Builder builder) {
        // 相机 ID
        this.cameraId = builder.cameraId;
        // 分辨率
        this.resolution = builder.resolution;
        // 旋转角度
        this.rotation = builder.rotation;
        // 图像增强模式
        this.enhanceMode = builder.enhanceMode;
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
     * 获取旋转角度
     *
     * @return 旋转角度
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * 获取图像增强模式
     *
     * @return 图像增强模式
     */
    public EnhanceMode getEnhanceMode() {
        return enhanceMode;
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
         * 旋转角度
         * <p>
         * 默认 {@link Surface#ROTATION_0}
         */
        private int rotation = Surface.ROTATION_0;
        /**
         * 图像增强模式
         * <p>
         * 默认 {@link EnhanceMode#NONE}
         */
        private EnhanceMode enhanceMode = EnhanceMode.NONE;

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
         * 设置旋转角度
         *
         * @param rotation 旋转角度
         *                 {@link Surface#ROTATION_0} 屏幕竖屏正向
         *                 {@link Surface#ROTATION_90} 屏幕顺旋九十
         *                 {@link Surface#ROTATION_180} 屏幕顺旋一八
         *                 {@link Surface#ROTATION_270} 屏幕顺旋二七
         * @return 构建器实例
         */
        public Builder setRotation(int rotation) {
            this.rotation = rotation;
            return this;
        }

        /**
         * 设置图像增强模式
         *
         * @param enhanceMode 图像增强模式
         * @return 构建器实例
         */
        public Builder setEnhanceMode(@NonNull EnhanceMode enhanceMode) {
            this.enhanceMode = enhanceMode;
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