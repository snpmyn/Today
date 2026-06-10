package util.overlay

import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

/**
 * Created on 2026/6/10.
 * @author 郑少鹏
 * @desc 悬浮权限配套原件
 */
class OverlayPermissionKit {
    companion object {
        /**
         * 申请悬浮权限
         *
         * true - 已有权限
         * false - 跳设置页
         */
        fun requestOverlayPermission(appCompatActivity: AppCompatActivity): Boolean {
            return if (Settings.canDrawOverlays(appCompatActivity)) {
                true
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:${appCompatActivity.packageName}".toUri()
                )
                appCompatActivity.startActivity(intent)
                false
            }
        }
    }
}