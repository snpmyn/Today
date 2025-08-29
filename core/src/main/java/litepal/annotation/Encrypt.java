package litepal.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for encrypt string field value when persisted into database.
 *
 * @author Tony Green
 * @since 1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Encrypt {
    /**
     * Set the algorithm for encryption.
     */
    String algorithm();
}