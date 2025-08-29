package litepal.tablemanager.typechange;

/**
 * This class deals with boolean type.
 *
 * @author Tony Green
 * @since 1.0
 */
public class BooleanOrm extends OrmChange {
    /**
     * If the field type passed in is boolean, it will change it into integer as column type.
     */
    @Override
    public String object2Relation(String fieldType) {
        if (fieldType != null) {
            if (fieldType.equals("boolean") || fieldType.equals("java.lang.Boolean")) {
                return "integer";
            }
        }
        return null;
    }
}