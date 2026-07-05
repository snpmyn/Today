package widget.network;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Created on 2026/7/2.
 *
 * @author 郑少鹏
 * @desc 加解密管理器
 */
public class CryptoManager {
    private static final CryptoManager INSTANCE = new CryptoManager();

    /**
     * constructor
     */
    private CryptoManager() {
        // 防外部实例化
    }

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static CryptoManager getInstance() {
        return INSTANCE;
    }

    /**
     * 加密请求参数
     *
     * @param jsonObject JSONObject
     * @return 加密后请求参数
     */
    public String encryptRequest(JSONObject jsonObject) {
        if (jsonObject == null) {
            return "";
        }
        return AESUtils.encrypt(jsonObject.toString());
    }

    /**
     * 解密响应数据
     *
     * @param data 数据
     * @return 解密后响应数据
     */
    public String decryptResponse(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        return AESUtils.decrypt(data);
    }
}