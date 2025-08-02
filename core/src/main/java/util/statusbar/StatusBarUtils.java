package util.statusbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.zsp.core.R;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import timber.log.Timber;

/**
 * Created on 2017/7/20.
 *
 * @author 郑少鹏
 * @desc StatusBarUtils
 */
public class StatusBarUtils {
    private static final int DEFAULT_STATUS_BAR_ALPHA = 112;
    private static final int FAKE_STATUS_BAR_VIEW_ID = 1000;
    private static final int FAKE_TRANSLUCENT_VIEW_ID = 1001;
    private static final int TAG_KEY_HAVE_SET_OFFSET = -123;

    /**
     * 状态栏色
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param color             状态栏色值
     */
    public static void setColor(AppCompatActivity appCompatActivity, @ColorInt int color) {
        setColor(appCompatActivity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 状态栏色
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param color             状态栏色值
     * @param statusBarAlpha    状态栏透明度
     */
    private static void setColor(@NotNull AppCompatActivity appCompatActivity, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        appCompatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        appCompatActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        appCompatActivity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
    }

    /**
     * 滑返界面状态栏色
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param color             状态栏色值
     */
    public static void setColorForSwipeBack(AppCompatActivity appCompatActivity, int color) {
        setColorForSwipeBackCustom(appCompatActivity, color);
    }

    /**
     * 滑返界面状态栏色
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param color             状态栏色值
     */
    private static void setColorForSwipeBackCustom(@NotNull AppCompatActivity appCompatActivity, @ColorInt int color) {
        ViewGroup contentView = appCompatActivity.findViewById(android.R.id.content);
        View rootView = contentView.getChildAt(0);
        int statusBarHeight = getStatusBarHeight(appCompatActivity);
        if (rootView instanceof CoordinatorLayout) {
            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;
            coordinatorLayout.setStatusBarBackgroundColor(calculateStatusColor(color, StatusBarUtils.DEFAULT_STATUS_BAR_ALPHA));
        } else {
            contentView.setPadding(0, statusBarHeight, 0, 0);
            contentView.setBackgroundColor(calculateStatusColor(color, StatusBarUtils.DEFAULT_STATUS_BAR_ALPHA));
        }
        setTransparentForWindow(appCompatActivity);
    }

    /**
     * 状态栏纯色 无半透明效果
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param color             状态栏色值
     */
    public static void setColorNoTranslucent(AppCompatActivity appCompatActivity, @ColorInt int color) {
        setColor(appCompatActivity, color, 0);
    }

    /**
     * 状态栏色（5.0 下无半透明效果，不建议用）
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param color             状态栏色值
     */
    @Deprecated
    public static void setColorDiff(AppCompatActivity appCompatActivity, @ColorInt int color) {
        transparentStatusBar(appCompatActivity);
        ViewGroup contentView = appCompatActivity.findViewById(android.R.id.content);
        // 移半透明矩形（避叠加）
        View fakeStatusBarView = contentView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (null != fakeStatusBarView) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(color);
        } else {
            contentView.addView(createStatusBarView(appCompatActivity, color));
        }
        setRootView(appCompatActivity);
    }

    /**
     * 状态栏半透明
     * 适用图作背景界面，此时需图填充至状态栏
     *
     * @param appCompatActivity 需设 AppCompatActivity
     */
    public static void setTranslucent(AppCompatActivity appCompatActivity) {
        setTransparent(appCompatActivity);
        addTranslucentView(appCompatActivity, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 针对根布局为 CoordinatorLayout, 状态栏半透明
     * 适用图作背景界面，此时需图填充至状态栏
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param statusBarAlpha    状态栏透明度
     */
    public static void setTranslucentForCoordinatorLayout(AppCompatActivity appCompatActivity, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        transparentStatusBar(appCompatActivity);
        addTranslucentView(appCompatActivity, statusBarAlpha);
    }

    /**
     * 状态栏全透明
     *
     * @param appCompatActivity 需设 AppCompatActivity
     */
    private static void setTransparent(AppCompatActivity appCompatActivity) {
        transparentStatusBar(appCompatActivity);
        setRootView(appCompatActivity);
    }

    /**
     * 状态栏透明（5.0+ 半透明效果，不建议用）
     * 适用图作背景界面，此时需图填充至状态栏
     *
     * @param appCompatActivity 需设 AppCompatActivity
     */
    @Deprecated
    public static void setTranslucentDiff(@NotNull AppCompatActivity appCompatActivity) {
        // 状态栏透明
        appCompatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setRootView(appCompatActivity);
    }

    /**
     * DrawerLayout 状态栏色
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param drawerLayout      DrawerLayout
     * @param color             状态栏色值
     */
    public static void setColorForDrawerLayout(AppCompatActivity appCompatActivity, DrawerLayout drawerLayout, @ColorInt int color) {
        setColorForDrawerLayout(appCompatActivity, drawerLayout, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * DrawerLayout 状态栏色 纯色
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param drawerLayout      DrawerLayout
     * @param color             状态栏色值
     */
    public static void setColorNoTranslucentForDrawerLayout(AppCompatActivity appCompatActivity, DrawerLayout drawerLayout, @ColorInt int color) {
        setColorForDrawerLayout(appCompatActivity, drawerLayout, color, 0);
    }

    /**
     * DrawerLayout 布局状态栏色
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param drawerLayout      DrawerLayout
     * @param color             状态栏色值
     * @param statusBarAlpha    状态栏透明度
     */
    private static void setColorForDrawerLayout(@NotNull AppCompatActivity appCompatActivity, @NotNull DrawerLayout drawerLayout, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        appCompatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        appCompatActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        appCompatActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        // 生一状态栏大小矩形
        // 添 StatusBarView 到布局
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (null != fakeStatusBarView) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(color);
        } else {
            contentLayout.addView(createStatusBarView(appCompatActivity, color), 0);
        }
        // 内容布局非 LinearLayout 时设 PaddingTop
        if (!(contentLayout instanceof LinearLayout) && (null != contentLayout.getChildAt(1))) {
            contentLayout.getChildAt(1).setPadding(contentLayout.getPaddingLeft(), getStatusBarHeight(appCompatActivity) + contentLayout.getPaddingTop(), contentLayout.getPaddingRight(), contentLayout.getPaddingBottom());
        }
        // 设属性
        setDrawerLayoutProperty(drawerLayout, contentLayout);
        addTranslucentView(appCompatActivity, statusBarAlpha);
    }

    /**
     * DrawerLayout 属性
     *
     * @param drawerLayout              DrawerLayout
     * @param drawerLayoutContentLayout DrawerLayout 内容布局
     */
    private static void setDrawerLayoutProperty(@NotNull DrawerLayout drawerLayout, @NotNull ViewGroup drawerLayoutContentLayout) {
        ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
        drawerLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setClipToPadding(true);
        drawer.setFitsSystemWindows(false);
    }

    /**
     * DrawerLayout 状态栏颜色（5.0 下无半透明效果，不建议用）
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param drawerLayout      DrawerLayout
     * @param color             状态栏色值
     */
    @Deprecated
    public static void setColorForDrawerLayoutDiff(@NotNull AppCompatActivity appCompatActivity, @NotNull DrawerLayout drawerLayout, @ColorInt int color) {
        appCompatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 生一状态栏大小矩形
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (null != fakeStatusBarView) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, DEFAULT_STATUS_BAR_ALPHA));
        } else {
            // 添 StatusBarView 到布局
            contentLayout.addView(createStatusBarView(appCompatActivity, color), 0);
        }
        // 内容布局非 LinearLayout 时设 PaddingTop
        if (!(contentLayout instanceof LinearLayout) && (null != contentLayout.getChildAt(1))) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(appCompatActivity), 0, 0);
        }
        // 属性
        setDrawerLayoutProperty(drawerLayout, contentLayout);
    }

    /**
     * DrawerLayout 状态栏透明
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param drawerLayout      DrawerLayout
     */
    public static void setTranslucentForDrawerLayout(AppCompatActivity appCompatActivity, DrawerLayout drawerLayout) {
        setTransparentForDrawerLayout(appCompatActivity, drawerLayout);
        addTranslucentView(appCompatActivity, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * DrawerLayout 状态栏透明
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param drawerLayout      DrawerLayout
     */
    private static void setTransparentForDrawerLayout(@NotNull AppCompatActivity appCompatActivity, @NotNull DrawerLayout drawerLayout) {
        appCompatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        appCompatActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        appCompatActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        // 内容布局非 LinearLayout 时设 PaddingTop
        if (!(contentLayout instanceof LinearLayout) && (null != contentLayout.getChildAt(1))) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(appCompatActivity), 0, 0);
        }
        // 属性
        setDrawerLayoutProperty(drawerLayout, contentLayout);
    }

    /**
     * DrawerLayout 状态栏透明（5.0+ 半透明效果，不建议用）
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param drawerLayout      DrawerLayout
     */
    @Deprecated
    public static void setTranslucentForDrawerLayoutDiff(@NotNull AppCompatActivity appCompatActivity, @NotNull DrawerLayout drawerLayout) {
        // 状态栏透明
        appCompatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 内容布局属性
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        contentLayout.setFitsSystemWindows(true);
        contentLayout.setClipToPadding(true);
        // 抽屉布局属性
        ViewGroup viewGroup = (ViewGroup) drawerLayout.getChildAt(1);
        viewGroup.setFitsSystemWindows(false);
        // DrawerLayout 属性
        drawerLayout.setFitsSystemWindows(false);
    }

    /**
     * 头部 ImageView 界面状态栏全透明
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param needOffsetView    需向下偏移 View
     */
    public static void setTransparentForImageView(AppCompatActivity appCompatActivity, View needOffsetView) {
        setTranslucentForImageView(appCompatActivity, 0, needOffsetView);
    }

    /**
     * 头部 ImageView 界面状态栏透明（用默透明度）
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param needOffsetView    需向下偏移 View
     */
    public static void setTranslucentForImageView(AppCompatActivity appCompatActivity, View needOffsetView) {
        setTranslucentForImageView(appCompatActivity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
    }

    /**
     * 头部 ImageView 界面状态栏透明
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param statusBarAlpha    状态栏透明度
     * @param needOffsetView    需向下偏移 View
     */
    private static void setTranslucentForImageView(AppCompatActivity appCompatActivity, @IntRange(from = 0, to = 255) int statusBarAlpha, View needOffsetView) {
        setTransparentForWindow(appCompatActivity);
        addTranslucentView(appCompatActivity, statusBarAlpha);
        if (null != needOffsetView) {
            Object haveSetOffset = needOffsetView.getTag(TAG_KEY_HAVE_SET_OFFSET);
            if ((null != haveSetOffset) && (Boolean) haveSetOffset) {
                return;
            }
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(appCompatActivity), layoutParams.rightMargin, layoutParams.bottomMargin);
            needOffsetView.setTag(TAG_KEY_HAVE_SET_OFFSET, true);
        }
    }

    /**
     * Fragment 头部 ImageView 界面状态栏透明
     *
     * @param appCompatActivity Fragment 对应 Activity
     * @param needOffsetView    需向下偏移 View
     */
    public static void setTranslucentForImageViewInFragment(AppCompatActivity appCompatActivity, View needOffsetView) {
        setTranslucentForImageViewInFragment(appCompatActivity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
    }

    /**
     * Fragment 头部 ImageView 界面状态栏透明
     *
     * @param appCompatActivity Fragment 对应 Activity
     * @param needOffsetView    需向下偏移 View
     */
    public static void setTransparentForImageViewInFragment(AppCompatActivity appCompatActivity, View needOffsetView) {
        setTranslucentForImageViewInFragment(appCompatActivity, 0, needOffsetView);
    }

    /**
     * Fragment 头部 ImageView 界面状态栏透明
     *
     * @param appCompatActivity Fragment 对应 Activity
     * @param statusBarAlpha    状态栏透明度
     * @param needOffsetView    需向下偏移 View
     */
    private static void setTranslucentForImageViewInFragment(AppCompatActivity appCompatActivity, @IntRange(from = 0, to = 255) int statusBarAlpha, View needOffsetView) {
        setTranslucentForImageView(appCompatActivity, statusBarAlpha, needOffsetView);
    }

    /**
     * 隐藏伪状态栏 View
     *
     * @param appCompatActivity 调用 Activity
     */
    public static void hideFakeStatusBarView(@NotNull AppCompatActivity appCompatActivity) {
        ViewGroup decorView = (ViewGroup) appCompatActivity.getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (null != fakeStatusBarView) {
            fakeStatusBarView.setVisibility(View.GONE);
        }
        View fakeTranslucentView = decorView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (null != fakeTranslucentView) {
            fakeTranslucentView.setVisibility(View.GONE);
        }
    }

    private static void clearPreviousSetting(@NotNull AppCompatActivity appCompatActivity) {
        ViewGroup decorView = (ViewGroup) appCompatActivity.getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (null != fakeStatusBarView) {
            decorView.removeView(fakeStatusBarView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) appCompatActivity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * 添半透明矩形条
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param statusBarAlpha    透明值
     */
    private static void addTranslucentView(@NotNull AppCompatActivity appCompatActivity, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        ViewGroup contentView = appCompatActivity.findViewById(android.R.id.content);
        View fakeTranslucentView = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (null != fakeTranslucentView) {
            if (fakeTranslucentView.getVisibility() == View.GONE) {
                fakeTranslucentView.setVisibility(View.VISIBLE);
            }
            fakeTranslucentView.setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0));
        } else {
            contentView.addView(createTranslucentStatusBarView(appCompatActivity, statusBarAlpha));
        }
    }

    /**
     * 生一同状态栏大小彩色矩形条
     *
     * @param appCompatActivity 需设 AppCompatActivity
     * @param color             状态栏色值
     * @return 状态栏矩形条
     */
    private static @NotNull View createStatusBarView(AppCompatActivity appCompatActivity, @ColorInt int color) {
        View statusBarView = new View(appCompatActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(appCompatActivity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, 0));
        statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
        return statusBarView;
    }

    /**
     * 根布局参数
     */
    private static void setRootView(@NotNull AppCompatActivity appCompatActivity) {
        ViewGroup parent = appCompatActivity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    /**
     * 透明
     */
    private static void setTransparentForWindow(@NotNull AppCompatActivity appCompatActivity) {
        appCompatActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        appCompatActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * 状态栏透明
     */
    private static void transparentStatusBar(@NotNull AppCompatActivity appCompatActivity) {
        appCompatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        appCompatActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        appCompatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        appCompatActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * 创建半透明矩形 View
     *
     * @param alpha 透明值
     * @return 半透明 View
     */
    private static @NotNull View createTranslucentStatusBarView(AppCompatActivity appCompatActivity, int alpha) {
        // 绘一状态栏等高矩形
        View statusBarView = new View(appCompatActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(appCompatActivity));
        statusBarView.setLayoutParams(layoutParams);
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        statusBarView.setId(FAKE_TRANSLUCENT_VIEW_ID);
        return statusBarView;
    }

    /**
     * 获状态栏高
     *
     * @param context 上下文
     * @return 态栏高
     */
    public static int getStatusBarHeight(@NotNull Context context) {
        int result = 0;
        // 获状态栏高
        @SuppressLint({"InternalInsetResource", "DiscouragedApi"}) int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 算状态栏色
     *
     * @param color color 值
     * @param alpha alpha 值
     * @return 最终状态栏色
     */
    private static int calculateStatusColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = (1 - alpha / 255.0F);
        int red = (color >> 16 & 0xff);
        int green = (color >> 8 & 0xff);
        int blue = (color & 0xff);
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return (0xff << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * 改 MIUI 内置状态栏字体色模式（Dark、Light 两模式）
     *
     * @param appCompatActivity 活动
     * @param mode              模式
     */
    private static void setMiUiStatusBarDarkMode(@NotNull AppCompatActivity appCompatActivity, boolean mode) {
        Class<? extends Window> clazz = appCompatActivity.getWindow().getClass();
        try {
            int darkModeFlag;
            @SuppressLint("PrivateApi") Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(appCompatActivity.getWindow(), mode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * 状态栏字体色暗
     *
     * @param appCompatActivity 活动
     * @param areDark           暗否
     */
    private static void statusBarTextColorDark(@NonNull AppCompatActivity appCompatActivity, boolean areDark) {
        View decorView = appCompatActivity.getWindow().getDecorView();
        int vis = decorView.getSystemUiVisibility();
        if (areDark) {
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decorView.setSystemUiVisibility(vis);
    }

    /**
     * 状态栏亮
     * <p>
     * 默 white
     *
     * @param appCompatActivity 活动
     * @param colorResId        颜色资源 ID
     */
    public static void statusBarLight(AppCompatActivity appCompatActivity, int colorResId) {
        StatusBarUtils.setColorNoTranslucent(appCompatActivity, ContextCompat.getColor(appCompatActivity, (colorResId == 0) ? R.color.white : colorResId));
        StatusBarUtils.statusBarTextColorDark(appCompatActivity, true);
    }

    /**
     * 状态栏暗
     * <p>
     * 默 purple_500
     *
     * @param appCompatActivity 活动
     * @param colorResId        颜色资源 ID
     */
    public static void statusBarDark(AppCompatActivity appCompatActivity, int colorResId) {
        StatusBarUtils.setColorNoTranslucent(appCompatActivity, ContextCompat.getColor(appCompatActivity, (colorResId == 0) ? R.color.purple_500 : colorResId));
        StatusBarUtils.statusBarTextColorDark(appCompatActivity, false);
    }
}