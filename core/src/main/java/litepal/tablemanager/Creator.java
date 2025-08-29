package litepal.tablemanager;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import litepal.tablemanager.model.TableModel;
import litepal.util.Const;
import litepal.util.DBUtility;

/**
 * This is a subclass of Generator. Use to create tables. It will automatically
 * build a create table SQL based on the passing TableModel object. In case of
 * there's already a table with the same name in the database, LitePal will
 * always drop the table first before create a new one. If there's syntax error
 * in the executing SQL by accident, Creator will throw a
 * DatabaseGenerateException.
 *
 * @author Tony Green
 * @since 1.0
 */
class Creator extends AssociationCreator {
    public static final String TAG = "Creator";

    /**
     * Analyzing the table model, create a table in the database based on the
     * table model's value.
     */
    @Override
    protected void createOrUpgradeTable(SQLiteDatabase db, boolean force) {
        for (TableModel tableModel : getAllTableModels()) {
            createOrUpgradeTable(tableModel, db, force);
        }
    }

    protected void createOrUpgradeTable(TableModel tableModel, SQLiteDatabase db, boolean force) {
        execute(getCreateTableSQLs(tableModel, db, force), db);
        giveTableSchemaACopy(tableModel.getTableName(), Const.TableSchema.NORMAL_TABLE, db);
    }

    /**
     * When creating a new table, it should always try to drop the same name
     * table if exists. This method create a SQL array for the whole create
     * table job.
     *
     * @param tableModel The table model.
     * @param db         Instance of SQLiteDatabase.
     * @param force      Drop the table first if it already exists.
     * @return A SQL array contains drop table if it exists and create new
     * table.
     */
    protected List<String> getCreateTableSQLs(TableModel tableModel, SQLiteDatabase db, boolean force) {
        List<String> sqlList = new ArrayList<>();
        if (force) {
            sqlList.add(generateDropTableSQL(tableModel));
            sqlList.add(generateCreateTableSQL(tableModel));
        } else {
            if (DBUtility.isTableExists(tableModel.getTableName(), db)) {
                return null;
            } else {
                sqlList.add(generateCreateTableSQL(tableModel));
            }
        }
        // create index after create table
        sqlList.addAll(generateCreateIndexSQLs(tableModel));
        return sqlList;
    }

    /**
     * Generate a SQL for dropping table.
     *
     * @param tableModel The table model.
     * @return A SQL to drop table.
     */
    private String generateDropTableSQL(@NonNull TableModel tableModel) {
        return generateDropTableSQL(tableModel.getTableName());
    }

    /**
     * Generate a create table SQL by analyzing the TableModel. Note that it
     * will always generate a SQL with id/_id column in it as primary key, and
     * this id is auto increment as integer. Do not try to assign or modify it.
     *
     * @param tableModel Use the TableModel to get table name and columns name to
     *                   generate SQL.
     * @return A generated create table SQL.
     */
    String generateCreateTableSQL(@NonNull TableModel tableModel) {
        return generateCreateTableSQL(tableModel.getTableName(), tableModel.getColumnModels(), true);
    }

    /**
     * Generate create index SQLs by analyzing the TableModel.
     *
     * @param tableModel Use the TableModel to get table name and columns name to
     *                   generate SQLs.
     * @return A generated create index SQLs.
     */
    List<String> generateCreateIndexSQLs(@NonNull TableModel tableModel) {
        return generateCreateIndexSQLs(tableModel.getTableName(), tableModel.getColumnModels());
    }
}