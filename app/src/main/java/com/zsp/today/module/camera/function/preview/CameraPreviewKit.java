package com.zsp.today.module.camera.function.preview;

import android.util.Size;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Created on 2026/9/15.
 *
 * @author 郑少鹏
 * @desc 相机预览配套原件
 */
public class CameraPreviewKit {
    /**
     * 应用预览配置
     *
     * @param previewView 预览视图
     */
    public void applyPreviewConfig(@NonNull PreviewView previewView) {
        // ==================================================================================================================================================
        // A. 渲染模式 - COMPATIBLE
        //    采用 TextureView 模式以提升复杂 UI (如圆角 CardView 裁剪、Overlay 覆盖物) 兼容性
        // B. 缩放策略 - FILL_CENTER
        //    - FIT_CENTER 缺陷
        //    TextureView 渲染层在进行矩阵变换时，因 Android 视图树测量 (Measure Pass) 与 Sensor 帧率同步的亚像素四舍五入偏差，极其容易在 View 边缘产生 1 ~ 2px 的补齐黑边 / 黑缝。
        //    - FILL_CENTER 优势
        //    等比放大画面以完全覆盖 View。在外层 CardView 已锁定图像原生宽高比的前提下，以极微小裁切 (容错) 抵消亚像素黑边。
        // ==================================================================================================================================================
        previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
    }

    /**
     * 更新预览视图容器宽高比
     *
     * @param previewViewContainerView 预览视图容器
     * @param resolution               分辨率
     */
    public void updatePreviewContainerRatio(@NonNull View previewViewContainerView, @Nullable Size resolution) {
        if (resolution == null) {
            return;
        }
        ViewGroup.LayoutParams params = previewViewContainerView.getLayoutParams();
        if (params instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) params;
            int width = resolution.getWidth();
            int height = resolution.getHeight();
            layoutParams.dimensionRatio = "H," + width + ":" + height;
            previewViewContainerView.setLayoutParams(layoutParams);
        }
    }
}