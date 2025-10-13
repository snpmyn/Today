package widget.media.image;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import widget.media.MediaFileTypeEnum;

/**
 * @decs: 图片选择器
 * @author: 郑少鹏
 * @date: 2025/10/13 10:43
 * @version: v 1.0
 */
public class ImagePicker {
    private AppCompatActivity appCompatActivity;
    private Fragment fragment;
    private PickMode pickMode = PickMode.SINGLE;
    private Uri cameraUri;
    private ActivityResultLauncher<Uri> cameraLauncher;
    /**
     * Legacy Picker
     * <p>
     * Android 10-
     */
    private ActivityResultLauncher<Intent> legacyPicker;
    /**
     * Photo Picker
     * <p>
     * Android 13+
     */
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerSingle;
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerMultiple;
    /**
     * 图片单选监听
     */
    private OnImageSingleSelectListener onImageSingleSelectListener;
    /**
     * 图片多选监听
     */
    private OnImageMultipleSelectListener onImageMultipleSelectListener;

    /**
     * 选择模式
     */
    private enum PickMode {
        SINGLE, MULTIPLE, CAMERA
    }

    private ImagePicker(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        initLaunchers();
    }

    private ImagePicker(Fragment fragment) {
        this.fragment = fragment;
        initLaunchers();
    }

    /**
     * 和
     * <p>
     * 必须在 {@link AppCompatActivity#onCreate(Bundle)} 中调用
     * 不能在 {@link AppCompatActivity#onResume()} 或其它生命周期后调用
     *
     * @param appCompatActivity 活动
     * @return 图片选择器
     */
    @NonNull
    @Contract("_ -> new")
    public static ImagePicker with(AppCompatActivity appCompatActivity) {
        return new ImagePicker(appCompatActivity);
    }

    /**
     * 和
     * <p>
     * 必须在 {@link Fragment#onViewCreated(View, Bundle)} 中调用
     *
     * @param fragment 碎片
     * @return 图片选择器
     */
    @NonNull
    @Contract("_ -> new")
    public static ImagePicker with(Fragment fragment) {
        return new ImagePicker(fragment);
    }

    /**
     * 单选
     *
     * @return 图片选择器
     */
    public ImagePicker single() {
        this.pickMode = PickMode.SINGLE;
        return this;
    }

    /**
     * 多选
     *
     * @return 图片选择器
     */
    public ImagePicker multiple() {
        this.pickMode = PickMode.MULTIPLE;
        return this;
    }

    /**
     * 拍照
     *
     * @return 图片选择器
     */
    public ImagePicker camera() {
        this.pickMode = PickMode.CAMERA;
        return this;
    }

    /**
     * 开始
     *
     * @param onImageSingleSelectListener 图片单选监听
     */
    public void start(OnImageSingleSelectListener onImageSingleSelectListener) {
        this.onImageSingleSelectListener = onImageSingleSelectListener;
        launch();
    }

    /**
     * 开始
     *
     * @param onImageMultipleSelectListener 图片多选监听
     */
    public void start(OnImageMultipleSelectListener onImageMultipleSelectListener) {
        this.onImageMultipleSelectListener = onImageMultipleSelectListener;
        launch();
    }

    /**
     * 初始化启动器
     */
    private void initLaunchers() {
        if (null != appCompatActivity) {
            initForAppCompatActivity();
        } else if (null != fragment) {
            initForFragment();
        }
    }

    /**
     * 为活动初始化
     */
    private void initForAppCompatActivity() {
        photoPickerSingle = appCompatActivity.registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::handleSingleResult);
        photoPickerMultiple = appCompatActivity.registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(9), this::handleMultipleResult);
        legacyPicker = appCompatActivity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> handleLegacyResult(result.getResultCode(), result.getData()));
        cameraLauncher = appCompatActivity.registerForActivityResult(new ActivityResultContracts.TakePicture(), this::handleCameraResult);
    }

    /**
     * 为碎片初始化
     */
    private void initForFragment() {
        // Photo Picker
        photoPickerSingle = fragment.registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::handleSingleResult);
        photoPickerMultiple = fragment.registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(100), this::handleMultipleResult);
        // Legacy Picker
        legacyPicker = fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> handleLegacyResult(result.getResultCode(), result.getData()));
        // camera
        cameraLauncher = fragment.registerForActivityResult(new ActivityResultContracts.TakePicture(), this::handleCameraResult);
    }

    /**
     * 启动
     */
    private void launch() {
        if (pickMode == PickMode.CAMERA) {
            launchCamera();
        } else if (arePhotoPickerAvailable()) {
            launchPhotoPicker();
        } else {
            launchLegacyPicker();
        }
    }

    private void launchPhotoPicker() {
        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build();
        if (pickMode == PickMode.MULTIPLE) {
            photoPickerMultiple.launch(request);
        } else {
            photoPickerSingle.launch(request);
        }
    }

    private void launchLegacyPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (pickMode == PickMode.MULTIPLE) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        legacyPicker.launch(intent);
    }

    private void launchCamera() {
        cameraUri = createImageUri();
        if (null != cameraUri) {
            cameraLauncher.launch(cameraUri);
        } else {
            handleSingleResult(null);
        }
    }

    /**
     * 创建图片统一资源标识符
     *
     * @return 图片统一资源标识符
     */
    @Nullable
    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "IMG_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image from camera");
        values.put(MediaStore.Images.Media.MIME_TYPE, MediaFileTypeEnum.JPEG.getMimeType());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera/");
        }
        if (null != appCompatActivity) {
            return appCompatActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else if ((null != fragment) && (null != fragment.getContext())) {
            return fragment.requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        return null;
    }

    private void handleSingleResult(Uri uri) {
        if (null != onImageSingleSelectListener) {
            onImageSingleSelectListener.onImageSingleSelect(uri);
        }
    }

    private void handleMultipleResult(List<Uri> uris) {
        if (null != onImageMultipleSelectListener) {
            onImageMultipleSelectListener.onImageMultipleSelect((null != uris) ? uris : new ArrayList<>());
        }
    }

    /**
     * 处理拍照结果
     * <p>
     * ✅ 修复首次拍照相册不可见
     *
     * @param success 成功否
     */
    private void handleCameraResult(boolean success) {
        if (success && (null != cameraUri)) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                notifyMediaStore(cameraUri);
                handleSingleResult(cameraUri);
            }, 200);
        } else {
            handleSingleResult(null);
        }
    }

    private void handleLegacyResult(int resultCode, Intent data) {
        if ((resultCode != Activity.RESULT_OK) || (null == data)) {
            if (pickMode == PickMode.MULTIPLE) {
                handleMultipleResult(new ArrayList<>());
            } else {
                handleSingleResult(null);
            }
            return;
        }
        if (pickMode == PickMode.MULTIPLE) {
            List<Uri> uriList = new ArrayList<>();
            if (null != data.getClipData()) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    if (null != uri) {
                        uriList.add(uri);
                    }
                }
            } else if (null != data.getData()) {
                uriList.add(data.getData());
            }
            handleMultipleResult(uriList);
        } else {
            handleSingleResult(data.getData());
        }
    }

    /**
     * 通知内体存储
     * <p>
     * ✅ 主动通知系统媒体库更新
     *
     * @param uri 统一资源标识符
     */
    private void notifyMediaStore(@NonNull Uri uri) {
        try {
            Context context = (null != appCompatActivity) ? appCompatActivity : ((null != fragment) ? fragment.requireContext() : null);
            if (null == context) {
                return;
            }
            String path = getRealPathFromUri(context, uri);
            if (null != path) {
                MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.getContentResolver().update(uri, new ContentValues(), null, null);
                } else {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                    context.sendBroadcast(intent);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * 通过统一资源标识符获取真路径
     *
     * @param context 上下文
     * @param uri     统一资源标识符
     * @return 真路径
     */
    @Nullable
    private String getRealPathFromUri(@NonNull Context context, @NonNull Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if ((null != cursor) && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(column_index);
            }
        } catch (Exception ignored) {

        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return null;
    }

    private boolean arePhotoPickerAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                String action = (String) MediaStore.class.getField("ACTION_PICK_IMAGES").get(null);
                Context context = (null != appCompatActivity) ? appCompatActivity : ((null != fragment) ? fragment.getContext() : null);
                if (null == context) {
                    return false;
                }
                Intent intent = new Intent(action);
                return null != intent.resolveActivity(context.getPackageManager());
            } catch (Exception ignored) {

            }
        }
        return false;
    }

    /**
     * 图片单选监听
     */
    public interface OnImageSingleSelectListener {
        /**
         * 图片单选
         *
         * @param uri 统一资源标识符
         */
        void onImageSingleSelect(Uri uri);
    }

    /**
     * 图片多选监听
     */
    public interface OnImageMultipleSelectListener {
        /**
         * 图片多选
         *
         * @param uriList 统一资源标识符集
         */
        void onImageMultipleSelect(List<Uri> uriList);
    }
}