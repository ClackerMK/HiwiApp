package hiwi.mike.auftraganalyseapp.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.google.common.primitives.Ints;

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

    final Integer okayMin = minHisto - 1;
    final Integer okayMax = maxHisto + 1;

    public WorkstationCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);

        dbHelper = new WorkbookDbHelper(context);
        sqlDB = dbHelper.getReadableDatabase();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem_bubble, parent, false);
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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);

        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int project_count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

        Cursor ordersCrs = sqlDB.rawQuery(WorkbookContract.GET_ORDERS_BY_WORKSTATIONS(
                cursor.getInt(cursor.getColumnIndexOrThrow("_id"))), null);

        String reihenfolge = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_REIHENFOLGE));
        if (reihenfolge == null) {
            reihenfolge = "nicht definiert";
        }
        String kapStrg = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_KAPSTRG));
        if (kapStrg == null) {
            kapStrg = "nicht definiert";
        }


        int ZDLV = 0;
        double ZDLVm;

        double output = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_OUTPUT));

        int TAA;
        HashMap<Integer, Integer> taaMap = new HashMap();
        List<Entry> entriesOkay = new ArrayList<>();
        List<Entry> entriesNotOkay = new ArrayList<>();


        while (ordersCrs.moveToNext()) {
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

                if (taaMap.get(TAA) != null) {
                    taaMap.put(TAA, taaMap.get(TAA) + 1);
                } else {
                    taaMap.put(TAA, 1);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int y_max = 0;
        for (Map.Entry<Integer, Integer> entry : taaMap.entrySet()) {
            for (int i = 1; i <= entry.getValue(); i++) {
                if (entry.getKey() <= okayMax && entry.getKey() >= okayMin )
                {
                    entriesOkay.add(new Entry(entry.getKey(), i));
                }else
                {
                    entriesNotOkay.add(new Entry(entry.getKey(), i));
                }
            }
            if (y_max < entry.getValue()) {
                y_max = entry.getValue();
            }
        }
        ScatterDataSet dataSetOkay = new ScatterDataSet(entriesOkay, "Terminabweichung");
        dataSetOkay.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        dataSetOkay.setDrawValues(false);
        dataSetOkay.setScatterShapeSize(25f);
        //dataSetOkay.setColor(Color.rgb(50,205,50));
        ScatterDataSet dataSetNotOkay = new ScatterDataSet(entriesNotOkay, "Terminabweichung");
        dataSetNotOkay.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        dataSetNotOkay.setDrawValues(false);
        dataSetNotOkay.setScatterShapeSize(25f);
        dataSetNotOkay.setColor(Color.rgb(220,20,60));
        //dataSet.setBarBorderWidth(1f);
        /*dataSet.setBarSpacePercent(0.1f);*/

        /*
        TBA STYLING
         */
        ScatterChart chart = (ScatterChart) view.findViewById(R.id.bubble_chart);
        ScatterData scatterData = new ScatterData(dataSetOkay);
        scatterData.addDataSet(dataSetNotOkay);
        scatterData.setValueFormatter(new MyValueFormatter());
        chart.setData(scatterData);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getLegend().setEnabled(false);
        //chart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setAxisMinValue(.5f);
        chart.getAxisLeft().setAxisMaxValue(Math.max(5, y_max) + .5f);
        chart.getAxisLeft().setDrawLabels(true);

        chart.getXAxis().setValueFormatter(new LimitXAxisFormatter(minHisto, maxHisto));
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setAxisMinValue(minHisto - 1.5f);
        chart.getXAxis().setAxisMaxValue(maxHisto + 1.5f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setDescription("");

        ZDLVm = ((double) ZDLV) / project_count;

        double ZDL = ((double) project_count) / output;

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);


        tvHeader.setText(name);
        tvBody.setText(String.format(
                "Bestand: %d %s\n" +
                        "mittlere Leistung: %s %s\n" +
                        "Durchlaufzeit: %s %s\n" +
                        "mittlere verbleibende Durchlaufzeit: %s %s\n" +
                        "prognostizierte Terminabweihung: %s %s\n" +
                        "Reihenfolgebildung: %s\n" +
                        "Kapazitätssteuerung: %s",
                project_count, PluralSingular(project_count, "Auftrag", "Aufträge"),
                df.format(output), PluralSingular((float)output, "Auftrag", "Aufträge") + " pro Tag",
                df.format(ZDL), PluralSingular((float)ZDL, "Tag", "Tage"),
                df.format(ZDLVm), PluralSingular((float)ZDLVm, "Tag", "Tage"),
                df.format((ZDL / 2) - ZDLVm), PluralSingular((float)((ZDL / 2) - ZDLVm), "Tag", "Tage"),
                reihenfolge,
                kapStrg));

        view.setTag(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));

        TextView xlabel = (TextView) view.findViewById(R.id.xTitle);
        xlabel.setText("verbleibende Durchlaufzeit [Tage]");
        TextView ylabel = (TextView) view.findViewById(R.id.yTitle);
        ylabel.setText("Anzahl [-]");
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }
}
