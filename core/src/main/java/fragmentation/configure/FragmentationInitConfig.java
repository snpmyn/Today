package fragmentation.configure;

import fragmentation.fragmentation.Fragmentation;

/**
 * Created on 2019/1/16.
 *
 * @author 郑少鹏
 * @desc Fragmentation 初始化配置
 */
public class FragmentationInitConfig {
    /**
     * 初始化 Fragmentation
     *
     * @param debug 调试否
     */
    public static void initFragmentation(Boolean debug) {
        // 建于 Application 初始化
        // 更多查看 wiki 或 demo
        Fragmentation.builder()
                // 栈视图模式默悬浮球模式
                // SHAKE 摇一摇唤出、NONE 隐藏
                // 仅 Debug 环境生效
                .stackViewMode(Fragmentation.BUBBLE)
                // 测试场景 .debug(true)
                // 实际场景 .debug(false)
                .debug(debug)
                // 可获 {@link me.yokeyword.fragmentation.exception.AfterSaveStateTransactionWarning}
                // 遇 After onSaveInstanceState 不抛异常而回调至下 ExceptionHandler
                .handleException(e -> {
                    // 以 Bugtags 为例（传捕获 Exception 至 Bugtags 后台）
                    // Bugtags.sendException(e);
                }).install();
    }
}
