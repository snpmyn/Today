package com.zsp.today.module.zhilin.carousel.adapter

import android.widget.ImageView
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
     * 强迫外部业务子类去声明卡片独创的 XML 布局文件资源 ID
     * @return 卡片独创的 XML 布局文件资源 ID
     */
    override fun getItemLayoutId(): Int {
        return R.layout.item_carousel
    }

    /**
     * 强迫外部业务子类在其中进行 TextView、ImageView 等数据的加工与渲染填充
     * @param holder   CarouselViewHolder
     * @param item     条目
     * @param position 位置
     */
    override fun onBindItem(holder: CarouselViewHolder, item: CarouselBean, position: Int) {
        val carouselItemSiv = holder.findViewById<ImageView>(R.id.carouselItemSiv)
        GlideUtils.loadByObject(holder.itemView.context, item.imageResId, carouselItemSiv)
    }
}