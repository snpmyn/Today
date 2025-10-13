package util.statusbar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat
import androidx.palette.graphics.Palette
import util.screen.ScreenUtils

/**
 * @decs: 状态栏色帮助者
 * @author: 郑少鹏
 * @date: 2025/10/5 18:40
 * @version: v 1.0
 */
object StatusBarColorHelper {
    /**
     * 设置背景图片资源
     */
    fun setBackgroundImageResource(
        appCompatActivity: AppCompatActivity,
        imageView: View,
        imageResource: Int,
        statusBarColorListener: StatusBarColorListener? = null
    ) {
        val bitmap = BitmapFactory.decodeResource(appCompatActivity.resources, imageResource)
        if (imageView is ImageView) {
            imageView.setImageBitmap(bitmap)
        }
        detectBitmapColor(appCompatActivity, bitmap, statusBarColorListener)
    }

    /**
     * 检测位图颜色
     */
    private fun detectBitmapColor(
        appCompatActivity: AppCompatActivity,
        bitmap: Bitmap,
        statusBarColorListener: StatusBarColorListener? = null
    ) {
        val left = 0
        val top = 0
        val right = ScreenUtils.screenWidth(appCompatActivity)
        val bottom = StatusBarUtils.getStatusBarHeight(appCompatActivity)
        Palette.from(bitmap).maximumColorCount(5).setRegion(left, top, right, bottom)
            .generate { palette ->
                palette?.let {
                    var mostPopularSwatch: Palette.Swatch? = null
                    for (swatch in it.swatches) {
                        if ((null == mostPopularSwatch) || (swatch.population > mostPopularSwatch.population)) {
                            mostPopularSwatch = swatch
                        }
                    }
                    mostPopularSwatch?.let { swatch ->
                        val luminance = ColorUtils.calculateLuminance(swatch.rgb)
                        if (luminance < 0.5) {
                            setDarkStatusBar(appCompatActivity)
                            statusBarColorListener?.onDark()
                        } else {
                            setLightStatusBar(appCompatActivity)
                            statusBarColorListener?.onLight()
                        }
                    }
                }
            }
    }

    /**
     * 设置浅色状态栏
     */
    private fun setLightStatusBar(appCompatActivity: AppCompatActivity) {
        val window = appCompatActivity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
    }

    /**
     * 设置深色状态栏
     */
    private fun setDarkStatusBar(appCompatActivity: AppCompatActivity) {
        val window = appCompatActivity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
    }

    /**
     * 状态栏色监听
     */
    interface StatusBarColorListener {
        /**
         * 浅色模式
         */
        fun onLight()

        /**
         * 深色模式
         */
        fun onDark()
    }
}