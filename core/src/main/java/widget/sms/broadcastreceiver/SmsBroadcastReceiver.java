package widget.sms.broadcastreceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;

import util.log.LogUtils;
import widget.sms.kit.SmsKit;

/**
 * Created on 2018/1/11.
 *
 * @author 郑少鹏
 * @desc 短信广播接收器
 * <p>
 * 在 Android 中，发送短信时，响应码为 0 通常表示发送成功。
 * 但是，这个响应码并不是标准的短信发送成功标志，而是 Android 系统内部的返回值。
 * <p>
 * 使用 SmsManager 发送短信：如果你是通过 SmsManager 发送短信，那么通常会通过 SentIntent 来接收结果。
 * 在这个 SentIntent 中，resultCode 为 Activity.RESULT_OK 表示短信发送成功。
 * 而 0 可能表示没有明确的错误，或者是默认的成功标志，具体还需查看具体文档。
 * <p>
 * 通过 SmsManager 发送短信并使用广播接收结果：Android 中的 SmsManager 可以通过 BroadcastReceiver 发送一个发送结果的广播。
 * 此时，结果码 0 可能只是系统内部返回的一个默认值，并不代表失败。
 * <p>
 * 网络问题或运营商的限制：有时即使代码返回 0，短信可能由于网络问题或运营商的限制而未能真正发出去。
 * 在这种情况下，你应该检查其他的网络连接和配置，确保短信能够顺利发送。
 * <p>
 * 总结：0 通常表示成功，但最好结合具体的应用上下文、短信发送的 API 以及设备/运营商配置来确认是否真正成功。
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {
    /**
     * 短信广播接收器发送监听
     */
    private SmsBroadcastReceiverSendListener smsBroadcastReceiverSendListener;
    /**
     * 短信广播接收器传送监听
     */
    private SmsBroadcastReceiverDeliverListener smsBroadcastReceiverDeliverListener;

    /**
     * 设短信广播接收器发送监听
     *
     * @param smsBroadcastReceiverSendListener 短信广播接收器发送监听
     */
    public void setSmsBroadcastReceiverSendListener(SmsBroadcastReceiverSendListener smsBroadcastReceiverSendListener) {
        this.smsBroadcastReceiverSendListener = smsBroadcastReceiverSendListener;
    }

    /**
     * 设短信广播接收器传送监听
     *
     * @param smsBroadcastReceiverDeliverListener 短信广播接收器传送监听
     */
    public void setSmsBroadcastReceiverDeliverListener(SmsBroadcastReceiverDeliverListener smsBroadcastReceiverDeliverListener) {
        this.smsBroadcastReceiverDeliverListener = smsBroadcastReceiverDeliverListener;
    }

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        if (null != intent.getAction()) {
            if (intent.getAction().equals(SmsKit.SMS_SEND_ACTION)) {
                switch (getResultCode()) {
                    case 0:
                    case Activity.RESULT_OK:
                        // 短信发送成功
                        smsBroadcastReceiverSendListener.sendResultOk();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // 短信发送失败
                        smsBroadcastReceiverSendListener.sendResultErrorCenericFailure();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // 无线电关闭 发送失败
                        LogUtils.i(this.getClass().getSimpleName(), "RESULT_ERROR_RADIO_OFF");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // PDU 为空
                        LogUtils.i(this.getClass().getSimpleName(), "RESULT_ERROR_NULL_PDU");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // 无服务
                        LogUtils.i(this.getClass().getSimpleName(), "RESULT_ERROR_NO_SERVICE");
                        break;
                    default:
                        break;
                }
            } else if (intent.getAction().equals(SmsKit.SMS_DELIVER_ACTION)) {
                switch (getResultCode()) {
                    case 0:
                    case Activity.RESULT_OK:
                        // 短信已送达
                        smsBroadcastReceiverDeliverListener.deliverResultOk();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // 短信送达失败
                        smsBroadcastReceiverDeliverListener.deliverResultErrorCenericFailure();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // 无线电关闭 送达失败
                        LogUtils.i(this.getClass().getSimpleName(), "RESULT_ERROR_RADIO_OFF");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // PDU 为空
                        LogUtils.i(this.getClass().getSimpleName(), "RESULT_ERROR_NULL_PDU");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // 无服务
                        LogUtils.i(this.getClass().getSimpleName(), "RESULT_ERROR_NO_SERVICE");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 短信广播接收器发送监听
     */
    public interface SmsBroadcastReceiverSendListener {
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
     * 短信广播接收器传送监听
     */
    public interface SmsBroadcastReceiverDeliverListener {
        /**
         * 传送 (RESULT_OK)
         */
        void deliverResultOk();

        /**
         * 传送 (RESULT_ERROR_GENERIC_FAILURE)
         */
        void deliverResultErrorCenericFailure();
    }
}