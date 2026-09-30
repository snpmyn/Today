package widget.toast

import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import pool.application.BasePoolApp

/**
 * Created on 2026/3/29.
 * @author 郑少鹏
 * @desc 吐司
 *
 * Java 中默认参数只能通过 Kotlin 提供的 @JvmOverloads 自动生成重载方法
 * 如果不加 @JvmOverloads
 * Java 必须手动传所有参数
 */
@Suppress("DEPRECATION")
@JvmOverloads
fun String.showToast(
    duration: Int = Toast.LENGTH_SHORT, gravity: Int? = null, xOffset: Int = 0, yOffset: Int = 0
) {
    val context = BasePoolApp.getBasePoolAppInstance()
    if (gravity != null) {
        // 传递了 gravity 时使用自定义 View 方式构造
        // 保证全版本 (含 Android 11+) 位置控制均可生效
        val textView = TextView(context).apply {
            text = this@showToast
            this.gravity = Gravity.CENTER
        }
        Toast(context).apply {
            this.duration = duration
            this.view = textView
            setGravity(gravity, xOffset, yOffset)
            show()
        }
    } else {
        Toast.makeText(context, this, duration).show()
    }
}

@Suppress("DEPRECATION")
@JvmOverloads
fun Int.showToast(
    duration: Int = Toast.LENGTH_SHORT, gravity: Int? = null, xOffset: Int = 0, yOffset: Int = 0
) {
    val context = BasePoolApp.getBasePoolAppInstance()
    if (gravity != null) {
        // 传递了 gravity 时使用自定义 View 方式构造
        // 保证全版本 (含 Android 11+) 位置控制均可生效
        val textView = TextView(context).apply {
            setText(this@showToast)
            this.gravity = Gravity.CENTER
        }
        Toast(context).apply {
            this.duration = duration
            this.view = textView
            setGravity(gravity, xOffset, yOffset)
            show()
        }
    } else {
        Toast.makeText(context, this, duration).show()
    }
}

@JvmOverloads
fun String.showCenterToast(duration: Int = Toast.LENGTH_SHORT) {
    showToast(duration = duration, gravity = Gravity.CENTER)
}

@JvmOverloads
fun Int.showCenterToast(duration: Int = Toast.LENGTH_SHORT) {
    showToast(duration = duration, gravity = Gravity.CENTER)
}