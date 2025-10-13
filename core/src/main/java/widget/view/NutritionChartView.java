package widget.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import util.list.ListUtils;

/**
 * @decs: 营养图表视图
 * @author: 郑少鹏
 * @date: 2025/10/3 19:44
 * @version: v 1.0
 */
public class NutritionChartView extends View {
    private Paint paintArc, paintLabelBg, paintLabelText;
    private Paint paintCenterValue, paintCenterUnit, paintCenterSubTitle;
    private RectF rectF;
    private List<NutritionPart> dataList = new ArrayList<>();
    private float totalValue = 0;
    private float animatedProgress = 0.0F;
    private float strokeWidth = 40.0F;
    private float gapAngle = 2.0F;
    private float labelTextSize = 36.0F;
    private boolean labelBold = false;
    private int labelBackgroundColor = Color.argb(180, 240, 240, 240);
    private float labelPadding = 16.0F;
    private float labelArcMargin = 8.0F;
    private int labelTextColor = Color.BLACK;
    private LabelMode labelMode = LabelMode.PERCENT;
    private String unit = "Kcal";
    private boolean showCenterText = true;
    private int centerValueColor = Color.BLACK;
    private boolean centerValueBold = false;
    private float centerValueSize = 48.0F;
    private int centerUnitColor = Color.GRAY;
    private boolean centerUnitBold = false;
    private float centerUnitSize = 32.0F;
    private int centerUnitPosition = 0;
    private float centerUnitMargin = 10.0F;
    private String centerSubTitle = "";
    private int centerSubTitleColor = Color.GRAY;
    private float centerSubTitleSize = 28.0F;
    private boolean centerSubTitleBold = false;
    private float centerSubTitleMargin = 10.0F;
    private final int[] defaultColors = {Color.parseColor("#FF8A65"), Color.parseColor("#FFB74D"), Color.parseColor("#FFD54F"), Color.parseColor("#FFCDD2"), Color.parseColor("#F8BBD0"), Color.parseColor("#F48FB1"), Color.parseColor("#CE93D8"), Color.parseColor("#B39DDB"), Color.parseColor("#90CAF9"), Color.parseColor("#A5D6A7"), Color.parseColor("#FFE082"), Color.parseColor("#FFAB91"), Color.parseColor("#FFF59D"), Color.parseColor("#FFE0B2"), Color.parseColor("#FFCCBC")};
    private final Random random = new Random();

    public enum LabelMode {VALUE, PERCENT}

    public NutritionChartView(Context context) {
        super(context);
        init();
    }

    public NutritionChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public NutritionChartView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init();
    }

    private void init() {
        paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setStrokeCap(Paint.Cap.ROUND);
        paintArc.setStrokeWidth(strokeWidth);
        paintLabelText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLabelText.setColor(labelTextColor);
        paintLabelText.setTextSize(labelTextSize);
        paintLabelText.setTextAlign(Paint.Align.CENTER);
        paintLabelBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLabelBg.setColor(labelBackgroundColor);
        paintLabelBg.setStyle(Paint.Style.FILL);
        paintCenterValue = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCenterValue.setTextAlign(Paint.Align.LEFT);
        paintCenterUnit = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCenterUnit.setTextAlign(Paint.Align.LEFT);
        paintCenterSubTitle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCenterSubTitle.setTextAlign(Paint.Align.LEFT);
        rectF = new RectF();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if ((null == dataList) || ListUtils.listIsEmpty(dataList)) {
            return;
        }
        int width = getWidth(), height = getHeight();
        float centerX = width / 2.0F, centerY = height / 2.0F;
        int radius = (Math.min(width, height) / 2 - 60);
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float startAngle = -90.0F;
        float capAngle = (strokeWidth / 2.0F) / radius * 180.0F / (float) Math.PI;
        for (NutritionPart nutritionPart : dataList) {
            float sweepAngle = (360.0F * (nutritionPart.value / totalValue) * animatedProgress);
            float drawSweep = Math.max(0, sweepAngle - 2 * capAngle - gapAngle);
            paintArc.setStrokeWidth(strokeWidth);
            paintArc.setColor(nutritionPart.color);
            paintArc.setStrokeCap(Paint.Cap.ROUND);
            if (drawSweep > 0) {
                canvas.drawArc(rectF, startAngle + capAngle + gapAngle / 2.0F, drawSweep, false, paintArc);
            }
            if ((animatedProgress >= 1.0F) && (sweepAngle > 0)) {
                drawLabel(canvas, nutritionPart, startAngle, sweepAngle, radius, centerX, centerY, capAngle);
            }
            startAngle += sweepAngle;
        }
        if (showCenterText) {
            drawCenterText(canvas, centerX, centerY);
        }
    }

    private void drawLabel(Canvas canvas, NutritionPart part, float startAngle, float sweepAngle, float radius, float centerX, float centerY, float capAngle) {
        boolean singleLabel = (dataList.size() == 1);
        String labelText = "";
        if (labelMode == LabelMode.VALUE) {
            labelText = (part.label + " " + ((int) part.value));
        } else if (labelMode == LabelMode.PERCENT) {
            labelText = (part.label + " " + String.format(Locale.getDefault(), "%.0f%%", part.value / totalValue * 100.0F));
        }
        paintLabelText.setTextSize(labelTextSize);
        paintLabelText.setFakeBoldText(labelBold);
        paintLabelText.setTextAlign(Paint.Align.CENTER);
        paintLabelText.setColor(labelTextColor);
        paintLabelBg.setColor(labelBackgroundColor);
        float textHeight = (paintLabelText.descent() - paintLabelText.ascent());
        float safeLabelRadius = (radius - strokeWidth / 2.0F - labelPadding - textHeight / 2.0F - labelArcMargin);
        float middleAngle = (startAngle + sweepAngle / 2.0F);
        // 小弧段旋转显示
        if (!singleLabel && (sweepAngle < 30.0F)) {
            float angleRad = (float) Math.toRadians(middleAngle);
            float labelX = (centerX + (float) Math.cos(angleRad) * safeLabelRadius);
            float labelY = (centerY + (float) Math.sin(angleRad) * safeLabelRadius);
            canvas.save();
            canvas.translate(labelX, labelY);
            float rotateAngle = middleAngle;
            if ((middleAngle > 90.0F) && (middleAngle < 270.0F)) {
                rotateAngle += 180.0F;
            }
            canvas.rotate(rotateAngle);
            canvas.drawText(labelText, 0, 0, paintLabelText);
            canvas.restore();
            return;
        }
        // 正常大弧段或单段
        float sweepRadians = ((sweepAngle - 2 * capAngle - gapAngle) * (float) Math.PI / 180.0F);
        float minLabelWidth = 60.0F;
        float maxLabelWidth = singleLabel ? (2 * radius) : Math.max(2 * safeLabelRadius * (float) Math.sin(sweepRadians / 2.0F) - 2 * labelPadding, minLabelWidth);
        float textWidth = paintLabelText.measureText(labelText);
        if (textWidth > maxLabelWidth) {
            float adjustedTextSize = (labelTextSize * maxLabelWidth / textWidth);
            paintLabelText.setTextSize(adjustedTextSize);
            textWidth = paintLabelText.measureText(labelText);
            textHeight = (paintLabelText.descent() - paintLabelText.ascent());
        }
        float labelX = (centerX + (float) Math.cos(Math.toRadians(middleAngle)) * safeLabelRadius);
        float labelY = (centerY + (float) Math.sin(Math.toRadians(middleAngle)) * safeLabelRadius);
        RectF bgRect = new RectF(labelX - textWidth / 2.0F - labelPadding, labelY - textHeight / 2.0F - labelPadding, labelX + textWidth / 2.0F + labelPadding, labelY + textHeight / 2.0F + labelPadding);
        canvas.drawRoundRect(bgRect, 12.0F, 12.0F, paintLabelBg);
        float bgCenterY = ((bgRect.top + bgRect.bottom) / 2.0F);
        float textBaseY = (bgCenterY - (paintLabelText.descent() + paintLabelText.ascent()) / 2.0F);
        canvas.drawText(labelText, labelX, textBaseY, paintLabelText);
        paintLabelText.setTextSize(labelTextSize);
    }

    private void drawCenterText(Canvas canvas, float centerX, float centerY) {
        float yOffset = 0.0F;
        if (!TextUtils.isEmpty(centerSubTitle)) {
            paintCenterSubTitle.setColor(centerSubTitleColor);
            paintCenterSubTitle.setTextSize(centerSubTitleSize);
            paintCenterSubTitle.setFakeBoldText(centerSubTitleBold);
            float subWidth = paintCenterSubTitle.measureText(centerSubTitle);
            float subX = (centerX - subWidth / 2.0F);
            float subY = (centerY - centerValueSize / 2.0F - centerSubTitleMargin);
            canvas.drawText(centerSubTitle, subX, subY, paintCenterSubTitle);
            yOffset += (centerSubTitleSize + centerSubTitleMargin);
        }
        String valueText = String.valueOf((int) totalValue);
        paintCenterValue.setColor(centerValueColor);
        paintCenterValue.setTextSize(centerValueSize);
        paintCenterValue.setFakeBoldText(centerValueBold);
        float valueWidth = paintCenterValue.measureText(valueText);
        float valueHeight = (paintCenterValue.descent() - paintCenterValue.ascent());
        paintCenterUnit.setColor(centerUnitColor);
        paintCenterUnit.setTextSize(centerUnitSize);
        paintCenterUnit.setFakeBoldText(centerUnitBold);
        float unitWidth = paintCenterUnit.measureText(unit);
        float unitHeight = (paintCenterUnit.descent() - paintCenterUnit.ascent());
        if (centerUnitPosition == 0) {
            float totalWidth = (valueWidth + centerUnitMargin + unitWidth);
            float startX = (centerX - totalWidth / 2.0F);
            float baseY = (centerY + yOffset / 2.0F + valueHeight / 4.0F);
            canvas.drawText(valueText, startX, baseY, paintCenterValue);
            canvas.drawText(unit, startX + valueWidth + centerUnitMargin, baseY, paintCenterUnit);
        } else {
            float baseYValue = (centerY + yOffset / 2.0F + valueHeight / 4.0F);
            float valueX = (centerX - valueWidth / 2.0F);
            canvas.drawText(valueText, valueX, baseYValue, paintCenterValue);
            float unitX = (centerX - unitWidth / 2.0F);
            float unitY = (baseYValue + unitHeight + centerUnitMargin);
            canvas.drawText(unit, unitX, unitY, paintCenterUnit);
        }
    }

    public void setData(@NonNull List<NutritionPart> nutritionPartList) {
        this.dataList = nutritionPartList;
        totalValue = 0;
        for (int i = 0; i < nutritionPartList.size(); i++) {
            NutritionPart part = nutritionPartList.get(i);
            totalValue += part.value;
            if (part.color == 0) {
                if (i < defaultColors.length) {
                    part.color = defaultColors[i];
                } else {
                    part.color = Color.rgb(random.nextInt(200) + 30, random.nextInt(200) + 30, random.nextInt(200) + 30);
                }
            }
        }
        startAnimation();
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0F, 1.0F);
        animator.setDuration(1500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animatedProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void setStrokeWidth(float width) {
        strokeWidth = width;
        paintArc.setStrokeWidth(width);
        invalidate();
    }

    public void setGapAngle(float gap) {
        gapAngle = gap;
        invalidate();
    }

    public void setLabelTextSize(float size) {
        labelTextSize = size;
        invalidate();
    }

    public void setLabelBold(boolean bold) {
        labelBold = bold;
        invalidate();
    }

    public void setLabelBackgroundColor(int color) {
        labelBackgroundColor = color;
        paintLabelBg.setColor(color);
        invalidate();
    }

    public void setLabelTextColor(int color) {
        labelTextColor = color;
        invalidate();
    }

    public void setLabelPadding(float padding) {
        labelPadding = padding;
        invalidate();
    }

    public void setLabelMode(LabelMode mode) {
        labelMode = mode;
        invalidate();
    }

    public void setLabelArcMargin(float margin) {
        labelArcMargin = margin;
        invalidate();
    }

    public void setCenterValueColor(int color) {
        centerValueColor = color;
        invalidate();
    }

    public void setCenterValueBold(boolean bold) {
        centerValueBold = bold;
        invalidate();
    }

    public void setCenterValueSize(float size) {
        centerValueSize = size;
        invalidate();
    }

    public void setUnit(String unit) {
        this.unit = unit;
        invalidate();
    }

    public void setCenterUnitColor(int color) {
        centerUnitColor = color;
        invalidate();
    }

    public void setCenterUnitBold(boolean bold) {
        centerUnitBold = bold;
        invalidate();
    }

    public void setCenterUnitSize(float size) {
        centerUnitSize = size;
        invalidate();
    }

    public void setCenterUnitPosition(int position) {
        if ((position == 0) || (position == 1)) {
            centerUnitPosition = position;
            invalidate();
        }
    }

    public void setCenterUnitMargin(float margin) {
        centerUnitMargin = margin;
        invalidate();
    }

    public void setCenterSubTitle(String text) {
        centerSubTitle = text;
        invalidate();
    }

    public void setCenterSubTitleColor(int color) {
        centerSubTitleColor = color;
        invalidate();
    }

    public void setCenterSubTitleSize(float size) {
        centerSubTitleSize = size;
        invalidate();
    }

    public void setCenterSubTitleBold(boolean bold) {
        centerSubTitleBold = bold;
        invalidate();
    }

    public void setCenterSubTitleMargin(float margin) {
        centerSubTitleMargin = margin;
        invalidate();
    }

    public void setShowCenterText(boolean show) {
        showCenterText = show;
        invalidate();
    }

    public static class NutritionPart {
        public float value;
        public int color;
        public String label;

        public NutritionPart(float value, int color, String label) {
            this.value = value;
            this.color = color;
            this.label = label;
        }
    }
}