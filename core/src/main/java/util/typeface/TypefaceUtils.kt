package util.typeface

import android.content.Context
import android.graphics.Typeface
import java.util.concurrent.ConcurrentHashMap

/**
 * Created on 2026/4/8.
 * @author 郑少鹏
 * @desc Typeface 工具类
 *
 * 字体加载是比较重的 IO 操作
 * 建议缓存
 */
object TypefaceUtils {
    /**
     * Typeface 缓存集合
     *
     * ConcurrentHashMap
     * 线程安全 + 可修改
     */
    private var typefaceCacheMap = ConcurrentHashMap<String, Typeface>()

    /**
     * 获取 Typeface
     */
    private fun getTypeface(context: Context, path: String): Typeface {
        return typefaceCacheMap[path] ?: Typeface.createFromAsset(context.assets, path)
            .also { typefaceCacheMap[path] = it }
    }

    /**
     * SansBold
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansBold(context: Context): Typeface {
        return getTypeface(context, "typeface/OpenSans-Bold.ttf")
    }

    /**
     * SansBoldItalic
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansBoldItalic(context: Context): Typeface {
        return getTypeface(context, "typeface/OpenSans-BoldItalic.ttf")
    }

    /**
     * SansExtraBold
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansExtraBold(context: Context): Typeface {
        return getTypeface(context, "typeface/OpenSans-ExtraBold.ttf")
    }

    /**
     * SansExtraBoldItalic
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansExtraBoldItalic(context: Context): Typeface {
        return getTypeface(
            context, "typeface/OpenSans-ExtraBoldItalic.ttf"
        )
    }

    /**
     * SansItalic
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansItalic(context: Context): Typeface {
        return getTypeface(context, "typeface/OpenSans-Italic.ttf")
    }

    /**
     * SansLight
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansLight(context: Context): Typeface {
        return getTypeface(context, "typeface/OpenSans-Light.ttf")
    }

    /**
     * SansLightItalic
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansLightItalic(context: Context): Typeface {
        return getTypeface(context, "typeface/OpenSans-LightItalic.ttf")
    }

    /**
     * SansRegular
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansRegular(context: Context): Typeface {
        return getTypeface(context, "typeface/OpenSans-Regular.ttf")
    }

    /**
     * SansSemibold
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansSemibold(context: Context): Typeface {
        return getTypeface(context, "typeface/OpenSans-Semibold.ttf")
    }

    /**
     * SansSemiboldItalic
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun sansSemiboldItalic(context: Context): Typeface {
        return getTypeface(context, "typeface/OpenSans-SemiboldItalic.ttf")
    }

    /**
     * LiuJianMaoCaoRegular
     *
     * @param context 上下文
     * @return Typeface
     */
    @JvmStatic
    fun liuJianMaoCaoRegular(context: Context): Typeface {
        return getTypeface(context, "typeface/LiuJianMaoCao-Regular.ttf")
    }
}