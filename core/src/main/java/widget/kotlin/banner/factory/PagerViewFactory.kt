package widget.kotlin.banner.factory

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zsp.core.R
import widget.kotlin.banner.IBannerViewInstance
import widget.kotlin.banner.IPagerViewFactory
import widget.kotlin.banner.IPagerViewInstance
import widget.kotlin.banner.pager.PagerRecyclerView

/**
 * @decs: PagerView 工厂
 * @author: 郑少鹏
 * @date: 2019/8/19 16:08
 */
internal class PagerViewFactory(
    private val bannerView: IBannerViewInstance, private val intervalUseViewPager: Boolean = false
) : IPagerViewFactory {
    /**
     * 据参创对应 PagerView 实例
     */
    override fun getPagerView(): IPagerViewInstance {
        return if (bannerView.isSmoothMode()) {
            casePagerRecycler(true)
        } else {
            if (intervalUseViewPager) {
                // 此处可据需用 ViewPager 作底层实现
                throw IllegalStateException(
                    bannerView.getContext()
                        .getString(R.string.bannerViewPagerIsNotUsedAsTheUnderlyingImplementationHere)
                )
            } else {
                casePagerRecycler(false)
            }
        }
    }

    /**
     * 处理 PagerRecyclerView
     */
    private fun casePagerRecycler(isSmoothMode: Boolean): IPagerViewInstance {
        val recyclerView = PagerRecyclerView(bannerView.getContext())
        recyclerView.layoutManager =
            LinearLayoutManager(bannerView.getContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun getItemCount(): Int {
                return Int.MAX_VALUE
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (!isActivityDestroyed(holder.itemView.context)) {
                    val realPos = position % bannerView.getCount()
                    bannerView.onBindView(
                        holder.itemView.findViewById(R.id.bannerViewRealItem), realPos
                    )
                }
            }

            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): RecyclerView.ViewHolder {
                val itemWrapper = LayoutInflater.from(parent.context).inflate(
                    R.layout.banner_view_item, parent, false
                ) as RelativeLayout
                // 处理 ItemWrapperView 宽
                itemWrapper.layoutParams.width =
                    bannerView.getItemViewWidth() + bannerView.getItemViewMargin()
                // 外部实际 ItemView
                val itemView = bannerView.getItemView(parent.context)
                itemView.id = R.id.bannerViewRealItem
                val ivParams = RelativeLayout.LayoutParams(
                    bannerView.getItemViewWidth(), ViewGroup.LayoutParams.MATCH_PARENT
                )
                ivParams.addRule(bannerView.getItemViewAlign())
                // 添 ItemView 至 Wrapper
                itemWrapper.addView(itemView, ivParams)
                return object : RecyclerView.ViewHolder(itemWrapper) {}
            }
        }
        // 初始位
        recyclerView.scrollToPosition(bannerView.getCount() * 100)
        recyclerView.setSmoothMode(isSmoothMode)
        return recyclerView
    }

    private fun isActivityDestroyed(context: Context?): Boolean {
        if (null == context) return true
        if (context !is Activity) {
            throw IllegalStateException(context.getString(R.string.bannerContextCorrespondsToTheActivityInstance))
        }
        if (context.isFinishing || context.isDestroyed) {
            return true
        }
        return false
    }
}