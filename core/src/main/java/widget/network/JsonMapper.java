package widget.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created on 2026/7/2.
 *
 * @author 郑少鹏
 * @desc JSON 映射器
 */
public class JsonMapper {
    private static final Gson gson = new Gson();

    /**
     * JSON 字符串转目标对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标对象类型
     * @param <T>   目标对象类型
     * @return 转后目标对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }

    /**
     * JSON 字符串转目标对象 List
     *
     * @param json  JSON 字符串
     * @param clazz 目标对象类型
     * @param <T>   目标对象类型
     * @return 转后目标对象 List
     */
    public static <T> List<T> fromListJson(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json)) {
            return Collections.emptyList();
        }
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return gson.fromJson(json, type);
    }

    /**
     * JSON 字符串转目标对象 Map
     *
     * @param json  JSON 字符串
     * @param key   键类型
     * @param value 值类型
     * @param <K>   键类型
     * @param <V>   值类型
     * @return 转后目标对象 Map
     */
    public static <K, V> Map<K, V> fromMapJson(String json, Class<K> key, Class<V> value) {
        if (TextUtils.isEmpty(json)) {
            return Collections.emptyMap();
        }
        Type type = TypeToken.getParameterized(Map.class, key, value).getType();
        return gson.fromJson(json, type);
    }
}