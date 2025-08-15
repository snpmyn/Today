package fragmentation.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fragmentation.fragmentation.BaseExtraTransaction;
import fragmentation.fragmentation.ISupportFragment;
import fragmentation.fragmentation.SupportFragmentDelegate;
import fragmentation.fragmentation.SupportHelper;
import fragmentation.fragmentation.animation.FragmentAnimator;
import timber.log.Timber;

/**
 * Created on 2019/1/14.
 *
 * @author 郑少鹏
 * @desc SupportFragment
 * 用 Fragment 过程有些场景需懒加载，如 FragmentAdapter 懒加载、同级 Fragment 切换懒加载。库自 0.8 提供 onLazyInitView(Bundle saveInstanceState)。
 * 该法于 Fragment 第一次对用户可见时（第一次 onSupportVisible()）调。
 * View 安全法（调时 onCreateView() 已被调）。
 * <p>
 * onSupportVisible() 等生命周期调用顺序 onActivityCreated() -> onResume() -> onSupportVisible -> onLazyInitView() -> onEnterAnimationEnd -> onSupportInvisible() -> onPause()。
 * Fragment 可见时调（含嵌套子 Fragment）。
 * A 内子 FragmentB，B 通 A startFragment(C)（此时 A 和 C 同级）。pop(C) 都调 A 和 B 之 onSupportVisible()。
 * View 安全法（调时 onCreateView() 已被调）。
 * <p>
 * 复杂 Fragment 页面第一次 start 因该 Fragment 复杂初始和动画同进行致动画卡顿，库提供一解决方案 onEnterAnimationEnd(Bundle saveInstanceState)。
 * 该法于转场动画结束后调，没动画则 onActivityCreated 调。此时于 onEnterAnimationEnd(Bundle saveInstanceState) 初始复杂数据以保 Fragment 动画流畅。
 * <p>
 * 竖直动画视觉上较横向动画流畅。
 * <p>
 * 按 Back 键后事件首传递至 Activity 内栈顶 Fragment。该 Fragment 有子 Fragment 则传递至子栈内栈顶子 Fragment，依次类推。
 * 栈顶子 Fragment 不处理该事件则向上传递（栈底还有子 Fragment 则向栈底传递，没则向父 Fragment 传递，最终至 SupportActivity）。
 * <p>
 * startLoad 于 onLazyInitView 打开新页面执行 startLoad，打开已打开页面不再执行。
 * 于 onCreateView 初启应用执行所有页面逻辑操作，此后不再执行。
 * <p>
 * FragmentA（继承 {@link SupportFragment}）嵌套 FragmentB。
 * FragmentB 通 {@link #start(ISupportFragment)} 开启 FragmentC，FragmentB 与 FragmentC 同栈且都嵌套于 FragmentA。
 * 点返回键 FragmentA 出栈需复写 {@link #onBackPressedSupport()}。
 * {@link #onBackPressedSupport()} 中需 {@link #pop()} 不可 _mActivity.onBackPressed()。
 * 其它处可 _mActivity.onBackPressed()。
 */
public class SupportFragment extends Fragment implements ISupportFragment {
    /**
     * SupportFragmentDelegate
     */
    private final SupportFragmentDelegate supportFragmentDelegate = new SupportFragmentDelegate(this);
    /**
     * SupportActivity
     */
    protected SupportActivity fragmentationSupportActivity;

    @Override
    public SupportFragmentDelegate getSupportDelegate() {
        return supportFragmentDelegate;
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
        return supportFragmentDelegate.extraTransaction();
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context Context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        supportFragmentDelegate.onAttach((Activity) context);
        fragmentationSupportActivity = (SupportActivity) supportFragmentDelegate.getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportFragmentDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.d("onActivityCreated");
        supportFragmentDelegate.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d("onSaveInstanceState");
        supportFragmentDelegate.onSaveInstanceState(outState);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Timber.d("onCreateAnimation");
        return supportFragmentDelegate.onCreateAnimation(transit, enter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume");
        supportFragmentDelegate.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause");
        supportFragmentDelegate.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroyView");
        supportFragmentDelegate.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");
        supportFragmentDelegate.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Timber.d("onHiddenChanged");
        supportFragmentDelegate.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Timber.d("setUserVisibleHint");
        supportFragmentDelegate.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * onSupportVisible
     * <p>
     * Called when the fragment is visible.
     * Fragment 对用户可见时调
     * <p>
     * Is the combination of  [onHiddenChanged() + onResume() / onPause() + setUserVisibleHint()]
     */
    @Override
    public void onSupportVisible() {
        Timber.d("onSupportVisible");
        supportFragmentDelegate.onSupportVisible();
    }

    /**
     * onLazyInitView
     * <p>
     * Lazy initial，Called when fragment is first called.
     * <p>
     * 同级懒加载与 ViewPager 懒加载结合调。
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        Timber.d("onLazyInitView");
        supportFragmentDelegate.onLazyInitView();
    }

    /**
     * onEnterAnimationEnd
     * <p>
     * Called when the enter-animation end.
     * 入栈动画结束时调。
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        Timber.d("onEnterAnimationEnd");
        supportFragmentDelegate.onEnterAnimationEnd();
    }

    /**
     * onSupportInvisible
     * <p>
     * Called when the fragment is invisible.
     * Fragment 对用户不可见时调。
     * <p>
     * Is the combination of  [onHiddenChanged() + onResume() / onPause() + setUserVisibleHint()]
     */
    @Override
    public void onSupportInvisible() {
        Timber.d("onSupportInvisible");
        supportFragmentDelegate.onSupportInvisible();
    }

    /**
     * enqueueAction
     * <p>
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面事务全部执行后执行该 Action。
     *
     * @param runnable runnable
     * @deprecated Use{@link #post(Runnable)}instead.
     */
    @Deprecated
    @Override
    public void enqueueAction(Runnable runnable) {
        supportFragmentDelegate.post(runnable);
    }

    /**
     * post
     * <p>
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面事务全部执行后执行该 Action。
     *
     * @param runnable runnable
     */
    @Override
    public void post(Runnable runnable) {
        supportFragmentDelegate.post(runnable);
    }

    /**
     * isSupportVisible
     * <p>
     * Return true if the fragment has been supportVisible.
     *
     * @return 支持可见否
     */
    @Override
    final public boolean isSupportVisible() {
        return supportFragmentDelegate.isSupportVisible();
    }

    /**
     * onCreateFragmentAnimator
     * <p>
     * Set fragment animation with a higher priority than the ISupportActivity.
     *
     * @return FragmentAnimator
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return supportFragmentDelegate.onCreateFragmentAnimator();
    }

    /**
     * 获所设全局动画
     *
     * @return FragmentAnimator
     */
    @Override
    public FragmentAnimator getFragmentAnimator() {
        return supportFragmentDelegate.getFragmentAnimator();
    }

    /**
     * 设 Fragment 内全局动画
     *
     * @param fragmentAnimator fragmentAnimator
     */
    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        supportFragmentDelegate.setFragmentAnimator(fragmentAnimator);
    }

    /**
     * setFragmentResult
     * <p>
     * Similar to {@link Activity#setResult(int, Intent)}.
     *
     * @param resultCode resultCode
     * @param bundle     bundle
     * @see #startForResult(ISupportFragment, int)
     */
    @Override
    public void setFragmentResult(int resultCode, Bundle bundle) {
        supportFragmentDelegate.setFragmentResult(resultCode, bundle);
    }

    /**
     * onFragmentResult
     * <p>
     * Similar to {@link Activity}之onActivityResult(int, int, Intent).
     *
     * @param requestCode 响应码
     * @param resultCode  结果码
     * @param data        数据
     * @see #startForResult(ISupportFragment, int)
     */
    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        supportFragmentDelegate.onFragmentResult(requestCode, resultCode, data);
    }

    /**
     * onNewBundle
     * <p>
     * start(TargetFragment,LaunchMode) 时启动模式为 SingleTask / SingleTop，回调 TargetFragment 该法。
     * Similar to {@link Activity}之onNewIntent(Intent).
     *
     * @param args args
     * @see #start(ISupportFragment, int)
     */
    @Override
    public void onNewBundle(Bundle args) {
        supportFragmentDelegate.onNewBundle(args);
    }

    /**
     * putNewBundle
     * <p>
     * 用于 SingleTask / SingleTop 启动模式。
     *
     * @param newBundle newBundle
     * @see #start(ISupportFragment, int)
     */
    @Override
    public void putNewBundle(Bundle newBundle) {
        supportFragmentDelegate.putNewBundle(newBundle);
    }

    /**
     * 处理回退事件
     * <p>
     * 返 true 消费该事件，不再向上传递。
     * 返 false 向上最终传递至 Fragment 宿主 Activity。此时宿主 Activity 复写 onBackPressedSupport 则执行，没复写不执行。
     * Fragment 宿主 Activity 之基类复写 onKeyUp 时同执行。
     *
     * @return boolean
     */
    @Override
    public boolean onBackPressedSupport() {
        return supportFragmentDelegate.onBackPressedSupport();
    }

    /*
     ****************************************以下为可选法(Optional methods)****************************************
     */

    /**
     * 显软键盘
     * <p>
     * 调后于 onPause 自隐。
     *
     * @param view 视图
     */
    protected void showSoftInput(final View view) {
        supportFragmentDelegate.showSoftInput(view);
    }

    /**
     * 隐软键盘
     */
    protected void hideSoftInput() {
        supportFragmentDelegate.hideSoftInput();
    }

    /**
     * 加载根 Fragment
     * <p>
     * 即 Activity 内头 Fragment 或 Fragment 内头子 Fragment。
     *
     * @param containerId 容器 ID
     * @param toFragment  目标 Fragment
     */
    protected void loadRootFragment(int containerId, ISupportFragment toFragment) {
        supportFragmentDelegate.loadRootFragment(containerId, toFragment);
    }

    protected void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnim) {
        supportFragmentDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnim);
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
        supportFragmentDelegate.loadMultipleRootFragment(containerId, showPosition, toFragments);
    }

    /**
     * showHideFragment
     * <p>
     * 显一 Fragment，隐其它同栈所有 Fragment。
     * 用该法时保同级栈内无多余 Fragment（仅通 loadMultipleRootFragment() 载入的 Fragment）。
     * <p>
     * 建用更明确{@link #showHideFragment(ISupportFragment, ISupportFragment)}。
     *
     * @param showFragment 需显 Fragment
     */
    public void showHideFragment(ISupportFragment showFragment) {
        supportFragmentDelegate.showHideFragment(showFragment);
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
    private void showHideFragment(ISupportFragment showFragment, ISupportFragment hideFragment) {
        supportFragmentDelegate.showHideFragment(showFragment, hideFragment);
    }

    public void start(ISupportFragment toFragment) {
        supportFragmentDelegate.start(toFragment);
    }

    /**
     * start
     *
     * @param toFragment 目标 Fragment
     * @param launchMode 启动模式（类似活动）
     */
    public void start(final ISupportFragment toFragment, @LaunchMode int launchMode) {
        supportFragmentDelegate.start(toFragment, launchMode);
    }

    /**
     * startForResult
     * <p>
     * Launch an fragment for which you would like a result when it popped.
     *
     * @param toFragment  目标 Fragment
     * @param requestCode 响应码
     */
    protected void startForResult(ISupportFragment toFragment, int requestCode) {
        supportFragmentDelegate.startForResult(toFragment, requestCode);
    }

    /**
     * startWithPop
     * <p>
     * Start the target Fragment and pop itself.
     *
     * @param toFragment 目标 Fragment
     */
    public void startWithPop(ISupportFragment toFragment) {
        supportFragmentDelegate.startWithPop(toFragment);
    }

    /**
     * startWithPopTo
     *
     * @param toFragment            目标 Fragment
     * @param targetFragmentClass   目标 Fragment
     * @param includeTargetFragment 含目标 Fragment否
     * @see #popTo(Class, boolean)
     * @see #start(ISupportFragment)
     */
    void startWithPopTo(ISupportFragment toFragment, Class<?> targetFragmentClass, boolean includeTargetFragment) {
        supportFragmentDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment);
    }

    public void replaceFragment(ISupportFragment toFragment, boolean addToBackStack) {
        supportFragmentDelegate.replaceFragment(toFragment, addToBackStack);
    }

    protected void pop() {
        supportFragmentDelegate.pop();
    }

    /**
     * popChild
     * <p>
     * Pop the child fragment.
     */
    protected void popChild() {
        supportFragmentDelegate.popChild();
    }

    /**
     * Pop the last fragment transition from the manager's fragment back stack.
     * <p>
     * 出栈至目标 Fragment。
     *
     * @param targetFragmentClass   目标 Fragment
     * @param includeTargetFragment 含目标 Fragment 否
     */
    private void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        supportFragmentDelegate.popTo(targetFragmentClass, includeTargetFragment);
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
        supportFragmentDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
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
        supportFragmentDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }

    /**
     * popToChild
     *
     * @param targetFragmentClass   目标 Fragment
     * @param includeTargetFragment 含目标 Fragment 否
     */
    public void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        supportFragmentDelegate.popToChild(targetFragmentClass, includeTargetFragment);
    }

    /**
     * popToChild
     *
     * @param targetFragmentClass         目标 Fragment
     * @param includeTargetFragment       含目标 Fragment 否
     * @param afterPopTransactionRunnable afterPopTransactionRunnable
     */
    public void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        supportFragmentDelegate.popToChild(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    /**
     * popToChild
     *
     * @param targetFragmentClass         目标 Fragment
     * @param includeTargetFragment       含目标 Fragment 否
     * @param afterPopTransactionRunnable afterPopTransactionRunnable
     * @param popAnim                     popAnim
     */
    public void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        supportFragmentDelegate.popToChild(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }

    /**
     * 栈顶 Fragment
     *
     * @return ISupportFragment
     */
    public ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getParentFragmentManager());
    }

    protected ISupportFragment getTopChildFragment() {
        return SupportHelper.getTopFragment(getChildFragmentManager());
    }

    /**
     * 目标 Fragment 前一 SupportFragment
     */
    protected ISupportFragment getPreFragment() {
        return SupportHelper.getPreFragment(this);
    }

    /**
     * 栈内 Fragment
     *
     * @param fragmentClass fragmentClass
     * @param <T>           <T>
     * @return ISupportFragment
     */
    public <T extends ISupportFragment> T findFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getParentFragmentManager(), fragmentClass);
    }

    /**
     * 栈内 Fragment
     *
     * @param fragmentClass fragmentClass
     * @param <T>           <T>
     * @return ISupportFragment
     */
    protected <T extends ISupportFragment> T findChildFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getChildFragmentManager(), fragmentClass);
    }
}