package com.zsp.today.module.zhilin.bean

/**
 * Created on 2026/3/27.
 * @author 郑少鹏
 * @desc 知林菜单动作
 */
sealed class ZhiLinMenuAction {
    object TabLayout : ZhiLinMenuAction()
    object RxJava : ZhiLinMenuAction()
    object Android : ZhiLinMenuAction()
    object Interview : ZhiLinMenuAction()
    object CustomView : ZhiLinMenuAction()
    object Mvp : ZhiLinMenuAction()
    object History : ZhiLinMenuAction()

    companion object {
        fun getMenuIdFromZhiLinMenuAction(zhiLinMenuAction: ZhiLinMenuAction): Int {
            return when (zhiLinMenuAction) {
                is TabLayout -> 1
                is RxJava -> 2
                is Android -> 3
                is Interview -> 4
                is CustomView -> 5
                is Mvp -> 6
                is History -> 7
            }
        }
    }
}