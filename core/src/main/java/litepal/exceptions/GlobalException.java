package litepal.exceptions;

/**
 * This is where all the global exceptions declared of LitePal.
 *
 * @author Tony Green
 * @since 1.0
 */
public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    /**
     * Application context is null.
     */
    public static final String APPLICATION_CONTEXT_IS_NULL = "Application context is null. Maybe you neither configured your application name with \"org.litepal.LitePalApplication\" in your AndroidManifest.xml, nor called LitePal.initialize(Context) method.";

    /**
     * Constructor of GlobalException.
     *
     * @param errorMessage the description of this exception.
     */
    public GlobalException(String errorMessage) {
        super(errorMessage);
    }
}
