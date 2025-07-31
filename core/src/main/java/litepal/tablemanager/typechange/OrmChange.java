package litepal.tablemanager.typechange;

/**
 * This is abstract super class to map the object field types to database column
 * types. The purpose of this class is to define a abstract method, and let all
 * subclasses implement it. Each subclass deals with a kind of changing and each
 * subclass will do their own logic to finish the changing job.
 *
 * @author Tony Green
 * @since 1.0
 */
public abstract class OrmChange {
    /**
     * Subclasses implement this method to do their own logic to change types.
     *
     * @param fieldType The field type passed in.
     * @return Column type.
     */
    public abstract String object2Relation(String fieldType);
}
