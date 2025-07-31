@file:JvmName("DrawKit")

package widget.kotlin.banner

import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt

/**
 * @decs: DrawKit
 * @author: 郑少鹏
 * @date: 2019/8/20 12:13
 */
/**
 * 创画笔
 */
@JvmOverloads
internal fun createPaint(colorString: String? = null, @ColorInt color: Int? = null): Paint {
    return Paint().apply {
        this.resetPaint(colorString, color)
    }
}

/**
 * 重置画笔
 */
@JvmOverloads
internal fun Paint.resetPaint(colorString: String? = null, @ColorInt color: Int? = null) {
    this.reset()
    // 默白色（处理系统渲染抗锯齿时人眼观察到像素色）
    this.color = color ?: (colorString ?: "#FFFFFF").toColorInt()
    this.isAntiAlias = true
    this.style = Paint.Style.FILL
    this.strokeWidth = 0.0F
}

/**
 * FlagSet 添 Flag
 */
internal fun Int.addFlag(flag: Int): Int {
    return this or flag
}

/**
 * FlagSet 移 Flag
 */
internal fun Int.removeFlag(flag: Int): Int {
    return this and (flag.inv())
}

/**
 * FlagSet 含 Flag
 */
internal fun Int.containFlag(flag: Int): Boolean {
    return this or flag == this
}
