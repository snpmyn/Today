package widget.service.base;

import android.content.ServiceConnection;

/**
 * Created on 2025/9/9.
 *
 * @author 郑少鹏
 * @desc 服务连接基类
 */
public abstract class BaseServiceConnection implements ServiceConnection {
    /**
     * 服务连接否
     */
    public boolean areServiceConnect = false;
}