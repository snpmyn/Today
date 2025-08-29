package util.file;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import util.json.JsonFormat;
import util.url.UrlUtils;
import util.value.UtilMagic;

/**
 * Created on 2017/12/6.
 *
 * @author 郑少鹏
 * @desc FileUtils
 */
public class FileUtils {
    /**
     * 创文件夹
     *
     * @param folderPath  文件夹路径
     * @param singleLevel 单级否
     */
    public static void createFolder(String folderPath, boolean singleLevel) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            // mkdirs() 建多级文件夹
            // mkdir() 建一级文件夹
            if (singleLevel) {
                boolean flag = folder.mkdir();
            } else {
                boolean flag = folder.mkdirs();
            }
        }
    }

    /**
     * 据 Uri 获真路径
     *
     * @param context    上下文
     * @param contentUri contentUri
     * @return 据 Uri 真路径
     */
    public static @org.jetbrains.annotations.Nullable String getRealPathFromUri(Context context, Uri contentUri) {
        String[] pro = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = context.getContentResolver().query(contentUri, pro, null, null, null)) {
            int columnIndex;
            if (null != cursor) {
                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(columnIndex);
            } else {
                return null;
            }
        }
    }

    /**
     * 据路径文件存否
     *
     * @param filePath filePath
     * @return 据路径文件存否
     */
    public static boolean areFileExistByPath(final String filePath) {
        return areFileExist(getFileByPath(filePath));
    }

    /**
     * 文件存否
     *
     * @param file file
     * @return 文件存否
     */
    private static boolean areFileExist(final File file) {
        return (file != null) && file.exists();
    }

    /**
     * 据文件路径获文件
     *
     * @param filePath filePath
     * @return 文件
     */
    private static @org.jetbrains.annotations.Nullable File getFileByPath(final String filePath) {
        return areSpace(filePath) ? null : new File(filePath);
    }

    private static boolean areSpace(final String s) {
        if (null == s) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删文件或文件夹
     *
     * @param fileName 所删文件名
     * @return 成 true 败 false
     */
    public static boolean deleteByName(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return deleteByNameTwo(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删单文件
     *
     * @param fileName 所删文件名
     * @return 成 true 败 false
     */
    private static boolean deleteByNameTwo(String fileName) {
        File file = new File(fileName);
        // 路径对应文件存在且是文件则直删
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删单文件" + fileName + "成功");
                return true;
            } else {
                System.out.println("删单文件" + fileName + "失败");
                return false;
            }
        } else {
            System.out.println("删单文件失败：" + fileName + "不存在");
            return false;
        }
    }

    /**
     * 删目录及目录下文件
     *
     * @param dir 所删目录文件路径
     * @return 成 true 败 false
     */
    private static boolean deleteDirectory(@NotNull String dir) {
        // dir 不以文件分隔符结尾则自动添文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = (dir + File.separator);
        }
        File dirFile = new File(dir);
        // dir 对应文件不存或非目录则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Timber.d("删目录失败：%s %s", dir, "不存在");
            return false;
        }
        boolean flag = true;
        // 删文件夹所有文件（含子目录）
        File[] files = dirFile.listFiles();
        if (null != files) {
            for (File file : files) {
                // 删子文件
                if (file.isFile()) {
                    flag = deleteByNameTwo(file.getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
                // 删子目录
                else if (file.isDirectory()) {
                    flag = deleteDirectory(file.getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }
        }
        if (!flag) {
            Timber.d("删目录失败");
            return false;
        }
        // 删当前目录
        if (dirFile.delete()) {
            Timber.d("删目录 %s %s", dir, "成功");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删文件或文件夹
     *
     * @param file 文件
     */
    public static void deleteFile(File file) {
        try {
            if ((null == file) || !file.exists()) {
                return;
            }
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (null != files) {
                    for (File f : files) {
                        if (f.exists()) {
                            if (f.isDirectory()) {
                                deleteFile(f);
                            } else {
                                boolean flag = f.delete();
                            }
                        }
                    }
                }
            } else {
                boolean flag = file.delete();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * 据输入流存文件
     *
     * @param file        文件
     * @param inputStream 输入流
     * @return 文件
     */
    public static boolean writeFile(File file, InputStream inputStream) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            byte[] data = new byte[1024];
            int length;
            while ((length = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, length);
            }
            outputStream.flush();
            return true;
        } catch (Exception e) {
            Timber.e(e);
            if ((null != file) && file.exists()) {
                file.deleteOnExit();
            }
        } finally {
            closeStream(outputStream);
            closeStream(inputStream);
        }
        return false;
    }

    /**
     * 关流
     *
     * @param closeable closeable
     */
    private static void closeStream(Closeable closeable) {
        if ((null != closeable)) {
            try {
                closeable.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    /**
     * 文件转字符串
     *
     * @param filePath 文件地址
     * @return 字符串
     */
    public static @NotNull StringBuilder fileToString(String filePath) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String s;
            while (null != (s = bufferedReader.readLine())) {
                result.append(System.lineSeparator()).append(s);
            }
            bufferedReader.close();
        } catch (Exception e) {
            Timber.e(e);
        }
        return result;
    }

    /**
     * 拷贝
     *
     * @param newFile  新文件
     * @param destFile 目标文件
     * @return 成 true 败 false
     * @throws IOException 输入/出流异常
     */
    public static boolean copy(File newFile, File destFile) throws IOException {
        if ((null != newFile) && (null != destFile)) {
            if (!newFile.exists()) {
                return false;
            } else {
                BufferedInputStream var2 = null;
                BufferedOutputStream var3 = null;
                try {
                    String var4 = destFile.getAbsolutePath();
                    String var5 = var4.substring(0, var4.lastIndexOf(File.separator));
                    File var6 = new File(var5);
                    if (!var6.exists()) {
                        boolean flag = var6.mkdirs();
                    }
                    var2 = new BufferedInputStream(new FileInputStream(newFile));
                    var3 = new BufferedOutputStream(new FileOutputStream(destFile));
                    byte[] var7 = new byte[5120];
                    int var8;
                    while ((var8 = var2.read(var7)) != -1) {
                        var3.write(var7, 0, var8);
                    }
                    var3.flush();
                    return true;
                } finally {
                    if ((null != var2)) {
                        try {
                            var2.close();
                        } catch (IOException var19) {
                            Timber.e(var19);
                        }
                    }
                    if ((null != var3)) {
                        try {
                            var3.close();
                        } catch (IOException var18) {
                            Timber.e(var18);
                        }
                    }
                }
            }
        } else {
            return false;
        }
    }

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri String
     * @return Extension including the dot("."); "" if there is no extension; null if uri was null.
     */
    private static String getExtension(String uri) {
        if ((null == uri)) {
            return null;
        }
        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     * Whether the uri is a media uri.
     *
     * @param uri Uri
     * @return true if uri is a MediaStore uri
     */
    public static boolean areMediaUri(@NotNull Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * Convert file into uri.
     *
     * @param file File
     * @return Uri
     */
    public static Uri getUri(File file) {
        return (null != file) ? Uri.fromFile(file) : null;
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file File
     * @return File
     */
    public static File getPathWithoutFilename(File file) {
        if (null != file) {
            if (file.isDirectory()) {
                // no file to be split off
                // return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();
                // Construct path without file name.
                String pathWithoutName = (filepath.substring(0, filepath.length() - filename.length()));
                if (pathWithoutName.endsWith(UtilMagic.STRING_BACKSLASH)) {
                    pathWithoutName = pathWithoutName.substring(0, pathWithoutName.length() - 1);
                }
                return new File(pathWithoutName);
            }
        }
        return null;
    }

    /**
     * Get mime type for the give uri.
     *
     * @param file File
     * @return String
     */
    private static String getMimeType(@NotNull File file) {
        String extension = getExtension(file.getName());
        if (!extension.isEmpty()) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
        }
        return "application/octet-stream";
    }

    /**
     * Get mime type for the give uri.
     *
     * @param context   Context
     * @param authority YOUR_AUTHORITY.provider
     * @param uri       uri
     * @return String
     */
    public static @org.jetbrains.annotations.Nullable String getMimeType(Context context, String authority, Uri uri) {
        String path = getPath(context, authority, uri);
        File file;
        if (null != path) {
            file = new File(path);
            return getMimeType(file);
        } else {
            return null;
        }
    }

    /**
     * Whether the uri authority is local.
     *
     * @param authority YOUR_AUTHORITY.provider
     * @param uri       uri
     * @return boolean
     */
    private static boolean areLocalStorageDocument(@NotNull String authority, @NotNull Uri uri) {
        return authority.equals(uri.getAuthority());
    }

    /**
     * Whether the uri authority is ExternalStorageProvider.
     *
     * @param uri the uri to check
     * @return boolean
     */
    private static boolean areExternalStorageDocument(@NotNull Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * Whether the uri authority is DownloadsProvider.
     *
     * @param uri the uri to check
     * @return boolean
     */
    private static boolean areDownloadsDocument(@NotNull Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * Whether the uri authority is MediaProvider.
     *
     * @param uri the uri to check
     * @return boolean
     */
    private static boolean areMediaDocument(@NotNull Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Whether the uri authority is Google Photos.
     *
     * @param uri the uri to check
     * @return boolean
     */
    private static boolean areGooglePhotosUri(@NotNull Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this uri.
     * <p>
     * This is useful for MediaStore uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static @org.jetbrains.annotations.Nullable String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        final String column = MediaStore.Files.FileColumns.DATA;
        final String[] projection = {column};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if ((null != cursor) && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(column));
            }
        }
        return null;
    }

    /**
     * Get a file path from a uri.
     * <p>
     * This will get the the path for storage access framework documents, as well as the _data field for the MediaStore and other file-based ContentProviders.
     * Callers should check whether the path is local before assuming it represents a local file.
     *
     * @param context   Context
     * @param authority YOUR_AUTHORITY.provider
     * @param uri       the uri to query
     * @return String
     */
    public static @org.jetbrains.annotations.Nullable String getPath(final Context context, String authority, final @NotNull Uri uri) {
        Timber.d("Authority: %s +\n Fragment: %s +\n Port: %s +\n Query: %s +\n Scheme: %s +\n Host: %s +\n Segments: %s", uri.getAuthority(), uri.getFragment(), uri.getPort(), uri.getQuery(), uri.getScheme(), uri.getHost(), uri.getPathSegments().toString());
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (areLocalStorageDocument(authority, uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (areExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if (UtilMagic.STRING_PRIMARY.equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageState() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (areDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                if ((null != id) && id.startsWith(UtilMagic.STRING_RAW_COLON)) {
                    return id.substring(4);
                }
                String[] contentUriPrefixesToTry = new String[]{"content://downloads/public_downloads", "content://downloads/my_downloads"};
                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    Uri contentUri = null;
                    if (null != id) {
                        contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.parseLong(id));
                    }
                    try {
                        String path = getDataColumn(context, contentUri, null, null);
                        if (null != path) {
                            return path;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                String fileName = getFileName(context, authority, uri);
                File cacheDir = getDocumentCacheDir(context);
                File file = generateFileName(fileName, cacheDir);
                String destinationPath = null;
                if (null != file) {
                    destinationPath = file.getAbsolutePath();
                    saveFileFromUri(context, uri, destinationPath);
                }
                return destinationPath;
            }
            // MediaProvider
            else if (areMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                Uri contentUri = getUri(split);
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if (UtilMagic.STRING_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (areGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if (UtilMagic.STRING_FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 获取统一资源标识符
     *
     * @param split 字符串数组
     * @return 统一资源标识符
     */
    @Nullable
    private static Uri getUri(@NonNull String[] split) {
        final String type = split[0];
        Uri contentUri = null;
        if (UtilMagic.STRING_IMAGE.equals(type)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (UtilMagic.STRING_VIDEO.equals(type)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if (UtilMagic.STRING_AUDIO.equals(type)) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        return contentUri;
    }

    /**
     * Convert uri into file, if possible.
     *
     * @param context   Context
     * @param authority YOUR_AUTHORITY.provider
     * @param uri       unsupported or pointed to a remote resource
     * @return File
     */
    public static File getFile(Context context, String authority, Uri uri) {
        if (null != uri) {
            String path = getPath(context, authority, uri);
            if (UrlUtils.areLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }

    /**
     * Get the file size in a human-readable string.
     *
     * @param size 大小
     * @return 大小
     */
    public static @NotNull String getReadableFileSize(int size) {
        final int bytesInKiloBytes = 1024;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String kiloBytes = " KB";
        final String megaBytes = " MB";
        final String gigaBytes = " GB";
        float fileSize = 0;
        String suffix = kiloBytes;
        if (size > bytesInKiloBytes) {
            fileSize = Integer.valueOf(size / bytesInKiloBytes).floatValue();
            if (fileSize > bytesInKiloBytes) {
                fileSize = (fileSize / bytesInKiloBytes);
                if (fileSize > bytesInKiloBytes) {
                    fileSize = (fileSize / bytesInKiloBytes);
                    suffix = gigaBytes;
                } else {
                    suffix = megaBytes;
                }
            }
        }
        return dec.format(fileSize) + suffix;
    }

    private static @NotNull File getDocumentCacheDir(@NonNull Context context) {
        File dir = new File(context.getCacheDir(), "documents");
        if (!dir.exists()) {
            boolean flag = dir.mkdirs();
        }
        Timber.d("cacheDir: %s", context.getCacheDir());
        Timber.d("dir: %s", dir);
        return dir;
    }

    @Nullable
    private static File generateFileName(@Nullable String name, File directory) {
        if (null == name) {
            return null;
        }
        File file = new File(directory, name);
        if (file.exists()) {
            String fileName = name;
            String extension = "";
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex);
                extension = name.substring(dotIndex);
            }
            int index = 0;
            while (file.exists()) {
                index++;
                name = (fileName + '(' + index + ')' + extension);
                file = new File(directory, name);
            }
        }
        try {
            if (!file.createNewFile()) {
                return null;
            }
        } catch (IOException e) {
            Timber.e(e);
            return null;
        }
        return file;
    }

    private static void saveFileFromUri(@NotNull Context context, Uri uri, String destinationPath) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri); BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(destinationPath, false))) {
            try {
                byte[] buf = new byte[1024];
                if (null != inputStream) {
                    int tag = inputStream.read(buf);
                    do {
                        bufferedOutputStream.write(buf);
                    } while (inputStream.read(buf) != -1);
                }
            } catch (IOException e) {
                Timber.e(e);
            }
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    public static byte[] readBytesFromFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            // read file into bytes[]
            fileInputStream = new FileInputStream(file);
            int tag = fileInputStream.read(bytesArray);
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }
        return bytesArray;
    }

    public static @NotNull File createTempImageFile(@NotNull Context context, String fileName) throws IOException {
        // create an image file name
        File storageDir = new File(context.getCacheDir(), "documents");
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    private static String getFileName(@NonNull Context context, String authority, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        String filename = null;
        if (null == mimeType) {
            String path = getPath(context, authority, uri);
            if (null == path) {
                filename = getName(uri.toString());
            } else {
                File file = new File(path);
                filename = file.getName();
            }
        } else {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (null != returnCursor) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                returnCursor.close();
            }
        }
        return filename;
    }

    private static String getName(String filename) {
        if (null == filename) {
            return null;
        }
        int index = filename.lastIndexOf('/');
        return filename.substring(index + 1);
    }

    /**
     * assets 转文件
     *
     * @param context                   上下文
     * @param count                     数量
     * @param assetsFileNamePrefix      assets 文件前缀
     * @param transferredFileNamePrefix 转后文件前缀
     * @return 文件集
     */
    public static @NotNull List<File> assetsToFiles(Context context, int count, String assetsFileNamePrefix, String transferredFileNamePrefix) {
        final List<File> files = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                InputStream inputStream = context.getApplicationContext().getResources().getAssets().open(assetsFileNamePrefix + i);
                File file = new File(context.getApplicationContext().getExternalFilesDir("assets"), transferredFileNamePrefix + i);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int length = inputStream.read(buffer);
                while (length > 0) {
                    fileOutputStream.write(buffer, 0, length);
                    length = inputStream.read(buffer);
                }
                fileOutputStream.close();
                inputStream.close();
                files.add(file);
            } catch (IOException e) {
                Timber.e(e);
            }
        }
        return files;
    }

    /**
     * assets 转统一资源标识符
     *
     * @param context                   上下文
     * @param count                     数量
     * @param assetsFileNamePrefix      assets 文件前缀
     * @param transferredFileNamePrefix 转后文件前缀
     * @return 统一资源标识符集
     */
    public static @NotNull List<Uri> assetsToUris(Context context, int count, String assetsFileNamePrefix, String transferredFileNamePrefix) {
        final List<Uri> uris = new ArrayList<>();
        final List<File> files = assetsToFiles(context, count, assetsFileNamePrefix, transferredFileNamePrefix);
        for (int i = 0; i < count; i++) {
            Uri uri = Uri.fromFile(files.get(i));
            uris.add(uri);
        }
        return uris;
    }

    /**
     * 算图文件尺寸
     *
     * @param imageFile 图文件
     * @return 尺寸数组
     */
    public static int @NotNull [] calculatedImageFileSize(@NotNull File imageFile) {
        int[] size = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;
        return size;
    }

    /**
     * 存字符串为文件
     *
     * @param string   字符串
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return 存成功否
     */
    public static boolean saveStringAsFile(Context context, String string, String filePath, String fileName) {
        // 文件创否
        boolean flag = true;
        // 拼接文件完整路径
        String fullPath = filePath + File.separator;
        // 创 JSON 文件
        try {
            // 创新文件
            File file = new File(fullPath, fileName);
            File parentFile = file.getParentFile();
            if ((null != parentFile) && !parentFile.exists()) {
                // 父目录不存则创父目录
                boolean mkdirsFlag = parentFile.mkdirs();
            }
            if (file.exists()) {
                // 已存删旧文件
                /*boolean deleteFlagOne = file.delete();*/
                /*boolean deleteFlagTwo = deleteFileByFileName(context, fileName, "txt");*/
                boolean deleteFlagThree = deleteFileByFileUri(context, getUriForFile(context, file));
            }
            boolean createNewFileFlag = file.createNewFile();
            // 格式化 JSON 字符串
            string = JsonFormat.formatJson(string);
            // 格式化后字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            write.write(string);
            write.flush();
            write.close();
        } catch (Exception e) {
            Timber.e(e);
            flag = false;
        }
        // 返成否
        return flag;
    }

    /**
     * 通过文件统一资源标识符删除文件
     *
     * @param context 上下文
     * @param fileUri 文件统一资源标识符
     * @return 删除结果
     */
    public static boolean deleteFileByFileUri(@NonNull Context context, Uri fileUri) {
        try {
            int deletedRows = context.getContentResolver().delete(fileUri, null, null);
            return deletedRows > 0;
        } catch (SecurityException e) {
            Timber.e(e);
            return false;
        }
    }

    /**
     * 通过文件 ID 删除文件
     *
     * @param context  上下文
     * @param fileId   文件 ID
     * @param mimeType 文件格式
     * @return 删除结果
     */
    public static boolean deleteFileByFileId(Context context, long fileId, @NonNull String mimeType) {
        Uri contentUri;
        if (mimeType.startsWith("image/")) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (mimeType.startsWith("video/")) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if (mimeType.startsWith("audio/")) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else {
            // 通用文件
            contentUri = MediaStore.Files.getContentUri("external");
        }
        String selection = MediaStore.MediaColumns._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(fileId)};
        try {
            int deletedRows = context.getContentResolver().delete(contentUri, selection, selectionArgs);
            return deletedRows > 0;
        } catch (SecurityException e) {
            Timber.e(e);
            return false;
        }
    }

    /**
     * 通过文件名删除文件
     *
     * @param context  上下文
     * @param fileName 文件名
     * @param mimeType 文件格式
     * @return 删除结果
     */
    public static boolean deleteFileByFileName(Context context, String fileName, @NonNull String mimeType) {
        Uri contentUri;
        if (mimeType.startsWith("image/")) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (mimeType.startsWith("video/")) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if (mimeType.startsWith("audio/")) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else {
            // 通用文件
            contentUri = MediaStore.Files.getContentUri("external");
        }
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{fileName};
        try {
            int deletedRows = context.getContentResolver().delete(contentUri, selection, selectionArgs);
            return deletedRows > 0;
        } catch (SecurityException e) {
            Timber.e(e);
            return false;
        }
    }

    /**
     * 获取文件统一资源标识符
     *
     * @param context 上下文
     * @param file    文件
     * @return 统一资源标识符
     */
    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }

    /**
     * 获取文件流
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return 文件流
     * @throws FileNotFoundException FileNotFoundException
     */
    public static String getFileStream(@NonNull Context context, String fileName) throws FileNotFoundException {
        String content;
        FileInputStream fileInputStream = context.openFileInput(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
            Timber.e(e);
        } finally {
            content = stringBuilder.toString();
        }
        return content;
    }

    /**
     * 创建缓存文件
     * <p>
     * 如果您只需要暂时存储敏感数据，应使用应用在内部存储空间中的指定缓存目录保存数据。
     * 与所有应用专属存储空间一样，当用户卸载应用后，系统会移除存储在此目录中的文件，但也可以更早地移除此目录中的文件。
     * <p>
     * 注意：此缓存目录旨在存储应用的少量敏感数据。如需确定应用当前可用的缓存空间大小，请调用 getCacheQuotaBytes()。
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return 缓存文件
     * @throws IOException IOException
     */
    @NonNull
    public static File createCacheFile(@NonNull Context context, String fileName) throws IOException {
        return File.createTempFile(fileName, null, context.getCacheDir());
    }

    /**
     * 创建外部缓存文件
     * <p>
     * 如需将应用专属文件添加到外部存储空间中的缓存，请获取对 externalCacheDir 的引用。
     *
     * @param context  上下文
     * @param fileName 文件名
     */
    public static void createExternalCacheFile(@NonNull Context context, String fileName) {
        File externalCacheFile = new File(context.getExternalCacheDir(), fileName);
    }

    /**
     * 删除文件
     *
     * @param context  上下文
     * @param fileName 文件名
     */
    public static void deleteFile(@NonNull Context context, String fileName) {
        context.deleteFile(fileName);
    }

    /**
     * 验证存储空间的可用性
     * <p>
     * 由于外部存储空间位于用户可能能够移除的物理卷上，因此在尝试从外部存储空间读取应用专属数据或将应用专属数据写入外部存储空间之前，请验证该卷是否可访问。
     * 您可以通过调用 Environment.getExternalStorageState() 查询该卷的状态。
     * <p>
     * 如果返回的状态为 MEDIA_MOUNTED，那么您就可以在外部存储空间中读取和写入应用专属文件。
     * 如果返回的状态为 MEDIA_MOUNTED_READ_ONLY，您只能读取这些文件。
     *
     * @return 可读可写
     */
    private boolean areExternalStorageReadableAndWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 验证存储空间的可用性
     * <p>
     * 由于外部存储空间位于用户可能能够移除的物理卷上，因此在尝试从外部存储空间读取应用专属数据或将应用专属数据写入外部存储空间之前，请验证该卷是否可访问。
     * 您可以通过调用 Environment.getExternalStorageState() 查询该卷的状态。
     * <p>
     * 如果返回的状态为 MEDIA_MOUNTED，那么您就可以在外部存储空间中读取和写入应用专属文件。
     * 如果返回的状态为 MEDIA_MOUNTED_READ_ONLY，您只能读取这些文件。
     *
     * @return 只读
     */
    private boolean areExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }
}