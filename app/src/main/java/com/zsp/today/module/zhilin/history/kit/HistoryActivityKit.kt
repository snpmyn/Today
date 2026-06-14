package com.zsp.today.module.zhilin.history.kit

import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.zsp.today.R
import util.list.ListUtils
import widget.carousel.one.CarouselItem
import widget.carousel.one.CarouselKit
import widget.carousel.one.CarouselType
import widget.screen.kit.ScreenHandleKit
import widget.screen.listener.ScreenHandleListener

/**
 * Created on 2026/2/27.
 * @author 郑少鹏
 * @desc 历史页配套原件
 */
class HistoryActivityKit {
    private var carouselKit: CarouselKit? = null
    private var defaultSelectCondition: String? = null
    private val conditionTemporary: Array<String?> = arrayOfNulls(1)
    private val carouselItemList: MutableList<CarouselItem> = prepareCarouselItemList()
    private val carouselItemCarouselTitleList: MutableList<String?> =
        prepareCarouselItemCarouselTitleList()

    /**
     * 轮播
     * @param recyclerView RecyclerView
     */
    fun carousel(recyclerView: RecyclerView) {
        carouselKit = CarouselKit()
        // 执行
        carouselKit!!.execute(
            recyclerView,
            carouselItemList,
            FullScreenCarouselStrategy(),
            RecyclerView.VERTICAL,
            false,
            CarouselLayoutManager.ALIGNMENT_CENTER,
            false,
            0,
            0,
            0,
            8,
            false,
            null
        )
        // 监听当前位置
        carouselKit!!.observeCurrentPosition(
            recyclerView
        ) { position: Int ->
            defaultSelectCondition = carouselItemCarouselTitleList[position]
        }
    }

    /**
     * 显示历史要点列表
     * @param appCompatActivity 活动
     * @param recyclerView      RecyclerView
     */
    fun showHistoryPointList(appCompatActivity: AppCompatActivity, recyclerView: RecyclerView) {
        // ScreenHandleKit
        val screenHandleKit = ScreenHandleKit(appCompatActivity)
        // 打包集合条件
        screenHandleKit.packListConditions(
            appCompatActivity.getString(R.string.interviewPoint),
            3,
            true,
            carouselItemCarouselTitleList
        )
        // 默选
        screenHandleKit.defaultSelect(
            appCompatActivity.getString(R.string.interviewPoint),
            if (TextUtils.isEmpty(defaultSelectCondition)) carouselItemCarouselTitleList[0] else defaultSelectCondition
        )
        // 关联
        screenHandleKit.associate()
        // 设筛选操作监听
        screenHandleKit.setScreenHandleListener(object : ScreenHandleListener {
            override fun click(
                view: View?, classification: String?, condition: String?, selected: Boolean
            ) {
                conditionTemporary[0] = condition
            }

            override fun reset() {
                screenHandleKit.reset()
            }

            override fun ensure() {
                screenHandleKit.dismiss()
                defaultSelectCondition = conditionTemporary[0]
                carouselKit!!.scrollToPosition(
                    recyclerView,
                    ListUtils.getTargetIndex(carouselItemCarouselTitleList, conditionTemporary[0]),
                    false
                )
            }
        })
        // 显示
        screenHandleKit.show()
    }

    /**
     * 准备轮播条目集
     * @return 轮播条目集
     */
    private fun prepareCarouselItemList(): MutableList<CarouselItem> {
        val carouselItemList: MutableList<CarouselItem> = ArrayList(2)
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.history_point_1, "事件\n时序", "事件时序"
            )
        )
        return carouselItemList
    }

    /**
     * 准备轮播条目轮播标题集
     * @return 轮播条目轮播标题集
     */
    private fun prepareCarouselItemCarouselTitleList(): MutableList<String?> {
        val carouselItemCarouselTitleList: MutableList<String?> = ArrayList(carouselItemList.size)
        for (carouselItem in carouselItemList) {
            carouselItemCarouselTitleList.add(carouselItem.getCarouselTitle())
        }
        return carouselItemCarouselTitleList
    }
}