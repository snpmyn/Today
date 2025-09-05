package com.zsp.today.basic.kit;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.zsp.today.R;
import com.zsp.today.base.BaseDataBaseTable;
import com.zsp.today.basic.value.Folder;

import litepal.kit.LitePalKit;
import util.file.FileUtils;
import util.json.JsonFormat;
import widget.toast.ToastKit;

/**
 * Created on 2022/6/20
 *
 * @author zsp
 * @desc 备份配套元件
 */
public class BackupKit {
    public static BackupKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 备份
     *
     * @param appCompatActivity 活动
     * @param modelClass        模型类
     * @param backupKitListener 配套元件监听
     * @param <T>               <T>
     */
    public <T extends BaseDataBaseTable> void backup(AppCompatActivity appCompatActivity, Class<T> modelClass, BackupKitListener backupKitListener) {
        String json = new Gson().toJson(LitePalKit.getInstance().findAll(modelClass));
        // 内备份
        boolean internalBackupState = FileUtils.saveStringAsFile(appCompatActivity, JsonFormat.formatJson(json), Folder.INTERNAL_BACKUP, modelClass.getSimpleName() + ".json");
        // 外备份
        boolean externalBackupState = FileUtils.saveStringAsFile(appCompatActivity, JsonFormat.formatJson(json), Folder.EXTERNAL_BACKUP, modelClass.getSimpleName() + ".json");
        if (null == backupKitListener) {
            if (!internalBackupState) {
                ToastKit.showShort(appCompatActivity.getString(R.string.internalBackupFail));
            } else if (!externalBackupState) {
                ToastKit.showShort(appCompatActivity.getString(R.string.externalBackupFail));
            }
            return;
        }
        backupKitListener.backup(internalBackupState, externalBackupState);
    }

    /**
     * 配套元件监听
     */
    public interface BackupKitListener {
        /**
         * 备份
         *
         * @param internalBackupState 内备份状态
         * @param externalBackupState 外备份状态
         */
        void backup(boolean internalBackupState, boolean externalBackupState);
    }

    private static final class InstanceHolder {
        static final BackupKit INSTANCE = new BackupKit();
    }
}