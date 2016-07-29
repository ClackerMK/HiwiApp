package hiwi.mike.auftraganalyseapp.Database;

import android.opengl.GLES31Ext;
import android.provider.BaseColumns;

/**
 * Created by dave on 08.06.16.
 */


public final class WorkbookContract {
    public WorkbookContract() {}

    public static final int     VERSION = 7;

    private static final String TEXT_TYPE          = " TEXT";
    private static final String COMMA_SEP          = ",";


    /* Inner class that defines the table contents of Workbooks */
    public static abstract class WorkbookEntry implements BaseColumns {
        public static final String TABLE_NAME = "Workbooks";
        public static final String COLUMN_NAME_ENTRY_ID = "_id";
        public static final String COLUMN_NAME_ENTRY_NAME = "name";
        public static final String COLUMN_NAME_LAST_OPENED = "lastOpened";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_ENTRY_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LAST_OPENED + TEXT_TYPE + " DEFAULT CURRENT_TIMESTAMP" +
                ");";
        public static final String DELETE_TABLE = "DROP TABLE " + TABLE_NAME + ";";
    }

    /* Inner class that defines the table contents of Projects */
    public static abstract class ProjectEntry implements BaseColumns {
        public static final String TABLE_NAME = "Projects";
        public static final String COLUMN_NAME_ENTRY_ID = "_id";
        public static final String COLUMN_NAME_WORKBOOK_ID = "wb_id";
        public static final String COLUMN_NAME_ENTRY_NAME = "name";
        public static final String COLUMN_NAME_LAST_OPENED = "lastOpened";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_WORKBOOK_ID + " INTEGER" + COMMA_SEP +
                COLUMN_NAME_ENTRY_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LAST_OPENED + TEXT_TYPE + " DEFAULT CURRENT_TIMESTAMP" + COMMA_SEP +
                "FOREIGN KEY (" + COLUMN_NAME_WORKBOOK_ID + ") " +
                "REFERENCES " + WorkbookEntry.TABLE_NAME + " (" + WorkbookEntry.COLUMN_NAME_ENTRY_ID + ") ON DELETE CASCADE" +
                ");";
        public static final String DELETE_TABLE = "DROP TABLE " + TABLE_NAME + ";";
    }

    /* Inner class that defines the table contents of Orders */
    public static abstract class OrderEntry implements BaseColumns {
        public static final String TABLE_NAME = "Orders";
        public static final String COLUMN_NAME_ENTRY_ID = "_id";
        public static final String COLUMN_NAME_PROJECT_ID = "p_id";
        public static final String COLUMN_NAME_WORKSTATION_ID = "ws_id";
        public static final String COLUMN_NAME_ENTRY_NR = "nr";
        public static final String COLUMN_NAME_ENTRY_TARGET_DATE = "targetDate";
        public static final String COLUMN_NAME_ENTRY_TIME = "givenTime";
        public static final String COLUMN_NAME_WIP = "wip";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_PROJECT_ID + " INTEGER" + COMMA_SEP +
                COLUMN_NAME_WORKSTATION_ID + " INTEGER" + COMMA_SEP +
                COLUMN_NAME_ENTRY_NR + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ENTRY_TARGET_DATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ENTRY_TIME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_WIP + " INTEGER" + COMMA_SEP +
                "FOREIGN KEY (" + COLUMN_NAME_PROJECT_ID + ") " +
                "REFERENCES " + ProjectEntry.TABLE_NAME + " (" + ProjectEntry.COLUMN_NAME_ENTRY_ID + ") ON DELETE CASCADE" +
                ");";

        public static final String DELETE_TABLE = "DROP TABLE " + TABLE_NAME + ";";
    }

    /* Inner class that defines the table contents of Workbooks */
    public static abstract class WorkstationEntry implements BaseColumns {
        public static final String TABLE_NAME = "WorkStation";
        public static final String COLUMN_NAME_ENTRY_ID = "_id";
        public static final String COLUMN_NAME_WORKBOOK_ID = "wb_id";
        public static final String COLUMN_NAME_ENTRY_NAME = "label";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_WORKBOOK_ID + " INTEGER" + COMMA_SEP +
                COLUMN_NAME_ENTRY_NAME + TEXT_TYPE + COMMA_SEP +
                "FOREIGN KEY (" + COLUMN_NAME_WORKBOOK_ID + ") " +
                "REFERENCES " + WorkbookEntry.TABLE_NAME + " (" + WorkbookEntry.COLUMN_NAME_ENTRY_ID + ") ON DELETE CASCADE" +
                ");";

        public static final String DELETE_TABLE = "DROP TABLE " + TABLE_NAME + ";";
    }

    public static final String INSERT_WORKBOOK(String name)
    {
        return "INSERT INTO " + WorkbookEntry.TABLE_NAME + "(" + WorkbookEntry.COLUMN_NAME_ENTRY_NAME + ")" +
                " VALUES ('" + name +
                "');";
    }

    public static final String INSERT_PROJECT(String name, int wb_id)
    {
        return "INSERT INTO " + ProjectEntry.TABLE_NAME + "(" + ProjectEntry.COLUMN_NAME_ENTRY_NAME + COMMA_SEP + ProjectEntry.COLUMN_NAME_WORKBOOK_ID + ")" +
                " VALUES ('" + name + "'" + COMMA_SEP + "'" +  wb_id +
                "');";
    }

    public static final String GET_WORKBOOKS_WITH_ANZ_ORDERS()
    {
        return "SELECT " + WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
                WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_NAME + " as " + WorkbookEntry.COLUMN_NAME_ENTRY_NAME  + COMMA_SEP +
                " COUNT(" + ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_ENTRY_ID + ") as count" + //COMMA_SEP +
                //WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_LAST_OPENED + " as " + WorkbookEntry.COLUMN_NAME_LAST_OPENED +
                " FROM " + WorkbookEntry.TABLE_NAME +
                " LEFT OUTER JOIN " + ProjectEntry.TABLE_NAME +
                " ON " + WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_ID + " = " +
                ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_WORKBOOK_ID +
                " GROUP BY " + WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_ID + COMMA_SEP +
                WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_NAME +
                " ORDER BY " + WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_LAST_OPENED + " DESC ;";
    }

    public static final String GET_WORKBOOKS_BY_ID(int id)
    {
        return "SELECT * FROM " + WorkbookEntry.TABLE_NAME + " WHERE " + WorkbookEntry.COLUMN_NAME_ENTRY_ID + " = " + id + ";";
    }

    public static final String GET_WORKSTATION_BY_ID(int id)
    {
        return "SELECT " + WorkstationEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
                WorkstationEntry.COLUMN_NAME_ENTRY_NAME + " as " + WorkstationEntry.COLUMN_NAME_ENTRY_NAME +
                " FROM " + WorkstationEntry.TABLE_NAME +
                " WHERE " + WorkstationEntry.COLUMN_NAME_ENTRY_ID + " = " + id + ";";
    }

    public static final String GET_WORKSTATION_BY_WRB_ID(int wrb_id)
    {
        return "SELECT " + WorkstationEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
                WorkstationEntry.COLUMN_NAME_ENTRY_NAME + " as " + WorkstationEntry.COLUMN_NAME_ENTRY_NAME +
                " FROM " + WorkstationEntry.TABLE_NAME +
                " WHERE " + WorkstationEntry.COLUMN_NAME_WORKBOOK_ID + " = " + wrb_id + ";";
    }

    public static final String GET_PROJECTS_BY_ID(int id)
    {
        return "SELECT * FROM " + ProjectEntry.TABLE_NAME + " Where " + ProjectEntry.COLUMN_NAME_ENTRY_ID + " = " + id + ";";
    }

    public static final String GET_PROJECTS_BY_WORKBOOK(int id)
    {
        return "SELECT " + ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
                ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_ENTRY_NAME + " as " + ProjectEntry.COLUMN_NAME_ENTRY_NAME  + COMMA_SEP +
                " COUNT(" + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_ID + ") as count" + //COMMA_SEP +
                //ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_LAST_OPENED + " as " + ProjectEntry.COLUMN_NAME_LAST_OPENED +
                " FROM " + ProjectEntry.TABLE_NAME +
                " LEFT OUTER JOIN " + OrderEntry.TABLE_NAME +
                " ON " + ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_ENTRY_ID + " = " +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_PROJECT_ID +
                " WHERE " + ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_WORKBOOK_ID + "=" + id +
                " GROUP BY " + ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_ENTRY_ID + COMMA_SEP +
                ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_ENTRY_NAME +
                " ORDER BY " + ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_LAST_OPENED + " DESC;";
    }


    public static final String GET_ORDERS_BY_PROJECT(int prj_id)
    {
        return "SELECT " + OrderEntry.TABLE_NAME  + "." + OrderEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_NR + " as " + OrderEntry.COLUMN_NAME_ENTRY_NR + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE + " as " + OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_TIME + " as " + OrderEntry.COLUMN_NAME_ENTRY_TIME + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WIP + " as " + OrderEntry.COLUMN_NAME_WIP + COMMA_SEP +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_NAME + " as " +  WorkstationEntry.COLUMN_NAME_ENTRY_NAME +
                " FROM " + OrderEntry.TABLE_NAME + " INNER JOIN " + WorkstationEntry.TABLE_NAME + " ON " +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WORKSTATION_ID + "=" + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID +
                " WHERE " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_PROJECT_ID + "=" + prj_id + ";";
    }

    public static final String GET_ORDER_BY_ID(int order_id)
    {
        return "SELECT " + OrderEntry.TABLE_NAME  + "." + OrderEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
        OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_NR + " as " + OrderEntry.COLUMN_NAME_ENTRY_NR + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE + " as " + OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_TIME + " as " + OrderEntry.COLUMN_NAME_ENTRY_TIME + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WIP + " as " + OrderEntry.COLUMN_NAME_WIP + COMMA_SEP +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_NAME + " as " +  WorkstationEntry.COLUMN_NAME_ENTRY_NAME +
                " FROM " + OrderEntry.TABLE_NAME + " INNER JOIN " + WorkstationEntry.TABLE_NAME + " ON " +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WORKSTATION_ID + "=" + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID +
                " WHERE " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_ID + "=" + order_id + ";";
    }
}
