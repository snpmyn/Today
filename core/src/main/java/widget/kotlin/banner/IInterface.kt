package widget.kotlin.banner

import android.content.Context
import android.view.View

/**
 * @decs: IBannerView
 * @author: 郑少鹏
 * @date: 2019/8/19 16:26
 */
interface IBannerView : OnPageChangeListener, IBannerViewBase {
    /**
     * count 为 0 时默视图
     */
    fun getDefaultView(context: Context): View? {
        return null
    }

    /**
     * 默关自滚
     */
    fun isDefaultAutoScroll(): Boolean {
        return false
    }

    override fun onPageSelected(position: Int) {}
}

/**
 * 定义页切调
 */
interface OnPageChangeListener {
    fun onPageSelected(position: Int)
}

interface IBannerViewBase {
    fun getCount(): Int
    fun getItemView(context: Context): View
    fun onBindView(itemView: View, position: Int)
}

/**
 * 定义 BannerView 实例接口
 */
interface IBannerViewInstance : IBannerViewBase {
    fun getContext(): Context
    fun isSmoothMode(): Boolean
    fun getItemViewWidth(): Int
    fun getItemViewMargin(): Int
    fun getItemViewAlign(): Int
}

/**
 * PagerView 实例需实现接口
 */
interface IPagerViewInstance {
    /**
     * 自滚
     * @param intervalInMillis: Int INTERVAL 模式为页切间隔；SMOOTH 模式为滚一页需时。
     */
    fun startAutoScroll(intervalInMillis: Int)
    fun stopAutoScroll()
    fun getCurrentPosition(): Int
    fun getRealCurrentPosition(realCount: Int): Int
    fun setSmoothMode(enabled: Boolean)
    fun setPageHoldInMillis(pageHoldInMillis: Int)
    fun setOnPageChangeListener(listener: OnPageChangeListener)
    fun notifyDataSetChanged()
}

/**
 * 指示器实例需实现接口
 */
interface IIndicatorInstance {
    /**
     * 外实现
     */
    fun setIndicator(impl: IIndicator)

    /**
     * 重布局
     */
    fun doRequestLayout()

    /**
     * 重绘
     */
    fun doInvalidate()
}

/**
 * 指示器依赖外实现
 */
interface IIndicator {
    /**
     * 获适配器总数
     */
    fun getCount(): Int

    /**
     * 获当前选中页索引
     */
    fun getCurrentIndex(): Int
}

/**
 * PagerView 工厂接口
 */
interface IPagerViewFactory {
    fun getPagerView(): IPagerViewInstance
}