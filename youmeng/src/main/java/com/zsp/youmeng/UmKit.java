package com.zsp.youmeng;

import android.app.Application;
import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.Map;

/**
 * Created on 2025/10/18.
 *
 * @author 郑少鹏
 * @desc 友盟配套原件
 */
public class UmKit {
    public static UmKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 预初始化
     * <p>
     * 为保证集成友盟统计 SDK 后满足工信部相关合规要求
     * 确保 APP 安装后首次冷启动在 {@link Application#onCreate()} 调预初始化函数 {@link UMConfigure#preInit(Context, String, String)} 并弹隐私授权弹窗
     * <p>
     * 预初始化函数不采集设备信息
     * 不向友盟后台上报数据
     * 耗时极少
     * 不影响冷启动体验
     * <p>
     * 务必在 {@link Application#onCreate()} 调
     * 避免统计日活不准确
     *
     * @param context 上下文
     * @param appKey  APP KEY
     * @param channel 渠道
     */
    public void preInit(Context context, String appKey, String channel) {
        UMConfigure.preInit(context, appKey, channel);
    }

    /**
     * 初始化
     * <p>
     * 确保 APP 首次冷启动在用户阅读《隐私政策》并取得授权后调正式初始化函数 {@link UMConfigure#init(Context, String, String, int, String)} 初始化统计 SDK
     * 此时 SDK 才真正采集设备信息并上报数据
     * 用户不同意《隐私政策》授权则不能调 {@link UMConfigure#init(Context, String, String, int, String)} 初始化函数
     * <p>
     * APP 获取到《隐私政策》用户授权后冷启动应保证在 {@link Application#onCreate()} 调预初始化函数 {@link UMConfigure#preInit(Context, String, String)}
     * 正式初始化函数 {@link UMConfigure#init(Context, String, String, int, String)} 可按需调
     * 可在预初始化函数后接着调
     * 也可放到后台线程中延迟调
     * 但必须调
     * <p>
     * appKey 和 channel 须保持和预初始化一致
     * <p>
     * deviceType - 设备类型
     * {@link UMConfigure#DEVICE_TYPE_PHONE} 手机
     * {@link UMConfigure#DEVICE_TYPE_BOX} 盒子
     * <p>
     * pushSecret
     * 推送业务 secret
     *
     * @param context 上下文
     * @param appKey  APP KEY
     * @param channel 渠道
     */
    public void init(Context context, String appKey, String channel) {
        UMConfigure.init(context, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, "");
    }

    /**
     * 设置会话持续毫秒
     *
     * @param interval 间隔
     *                 单位毫秒
     *                 40 * 1000 为 40 秒
     */
    public void setSessionContinueMillis(long interval) {
        MobclickAgent.setSessionContinueMillis(interval);
    }

    /**
     * 杀死进程
     * <p>
     * 开发者调 kill 或 exit 之类方法或双击返回键杀死进程
     * 务必在此前 {@link MobclickAgent#onKillProcess(Context)} 保存统计数据
     *
     * @param context 上下文
     */
    public void onKillProcess(Context context) {
        MobclickAgent.onKillProcess(context);
    }

    /**
     * 画像登入
     *
     * @param provider 提供者
     *                 用户通过第三方账号登录
     *                 可调此接口统计
     *                 <p>
     *                 支持自定义
     *                 不可下划线 _ 开头
     *                 用大写字母和数字标识
     *                 长度小于 32 字节
     *                 <p>
     *                 上市公司建议用股票代码
     * @param id       ID
     *                 用户账号 ID
     *                 长度小于 64 字节
     */
    public void onProfileSignIn(String provider, String id) {
        if (null == provider) {
            MobclickAgent.onProfileSignIn(id);
            return;
        }
        MobclickAgent.onProfileSignIn(provider, id);
    }

    /**
     * 画像登出
     * <p>
     * 登出需调此接口
     * 调后不再发送账号相关内容
     */
    public void onProfileSignOff() {
        MobclickAgent.onProfileSignOff();
    }

    /**
     * 用户画像手机号
     * <p>
     * 用户属性设置接口须在调账号登入接口 {@link MobclickAgent#onProfileSignIn(String, String)} 后调
     *
     * @param phoneNumber 手机号
     */
    public void userProfileMobile(String phoneNumber) {
        MobclickAgent.userProfileMobile(phoneNumber);
    }

    /**
     * 用户画像邮箱
     * <p>
     * 用户属性设置接口须在调账号登入接口 {@link MobclickAgent#onProfileSignIn(String, String)} 后调
     *
     * @param email 邮箱
     */
    public void userProfileEmail(String email) {
        MobclickAgent.userProfileEMail(email);
    }

    /**
     * 用户画像
     * <p>
     * 须调 {@link MobclickAgent#onProfileSignIn(String, String)} 后调
     * <p>
     * 仅支持最多 20 个用户自定义属性设置
     * 超出 20 个后的 key 设置无效
     * <p>
     * 设置自定义用户属性
     * 同 key 多次设置时
     * value 会覆盖替换
     *
     * @param key   键
     * @param value 值
     */
    public void userProfile(String key, Object value) {
        MobclickAgent.userProfile(key, value);
    }

    /**
     * 设置页面采集模式
     * <p>
     * 自动采集
     * {@link MobclickAgent.PageMode#AUTO}
     * 自动采集模式无需做任何页面埋点
     * 上报所有的页面访问信息
     * APP 下次启动默认上报本次 APP 所有 Activity 信息
     * 页面进入 - Activity onResume 函数
     * 页面退出 - Activity onPause 函数
     * 页面名称 - Activity 路径
     * 页面访问时长 - 监听到 onResume 函数和 onPause 函数时刻差
     * <p>
     * 手动采集
     * {@link MobclickAgent.PageMode#MANUAL}
     * 如需统计非 Activity 页面
     * 例如 Fragment、自定义 View 等
     * 需手动采集模式
     * 手动采集模式不采集 Activity 信息
     * 仅采集开发者埋点页面信息
     *
     * @param pageMode 页面模式
     */
    public void setPageCollectionMode(MobclickAgent.PageMode pageMode) {
        MobclickAgent.setPageCollectionMode(pageMode);
    }

    /**
     * 页面开始
     *
     * @param viewName 视图名
     */
    public void onPageStart(String viewName) {
        MobclickAgent.onPageStart(viewName);
    }

    /**
     * 页面结束
     *
     * @param viewName 视图名
     */
    public void onPageEnd(String viewName) {
        MobclickAgent.onPageEnd(viewName);
    }

    /**
     * 时间对象
     * <p>
     * 自定义事件可用于追踪用户行为
     * 记录行为发生的具体细节
     *
     * @param context  上下文
     * @param eventId  事件 ID
     *                 指一个操作
     * @param eventMap 事件集
     *                 参数值可如下类型之一 String、Long、Integer、Float、Double、Short
     */
    public void onEventObject(Context context, String eventId, Map<String, Object> eventMap) {
        MobclickAgent.onEventObject(context, eventId, eventMap);
    }

    /**
     * 进程事件
     * <p>
     * 是否支持在子进程中统计自定义事件
     *
     * @param setProcessEvent 设置进程事件否
     */
    public void setProcessEvent(boolean setProcessEvent) {
        UMConfigure.setProcessEvent(setProcessEvent);
    }

    /**
     * 提交政策授权结果
     * <p>
     * 友盟自动校验并判断是否采集其信息
     *
     * @param context           上下文
     * @param policyGrantResult 政策授权结果
     */
    public void submitPolicyGrantResult(Context context, boolean policyGrantResult) {
        UMConfigure.submitPolicyGrantResult(context, policyGrantResult);
    }

    /**
     * 是否允许 IMSI 收集
     *
     * @param enableImsiCollection 允许 IMSI 收集否
     */
    public void enableImsiCollection(boolean enableImsiCollection) {
        UMConfigure.enableImsiCollection(enableImsiCollection);
    }

    /**
     * 是否允许 ICCID 收集
     *
     * @param enableIccidCollection 允许 ICCID 收集否
     */
    public void enableIccidCollection(boolean enableIccidCollection) {
        UMConfigure.enableIccidCollection(enableIccidCollection);
    }

    /**
     * 是否允许 IMEI 收集
     *
     * @param enableImeiCollection 允许 IMEI 收集否
     */
    public void enableImeiCollection(boolean enableImeiCollection) {
        UMConfigure.enableImeiCollection(enableImeiCollection);
    }

    /**
     * 是否允许 WIFI MAC 收集
     *
     * @param enableWiFiMacCollection 允许 WIFI MAC 收集否
     */
    public void enableWiFiMacCollection(boolean enableWiFiMacCollection) {
        UMConfigure.enableWiFiMacCollection(enableWiFiMacCollection);
    }

    /**
     * 设置是否允许日志
     *
     * @param logEnable 允许日志否
     */
    public void setLogEnabled(boolean logEnable) {
        UMConfigure.setLogEnabled(logEnable);
    }

    private static final class InstanceHolder {
        static final UmKit INSTANCE = new UmKit();
    }
}