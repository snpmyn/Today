package com.zsp.today.module.zhilin.bean

/**
 * Created on 2026/3/27.
 * @author 郑少鹏
 * @desc 知林菜单动作
 */
sealed class ZhiLinMenuAction {
    object TabLayout : ZhiLinMenuAction()
    object RxJava : ZhiLinMenuAction()
    object Compose : ZhiLinMenuAction()
    object Interview : ZhiLinMenuAction()
    object CustomView : ZhiLinMenuAction()
    object Mvp : ZhiLinMenuAction()
    object History : ZhiLinMenuAction()
    object COLLABORATION : ZhiLinMenuAction()
    object FLOATING : ZhiLinMenuAction()
    object OVERLAY : ZhiLinMenuAction()
    object CAROUSEL : ZhiLinMenuAction()

    companion object {
        fun getMenuIdFromZhiLinMenuAction(zhiLinMenuAction: ZhiLinMenuAction): Int {
            return when (zhiLinMenuAction) {
                is TabLayout -> 1
                is RxJava -> 2
                is Compose -> 3
                is Interview -> 4
                is CustomView -> 5
                is Mvp -> 6
                is History -> 7
                is COLLABORATION -> 8
                is FLOATING -> 9
                is OVERLAY -> 10
                is CAROUSEL -> 11
            }
        }
    }
}