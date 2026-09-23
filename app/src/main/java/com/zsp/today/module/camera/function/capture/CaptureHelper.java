package com.zsp.today.module.camera.function.capture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.zsp.today.module.camera.LogKit;
import com.zsp.today.module.camera.function.callback.CameraCaptureCallback;
import com.zsp.today.module.camera.function.value.EnhanceMode;
import com.zsp.today.module.camera.media.MediaScanKit;
import com.zsp.today.module.camera.storage.MediaFileNameEngine;
import com.zsp.today.module.camera.storage.MediaStorageConfig;
import com.zsp.today.module.camera.storage.MediaStorageType;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import timber.log.Timber;

/**
 * @decs: 拍照辅助者
 * @author: 郑少鹏
 * @date: 2026/9/18 15:08
 * @version: v 1.0
 */
public class CaptureHelper {
    /**
     * 帧锁
     */
    private static final Object FRAME_LOCK = new Object();
    /**
     * 最新帧 YUV 内存数据缓存
     */
    private static byte[] latestYuvBytes;
    /**
     * YUV 字节复用缓冲区
     */
    private static byte[] nv21BufferCache;
    /**
     * 最新帧宽
     */
    private static int latestFrameWidth = 0;
    /**
     * 最新帧高
     */
    private static int latestFrameHeight = 0;
    /**
     * 最新帧旋转角度
     */
    private static int latestFrameRotationDegrees = 0;

    /**
     * 重置序号
     */
    public static void resetSequence() {
        MediaFileNameEngine.resetSequence();
    }

    /**
     * 生成保存路径
     *
     * @return 保存路径
     */
    public static @Nullable String generateSavePath() {
        File targetFile = MediaStorageConfig.getInstance().generateSaveFile(MediaStorageType.CAPTURE, null);
        if (targetFile == null) {
            return null;
        }
        File parentDir = targetFile.getParentFile();
        if ((parentDir != null) && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created && !parentDir.exists()) {
                Timber.tag(LogKit.TAG).e("创建照片存储目录失败 || %s", parentDir.getAbsolutePath());
                return null;
            }
        }
        return targetFile.getAbsolutePath();
    }

    /**
     * 拍照
     *
     * @param context               上下文
     * @param imageCapture          抓拍用例对象
     * @param previewView           预览视图
     *                              硬件抓拍失败时降级方案
     * @param isUvcCamera           是否为 UVC 高拍仪
     * @param executorService       增强实现
     * @param enhanceMode           图像增强模式
     * @param cameraCaptureCallback 相机拍照回调
     */
    public static void capture(@NonNull Context context, ImageCapture imageCapture, @NonNull PreviewView previewView, boolean isUvcCamera, @NonNull ExecutorService executorService, EnhanceMode enhanceMode, CameraCaptureCallback cameraCaptureCallback) {
        Executor mainExecutor = ContextCompat.getMainExecutor(context);
        if (imageCapture == null) {
            if (cameraCaptureCallback != null) {
                mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_CAMERA_CLOSED, "相机尚未初始化成功", null)));
            }
            return;
        }
        String savePath = generateSavePath();
        if (savePath == null) {
            if (cameraCaptureCallback != null) {
                mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_FILE_IO, "创建照片保存文件失败", null)));
            }
            return;
        }
        File rawPhotoFile = new File(savePath);
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(rawPhotoFile).build();
        // 优先使用 CameraX 硬件传感器抓拍
        imageCapture.takePicture(outputOptions, executorService, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Timber.tag(LogKit.TAG).d("相机硬件传感器抓拍成功 || %s", rawPhotoFile.getAbsolutePath());
                // 扫描单个文件
                MediaScanKit.scanSingleFile(context, rawPhotoFile.getAbsolutePath(), "image/jpeg");
                if (cameraCaptureCallback != null) {
                    mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureSuccess(rawPhotoFile));
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Timber.tag(LogKit.TAG).w(exception, "硬件抓拍失败，自动降级至 ImageAnalysis 原始帧抓拍处理。");
                captureFromLatestFrame(context, previewView, rawPhotoFile, isUvcCamera, executorService, enhanceMode, cameraCaptureCallback);
            }
        });
    }

    /**
     * 从最新帧拍照
     *
     * @param context               上下文
     * @param previewView           预览视图
     * @param photoFile             照片文件
     * @param isUvcCamera           是否为 UVC 高拍仪
     * @param executorService       增强实现
     * @param enhanceMode           图像增强模式
     * @param cameraCaptureCallback 相机拍照回调
     */
    private static void captureFromLatestFrame(@NonNull Context context, @NonNull PreviewView previewView, File photoFile, boolean isUvcCamera, @NonNull ExecutorService executorService, EnhanceMode enhanceMode, CameraCaptureCallback cameraCaptureCallback) {
        Executor mainExecutor = ContextCompat.getMainExecutor(context);
        executorService.execute(() -> {
            byte[] yuvData;
            int width;
            int height;
            int rotation;
            synchronized (FRAME_LOCK) {
                if (latestYuvBytes != null) {
                    yuvData = new byte[latestYuvBytes.length];
                    System.arraycopy(latestYuvBytes, 0, yuvData, 0, latestYuvBytes.length);
                } else {
                    yuvData = null;
                }
                width = latestFrameWidth;
                height = latestFrameHeight;
                rotation = isUvcCamera ? 0 : latestFrameRotationDegrees;
            }
            if ((yuvData == null) || (width <= 0) || (height <= 0)) {
                Timber.tag(LogKit.TAG).w("帧数据缓存为空，降级至 PreviewView 截屏处理。");
                mainExecutor.execute(() -> captureFromPreviewView(context, previewView, photoFile, executorService, cameraCaptureCallback));
                return;
            }
            try {
                YuvImage yuvImage = new YuvImage(yuvData, ImageFormat.NV21, width, height, null);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                // 1. 判断是否需要旋转
                // UVC 场景旋转角度恒为 0° -> 无需旋转
                // 非 UVC 且存在旋转角度场景 -> 需要旋转
                boolean needRotate = (!isUvcCamera && (rotation != 0));
                // 2. 判断是否需要图像增强
                boolean needEnhance = (enhanceMode != EnhanceMode.NONE);
                try (OutputStream outputStream = new FileOutputStream(photoFile)) {
                    if (!needRotate && !needEnhance) {
                        // 无需旋转 && 无需图像增强
                        // 直接写入 -> 避免二次编解码开销
                        outputStream.write(imageBytes);
                    } else {
                        // 需要旋转 || 需要图像增强
                        // 只要有任何一项需求，就必须解码成 Bitmap 处理。
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                        // 1. 需要旋转 -> 才做旋转
                        // 仅针对非 UVC 且存在旋转角度场景
                        if (needRotate) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(rotation);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            if (rotatedBitmap != bitmap) {
                                bitmap.recycle();
                            }
                            bitmap = rotatedBitmap;
                        }
                        // 2. 需要图像增强 -> 才做图像增强
                        Bitmap processedBitmap = enhanceBitmap(bitmap, enhanceMode);
                        // 3. 重新压缩写盘
                        processedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        // 回收 Bitmap 规避内存溢出
                        if (processedBitmap != bitmap) {
                            bitmap.recycle();
                        }
                        processedBitmap.recycle();
                    }
                    MediaScanKit.scanSingleFile(context, photoFile.getAbsolutePath(), "image/jpeg");
                    Timber.tag(LogKit.TAG).d("ImageAnalysis 原始帧降级拍照成功 [增强模式: %s] || %s", enhanceMode.name(), photoFile.getAbsolutePath());
                    if (cameraCaptureCallback != null) {
                        mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureSuccess(photoFile));
                    }
                }
            } catch (Exception e) {
                Timber.tag(LogKit.TAG).e(e, "ImageAnalysis 原始帧保存失败，尝试 PreviewView 截屏兜底");
                mainExecutor.execute(() -> captureFromPreviewView(context, previewView, photoFile, executorService, cameraCaptureCallback));
            }
        });
    }

    /**
     * 从预览视图拍照
     *
     * @param context               上下文
     * @param previewView           预览视图
     * @param photoFile             照片文件
     * @param executorService       增强实现
     * @param cameraCaptureCallback 相机拍照回调
     */
    private static void captureFromPreviewView(@NonNull Context context, @NonNull PreviewView previewView, File photoFile, @NonNull ExecutorService executorService, CameraCaptureCallback cameraCaptureCallback) {
        Executor mainExecutor = ContextCompat.getMainExecutor(context);
        Bitmap bitmap = previewView.getBitmap();
        if (bitmap == null) {
            if (cameraCaptureCallback != null) {
                mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "预览帧获取为空", null)));
            }
            return;
        }
        executorService.execute(() -> {
            try (OutputStream outputStream = new FileOutputStream(photoFile)) {
                Timber.tag(LogKit.TAG).d("PreviewView 截屏降级拍照成功 || %s", photoFile.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                bitmap.recycle();
                MediaScanKit.scanSingleFile(context, photoFile.getAbsolutePath());
                if (cameraCaptureCallback != null) {
                    mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureSuccess(photoFile));
                }
            } catch (Exception e) {
                Timber.tag(LogKit.TAG).e(e, "预览截图保存失败");
                if (cameraCaptureCallback != null) {
                    mainExecutor.execute(() -> cameraCaptureCallback.onCameraCaptureError(new ImageCaptureException(ImageCapture.ERROR_UNKNOWN, "截屏保存异常: " + e.getMessage(), e)));
                }
            }
        });
    }

    /**
     * 图像算法处理
     *
     * @param bitmap      像素数据
     * @param enhanceMode 图像增强模式
     * @return 处理后像素数据
     */
    private static Bitmap enhanceBitmap(@NonNull Bitmap bitmap, @NonNull EnhanceMode enhanceMode) {
        if (enhanceMode == EnhanceMode.NONE) {
            return bitmap;
        }
        if (enhanceMode == EnhanceMode.DOCUMENT) {
            return enhanceDocumentClarity(bitmap);
        } else if (enhanceMode == EnhanceMode.USM_SHARPEN) {
            return enhanceSharpen(bitmap);
        }
        return bitmap;
    }

    /**
     * 文档图像对比度及清晰度算法增强
     * <p>
     * 针对高拍仪黑白 / 彩色文档场景
     *
     * @param bitmap 像素数据
     * @return 增强后像素数据
     */
    @NonNull
    private static Bitmap enhanceDocumentClarity(@NonNull Bitmap bitmap) {
        Bitmap.Config config = (bitmap.getConfig() != null) ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 提升大约 15% 对比度并增加 10 增益
        // 使文本黑度与底纸白度更加分明
        float contrast = 1.15f;
        float brightness = 10f;
        float translate = ((-0.5f * contrast + 0.5f) * 255f + brightness);
        float[] colorTransform = {contrast, 0, 0, 0, translate, 0, contrast, 0, 0, translate, 0, 0, contrast, 0, translate, 0, 0, 0, 1, 0};
        paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(colorTransform)));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return result;
    }

    /**
     * 图像拉普拉斯边缘锐化增强
     *
     * @param bitmap 像素数据
     * @return 增强后像素数据
     */
    @NonNull
    private static Bitmap enhanceSharpen(@NonNull Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] resultPixels = new int[width * height];
        // 预先拷贝原始像素
        // 防止四周 1px 卷积盲区变黑
        System.arraycopy(pixels, 0, resultPixels, 0, pixels.length);
        // 经典拉普拉斯锐化核
        // [ 0, -1,  0 ]
        // [-1,  5, -1 ]
        // [ 0, -1,  0 ]
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int index = y * width + x;

                int c00 = pixels[(y - 1) * width + x];
                int c10 = pixels[y * width + (x - 1)];
                int c11 = pixels[index];
                int c12 = pixels[y * width + (x + 1)];
                int c21 = pixels[(y + 1) * width + x];

                int r = 5 * ((c11 >> 16) & 0xFF) - ((c00 >> 16) & 0xFF) - ((c10 >> 16) & 0xFF) - ((c12 >> 16) & 0xFF) - ((c21 >> 16) & 0xFF);
                int g = 5 * ((c11 >> 8) & 0xFF) - ((c00 >> 8) & 0xFF) - ((c10 >> 8) & 0xFF) - ((c12 >> 8) & 0xFF) - ((c21 >> 8) & 0xFF);
                int b = 5 * (c11 & 0xFF) - (c00 & 0xFF) - (c10 & 0xFF) - (c12 & 0xFF) - (c21 & 0xFF);

                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));

                resultPixels[index] = (0xFF000000) | (r << 16) | (g << 8) | b;
            }
        }
        Bitmap.Config config = (bitmap.getConfig() != null) ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap result = Bitmap.createBitmap(width, height, config);
        result.setPixels(resultPixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 更新最新帧
     * <p>
     * 由 ImageAnalysis 实时回调
     *
     * @param imageProxy  图像代理
     * @param isUvcCamera 是否为 UVC 高拍仪
     */
    public static void updateLatestFrame(@NonNull ImageProxy imageProxy, boolean isUvcCamera) {
        try (imageProxy) {
            if (imageProxy.getFormat() == ImageFormat.YUV_420_888) {
                int width = imageProxy.getWidth();
                int height = imageProxy.getHeight();
                int rotationDegrees = isUvcCamera ? 0 : imageProxy.getImageInfo().getRotationDegrees();
                // 转换工作在锁外进行
                // 提升 ImageAnalysis 吞吐率
                byte[] nv21Bytes = yuv420888ToNv21(imageProxy);
                synchronized (FRAME_LOCK) {
                    latestFrameWidth = width;
                    latestFrameHeight = height;
                    latestFrameRotationDegrees = rotationDegrees;
                    // 确保 latestYuvBytes 具有独立缓冲区
                    // 切断并发竞争读写
                    // 防止降级拍照数据撕裂 (花屏 / 绿条)
                    if ((latestYuvBytes == null) || (latestYuvBytes.length != nv21Bytes.length)) {
                        latestYuvBytes = new byte[nv21Bytes.length];
                    }
                    System.arraycopy(nv21Bytes, 0, latestYuvBytes, 0, nv21Bytes.length);
                }
            }
        } catch (Exception e) {
            Timber.tag(LogKit.TAG).e(e, "更新帧数据缓存异常");
        }
    }

    /**
     * 将 YUV_420_888 格式的 ImageProxy 安全快速地转为 NV21 字节数组
     *
     * @param imageProxy 图像代理
     * @return NV21 字节数组
     */
    @NonNull
    private static byte[] yuv420888ToNv21(@NonNull ImageProxy imageProxy) {
        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();
        int ySize = width * height;
        int uvSize = ySize / 2;
        int totalSize = ySize + uvSize;
        // 内存复用
        // 规避高频 GC 开销
        if ((nv21BufferCache == null) || (nv21BufferCache.length != totalSize)) {
            nv21BufferCache = new byte[totalSize];
        }
        byte[] nv21 = nv21BufferCache;
        ImageProxy.PlaneProxy[] proxyPlanes = imageProxy.getPlanes();
        // 1. 提取 Y 分量
        ByteBuffer yBuffer = proxyPlanes[0].getBuffer();
        int yRowStride = proxyPlanes[0].getRowStride();
        int yPixelStride = proxyPlanes[0].getPixelStride();
        if ((yPixelStride == 1) && (yRowStride == width)) {
            yBuffer.get(nv21, 0, ySize);
        } else {
            for (int row = 0; row < height; row++) {
                yBuffer.position(row * yRowStride);
                if (yPixelStride == 1) {
                    yBuffer.get(nv21, row * width, width);
                } else {
                    int rowOffset = row * width;
                    for (int col = 0; col < width; col++) {
                        nv21[rowOffset + col] = yBuffer.get();
                        if (col < width - 1) {
                            yBuffer.position(yBuffer.position() + yPixelStride - 1);
                        }
                    }
                }
            }
        }
        // 2. 提取 U / V 分量
        // NV21 存储格式: V, U, V, U...
        ByteBuffer uBuffer = proxyPlanes[1].getBuffer();
        ByteBuffer vBuffer = proxyPlanes[2].getBuffer();
        int uvRowStride = proxyPlanes[1].getRowStride();
        int uvPixelStride = proxyPlanes[1].getPixelStride();
        int uvWidth = width / 2;
        int uvHeight = height / 2;
        int pos = ySize;
        byte[] rowU = new byte[uvRowStride];
        byte[] rowV = new byte[uvRowStride];
        for (int row = 0; row < uvHeight; row++) {
            uBuffer.position(row * uvRowStride);
            vBuffer.position(row * uvRowStride);
            int uRemaining = uBuffer.remaining();
            int vRemaining = vBuffer.remaining();
            int bytesToRead = Math.min(uvRowStride, Math.min(uRemaining, vRemaining));
            uBuffer.get(rowU, 0, bytesToRead);
            vBuffer.get(rowV, 0, bytesToRead);
            for (int col = 0; col < uvWidth; col++) {
                int colOffset = col * uvPixelStride;
                // 对 UV 跨度进行双重界限保护
                // 防止非标 UVC 驱动硬件越界崩溃
                if ((colOffset < bytesToRead) && ((colOffset + 1) < bytesToRead)) {
                    nv21[pos++] = rowV[colOffset];
                    nv21[pos++] = rowU[colOffset];
                }
            }
        }
        return nv21;
    }

    /**
     * 清除帧缓存
     */
    public static void clearFrameCache() {
        synchronized (FRAME_LOCK) {
            latestYuvBytes = null;
            nv21BufferCache = null;
            latestFrameWidth = 0;
            latestFrameHeight = 0;
            latestFrameRotationDegrees = 0;
        }
    }
}