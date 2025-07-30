package fragmentation.fragmentation;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentationMagician;

import org.jetbrains.annotations.NotNull;

import fragmentation.fragmentation.animation.DefaultVerticalAnimator;
import fragmentation.fragmentation.animation.FragmentAnimator;
import fragmentation.fragmentation.debug.DebugStackDelegate;
import fragmentation.fragmentation.queue.BaseAction;

/**
 * @decs: SupportActivityDelegate
 * @author: 郑少鹏
 * @date: 2019/5/20 9:48
 */
public class SupportActivityDelegate {
    private final ISupportActivity mSupport;
    private final FragmentActivity mActivity;
    private final DebugStackDelegate mDebugStackDelegate;
    boolean mPopMultipleNoAnim = false;
    boolean mFragmentClickable = true;
    private TransactionDelegate mTransactionDelegate;
    private FragmentAnimator mFragmentAnimator;
    private int mDefaultFragmentBackground = 0;

    public SupportActivityDelegate(ISupportActivity iSupportActivity) {
        if (!(iSupportActivity instanceof FragmentActivity)) {
            throw new RuntimeException("Must extends FragmentActivity / AppCompatActivity");
        }
        this.mSupport = iSupportActivity;
        this.mActivity = (FragmentActivity) iSupportActivity;
        this.mDebugStackDelegate = new DebugStackDelegate(this.mActivity);
    }

    /**
     * extraTransaction
     * <p>
     * Perform some extra transactions.
     * 自定 Tag 添 SharedElement 动画，操作非回退栈 Fragment。
     *
     * @return BaseExtraTransaction
     */
    public BaseExtraTransaction extraTransaction() {
        return new BaseExtraTransaction.BaseExtraTransactionImpl<>((FragmentActivity) mSupport, getTopFragment(), getTransactionDelegate(), true);
    }

    public void onCreate() {
        mTransactionDelegate = getTransactionDelegate();
        mFragmentAnimator = mSupport.onCreateFragmentAnimator();
        mDebugStackDelegate.onCreate(Fragmentation.getDefault().getMode());
    }

    TransactionDelegate getTransactionDelegate() {
        if (null == mTransactionDelegate) {
            mTransactionDelegate = new TransactionDelegate(mSupport);
        }
        return mTransactionDelegate;
    }

    public void onPostCreate() {
        mDebugStackDelegate.onPostCreate(Fragmentation.getDefault().getMode());
    }

    /**
     * 获所设全局动画
     *
     * @return FragmentAnimator
     */
    public FragmentAnimator getFragmentAnimator() {
        return mFragmentAnimator.copy();
    }

    /**
     * Set all fragments animation.
     *
     * @param fragmentAnimator FragmentAnimator
     */
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        this.mFragmentAnimator = fragmentAnimator;
        for (Fragment fragment : FragmentationMagician.getActiveFragments(getSupportFragmentManager())) {
            if (fragment instanceof ISupportFragment) {
                ISupportFragment iSupportFragment = (ISupportFragment) fragment;
                SupportFragmentDelegate supportDelegate = iSupportFragment.getSupportDelegate();
                if (supportDelegate.mAnimByActivity) {
                    supportDelegate.mFragmentAnimator = fragmentAnimator.copy();
                    if (null != supportDelegate.mAnimHelper) {
                        supportDelegate.mAnimHelper.notifyChanged(supportDelegate.mFragmentAnimator);
                    }
                }
            }
        }
    }

    /**
     * onCreateFragmentAnimator
     * <p>
     * 构建 Fragment 转场动画。
     * <p>
     * Activity 内实现构建 Activity 内所有 Fragment 转场动画。
     * Fragment 内实现构建该 Fragment 转场动画，此时优先 Activity 之 onCreateFragmentAnimator()。
     *
     * @return FragmentAnimator
     */
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultVerticalAnimator();
    }

    int getDefaultFragmentBackground() {
        return mDefaultFragmentBackground;
    }

    /**
     * Fragment 默背景
     * <p>
     * Fragment 根布局没设 background 属性时背景默 Theme 之 android:windowBackground。
     * 通该法可改其内所有 Fragment 默背景。
     *
     * @param backgroundRes 背景资源
     */
    public void setDefaultFragmentBackground(@DrawableRes int backgroundRes) {
        mDefaultFragmentBackground = backgroundRes;
    }

    /**
     * 显栈视图对话框
     * <p>
     * 调试用。
     */
    void showFragmentStackHierarchyView() {
        mDebugStackDelegate.showFragmentStackHierarchyView();
    }

    /**
     * 显栈视图日志
     * <p>
     * 调试用。
     */
    void logFragmentStackHierarchy() {
        mDebugStackDelegate.logFragmentRecords();
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面事务全执行后执行该 Action。
     *
     * @param runnable 线程
     */
    public void post(final Runnable runnable) {
        mTransactionDelegate.post(runnable);
    }

    /**
     * onBackPressed
     * <p>
     * 不建复写该方法，{@link #onBackPressedSupport} 替。
     */
    public void onBackPressed() {
        mTransactionDelegate.mActionQueue.enqueue(new BaseAction(BaseAction.ACTION_BACK) {
            @Override
            public void run() {
                if (!mFragmentClickable) {
                    mFragmentClickable = true;
                }
                // 获 activeFragment（栈顶开始，状 show 的那个）
                ISupportFragment activeFragment = SupportHelper.getActiveFragment(getSupportFragmentManager());
                if (mTransactionDelegate.dispatchBackPressedEvent(activeFragment)) {
                    return;
                }
                mSupport.onBackPressedSupport();
            }
        });
    }

    /**
     * onBackPressedSupport
     * <p>
     * 回调时机为 Activity 回退栈内 Fragment 数小等 1 时默 finish Activity。
     * 尽量复写该法而非 onBackPress() 保 SupportFragment 内 onBackPressedSupport() 回退事件正常执行。
     */
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            ActivityCompat.finishAfterTransition(mActivity);
        }
    }

    public void onDestroy() {
        mDebugStackDelegate.onDestroy();
    }

    public boolean dispatchTouchEvent() {
        // 防点速过快致抖
        return !mFragmentClickable;
    }

    /*
     **********************************************************************************************
     */

    /**
     * 加载根 Fragment
     * <p>
     * Activity 内头 Fragment 或 Fragment 内头子 Fragment。
     *
     * @param containerId 容器 ID
     * @param toFragment  目标 Fragment
     */
    public void loadRootFragment(int containerId, ISupportFragment toFragment) {
        loadRootFragment(containerId, toFragment, true, false);
    }

    public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnimation) {
        mTransactionDelegate.loadRootTransaction(getSupportFragmentManager(), containerId, toFragment, addToBackStack, allowAnimation);
    }

    /**
     * 加载多同级根 Fragment
     * <p>
     * 似 Wechat、QQ 主页场景。
     *
     * @param containerId  容器 ID
     * @param showPosition 显位
     * @param toFragments  目标 Fragment
     */
    public void loadMultipleRootFragment(int containerId, int showPosition, ISupportFragment... toFragments) {
        mTransactionDelegate.loadMultipleRootTransaction(getSupportFragmentManager(), containerId, showPosition, toFragments);
    }

    /**
     * showHideFragment
     * <p>
     * 显一 Fragment，隐其它同栈所有 Fragment。
     * 用该法时保同级栈内无多余 Fragment（仅通 loadMultipleRootFragment() 载入的 Fragment）。
     * <p>
     * 建用更明确 {@link #showHideFragment(ISupportFragment, ISupportFragment)}。
     *
     * @param showFragment 需显 Fragment
     */
    public void showHideFragment(ISupportFragment showFragment) {
        showHideFragment(showFragment, null);
    }

    /**
     * showHideFragment
     * <p>
     * 显一 Fragment，隐一 Fragment。
     * 主于似微信主页切 tab 场景。
     *
     * @param showFragment 需显 Fragment
     * @param hideFragment 需隐 Fragment
     */
    public void showHideFragment(ISupportFragment showFragment, ISupportFragment hideFragment) {
        mTransactionDelegate.showHideFragment(getSupportFragmentManager(), showFragment, hideFragment);
    }

    public void start(ISupportFragment toFragment) {
        start(toFragment, ISupportFragment.STANDARD);
    }

    /**
     * @param launchMode Similar to Activity's LaunchMode.
     */
    public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        mTransactionDelegate.dispatchStartTransaction(getSupportFragmentManager(), getTopFragment(), toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
    }

    /**
     * Launch an fragment for which you would like a result when it poped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {
        mTransactionDelegate.dispatchStartTransaction(getSupportFragmentManager(), getTopFragment(), toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT);
    }

    /**
     * Start the target Fragment and pop itself.
     */
    public void startWithPop(ISupportFragment toFragment) {
        mTransactionDelegate.startWithPop(getSupportFragmentManager(), getTopFragment(), toFragment);
    }

    public void startWithPopTo(ISupportFragment toFragment, @NotNull Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mTransactionDelegate.startWithPopTo(getSupportFragmentManager(), getTopFragment(), toFragment, targetFragmentClass.getName(), includeTargetFragment);
    }

    public void replaceFragment(ISupportFragment toFragment, boolean addToBackStack) {
        mTransactionDelegate.dispatchStartTransaction(getSupportFragmentManager(), getTopFragment(), toFragment, 0, ISupportFragment.STANDARD, addToBackStack ? TransactionDelegate.TYPE_REPLACE : TransactionDelegate.TYPE_REPLACE_DO_NOT_BACK);
    }

    /**
     * Pop the child fragment.
     */
    public void pop() {
        mTransactionDelegate.pop(getSupportFragmentManager());
    }

    /**
     * Pop the last fragment transition from the manager's fragment back stack.
     * <p>
     * 出栈至目标 Fragment。
     *
     * @param targetFragmentClass   目标 Fragment
     * @param includeTargetFragment 含目标 Fragment 否
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        popTo(targetFragmentClass, includeTargetFragment, null);
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 若你想出栈后立刻 FragmentTransaction 操作，用该法。
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, TransactionDelegate.DEFAULT_POP_TO_ANIM);
    }

    public void popTo(@NotNull Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnimation) {
        mTransactionDelegate.popTo(targetFragmentClass.getName(), includeTargetFragment, afterPopTransactionRunnable, getSupportFragmentManager(), popAnimation);
    }

    private @NotNull FragmentManager getSupportFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }

    private ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }
}