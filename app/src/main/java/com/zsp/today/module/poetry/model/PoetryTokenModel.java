package com.zsp.today.module.poetry.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 2026/3/10.
 *
 * @author 郑少鹏
 * @desc 诗词 token 模型
 */
public class PoetryTokenModel {
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}