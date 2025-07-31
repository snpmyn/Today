package litepal.tablemanager.typechange;

/**
 * This class deals with date type.
 *
 * @author Tony Green
 * @since 1.1
 */
public class DateOrm extends OrmChange {
    /**
     * If the field type passed in is Date, it will change it into integer as column type.
     */
    @Override
    public String object2Relation(String fieldType) {
        if (fieldType != null) {
            if (fieldType.equals("java.util.Date")) {
                return "integer";
            }
        }
        return null;
    }
}