package util.vibrator;

import static android.Manifest.permission.VIBRATE;
import static android.content.Context.VIBRATOR_SERVICE;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresPermission;

/**
 * Created on 2019/8/31.
 *
 * @author 郑少鹏
 * @desc VibratorUtils
 */
public class VibratorUtils {
    private static Vibrator vibrator;

    /**
     * 一次振动
     *
     * @param context      上下文
     * @param milliseconds 振动时长（ms）
     * @param amplitude    振动强度（1 到 255 间或 DEFAULT_AMPLITUDE）
     */
    @RequiresPermission(VIBRATE)
    public static void oneShotVibration(Context context, long milliseconds, int amplitude) {
        Vibrator vibrator = getVibrator(context);
        if (null != vibrator) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(milliseconds, amplitude);
                vibrator.vibrate(vibrationEffect);
            } else {
                vibrator.vibrate(200);
            }
        }
    }

    /**
     * 波形振动
     *
     * @param context    上下文
     * @param timings    交替开关定时模式（从关闭开始，0 忽略时序 / 幅度序列）
     * @param amplitudes 振幅值（0（断开）到 255 间或 DEFAULT_AMPLITUDE）
     * @param repeat     振动重复模式（-1 不重复、0 一直重复、1 从数组下标 1 开始重复振动后结束、2 从数组下标 2 开始重复振动后结束）
     */
    @RequiresPermission(VIBRATE)
    public static void waveformVibration(Context context, long[] timings, int[] amplitudes, int repeat) {
        Vibrator vibrator = getVibrator(context);
        if (null != vibrator) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                VibrationEffect vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat);
                vibrator.vibrate(vibrationEffect);
            } else {
                vibrator.vibrate(timings, repeat);
            }
        }
    }

    /**
     * Cancel vibrate.
     * Must hold <uses-permission android:name="android.permission.VIBRATE" />
     *
     * @param context Context
     */
    @RequiresPermission(VIBRATE)
    public static void cancel(Context context) {
        Vibrator vibrator = getVibrator(context);
        if (null == vibrator) {
            return;
        }
        vibrator.cancel();
    }

    /**
     * Get vibrator.
     *
     * @param context Context
     * @return Vibrator
     */
    private static Vibrator getVibrator(Context context) {
        if (null == vibrator) {
            vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        }
        return vibrator;
    }
}