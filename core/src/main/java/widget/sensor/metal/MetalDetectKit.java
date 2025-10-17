package widget.sensor.metal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.VibrationEffect;
import android.view.View;

import androidx.annotation.NonNull;

import com.zsp.core.R;

import util.vibrator.VibratorUtils;
import util.view.ViewUtils;

/**
 * @decs: 金属探测配套原件
 * @author: 郑少鹏
 * @date: 2025/10/16 19:09
 * @version: v 1.0
 * <p>
 * 手机金属探测器依赖磁力计检测磁场强度变化
 * <p>
 * 仅可探测
 * 铁、钢、镍、钴等磁性金属
 * <p>
 * 不可探测
 * 铜、铝、金、银、铅等非磁性金属
 * <p>
 * 手机磁力计测量磁场的强度和方向
 * 磁性材料会产生局部磁场干扰
 * 磁力计能感知到
 * <p>
 * 非磁性材料（如金）不会改变周围磁场
 * 故磁力计无法检测
 * <p>
 * 普通 Android 手机仅可检测磁性金属
 * 无法探测黄金、铜、银等非磁性金属
 */
public class MetalDetectKit implements SensorEventListener {
    /**
     * 金属探测监听
     */
    public interface MetalDetectListener {
        void onMagneticFieldChange(float magnitude);

        void onMetalDetect();
    }

    private final SensorManager sensorManager;
    private final Sensor magnetometer;
    private final MetalDetectListener metalDetectListener;
    private final Context context;
    private final View flashView;
    private float lastMagnitude = 0.0F;
    private boolean firstEvent = true;
    private float threshold = 70.0F;
    private float deltaThreshold = 15.0F;

    public MetalDetectKit(@NonNull Context context, @NonNull MetalDetectListener metalDetectListener, View flashView) {
        this.context = context;
        this.metalDetectListener = metalDetectListener;
        this.flashView = flashView;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (null == magnetometer) {
            throw new RuntimeException(context.getString(R.string.deviceDoesNotHaveMagnetometerSensor));
        }
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public void setDeltaThreshold(float deltaThreshold) {
        this.deltaThreshold = deltaThreshold;
    }

    public void start() {
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
        firstEvent = true;
        lastMagnitude = 0.0F;
    }

    @Override
    public void onSensorChanged(@NonNull SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
        if (null != metalDetectListener) {
            metalDetectListener.onMagneticFieldChange(magnitude);
        }
        if (firstEvent) {
            firstEvent = false;
            lastMagnitude = magnitude;
            return;
        }
        if ((magnitude > threshold) || (Math.abs(magnitude - lastMagnitude) > deltaThreshold)) {
            if (null != metalDetectListener) {
                metalDetectListener.onMetalDetect();
            }
            flashAndVibrate();
        }
        lastMagnitude = magnitude;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @SuppressLint("InlinedApi")
    private void flashAndVibrate() {
        ViewUtils.flash(context, flashView, 100);
        VibratorUtils.oneShotVibration(context, 200, VibrationEffect.DEFAULT_AMPLITUDE);
    }
}