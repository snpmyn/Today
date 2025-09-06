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