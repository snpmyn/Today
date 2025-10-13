package widget.view.kit;

import android.graphics.Color;

import androidx.annotation.NonNull;

import java.util.List;

import widget.view.AccountLineView;

/**
 * Created on 2025/10/4.
 *
 * @author 郑少鹏
 * @desc 账目线段视图配套原件
 */
public class AccountLineViewKit {
    /**
     * 执行
     *
     * @param accountLineView 账目线段视图
     * @param accountItemList 账目条目集
     */
    public static void execute(@NonNull AccountLineView accountLineView, List<AccountLineView.AccountItem> accountItemList) {
        // 设置线段高度
        accountLineView.setLineHeight(100.0F);
        // 设置条目间距
        accountLineView.setItemSpacing(40.0F);
        // 设置文本粗体否
        accountLineView.setTextBold(true);
        // 设置文本尺寸
        accountLineView.setTextSize(38.0F);
        // 设置文本颜色
        accountLineView.setTextColor(Color.WHITE);
        // 设置文本模式
        accountLineView.setTextMode(AccountLineView.TextMode.PERCENT);
        // 设置账目条目集
        accountLineView.setAccountItems(accountItemList);
    }
}