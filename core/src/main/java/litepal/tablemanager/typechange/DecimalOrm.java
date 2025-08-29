package litepal.tablemanager.typechange;

/**
 * This class deals with decimal type.
 *
 * @author Tony Green
 * @since 1.0
 */
public class DecimalOrm extends OrmChange {
    /**
     * If the field type passed in is float or double, it will change it into
     * real as column type.
     */
    @Override
    public String object2Relation(String fieldType) {
        if (fieldType != null) {
            if (fieldType.equals("float") || fieldType.equals("java.lang.Float")) {
                return "real";
            }
            if (fieldType.equals("double") || fieldType.equals("java.lang.Double")) {
                return "real";
            }
        }
        return null;
    }
}