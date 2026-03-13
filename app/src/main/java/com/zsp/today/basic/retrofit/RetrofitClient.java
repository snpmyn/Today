package com.zsp.today.basic.retrofit;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.zsp.today.module.poetry.TokenManager;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 2026/3/10.
 *
 * @author 郑少鹏
 * @desc Retrofit 客户端
 */
public class RetrofitClient {
    /**
     * Retrofit 集
     */
    private static final Map<String, Retrofit> RETROFIT_MAP = new ConcurrentHashMap<>();

    /**
     * constructor
     */
    private RetrofitClient() {

    }

    /**
     * 获取 Retrofit
     *
     * @param baseUrl   基础统一资源定位符
     * @param needToken 需要 token 否
     * @return Retrofit
     */
    private static Retrofit getRetrofit(String baseUrl, boolean needToken) {
        Retrofit retrofit = RETROFIT_MAP.get(baseUrl);
        if (null == retrofit) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @NonNull
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request.Builder builder = chain.request().newBuilder();
                    // 携带 UA
                    builder.addHeader("User-Agent", "Android");
                    // 需要 token
                    // 携带 token
                    if (needToken && TokenManager.hasToken()) {
                        builder.addHeader("X-User-Token", TokenManager.getToken());
                    }
                    return chain.proceed(builder.build());
                }
            }).build();
            // Strictness.STRICT 严格 JSON（默认）
            // Strictness.LENIENT 允许非标准 JSON
            Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).create();
            retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient).addConverterFactory(GsonConverterFactory.create(gson)).build();
            RETROFIT_MAP.put(baseUrl, retrofit);
        }
        return retrofit;
    }

    /**
     * 获取 API 服务
     * <p>
     * 该步仅生成 Retrofit 动态代理对象
     * 并不发请求
     * 也不读取 token
     *
     * @param baseUrl   基础统一资源定位符
     * @param needToken 需要 token 否
     * @return API 服务
     */
    @NonNull
    public static ApiService getApiService(String baseUrl, boolean needToken) {
        return getRetrofit(baseUrl, needToken).create(ApiService.class);
    }
}