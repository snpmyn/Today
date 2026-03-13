package com.zsp.today.module.zhilin.android.bean;

/**
 * Created on 2025/9/28.
 *
 * @author 郑少鹏
 * @desc 安卓文章枚举
 */
public enum AndroidArticleEnum {
    /**
     * Git
     */
    GIT(1, "Git", "file:///android_asset/html/android/git.html"),
    /**
     * 运算符
     */
    OPERATOR(2, "运算符", "file:///android_asset/html/android/operator.html"),
    /**
     * 抽象方法
     */
    ABSTRACT_METHOD(3, "抽象方法", "file:///android_asset/html/android/AbstractMethod.html"),
    /**
     * SDK 版本
     */
    SDK_VERSION(4, "SDK 版本", "file:///android_asset/html/android/SdkVersion.html"),
    /**
     * 重载与重写
     */
    OVERLOAD_AND_OVERRIDE(5, "重载与重写", "file:///android_asset/html/android/OverloadAndOverride.html"),
    /**
     * Activity 生命周期
     */
    ACTIVITY_LIFECYCLE(6, "Activity 生命周期", "file:///android_asset/html/android/ActivityLifecycle.html");
    /**
     * 文章 ID
     */
    private final int articleId;
    /**
     * 文章名称
     */
    private final String articleName;
    /**
     * 文章链接
     */
    private final String articleUrl;

    /**
     * constructor
     *
     * @param articleId   文章 ID
     * @param articleName 文章名称
     * @param articleUrl  文章链接
     */
    AndroidArticleEnum(int articleId, String articleName, String articleUrl) {
        this.articleId = articleId;
        this.articleName = articleName;
        this.articleUrl = articleUrl;
    }

    public int getArticleId() {
        return articleId;
    }

    public String getArticleName() {
        return articleName;
    }

    public String getArticleUrl() {
        return articleUrl;
    }
}