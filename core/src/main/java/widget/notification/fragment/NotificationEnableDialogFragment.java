package widget.notification.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.zsp.core.R;

import widget.notification.listener.NotificationEnableDialogOnClickListener;

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

    /**
     * Called to have the fragment instantiate its user interface view.
     * <p>
     * This is optional, and non-graphical fragments can return null.
     * This will be called between {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * A default View can be returned by calling {@link androidx.fragment.app.Fragment#Fragment(int)} in your
     * constructor. Otherwise, this method returns null.
     * <p>
     * It is recommended to <strong>only</strong> inflate the layout in this method and move
     * logic that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     * <p>
     * If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_notification_enable_dialog, container);
        return mView;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
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
        window.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.white_solid_r10));
        // 下次再说
        MaterialButton fragmentNotificationEnableDialogMbTalkAboutItNext = mView.findViewById(R.id.fragmentNotificationEnableDialogMbTalkAboutItNext);
        fragmentNotificationEnableDialogMbTalkAboutItNext.setOnClickListener(v -> {
            dismiss();
            notificationEnableDialogOnClickListener.talkAboutItNext();
        });
        // 去打开
        MaterialButton fragmentNotificationEnableDialogMbGoToOpen = mView.findViewById(R.id.fragmentNotificationEnableDialogMbGoToOpen);
        fragmentNotificationEnableDialogMbGoToOpen.setOnClickListener(v -> {
            dismiss();
            notificationEnableDialogOnClickListener.goToOpen();
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