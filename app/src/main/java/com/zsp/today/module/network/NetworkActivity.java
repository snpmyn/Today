package com.zsp.today.module.network;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.zsp.today.databinding.ActivityNetworkBinding;
import com.zsp.today.module.network.contract.NetworkActivityContract;
import com.zsp.today.module.network.model.NetworkModel;

import pool.base.BasePoolActivity;

/**
 * Created on 2026/3/11.
 *
 * @author 郑少鹏
 * @desc 网络页
 */
public class NetworkActivity extends BasePoolActivity implements NetworkActivityContract.NetworkActivityView {
    private ActivityNetworkBinding activityNetworkBinding;
    private NetworkActivityContract.NetworkActivityPresenter networkActivityPresenter;

    /**
     * ViewBinding
     * <p>
     * Java 动态绑定
     * Java 运行时多态
     * Java 动态分派机制
     * <p>
     * 如果子类重写 viewBinding()
     * 那么 onCreate() 中调用时会优先执行子类的方法
     *
     * @return ViewBinding
     */
    @Override
    protected ViewBinding viewBinding() {
        activityNetworkBinding = ActivityNetworkBinding.inflate(getLayoutInflater());
        return activityNetworkBinding;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {

    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        networkActivityPresenter = new NetworkActivityContract.NetworkActivityPresenter(this);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {

    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        networkActivityPresenter.request();
    }

    /**
     * 网络模型
     *
     * @param networkModel 网络模型
     */
    @Override
    public void networkModel(@NonNull NetworkModel networkModel) {
        activityNetworkBinding.networkActivityCountry.setText(networkModel.getCountryName());
        activityNetworkBinding.networkActivityCity.setText(networkModel.getCity());
        activityNetworkBinding.networkActivityLanguage.setText(networkModel.getLanguages());
        activityNetworkBinding.networkActivityIp.setText(networkModel.getIp());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkActivityPresenter.detachView();
    }
}