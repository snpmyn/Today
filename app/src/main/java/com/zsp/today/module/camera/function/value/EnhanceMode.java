package com.zsp.today.module.camera.function.value;

/**
 * @decs: 图像增强模式
 * @author: 郑少鹏
 * @date: 2026/9/18 15:30
 * @version: v 1.0
 */
public enum EnhanceMode {
    /**
     * 原始模式
     * <p>
     * 不做任何处理增强
     * 保持底层原生输出
     */
    NONE,
    /**
     * 文档模式
     * <p>
     * 拉伸对比度 + 白平衡优化
     * 提升黑白 / 彩色文档文字清晰度与背景平整度
     */
    DOCUMENT,
    /**
     * 锐化模式
     * <p>
     * 应用边缘检测 + 拉普拉斯锐化矩阵
     * 提升字体边缘线条清晰度
     */
    USM_SHARPEN
}