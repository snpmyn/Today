package com.zsp.today.module.zhilin.carousel.kit

import com.zsp.today.R
import com.zsp.today.databinding.ActivityCarouselBinding
import com.zsp.today.module.zhilin.carousel.adapter.CarouselItemAdapter
import com.zsp.today.module.zhilin.carousel.bean.CarouselBean
import widget.carousel.two.CarouselView
import widget.toast.showToast

/**
 * Created on 2026/6/12.
 * @author 郑少鹏
 * @desc 轮播页配套原件
 */
class CarouselActivityKit {
    fun execute(activityCarouselBinding: ActivityCarouselBinding) {
        val list = listOf(
            CarouselBean(R.drawable.banner_13),
            CarouselBean(R.drawable.banner_13),
            CarouselBean(R.drawable.banner_13),
            CarouselBean(R.drawable.banner_13),
            CarouselBean(R.drawable.banner_13),
            CarouselBean(R.drawable.banner_13),
            CarouselBean(R.drawable.banner_13),
        )
        // 轮播条目适配器
        val carouselItemAdapter = CarouselItemAdapter()
        carouselItemAdapter.addCarouselData(list)
        carouselItemAdapter.setOnItemClickListener { _, _, _ ->
            "点击".showToast()
        }
        activityCarouselBinding.carouselActivityCv.setOnPageChangeListener {
            "选中".showToast()
        }
        // CarouselView
        activityCarouselBinding.carouselActivityCv.setCarouselAdapter(carouselItemAdapter)
        activityCarouselBinding.carouselActivityCv.setScrollSpringiness(2.0f)
        // 控件点击事件
        activityCarouselBinding.carouselActivityMbFirstItem.setOnClickListener {
            activityCarouselBinding.carouselActivityCv.setCurrentItem(0, true)
            "头条".showToast()
        }
        activityCarouselBinding.carouselActivityMbPreviousItem.setOnClickListener {
            activityCarouselBinding.carouselActivityCv.scrollPrevious()
            "上张".showToast()
        }
        activityCarouselBinding.carouselActivityMbPause.setOnClickListener {
            activityCarouselBinding.carouselActivityCv.pauseAutoScroll()
            "暂停".showToast()
        }
        activityCarouselBinding.carouselActivityMbTurnLeft.setOnClickListener {
            activityCarouselBinding.carouselActivityCv.setAutoScrollDirection(CarouselView.DIRECTION_FORWARD)
            "左转".showToast()
        }
        activityCarouselBinding.carouselActivityMbTurnRight.setOnClickListener {
            activityCarouselBinding.carouselActivityCv.setAutoScrollDirection(CarouselView.DIRECTION_BACKWARD)
            "右转".showToast()
        }
        activityCarouselBinding.carouselActivityMbResume.setOnClickListener {
            activityCarouselBinding.carouselActivityCv.resumeAutoScroll()
            "恢复".showToast()
        }
        activityCarouselBinding.carouselActivityMbNextItem.setOnClickListener {
            activityCarouselBinding.carouselActivityCv.scrollNext()
            "下张".showToast()
        }
        activityCarouselBinding.carouselActivityMbEndItem.setOnClickListener {
            activityCarouselBinding.carouselActivityCv.setCurrentItem(list.size - 1, true)
            "末条".showToast()
        }
    }
}