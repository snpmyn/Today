package com.zsp.today.basic.retrofit;

import com.zsp.today.module.network.model.NetworkModel;
import com.zsp.today.module.poetry.TokenModel;
import com.zsp.today.module.poetry.model.PoetryModel;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created on 2026/3/10.
 *
 * @author 郑少鹏
 * @desc API 服务
 * <p>
 * 动态代理
 * 解析注解
 * <p>
 * GET GET 请求
 * POST POST 请求
 * PUT PUT 请求
 * DELETE DELETE 请求
 * Path URL 参数
 * Query 查询参数
 * Body 请求体
 * Header 请求头
 */
public interface ApiService {
    /**
     * 获取网络模型
     *
     * @return 网络模型
     */
    @GET("json/")
    Call<NetworkModel> getNetworkModel();

    /**
     * 获取 token 模型
     *
     * @return token 模型
     */
    @GET("token")
    Call<TokenModel> getTokenModel();

    /**
     * 获取诗词模型
     *
     * @return 诗词模型
     */
    @GET("one.json")
    Call<PoetryModel> getPoetryModel();
}