package fragmentation.fragmentation.helper.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentationMagician;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import fragmentation.fragmentation.ISupportFragment;
import fragmentation.value.FragmentationMagic;

/**
 * @decs: VisibleDelegate
 * @author: 郑少鹏
 * @date: 2019/5/20 9:36
 */
public class VisibleDelegate {
    private static final String FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE = "fragmentation_invisible_when_leave";
    private static final String FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE = "fragmentation_compat_replace";
    private final ISupportFragment iSupportFragment;
    private final Fragment mFragment;
    /**
     * SupportVisible 相关
     */
    private boolean mAreSupportVisible;
    private boolean mNeedDispatch = true;
    private boolean mInvisibleWhenLeave;
    private boolean mAreFirstVisible = true;
    private boolean mFirstCreateViewCompatReplace = true;
    private Handler mHandler;
    private Bundle mSaveInstanceState;

    public VisibleDelegate(ISupportFragment fragment) {
        this.iSupportFragment = fragment;
        this.mFragment = (Fragment) fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            mSaveInstanceState = savedInstanceState;
            // setUserVisibleHint() may be called before onCreate()
            mInvisibleWhenLeave = savedInstanceState.getBoolean(FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE);
            mFirstCreateViewCompatReplace = savedInstanceState.getBoolean(FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE);
        }
    }

    public void onSaveInstanceState(@NotNull Bundle outState) {
        outState.putBoolean(FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE, mInvisibleWhenLeave);
        outState.putBoolean(FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE, mFirstCreateViewCompatReplace);
    }

    public void onActivityCreated() {
        if (!mFirstCreateViewCompatReplace && (null != mFragment.getTag()) && mFragment.getTag().startsWith(FragmentationMagic.STRING_ANDROID_SWITCHER)) {
            return;
        }
        if (mFirstCreateViewCompatReplace) {
            mFirstCreateViewCompatReplace = false;
        }
        if (!mInvisibleWhenLeave && !mFragment.isHidden() && mFragment.getUserVisibleHint()) {
            if ((null == mFragment.getParentFragment()) || areFragmentVisible(mFragment.getParentFragment())) {
                mNeedDispatch = false;
                safeDispatchUserVisibleHint(true);
            }
        }
    }

    public void onResume() {
        if (!mAreFirstVisible) {
            if (!mAreSupportVisible && !mInvisibleWhenLeave && areFragmentVisible(mFragment)) {
                mNeedDispatch = false;
                dispatchSupportVisible(true);
            }
        }
    }

    public void onPause() {
        if (mAreSupportVisible && areFragmentVisible(mFragment)) {
            mNeedDispatch = false;
            mInvisibleWhenLeave = false;
            dispatchSupportVisible(false);
        } else {
            mInvisibleWhenLeave = true;
        }
    }

    public void onHiddenChanged(boolean hidden) {
        if (!hidden && !mFragment.isResumed()) {
            // if fragment is shown but not resumed, ignore...
            mInvisibleWhenLeave = false;
            return;
        }
        if (hidden) {
            safeDispatchUserVisibleHint(false);
        } else {
            enqueueDispatchVisible();
        }
    }

    public void onDestroyView() {
        mAreFirstVisible = true;
    }

    public void setUserVisibleHint(boolean areVisibleToUser) {
        boolean flag = (mFragment.isResumed() || (!mFragment.isAdded() && areVisibleToUser));
        if (flag) {
            if (!mAreSupportVisible && areVisibleToUser) {
                safeDispatchUserVisibleHint(true);
            } else if (mAreSupportVisible && !areVisibleToUser) {
                dispatchSupportVisible(false);
            }
        }
    }

    private void safeDispatchUserVisibleHint(boolean visible) {
        if (mAreFirstVisible) {
            if (!visible) {
                return;
            }
            enqueueDispatchVisible();
        } else {
            dispatchSupportVisible(visible);
        }
    }

    private void enqueueDispatchVisible() {
        getHandler().post(() -> dispatchSupportVisible(true));
    }

    private void dispatchSupportVisible(boolean visible) {
        if (visible && areParentInvisible()) {
            return;
        }
        if (mAreSupportVisible == visible) {
            mNeedDispatch = true;
            return;
        }
        mAreSupportVisible = visible;
        if (visible) {
            if (checkAddState()) {
                return;
            }
            iSupportFragment.onSupportVisible();
            if (mAreFirstVisible) {
                mAreFirstVisible = false;
                iSupportFragment.onLazyInitView(mSaveInstanceState);
            }
            dispatchChild(true);
        } else {
            dispatchChild(false);
            iSupportFragment.onSupportInvisible();
        }
    }

    private void dispatchChild(boolean visible) {
        if (!mNeedDispatch) {
            mNeedDispatch = true;
        } else {
            if (checkAddState()) {
                return;
            }
            FragmentManager fragmentManager = mFragment.getChildFragmentManager();
            List<Fragment> childFragments = FragmentationMagician.getActiveFragments(fragmentManager);
            for (Fragment child : childFragments) {
                if ((child instanceof ISupportFragment) && !child.isHidden() && child.getUserVisibleHint()) {
                    ((ISupportFragment) child).getSupportDelegate().getVisibleDelegate().dispatchSupportVisible(visible);
                }
            }
        }
    }

    private boolean areParentInvisible() {
        Fragment parentFragment = mFragment.getParentFragment();
        if (parentFragment instanceof ISupportFragment) {
            return !((ISupportFragment) parentFragment).isSupportVisible();
        }
        return (null != parentFragment) && !parentFragment.isVisible();
    }

    private boolean checkAddState() {
        if (!mFragment.isAdded()) {
            mAreSupportVisible = !mAreSupportVisible;
            return true;
        }
        return false;
    }

    private boolean areFragmentVisible(@NotNull Fragment fragment) {
        return !fragment.isHidden() && fragment.getUserVisibleHint();
    }

    public boolean areSupportVisible() {
        return mAreSupportVisible;
    }

    private Handler getHandler() {
        if (null == mHandler) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }
}