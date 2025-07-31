package litepal.util.cipher;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

/**
 * Utility to manage encryption and decryption for different algorithms.
 *
 * @author Tony Green
 * @since 1.6
 */
public class CipherUtil {
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static String aesKey = "LitePalKey";

    /**
     * Encrypt the plain text with AES algorithm.
     *
     * @param plainText The plain text.
     * @return The Encrypt content.
     */
    @Nullable
    public static String aesEncrypt(String plainText) {
        if (TextUtils.isEmpty(plainText)) {
            return plainText;
        }
        try {
            return AESCrypt.encrypt(aesKey, plainText);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * Decrypt the encrypted text with AES algorithm.
     *
     * @param encryptedText The encrypted text.
     * @return The plain content.
     */
    @Nullable
    public static String aesDecrypt(String encryptedText) {
        if (TextUtils.isEmpty(encryptedText)) {
            return encryptedText;
        }
        try {
            return AESCrypt.decrypt(aesKey, encryptedText);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * Encrypt the plain text with MD5 algorithm.
     *
     * @param plainText The plain text.
     * @return The Encrypt content.
     */
    @NonNull
    public static String md5Encrypt(@NonNull String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(plainText.getBytes(Charset.defaultCharset()));
            return new String(toHex(digest.digest()));
        } catch (NoSuchAlgorithmException e) {
            Timber.e(e);
        }
        return "";
    }

    @NonNull
    @Contract(pure = true)
    private static char[] toHex(@NonNull byte[] data) {
        char[] toDigits = DIGITS_UPPER;
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }
}