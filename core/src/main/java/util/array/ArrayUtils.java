package util.array;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * Created on 2025/9/19.
 *
 * @author 郑少鹏
 * @desc 数组工具类
 */
public class ArrayUtils {
    /**
     * 数组为空
     *
     * @param array 数组
     * @param <T>   泛型
     * @return 数组为空否
     */
    public static <T> boolean arrayIsEmpty(T[] array) {
        return ((null == array) || (array.length == 0));
    }

    /**
     * 数组不为空
     *
     * @param array 数组
     * @param <T>   泛型
     * @return 数组不为空否
     */
    public static <T> boolean arrayIsNotEmpty(T[] array) {
        return ((null != array) && (array.length != 0));
    }

    /**
     * 下标
     *
     * @param array  数组
     * @param object object
     * @param <T>    泛型
     * @return 下标
     */
    @Contract(pure = true)
    public static <T> int index(@NonNull T[] array, T object) {
        for (int i = 0; i < array.length; i++) {
            if (object.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }
}