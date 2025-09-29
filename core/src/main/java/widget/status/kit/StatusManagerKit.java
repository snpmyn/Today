package widget.status.kit;

import android.content.Intent;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.core.R;

import org.jetbrains.annotations.Contract;

import java.util.List;

import util.list.ListUtils;
import widget.status.listener.StatusManagerListener;
import widget.status.manager.StatusManager;

/**
 * Created on 2021/2/10
 *
 * @author zsp
 * @desc 状态管理器配套元件
 */
public class StatusManagerKit {
    /**
     * 产生
     *
     * @param appCompatActivity        活动
     * @param recyclerView             控件
     * @param statusManagerKitListener 状态管理器配套元件监听
     * @return 状态管理器
     */
    @NonNull
    @Contract("_, _, _ -> new")
    public static StatusManager generate(AppCompatActivity appCompatActivity, RecyclerView recyclerView, StatusManagerKitListener statusManagerKitListener) {
        return StatusManager.generate(recyclerView, new StatusManagerListener() {
            @Override
            public void setLoadingEvent(View loadingView) {

            }

            @Override
            public void setEmptyEvent(View emptyView) {

            }

            @Override
            public void setRetryEvent(int statusCode, View retryView) {
                View view = retryView.findViewById(R.id.statusRetryMb);
                view.setOnClickListener(v -> {
                    switch (statusCode) {
                        // 无网络
                        case 0:
                            if (null != statusManagerKitListener) {
                                statusManagerKitListener.noNetwork();
                            }
                            appCompatActivity.startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), StatusManager.requestCode);
                            break;
                        // 连接失败
                        case 1:
                            if (null != statusManagerKitListener) {
                                statusManagerKitListener.connectFail();
                            }
                            break;
                        // 加载失败
                        case 2:
                            if (null != statusManagerKitListener) {
                                statusManagerKitListener.loadFail();
                            }
                            break;
                        default:
                            break;
                    }
                });
            }
        });
    }

    /**
     * 状态判断
     * <p>
     * 0 无网络
     * 1 连接失败
     * 2 加载失败
     * {@link StatusManager#showRetry(int)}
     * <p>
     * 3 加载
     * {@link StatusManager#showLoading()}
     * <p>
     * 4 空
     * {@link StatusManager#showEmpty()}
     * <p>
     * 5 内容
     * {@link StatusManager#showContent()}
     *
     * @param statusManager 状态管理器
     * @param loading       加载中否
     * @param list          集合
     */
    public static void statusJudge(StatusManager statusManager, boolean loading, List<?> list) {
        if (loading) {
            statusManager.showLoading();
            return;
        }
        if (ListUtils.listIsEmpty(list)) {
            statusManager.showEmpty();
            return;
        }
        statusManager.showContent();
    }

    /**
     * 状态管理器配套元件监听
     */
    public interface StatusManagerKitListener {
        /**
         * 无网络
         * <p>
         * 状态码 0
         */
        void noNetwork();

        /**
         * 连接失败
         * <p>
         * 状态码 1
         */
        void connectFail();

        /**
         * 加载失败
         * <p>
         * 状态码 2
         */
        void loadFail();
    }
}