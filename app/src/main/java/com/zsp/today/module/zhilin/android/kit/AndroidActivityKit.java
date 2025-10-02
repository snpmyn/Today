package com.zsp.today.module.zhilin.android.kit;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.module.zhilin.android.bean.AndroidArticleEnum;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import util.list.ListUtils;
import widget.bottomsheetdialog.SingleChooseBottomSheetDialogKit;

/**
 * Created on 2025/9/28.
 *
 * @author 郑少鹏
 * @desc 安卓页配套原件
 */
public class AndroidActivityKit {
    /**
     * 指定文章名称
     */
    private String appointArticleName;
    /**
     * 文章名称集
     */
    private List<String> articleNameList;
    /**
     * 文章名称和文章链接集
     */
    private Map<String, String> articleNameAndArticleUrlMap;

    /**
     * 设置标题
     *
     * @param webView         TextView
     * @param materialToolbar MaterialToolbar
     */
    public void setTitle(@NotNull WebView webView, MaterialToolbar materialToolbar) {
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                materialToolbar.setTitle(title);
            }
        };
        webView.setWebChromeClient(webChromeClient);
    }

    /**
     * 准备数据
     */
    public void prepareData() {
        // 指定文章名称
        appointArticleName = AndroidArticleEnum.OVERLOAD_AND_OVERRIDE.getArticleName();
        // 安卓文章枚举数组
        AndroidArticleEnum[] androidArticleEnums = AndroidArticleEnum.values();
        // 文章名称集
        articleNameList = new ArrayList<>(androidArticleEnums.length);
        // 文章名称和文章链接集
        articleNameAndArticleUrlMap = new HashMap<>(androidArticleEnums.length);
        for (AndroidArticleEnum androidArticleEnum : androidArticleEnums) {
            String articleName = androidArticleEnum.getArticleName();
            articleNameList.add(articleName);
            articleNameAndArticleUrlMap.put(articleName, androidArticleEnum.getArticleUrl());
        }
    }

    /**
     * 显示安卓文章列表
     *
     * @param appCompatActivity 活动
     * @param webView           WebView
     */
    public void showAndroidArticleList(AppCompatActivity appCompatActivity, WebView webView) {
        int defaultSelectPosition = ListUtils.getTargetIndex(articleNameList, appointArticleName);
        SingleChooseBottomSheetDialogKit.show(appCompatActivity, articleNameList, defaultSelectPosition, new SingleChooseBottomSheetDialogKit.SingleChooseBottomSheetDialogKitListener() {
            @Override
            public void singleChoose(String value) {
                appointArticleName = value;
                showAndroidArticleContent(webView, articleNameAndArticleUrlMap.get(appointArticleName));
            }
        });
    }

    /**
     * 显示安卓文章内容
     *
     * @param webView WebView
     * @param url     链接
     */
    public void showAndroidArticleContent(@NonNull WebView webView, String url) {
        webView.loadUrl(Objects.requireNonNull(url));
    }
}