package pool.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zsp.core.R;

import fragmentation.support.SupportFragment;
import util.activity.ActivitySuperviseManager;
import util.log.LogUtils;

/**
 * Created on 2021/3/12
 *
 * @author zsp
 * @desc BasePoolFragment
 */
public abstract class BasePoolFragment extends SupportFragment {
    /**
     * 视图
     */
    private View view;
    /**
     * 第一 Fragment 否
     */
    private boolean areFirstFragment;
    /**
     * OnBackToFirstListener
     */
    private OnBackToFirstListener onBackToFirstListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBackToFirstListener) {
            onBackToFirstListener = (OnBackToFirstListener) context;
        } else {
            throw new RuntimeException(context + " must implements OnBackToFirstListener");
        }
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(layoutResId(), container, false);
        areFirstFragment = areFirstFragment();
        eventBusRegister();
        stepUi(view);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onBackToFirstListener = null;
    }

    /**
     * onSupportVisible
     * <p>
     * Called when the fragment is visible.
     * Fragment 对用户可见时调。
     * <p>
     * Is the combination of [onHiddenChanged() + onResume() / onPause() + setUserVisibleHint()]
     */
    @Override
    public void onSupportVisible() {
        visibleToUser();
    }

    /**
     * onLazyInitView
     * <p>
     * Lazy initial，Called when fragment is first called.
     * 同级懒加载与 ViewPager 懒加载结合调。
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        startLoadOnLazyInitView();
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
        startLoadOnEnterAnimationEnd();
        view.setOnTouchListener((v, event) -> {
            view.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if ((null != fragmentationSupportActivity) && (null != fragmentationSupportActivity.getCurrentFocus()) && (null != fragmentationSupportActivity.getCurrentFocus().getWindowToken())) {
                    hideSoftInput();
                }
            }
            return false;
        });
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
        invisibleToUser();
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    protected abstract int layoutResId();

    /**
     * 第一 Fragment 否
     *
     * @return 第一 Fragment 否
     */
    protected abstract boolean areFirstFragment();

    /**
     * EventBus 注册
     * <p>
     * onDestroyView 反注册。
     */
    protected abstract void eventBusRegister();

    /**
     * 初始控件
     * <p>
     * 此处仅设 Toolbar 标题、返回箭头等轻量 UI 操作。
     *
     * @param view 视图
     */
    protected abstract void stepUi(View view);

    /**
     * Fragment 对用户可见时调
     */
    protected abstract void visibleToUser();

    /**
     * 开始加载
     * <p>
     * 某些场景需懒加载，如 FragmentAdapter 懒加载、同级 Fragment 切换懒加载。
     * 库自 0.8 提供 onLazyInitView(Bundle saveInstanceState) 使用。
     */
    protected abstract void startLoadOnLazyInitView();

    /**
     * 开始加载
     * <p>
     * 此处设 Listener、各 Adapter、请求数据等。
     * onDestroyView 释放。
     */
    protected abstract void startLoadOnEnterAnimationEnd();

    /**
     * Fragment 对用户不可见时调
     */
    protected abstract void invisibleToUser();

    /**
     * EventBus 反注册
     * <p>
     * onCreateView 注册。
     */
    protected abstract void eventBusUnregister();

    /**
     * 处理回退事件
     * <p>
     * 返 true 消费该事件，不再向上传递。
     * 返 false 向上最终传递至 Fragment 宿主 Activity。此时宿主 Activity 复写 onBackPressedSupport 则执行，没复写不执行。
     * Fragment 宿主 Activity 之基类复写 onKeyUp 时同执行。
     * MainActivity 于该法处理。
     * SplashActivity 与 LoginActivity 于 BaseActivity 之 onKeyUp 处理。
     *
     * @return boolean
     */
    @Override
    public boolean onBackPressedSupport() {
        LogUtils.i("是第一个碎片", areFirstFragment + " onBackPressedSupport " + getChildFragmentManager().getBackStackEntryCount());
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            popChild();
        } else if (areFirstFragment) {
            // 第一 Fragment 时退
            ActivitySuperviseManager.getInstance().twoClickToExit(getString(R.string.exitAppHint));
        } else {
            // 非第一则回第一 Fragment
            onBackToFirstListener.onBackToFirstFragment();
        }
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventBusUnregister();
    }

    public interface OnBackToFirstListener {
        /**
         * 回第一 Fragment
         */
        void onBackToFirstFragment();
    }
}