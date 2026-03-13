package com.zsp.today.module.poetry.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zsp.today.basic.retrofit.ApiService;
import com.zsp.today.basic.retrofit.RetrofitClient;
import com.zsp.today.basic.retrofit.TokenManager;
import com.zsp.today.module.poetry.TokenModel;
import com.zsp.today.module.poetry.model.PoetryModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created on 2026/3/10.
 *
 * @author 郑少鹏
 * @desc 诗词存储库
 */
public class PoetryRepository {
    /**
     * 获取诗词模型
     *
     * @param baseUrl   基础统一资源定位符
     * @param needToken 需要 token 否
     * @return LiveData<PoetryModel>
     */
    public LiveData<PoetryModel> getPoetryModel(String baseUrl, boolean needToken) {
        ApiService apiService = RetrofitClient.getApiService(baseUrl, needToken);
        MutableLiveData<PoetryModel> mutableLiveData = new MutableLiveData<>();
        // 无 Token 时先获取
        if (!TokenManager.hasToken()) {
            apiService.getTokenModel().enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<TokenModel> call, @NonNull Response<TokenModel> response) {
                    if (null != response.body()) {
                        TokenManager.setToken(response.body().getData());
                        execute(apiService, mutableLiveData);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TokenModel> call, @NonNull Throwable t) {
                    Timber.e(t);
                }
            });
        } else {
            execute(apiService, mutableLiveData);
        }
        return mutableLiveData;
    }

    /**
     * 执行
     *
     * @param apiService      API 服务
     * @param mutableLiveData MutableLiveData<PoetryModel>
     */
    private void execute(@NonNull ApiService apiService, MutableLiveData<PoetryModel> mutableLiveData) {
        apiService.getPoetryModel().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PoetryModel> call, @NonNull Response<PoetryModel> response) {
                if (null != response.body()) {
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PoetryModel> call, @NonNull Throwable t) {
                Timber.e(t);
            }
        });
    }
}