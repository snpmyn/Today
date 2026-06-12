package com.zsp.today.application;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on 2026/6/3.
 *
 * @author 郑少鹏
 * @desc 轮播布局管理器（已支持一屏多子项多阶梯级联缩放错落有致）
 */
public class CarouselLayoutManager extends LinearLayoutManager {
    private float minScale = 0.78f;
    private float minAlpha = 0.6f;
    private boolean enableAlpha = true;
    private boolean enableScale = true;
    private int itemWidth = 0;

    public CarouselLayoutManager(Context context) {
        super(context, HORIZONTAL, false);
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        applyItemTransform();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int result = super.scrollHorizontallyBy(dx, recycler, state);
        applyItemTransform();
        return result;
    }

    /**
     * 应用项目转换核心动画
     */
    private void applyItemTransform() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        float recyclerCenterX = (getWidth() / 2f);
        int currentItemWidth = itemWidth > 0 ? itemWidth : getChildAt(0).getWidth();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) {
                continue;
            }
            float childCenterX = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2f;
            float distance = Math.abs(recyclerCenterX - childCenterX);

            // ==================== 核心修正：5条目阶梯级联缩放计算 ====================
            // 以半个屏幕宽度做为最大变化基准线，而非单个卡片宽度。
            // 这样 5 个卡片由于距离中心位置不同，算出的 fraction 分别是: 0 -> 0.4 -> 0.8...
            float fraction = distance / recyclerCenterX;
            fraction = Math.min(fraction, 1f);

            // 1. 缩放阶梯计算
            float scale = (1f - (1f - minScale) * fraction);
            if (enableScale) {
                child.setScaleX(scale);
                child.setScaleY(scale);
            }

            // 2. 透明度阶梯计算
            if (enableAlpha) {
                float alpha = (1f - (1f - minAlpha) * fraction);
                child.setAlpha(alpha);
            }

            // 3. Z轴层级（越近越靠上，永远不被边缘遮挡）
            child.setTranslationZ((1.0f - fraction) * 100);

            // 4. 精准多阶梯间距修正算法：
            // 依据每一个卡片各自独有的动态缩放比，计算应该内聚贴合的像素值，创造完美的紧凑错落感
            if (enableScale) {
                float gapTranslation = currentItemWidth * (1.0f - scale) * 0.5f;
                if (childCenterX < recyclerCenterX) {
                    // 左侧所有卡片按比例齐刷刷向右侧靠拢吸附
                    child.setTranslationX(gapTranslation);
                } else if (childCenterX > recyclerCenterX) {
                    // 右侧所有卡片按比例齐刷刷向左侧靠拢吸附
                    child.setTranslationX(-gapTranslation);
                } else {
                    child.setTranslationX(0);
                }
            }
        }
    }

    public View findCenterView() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return null;
        }
        float centerX = getWidth() / 2f;
        View centerView = null;
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) {
                continue;
            }
            float childCenter = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2f;
            float distance = Math.abs(centerX - childCenter);
            if (distance < minDistance) {
                minDistance = distance;
                centerView = child;
            }
        }
        return centerView;
    }

    public int getCenterPosition() {
        View centerView = findCenterView();
        if (centerView == null) {
            return RecyclerView.NO_POSITION;
        }
        return getPosition(centerView);
    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
        requestLayout();
    }

    public float getMinAlpha() {
        return minAlpha;
    }

    public void setMinAlpha(float minAlpha) {
        this.minAlpha = minAlpha;
        requestLayout();
    }

    public boolean isEnableAlpha() {
        return enableAlpha;
    }

    public void setEnableAlpha(boolean enableAlpha) {
        this.enableAlpha = enableAlpha;
        requestLayout();
    }

    public boolean isEnableScale() {
        return enableScale;
    }

    public void setEnableScale(boolean enableScale) {
        this.enableScale = enableScale;
        requestLayout();
    }
}