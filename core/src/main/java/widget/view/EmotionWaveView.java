package widget.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import com.zsp.core.R;

import util.array.ArrayUtils;

/**
 * @decs: 心情波视图
 * @author: 郑少鹏
 * @date: 2025/9/13 17:49
 * @version: v 1.0
 */
public class EmotionWaveView extends View {
    private Path seaPath;
    private Paint seaPaint;
    private Paint textPaint;
    private Paint circlePaint;
    private Paint ripplePaint;
    private float volume = 0.0F;
    /**
     * 心情文本数组
     */
    private String[] emotionTexts;
    private float seaOffset = 0.0F;
    /**
     * 心情文本
     */
    private String emotionText = "平静";
    private float breathingScale = 1.0F;
    private float rippleProgress = 0.0F;

    public EmotionWaveView(Context context) {
        super(context);
        init();
    }

    public EmotionWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmotionWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.parseColor("#FFB6B9"));
        ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint.setStyle(Paint.Style.STROKE);
        ripplePaint.setStrokeWidth(6.0F);
        seaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        seaPaint.setStyle(Paint.Style.FILL);
        seaPaint.setColor(Color.parseColor("#A6DCEC"));
        seaPath = new Path();
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#444444"));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40.0F);
        textPaint.setFakeBoldText(true);
        startBreathingAnimation();
        startRippleAnimation();
        startSeaAnimation();
    }

    private void startBreathingAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.95F, 1.05F);
        animator.setDuration(3000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(animation -> {
            breathingScale = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    private void startRippleAnimation() {
        ValueAnimator rippleAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        rippleAnimator.setDuration(4000);
        rippleAnimator.setInterpolator(new LinearInterpolator());
        rippleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rippleAnimator.addUpdateListener(animation -> {
            rippleProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        rippleAnimator.start();
    }

    private void startSeaAnimation() {
        ValueAnimator seaAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        seaAnimator.setDuration(3000);
        seaAnimator.setInterpolator(new LinearInterpolator());
        seaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        seaAnimator.addUpdateListener(animation -> {
            seaOffset = (float) animation.getAnimatedValue();
            invalidate();
        });
        seaAnimator.start();
    }

    public void setVolume(float normalizedVolume) {
        this.volume = Math.max(0.0F, Math.min(normalizedVolume, 1.0F));
        if (this.volume <= 0.0F) {
            emotionText = canToggle() ? emotionTexts[0] : getContext().getString(R.string.calm);
            circlePaint.setColor(Color.parseColor("#FFB6B9"));
        } else {
            updateEmotion();
        }
        invalidate();
    }

    private void updateEmotion() {
        if ((volume > 0.0F) && (volume < 0.1F)) {
            emotionText = canToggle() ? emotionTexts[0] : getContext().getString(R.string.calm);
            circlePaint.setColor(Color.parseColor("#FFB6B9"));
        } else if (volume < 0.3F) {
            emotionText = canToggle() ? emotionTexts[1] : getContext().getString(R.string.gentle);
            circlePaint.setColor(Color.parseColor("#FFD3B6"));
        } else if (volume < 0.5F) {
            emotionText = canToggle() ? emotionTexts[2] : getContext().getString(R.string.relax);
            circlePaint.setColor(Color.parseColor("#D6EAF8"));
        } else if (volume < 0.7F) {
            emotionText = canToggle() ? emotionTexts[3] : getContext().getString(R.string.breathe);
            circlePaint.setColor(Color.parseColor("#E8DAEF"));
        } else {
            emotionText = canToggle() ? emotionTexts[4] : getContext().getString(R.string.calmTwo);
            circlePaint.setColor(Color.parseColor("#F9E79F"));
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float centerX = (width / 2.0F);
        float centerY = (height / 2.0F);
        drawSea(canvas, width, height);
        float baseRadius = (Math.min(width, height) / 6.0F);
        float dynamicRadius = (baseRadius + volume * 200.0F);
        float finalRadius = (dynamicRadius * breathingScale);
        canvas.drawCircle(centerX, centerY, finalRadius, circlePaint);
        for (int i = 0; i < 3; i++) {
            float progressOffset = ((rippleProgress + i * 0.33F) % 1.0F);
            float rippleRadius = (finalRadius + progressOffset * 300.0F);
            int startColor = adjustAlpha(Color.parseColor("#FFB6B9"), 0.4F * (1 - progressOffset));
            int endColor = adjustAlpha(Color.parseColor("#FFD3B6"), 0.0F);
            @SuppressLint("DrawAllocation") RadialGradient radialGradient = new RadialGradient(centerX, centerY, rippleRadius, startColor, endColor, Shader.TileMode.CLAMP);
            ripplePaint.setShader(radialGradient);
            canvas.drawCircle(centerX, centerY, rippleRadius, ripplePaint);
        }
        float textY = (centerY - (textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(emotionText, centerX, textY, textPaint);
    }

    private void drawSea(Canvas canvas, int width, int height) {
        seaPath.reset();
        int waveCount = 2;
        // 平静状态下基础波动
        float minWaveHeight = 20.0F;
        // 音量越大
        // 波浪越高
        float maxWaveHeight = (minWaveHeight + volume * 46);
        int baseY = (int) (height * 0.75);
        seaPath.moveTo(0, baseY);
        for (int x = 0; x <= width; x++) {
            float y = (float) (Math.sin(2 * Math.PI * (x + seaOffset * width) / ((double) width / waveCount)) * maxWaveHeight);
            seaPath.lineTo(x, baseY + y);
        }
        seaPath.lineTo(width, height);
        seaPath.lineTo(0, height);
        seaPath.close();
        canvas.drawPath(seaPath, seaPaint);
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.min(255, Math.max(0, (int) (Color.alpha(color) * factor)));
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * 设置心情文本数组并刷新
     *
     * @param emotionTexts 心情文本数组
     */
    public void setEmotionTextsAndInvalidate(@NonNull String[] emotionTexts) {
        if (ArrayUtils.arrayIsEmpty(emotionTexts)) {
            return;
        }
        this.emotionTexts = emotionTexts;
        // 刷新
        invalidate();
    }

    /**
     * 恢复
     */
    public void restore() {
        emotionTexts = null;
    }

    /**
     * 是否可以切换
     *
     * @return 可以切换否
     */
    private boolean canToggle() {
        return ArrayUtils.arrayIsNotEmpty(emotionTexts);
    }
}