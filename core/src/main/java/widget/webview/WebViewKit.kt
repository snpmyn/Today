package widget.webview

import android.webkit.WebChromeClient
import android.webkit.WebView
import com.google.android.material.appbar.MaterialToolbar

/**
 * Created on 2026/3/30.
 * @author 郑少鹏
 * @desc WebView 配套原件
 */
class WebViewKit {
    companion object {
        /**
         * 设置 WebChromeClient
         *
         * @param webView         WebView
         * @param materialToolbar MaterialToolbar
         */
        fun setWebChromeClient(webView: WebView, materialToolbar: MaterialToolbar) {
            val webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    val safeTitle = title?.takeIf { title.isNotBlank() } ?: return
                    materialToolbar.setTitle(safeTitle)
                }
            }
            webView.webChromeClient = webChromeClient
        }

        /**
         * 加载统一资源定位符
         *
         * @param webView WebView
         * @param url     统一资源定位符
         */
        fun loadUrl(webView: WebView?, url: String?) {
            webView?.let { webView ->
                val safeUrl = url ?: return
                webView.loadUrl(safeUrl)
            }
        }
    }
}