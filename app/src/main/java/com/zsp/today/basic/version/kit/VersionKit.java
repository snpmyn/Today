package com.zsp.today.basic.version.kit;

import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.zsp.today.BuildConfig;
import com.zsp.today.R;
import com.zsp.today.basic.version.bean.VersionInfoBean;

import widget.dialog.bocdialog.kit.BocDialogKit;
import widget.ion.config.IonInitConfig;
import widget.ion.kit.IonErrorKit;
import widget.toast.ToastKit;
import widget.update.ApkDownloadManager;

/**
 * Created on 2025/9/23.
 *
 * @author 郑少鹏
 * @desc 版本配套原件
 */
public class VersionKit {
    /**
     * 检查
     * <p>
     * 检测 App 是否有更新
     * POST <a href="https://api.pgyer.com/apiv2/app/check">...</a>
     * <p>
     * 选填 .addQuery("buildVersion", "")
     * 用 App 本身 Build 版本号
     * Android 对应字段 versionName
     * iOS 对应字段 version
     * <p>
     * 选填 .addQuery("buildBuildVersion", "")
     * 用蒲公英生成的自增 Build 版本号
     * <p>
     * 选填 .addQuery("channelKey", "")
     * 渠道 KEY
     *
     * @param appCompatActivity 活动
     * @param hint              提示否
     */
    public static void check(AppCompatActivity appCompatActivity, boolean hint) {
        if (hint) {
            BocDialogKit.getInstance(appCompatActivity).bocCommonLoading(appCompatActivity.getString(R.string.check), null);
        }
        Ion.with(appCompatActivity).load(IonInitConfig.POST, "https://api.pgyer.com/apiv2/app/check").setTimeout(IonInitConfig.TIME_OUT).setLogging(IonInitConfig.LOG_TAG, IonInitConfig.LOG_LEVEL).addQuery("_api_key", "33ff1520a3b3b352210b83f33b7bdf96").addQuery("appKey", "070f15aa424fefee1560725d8d596e8e").asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (hint) {
                    BocDialogKit.getInstance(appCompatActivity).end();
                }
                if (null == e) {
                    VersionInfoBean versionInfoBean = new Gson().fromJson(result, VersionInfoBean.class);
                    VersionInfoBean.Data data = versionInfoBean.getData();
                    int code = versionInfoBean.getCode();
                    if (code == 0) {
                        // 该接口成功码 0
                        // 是否需要更新
                        String buildVersionNo = data.getBuildVersionNo();
                        boolean needUpdate = !TextUtils.isEmpty(buildVersionNo) && (Integer.parseInt(buildVersionNo) > BuildConfig.VERSION_CODE);
                        // 是否需要强制更新
                        String forceUpdateVersionNo = data.getForceUpdateVersionNo();
                        boolean needForceUpdate = !TextUtils.isEmpty(forceUpdateVersionNo) && (Integer.parseInt(forceUpdateVersionNo) > BuildConfig.VERSION_CODE);
                        // 需要更新或需要强制更新
                        if (needUpdate || needForceUpdate) {
                            String downloadURL = data.getDownloadURL();
                            String buildUpdateDescription = data.getBuildUpdateDescription();
                            new ApkDownloadManager(appCompatActivity).execute(needUpdate, needForceUpdate, "知伴.apk", downloadURL, buildUpdateDescription, true);
                        } else if (hint) {
                            ToastKit.showShort(appCompatActivity.getString(R.string.currentTheLatestVersion));
                        }
                    } else if (hint) {
                        ToastKit.showShort(IonErrorKit.getMessage(code));
                    }
                } else if (hint) {
                    ToastKit.showShort(appCompatActivity.getString(R.string.failToObtainVersionInformation));
                }
            }
        });
    }
}