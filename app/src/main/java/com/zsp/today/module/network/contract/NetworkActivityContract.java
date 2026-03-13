package com.zsp.today.module.network.contract;

import androidx.annotation.NonNull;

import com.zsp.today.basic.retrofit.ApiService;
import com.zsp.today.basic.retrofit.RetrofitBaseUrl;
import com.zsp.today.basic.retrofit.RetrofitClient;
import com.zsp.today.module.network.model.NetworkModel;

import mvp.base.BasePresenter;
import mvp.base.BaseView;
import mvp.callback.CallBack;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created on 2026/3/12.
 *
 * @author 郑少鹏
 * @desc 网络页契约
 * <p>
 * Contract 只是一个命名空间
 * 并不创建对象
 */
public class NetworkActivityContract {
    public static class NetworkActivityModel {
        private final ApiService apiService;

        public NetworkActivityModel() {
            apiService = RetrofitClient.getApiService(RetrofitBaseUrl.NETWORK_BASE_URL, false);
        }

        public void getNetworkModel(CallBack<NetworkModel> callBack) {
            apiService.getNetworkModel().enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<NetworkModel> call, @NonNull Response<NetworkModel> response) {
                    if (null != response.body()) {
                        callBack.onResult(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<NetworkModel> call, @NonNull Throwable t) {
                    Timber.e(t);
                }
            });
        }
    }

    public interface NetworkActivityView extends BaseView {
        /**
         * 网络模型
         *
         * @param networkModel 网络模型
         */
        void networkModel(NetworkModel networkModel);
    }

    public static class NetworkActivityPresenter extends BasePresenter<NetworkActivityView> {
        private final NetworkActivityModel networkActivityModel;

        public NetworkActivityPresenter(NetworkActivityView networkActivityView) {
            attachView(networkActivityView);
            networkActivityModel = new NetworkActivityModel();
        }

        /**
         * 请求
         */
        public void request() {
            networkActivityModel.getNetworkModel(result -> getView().networkModel(result));
        }
    }
}