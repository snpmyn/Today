package widget.sms.kit;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import widget.sms.broadcastreceiver.SmsBroadcastReceiver;

/**
 * Created on 2018/1/11.
 *
 * @author 郑少鹏
 * @desc 短信配套原件
 */
public class SmsKit implements SmsBroadcastReceiver.SmsBroadcastReceiverSendListener, SmsBroadcastReceiver.SmsBroadcastReceiverDeliverListener {
    /**
     * 短信广播发送/传送接收器
     */
    private SmsBroadcastReceiver smsSendBroadcastReceiver, smsDeliverBroadcastReceiver;
    /**
     * 意图
     */
    private final PendingIntent smsSendPendingIntent;
    private final PendingIntent smsDeliverPendingIntent;
    /**
     * 滤值
     */
    public static String SMS_SEND_ACTION = "SMS_SEND_ACTION";
    public static String SMS_DELIVER_ACTION = "SMS_DELIVER_ACTION";
    /**
     * 短信配套原件发送/传送监听
     */
    private SmsKitSendListener smsKitSendListener;
    private SmsKitDeliverListener smsKitDeliverListener;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public SmsKit(Context context) {
        // 注册接收器
        registerReceiver(context);
        // 设监听
        setListener();
        // 意图
        Intent smsSendIntent = new Intent(SMS_SEND_ACTION);
        smsSendPendingIntent = PendingIntent.getBroadcast(context, 0, smsSendIntent, PendingIntent.FLAG_IMMUTABLE);
        Intent smsDeliverIntent = new Intent(SMS_DELIVER_ACTION);
        smsDeliverPendingIntent = PendingIntent.getBroadcast(context, 0, smsDeliverIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    /**
     * 注册接收器
     *
     * @param context 上下文
     */
    private void registerReceiver(Context context) {
        // 发送
        smsSendBroadcastReceiver = new SmsBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(SMS_SEND_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(smsSendBroadcastReceiver, intentFilter);
        // 传送
        smsDeliverBroadcastReceiver = new SmsBroadcastReceiver();
        intentFilter = new IntentFilter(SMS_DELIVER_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(smsDeliverBroadcastReceiver, intentFilter);
    }

    /**
     * 反注册接收器
     *
     * @param context 上下文
     */
    public void unregisterReceiver(Context context) {
        // 发送
        if (null != smsSendBroadcastReceiver) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(smsSendBroadcastReceiver);
            smsSendBroadcastReceiver = null;
        }
        // 传送
        if (null != smsDeliverBroadcastReceiver) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(smsDeliverBroadcastReceiver);
            smsDeliverBroadcastReceiver = null;
        }
    }

    /**
     * 单发
     * <p>
     * 这行代码用于发送一条可能被分割成多条短信的长文本消息
     * 当短信内容超过单个 SMS 消息的长度限制（通常是 160 个 7-bit 字符或 70 个 Unicode 字符）时，系统会自动将其分割成多个部分发送，并在接收端重新组装成一条完整的消息。
     * <p>
     * destinationAddress (目标地址)
     * 接收短信的电话号码
     * <p>
     * scAddress (短信中心地址)
     * 用于处理发送短信的短信中心（SMSC）的地址
     * 通常为 null，表示使用设备默认的短信中心。
     * <p>
     * parts (消息部分)
     * 一个 ArrayList<String>，包含了要发送的长短信被分割后的各个部分。
     * <p>
     * sentIntents (发送状态 PendingIntent 列表)
     * 一个 ArrayList<PendingIntent>
     * 列表中的每一个 PendingIntent 都会在对应的短信部分发送尝试完成后被触发（广播或启动活动/服务）
     * 你可以通过它来监听每条分割短信的发送状态（成功/失败）
     * <p>
     * deliveryIntents (送达回执 PendingIntent 列表)
     * 一个 ArrayList<PendingIntent>
     * 列表中的每一个 PendingIntent 会在对应的短信部分已成功送达接收方设备时被触发
     *
     * @param context            上下文
     * @param destinationAddress 目标地址
     * @param content            内容
     */
    public void singleShot(Context context, String destinationAddress, String content) {
        // 发送短信广播发送接收器
        Intent smsSendIntent = new Intent(SMS_SEND_ACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(smsSendIntent);
        // 发送短信广播传送接收器
        Intent smsDeliverIntent = new Intent(SMS_DELIVER_ACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(smsDeliverIntent);
        // 短信管理器
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> divideMessageContent = smsManager.divideMessage(content);
        try {
            if (divideMessageContent.size() == 1) {
                smsManager.sendTextMessage(destinationAddress, null, divideMessageContent.get(0), smsSendPendingIntent, smsDeliverPendingIntent);
            } else if (divideMessageContent.size() > 1) {
                ArrayList<PendingIntent> smsSendPendingIntents = new ArrayList<>();
                ArrayList<PendingIntent> smsDeliverPendingIntents = new ArrayList<>();
                for (int i = 0; i < divideMessageContent.size(); i++) {
                    smsSendPendingIntents.add(i, smsSendPendingIntent);
                    smsDeliverPendingIntents.add(i, smsDeliverPendingIntent);
                }
                smsManager.sendMultipartTextMessage(destinationAddress, null, divideMessageContent, smsSendPendingIntents, smsDeliverPendingIntents);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * 群发
     *
     * @param context                上下文
     * @param destinationAddressList 目标地址集
     * @param content                内容
     */
    public void massSend(Context context, @NonNull List<String> destinationAddressList, String content) {
        for (String destinationAddress : destinationAddressList) {
            singleShot(context, destinationAddress, content);
        }
    }

    /**
     * 设监听
     */
    private void setListener() {
        smsSendBroadcastReceiver.setSmsBroadcastReceiverSendListener(this);
        smsDeliverBroadcastReceiver.setSmsBroadcastReceiverDeliverListener(this);
    }

    /**
     * 设短信配套原件发送监听
     *
     * @param smsKitSendListener 短信配套原件发送监听
     */
    public void setSmsKitSendListener(SmsKitSendListener smsKitSendListener) {
        this.smsKitSendListener = smsKitSendListener;
    }

    /**
     * 设短信配套原件传送监听
     *
     * @param smsKitDeliverListener 短信配套原件传送监听
     */
    public void setSmsKitDeliverListener(SmsKitDeliverListener smsKitDeliverListener) {
        this.smsKitDeliverListener = smsKitDeliverListener;
    }

    /**
     * 短信配套原件发送监听
     */
    public interface SmsKitSendListener {
        /**
         * 发送 (RESULT_OK)
         */
        void sendResultOk();

        /**
         * 发送 (RESULT_ERROR_GENERIC_FAILURE)
         */
        void sendResultErrorCenericFailure();
    }

    /**
     * 短信配套原件传送监听
     */
    public interface SmsKitDeliverListener {
        /**
         * 传送 (RESULT_OK)
         */
        void deliverResultOk();

        /**
         * 传送 (RESULT_ERROR_GENERIC_FAILURE)
         */
        void deliverResultErrorCenericFailure();
    }

    /**
     * 传送 (RESULT_OK)
     */
    @Override
    public void deliverResultOk() {
        smsKitDeliverListener.deliverResultOk();
    }

    /**
     * 传送 (RESULT_ERROR_GENERIC_FAILURE)
     */
    @Override
    public void deliverResultErrorCenericFailure() {
        smsKitDeliverListener.deliverResultErrorCenericFailure();
    }

    /**
     * 发送 (RESULT_OK)
     */
    @Override
    public void sendResultOk() {
        smsKitSendListener.sendResultOk();
    }

    /**
     * 发送 (RESULT_ERROR_GENERIC_FAILURE)
     */
    @Override
    public void sendResultErrorCenericFailure() {
        smsKitSendListener.sendResultErrorCenericFailure();
    }
}