package fragmentation.support;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import fragmentation.fragmentation.BaseExtraTransaction;
import fragmentation.fragmentation.ISupportActivity;
import fragmentation.fragmentation.ISupportFragment;
import fragmentation.fragmentation.SupportActivityDelegate;
import fragmentation.fragmentation.SupportHelper;
import fragmentation.fragmentation.animation.FragmentAnimator;

/**
 * Created on 2017/7/19.
 *
 * @author 郑少鹏
 * @desc SupportActivity
 */
public class SupportActivity extends AppCompatActivity implements ISupportActivity {
    /**
     * SupportActivityDelegate
     */
    final SupportActivityDelegate supportActivityDelegate = new SupportActivityDelegate(this);

    @Override
    public SupportActivityDelegate getSupportDelegate() {
        return supportActivityDelegate;
    }

    /**
     * extraTransaction
     * <p>
     * Perform some extra transactions.
     * 自定 Tag 添 SharedElement 动画，操作非回退栈 Fragment。
     *
     * @return BaseExtraTransaction
     */
    @Override
    public BaseExtraTransaction extraTransaction() {
        return supportActivityDelegate.extraTransaction();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportActivityDelegate.onCreate();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        supportActivityDelegate.onPostCreate();
    }

    @Override
    protected void onDestroy() {
        supportActivityDelegate.onDestroy();
        super.onDestroy();
    }

    /**
     * Note: return supportActivityDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
     *
     * @param ev 手势事件
     * @return boolean
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return supportActivityDelegate.dispatchTouchEvent() || super.dispatchTouchEvent(ev);
    }

    /**
     * onBackPressed
     * <p>
     * 不建复写该方法，{@link #onBackPressedSupport} 替。
     */
    @Override
    final public void onBackPressed() {
        super.onBackPressed();
        supportActivityDelegate.onBackPressed();
    }

    /**
     * onBackPressedSupport
     * <p>
     * 回调时机为 Activity 回退栈内 Fragment 数小等 1 时默 finish Activity。
     * 尽量复写该法而非 onBackPress() 保 SupportFragment 内 onBackPressedSupport() 回退事件正常执行。
     */
    @Override
    public void onBackPressedSupport() {
        supportActivityDelegate.onBackPressedSupport();
    }

    /**
     * 获所设全局动画
     *
     * @return FragmentAnimator
     */
    @Override
    public FragmentAnimator getFragmentAnimator() {
        return supportActivityDelegate.getFragmentAnimator();
    }

    /**
     * Set all fragments animation.
     *
     * @param fragmentAnimator FragmentAnimator
     */
    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        supportActivityDelegate.setFragmentAnimator(fragmentAnimator);
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
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return supportActivityDelegate.onCreateFragmentAnimator();
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
    @Override
    public void post(Runnable runnable) {
        supportActivityDelegate.post(runnable);
    }

    /*
     ****************************************以下为可选法(Optional methods)****************************************
     */

    /**
     * 加载根 Fragment
     * <p>
     * Activity 内头 Fragment 或 Fragment 内头子 Fragment。
     *
     * @param containerId 容器 ID
     * @param toFragment  目标 Fragment
     */
    public void loadRootFragment(int containerId, @NonNull ISupportFragment toFragment) {
        supportActivityDelegate.loadRootFragment(containerId, toFragment);
    }

    public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnimation) {
        supportActivityDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnimation);
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
        supportActivityDelegate.loadMultipleRootFragment(containerId, showPosition, toFragments);
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
        supportActivityDelegate.showHideFragment(showFragment);
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
        supportActivityDelegate.showHideFragment(showFragment, hideFragment);
    }

    /**
     * It is recommended to use {@link SupportFragment#start(ISupportFragment)}.
     */
    public void start(ISupportFragment toFragment) {
        supportActivityDelegate.start(toFragment);
    }

    /**
     * It is recommended to use {@link SupportFragment#start(ISupportFragment, int)}.
     *
     * @param launchMode Similar to Activity's LaunchMode.
     */
    public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        supportActivityDelegate.start(toFragment, launchMode);
    }

    /**
     * It is recommended to use {@link SupportFragment#startForResult(ISupportFragment, int)}.
     * Launch an fragment for which you would like a result when it popped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {
        supportActivityDelegate.startForResult(toFragment, requestCode);
    }

    /**
     * It is recommended to use {@link SupportFragment#startWithPop(ISupportFragment)}.
     * Start the target fragment and pop itself.
     */
    public void startWithPop(ISupportFragment toFragment) {
        supportActivityDelegate.startWithPop(toFragment);
    }

    /**
     * It is recommended to use {@link SupportFragment#startWithPopTo(ISupportFragment, Class, boolean)}.
     *
     * @see #popTo(Class, boolean)
     * @see #start(ISupportFragment)
     */
    public void startWithPopTo(ISupportFragment toFragment, Class<?> targetFragmentClass, boolean includeTargetFragment) {
        supportActivityDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment);
    }

    /**
     * replaceFragment
     * <p>
     * It is recommended to use {@link SupportFragment#replaceFragment(ISupportFragment, boolean)}.
     *
     * @param toFragment     目标 Fragment
     * @param addToBackStack 添至回退栈否
     */
    public void replaceFragment(ISupportFragment toFragment, boolean addToBackStack) {
        supportActivityDelegate.replaceFragment(toFragment, addToBackStack);
    }

    /**
     * pop
     * <p>
     * Pop the fragment.
     */
    public void pop() {
        supportActivityDelegate.pop();
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
        supportActivityDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    /**
     * popTo
     * <p>
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 若你想出栈后立刻 FragmentTransaction 操作，用该法。
     *
     * @param targetFragmentClass         目标 Fragment
     * @param includeTargetFragment       含目标 Fragment 否
     * @param afterPopTransactionRunnable afterPopTransactionRunnable
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        supportActivityDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    /**
     * popTo
     *
     * @param targetFragmentClass         目标 Fragment
     * @param includeTargetFragment       含目标 Fragment 否
     * @param afterPopTransactionRunnable afterPopTransactionRunnable
     * @param popAnim                     popAnim
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        supportActivityDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
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
        supportActivityDelegate.setDefaultFragmentBackground(backgroundRes);
    }

    /**
     * 栈顶 SupportFragment
     *
     * @return ISupportFragment
     */
    public ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }

    /**
     * Same as fragmentManager.findFragmentByTag(fragmentClass.getName());
     * <p>
     * Find Fragment from FragmentStack.
     */
    public <T extends ISupportFragment> T findFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getSupportFragmentManager(), fragmentClass);
    }
}
