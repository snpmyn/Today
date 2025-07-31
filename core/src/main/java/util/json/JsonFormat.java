package util.json;

import androidx.annotation.NonNull;

/**
 * Created on 2018/6/21.
 *
 * @author 郑少鹏
 * @desc JSON 格式化
 */
public class JsonFormat {
    /**
     * 格式化 JSON
     *
     * @param json 待格式化 JSON
     * @return 格式化后 JSON 字符串
     */
    @NonNull
    public static String formatJson(@NonNull String json) {
        StringBuilder result = new StringBuilder();
        int length = json.length();
        int number = 0;
        char key;
        // 遍历输入字符串
        for (int i = 0; i < length; i++) {
            // 一【当前字符】
            key = json.charAt(i);
            // 二【当前字符前是括号/花括号】
            if ((key == '[') || (key == '{')) {
                //（1）前仍有字符且“:”，打印换行缩进。
                if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append(indent(number));
                }
                //（2）打印当前字符
                result.append(key);
                //（3）前方括号/花括号则后换行
                result.append('\n');
                //（4）每现一次前方括号/花括号，缩进次数增一。
                number++;
                result.append(indent(number));
                //（5）下次循环
                continue;
            }
            // 三【当前字符后方括号/花括号】
            if ((key == ']') || (key == '}')) {
                //（1）后方括号/花括号则前换行
                result.append('\n');
                //（2）每现一次后方括号/花括号，缩进次数减一。
                number--;
                result.append(indent(number));
                //（3）打印当前字符
                result.append(key);
                //（4）当前字符后还有字符且不为“,”
                // TODO: 2024/7/16 致最后一对}]换两行，故拼空字符。
                /*if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {*/
                /*result.append('\n');*/
                /*}*/
                //（5）下次循环
                continue;
            }
            // 四【当前字符逗号，逗号后换行缩进，不改缩进次数】
            if ((key == ',')) {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }
            // 五【打印当前字符】
            result.append(key);
        }
        return result.toString();
    }

    /**
     * 返指定次数的缩进字符串
     * <p>
     * 每次缩三空格，即 SPACE。
     *
     * @param number 缩进次数
     * @return 指定缩进次数的字符串
     */
    @NonNull
    private static String indent(int number) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < number; i++) {
            String space = "   ";
            stringBuilder.append(space);
        }
        return stringBuilder.toString();
    }
}
