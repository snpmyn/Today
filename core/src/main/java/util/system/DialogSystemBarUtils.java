package util.system;

import android.app.Dialog;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * @decs: 对话框系统栏工具类
 * @author: 郑少鹏
 * @date: 2025/10/9 21:14
 * @version: v 1.0
 * <p>
 * 暂无引用
 */
public class DialogSystemBarUtils {
    /**
     * 设置浅色状态栏
     *
     * @param dialog   对话框
     * @param areLight 浅色模式否
     */
    public static void setLightStatusBar(@NonNull Dialog dialog, boolean areLight) {
        if (null == dialog.getWindow()) {
            return;
        }
        View decorView = dialog.getWindow().getDecorView();
        if (areLight) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * 设置浅色导航栏
     *
     * @param dialog   对话框
     * @param areLight 浅色模式否
     */
    public static void setLightNavigationBar(@NonNull Dialog dialog, boolean areLight) {
        if (null == dialog.getWindow()) {
            return;
        }
        if (areLight) {
            View decorView = dialog.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    /**
     * 沉浸式状态栏
     *
     * @param dialog 对话框
     */
    public static void immersiveStatusBar(@NonNull Dialog dialog) {
        if (null == dialog.getWindow()) {
            return;
        }
        ViewGroup decorView = (ViewGroup) dialog.getWindow().getDecorView();
        decorView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if ((null != child) && (child.getId() == android.R.id.statusBarBackground)) {
                    child.setScaleX(0.0F);
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
    }

    /**
     * 沉浸式导航栏
     *
     * @param dialog 对话框
     */
    public static void immersiveNavigationBar(@NonNull Dialog dialog) {
        if (null == dialog.getWindow()) {
            return;
        }
        ViewGroup decorView = (ViewGroup) dialog.getWindow().getDecorView();
        decorView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (null != child) {
                    if ((child.getId() == android.R.id.navigationBarBackground) || (child.getId() == android.R.id.statusBarBackground)) {
                        child.setScaleX(0.0F);
                    }
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
    }
}