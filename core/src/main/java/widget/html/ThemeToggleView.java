package widget.html;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/**
 * @decs: 主题切换视图
 * @author: 郑少鹏
 * @date: 2025/10/13 14:13
 * @version: v 1.0
 * <p>
 * 暂无引用
 * <p>
 * 垂直方向居中
 * <p>
 * 水平方向偏左
 * android:paddingStart="@dimen/dp_8"
 */
public class ThemeToggleView extends FrameLayout {
    private final WebView webView;
    private OnToggleChangeListener onToggleChangeListener;

    public ThemeToggleView(Context context) {
        super(context);
        webView = createWebView(context);
        addView(webView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public ThemeToggleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        webView = createWebView(context);
        addView(webView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public ThemeToggleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        webView = createWebView(context);
        addView(webView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @NonNull
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private WebView createWebView(Context context) {
        WebView webView = new WebView(context);
        WebSettings settings = webView.getSettings();
        // 必须启用 JS
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        // 禁止本地文件相互访问
        settings.setAllowFileAccessFromFileURLs(false);
        // 禁止本地文件访问外部资源
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(LAYER_TYPE_SOFTWARE, null);
        // 限制加载来源
        // 只允许 file://
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return !"file".equals(request.getUrl().getScheme());
            }
        });
        // JS 回调接口
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void onThemeToggled(final boolean areChecked) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (onToggleChangeListener != null) {
                        onToggleChangeListener.onToggleChange(areChecked);
                    }
                });
            }
        }, "Android");
        // 加载本地 HTML
        webView.loadUrl("file:///android_asset/html/ThemeToggle.html");
        return webView;
    }

    /**
     * 设置切换变化监听
     *
     * @param onToggleChangeListener 切换变化监听
     */
    public void setOnToggleChangeListener(OnToggleChangeListener onToggleChangeListener) {
        this.onToggleChangeListener = onToggleChangeListener;
    }

    /**
     * 设置选中
     *
     * @param checked 选中否
     */
    public void setChecked(boolean checked) {
        webView.evaluateJavascript("document.getElementById('toggle').checked = " + checked + ";", null);
    }

    /**
     * 切换变化监听
     */
    public interface OnToggleChangeListener {
        /**
         * 切换变化
         *
         * @param areChecked 选中否
         */
        void onToggleChange(boolean areChecked);
    }
}