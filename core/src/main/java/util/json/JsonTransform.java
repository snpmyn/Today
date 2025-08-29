package util.json;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import util.log.LogUtils;

/**
 * Created on 2018/4/3.
 *
 * @author 郑少鹏
 * @desc JSON 转化
 */
public class JsonTransform {
    /**
     * 文件转 JSON
     *
     * @param filePath 文件路径
     *                 相对路径 + File.separator + 文件名
     * @return 转后 JSON 字符串
     */
    @NonNull
    public static String transformJsonFromFile(String filePath) {
        File file = new File(filePath);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while (null != (line = bufferedReader.readLine())) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            LogUtils.exception(e);
        }
        return stringBuilder.toString();
    }

    /**
     * assets 转 JSON
     *
     * @param context    上下文
     * @param assetsPath assets 路径
     * @return 转后 JSON 字符串
     */
    public String transformJsonFromAssets(@NonNull Context context, String assetsPath) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(assetsPath)));
            String line;
            while (null != (line = bufferedReader.readLine())) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            LogUtils.exception(e);
        }
        return stringBuilder.toString();
    }
}