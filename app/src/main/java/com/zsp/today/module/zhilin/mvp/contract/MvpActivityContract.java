package com.zsp.today.module.zhilin.mvp.contract;

import android.os.Handler;
import android.os.Looper;

import mvp.base.BasePresenter;
import mvp.base.BaseView;
import mvp.callback.CallBack;

/**
 * Created on 2020-09-07
 *
 * @author zsp
 * @desc MVP 页契约
 * <p>
 * Contract 只是一个命名空间
 * 并不会创建对象
 */
public interface MvpActivityContract {
    class MvpActivityModel {
        private final CallBack<String> callBack;

        MvpActivityModel(CallBack<String> callBack) {
            this.callBack = callBack;
        }

        private void request() {
            new Handler(Looper.getMainLooper()).postDelayed(() -> callBack.onResult("UPDATE"), 2000);
        }
    }

    interface MvpActivityView extends BaseView {
        /**
         * 更新文本
         *
         * @param text 文本
         */
        void updateText(String text);
    }

    class MvpActivityPresenter extends BasePresenter<MvpActivityView> {
        private final MvpActivityModel mvpActivityModel;

        public MvpActivityPresenter(final MvpActivityView mvpActivityView) {
            attachView(mvpActivityView);
            mvpActivityModel = new MvpActivityModel(mvpActivityView::updateText);
        }

        /**
         * 请求
         */
        public void request() {
            mvpActivityModel.request();
        }
    }
}