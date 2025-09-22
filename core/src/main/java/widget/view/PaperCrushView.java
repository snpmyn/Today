package widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import util.datetime.CurrentTimeMillisClock;

/**
 * @decs: 纸张粉碎视图
 * @author: 郑少鹏
 * @date: 2025/9/13 17:57
 * @version: v 1.0
 */
public class PaperCrushView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private final Path tmpPath = new Path();
    /**
     * 粒子池
     */
    private final Deque<PaperParticle> particlePool = new ArrayDeque<>(200);
    private final List<PaperParticle> centerParticles = new ArrayList<>();
    private final List<PaperParticle> topParticles = new ArrayList<>();
    private float maxRadius, currentRadius = 0.0F;
    private int centerX, centerY;
    private float volume = 0.0F;
    /**
     * 光圈渐变缓存
     */
    private RadialGradient cachedGradient;
    private float cachedRadius = -1;
    /**
     * FPS 监控
     */
    private long lastFrameTime = 0;
    private float fps = 60.0F;
    private int maxCenterParticles = 150;
    private int maxTopParticles = 80;

    public PaperCrushView(Context context) {
        super(context);
        init();
    }

    public PaperCrushView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 200; i++) {
            particlePool.add(new PaperParticle());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = (w / 2);
        centerY = (h / 2);
        maxRadius = (Math.min(w, h) / 3.0F);
    }

    public void setVolume(final float v) {
        float clipped = Math.max(0.0F, Math.min(1.0F, v));
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setVolumeOnUiThread(clipped);
        } else {
            post(() -> setVolumeOnUiThread(clipped));
        }
    }

    private void setVolumeOnUiThread(float v) {
        this.volume = v;
        generateCenterParticles(v);
        generateTopParticles(v);
        currentRadius = (maxRadius * v);
        invalidate();
    }

    private void generateCenterParticles(float volume) {
        int count = (int) (volume * 30);
        if (volume > 0.8F) {
            count += 15;
        }
        if ((centerParticles.size() + count) > maxCenterParticles) {
            count = Math.max(0, maxCenterParticles - centerParticles.size());
        }
        for (int i = 0; i < count; i++) {
            PaperParticle p = obtainParticle();
            float width = (random.nextFloat() * 20 + 10);
            float height = (width * (0.6F + random.nextFloat() * 0.4F));
            float angle = (random.nextFloat() * 360.0F);
            float speed = (random.nextFloat() * 6 + 2);
            float vx = (float) Math.cos(Math.toRadians(angle)) * speed;
            float vy = (float) Math.sin(Math.toRadians(angle)) * speed;
            p.reset(centerX, centerY, width, height, Color.argb(255, 255, 180 + random.nextInt(55), 180 + random.nextInt(55)), vx, vy, random.nextFloat() * 360, (random.nextFloat() - 0.5F) * 10);
            centerParticles.add(p);
        }
    }

    private void generateTopParticles(float volume) {
        int count = (int) (volume * 10);
        if ((topParticles.size() + count) > maxTopParticles) {
            count = Math.max(0, maxTopParticles - topParticles.size());
        }
        for (int i = 0; i < count; i++) {
            PaperParticle p = obtainParticle();
            float width = (random.nextFloat() * 15 + 5);
            float height = (width * (0.5F + random.nextFloat() * 0.5F));
            float x = (getWidth() > 0) ? (random.nextFloat() * getWidth()) : centerX;
            float y = (random.nextFloat() * 50);
            float vy = (random.nextFloat() * 2 + 1);
            p.reset(x, y, width, height, Color.argb(120, 255, 200 + random.nextInt(55), 200 + random.nextInt(55)), 0, vy, random.nextFloat() * 360, (random.nextFloat() - 0.5F) * 5);
            topParticles.add(p);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        long now = System.nanoTime();
        if (lastFrameTime > 0) {
            float frameMs = ((now - lastFrameTime) / 1_000_000.0F);
            fps = (1000.0F / Math.max(1.0F, frameMs));
            adjustParticleLimits();
        }
        lastFrameTime = now;
        super.onDraw(canvas);
        // 光圈呼吸 + 动态颜色
        if (currentRadius > 0) {
            float time = CurrentTimeMillisClock.getInstance().now() % 1000 / 1000.0F;
            float radius = (currentRadius * (1 + 0.15F * (float) Math.sin(time * 2 * Math.PI)));
            int centerRed = 255;
            int centerGreen = (170 + (int) (30 * Math.sin(time * 2 * Math.PI)));
            int centerBlue = (170 + (int) (30 * Math.sin(time * 2 * Math.PI)));
            int alpha = 180;
            if ((null == cachedGradient) || (Math.abs(radius - cachedRadius) > 2.0F)) {
                cachedGradient = new RadialGradient(centerX, centerY, Math.max(1.0F, radius), Color.argb(alpha, centerRed, centerGreen, centerBlue), Color.argb(0, 255, 200, 200), Shader.TileMode.CLAMP);
                cachedRadius = radius;
            }
            paint.setShader(cachedGradient);
            canvas.drawCircle(centerX, centerY, radius, paint);
            paint.setShader(null);
        }
        drawParticles(canvas, topParticles, getHeight() / 2.0F);
        drawParticles(canvas, centerParticles, getHeight());
        if (!topParticles.isEmpty() || !centerParticles.isEmpty() || (volume > 0)) {
            postInvalidateOnAnimation();
        } else if (currentRadius > 0) {
            currentRadius *= 0.9F;
            if (currentRadius < 1.0F) currentRadius = 0;
            postInvalidateOnAnimation();
        }
    }

    private void adjustParticleLimits() {
        if (fps < 30.0F) {
            maxCenterParticles = Math.max(50, maxCenterParticles - 5);
            maxTopParticles = Math.max(30, maxTopParticles - 2);
        } else if (fps > 50.0F) {
            maxCenterParticles = Math.min(150, maxCenterParticles + 2);
            maxTopParticles = Math.min(80, maxTopParticles + 1);
        }
    }

    private void drawParticles(Canvas canvas, @NonNull List<PaperParticle> list, float maxY) {
        Iterator<PaperParticle> it = list.iterator();
        while (it.hasNext()) {
            PaperParticle p = it.next();
            p.vy += 0.05F;
            p.x += (p.vx + (float) Math.sin(p.y / 15.0F) * 1.5F);
            p.y += p.vy;
            p.rotation += p.rotationSpeed;
            p.alpha -= 4;
            if ((p.alpha <= 0) || (p.y > maxY)) {
                it.remove();
                recycleParticle(p);
                continue;
            }
            paint.setColor(p.color);
            paint.setAlpha(Math.max(p.alpha, 0));
            canvas.save();
            canvas.rotate(p.rotation, p.x, p.y);
            tmpPath.reset();
            tmpPath.moveTo(p.x - p.width / 2, p.y - p.height / 2);
            tmpPath.lineTo(p.x + p.width / 2, p.y - p.height / 2);
            tmpPath.lineTo(p.x + p.width / 2, p.y + p.height / 2 - p.width / 4);
            tmpPath.lineTo(p.x - p.width / 2, p.y + p.height / 2);
            tmpPath.close();
            canvas.drawPath(tmpPath, paint);
            canvas.restore();
        }
    }

    private PaperParticle obtainParticle() {
        return particlePool.isEmpty() ? new PaperParticle() : particlePool.poll();
    }

    private void recycleParticle(@NonNull PaperParticle p) {
        p.reset(0, 0, 0, 0, 0, 0, 0, 0, 0);
        if (particlePool.size() < 200) {
            particlePool.offer(p);
        }
    }

    static class PaperParticle {
        float x, y, width, height, vx, vy, rotation, rotationSpeed;
        int color, alpha;

        void reset(float x, float y, float width, float height, int color, float vx, float vy, float rotation, float rotationSpeed) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.vx = vx;
            this.vy = vy;
            this.rotation = rotation;
            this.rotationSpeed = rotationSpeed;
            this.alpha = 255;
        }
    }
}