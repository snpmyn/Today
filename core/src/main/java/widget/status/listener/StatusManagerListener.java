package widget.status.listener;

import android.view.View;

import widget.status.manager.StatusManager;

/**
 * @decs: 状态管理器监听
 * @author: 郑少鹏
 * @date: 2018/10/23 18:54
 */
public abstract class StatusManagerListener {
    /**
     * 加载
     *
     * @param loadingView 加载试图
     */
    public abstract void setLoadingEvent(View loadingView);

    /**
     * 空
     *
     * @param emptyView 空视图
     */
    public abstract void setEmptyEvent(View emptyView);

    /**
     * 重试
     *
     * @param statusCode 状态码
     * @param retryView  重试视图
     */
    public abstract void setRetryEvent(int statusCode, View retryView);

    public int generateLoadingLayoutId() {
        return StatusManager.NO_LAYOUT_ID;
    }

    public int generateEmptyLayoutId() {
        return StatusManager.NO_LAYOUT_ID;
    }

    public int generateRetryLayoutId() {
        return StatusManager.NO_LAYOUT_ID;
    }

    public View generateLoadingLayout() {
        return null;
    }

    public View generateEmptyLayout() {
        return null;
    }

    public View generateRetryLayout() {
        return null;
    }

    public boolean areSetLoadingLayout() {
        return ((generateLoadingLayoutId() != StatusManager.NO_LAYOUT_ID) || (null != generateLoadingLayout()));
    }

    public boolean areSetEmptyLayout() {
        return ((generateEmptyLayoutId() != StatusManager.NO_LAYOUT_ID) || (null != generateEmptyLayout()));
    }

    public boolean areSetRetryLayout() {
        return ((generateRetryLayoutId() != StatusManager.NO_LAYOUT_ID) || (null != generateRetryLayout()));
    }
}