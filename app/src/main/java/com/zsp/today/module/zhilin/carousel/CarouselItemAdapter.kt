package com.zsp.today.module.zhilin.carousel

import android.view.View
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import com.zsp.today.R
import com.zsp.today.module.zhilin.carousel.bean.CarouselBean
import util.glide.util.GlideUtils
import widget.carousel.three.CarouselAdapter

/**
 * Created on 2026/6/12.
 * @author 郑少鹏
 * @desc 轮播条目适配器
 */
class CarouselItemAdapter : CarouselAdapter<CarouselBean>() {
    /**
     * 当前选中位
     * 默认第一项
     */
    private var currentSelectPosition: Int = 0

    /**
     * 强迫外部业务子类去声明卡片独创的 XML 布局文件资源 ID
     *
     * @return 卡片独创的 XML 布局文件资源 ID
     */
    override fun getItemLayoutId(): Int {
        return R.layout.item_carousel
    }

    /**
     * 外部页面滑动时
     *
     * 同步更新当前的获焦位置并全局刷新视图
     */
    fun setCurrentSelectPosition(position: Int) {
        val oldPosition = this.currentSelectPosition
        if (oldPosition == position) {
            return
        }
        this.currentSelectPosition = position
        notifyItemChanged(oldPosition, PAYLOAD_TOGGLE_CARD)
        notifyItemChanged(position, PAYLOAD_TOGGLE_CARD)
    }

    /**
     * 绑定条目
     *
     * 强迫外部业务子类在其中进行 TextView、ImageView 等数据的加工与渲染填充
     *
     * @param holder   CarouselViewHolder
     * @param item     条目
     * @param position 位置
     */
    override fun onBindItem(
        holder: CarouselViewHolder, item: CarouselBean, position: Int
    ) {
        // 初始化控件
        val carouselItemSiv = holder.findViewById<ImageView>(R.id.carouselItemSiv)
        // 加载图片
        // 全量时才加载 + 滑行时不动它
        GlideUtils.loadByObject(holder.itemView.context, item.imageResId, carouselItemSiv)
        // 刷新卡片的标签与按钮状态
        bindCardStatus(holder, position)
    }

    /**
     * 绑定条目
     *
     * 提供一个空实现重载方法
     * 允许具体子类有选择重写
     *
     * @param holder   CarouselViewHolder
     * @param item     条目
     * @param position 位置
     * @param payloads List<Object>
    </Object> */
    override fun onBindItem(
        holder: CarouselViewHolder, item: CarouselBean, position: Int, payloads: List<Any?>
    ) {
        if (payloads.contains(PAYLOAD_TOGGLE_CARD)) {
            // 刷新卡片的标签与按钮状态
            bindCardStatus(holder, position)
        } else {
            // 没有特定标记，或者有其他刷新需求时，降级走全量刷新
            super.onBindItem(holder, item, position, payloads)
        }
    }

    /**
     * 刷新卡片的标签与按钮状态
     *
     * 由于数据不一样
     * 由统一方法处理
     */
    private fun bindCardStatus(holder: CarouselViewHolder, position: Int) {
        val carouselItemMb = holder.findViewById<MaterialButton>(R.id.carouselItemMb)
        // 是否当前聚焦
        val isCurrentFocus = (currentSelectPosition == position)
        // 放弃按钮 - 获焦控制
        carouselItemMb.isEnabled = isCurrentFocus
        carouselItemMb.isClickable = isCurrentFocus
        // 放弃按钮 - 显示 / 隐藏
        carouselItemMb.visibility = if (isCurrentFocus) View.VISIBLE else View.INVISIBLE
        carouselItemMb.alpha = if (isCurrentFocus) 1.0f else 0.0f
    }

    companion object {
        /**
         * 卡片切换的局部刷新特征标记
         */
        private const val PAYLOAD_TOGGLE_CARD = "payload_toggle_card"
    }
}