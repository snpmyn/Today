package widget.status.kit;

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
     * @param needLoad      需加载
     * @param list          集合
     */
    public static void statusJudge(StatusManager statusManager, boolean needLoad, List<?> list) {
        if (needLoad) {
            statusManager.showLoading();
            return;
        }
        if (ListUtils.listIsEmpty(list)) {
            statusManager.showEmpty();
            return;
        }
        statusManager.showContent();
    }
}