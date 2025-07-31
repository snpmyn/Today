package litepal.tablemanager.typechange;

/**
 * This class deals with text type.
 *
 * @author Tony Green
 * @since 1.0
 */
public class TextOrm extends OrmChange {
    /**
     * If the field type passed in is char or String, it will change it into
     * text as column type.
     */
    @Override
    public String object2Relation(String fieldType) {
        if (fieldType != null) {
            if (fieldType.equals("char") || fieldType.equals("java.lang.Character")) {
                return "text";
            }
            if (fieldType.equals("java.lang.String")) {
                return "text";
            }
        }
        return null;
    }
}
