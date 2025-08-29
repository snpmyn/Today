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
import widget.status.listener.BaseStatusListener;

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
    private final StatusLayout statusLayout;
    /**
     * 状（0 无网络、1 连接失败、2 加载失败、3 加载、4 空、5 内容）
     */
    public int status;
    /**
     * 请求码
     */
    public int requestCode = 101;

    private StatusManager(Object activityOrFragmentOrView, BaseStatusListener listener) {
        if (null == listener) {
            listener = new BaseStatusListener() {
                @Override
                public void setRetryEvent(View retryView) {

                }
            };
        }
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
            throw new IllegalArgumentException("the argument's type must be Fragment or Activity: init(context)");
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
        // setup loading、empty、retry layout
        setupLoadingLayout(listener, statusLayout);
        setupEmptyLayout(listener, statusLayout);
        setupRetryLayout(listener, statusLayout);
        // callback
        listener.setLoadingEvent(statusLayout.getLoadingView());
        listener.setEmptyEvent(statusLayout.getEmptyView());
        listener.setRetryEvent(statusLayout.getRetryView());
        this.statusLayout = statusLayout;
    }

    @NonNull
    @Contract("_, _ -> new")
    public static StatusManager generate(Object activityOrFragment, BaseStatusListener listener) {
        return new StatusManager(activityOrFragment, listener);
    }

    private void setupLoadingLayout(@NonNull BaseStatusListener listener, StatusLayout statusLayout) {
        if (listener.isSetLoadingLayout()) {
            int layoutId = listener.generateLoadingLayoutId();
            if (layoutId != NO_LAYOUT_ID) {
                statusLayout.setLoadingView(layoutId);
            } else {
                statusLayout.setLoadingView(listener.generateLoadingLayout());
            }
        } else {
            if (BASE_LOADING_LAYOUT_ID != NO_LAYOUT_ID) {
                statusLayout.setLoadingView(BASE_LOADING_LAYOUT_ID);
            }
        }
    }

    private void setupEmptyLayout(@NonNull BaseStatusListener listener, StatusLayout statusLayout) {
        if (listener.isSetEmptyLayout()) {
            int layoutId = listener.generateEmptyLayoutId();
            if (layoutId != NO_LAYOUT_ID) {
                statusLayout.setEmptyView(layoutId);
            } else {
                statusLayout.setEmptyView(listener.generateEmptyLayout());
            }
        } else {
            if (BASE_EMPTY_LAYOUT_ID != NO_LAYOUT_ID) {
                statusLayout.setEmptyView(BASE_EMPTY_LAYOUT_ID);
            }
        }
    }

    private void setupRetryLayout(@NonNull BaseStatusListener listener, StatusLayout statusLayout) {
        if (listener.isSetRetryLayout()) {
            int layoutId = listener.generateRetryLayoutId();
            if (layoutId != NO_LAYOUT_ID) {
                statusLayout.setLoadingView(layoutId);
            } else {
                statusLayout.setLoadingView(listener.generateRetryLayout());
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
        status = 3;
        statusLayout.showLoading();
    }

    /**
     * 空
     */
    public void showEmpty() {
        status = 4;
        statusLayout.showEmpty();
    }

    /**
     * 重试
     *
     * @param status 0 无网络、1 连接失败、2 加载失败
     */
    public void showRetry(int status) {
        statusLayout.showRetry(status);
        this.status = status;
    }

    /**
     * 内容
     */
    public void showContent() {
        status = 5;
        statusLayout.showContent();
    }
}