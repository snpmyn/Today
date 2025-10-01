package widget.textview.menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2022/3/21
 *
 * @author zsp
 * @desc 动作菜单
 */
public class ActionMenu extends LinearLayout {
    public static final String DEFAULT_ACTION_MENU_ITEM_TITLE_SELECT_ALL = "全选";
    public static final String DEFAULT_ACTION_MENU_ITEM_TITLE_COPY = "复制";
    private final Context context;
    private int menuItemMargin;
    /**
     * 动作菜单背景色
     */
    private int actionMenuBackgroundColor = 0xbb000000;
    /**
     * 动作菜单项文本色
     */
    private int actionMenuItemTextColor = 0xffffffff;
    /**
     * 动作菜单条目标题集
     */
    private List<String> actionMenuItemTitleList;

    public ActionMenu(Context context) {
        this(context, null);
    }

    public ActionMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 45);
        setLayoutParams(layoutParams);
        setPadding(25, 0, 25, 0);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setActionMenuBackGround(actionMenuBackgroundColor);
        menuItemMargin = 25;
    }

    /**
     * 设动作菜单背景
     *
     * @param actionMenuBackgroundColor 动作菜单背景色
     */
    private void setActionMenuBackGround(int actionMenuBackgroundColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(actionMenuBackgroundColor);
        gradientDrawable.setCornerRadius(8);
        setBackground(gradientDrawable);
    }

    /**
     * 默添菜单项
     * <p>
     * 全选、复制
     */
    public void addDefaultMenuItem() {
        View selectAllView = createMenuItem(DEFAULT_ACTION_MENU_ITEM_TITLE_SELECT_ALL);
        View copyView = createMenuItem(DEFAULT_ACTION_MENU_ITEM_TITLE_COPY);
        addView(selectAllView);
        addView(copyView);
        invalidate();
    }

    /**
     * 移默动作菜单条目
     */
    public void removeDefaultActionMenuItem() {
        if (getChildCount() == 0) {
            return;
        }
        View selectAllView = findViewWithTag(DEFAULT_ACTION_MENU_ITEM_TITLE_SELECT_ALL);
        View copyView = findViewWithTag(DEFAULT_ACTION_MENU_ITEM_TITLE_COPY);
        if (null != selectAllView) {
            removeView(selectAllView);
        }
        if (null != copyView) {
            removeView(copyView);
        }
        invalidate();
    }

    /**
     * 添动作菜单项
     *
     * @param actionMenuItemTitleList 动作菜单条目标题集
     */
    public void addActionMenuItem(List<String> actionMenuItemTitleList) {
        this.actionMenuItemTitleList = actionMenuItemTitleList;
    }

    /**
     * 添自定菜单项
     */
    public void addCustomMenuItem() {
        boolean flag = ((null == actionMenuItemTitleList) || actionMenuItemTitleList.isEmpty());
        if (flag) {
            return;
        }
        // 去重
        List<String> list = new ArrayList<>();
        for (String title : actionMenuItemTitleList) {
            if (!list.contains(title)) {
                list.add(title);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            final View menuItem = createMenuItem(list.get(i));
            addView(menuItem);
        }
        invalidate();
    }

    /**
     * 创菜单项
     *
     * @param itemTitle 条目标题
     * @return 视图
     */
    @NonNull
    private View createMenuItem(final String itemTitle) {
        final TextView menuItem = new TextView(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.leftMargin = params.rightMargin = menuItemMargin;
        menuItem.setLayoutParams(params);
        menuItem.setTextSize(14);
        menuItem.setTextColor(actionMenuItemTextColor);
        menuItem.setBackgroundColor(Color.TRANSPARENT);
        menuItem.setGravity(Gravity.CENTER);
        menuItem.setText(itemTitle);
        menuItem.setTag(itemTitle);
        return menuItem;
    }

    /**
     * 设动作菜单项文本色
     *
     * @param actionMenuItemTextColor 动作菜单项文本色
     */
    public void setActionMenuItemTextColor(int actionMenuItemTextColor) {
        this.actionMenuItemTextColor = actionMenuItemTextColor;
    }

    /**
     * 设动作菜单背景色
     *
     * @param actionMenuBackgroundColor 动作菜单背景色
     */
    public void setActionMenuBackgroundColor(int actionMenuBackgroundColor) {
        this.actionMenuBackgroundColor = actionMenuBackgroundColor;
        setActionMenuBackGround(this.actionMenuBackgroundColor);
    }
}