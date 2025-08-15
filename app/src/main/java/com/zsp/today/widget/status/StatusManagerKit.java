package com.zsp.today.widget.status;

import java.util.List;

import util.list.ListUtils;
import widget.status.manager.StatusManager;

/**
 * Created on 2021/2/10
 *
 * @author zsp
 * @desc 状态管理器配套元件
 */
public class StatusManagerKit {
    /**
     * 状态判断
     *
     * @param statusManager 状态管理器
     * @param list          集合
     */
    public static void statusJudge(StatusManager statusManager, List<?> list) {
        if (ListUtils.listIsEmpty(list)) {
            statusManager.showEmpty();
            return;
        }
        statusManager.showContent();
    }
}