package hiwi.mike.auftraganalyseapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by dave on 08.06.16.
 */
public class WorkbookDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = WorkbookContract.VERSION;

    public static final String DATABASE_NAME = "Workbook.db";

    public WorkbookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraint
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WorkbookContract.OrderEntry.CREATE_TABLE);
        db.execSQL(WorkbookContract.WorkbookEntry.CREATE_TABLE);
        db.execSQL(WorkbookContract.WorkstationEntry.CREATE_TABLE);
    }


    public void onDelete(SQLiteDatabase db)
    {
        db.execSQL(WorkbookContract.OrderEntry.DELETE_TABLE);
        db.execSQL(WorkbookContract.WorkbookEntry.DELETE_TABLE);
        db.execSQL(WorkbookContract.WorkstationEntry.DELETE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion == 7)
        {
            db.execSQL(WorkbookContract.OrderEntry.DELETE_TABLE);
            db.execSQL(WorkbookContract.WorkbookEntry.DELETE_TABLE);
//            db.execSQL("DROP TABLE Projects;");
            db.execSQL(WorkbookContract.WorkstationEntry.DELETE_TABLE);

            onCreate(db);
        }
            else
        {
            onDelete(db);
            onCreate(db);

        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onDelete(db);
        onCreate(db);
    }
}
