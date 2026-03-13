package com.zsp.today.module.poetry.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created on 2026/3/10.
 *
 * @author 郑少鹏
 * @desc 诗词模型
 * <p>
 * Gson 在解析 JSON 时
 * 默认使用字段名直接匹配 JSON key
 * 如果字段名和 JSON key 不一致，就需要 @SerializedName 指定映射。
 * <p>
 * 保证字段映射稳定
 * 兼容 JSON 命名规范
 * 防止混淆（Proguard / R8）
 */
public class PoetryModel {
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private DataDTO data;
    @SerializedName("token")
    private String token;
    @SerializedName("ipAddress")
    private String ipAddress;
    @SerializedName("warning")
    private Object warning;

    public static class DataDTO {
        @SerializedName("id")
        private String id;
        @SerializedName("content")
        private String content;
        @SerializedName("popularity")
        private Integer popularity;
        @SerializedName("origin")
        private OriginDTO origin;
        @SerializedName("matchTags")
        private List<String> matchTags;
        @SerializedName("recommendedReason")
        private String recommendedReason;
        @SerializedName("cacheAt")
        private String cacheAt;

        public static class OriginDTO {
            @SerializedName("title")
            private String title;
            @SerializedName("dynasty")
            private String dynasty;
            @SerializedName("author")
            private String author;
            @SerializedName("content")
            private List<String> content;
            @SerializedName("translate")
            private Object translate;

            public String getTitle() {
                return title;
            }

            public String getDynasty() {
                return dynasty;
            }

            public String getAuthor() {
                return author;
            }

            public List<String> getContent() {
                return content;
            }
        }

        public OriginDTO getOrigin() {
            return origin;
        }
    }

    public DataDTO getData() {
        return data;
    }
}