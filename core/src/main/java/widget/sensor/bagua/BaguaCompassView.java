package widget.sensor.bagua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

/**
 * @decs: 八卦罗盘视图
 * @author: 郑少鹏
 * @date: 2025/10/16 15:37
 * @version: v 1.0
 */
public class BaguaCompassView extends View implements SensorEventListener {
    private Paint circlePaint, textPaint, needlePaint, gridPaint;
    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float[] gravity, geomagnetic;
    private float currentAzimuth = 0.0F;
    /**
     * 八卦数据
     * <p>
     * 北为坎
     * 顺时针
     */
    private static final String[] BAGUA = {"坎", "艮", "震", "巽", "离", "坤", "兑", "乾"};
    private static final String[] DIRECTIONS = {"北", "东北", "东", "东南", "南", "西南", "西", "西北"};
    private static final String[] BAGUA_SYMBOLS = {"☵", "☶", "☳", "☴", "☲", "☷", "☱", "☰"};
    private static final String[] BAGUA_ELEMENT = {"水", "土", "木", "木", "火", "土", "金", "金"};
    /**
     * 卦义
     */
    private static final String[] BAGUA_MEANING = {"水险", "土阻", "雷动", "风入", "火明", "厚载", "泽润", "健行"};
    /**
     * 八卦描述
     */
    private static final String[] BAGUA_DESCRIPTION = {"险象环生\n前路湿滑", "石门阻路\n止步不前", "雷声轰鸣\n暗室机关启动", "风起尘动\n通道曲折", "火光微亮\n照亮机关", "厚土覆盖\n墓室深重", "泽润石壁\n水滴暗门", "天行健\n穹窿坚固"};
    private int currentBaguaIndex = 0;
    private int centerX, centerY, radius;
    /**
     * 圆盘
     */
    private float outerCircleStroke = 5.0F, middleCircleStroke = 4.0F, innerCircleStroke = 3.0F;
    private int circleColor = Color.parseColor("#8B4513");
    private float outerCircleRadius = 1.0F, middleCircleRadius = 0.75F, innerCircleRadius = 0.45F;
    /**
     * 网格线
     */
    private float gridLineStroke = 2.0F;
    private int gridLineColor = Color.parseColor("#CD853F");
    /**
     * 文字
     */
    private float directionTextSize = 32.0F;
    private int directionTextColor = Color.parseColor("#8B0000");
    private boolean directionTextBold = true;
    private float directionRadius = 0.88F;
    private float symbolTextSize = 55.0F;
    private int symbolTextColor = Color.parseColor("#DAA520");
    private boolean symbolTextBold = false;
    private float symbolRadius = 0.72F;
    private float baguaTextSize = 48.0F;
    private int baguaTextColor = Color.parseColor("#B8860B");
    private boolean baguaTextBold = true;
    private float baguaRadius = 0.58F;
    private float meaningTextSize = 28.0F;
    private int meaningTextColor = Color.parseColor("#8B4513");
    private boolean meaningTextBold = false;
    private float meaningRadius = 0.36F;
    /**
     * 太极
     */
    private float taiJiOuterRadius = 0.12F, taiJiMiddleRadius = 0.08F, taiJiInnerRadius = 0.04F;
    private int taiJiOuterColor = Color.parseColor("#8B4513");
    private int taiJiMiddleColor = Color.parseColor("#DAA520");
    private int taiJiInnerColor = Color.parseColor("#8B0000");
    /**
     * 指针
     */
    private float needleLength = 0.4F;
    private int needleNorthColor = Color.RED;
    private int needleSouthColor = Color.WHITE;
    private int needleCenterColor = Color.BLACK;
    private int needleCenterInnerColor = Color.YELLOW;
    private float needleCenterRadius = 10.0F;
    private float needleCenterInnerRadius = 6.0F;
    private int backgroundColor = Color.TRANSPARENT;
    /**
     * 平滑与滤波控制
     */
    private static final float FILTER_ALPHA = 0.15F;
    private static final float SMOOTH_FACTOR = 0.1F;
    private static final long UPDATE_INTERVAL_MS = 50L;
    private float smoothAzimuth = 0.0F;
    private long lastUpdateTime = 0L;

    public interface OnBaguaChangeListener {
        void onBaguaChange(String bagua, String direction, String element, String meaning, String description, String symbol);
    }

    private OnBaguaChangeListener onBaguaChangeListener;
    private float azimuthThreshold = 1.0F;
    private float lastReportedAzimuth = -1.0F;

    public void setOnBaguaChangedListener(OnBaguaChangeListener onBaguaChangeListener) {
        this.onBaguaChangeListener = onBaguaChangeListener;
    }

    public void setAzimuthThreshold(float threshold) {
        azimuthThreshold = Math.max(0, threshold);
    }

    public BaguaCompassView(Context context) {
        super(context);
        init(context);
    }

    public BaguaCompassView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public BaguaCompassView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStyle(Paint.Style.STROKE);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(w, h) / 2;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        canvas.save();
        canvas.rotate(-currentAzimuth, centerX, centerY);
        drawCircles(canvas);
        drawGrid(canvas);
        drawBagua(canvas);
        canvas.restore();
        drawNeedle(canvas);
    }

    private void drawBackground(@NonNull Canvas canvas) {
        @SuppressLint("DrawAllocation") Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int innerColor = ColorUtils.setAlphaComponent(backgroundColor, 240);
        int outerColor = ColorUtils.setAlphaComponent(backgroundColor, 120);
        Shader shader = new RadialGradient(centerX, centerY, radius, innerColor, outerColor, Shader.TileMode.CLAMP);
        bgPaint.setShader(shader);
        canvas.drawCircle(centerX, centerY, radius, bgPaint);
    }

    private void drawCircles(@NonNull Canvas canvas) {
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(outerCircleStroke);
        canvas.drawCircle(centerX, centerY, radius * outerCircleRadius, circlePaint);
        circlePaint.setStrokeWidth(middleCircleStroke);
        canvas.drawCircle(centerX, centerY, radius * middleCircleRadius, circlePaint);
        circlePaint.setStrokeWidth(innerCircleStroke);
        canvas.drawCircle(centerX, centerY, radius * innerCircleRadius, circlePaint);
    }

    private void drawGrid(@NonNull Canvas canvas) {
        gridPaint.setStrokeWidth(gridLineStroke);
        gridPaint.setColor(gridLineColor);
        for (int i = 0; i < 8; i++) {
            float angle = i * 45;
            float endX = (centerX + (float) Math.cos(Math.toRadians(angle - 90)) * radius);
            float endY = (centerY + (float) Math.sin(Math.toRadians(angle - 90)) * radius);
            canvas.drawLine(centerX, centerY, endX, endY, gridPaint);
        }
    }

    private void drawBagua(@NonNull Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            float angle = i * 45;
            drawRotatedText(canvas, DIRECTIONS[i], angle, directionRadius, directionTextSize, directionTextColor, directionTextBold, 10);
            drawRotatedText(canvas, BAGUA_SYMBOLS[i], angle, symbolRadius, symbolTextSize, symbolTextColor, symbolTextBold, 18);
            drawRotatedText(canvas, BAGUA[i], angle, baguaRadius, baguaTextSize, baguaTextColor, baguaTextBold, 15);
            drawRotatedText(canvas, BAGUA_MEANING[i], angle, meaningRadius, meaningTextSize, meaningTextColor, meaningTextBold, 10);
        }
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(taiJiOuterColor);
        canvas.drawCircle(centerX, centerY, radius * taiJiOuterRadius, circlePaint);
        circlePaint.setColor(taiJiMiddleColor);
        canvas.drawCircle(centerX, centerY, radius * taiJiMiddleRadius, circlePaint);
        circlePaint.setColor(taiJiInnerColor);
        canvas.drawCircle(centerX, centerY, radius * taiJiInnerRadius, circlePaint);
    }

    private void drawRotatedText(@NonNull Canvas canvas, String text, float angle, float radiusRatio, float textSize, int color, boolean bold, float offsetY) {
        float r = radius * radiusRatio;
        float x = (centerX + (float) Math.cos(Math.toRadians(angle - 90)) * r);
        float y = (centerY + (float) Math.sin(Math.toRadians(angle - 90)) * r);
        canvas.save();
        canvas.rotate(currentAzimuth, x, y);
        textPaint.setTextSize(textSize);
        textPaint.setColor(color);
        textPaint.setFakeBoldText(bold);
        canvas.drawText(text, x, y + offsetY, textPaint);
        canvas.restore();
    }

    private void drawNeedle(@NonNull Canvas canvas) {
        Path path = new Path();
        float halfWidth = needleCenterRadius / 2;
        // 红针指北
        path.moveTo(centerX, centerY - radius * needleLength);
        path.lineTo(centerX - halfWidth, centerY);
        path.lineTo(centerX + halfWidth, centerY);
        path.close();
        needlePaint.setColor(needleNorthColor);
        canvas.drawPath(path, needlePaint);
        // 白针指南
        path.reset();
        path.moveTo(centerX, centerY + radius * needleLength);
        path.lineTo(centerX - halfWidth, centerY);
        path.lineTo(centerX + halfWidth, centerY);
        path.close();
        needlePaint.setColor(needleSouthColor);
        canvas.drawPath(path, needlePaint);
        // 中心点
        needlePaint.setColor(needleCenterColor);
        canvas.drawCircle(centerX, centerY, needleCenterRadius, needlePaint);
        needlePaint.setColor(needleCenterInnerColor);
        canvas.drawCircle(centerX, centerY, needleCenterInnerRadius, needlePaint);
    }

    @Override
    public void onSensorChanged(@NonNull SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (null == gravity) {
                gravity = event.values.clone();
            } else for (int i = 0; i < 3; i++)
                gravity[i] = (FILTER_ALPHA * event.values[i] + (1 - FILTER_ALPHA) * gravity[i]);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            if (null == geomagnetic) {
                geomagnetic = event.values.clone();
            } else for (int i = 0; i < 3; i++)
                geomagnetic[i] = (FILTER_ALPHA * event.values[i] + (1 - FILTER_ALPHA) * geomagnetic[i]);
        }
        if ((null != gravity) && (null != geomagnetic)) {
            float[] R = new float[9], I = new float[9];
            if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float rawAzimuth = (float) Math.toDegrees(orientation[0]);
                rawAzimuth = (rawAzimuth + 360.0F) % 360.0F;
                smoothAzimuth += SMOOTH_FACTOR * ((rawAzimuth - smoothAzimuth + 540.0F) % 360.0F - 180.0F);
                smoothAzimuth = (smoothAzimuth + 360.0F) % 360.0F;
                long now = System.currentTimeMillis();
                if (now - lastUpdateTime > UPDATE_INTERVAL_MS) {
                    lastUpdateTime = now;
                    currentAzimuth = smoothAzimuth;
                    currentBaguaIndex = ((int) Math.floor((currentAzimuth + 22.5F) / 45.0F)) % 8;
                    if (currentBaguaIndex < 0) {
                        currentBaguaIndex += 8;
                    }
                    invalidate();
                    if ((null != onBaguaChangeListener) && ((lastReportedAzimuth < 0) || (Math.abs(currentAzimuth - lastReportedAzimuth) >= azimuthThreshold))) {
                        lastReportedAzimuth = currentAzimuth;
                        onBaguaChangeListener.onBaguaChange(getCurrentBaguaName(), getCurrentDirection(), getCurrentElement(), getCurrentMeaning(), getCurrentDescription(), getCurrentSymbol());
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public String getCurrentBaguaName() {
        return BAGUA[currentBaguaIndex];
    }

    public String getCurrentDirection() {
        return DIRECTIONS[currentBaguaIndex];
    }

    public String getCurrentElement() {
        return BAGUA_ELEMENT[currentBaguaIndex];
    }

    public String getCurrentMeaning() {
        return BAGUA_MEANING[currentBaguaIndex];
    }

    public String getCurrentDescription() {
        return BAGUA_DESCRIPTION[currentBaguaIndex];
    }

    public String getCurrentSymbol() {
        return BAGUA_SYMBOLS[currentBaguaIndex];
    }

    @Override
    public void setBackgroundColor(int color) {
        backgroundColor = color;
        invalidate();
    }

    public void setCircleColor(int color) {
        circleColor = color;
        invalidate();
    }

    public void setCircleStrokes(float o, float m, float i) {
        outerCircleStroke = o;
        middleCircleStroke = m;
        innerCircleStroke = i;
        invalidate();
    }

    public void setCircleRadius(float o, float m, float i) {
        outerCircleRadius = o;
        middleCircleRadius = m;
        innerCircleRadius = i;
        invalidate();
    }

    public void setGridLine(float stroke, int color) {
        gridLineStroke = stroke;
        gridLineColor = color;
        invalidate();
    }

    public void setDirectionText(float size, int color, boolean bold, float radius) {
        directionTextSize = size;
        directionTextColor = color;
        directionTextBold = bold;
        directionRadius = radius;
        invalidate();
    }

    public void setSymbolText(float size, int color, boolean bold, float radius) {
        symbolTextSize = size;
        symbolTextColor = color;
        symbolTextBold = bold;
        symbolRadius = radius;
        invalidate();
    }

    public void setBaguaText(float size, int color, boolean bold, float radius) {
        baguaTextSize = size;
        baguaTextColor = color;
        baguaTextBold = bold;
        baguaRadius = radius;
        invalidate();
    }

    public void setMeaningText(float size, int color, boolean bold, float radius) {
        meaningTextSize = size;
        meaningTextColor = color;
        meaningTextBold = bold;
        meaningRadius = radius;
        invalidate();
    }

    public void setTaJiColor(int o, int m, int i) {
        taiJiOuterColor = o;
        taiJiMiddleColor = m;
        taiJiInnerColor = i;
        invalidate();
    }

    public void setTaiJiRadius(float o, float m, float i) {
        taiJiOuterRadius = o;
        taiJiMiddleRadius = m;
        taiJiInnerRadius = i;
        invalidate();
    }

    public void setNeedleColor(int n, int s, int c, int ci) {
        needleNorthColor = n;
        needleSouthColor = s;
        needleCenterColor = c;
        needleCenterInnerColor = ci;
        invalidate();
    }

    public void setNeedleSize(float l, float cr, float cir) {
        needleLength = l;
        needleCenterRadius = cr;
        needleCenterInnerRadius = cir;
        invalidate();
    }
}