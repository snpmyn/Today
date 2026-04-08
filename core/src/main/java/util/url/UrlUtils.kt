package util.url

/**
 * Created on 2026/4/8.
 * @author 郑少鹏
 * @desc UrlUtils
 */
class UrlUtils {
    companion object {
        /**
         * Whether the url is a local one.
         *
         * @param url String
         * @return boolean
         */
        fun areLocal(url: String?): Boolean = url?.let {
            !url.startsWith("http://") && !url.startsWith("https://")
        } ?: false
    }
}