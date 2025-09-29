package widget.status.manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

import widget.status.layout.StatusLayout;
import widget.status.listener.StatusManagerListener;

/**
 * @decs: 状态管理器
 * @author: 郑少鹏
 * @date: 2018/10/23 18:54
 */
public class StatusManager {
    public static final int NO_LAYOUT_ID = 0;
    public static int BASE_LOADING_LAYOUT_ID = NO_LAYOUT_ID;
    public static int BASE_EMPTY_LAYOUT_ID = NO_LAYOUT_ID;
    public static int BASE_RETRY_LAYOUT_ID = NO_LAYOUT_ID;
    /**
     * 状态布局
     */
    private final StatusLayout statusLayout;
    /**
     * 状态管理器监听
     */
    private final StatusManagerListener statusManagerListener;
    /**
     * 状态码
     * <p>
     * 0 无网络、1 连接失败、2 加载失败、3 加载、4 空、5 内容
     */
    public int statusCode;
    /**
     * 请求码
     */
    public static int requestCode = 101;

    /**
     * constructor
     *
     * @param activityOrFragmentOrView 活动或碎片或视图
     * @param statusManagerListener    状态管理器监听
     */
    private StatusManager(Object activityOrFragmentOrView, StatusManagerListener statusManagerListener) {
        this.statusManagerListener = Objects.requireNonNullElseGet(statusManagerListener, () -> new StatusManagerListener() {
            @Override
            public void setLoadingEvent(View loadingView) {

            }

            @Override
            public void setEmptyEvent(View emptyView) {

            }

            @Override
            public void setRetryEvent(int statusCode, View retryView) {

            }
        });
        ViewGroup contentParent;
        Context context;
        if (activityOrFragmentOrView instanceof Activity) {
            Activity activity = (Activity) activityOrFragmentOrView;
            context = activity;
            contentParent = activity.findViewById(android.R.id.content);
        } else if (activityOrFragmentOrView instanceof Fragment) {
            Fragment fragment = (Fragment) activityOrFragmentOrView;
            context = fragment.getActivity();
            contentParent = (ViewGroup) (Objects.requireNonNull(fragment.getView(), "must not be null").getParent());
        } else if (activityOrFragmentOrView instanceof View) {
            View view = (View) activityOrFragmentOrView;
            contentParent = (ViewGroup) (view.getParent());
            context = view.getContext();
        } else {
            throw new IllegalArgumentException("the argument's type must be Fragment or Activity: init (context)");
        }
        int childCount = contentParent.getChildCount();
        // get contentParent
        int index = 0;
        View oldContent;
        if (activityOrFragmentOrView instanceof View) {
            oldContent = (View) activityOrFragmentOrView;
            for (int i = 0; i < childCount; i++) {
                if (contentParent.getChildAt(i) == oldContent) {
                    index = i;
                    break;
                }
            }
        } else {
            oldContent = contentParent.getChildAt(0);
        }
        contentParent.removeView(oldContent);
        // setup content layout
        StatusLayout statusLayout = new StatusLayout(context);
        ViewGroup.LayoutParams lp = oldContent.getLayoutParams();
        contentParent.addView(statusLayout, index, lp);
        statusLayout.setContentView(oldContent);
        // setup LoadingLayout
        setupLoadingLayout(statusLayout);
        // setup EmptyLayout
        setupEmptyLayout(statusLayout);
        // setup RetryLayout
        setupRetryLayout(statusLayout);
        // 状态布局
        this.statusLayout = statusLayout;
    }

    @NonNull
    @Contract("_, _ -> new")
    public static StatusManager generate(Object activityOrFragmentOrView, StatusManagerListener statusManagerListener) {
        return new StatusManager(activityOrFragmentOrView, statusManagerListener);
    }

    private void setupLoadingLayout(StatusLayout statusLayout) {
        if (this.statusManagerListener.areSetLoadingLayout()) {
            int layoutId = this.statusManagerListener.generateLoadingLayoutId();
            if (layoutId != NO_LAYOUT_ID) {
                statusLayout.setLoadingView(layoutId);
            } else {
                statusLayout.setLoadingView(this.statusManagerListener.generateLoadingLayout());
            }
        } else {
            if (BASE_LOADING_LAYOUT_ID != NO_LAYOUT_ID) {
                statusLayout.setLoadingView(BASE_LOADING_LAYOUT_ID);
            }
        }
    }

    private void setupEmptyLayout(StatusLayout statusLayout) {
        if (this.statusManagerListener.areSetEmptyLayout()) {
            int layoutId = this.statusManagerListener.generateEmptyLayoutId();
            if (layoutId != NO_LAYOUT_ID) {
                statusLayout.setEmptyView(layoutId);
            } else {
                statusLayout.setEmptyView(this.statusManagerListener.generateEmptyLayout());
            }
        } else {
            if (BASE_EMPTY_LAYOUT_ID != NO_LAYOUT_ID) {
                statusLayout.setEmptyView(BASE_EMPTY_LAYOUT_ID);
            }
        }
    }

    private void setupRetryLayout(StatusLayout statusLayout) {
        if (this.statusManagerListener.areSetRetryLayout()) {
            int layoutId = this.statusManagerListener.generateRetryLayoutId();
            if (layoutId != NO_LAYOUT_ID) {
                statusLayout.setRetryView(layoutId);
            } else {
                statusLayout.setRetryView(this.statusManagerListener.generateRetryLayout());
            }
        } else {
            if (BASE_RETRY_LAYOUT_ID != NO_LAYOUT_ID) {
                statusLayout.setRetryView(BASE_RETRY_LAYOUT_ID);
            }
        }
    }

    /**
     * 加载
     */
    public void showLoading() {
        statusCode = 3;
        statusLayout.showLoading();
        this.statusManagerListener.setLoadingEvent(statusLayout.getLoadingView());
    }

    /**
     * 空
     */
    public void showEmpty() {
        statusCode = 4;
        statusLayout.showEmpty();
        this.statusManagerListener.setEmptyEvent(statusLayout.getEmptyView());
    }

    /**
     * 内容
     */
    public void showContent() {
        statusCode = 5;
        statusLayout.showContent();
    }

    /**
     * 重试
     *
     * @param statusCode 状态码
     *                   0 无网络
     *                   1 连接失败
     *                   2 加载失败
     */
    public void showRetry(int statusCode) {
        if ((statusCode != 0) && (statusCode != 1) && (statusCode != 2)) {
            return;
        }
        this.statusCode = statusCode;
        statusLayout.showRetry(statusCode);
        this.statusManagerListener.setRetryEvent(this.statusCode, statusLayout.getRetryView());
    }
}