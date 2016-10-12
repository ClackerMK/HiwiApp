package hiwi.mike.auftraganalyseapp.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Helper.DateHelper;
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

    String PluralSingular (float i, String singular, String plural)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        if (df.format(i).equals("1"))
        {
            return singular;
        }else
        {
            return plural;
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);

        String no = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR));
        String vorgabeDatum = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE));
        DateFormat format = (DateFormat) DateHelper.ISOFormat.clone();
        Date dVorgabeDatum = null;
        Date dDocumentDatum = null;
        try {
            dVorgabeDatum = format.parse(vorgabeDatum);
            dDocumentDatum = format.parse(cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int daysDifference = DateHelper.daysBetween(DateHelper.DateToCalendar(dVorgabeDatum),
                                                DateHelper.DateToCalendar(dDocumentDatum));

        String zeit = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TIME));
        Integer wip = cursor.getInt(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_WIP));

        tvHeader.setText(no);
        tvBody.setText(String.format("Plan-Fertigstellungstermin: %s\n" +
                "Eintragsdatum: %s\n" +
                "verbleibende Durchlaufzeit: %d %s\n" +
                "Auftragszeit: %s\n" +
                "Wird bearbeitet: %s",
                DateHelper.DMYFormat.format(dVorgabeDatum),
                DateHelper.DMYFormat.format(dDocumentDatum),
                daysDifference, PluralSingular(daysDifference, "Tag", "Tage"),
                zeit,
                (new String[]{"nein","ja"})[wip]));

        view.setTag(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
    }

    @Override
    public void changeCursor(Cursor cursor)
    {
        super.changeCursor(cursor);
    }
}
