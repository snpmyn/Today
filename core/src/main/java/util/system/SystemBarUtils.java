package util.system;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.zsp.core.R;

import org.jetbrains.annotations.Contract;

import util.statusbar.StatusBarUtils;

/**
 * @decs: 系统栏工具类
 * @author: 郑少鹏
 * @date: 2025/10/9 21:18
 * @version: v 1.0
 * <p>
 * 暂无引用
 */
public class SystemBarUtils {
    private static final int STATUS_BAR_MASK_COLOR = 0x7F000000;

    /**
     * 设置浅色状态栏
     *
     * @param activity 活动
     * @param areLight 浅色模式否
     */
    public static void setLightStatusBar(@NonNull Activity activity, boolean areLight) {
        View decorView = activity.getWindow().getDecorView();
        if (areLight) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * 设置浅色导航栏
     *
     * @param activity 活动
     * @param areLight 浅色模式否
     */
    public static void setLightNavigationBar(Activity activity, boolean areLight) {
        if (areLight) {
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    /**
     * 沉浸式状态栏
     * <p>
     * 必须在 {@link Activity#onCreate(Bundle)} 中调用
     *
     * @param activity 活动
     */
    public static void immersiveStatusBar(@NonNull Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View view = decorView.getChildAt(0);
        view.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
            if (layoutParams.topMargin > 0) {
                layoutParams.topMargin = 0;
                v.setLayoutParams(layoutParams);
            }
            if (v.getPaddingTop() > 0) {
                v.setPadding(0, 0, 0, v.getPaddingBottom());
                View content = activity.findViewById(android.R.id.content);
                content.requestLayout();
            }
        });
        View content = activity.findViewById(android.R.id.content);
        content.setPadding(0, 0, 0, content.getPaddingBottom());
        if (null == decorView.findViewById(R.id.status_bar_view)) {
            View statusBarView = new View(activity);
            statusBarView.setId(R.id.status_bar_view);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, StatusBarUtils.getStatusBarHeight(activity));
            layoutParams.gravity = Gravity.TOP;
            statusBarView.setLayoutParams(layoutParams);
            decorView.addView(statusBarView);
        }
        setStatusBarColor(activity, Color.TRANSPARENT);
    }

    /**
     * 沉浸式导航栏
     * <p>
     * 必须在 {@link Activity#onCreate(Bundle)} 中调用
     *
     * @param fragmentActivity 碎片活动
     */
    public static void immersiveNavigationBar(@NonNull FragmentActivity fragmentActivity) {
        ViewGroup decorView = (ViewGroup) fragmentActivity.getWindow().getDecorView();
        View view = decorView.getChildAt(0);
        MutableLiveData<Integer> heightLiveData = new MutableLiveData<>(0);
        decorView.setTag(R.id.navigation_height_live_data, heightLiveData);
        view.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
            if (layoutParams.bottomMargin > 0) {
                layoutParams.bottomMargin = 0;
                v.setLayoutParams(layoutParams);
            }
            if (v.getPaddingBottom() > 0) {
                v.setPadding(0, v.getPaddingTop(), 0, 0);
                View content = fragmentActivity.findViewById(android.R.id.content);
                content.requestLayout();
            }
        });
        View content = fragmentActivity.findViewById(android.R.id.content);
        content.setPadding(0, content.getPaddingTop(), 0, -1);
        if (null == decorView.findViewById(R.id.navigation_bar_view)) {
            View navigationView = new View(fragmentActivity);
            navigationView.setId(R.id.navigation_bar_view);
            Integer value = heightLiveData.getValue();
            if (null != value) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, value);
                layoutParams.gravity = Gravity.BOTTOM;
                navigationView.setLayoutParams(layoutParams);
            }
            decorView.addView(navigationView);
        }
        setNavigationBarColor(fragmentActivity, Color.TRANSPARENT);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 活动
     * @param color    颜色
     */
    public static void setStatusBarColor(@NonNull Activity activity, int color) {
        View statusBarView = activity.getWindow().getDecorView().findViewById(R.id.status_bar_view);
        if (null != statusBarView) {
            statusBarView.setBackgroundColor(color);
        }
    }

    /**
     * 设置导航栏颜色
     *
     * @param activity 活动
     * @param color    颜色
     */
    public static void setNavigationBarColor(@NonNull Activity activity, int color) {
        View navigationView = activity.getWindow().getDecorView().findViewById(R.id.navigation_bar_view);
        if (null != navigationView) {
            navigationView.setBackgroundColor(color);
        }
    }

    /**
     * 获取屏幕尺寸
     *
     * @param activity 活动
     * @return 屏幕尺寸
     */
    @NonNull
    @Contract("_ -> new")
    public static Size getScreenSize(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return new Size(activity.getWindowManager().getCurrentWindowMetrics().getBounds().width(), activity.getWindowManager().getCurrentWindowMetrics().getBounds().height());
        } else {
            return new Size(activity.getWindowManager().getDefaultDisplay().getWidth(), activity.getWindowManager().getDefaultDisplay().getHeight());
        }
    }
}