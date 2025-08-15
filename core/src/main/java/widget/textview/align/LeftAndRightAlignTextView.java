package widget.textview.align;

import static android.Manifest.permission.VIBRATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
import android.text.Selection;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.widget.AppCompatEditText;

import com.zsp.core.R;

import java.util.Objects;

import timber.log.Timber;
import util.density.DensityUtils;
import util.keyboard.KeyboardUtils;
import util.screen.ScreenUtils;
import util.statusbar.StatusBarUtils;
import widget.toast.ToastKit;
import util.validate.CheckOutUtils;
import util.vibrator.VibratorUtils;
import widget.value.WidgetMagic;

/**
 * Created on 2022/3/21
 *
 * @author zsp
 * @desc 左右对齐 TextView
 */
public class LeftAndRightAlignTextView extends AppCompatEditText {
    private static final String TAG = LeftAndRightAlignTextView.class.getSimpleName();
    /**
     * 上下文
     */
    private final Context mContext;
    /**
     * 屏高
     */
    private int mScreenHeight;
    /**
     * 状态栏高
     */
    private int mStatusBarHeight;
    /**
     * 弹出菜单高
     */
    private int mActionMenuHeight;
    /**
     * 选中文本背景高亮色
     */
    private int mTextHighlightColor;
    /**
     * 触摸坐标
     */
    private float mTouchDownX = 0;
    private float mTouchDownY = 0;
    private float mTouchDownRawY = 0;
    /**
     * 长按否
     */
    private boolean areLongPress = false;
    /**
     * 长按事件结束后标记该事件
     */
    private boolean areLongPressTouchActionUp = false;
    /**
     * 长按震动否
     */
    private boolean areVibrator = false;
    /**
     * 两端对齐否（默 true）
     */
    private boolean areTextJustify;
    /**
     * 两端对齐否（默 false）
     */
    private boolean areForbiddenActionMenu;
    /**
     * 全选否
     */
    private boolean areActionSelectAll = false;
    /**
     * 起始行（按下）
     */
    private int mStartLine;
    /**
     * 字符串始位偏移量（按下）
     */
    private int mStartTextOffset;
    /**
     * 当前行（移动）
     */
    private int mCurrentLine;
    /**
     * 字符串当前位偏移量（移动）
     */
    private int mCurrentTextOffset;
    /**
     * 内容宽（无 padding）
     */
    private int mViewTextWidth;
    /**
     * 长按弹菜单
     */
    private PopupWindow mActionMenuPopupWindow;
    private ActionMenu mActionMenu = null;
    private OnClickListener mOnClickListener;
    private ActionMenuCallBack mActionMenuCallBack;
    /**
     * 菜单点击事件监听
     */
    private final OnClickListener mMenuClickListener = new OnClickListener() {
        @Override
        public void onClick(@NonNull View v) {
            String menuItemTitle = (String) v.getTag();
            // 选中字符开始和结束位
            int start = getSelectionStart();
            int end = getSelectionEnd();
            // 获选中字符
            String selectedStr;
            if ((start < 0) || (end < 0) || (end <= start)) {
                selectedStr = "";
            } else {
                selectedStr = Objects.requireNonNull(getText()).toString().substring(start, end);
            }
            switch (menuItemTitle) {
                // 全选
                case ActionMenu.DEFAULT_ACTION_MENU_ITEM_TITLE_SELECT_ALL:
                    if (areTextJustify) {
                        mStartLine = 0;
                        mCurrentLine = getLayout().getLineCount() - 1;
                        mStartTextOffset = 0;
                        mCurrentTextOffset = getLayout().getLineEnd(mCurrentLine);
                        areActionSelectAll = true;
                        LeftAndRightAlignTextView.this.invalidate();
                    }
                    Selection.selectAll(getEditableText());
                    break;
                // 复制
                case ActionMenu.DEFAULT_ACTION_MENU_ITEM_TITLE_COPY:
                    KeyboardUtils.copyText(mContext, selectedStr);
                    ToastKit.showShort(mContext.getString(R.string.copySuccessful));
                    hideActionMenu();
                    break;
                default:
                    // 自定
                    if (null != mActionMenuCallBack) {
                        mActionMenuCallBack.onActionMenuItemClick(menuItemTitle, selectedStr);
                    }
                    hideActionMenu();
                    break;
            }
        }
    };

    public LeftAndRightAlignTextView(Context context) {
        this(context, null);
    }

    public LeftAndRightAlignTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftAndRightAlignTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LeftAndRightAlignTextView);
        areTextJustify = typedArray.getBoolean(R.styleable.LeftAndRightAlignTextView_leftAndRightAlignTextViewTextJustify, true);
        areForbiddenActionMenu = typedArray.getBoolean(R.styleable.LeftAndRightAlignTextView_leftAndRightAlignTextViewForbiddenActionMenu, false);
        mTextHighlightColor = typedArray.getColor(R.styleable.LeftAndRightAlignTextView_leftAndRightAlignTextViewTextHeightColor, 0x60ffeb3b);
        typedArray.recycle();
        init();
    }

    private void init() {
        mScreenHeight = ScreenUtils.screenHeight(mContext);
        mStatusBarHeight = StatusBarUtils.getStatusBarHeight(mContext);
        mActionMenuHeight = DensityUtils.dipToPxByFloat(mContext, 45.0F);
        if (areTextJustify) {
            setGravity(Gravity.TOP);
        }
        setTextIsSelectable(true);
        setCursorVisible(false);
        setTextHighlightColor(mTextHighlightColor);
    }

    @Override
    public boolean getDefaultEditable() {
        // false 屏蔽系统自带菜单
        return false;
    }

    public void setAreTextJustify(boolean areTextJustify) {
        this.areTextJustify = areTextJustify;
    }

    public void setAreForbiddenActionMenu(boolean areForbiddenActionMenu) {
        this.areForbiddenActionMenu = areForbiddenActionMenu;
    }

    public void setTextHighlightColor(int color) {
        this.mTextHighlightColor = color;
        String colorHex = String.format("%08X", color);
        colorHex = "#40" + colorHex.substring(2);
        setHighlightColor(Color.parseColor(colorHex));
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        if (null != l) {
            mOnClickListener = l;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresPermission(VIBRATE)
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        Layout layout = getLayout();
        int currentLine;
        int triggerLongPressTimeThreshold = 300;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null == mActionMenu) {
                    mActionMenu = createActionMenu();
                }
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                mTouchDownRawY = event.getRawY();
                areLongPress = false;
                areVibrator = false;
                areLongPressTouchActionUp = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!areForbiddenActionMenu || mActionMenu.getChildCount() == 0) {
                    currentLine = layout.getLineForVertical(getScrollY() + (int) event.getY());
                    int wordOffsetMove = layout.getOffsetForHorizontal(currentLine, (int) event.getX());
                    int triggerLongPressDistanceThreshold = 10;
                    if (((event.getEventTime() - event.getDownTime()) >= triggerLongPressTimeThreshold) && (Math.abs(event.getX() - mTouchDownX) < triggerLongPressDistanceThreshold) && (Math.abs(event.getY() - mTouchDownY) < triggerLongPressDistanceThreshold)) {
                        areLongPress = true;
                        areLongPressTouchActionUp = false;
                        mStartLine = currentLine;
                        mStartTextOffset = wordOffsetMove;
                        if (!areVibrator) {
                            VibratorUtils.oneShotVibration(mContext, 30, 100);
                            areVibrator = true;
                        }
                    }
                    if (areLongPress) {
                        if (!areTextJustify) {
                            requestFocus();
                        }
                        mCurrentLine = currentLine;
                        mCurrentTextOffset = wordOffsetMove;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        Selection.setSelection(getEditableText(), Math.min(mStartTextOffset, wordOffsetMove), Math.max(mStartTextOffset, wordOffsetMove));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (areLongPress) {
                    currentLine = layout.getLineForVertical(getScrollY() + (int) event.getY());
                    mCurrentLine = currentLine;
                    mCurrentTextOffset = layout.getOffsetForHorizontal(currentLine, (int) event.getX());
                    int maxOffset = getEditableText().length() - 1;
                    if (mStartTextOffset > maxOffset) {
                        mStartTextOffset = maxOffset;
                    }
                    if (mCurrentTextOffset > maxOffset) {
                        mCurrentTextOffset = maxOffset;
                    }
                    if (mCurrentTextOffset == mStartTextOffset) {
                        if (mCurrentTextOffset == layout.getLineEnd(currentLine) - 1) {
                            mStartTextOffset -= 1;
                        } else {
                            mCurrentTextOffset += 1;
                        }
                    }
                    Selection.setSelection(getEditableText(), Math.min(mStartTextOffset, mCurrentTextOffset), Math.max(mStartTextOffset, mCurrentTextOffset));
                    showActionMenu(calculatorActionMenuPositionOnY((int) mTouchDownRawY, (int) event.getRawY()), mActionMenu);
                    areLongPressTouchActionUp = true;
                    areLongPress = false;
                } else if ((event.getEventTime() - event.getDownTime()) < triggerLongPressTimeThreshold) {
                    if (null != mOnClickListener) {
                        mOnClickListener.onClick(this);
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 创动作菜单
     *
     * @return 动作菜单
     */
    @NonNull
    private ActionMenu createActionMenu() {
        // 创菜单
        ActionMenu actionMenu = new ActionMenu(mContext);
        // 移除默菜单项否
        boolean areRemoveDefaultItem = false;
        if (null != mActionMenuCallBack) {
            areRemoveDefaultItem = mActionMenuCallBack.onCreateActionMenu(actionMenu);
        }
        if (!areRemoveDefaultItem) {
            // 添默菜单项
            actionMenu.addDefaultMenuItem();
        }
        // 添自定菜单项
        actionMenu.addCustomMenuItem();
        // 获焦
        actionMenu.setFocusable(true);
        actionMenu.setFocusableInTouchMode(true);
        if (actionMenu.getChildCount() != 0) {
            // 菜单项监听
            for (int i = 0; i < actionMenu.getChildCount(); i++) {
                actionMenu.getChildAt(i).setOnClickListener(mMenuClickListener);
            }
        }
        return actionMenu;
    }

    /**
     * 长按弹菜单
     *
     * @param offsetY    Y 偏移
     * @param actionMenu 菜单
     */
    private void showActionMenu(int offsetY, ActionMenu actionMenu) {
        mActionMenuPopupWindow = new PopupWindow(actionMenu, WindowManager.LayoutParams.WRAP_CONTENT, mActionMenuHeight, true);
        mActionMenuPopupWindow.setFocusable(true);
        mActionMenuPopupWindow.setOutsideTouchable(false);
        mActionMenuPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mActionMenuPopupWindow.showAtLocation(this, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, offsetY);
        mActionMenuPopupWindow.setOnDismissListener(() -> {
            Selection.removeSelection(getEditableText());
            // 分散对齐则菜单销毁后强刷避文本背景未消
            if (areTextJustify) {
                LeftAndRightAlignTextView.this.postInvalidate();
            }
        });
    }

    /**
     * 隐菜单
     */
    private void hideActionMenu() {
        if (null != mActionMenuPopupWindow) {
            mActionMenuPopupWindow.dismiss();
            mActionMenuPopupWindow = null;
        }
    }

    /**
     * 算菜单相对父布局 Y 偏移
     *
     * @param yOffsetStart 所选字符起始位相对屏 Y 偏移
     * @param yOffsetEnd   所选字符结束位相对屏 Y 偏移
     * @return 菜单相对父布局 Y 偏移
     */
    private int calculatorActionMenuPositionOnY(int yOffsetStart, int yOffsetEnd) {
        if (yOffsetStart > yOffsetEnd) {
            int temp = yOffsetStart;
            yOffsetStart = yOffsetEnd;
            yOffsetEnd = temp;
        }
        int actionMenuOffsetY;
        if (yOffsetStart < (mActionMenuHeight * WidgetMagic.INT_THREE / WidgetMagic.INT_TWO + mStatusBarHeight)) {
            if (yOffsetEnd > (mScreenHeight - mActionMenuHeight * WidgetMagic.INT_THREE / WidgetMagic.INT_TWO)) {
                // 菜单显屏中间
                actionMenuOffsetY = (mScreenHeight / 2 - mActionMenuHeight / 2);
            } else {
                // 菜单显所选文本下方
                actionMenuOffsetY = (yOffsetEnd + mActionMenuHeight / 2);
            }
        } else {
            // 菜单显所选文本上方
            actionMenuOffsetY = (yOffsetStart - mActionMenuHeight * 3 / 2);
        }
        return actionMenuOffsetY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Timber.e(TAG, "SelectableTextView", "onDraw");
        if (!areTextJustify) {
            // 无需两端对齐
            super.onDraw(canvas);
        } else {
            // textview 内容实宽
            mViewTextWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            // 重绘文本（两端对齐）
            drawTextWithJustify(canvas);
            // 长按、全选、手指滑动过快需绘背景
            // 避 ACTION_UP 后未绘背景
            if (areLongPress | areActionSelectAll | areLongPressTouchActionUp) {
                drawSelectedTextBackground(canvas);
                areActionSelectAll = false;
                areLongPressTouchActionUp = false;
            }
        }
    }

    /**
     * 重绘文字（两端对齐）
     *
     * @param canvas 画布
     */
    private void drawTextWithJustify(Canvas canvas) {
        // 画笔
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();
        String textString = Objects.requireNonNull(getText()).toString();
        // 当前所在行 Y 向偏移
        int currentLineOffsetY = getPaddingTop();
        // 使用 Math.round() 四舍五入
        currentLineOffsetY += Math.round(getTextSize());
        Layout layout = getLayout();
        // 循环每行绘文字
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            // TextView 每行内容
            String lineString = textString.substring(lineStart, lineEnd);
            // 每行字符串宽（不含字符间距）
            float desiredWidth = StaticLayout.getDesiredWidth(textString, lineStart, lineEnd, getPaint());
            if (areLineNeedJustify(lineString)) {
                // 末行无需重绘
                if (i == (layout.getLineCount() - 1)) {
                    canvas.drawText(lineString, getPaddingLeft(), currentLineOffsetY, textPaint);
                } else {
                    drawJustifyTextForLine(canvas, lineString, desiredWidth, currentLineOffsetY);
                }
            } else {
                canvas.drawText(lineString, getPaddingLeft(), currentLineOffsetY, textPaint);
            }
            // 更新行 Y 向偏移
            currentLineOffsetY += getLineHeight();
        }
    }

    /**
     * 绘选中文字背景
     *
     * @param canvas 画布
     */
    private void drawSelectedTextBackground(Canvas canvas) {
        if (mStartTextOffset == mCurrentTextOffset) {
            return;
        }
        // 文字背景高亮画笔
        Paint highLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highLightPaint.setStyle(Paint.Style.FILL);
        highLightPaint.setColor(mTextHighlightColor);
        highLightPaint.setAlpha(60);
        // 算开始位和结束位字符相对 view 最左侧 X 偏移
        float startToLeftPosition = calculatorCharPositionToLeft(mStartLine, mStartTextOffset);
        float currentToLeftPosition = calculatorCharPositionToLeft(mCurrentLine, mCurrentTextOffset);
        // 行高
        int lineHeight = getLineHeight();
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        // 创三矩形（所有选中行对应矩形、起始行左侧未选中文字对应的矩形、结束行右侧未选中文字对应的矩形）
        RectF rectAll, rectLt, rectRb;
        // 版本控制
        if (mStartTextOffset < mCurrentTextOffset) {
            rectAll = new RectF(paddingLeft, mStartLine * lineHeight + paddingTop, mViewTextWidth + paddingLeft, (mCurrentLine + 1) * lineHeight + paddingTop);
            rectLt = new RectF(paddingLeft, mStartLine * lineHeight + paddingTop, startToLeftPosition, (mStartLine + 1) * lineHeight + paddingTop);
            rectRb = new RectF(currentToLeftPosition, mCurrentLine * lineHeight + paddingTop, mViewTextWidth + paddingLeft, (mCurrentLine + 1) * lineHeight + paddingTop);
        } else {
            rectAll = new RectF(paddingLeft, mCurrentLine * lineHeight + paddingTop, mViewTextWidth + paddingLeft, (mStartLine + 1) * lineHeight + paddingTop);
            rectLt = new RectF(paddingLeft, mCurrentLine * lineHeight + paddingTop, currentToLeftPosition, (mCurrentLine + 1) * lineHeight + paddingTop);
            rectRb = new RectF(startToLeftPosition, mStartLine * lineHeight + paddingTop, mViewTextWidth + paddingLeft, (mStartLine + 1) * lineHeight + paddingTop);
        }
        // 创三路径（分对应上三矩形）
        Path pathAll = new Path();
        Path pathLt = new Path();
        Path pathRb = new Path();
        pathAll.addRect(rectAll, Path.Direction.CCW);
        pathLt.addRect(rectLt, Path.Direction.CCW);
        pathRb.addRect(rectRb, Path.Direction.CCW);
        // 从 pathAll 减左上角和右下角矩形
        pathAll.addRect(rectAll, Path.Direction.CCW);
        pathAll.op(pathLt, Path.Op.DIFFERENCE);
        pathAll.op(pathRb, Path.Op.DIFFERENCE);
        canvas.drawPath(pathAll, highLightPaint);
    }

    /**
     * 重绘此行（两端对齐）
     *
     * @param canvas             画布
     * @param lineString         该行所有文字
     * @param desiredWidth       该行文本宽总和
     * @param currentLineOffsetY 该行Y偏移
     */
    private void drawJustifyTextForLine(Canvas canvas, String lineString, float desiredWidth, int currentLineOffsetY) {
        // 画笔 X 偏移
        float lineTextOffsetX = getPaddingLeft();
        // 首行否
        if (areFirstLineOfParagraph(lineString)) {
            String blanks = "  ";
            // 画出缩进空格
            canvas.drawText(blanks, lineTextOffsetX, currentLineOffsetY, getPaint());
            // 空格宽
            float blankWidth = StaticLayout.getDesiredWidth(blanks, getPaint());
            // 更新画笔 X 偏移
            lineTextOffsetX += blankWidth;
            lineString = lineString.substring(3);
        }
        // 算相邻字符(或单词)间需填充宽，英文按单词处理，中文按字符处理。
        // (TextView 内容实际宽 - 该行字符串宽) / (字符或单词个数 - 1)
        if (CheckOutUtils.areContainLetter(lineString)) {
            // 含英文以空格分割单词
            String[] lineWords = lineString.split(" ");
            // 算相邻单词间需插入空白
            float insertBlank = mViewTextWidth - desiredWidth;
            if (lineWords.length > 1) {
                insertBlank = (mViewTextWidth - desiredWidth) / (lineWords.length - 1);
            }
            // 遍历单词
            for (int i = 0; i < lineWords.length; i++) {
                // 分割后每单词为纯英文则按纯英文单词处理，直接在画布画出单词。
                // 分割后每单词含汉字则按汉字字符处理，逐绘字符。
                // 仅一单词则按中文处理
                // 末单词按纯英文单词处理
                String iOfWord = lineWords[i] + " ";
                boolean flag = (lineWords.length == 1) || (CheckOutUtils.areContainChinese(iOfWord) && (i < lineWords.length - 1));
                if (flag) {
                    // 单词按汉字字符处理
                    // 算单词相邻字符间需插空白
                    float insertBlankWordI = insertBlank;
                    if (iOfWord.length() > 1) {
                        insertBlankWordI = (insertBlank / (iOfWord.length() - 1));
                    }
                    // 遍历单词字符(逐绘)
                    for (int j = 0; j < iOfWord.length(); j++) {
                        String iOfWordAndCharJ = String.valueOf(iOfWord.charAt(j));
                        float width = StaticLayout.getDesiredWidth(iOfWordAndCharJ, getPaint());
                        canvas.drawText(iOfWordAndCharJ, lineTextOffsetX, currentLineOffsetY, getPaint());
                        // 更新画笔 X 偏移
                        lineTextOffsetX += width + insertBlankWordI;
                    }
                } else {
                    // 单词按纯英文处理
                    float widthOfWordI = StaticLayout.getDesiredWidth(iOfWord, getPaint());
                    canvas.drawText(iOfWord, lineTextOffsetX, currentLineOffsetY, getPaint());
                    // 更新画笔 X 偏移
                    lineTextOffsetX += widthOfWordI + insertBlank;
                }
            }
        } else {
            // 该行按中文处理
            float insertBlank = (mViewTextWidth - desiredWidth) / (lineString.length() - 1);
            for (int i = 0; i < lineString.length(); i++) {
                String charI = String.valueOf(lineString.charAt(i));
                float widthOfCharI = StaticLayout.getDesiredWidth(charI, getPaint());
                canvas.drawText(charI, lineTextOffsetX, currentLineOffsetY, getPaint());
                // 更新画笔 X 偏移
                lineTextOffsetX += (widthOfCharI + insertBlank);
            }
        }
    }

    /**
     * 算字符距控件左侧位移
     *
     * @param line       字符所在行
     * @param charOffset 字符偏移量
     */
    private float calculatorCharPositionToLeft(int line, int charOffset) {
        String text = Objects.requireNonNull(getText()).toString();
        Layout layout = getLayout();
        int lineStart = layout.getLineStart(line);
        int lineEnd = layout.getLineEnd(line);
        String lineString = text.substring(lineStart, lineEnd);
        if (lineString.equals(WidgetMagic.STRING_NEW_LINE)) {
            return getPaddingLeft();
        }
        // 最左侧
        if (lineStart == charOffset) {
            return getPaddingLeft();
        }
        // 最右侧
        if (charOffset == lineEnd - 1) {
            return mViewTextWidth + getPaddingLeft();
        }
        float desiredWidth = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
        // 中间位
        // 算相邻字符间需填充宽
        // (TextView 内容实际宽 - 该行字符串宽) / (字符数 - 1)
        float insertBlank = (mViewTextWidth - desiredWidth) / (lineString.length() - 1);
        // 算当前字符左侧所有字符宽
        float allLeftCharWidth = StaticLayout.getDesiredWidth(text.substring(lineStart, charOffset), getPaint());
        // 相邻字符间需填充宽 + 当前字符左侧所有字符宽
        return insertBlank * (charOffset - lineStart) + allLeftCharWidth + getPaddingLeft();
    }

    /**
     * 段首行否
     * <p>
     * 一汉字等同一字符，字符长大 3 且前两字符为空格。
     *
     * @param line 行
     * @return 首行否
     */
    private boolean areFirstLineOfParagraph(@NonNull String line) {
        return (line.length() > 3) && (line.charAt(0) == ' ') && (line.charAt(1) == ' ');
    }

    /**
     * 需缩放否
     * <p>
     * 该行末字符非换行符返 true
     * 该行末字符非换行符返 false
     *
     * @param lineStr 该行文字
     * @return 缩放否
     */
    private boolean areLineNeedJustify(@NonNull String lineStr) {
        return (!lineStr.isEmpty()) && (lineStr.charAt(lineStr.length() - 1) != '\n');
    }

    public void setCustomActionMenuCallBack(ActionMenuCallBack actionMenuCallBack) {
        this.mActionMenuCallBack = actionMenuCallBack;
    }
}