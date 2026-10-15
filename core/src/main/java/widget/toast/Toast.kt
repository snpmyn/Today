package widget.toast

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt
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
        val density = context.resources.displayMetrics.density
        // 传递了 gravity 时使用自定义 View 方式构造
        // 保证全版本 (含 Android 11+) 位置控制均可生效
        val textView = TextView(context).apply {
            text = this@showToast
            this.gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            textSize = 12f
            background = GradientDrawable().apply {
                setColor("#B3000000".toColorInt())
                cornerRadius = 8f * density + 0.5f
            }
            val paddingHorizontal = (16f * density + 0.5f).toInt()
            val paddingVertical = (10f * density + 0.5f).toInt()
            setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
        }
        // 使用外层 LinearLayout 包裹，确保 LayoutParams 和 gravity 正常生效，解决左右不居中问题。
        // 在 Android 中，直接把一个单独的 TextView 设给 Toast.view，在部分国产定制系统或高版本上，由于 LayoutParams 默认会被拉伸或对齐方式丢失，经常会导致整个 Toast 靠左或靠右。
        // 外层加上这个 LinearLayout 就能强制约束宽度并保证其内部视图严格居中。
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setGravity(Gravity.CENTER)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            addView(textView)
        }
        Toast(context).apply {
            this.duration = duration
            this.view = container
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
        val density = context.resources.displayMetrics.density
        // 传递了 gravity 时使用自定义 View 方式构造
        // 保证全版本 (含 Android 11+) 位置控制均可生效
        val textView = TextView(context).apply {
            setText(this@showToast)
            this.gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            textSize = 12f
            background = GradientDrawable().apply {
                setColor("#B3000000".toColorInt())
                cornerRadius = 8f * density + 0.5f
            }
            val paddingHorizontal = (16f * density + 0.5f).toInt()
            val paddingVertical = (10f * density + 0.5f).toInt()
            setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
        }
        // 使用外层 LinearLayout 包裹，确保 LayoutParams 和 gravity 正常生效，解决左右不居中问题。
        // 在 Android 中，直接把一个单独的 TextView 设给 Toast.view，在部分国产定制系统或高版本上，由于 LayoutParams 默认会被拉伸或对齐方式丢失，经常会导致整个 Toast 靠左或靠右。
        // 外层加上这个 LinearLayout 就能强制约束宽度并保证其内部视图严格居中。
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setGravity(Gravity.CENTER)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            addView(textView)
        }
        Toast(context).apply {
            this.duration = duration
            this.view = container
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