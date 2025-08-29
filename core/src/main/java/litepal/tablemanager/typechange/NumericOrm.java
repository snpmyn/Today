package litepal.tablemanager.typechange;

/**
 * This class deals with numeric type.
 *
 * @author Tony Green
 * @since 1.0
 */
public class NumericOrm extends OrmChange {
    /**
     * If the field type passed in is int, long or short, it will change it into integer as column type.
     */
    @Override
    public String object2Relation(String fieldType) {
        if (fieldType != null) {
            switch (fieldType) {
                case "int":
                case "java.lang.Integer":
                case "long":
                case "java.lang.Long":
                case "short":
                case "java.lang.Short":
                    return "integer";
            }
        }
        return null;
    }
}