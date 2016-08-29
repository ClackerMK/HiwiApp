package hiwi.mike.auftraganalyseapp.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.Helper.Helper;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 16.06.16.
 */
public class ProjectCursorAdapter extends CursorAdapter {

    WorkbookDbHelper dbHelper;
    SQLiteDatabase sqlDB;

    public ProjectCursorAdapter(Context context, Cursor cursor, int flags)
    {
        super(context, cursor, 0);


        dbHelper = new WorkbookDbHelper(context);
        sqlDB = dbHelper.getReadableDatabase();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);

        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int project_count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

        Cursor ordersCrs = sqlDB.rawQuery(WorkbookContract.GET_ORDERS_BY_WORKSTATIONS(
                cursor.getInt(cursor.getColumnIndexOrThrow("_id"))),null);

        int ZDLV = 0;
        double ZDLVm = 0;

        while (ordersCrs.moveToNext())
        {
            Date documentedDate, targetDate;
            try {
                documentedDate = Helper.ISOFormat.parse(
                        ordersCrs.getString(ordersCrs.getColumnIndexOrThrow(
                                WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE)));
                targetDate = Helper.ISOFormat.parse(
                        ordersCrs.getString(ordersCrs.getColumnIndexOrThrow(
                                WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE)));
                ZDLV += Helper.daysBetween(
                        Helper.DateToCalendar(targetDate), Helper.DateToCalendar(documentedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ZDLVm = ((double) ZDLV) / project_count;

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        tvHeader.setText(name);
        tvBody.setText(String.format("Einträge: %d\n" + "ZDLVm: %s", project_count, df.format(ZDLVm)));

        view.setTag(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
    }

    @Override
    public void changeCursor(Cursor cursor)
    {
        super.changeCursor(cursor);
    }
}
