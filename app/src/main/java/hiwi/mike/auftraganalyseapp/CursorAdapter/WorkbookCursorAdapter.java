package hiwi.mike.auftraganalyseapp.CursorAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.content.res.TypedArrayUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.google.common.primitives.Ints;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.Helper.DateHelper;
import hiwi.mike.auftraganalyseapp.Helper.LimitXAxisFormatter;
import hiwi.mike.auftraganalyseapp.Helper.MathHelper;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 08.06.16.
 */

public class WorkbookCursorAdapter extends CursorAdapter{

    private Context context;

    public WorkbookCursorAdapter(Context context, Cursor cursor, int flags)
    {
        super(context, cursor, 0);

        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.listitem_bubble, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));
        int project_count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

        tvHeader.setText(name);
        tvBody.setText(String.format("Projekte: %d",project_count));

        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

        view.setTag(id);

        WorkbookDbHelper dbHelper = new WorkbookDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor_ws = db.query(WorkbookContract.WorkstationEntry.TABLE_NAME,
                new String[]{WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID,
                WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME},
                WorkbookContract.WorkstationEntry.COLUMN_NAME_WORKBOOK_ID + " = ?",
                new String[]{Integer.toString(id)},
                null,null,null);

        ScatterChart chart = (ScatterChart) view.findViewById(R.id.bubble_chart);
        ScatterData scatterData = new ScatterData();
        int[] entry_nums = new int[7];

        while (cursor_ws.moveToNext()){

            Cursor cursor_ord = db.query(WorkbookContract.OrderEntry.TABLE_NAME,
                    new String[]{
                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_ID,
                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TIME,
                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE,
                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE,
                            WorkbookContract.OrderEntry.COLUMN_NAME_WIP
                    },
                    WorkbookContract.OrderEntry.COLUMN_NAME_WORKSTATION_ID + " = ?",
                    new String[]{
                            cursor_ws.getString(cursor_ws.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID))
                    },null,null,null);
            List<Entry> entries = new ArrayList<>();
            while (cursor_ord.moveToNext()) {
                int current_pos = cursor_ord.getPosition();
                int current_wip = cursor_ord.getInt(cursor_ord.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_WIP));

                if (current_wip != 0)
                {
                    int current_id = cursor_ord.getInt(cursor_ord.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_ID));
                    int current_overlooked = 0;
                    Date current_targetDate = null;
                    Date current_documentedDate = null;
                    try {
                        current_targetDate = DateHelper.ISOFormat.parse(
                                cursor_ord.getString(
                                        cursor_ord.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE)
                                )
                        );
                        current_documentedDate = DateHelper.ISOFormat.parse(
                                cursor_ord.getString(
                                        cursor_ord.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE)
                                )
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    cursor_ord.moveToFirst();
                    do {
                        int new_wip = cursor_ord.getInt(cursor_ord.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_WIP));

                        if (new_wip == 0) {
                            Date new_targetdate = null;
                            Date new_documentedDate = null;
                            try {
                                new_targetdate = DateHelper.ISOFormat.parse(
                                        cursor_ord.getString(
                                                cursor_ord.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE)
                                        )
                                );
                                new_documentedDate = DateHelper.ISOFormat.parse(
                                        cursor_ord.getString(
                                                cursor_ord.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE)
                                        )
                                );
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (DateHelper.daysBetween(DateHelper.DateToCalendar(current_targetDate),
                                    DateHelper.DateToCalendar(current_documentedDate)) > DateHelper.daysBetween(DateHelper.DateToCalendar(new_targetdate),
                                    DateHelper.DateToCalendar(new_documentedDate)))
                            {
                                current_overlooked++;
                            }

                        }

                    }while (cursor_ord.moveToNext());

                    if (current_overlooked > 6)
                        current_overlooked = 6;

                    cursor_ord.moveToPosition(current_pos);
                    entries.add(new Entry(current_overlooked,entry_nums[current_overlooked]+1));
                    entry_nums[current_overlooked] = entry_nums[current_overlooked] + 1;
                }
            }
            ScatterDataSet dataSet = new ScatterDataSet(entries,
                    cursor_ws.getString(
                            cursor_ws.getColumnIndexOrThrow(
                                    WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME)));

            if (cursor_ws.getCount() > 1)
            {
                float[] color_hsv = {0,1,1};

                float[] range_f = new float[cursor_ws.getCount()];
                for (int i = 1; i < cursor_ws.getCount(); i++)
                {
                    range_f[i-1] = (float) i;
                }

                color_hsv[0] = MathHelper.mapArrayToRange(range_f,0f,280f)[cursor_ws.getPosition()];
                dataSet.setColor(Color.HSVToColor(color_hsv));
            }
            dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            dataSet.setScatterShapeSize(25f);

            scatterData.addDataSet(dataSet);
            scatterData.setDrawValues(false);

        }
        /*List<BubbleEntry> entries = new ArrayList<>();
        entries.add(new BubbleEntry(1f, 1f, 10f));
        entries.add(new BubbleEntry(1f, 2f, 1f));
        entries.add(new BubbleEntry(2f, 1f, 1f));

        BubbleDataSet dataSet = new BubbleDataSet(entries, "Test");
        bubbleData.addDataSet(dataSet);*/

        chart.setData(scatterData);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getLegend().setEnabled(true);
        chart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setAxisMinValue(.5f);
        chart.getAxisLeft().setAxisMaxValue(Math.max(5, Ints.max(entry_nums)));
        chart.getAxisLeft().setDrawLabels(true);

        chart.getXAxis().setValueFormatter(new LimitXAxisFormatter(0,5));
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setAxisMinValue(-.5f);
        chart.getXAxis().setAxisMaxValue(6.5f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setDescription("");

        TextView xlabel = (TextView)view.findViewById(R.id.xTitle);
        xlabel.setText("Anzahl übergangener Aufträge[-]");
        TextView ylabel = (TextView)view.findViewById(R.id.yTitle);
        ylabel.setText("Anzahl[-]");

    }

    @Override
    public void changeCursor(Cursor cursor)
    {
        super.changeCursor(cursor);
    }
}
