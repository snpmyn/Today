package widget.status.layout;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.zsp.core.R;

import timber.log.Timber;

/**
 * @decs: 状态布局
 * @author: 郑少鹏
 * @date: 2018/10/23 18:54
 */
public class StatusLayout extends FrameLayout {
    private final LayoutInflater mLayoutInflater;
    private View mLoadingView;
    private View mEmptyView;
    private View mRetryView;
    private View mContentView;

    public StatusLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public StatusLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public StatusLayout(Context context) {
        this(context, null);
    }

    private boolean areMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public void showLoading() {
        if (areMainThread()) {
            showView(mLoadingView);
        } else {
            post(() -> showView(mLoadingView));
        }
        // 避 Fragment 场景碎片叠加致点击穿透
        mLoadingView.setClickable(true);
    }

    public void showEmpty() {
        if (areMainThread()) {
            showView(mEmptyView);
        } else {
            post(() -> showView(mEmptyView));
        }
        // 避 Fragment 场景碎片叠加致点击穿透
        mEmptyView.setClickable(true);
    }

    public void showRetry(int status) {
        ImageView statusRetryIv;
        TextView statusRetryTv;
        MaterialButton statusRetryMb;
        switch (status) {
            // 无网络
            case 0:
                statusRetryIv = mRetryView.findViewById(R.id.statusRetryIv);
                statusRetryTv = mRetryView.findViewById(R.id.statusRetryTv);
                statusRetryMb = mRetryView.findViewById(R.id.statusRetryMb);
                statusRetryIv.setImageResource(R.drawable.status_no_network);
                statusRetryTv.setText(R.string.networkExceptionAndTryAgainLater);
                statusRetryMb.setText(R.string.checkSetting);
                if (areMainThread()) {
                    showView(mRetryView);
                } else {
                    post(() -> showView(mRetryView));
                }
                break;
            // 连接失败
            case 1:
                statusRetryIv = mRetryView.findViewById(R.id.statusRetryIv);
                statusRetryTv = mRetryView.findViewById(R.id.statusRetryTv);
                statusRetryMb = mRetryView.findViewById(R.id.statusRetryMb);
                statusRetryIv.setImageResource(R.drawable.status_connect_fail);
                statusRetryTv.setText(R.string.serverExceptionAndTryAgainLater);
                statusRetryMb.setText(R.string.retry);
                if (areMainThread()) {
                    showView(mRetryView);
                } else {
                    post(() -> showView(mRetryView));
                }
                break;
            // 加载失败
            case 2:
                statusRetryIv = mRetryView.findViewById(R.id.statusRetryIv);
                statusRetryTv = mRetryView.findViewById(R.id.statusRetryTv);
                statusRetryMb = mRetryView.findViewById(R.id.statusRetryMb);
                statusRetryIv.setImageResource(R.drawable.status_load_fail);
                statusRetryTv.setText(R.string.loadFailAndTryAgainLater);
                statusRetryMb.setText(R.string.retry);
                if (areMainThread()) {
                    showView(mRetryView);
                } else {
                    post(() -> showView(mRetryView));
                }
                break;
            default:
                break;
        }
        // 避 Fragment 场景碎片叠加致点击穿透
        mRetryView.setClickable(true);
    }

    public void showContent() {
        if (areMainThread()) {
            showView(mContentView);
        } else {
            post(() -> showView(mContentView));
        }
    }

    private void showView(View view) {
        if (null == view) {
            return;
        }
        if (view == mLoadingView) {
            mLoadingView.setVisibility(View.VISIBLE);
            if (null != mRetryView) {
                mRetryView.setVisibility(View.GONE);
            }
            if (null != mContentView) {
                mContentView.setVisibility(View.GONE);
            }
            if (null != mEmptyView) {
                mEmptyView.setVisibility(View.GONE);
            }
        } else if (view == mEmptyView) {
            mEmptyView.setVisibility(View.VISIBLE);
            if (null != mLoadingView) {
                mLoadingView.setVisibility(View.GONE);
            }
            if (null != mRetryView) {
                mRetryView.setVisibility(View.GONE);
            }
            if (null != mContentView) {
                mContentView.setVisibility(View.GONE);
            }
        } else if (view == mRetryView) {
            mRetryView.setVisibility(View.VISIBLE);
            if (null != mLoadingView) {
                mLoadingView.setVisibility(View.GONE);
            }
            if (null != mContentView) {
                mContentView.setVisibility(View.GONE);
            }
            if (null != mEmptyView) {
                mEmptyView.setVisibility(View.GONE);
            }
        } else if (view == mContentView) {
            mContentView.setVisibility(View.VISIBLE);
            if (null != mLoadingView) {
                mLoadingView.setVisibility(View.GONE);
            }
            if (null != mRetryView) {
                mRetryView.setVisibility(View.GONE);
            }
            if (null != mEmptyView) {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public void setLoadingView(int layoutId) {
        setLoadingView(mLayoutInflater.inflate(layoutId, this, false));
    }

    public void setLoadingView(View view) {
        View loadingView = mLoadingView;
        if (null != loadingView) {
            Timber.d("You have already set a loading view and would be instead of this new one.");
        }
        removeView(loadingView);
        addView(view);
        mLoadingView = view;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setEmptyView(int layoutId) {
        setEmptyView(mLayoutInflater.inflate(layoutId, this, false));
    }

    public void setEmptyView(View view) {
        View emptyView = mEmptyView;
        if (null != emptyView) {
            Timber.d("You have already set a empty view and would be instead of this new one.");
        }
        removeView(emptyView);
        addView(view);
        mEmptyView = view;
    }

    public View getRetryView() {
        return mRetryView;
    }

    public void setRetryView(int layoutId) {
        setRetryView(mLayoutInflater.inflate(layoutId, this, false));
    }

    public void setRetryView(View view) {
        View retryView = mRetryView;
        if (null != retryView) {
            Timber.d("You have already set a retry view and would be instead of this new one.");
        }
        removeView(retryView);
        addView(view);
        mRetryView = view;
    }

    public View getContentView() {
        return mContentView;
    }

    public void setContentView(View view) {
        View contentView = mContentView;
        if (null != contentView) {
            Timber.d("You have already set a retry view and would be instead of this new one.");
        }
        removeView(contentView);
        addView(view);
        mContentView = view;
    }

    public void setContentView(int layoutId) {
        setContentView(mLayoutInflater.inflate(layoutId, this, false));
    }
}