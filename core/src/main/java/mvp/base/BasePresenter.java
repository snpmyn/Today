package mvp.base;

import androidx.annotation.UiThread;

/**
 * Created on 2020-09-07
 *
 * @author zsp
 * @desc 基类桥梁
 */
public abstract class BasePresenter<V extends BaseView> {
    private V view;

    @UiThread
    protected void attachView(V view) {
        this.view = view;
    }

    @UiThread
    public void detachView() {
        if (null != view) {
            view = null;
        }
    }

    protected V getView() {
        return view;
    }
}