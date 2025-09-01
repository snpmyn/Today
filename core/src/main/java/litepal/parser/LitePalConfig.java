package litepal.parser;

import java.util.ArrayList;
import java.util.List;

import util.list.ListUtils;

/**
 * Model for litepal.xml configuration file.
 *
 * @author guolin
 * @since 2016/11/10
 */
public class LitePalConfig {
    /**
     * The version of database.
     */
    private int version;
    /**
     * The name of database.
     */
    private String dbName;
    /**
     * The case of table names and column names and SQL.
     */
    private String cases;
    /**
     * Define where the .db file should be. Option values: internal external.
     */
    private String storage;
    /**
     * All the model classes that want to map in the database. Each class should
     * be given the full name including package name.
     */
    private List<String> classNames;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    /**
     * Get the class name list. Always add table_schema as a value.
     *
     * @return The class name list.
     */
    public List<String> getClassNames() {
        if (classNames == null) {
            classNames = new ArrayList<>();
            classNames.add("litepal.model.Table_Schema");
        } else if (ListUtils.listIsEmpty(classNames)) {
            classNames.add("litepal.model.Table_Schema");
        }
        return classNames;
    }

    /**
     * Add a class name into the current mapping model list.
     *
     * @param className Full package class name.
     */
    public void addClassName(String className) {
        getClassNames().add(className);
    }

    public void setClassNames(List<String> classNames) {
        this.classNames = classNames;
    }

    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }
}