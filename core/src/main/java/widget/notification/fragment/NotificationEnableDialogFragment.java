package widget.notification.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.zsp.core.R;

import util.layoutparams.LayoutParamsUtils;
import util.screen.ScreenUtils;
import widget.notification.listener.NotificationEnableDialogOnClickListener;
import widget.window.WindowKit;

/**
 * Created on 2019/8/8.
 *
 * @author 郑少鹏
 * @desc 通知允对话框碎片
 */
public class NotificationEnableDialogFragment extends DialogFragment {
    public final String TAG = this.getClass().getName();
    /**
     * 视图
     */
    private View mView;
    /**
     * 通知允对话框点监听
     */
    private NotificationEnableDialogOnClickListener notificationEnableDialogOnClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_notification_enable_dialog, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 对话框
        Dialog dialog = getDialog();
        if (null == dialog) {
            return;
        }
        // 窗体
        Window window = dialog.getWindow();
        if (null == window) {
            return;
        }
        // 背景
        if (null == getContext()) {
            return;
        }
        // 设置背景位图资源透明
        WindowKit.Companion.setBackgroundDrawableResourceTransparent(window);
        // 设置窗口管理器布局参数
        LayoutParamsUtils.setWindowManagerLayoutParams(window, true, (int) (ScreenUtils.screenWidth(getContext()) * 0.25), false, 0);
        // 下次吧
        MaterialButton fragmentNotificationEnableDialogMbNextTime = mView.findViewById(R.id.fragmentNotificationEnableDialogMbNextTime);
        fragmentNotificationEnableDialogMbNextTime.setOnClickListener(v -> {
            dismiss();
            if (null != notificationEnableDialogOnClickListener) {
                notificationEnableDialogOnClickListener.nextTime();
            }
        });
        // 去打开
        MaterialButton fragmentNotificationEnableDialogMbGoToOpen = mView.findViewById(R.id.fragmentNotificationEnableDialogMbGoToOpen);
        fragmentNotificationEnableDialogMbGoToOpen.setOnClickListener(v -> {
            dismiss();
            if (null != notificationEnableDialogOnClickListener) {
                notificationEnableDialogOnClickListener.goToOpen();
            }
        });
        // 取消
        setCancelable(false);
    }

    public void setNotificationEnableDialogOnClickListener(NotificationEnableDialogOnClickListener notificationEnableDialogOnClickListener) {
        this.notificationEnableDialogOnClickListener = notificationEnableDialogOnClickListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != notificationEnableDialogOnClickListener) {
            notificationEnableDialogOnClickListener = null;
        }
    }
}