package hiwi.mike.auftraganalyseapp.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Helper.Helper;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 16.06.16.
 */
public class OrderCursorAdapter extends CursorAdapter {
    public OrderCursorAdapter(Context context, Cursor cursor, int flags)
    {
        super(context, cursor, 0);
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

        String no = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR));
        String vorgabeDatum = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE));
        DateFormat format = (DateFormat)Helper.ISOFormat.clone();
        Date dVorgabeDatum = null;
        Date dDocumentDatum = null;
        try {
            dVorgabeDatum = format.parse(vorgabeDatum);
            dDocumentDatum = format.parse(cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int daysDifference = Helper.daysBetween(Helper.DateToCalendar(dVorgabeDatum),
                                                Helper.DateToCalendar(dDocumentDatum));

        String zeit = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TIME));
        String wip = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_WIP));

        tvHeader.setText(no);
        tvBody.setText(String.format("Plan-Fertigstellung: %s\n" +
                "Eintragsdatum: %s\n" +
                "ZDLV: %d\n" +
                "Zeit: %s\n" +
                "Wird bearbeitet: %s",
                Helper.DMYFormat.format(dVorgabeDatum), Helper.DMYFormat.format(dDocumentDatum), daysDifference, zeit, wip));

        view.setTag(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
    }

    @Override
    public void changeCursor(Cursor cursor)
    {
        super.changeCursor(cursor);
    }
}
