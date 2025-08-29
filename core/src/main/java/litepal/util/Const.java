package litepal.util;

public interface Const {
    interface Model {
        /**
         * One2One constant value.
         */
        int ONE_TO_ONE = 1;
        /**
         * Many2One constant value.
         */
        int MANY_TO_ONE = 2;
        /**
         * Many2Many constant value.
         */
        int MANY_TO_MANY = 3;
    }

    interface Config {
        /**
         * The suffix for each database file.
         */
        String DB_NAME_SUFFIX = ".db";
        /**
         * Constant for upper case.
         */
        String CASES_UPPER = "upper";
        /**
         * Constant for lower case.
         */
        String CASES_LOWER = "lower";
        /**
         * Constant for keep case.
         */
        String CASES_KEEP = "keep";
        /**
         * Constant configuration file name.
         */
        String CONFIGURATION_FILE_NAME = "litepal.xml";
    }

    interface TableSchema {
        /**
         * Table name in database.
         */
        String TABLE_NAME = "table_schema";
        /**
         * The name column in table_schema.
         */
        String COLUMN_NAME = "name";
        /**
         * The type column in table_schema.
         */
        String COLUMN_TYPE = "type";
        /**
         * Constant for normal table.
         */
        int NORMAL_TABLE = 0;
        /**
         * Constant for intermediate join table.
         */
        int INTERMEDIATE_JOIN_TABLE = 1;
        /**
         * Constant for generic table.
         */
        int GENERIC_TABLE = 2;
    }
}