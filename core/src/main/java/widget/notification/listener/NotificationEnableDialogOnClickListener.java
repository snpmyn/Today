package widget.notification.listener;

/**
 * Created on 2021/1/28
 *
 * @author zsp
 * @desc 通知允对话框点监听
 */
public interface NotificationEnableDialogOnClickListener {
    /**
     * 下次再说
     */
    void talkAboutItNext();

    /**
     * 去打开
     */
    void goToOpen();
}