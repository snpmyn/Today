package litepal.tablemanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import litepal.exceptions.DatabaseGenerateException;
import litepal.tablemanager.model.AssociationsModel;
import litepal.tablemanager.model.ColumnModel;
import litepal.tablemanager.model.GenericModel;
import litepal.util.BaseUtility;
import litepal.util.Const;
import litepal.util.DBUtility;
import litepal.util.LitePalLog;
import timber.log.Timber;

/**
 * When models have associations such as one2one, many2one or many2many, tables
 * should add foreign key column or create intermediate table to make the object
 * association mapping right. This process will be proceed automatically without
 * concerning by users. To make this happen, user just need to declare the
 * associations clearly in the models, and make sure all the mapping models are
 * added in the litepal.xml file.
 *
 * @author Tony Green
 * @since 1.0
 */
public abstract class AssociationCreator extends Generator {
    protected abstract void createOrUpgradeTable(SQLiteDatabase db, boolean force);

    /**
     * {@link AssociationCreator} analyzes two things. Add associations
     * including add foreign key column to tables and create intermediate join
     * tables.
     */
    @Override
    protected void addOrUpdateAssociation(SQLiteDatabase db, boolean force) {
        addAssociations(getAllAssociations(), db, force);
    }

    /**
     * Generate a create table SQL by the passed in parameters. Note that it
     * will always generate a SQL with id/_id column in it as primary key and
     * this id is auto increment as integer if the autoIncrementId is true, or
     * no primary key will be added.
     *
     * @param tableName       The table name.
     * @param columnModels    A list contains all column models with column info.
     * @param autoIncrementId Generate an auto increment id or not. Only intermediate join table doesn't need
     *                        an auto increment id.
     * @return A generated create table SQL.
     */
    protected String generateCreateTableSQL(String tableName, Collection<ColumnModel> columnModels, boolean autoIncrementId) {
        StringBuilder createTableSQL = new StringBuilder("create table ");
        createTableSQL.append(tableName).append(" (");
        if (autoIncrementId) {
            createTableSQL.append("id integer primary key autoincrement,");
        }
        if (isContainsOnlyIdField(columnModels)) {
            // Remove the last comma when only have id field in model.
            createTableSQL.deleteCharAt(createTableSQL.length() - 1);
        }
        boolean needSeparator = false;
        for (ColumnModel columnModel : columnModels) {
            if (columnModel.isIdColumn()) {
                continue;
            }
            if (needSeparator) {
                createTableSQL.append(", ");
            }
            needSeparator = true;
            createTableSQL.append(columnModel.getColumnName()).append(" ").append(columnModel.getColumnType());
            if (!columnModel.isNullable()) {
                createTableSQL.append(" not null");
            }
            if (columnModel.isUnique()) {
                createTableSQL.append(" unique");
            }
            String defaultValue = columnModel.getDefaultValue();
            if (!TextUtils.isEmpty(defaultValue)) {
                createTableSQL.append(" default ").append(defaultValue);
            }
        }
        createTableSQL.append(")");
        LitePalLog.d(TAG, "create table sql is >> " + createTableSQL);
        return createTableSQL.toString();
    }

    /**
     * Generate create index SQLs by the passed in parameters.
     *
     * @param tableName    The table name.
     * @param columnModels A list contains all column models with column info.
     * @return A generated create index SQLs.
     */
    protected List<String> generateCreateIndexSQLs(String tableName, @NonNull Collection<ColumnModel> columnModels) {
        List<String> sls = new ArrayList<>();
        for (ColumnModel columnModel : columnModels) {
            if (columnModel.hasIndex()) {
                sls.add(generateCreateIndexSQL(tableName, columnModel));
            }
        }
        return sls;
    }

    /**
     * Generate a SQL for dropping table.
     *
     * @param tableName The table name.
     * @return A SQL to drop table.
     */
    protected String generateDropTableSQL(String tableName) {
        return "drop table if exists " + tableName;
    }

    /**
     * Generate a SQL for add new column into the existing table.
     *
     * @param tableName   The table which want to add a column
     * @param columnModel Which contains column info
     * @return A SQL to add new column.
     */
    protected String generateAddColumnSQL(String tableName, @NonNull ColumnModel columnModel) {
        StringBuilder addColumnSQL = new StringBuilder();
        addColumnSQL.append("alter table ").append(tableName);
        addColumnSQL.append(" add column ").append(columnModel.getColumnName());
        addColumnSQL.append(" ").append(columnModel.getColumnType());
        if (!columnModel.isNullable()) {
            addColumnSQL.append(" not null");
        }
        if (columnModel.isUnique()) {
            addColumnSQL.append(" unique");
        }
        String defaultValue = columnModel.getDefaultValue();
        if (!TextUtils.isEmpty(defaultValue)) {
            addColumnSQL.append(" default ").append(defaultValue);
        } else {
            if (!columnModel.isNullable()) {
                if ("integer".equalsIgnoreCase(columnModel.getColumnType())) {
                    defaultValue = "0";
                } else if ("text".equalsIgnoreCase(columnModel.getColumnType())) {
                    defaultValue = "''";
                } else if ("real".equalsIgnoreCase(columnModel.getColumnType())) {
                    defaultValue = "0.0";
                }
                addColumnSQL.append(" default ").append(defaultValue);
            }
        }
        LitePalLog.d(TAG, "add column sql is >> " + addColumnSQL);
        return addColumnSQL.toString();
    }

    /**
     * Generate create index SQL by the passed in parameters.
     *
     * @param tableName   The table name.
     * @param columnModel Column model with column info.
     * @return A generated create index SQL.
     */
    protected String generateCreateIndexSQL(String tableName, @NonNull ColumnModel columnModel) {
        StringBuilder createIndexSQL = new StringBuilder();
        if (columnModel.hasIndex()) {
            createIndexSQL.append("create index ");
            createIndexSQL.append(DBUtility.getIndexName(tableName, columnModel.getColumnName()));
            createIndexSQL.append(" on ");
            createIndexSQL.append(tableName);
            createIndexSQL.append(" (");
            createIndexSQL.append(columnModel.getColumnName());
            createIndexSQL.append(")");
            LitePalLog.d(TAG, "create table index sql is >> " + createIndexSQL);
        }
        return createIndexSQL.toString();
    }

    /**
     * Judge the passed in column is a foreign key column format or not. Each
     * column name ends with _id will be considered as foreign key column
     * format.
     *
     * @param columnName The name of column.
     * @return Return true if it's foreign column format, otherwise return
     * false.
     */
    protected boolean isForeignKeyColumnFormat(String columnName) {
        if (!TextUtils.isEmpty(columnName)) {
            return columnName.toLowerCase(Locale.US).endsWith("_id") && !columnName.equalsIgnoreCase("_id");
        }
        return false;
    }

    /**
     * Once there's new table created. The table name will be saved into
     * table_schema as a copy. Each table name will be saved only once.
     *
     * @param tableName The table name.
     * @param tableType 0 means normal table, 1 means intermediate join table.
     * @param db        Instance of SQLiteDatabase.
     */
    protected void giveTableSchemaACopy(String tableName, int tableType, SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder("select * from ");
        sql.append(Const.TableSchema.TABLE_NAME);
        LitePalLog.d(TAG, "giveTableSchemaACopy SQL is >> " + sql);
        try (Cursor cursor = db.rawQuery(sql.toString(), null)) {
            if (isNeedToGiveACopy(cursor, tableName)) {
                ContentValues values = new ContentValues();
                values.put(Const.TableSchema.COLUMN_NAME, BaseUtility.changeCase(tableName));
                values.put(Const.TableSchema.COLUMN_TYPE, tableType);
                db.insert(Const.TableSchema.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Save the name of a created table into table_schema, but there're some
     * extra rules. Each table name should be only saved once, and special
     * tables will not be saved.
     *
     * @param cursor    The cursor used to iterator values in the table.
     * @param tableName The table name.
     * @return If all rules are passed return true, any of them failed return
     * false.
     */
    private boolean isNeedToGiveACopy(Cursor cursor, String tableName) {
        return !isValueExists(cursor, tableName) && !isSpecialTable(tableName);
    }

    /**
     * Judge the table name has already exist in the table_schema or not.
     *
     * @param cursor    The cursor used to iterator values in the table.
     * @param tableName The table name.
     * @return If value exists return true, or return false.
     */
    private boolean isValueExists(@NonNull Cursor cursor, String tableName) {
        boolean exist = false;
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(Const.TableSchema.COLUMN_NAME));
                if (name.equalsIgnoreCase(tableName)) {
                    exist = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        return exist;
    }

    /**
     * Judge a table is a special table or not. Currently table_schema is a
     * special table.
     *
     * @param tableName The table name.
     * @return Return true if it's special table.
     */
    private boolean isSpecialTable(String tableName) {
        return Const.TableSchema.TABLE_NAME.equalsIgnoreCase(tableName);
    }

    /**
     * Analyzing all the association models in the collection. Judge their
     * association types. If it's one2one or many2one associations, add the
     * foreign key column to the associated table. If it's many2many
     * associations, create an intermediate join table.
     *
     * @param associatedModels A collection contains all the association models.Use the
     *                         association models to get association type and associated
     *                         table names.
     * @param db               Instance of SQLiteDatabase.
     * @param force            Drop the table first if it already exists.
     */
    private void addAssociations(@NonNull Collection<AssociationsModel> associatedModels, SQLiteDatabase db, boolean force) {
        for (AssociationsModel associationModel : associatedModels) {
            if (Const.Model.MANY_TO_ONE == associationModel.getAssociationType() || Const.Model.ONE_TO_ONE == associationModel.getAssociationType()) {
                addForeignKeyColumn(associationModel.getTableName(), associationModel.getAssociatedTableName(), associationModel.getTableHoldsForeignKey(), db);
            } else if (Const.Model.MANY_TO_MANY == associationModel.getAssociationType()) {
                createIntermediateTable(associationModel.getTableName(), associationModel.getAssociatedTableName(), db, force);
            }
        }
        for (GenericModel genericModel : getGenericModels()) {
            createGenericTable(genericModel, db, force);
        }
    }

    /**
     * When it comes to many2many associations. Database need to create an
     * intermediate table for mapping this association. This method helps create
     * such a table, and the table name follows the concatenation of the two
     * target table names in alphabetical order with underline in the middle.
     *
     * @param tableName           The table name.
     * @param associatedTableName The associated table name.
     * @param db                  Instance of SQLiteDatabase.
     * @param force               Drop the table first if it already exists.
     */
    private void createIntermediateTable(String tableName, String associatedTableName, SQLiteDatabase db, boolean force) {
        List<ColumnModel> columnModelList = new ArrayList<>();
        ColumnModel column1 = new ColumnModel();
        column1.setColumnName(tableName + "_id");
        column1.setColumnType("integer");
        ColumnModel column2 = new ColumnModel();
        column2.setColumnName(associatedTableName + "_id");
        column2.setColumnType("integer");
        columnModelList.add(column1);
        columnModelList.add(column2);
        String intermediateTableName = DBUtility.getIntermediateTableName(tableName, associatedTableName);
        List<String> sls = new ArrayList<>();
        if (DBUtility.isTableExists(intermediateTableName, db)) {
            if (force) {
                sls.add(generateDropTableSQL(intermediateTableName));
                sls.add(generateCreateTableSQL(intermediateTableName, columnModelList, false));
            }
        } else {
            sls.add(generateCreateTableSQL(intermediateTableName, columnModelList, false));
        }
        execute(sls, db);
        giveTableSchemaACopy(intermediateTableName, Const.TableSchema.INTERMEDIATE_JOIN_TABLE, db);
    }

    /**
     * When declared generic collection fields in model class. Database need to create
     * generic tables for mapping these fields. This method helps create such a table.
     *
     * @param genericModel The GenericModel instance.
     * @param db           Instance of SQLiteDatabase.
     * @param force        Drop the table first if it already exists.
     */
    private void createGenericTable(@NonNull GenericModel genericModel, SQLiteDatabase db, boolean force) {
        String tableName = genericModel.getTableName();
        List<ColumnModel> columnModelList = getColumnModels(genericModel);
        List<String> sls = new ArrayList<>();
        if (DBUtility.isTableExists(tableName, db)) {
            if (force) {
                sls.add(generateDropTableSQL(tableName));
                sls.add(generateCreateTableSQL(tableName, columnModelList, false));
            }
        } else {
            sls.add(generateCreateTableSQL(tableName, columnModelList, false));
        }
        execute(sls, db);
        giveTableSchemaACopy(tableName, Const.TableSchema.GENERIC_TABLE, db);
    }

    @NonNull
    private static List<ColumnModel> getColumnModels(@NonNull GenericModel genericModel) {
        String valueColumnName = genericModel.getValueColumnName();
        String valueColumnType = genericModel.getValueColumnType();
        String valueIdColumnName = genericModel.getValueIdColumnName();
        List<ColumnModel> columnModelList = new ArrayList<>();
        ColumnModel column1 = new ColumnModel();
        column1.setColumnName(valueColumnName);
        column1.setColumnType(valueColumnType);
        ColumnModel column2 = new ColumnModel();
        column2.setColumnName(valueIdColumnName);
        column2.setColumnType("integer");
        columnModelList.add(column1);
        columnModelList.add(column2);
        return columnModelList;
    }

    /**
     * This method is used to add many to one association or one to one
     * association on tables. It will automatically build a SQL to add foreign
     * key to a table. If the passed in table name or associated table name
     * doesn't exist, it will throw an exception.
     *
     * @param tableName            The table name.
     * @param associatedTableName  The associated table name.
     * @param tableHoldsForeignKey The table which holds the foreign key.
     * @param db                   Instance of SQLiteDatabase.
     */
    protected void addForeignKeyColumn(String tableName, String associatedTableName, String tableHoldsForeignKey, SQLiteDatabase db) {
        if (DBUtility.isTableExists(tableName, db)) {
            if (DBUtility.isTableExists(associatedTableName, db)) {
                String foreignKeyColumn = null;
                if (tableName.equals(tableHoldsForeignKey)) {
                    foreignKeyColumn = getForeignKeyColumnName(associatedTableName);
                } else if (associatedTableName.equals(tableHoldsForeignKey)) {
                    foreignKeyColumn = getForeignKeyColumnName(tableName);
                }
                if (!DBUtility.isColumnExists(foreignKeyColumn, tableHoldsForeignKey, db)) {
                    ColumnModel columnModel = new ColumnModel();
                    columnModel.setColumnName(foreignKeyColumn);
                    columnModel.setColumnType("integer");
                    List<String> sls = new ArrayList<>();
                    sls.add(generateAddColumnSQL(tableHoldsForeignKey, columnModel));
                    execute(sls, db);
                } else {
                    LitePalLog.d(TAG, "column " + foreignKeyColumn + " is already exist, no need to add one");
                }
            } else {
                throw new DatabaseGenerateException(DatabaseGenerateException.TABLE_DOES_NOT_EXIST + associatedTableName);
            }
        } else {
            throw new DatabaseGenerateException(DatabaseGenerateException.TABLE_DOES_NOT_EXIST + tableName);
        }
    }

    /**
     * Check if the ColumnModel list contains only id field.
     *
     * @param columnModels List contains model fields.
     * @return If ColumnModel list is empty or contains only id, _id field, return true. Otherwise return false.
     */
    private boolean isContainsOnlyIdField(@NonNull Collection<ColumnModel> columnModels) {
        for (ColumnModel columnModel : columnModels) {
            if (!columnModel.isIdColumn()) return false;
        }
        return true;
    }
}
