package litepal.exceptions;

/**
 * When LitePal deals with CRUD actions of LitePalSupport, it may throw DataSupportException for older versions API.
 * The new CRUD APIs should throw {@link LitePalSupportException}
 *
 * @author Tony Green
 * @since 1.1
 */
public class DataSupportException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of LitePalSupportException.
     *
     * @param errorMessage The description of this exception.
     */
    public DataSupportException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructor of LitePalSupportException.
     *
     * @param errorMessage The description of this exception.
     * @param throwable    The cause of this exception.
     */
    public DataSupportException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
