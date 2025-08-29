package widget.tbs.kit;

import android.content.Context;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import java.util.HashMap;

/**
 * Created on 2021/3/25
 *
 * @author zsp
 * @desc TBS 配套元件
 */
public class TbsKit {
    public static TbsKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Start QbOrMiniQB ToLoadUrl
     * <p>
     * 优先调 QQ 浏览器打开文件。没装 QQ 浏览器，在 X5 内核下调简版 QB 打开文件。用的系统内核则调文件阅读器弹框。
     * <p>
     * TBS 已提供 9 种主流文件格式本地打开，需更高级能力用 QQ 浏览器打开文件。
     * <p>
     * 接入 TBS 可支持打开文件格式：doc、docx、ppt、pptx、xls、xlsx、pdf、txt、epub。
     * 调 QQ 浏览器可打开：rar(包含加密格式)、zip(包含加密格式)、tar、bz2、gz、7z(包含加密格式)、doc、docx、ppt、pptx、xls、xlsx、txt、pdf、epub、chm、html/htm、xml、mht、url、ini、log、bat、php、js、lrc、jpg、jpeg、png、gif、bmp、tiff 、webp、mp3、m4a、aac、amr、wav、ogg、mid、ra、wma、mpga、ape、flac。
     *
     * @param context       上下文
     *                      调 MiniQB 的 Activity 之 context。
     *                      只能是 Activity 之 context，不能是 Application 之 context。
     * @param filePath      文件路径
     *                      本地存储路径，如：/sdcard/Download/xxx.doc。不支持 file:/// 格式。暂不支持在线文件。
     * @param extraParams   额外参数
     *                      MiniQB 扩展功能。非必填，无特殊配置默 null。扩展功能参考"文件功能定制"。
     * @param valueCallback 值回调
     *                      MiniQB 打开/关闭时给调用方回调通知，以便应用层做相应处理。可在出现以下回调时结束进程，节约内存占用。
     *                      主要回调值：
     *                      {@link widget.tbs.value.TbsEnum#OPEN_FAIL} filePath 空（打开失败）
     *                      {@link widget.tbs.value.TbsEnum#QQ} 用 QQ 浏览器打开
     *                      {@link widget.tbs.value.TbsEnum#MINI_QB} 用 MiniQB 打开
     *                      {@link widget.tbs.value.TbsEnum#READER} 调阅读器弹框
     */
    public void startQbOrMiniQBToLoadUrl(Context context, String filePath, HashMap<String, String> extraParams, ValueCallback<String> valueCallback) {
        if (null == extraParams) {
            extraParams = new HashMap<>(2);
            extraParams.put("style", "1");
            extraParams.put("local", "true");
        }
        QbSdk.startQbOrMiniQBToLoadUrl(context, filePath, extraParams, valueCallback);
    }

    /**
     * 关闭文件阅读器
     * <p>
     * 主动关文件打开 UI 并清相应内存占用。
     *
     * @param context 上下文
     */
    public void closeFileReader(Context context) {
        QbSdk.closeFileReader(context);
    }

    private static final class InstanceHolder {
        static final TbsKit INSTANCE = new TbsKit();
    }
}