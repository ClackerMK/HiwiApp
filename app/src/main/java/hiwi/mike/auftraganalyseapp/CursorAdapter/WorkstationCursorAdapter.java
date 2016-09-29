package hiwi.mike.auftraganalyseapp.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.Helper.DateHelper;
import hiwi.mike.auftraganalyseapp.Helper.LimitXAxisFormatter;
import hiwi.mike.auftraganalyseapp.Helper.MyValueFormatter;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 16.06.16.
 */
public class WorkstationCursorAdapter extends CursorAdapter {

    WorkbookDbHelper dbHelper;
    SQLiteDatabase sqlDB;

    final Integer minHisto = -5;
    final Integer maxHisto = 5;

    public WorkstationCursorAdapter(Context context, Cursor cursor, int flags)
    {
        super(context, cursor, 0);

        dbHelper = new WorkbookDbHelper(context);
        sqlDB = dbHelper.getReadableDatabase();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.listitem_histo, parent, false);
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

        String reihenfolge = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_REIHENFOLGE));
        if (reihenfolge == null)
        {
            reihenfolge = "nicht definiert";
        }
        String kapStrg = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_KAPSTRG));
        if (kapStrg == null)
        {
            kapStrg = "nicht definiert";
        }


        int ZDLV = 0;
        double ZDLVm;

        double output = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_OUTPUT));

        int TAA;
        HashMap<Integer, Integer> taaMap = new HashMap();
        List<BarEntry> entries = new ArrayList<>();


        while (ordersCrs.moveToNext())
        {
            Date documentedDate, targetDate;
            try {
                documentedDate = DateHelper.ISOFormat.parse(
                        ordersCrs.getString(ordersCrs.getColumnIndexOrThrow(
                                WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE)));
                targetDate = DateHelper.ISOFormat.parse(
                        ordersCrs.getString(ordersCrs.getColumnIndexOrThrow(
                                WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE)));


                TAA = DateHelper.daysBetween(
                        DateHelper.DateToCalendar(targetDate), DateHelper.DateToCalendar(documentedDate));
                ZDLV += TAA;


                if (TAA < minHisto)
                    TAA = minHisto - 1;
                else if (TAA > maxHisto)
                    TAA = maxHisto + 1;

                if (taaMap.get(TAA) != null)
                {
                    taaMap.put(TAA, taaMap.get(TAA) + 1);
                } else
                {
                    taaMap.put(TAA, 1);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<Integer, Integer> entry: taaMap.entrySet())
        {
            entries.add(new BarEntry(entry.getKey(), entry.getValue()));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Terminabweichung");
        //dataSet.setBarBorderWidth(1f);
        /*dataSet.setBarSpacePercent(0.1f);*/

        /*
        TBA STYLING
         */
        BarChart chart = (BarChart) view.findViewById(R.id.hist_chart);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barData.setValueFormatter(new MyValueFormatter());
        chart.setData(barData);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);

        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setAxisMinValue(0);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setValueFormatter(new LimitXAxisFormatter(minHisto, maxHisto));
        /*chart.getXAxis().setAxisMinValue(minHisto-1);
        chart.getXAxis().setAxisMaxValue(maxHisto+1);*/
        chart.animateY(150, Easing.EasingOption.EaseOutCirc);

        chart.setDescription("Terminabweichung");

        ZDLVm = ((double) ZDLV) / project_count;

        double ZDL = ((double)project_count) / output;

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        tvHeader.setText(name);
        tvBody.setText(String.format(
                "Bestand: %d\n" +
                        "Leistung: %s\n" +
                        "Durchlaufzeit: %s\n" +
                        "mittlere verbleibende Durchlaufzeit: %s\n" +
                        "Terminabweihung: %s\n" +
                        "Reihenfolgebildung: %s\n" +
                        "Kapazitätssteuerung: %s",
                project_count,
                df.format(output),
                df.format(ZDL),
                df.format(ZDLVm),
                df.format((ZDL / 2) - ZDLVm),
                reihenfolge,
                kapStrg));

        view.setTag(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
    }

    @Override
    public void changeCursor(Cursor cursor)
    {
        super.changeCursor(cursor);
    }
}
