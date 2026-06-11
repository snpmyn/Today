package widget.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * Created on 2026/6/11.
 *
 * @author 郑少鹏
 * @desc 跨 APP 双向通信广播助手
 * <p>
 * 依赖 AndroidManifest.xml 静态注册托底
 * 无论应用在前台、后台还是处于无后台的非运行状态
 * 均能稳定接收显式广播并回调业务层
 * <p>
 * 清单文件配自定义权限
 * com.permission.RECEIVE_CROSS_APP_MSG
 * <p>
 * 清单文件进行静态注册
 */
public class CrossAppBroadcastHelper extends BroadcastReceiver {
    /**
     * 发送给 APP A 的广播 Action
     * <p>
     * 当 APP A 作为接收方时监听此 Action
     * APP B 作为发送方时往此 Action 发送数据
     */
    public static final String ACTION_TO_APP_A = "com.broadcast.ACTION_TO_A";
    /**
     * 发送给 APP B 的广播 Action
     * <p>
     * 当 APP B 作为接收方时监听此 Action
     * APP A 作为发送方时往此 Action 发送数据
     */
    public static final String ACTION_TO_APP_B = "com.broadcast.ACTION_TO_B";
    /**
     * 跨 APP 消息
     */
    public static final String CROSS_APP_MSG = "CrossAppMsg";
    /**
     * 静态持有外部传入的业务回调接口
     * <p>
     * 因系统在无后台拉起进程时会通过反射创建全新的 CrossAppBroadcastHelper 实例
     * 声明为 static 可确保系统创建的临时新实例能共享并访问到同一个业务回调指针
     */
    private static BroadcastCallback broadcastCallback;
    /**
     * 当前应用发送的 Action 标识
     */
    private static String currentAction;
    /**
     * 目标应用接收的 Action 标识
     */
    private static String targetAction;
    /**
     * 目标包名
     */
    private static String targetPackageName;

    /**
     * constructor
     * <p>
     * 必须保留显式的公开无参构造方法
     * 供 Android 系统在收到静态广播时进行反射实例化
     */
    public CrossAppBroadcastHelper() {

    }

    /**
     * 配置目标应用参数
     * <p>
     * 方便在外部事先设置好目标 Action 和目标包名
     *
     * @param currentAction     当前应用发送的 Action 标识
     * @param targetAction      目标应用接收的 Action 标识
     * @param targetPackageName 目标包名
     */
    public static void setTargetConfig(String currentAction, String targetAction, String targetPackageName) {
        CrossAppBroadcastHelper.currentAction = currentAction;
        CrossAppBroadcastHelper.targetAction = targetAction;
        CrossAppBroadcastHelper.targetPackageName = targetPackageName;
    }

    /**
     * 向另一个应用（目标进程）发送跨进程广播消息
     * <p>
     * 方便在 Activity 或 Service 中直接调用
     *
     * @param context 上下文
     * @param message 传递的具体文本数据字符串
     */
    public static void sendMessage(@NonNull Context context, String message) {
        Intent intent = new Intent(targetAction);
        intent.putExtra(CROSS_APP_MSG, message);
        // 显式指定目标包名
        // 突破 Android 8.0+ 后台隐式广播限制
        // 这样即使目标 App 进程不存在
        // 系统也会为了接收这个广播而创建 / 拉起目标进程
        intent.setPackage(targetPackageName);
        context.sendBroadcast(intent, "com.permission.RECEIVE_CROSS_APP_MSG");
    }

    /**
     * 设置广播消息接收的对外暴露回调接口
     * <p>
     * 建议在 {@link android.app.Application#onCreate()} 中调用以绑定业务监听
     *
     * @param broadcastCallback 广播消息接收的对外暴露回调接口
     */
    public static void setBroadcastCallback(BroadcastCallback broadcastCallback) {
        CrossAppBroadcastHelper.broadcastCallback = broadcastCallback;
    }

    /**
     * 物理广播接收核心回调
     * <p>
     * 无论是存活状态还是无后台状态
     * 系统只要收到匹配的显式广播
     * 均会在此触发
     *
     * @param context 上下文
     * @param intent  意图
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // 安全校验
        if (null == intent) {
            return;
        }
        String action = intent.getAction();
        // 确保接收到的 Action 与当前应用预期的 currentAction 完全一致
        if (TextUtils.equals(action, currentAction)) {
            String msg = intent.getStringExtra(CROSS_APP_MSG);
            // 通过静态指针将数据安全地抛给外部业务层接口
            if (null != broadcastCallback) {
                broadcastCallback.onMessageReceived(action, msg);
            }
        }
    }

    /**
     * 广播消息接收的对外暴露回调接口
     */
    public interface BroadcastCallback {
        /**
         * 收到跨进程广播消息时的触发回调
         *
         * @param action  当前收到的广播 Action 标识
         *                便于处理多 Action 混合逻辑
         * @param message 接收到的具体文本数据内容
         */
        void onMessageReceived(String action, String message);
    }
}