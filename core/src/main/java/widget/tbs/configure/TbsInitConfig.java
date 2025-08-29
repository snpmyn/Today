package widget.tbs.configure;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;

/**
 * Created on 2021/3/25
 * <p>
 * TBS 内核首次使用和加载时，ART 虚拟机会转 Dex 文件为 OAT。
 * 该过程由系统底层触发且耗时较长，易引 ANR，通 TBS 之 "dex2oat 优化方案" 解决。
 *
 * @author zsp
 * @desc TBS 初始化配置
 */
public class TbsInitConfig {
    /**
     * 初始化 TBS
     */
    public static void initTbs() {
        // 调 TBS 初始化、创建 WebView 前配
        HashMap<String, Object> hashMap = new HashMap<>(2);
        hashMap.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        hashMap.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(hashMap);
    }
}