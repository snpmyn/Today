package util.data;

import android.util.Base64;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @desc: 凯撒加密工具类
 * <p>
 * 暂无引用
 * <p>
 * 凯撒加密法，或称恺撒加密、恺撒变换、变换加密，是一种最简单且广为人知的加密技术。
 * 它是一种替换加密的技术，明文中的所有字母都在字母表上向后（或向前）按照一个固定数目进行偏移后被替换成密文。
 * <p>
 * 根据偏移量的不同，还存在若干特定的凯撒加密法名称：
 * 偏移量为 10: Avocat (A -> K)
 * 偏移量为 13: ROT13
 * 偏移量为 -5: Cassis (K 6)
 * 偏移量为 16: Cassette (K 7)
 * @author: zsp
 * @date: 2020-08-18 15:37
 */
public class CaesarCipherUtils {
    /**
     * 加密
     * <p>
     * 字符串转 base64 后加密
     *
     * @param string      字符串
     * @param offsetValue 偏移量 (1 - 25)
     * @return 加密结果
     */
    public static String encrypt(String string, int offsetValue) {
        if (null == string) {
            return null;
        }
        String base64 = new String(Base64.encode(string.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP));
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : base64.toCharArray()) {
            stringBuilder.append(encrypt(c, offsetValue));
        }
        return stringBuilder.toString();
    }

    /**
     * 解密
     *
     * @param string      字符串
     * @param offsetValue 偏移量 (1 - 25)
     * @return 解密后结果
     */
    public static String decrypt(String string, int offsetValue) {
        if (null == string) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : string.toCharArray()) {
            // 解密时偏移量取相反数
            stringBuilder.append(encrypt(c, -offsetValue));
        }
        return new String(Base64.decode(stringBuilder.toString().getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP));
    }

    /**
     * 包裹输入流
     * <p>
     * 原输入流为加密数据输入流
     *
     * @param inputStream 输入流
     * @param offsetValue 偏移量 (1 - 25)
     * @return 解密后输入流
     */
    @Contract("_, _ -> new")
    public static @NotNull InputStream wrapInputStream(InputStream inputStream, int offsetValue) {
        return new DecryptInputStream(inputStream, offsetValue);
    }

    /**
     * 包裹输出流
     * <p>
     * 包裹后输出流为加密输出流
     *
     * @param outputStream 输出流
     * @param offsetValue  偏移量 (1 - 25)
     * @return 加密后输出流
     */
    @Contract("_, _ -> new")
    public static @NotNull OutputStream wrapOutputStream(OutputStream outputStream, int offsetValue) {
        return new EncryptOutputStream(outputStream, offsetValue);
    }

    /**
     * 加密
     *
     * @param c           字节
     * @param offsetValue 偏移量 (1 - 25)
     * @return 加密结果
     */
    private static char encrypt(char c, int offsetValue) {
        // 如果字符串中某字符是小写字母
        if ((c >= 'a') && (c <= 'z')) {
            // 移动 key % 26 位
            c += (char) (offsetValue % 26);
            if (c < 'a') {
                // 向左超界
                c += 26;
            } else if (c > 'z') {
                // 向右超界
                c -= 26;
            }
        }
        // 如果字符串中某字符是大写字母
        else if ((c >= 'A') && (c <= 'Z')) {
            // 移动 key % 26 位
            c += (char) (offsetValue % 26);
            if (c < 'A') {
                // 向左超界
                c += 26;
            } else if (c > 'Z') {
                // 向右超界
                c -= 26;
            }
        }
        return c;
    }

    /**
     * 解密输入流
     */
    private static class DecryptInputStream extends InputStream {
        private final InputStream inputStream;
        private final int k;

        DecryptInputStream(InputStream in, int k) {
            this.inputStream = in;
            this.k = -k;
        }

        @Override
        public int read() throws IOException {
            int i = inputStream.read();
            if (i == -1) {
                return i;
            }
            return encrypt((char) i, k);
        }
    }

    /**
     * 加密输出流
     */
    private static class EncryptOutputStream extends OutputStream {
        private final OutputStream outputStream;
        private final int k;

        EncryptOutputStream(OutputStream out, int k) {
            this.outputStream = out;
            this.k = k;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(encrypt((char) b, k));
        }
    }
}