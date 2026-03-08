package widget.webview;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * Created on 2026/3/7.
 *
 * @author 郑少鹏
 * @desc WebView 配套原件
 */
public class WebViewKit {
    /**
     * 设置 WebChromeClient
     *
     * @param webView         WebView
     * @param materialToolbar MaterialToolbar
     */
    public static void setWebChromeClient(@NonNull WebView webView, MaterialToolbar materialToolbar) {
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                materialToolbar.setTitle(title);
            }
        };
        webView.setWebChromeClient(webChromeClient);
    }

    /**
     * 加载统一资源定位符
     *
     * @param webView WebView
     * @param url     统一资源定位符
     */
    public static void loadUrl(@NonNull WebView webView, String url) {
        webView.loadUrl(url);
    }
}