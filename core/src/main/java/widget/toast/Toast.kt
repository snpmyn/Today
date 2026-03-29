package widget.toast

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
@JvmOverloads
fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(BasePoolApp.getBasePoolAppInstance(), this, duration).show()
}

@JvmOverloads
fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(BasePoolApp.getBasePoolAppInstance(), this, duration).show()
}