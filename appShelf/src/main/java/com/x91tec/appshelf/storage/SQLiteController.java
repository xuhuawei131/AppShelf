package com.x91tec.appshelf.storage;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oeager on 16-3-6.
 */
public class SQLiteController {

    public static final int TYPE_NULL = -1;

    public static final int TYPE_INTEGER = 0;

    public static final int TYPE_REAL = 1;

    public static final int TYPE_TEXT = 2;

    public static final int TYPE_BLOB = 3;

    public static final int TYPE_TINYINT = 4;

    public static final int TYPE_CHAR = 5;

    public static final int TYPE_VARCHAR = 6;

    public static final int TYPE_NVARCHAR = 7;

    private static final String SYMBOL_SPACE = " ";

    private static final String SYMBOL_COMMA = ",";

    private static final String SYMBOL_LEFT_BRACKET = "(";

    private static final String SYMBOL_RIGHT_BRACKET = ")";

    public static final String CONSTRAINT_UNIQUE = "UNIQUE";

    public static final String CONSTRAINT_NOT_NULL = "NOT NULL";

    public static final String CONSTRAINT_PRIMARY_KEY = "PRIMARY KEY";

    public static final String CONSTRAINT_AUTOINCREMENT = "AUTOINCREMENT";

    static final String CONSTRAINT_FOREIGN_KEY = "FOREIGN KEY";

    static final String CONSTRAINT_CHECK = "CHECK";

    static final String CONSTRAINT_DEFAULT = "DEFAULT";


    public static void createTable(SQLiteDatabase db, Table table) {
        db.execSQL(table.toSQLiteString());
    }

    public static void dropTable(SQLiteDatabase db, String tableName) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public static String SQL_PART_CREATE_TABLE_START(String tableName) {
        return "CREATE TABLE " + tableName + SYMBOL_SPACE + SYMBOL_LEFT_BRACKET;
    }


    static String columnType2String(@ColumnType int columnType) {
        switch (columnType) {
            case TYPE_INTEGER:
                return "INTEGER";
            case TYPE_REAL:
                return "REAL";
            case TYPE_TEXT:
                return "TEXT";
            case TYPE_BLOB:
                return "BLOB";
            case TYPE_TINYINT:
                return "TINYINT";
            case TYPE_CHAR:
                return "CHAR";
            case TYPE_VARCHAR:
                return "VARCHAR";
            case TYPE_NVARCHAR:
                return "NVARCHAR";
            default:
                return "NULL";
        }
    }

    public static String buildForeignKeyConstraint(String columnName, String foreignTableName, String foreignColumnName) {
        return CONSTRAINT_FOREIGN_KEY + SYMBOL_LEFT_BRACKET + columnName + SYMBOL_RIGHT_BRACKET + SYMBOL_SPACE + " REFERENCES " + foreignTableName + SYMBOL_LEFT_BRACKET + foreignColumnName + SYMBOL_RIGHT_BRACKET;
    }

    public static String buildCheckConstraint(String condition) {
        return CONSTRAINT_CHECK + SYMBOL_LEFT_BRACKET + condition + SYMBOL_RIGHT_BRACKET;
    }

    public static String buildBoolConstraint(String columnName) {
        return buildCheckConstraint(columnName + "= 0 or " + columnName + " = 1");
    }

    public static String buildDefaultConstraint(String value) {
        return CONSTRAINT_DEFAULT + SYMBOL_SPACE + value;
    }

    public static String buildAutoIncrementPrimaryKey() {
        return CONSTRAINT_PRIMARY_KEY + SYMBOL_SPACE + CONSTRAINT_AUTOINCREMENT;
    }

    @IntDef({TYPE_NULL, TYPE_INTEGER, TYPE_REAL, TYPE_TEXT, TYPE_BLOB,TYPE_TINYINT,TYPE_CHAR,TYPE_VARCHAR,TYPE_NVARCHAR})
    public @interface ColumnType {
    }

    public static class Table {

        final String tableName;

        final List<Column> columns = new ArrayList<>();

        List<String> extraConstraints;

        public Table(String tableName) {
            this.tableName = tableName;
        }

        public void addColumn(Column column) {
            this.columns.add(column);
        }

        public void addColumn(String columnName, @ColumnType int columnType, String constraint) {
            addColumn(columnName, columnType2String(columnType), constraint);
        }

        public void addColumn(String columnName, String columnType, String constraint) {
            Column column = new Column(columnName, columnType, constraint);
            addColumn(column);
        }

        public void addExtraConstraint(String constraint) {
            if (extraConstraints == null) {
                extraConstraints = new ArrayList<>();
            }
            extraConstraints.add(constraint);
        }

        public String toSQLiteString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append(SQL_PART_CREATE_TABLE_START(tableName));
            int size = columns.size();
            int totalSize = calculateNum();
            int tempCount = 0;
            for (Column column : columns) {
                buffer.append(column.toString());
                buffer.append(++tempCount == totalSize ? SYMBOL_RIGHT_BRACKET : SYMBOL_COMMA);
            }
            if (size < totalSize) {
                for (String constraint : extraConstraints) {
                    buffer.append(extraConstraints);
                    buffer.append(++tempCount == totalSize ? SYMBOL_RIGHT_BRACKET : SYMBOL_COMMA);
                }
            }
            return buffer.toString();
        }

        boolean hasExtraConstraints() {
            return extraConstraints != null && !extraConstraints.isEmpty();
        }

        public int calculateNum() {
            int extraSize = hasExtraConstraints() ? extraConstraints.size() : 0;
            return extraSize + columns.size();
        }

    }

    public static class Column {
        final String columnName;
        final String columnType;

        final String constraint;

        public Column(String columnName, String columnType, String constraint) {
            this.columnName = columnName;
            this.columnType = columnType;
            this.constraint = constraint;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(columnName);
            builder.append(SYMBOL_SPACE);
            builder.append(columnType);
            builder.append(SYMBOL_SPACE);
            if (!TextUtils.isEmpty(constraint)) {
                builder.append(constraint);
            }
            return builder.toString();
        }
    }
}