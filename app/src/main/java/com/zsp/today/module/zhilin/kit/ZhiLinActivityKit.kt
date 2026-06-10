package com.zsp.today.module.zhilin.kit

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.zsp.today.module.zhilin.bean.ZhiLinMenuAction
import com.zsp.today.module.zhilin.bean.ZhiLinMenuEnum
import com.zsp.today.module.zhilin.collaboration.CollaborationActivity
import com.zsp.today.module.zhilin.compose.ComposeActivity
import com.zsp.today.module.zhilin.customview.CustomViewActivity
import com.zsp.today.module.zhilin.floating.FloatingActivity
import com.zsp.today.module.zhilin.history.HistoryActivity
import com.zsp.today.module.zhilin.interview.InterviewActivity
import com.zsp.today.module.zhilin.mvp.MvpActivity
import com.zsp.today.module.zhilin.rxjava.RxJavaActivity
import com.zsp.today.module.zhilin.tablayout.TabLayoutActivity
import widget.adapttemplate.bean.MenuBean
import widget.adapttemplate.kit.MenuAdapterKit
import widget.status.kit.StatusManagerKit
import widget.status.manager.StatusManager
import widget.transition.kit.TransitionKit

/**
 * Created on 2026/3/27.
 * @author 郑少鹏
 * @desc 知林页配套原件
 */
class ZhiLinActivityKit {
    /**
     * 展示
     *
     * @param appCompatActivity 活动
     * @param recyclerView      控件
     * @param spanCount         跨距数
     * @param space             间距
     * @param totalMargin       总外边距
     * @param statusManager     状态管理器
     */
    fun display(
        appCompatActivity: AppCompatActivity,
        recyclerView: RecyclerView,
        spanCount: Int,
        space: Int,
        totalMargin: Int,
        statusManager: StatusManager
    ) {
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, true, null)
        // 获取组件菜单图标资源 ID 集
        val zhiLinMenuEnums = ZhiLinMenuEnum.entries.toTypedArray()
        // 获取菜单集
        val menuBeanList: MutableList<MenuBean?> = ArrayList(zhiLinMenuEnums.size)
        for (zhiLinMenuEnum in zhiLinMenuEnums) {
            if (zhiLinMenuEnum.menuShow) {
                menuBeanList.add(
                    MenuBean(
                        ZhiLinMenuAction.getMenuIdFromZhiLinMenuAction(zhiLinMenuEnum.zhiLinMenuAction),
                        zhiLinMenuEnum.menuIconResId,
                        zhiLinMenuEnum.menuName
                    )
                )
            }
        }
        // 状态判断
        StatusManagerKit.statusJudge(statusManager, false, menuBeanList)
        // 菜单适配器配套元件
        val menuAdapterKit = MenuAdapterKit()
        menuAdapterKit.display(
            appCompatActivity, recyclerView, menuBeanList, spanCount, space, totalMargin, false
        ) { view: View?, menuBean: MenuBean? ->
            distribute(
                appCompatActivity, view, menuBean!!.menuId
            )
        }
    }

    /**
     * 分发
     *
     * @param appCompatActivity 活动
     * @param view              视图
     * @param menuId            菜单 ID
     */
    private fun distribute(appCompatActivity: AppCompatActivity, view: View?, menuId: Int) {
        when (menuId) {
            1 -> {
                val fromThisToTabLayoutActivityIntent =
                    Intent(appCompatActivity, TabLayoutActivity::class.java)
                TransitionKit.getInstance().jumpWithTransition(
                    appCompatActivity, view, fromThisToTabLayoutActivityIntent, false
                )
            }

            2 -> {
                val fromThisToRxJavaActivityIntent =
                    Intent(appCompatActivity, RxJavaActivity::class.java)
                TransitionKit.getInstance().jumpWithTransition(
                    appCompatActivity, view, fromThisToRxJavaActivityIntent, false
                )
            }

            3 -> {
                val fomThisToComposeActivityIntent =
                    Intent(appCompatActivity, ComposeActivity::class.java)
                TransitionKit.getInstance().jumpWithTransition(
                    appCompatActivity, view, fomThisToComposeActivityIntent, false
                )
            }

            4 -> {
                val fromThisToInterviewActivityIntent =
                    Intent(appCompatActivity, InterviewActivity::class.java)
                TransitionKit.getInstance().jumpWithTransition(
                    appCompatActivity, view, fromThisToInterviewActivityIntent, false
                )
            }

            5 -> {
                val fromThisToCustomViewActivityIntent =
                    Intent(appCompatActivity, CustomViewActivity::class.java)
                TransitionKit.getInstance().jumpWithTransition(
                    appCompatActivity, view, fromThisToCustomViewActivityIntent, false
                )
            }

            6 -> {
                val fromThisToMvpActivityIntent = Intent(appCompatActivity, MvpActivity::class.java)
                TransitionKit.getInstance()
                    .jumpWithTransition(appCompatActivity, view, fromThisToMvpActivityIntent, false)
            }

            7 -> {
                val fromThisToHistoryActivityIntent =
                    Intent(appCompatActivity, HistoryActivity::class.java)
                TransitionKit.getInstance().jumpWithTransition(
                    appCompatActivity, view, fromThisToHistoryActivityIntent, false
                )
            }

            8 -> {
                val fromThisToCollaborationActivityIntent =
                    Intent(appCompatActivity, CollaborationActivity::class.java)
                TransitionKit.getInstance().jumpWithTransition(
                    appCompatActivity, view, fromThisToCollaborationActivityIntent, false
                )
            }

            9 -> {
                val fromThisToFloatingActivityIntent =
                    Intent(appCompatActivity, FloatingActivity::class.java)
                TransitionKit.getInstance().jumpWithTransition(
                    appCompatActivity, view, fromThisToFloatingActivityIntent, false
                )
            }

            else -> {}
        }
    }
}