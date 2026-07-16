package com.zsp.today.module.zhilin.carousel.adapter

import android.view.View
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import com.zsp.today.R
import com.zsp.today.module.zhilin.carousel.bean.CarouselBean
import util.glide.util.GlideUtils
import widget.carousel.two.CarouselAdapter

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
     * 获取条目布局 ID
     *
     * 强迫外部业务子类去声明卡片独创的 XML 布局文件资源 ID
     *
     * @return 条目布局 ID
     */
    override fun getItemLayoutId(): Int {
        return R.layout.item_carousel
    }

    /**
     * 绑定条目
     *
     * 强迫外部业务子类在其中进行 TextView、ImageView 等数据的加工与渲染填充
     *
     * @param carouselViewHolder 轮播视图持有者
     * @param item               条目
     * @param position           位置
     */
    override fun onBindItem(
        carouselViewHolder: CarouselViewHolder, item: CarouselBean, position: Int
    ) {
        // 初始化控件
        val carouselItemSiv = carouselViewHolder.findViewById<ImageView>(R.id.carouselItemSiv)
        // 加载图片
        // 全量时才加载 + 滑行时不动它
        GlideUtils.loadByObject(
            carouselViewHolder.itemView.context, item.imageResId, carouselItemSiv
        )
        // 刷新卡片的标签与按钮状态
        bindCardStatus(carouselViewHolder, position)
    }

    /**
     * 绑定条目
     *
     * 提供一个空实现重载方法
     * 允许具体子类有选择重写
     *
     * 如果子类 Kotlin 实现
     * 为防止 Kotlin 只读协变 List 映射为字节码后与 Java 不型变泛型不一致导致多态失效、局部刷新降级
     * 必须强制声明为 List<Any>
     * 严格对齐 Java 字节码 List<Object>
     *
     * @param carouselViewHolder 轮播视图持有者
     * @param item               条目
     * @param position           位置
     * @param payloads           局部刷新标识集
     */
    override fun onBindItem(
        carouselViewHolder: CarouselViewHolder,
        item: CarouselBean,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.contains(PAYLOAD_TOGGLE_CARD)) {
            // 刷新卡片的标签与按钮状态
            bindCardStatus(carouselViewHolder, position)
        } else {
            // 无特定标记或其它刷新需求 -> 降级走全量刷新
            super.onBindItem(carouselViewHolder, item, position, payloads)
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
        // 按钮 - 获焦控制
        carouselItemMb.isEnabled = isCurrentFocus
        carouselItemMb.isClickable = isCurrentFocus
        // 按钮 - 显示 / 隐藏
        carouselItemMb.visibility = if (isCurrentFocus) View.VISIBLE else View.INVISIBLE
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

    companion object {
        /**
         * 卡片切换局部刷新标识
         */
        private const val PAYLOAD_TOGGLE_CARD = "payload_toggle_card"
    }
}