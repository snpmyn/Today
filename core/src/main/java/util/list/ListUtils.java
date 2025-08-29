package util.list;

import android.content.Context;
import android.text.TextUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

/**
 * Created on 2020/12/28
 *
 * @author zsp
 * @desc List 工具类
 */
public class ListUtils {
    /**
     * 集合为空
     *
     * @param list 集合
     * @param <T>  <T>
     * @return 集合为空否
     */
    public static <T> boolean listIsEmpty(List<T> list) {
        return ((null == list) || (list.isEmpty()));
    }

    /**
     * 集合不为空
     *
     * @param list 集合
     * @param <T>  <T>
     * @return 集合不为空否
     */
    public static <T> boolean listIsNotEmpty(List<T> list) {
        return ((null != list) && (!list.isEmpty()));
    }

    /**
     * 按序去重
     *
     * @param inputStringList 输入集合
     * @return 按序去重后集合
     */
    @Contract("_ -> param1")
    public static List<String> removeDuplicationWithSort(@NotNull List<String> inputStringList) {
        for (int i = 0; i < inputStringList.size(); i++) {
            for (int j = (inputStringList.size() - 1); j > i; j--) {
                if (TextUtils.equals(inputStringList.get(i), inputStringList.get(j))) {
                    inputStringList.remove(j);
                }
            }
        }
        return inputStringList;
    }

    /**
     * 无序去重
     *
     * @param inputStringList 输入集合
     * @return 无序去重后集合
     */
    @Contract("_ -> new")
    public static @NotNull List<String> removeDuplicationWithoutSort(List<String> inputStringList) {
        HashSet<String> stringHashSet = new HashSet<>(inputStringList);
        return new ArrayList<>(stringHashSet);
    }

    /**
     * 存数据（data -> data -> 包名 -> files）
     *
     * @param context  上下文
     * @param list     集合
     * @param fileName 文件名
     */
    public static void saveListToData(Context context, List<Object> list, String fileName) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            File file = new File(context.getFilesDir().getAbsoluteFile(), fileName);
            // 建空文件
            fileOutputStream = new FileOutputStream(file.toString());
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(list);
        } catch (Exception e) {
            Timber.e(e);
        }
        if (null != objectOutputStream) {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }
        if (null != fileOutputStream) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    /**
     * 获数据
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return 数据
     */
    public static List<?> getListFromData(Context context, String fileName) {
        ObjectInputStream objectInputStream = null;
        FileInputStream fileInputStream = null;
        List<?> saveList = new ArrayList<>();
        try {
            File file = new File(context.getFilesDir().getAbsoluteFile(), fileName);
            fileInputStream = new FileInputStream(file.toString());
            objectInputStream = new ObjectInputStream(fileInputStream);
            saveList = (List<?>) objectInputStream.readObject();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            try {
                if (null != objectInputStream) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                Timber.e(e);
            }
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                Timber.e(e);
            }
        }
        return saveList;
    }
}