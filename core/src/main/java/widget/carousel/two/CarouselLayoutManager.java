package widget.carousel.two;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import timber.log.Timber;

/**
 * Created on 2026/6/13.
 *
 * @author 郑少鹏
 * @desc 轮播布局管理器（精简轻量版）
 */
public class CarouselLayoutManager extends LinearLayoutManager {
    /**
     * 侧翼卡片缩放所能到达的最小值
     * <p>
     * 默认 0.82f
     * 即最大缩放掉 18% 尺寸
     */
    private float minScale = 0.82f;
    /**
     * 侧翼卡片渐变所能到达的最低透明
     * <p>
     * 默认 0.6f
     */
    private float minAlpha = 0.6f;
    /**
     * 是否开启渐变透明度视差开关
     */
    private boolean enableAlpha = true;
    /**
     * 是否开启卡片大小缩放视差开关
     */
    private boolean enableScale = true;
    /**
     * 单张卡片的固定物理像素宽度
     * <p>
     * 为 0 时系统会自动触发智能测绘预测
     */
    private int itemWidth = 0;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public CarouselLayoutManager(Context context) {
        // 固定为横向单行横滑排列
        // 且不反转布局
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
        // 智能宽度测绘防御
        // 如果外部未在 xml 中显式指定 item 宽度
        // 此处触发预测逻辑
        if ((itemWidth <= 0) && (state.getItemCount() > 0)) {
            try {
                // 试探性地向复用池索取第 0 位的卡片实例
                View dummy = recycler.getViewForPosition(0);
                // 赋予其无限制的测量规格进行强制物理测绘
                dummy.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                // 抓取其测绘成功后的物理像素宽度
                // 据此作为全局卡片标准款尺寸
                itemWidth = dummy.getMeasuredWidth();
                // 功成身退
                // 立刻将试探 View 毫损地回收归还到复用池
                // 避免乱序和内存堆积
                recycler.recycleView(dummy);
            } catch (Exception exception) {
                Timber.e(exception);
            }
        }
        try {
            // 交付父类去执行原生的子 View 锚定与基础铺设
            super.onLayoutChildren(recycler, state);
            // 实时注入卡片线性流动几何变换
            applyItemTransform();
        } catch (Exception exception) {
            Timber.e(exception);
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        // 捕获每一次横向滚动的像素物理位移值
        int result = super.scrollHorizontallyBy(dx, recycler, state);
        // 滑动的每一帧中
        // 必须重新走一遍实时计算变换
        // 达成无级流式特质
        applyItemTransform();
        return result;
    }

    /**
     * 核心无级缩放与渐变变换器
     * <p>
     * 实时计算子 View 相对于主容器几何中心点的距离
     * 实施无级差值渲染
     */
    private void applyItemTransform() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        // 计算整个轮播图大容器的正中央横坐标 X 点
        float recyclerCenterX = getWidth() / 2f;
        // 划定视差有效渲染的最大半物理翼展半径
        float halfWidth = getWidth() * 0.5f;
        // 挨个遍历当前屏幕上可见的每一个卡片节点进行矩阵形变
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) {
                continue;
            }
            // 强力尺寸规格化校正
            // 确保卡片无论如何蹂躏
            // 其宽度始终与全局 itemWidth 强对齐
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            if ((layoutParams != null) && (itemWidth > 0) && (layoutParams.width != itemWidth)) {
                layoutParams.width = itemWidth;
                child.setLayoutParams(layoutParams);
            }
            // 计算出该子卡片当前几何位置的横向正中心坐标点
            float childCenterX = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2f;
            // 核心公式
            // 计算卡片偏离主中心点的线性百分比差值
            // 0.0f 表示正中央
            // 1.0f 表示完全边缘化
            float fraction = Math.min(Math.abs(recyclerCenterX - childCenterX) / halfWidth, 1f);
            // 执行线性无级尺寸缩放
            if (enableScale) {
                // 距离越远
                // fraction 越接近 1
                // scale 越接近 minScale
                float scale = (1f - (1f - minScale) * fraction);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }
            // 执行线性无级透明渐变
            if (enableAlpha) {
                child.setAlpha(1f - (1f - minAlpha) * fraction);
            }
        }
    }

    /**
     * 几何距离遍历法
     * <p>
     * 检索计算并找出当前在物理位置上距离大容器正中央最贴近的那个 Item 的索引
     *
     * @return 当前在物理位置上距离大容器正中央最贴近的那个 Item 的索引
     */
    public int getCenterPosition() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return RecyclerView.NO_POSITION;
        }
        float centerX = getWidth() / 2f;
        View centerView = null;
        float minDistance = Float.MAX_VALUE;
        // 广度遍历可见子视图
        // 比对绝对值偏离像素差
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) {
                continue;
            }
            float childCenter = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2f;
            float distance = Math.abs(centerX - childCenter);
            // 动态截取距离中心最小的那一个卡片
            if (distance < minDistance) {
                minDistance = distance;
                centerView = child;
            }
        }
        // 反查其绑定的真实数据索引项位置并返回
        return (centerView == null) ? RecyclerView.NO_POSITION : getPosition(centerView);
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public void setMinAlpha(float minAlpha) {
        this.minAlpha = minAlpha;
    }

    public void setEnableAlpha(boolean enableAlpha) {
        this.enableAlpha = enableAlpha;
    }

    public void setEnableScale(boolean enableScale) {
        this.enableScale = enableScale;
    }
}