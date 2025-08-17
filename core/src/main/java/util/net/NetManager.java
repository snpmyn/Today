package util.net;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created on 2018/6/5.
 * 参考 <a href="https://developer.android.com/training/basics/network-ops/managing.html">...</a>
 * ConnectivityManager: Answers queries about the state of network connectivity.
 * It also notifies applications when network connectivity changes.
 * NetworkInfo: Describes the status of a network interface of a given type (currently either Mobile or Wi-Fi).
 *
 * @author 郑少鹏
 * @desc 网络管理器
 */
public class NetManager {
    private static final NetConnChangedReceiver S_NET_CONN_CHANGED_RECEIVER = new NetConnChangedReceiver();
    private static final List<NetConnChangedListener> S_NET_CONN_CHANGED_LISTENERS = new ArrayList<>();

    private NetManager() {
        throw new IllegalStateException("No instance!");
    }

    /**
     * 网络接口可用否（即网络连接可行否）
     * 连接（即存网络连接否，可建套接字并传数据否）
     *
     * @param context 上下文
     * @return 可用 true
     */
    public static boolean areNetConnected(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        return ((null != activeInfo) && activeInfo.isConnected());
    }

    /**
     * 移动数据否
     *
     * @param context 上下文
     * @return 移动数据 true
     */
    public static boolean areMobileConnected(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        return ((null != activeInfo) && activeInfo.isConnected() && (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE));
    }

    /**
     * 2G 否
     *
     * @param context 上下文
     * @return 2G true
     */
    public static boolean are2gConnected(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        if ((null == activeInfo) || !activeInfo.isConnected()) {
            return false;
        }
        int subtype = activeInfo.getSubtype();
        switch (subtype) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return true;
            default:
                return false;
        }
    }

    /**
     * 3G 否
     *
     * @param context 上下文
     * @return 3G true
     */
    public static boolean are3gConnected(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        if ((null == activeInfo) || !activeInfo.isConnected()) {
            return false;
        }
        int subtype = activeInfo.getSubtype();
        switch (subtype) {
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return true;
            default:
                return false;
        }
    }

    /**
     * 4G 否
     *
     * @param context 上下文
     * @return 4G true
     */
    public static boolean are4gConnected(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        if ((null == activeInfo) || !activeInfo.isConnected()) {
            return false;
        }
        int subtype = activeInfo.getSubtype();
        switch (subtype) {
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                return true;
            default:
                return false;
        }
    }

    /**
     * 移动网络运营商名
     * 中国联通
     * 中国移动
     * 中国电信
     *
     * @param context 上下文
     * @return 移动网络运营商名
     */
    public static @Nullable String getNetworkOperatorName(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        return (null != telephonyManager) ? telephonyManager.getNetworkOperatorName() : null;
    }

    /**
     * 移动终端类型
     * <ul>
     * <li>{@link TelephonyManager#PHONE_TYPE_NONE } : 0 手机制式未知</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_GSM  } : 1 手机制式 GSM（移动和联通）</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_CDMA } : 2 手机制式 CDMA（电信）</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_SIP  } : 3 </li>
     * </ul>
     *
     * @param context 上下文
     * @return 手机制式
     */
    public static int getPhoneType(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        return (null != telephonyManager) ? telephonyManager.getPhoneType() : 0;
    }

    /**
     * WIFI 连否
     *
     * @param context 上下文
     * @return wifi 连 true
     */
    public static boolean areWifiConnected(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        return ((null != activeInfo) && activeInfo.isConnected() && (activeInfo.getType() == ConnectivityManager.TYPE_WIFI));
    }

    /**
     * 连 WIFI
     *
     * @param context 上下文
     */
    public static void wifiConnect(@NotNull Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        if ((null != wifiManager) && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 注网络接收者
     *
     * @param context 上下文
     */
    public static void registerNetConnChangedReceiver(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.getApplicationContext().registerReceiver(S_NET_CONN_CHANGED_RECEIVER, intentFilter);
    }

    /**
     * 反注网络接收者
     *
     * @param context 上下文
     */
    public static void unregisterNetConnChangedReceiver(@NotNull Context context) {
        checkNonNull(context.getApplicationContext(), "context == null");
        context.getApplicationContext().unregisterReceiver(S_NET_CONN_CHANGED_RECEIVER);
        S_NET_CONN_CHANGED_LISTENERS.clear();
    }

    /**
     * 添网状变监听
     *
     * @param listener 网连状变监听
     */
    public static void addNetConnChangedListener(NetConnChangedListener listener) {
        checkNonNull(listener, "listener == null");
        log("addNetConnChangedListener: " + S_NET_CONN_CHANGED_LISTENERS.add(listener));
    }

    /**
     * 移网状变监听
     *
     * @param listener 网连状变监听
     */
    public static void removeNetConnChangedListener(NetConnChangedListener listener) {
        checkNonNull(listener, "listener == null");
        log("removeNetConnChangedListener: " + S_NET_CONN_CHANGED_LISTENERS.remove(listener));
    }

    private static void broadcastConnStatus(ConnectStatus connectStatus) {
        int size = S_NET_CONN_CHANGED_LISTENERS.size();
        if (size == 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            S_NET_CONN_CHANGED_LISTENERS.get(i).onNetConnChanged(connectStatus);
        }
    }

    /**
     * 网络信息
     *
     * @param context 上下文
     * @return 网络信息
     */
    private static @Nullable NetworkInfo getActiveNetworkInfo(@NotNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        return ((null != connectivityManager) ? connectivityManager.getActiveNetworkInfo() : null);
    }

    private static void checkNonNull(Object object, String message) {
        if (null == object) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void log(String msg) {
        Timber.d(msg);
    }

    public enum ConnectStatus {
        /**
         * 无网
         */
        NO_NETWORK, WIFI, MOBILE, MOBILE_2G, MOBILE_3G, MOBILE_4G, MOBILE_UNKNOWN, OTHER, NO_CONNECTED
    }

    public interface NetConnChangedListener {
        /**
         * 网络连状变
         *
         * @param connectStatus 连状
         */
        void onNetConnChanged(ConnectStatus connectStatus);
    }

    private static final class NetConnChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            log("onReceive");
            NetworkInfo activeInfo = getActiveNetworkInfo(context);
            if (null == activeInfo) {
                broadcastConnStatus(ConnectStatus.NO_NETWORK);
            } else if (activeInfo.isConnected()) {
                int networkType = activeInfo.getType();
                if (ConnectivityManager.TYPE_WIFI == networkType) {
                    broadcastConnStatus(ConnectStatus.WIFI);
                } else if (ConnectivityManager.TYPE_MOBILE == networkType) {
                    broadcastConnStatus(ConnectStatus.MOBILE);
                    int subtype = activeInfo.getSubtype();
                    if (TelephonyManager.NETWORK_TYPE_GPRS == subtype || TelephonyManager.NETWORK_TYPE_GSM == subtype || TelephonyManager.NETWORK_TYPE_EDGE == subtype || TelephonyManager.NETWORK_TYPE_CDMA == subtype || TelephonyManager.NETWORK_TYPE_1xRTT == subtype || TelephonyManager.NETWORK_TYPE_IDEN == subtype) {
                        broadcastConnStatus(ConnectStatus.MOBILE_2G);
                    } else if (TelephonyManager.NETWORK_TYPE_UMTS == subtype || TelephonyManager.NETWORK_TYPE_EVDO_0 == subtype || TelephonyManager.NETWORK_TYPE_EVDO_A == subtype || TelephonyManager.NETWORK_TYPE_HSDPA == subtype || TelephonyManager.NETWORK_TYPE_HSUPA == subtype || TelephonyManager.NETWORK_TYPE_HSPA == subtype || TelephonyManager.NETWORK_TYPE_EVDO_B == subtype || TelephonyManager.NETWORK_TYPE_EHRPD == subtype || TelephonyManager.NETWORK_TYPE_HSPAP == subtype || TelephonyManager.NETWORK_TYPE_TD_SCDMA == subtype) {
                        broadcastConnStatus(ConnectStatus.MOBILE_3G);
                    } else if (TelephonyManager.NETWORK_TYPE_LTE == subtype || TelephonyManager.NETWORK_TYPE_IWLAN == subtype) {
                        broadcastConnStatus(ConnectStatus.MOBILE_4G);
                    } else {
                        broadcastConnStatus(ConnectStatus.MOBILE_UNKNOWN);
                    }
                } else {
                    broadcastConnStatus(ConnectStatus.OTHER);
                }
            } else {
                broadcastConnStatus(ConnectStatus.NO_CONNECTED);
            }
        }
    }
}