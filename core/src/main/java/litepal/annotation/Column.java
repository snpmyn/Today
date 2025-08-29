package litepal.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for adding constraints to a column. Note that this annotation won't affect id column.
 *
 * @author Tony Green
 * @since 1.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * Set nullable constraint for the column.
     */
    boolean nullable() default true;

    /**
     * Set unique constraint for the column.
     */
    boolean unique() default false;

    /**
     * Set default value with String type for the column regardless of what column type is.
     */
    String defaultValue() default "";

    /**
     * Ignore to map this field into a column.
     */
    boolean ignore() default false;

    /**
     * Add index for the column.
     */
    boolean index() default false;
}