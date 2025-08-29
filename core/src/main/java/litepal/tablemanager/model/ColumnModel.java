package litepal.tablemanager.model;

import android.text.TextUtils;

/**
 * This is a model class for columns. It stores column name, column type, and column constraints
 * information.
 *
 * @author Tony Green
 * @since 1.3
 */
public class ColumnModel {
    /**
     * Name of column.
     */
    private String columnName;
    /**
     * Type for column.
     */
    private String columnType;
    /**
     * Nullable constraint.
     */
    private boolean isNullable = true;
    /**
     * Unique constraint.
     */
    private boolean isUnique = false;
    /**
     * Default constraint.
     */
    private String defaultValue = "";
    /**
     * Has index for this column or not.
     */
    private boolean hasIndex = false;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean isNullable) {
        this.isNullable = isNullable;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean hasIndex() {
        return hasIndex;
    }

    public void setHasIndex(boolean hasIndex) {
        this.hasIndex = hasIndex;
    }

    public void setDefaultValue(String defaultValue) {
        if ("text".equalsIgnoreCase(columnType)) {
            if (!TextUtils.isEmpty(defaultValue)) {
                this.defaultValue = "'" + defaultValue + "'";
            }
        } else {
            this.defaultValue = defaultValue;
        }
    }

    /**
     * Judge current ColumnModel is id column or not.
     *
     * @return True if it's id column. False otherwise.
     */
    public boolean isIdColumn() {
        return "_id".equalsIgnoreCase(columnName) || "id".equalsIgnoreCase(columnName);
    }
}