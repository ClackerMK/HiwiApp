package hiwi.mike.auftraganalyseapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.DialogFragments.AddWorkstationDialogFragment;
import hiwi.mike.auftraganalyseapp.DialogFragments.DatePickerFragment;
import hiwi.mike.auftraganalyseapp.DialogFragments.TimePickerFragment;
import hiwi.mike.auftraganalyseapp.Helper.DateHelper;

public class EditOrderActivity extends AppCompatActivity {

    private boolean newOrder = true;

    private Integer WorkbID = null;
    private Integer PrjID = null;
    private Integer OrdrID = null;

    private WorkbookDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new WorkbookDbHelper(this);

        Intent intent = getIntent();

        WorkbID = intent.getIntExtra(MainListActivity.MESSAGE_WBID,0);
        PrjID = intent.getIntExtra(MainListActivity.MESSAGE_PRJID,0);

        Spinner ws_spinner = (Spinner)findViewById(R.id.ws_spinner);

        String[] queryCols      = new String[]{WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID + " as _id",
                                        WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME};
        String[] adapterCols    = new String[]{WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME};
        int[] adapterRowViews   = new int[]{android.R.id.text1};

        Cursor spnnr_cursor = dbHelper.getWritableDatabase().query(true,
                WorkbookContract.WorkstationEntry.TABLE_NAME,
                queryCols,
                WorkbookContract.WorkstationEntry.COLUMN_NAME_WORKBOOK_ID + "=" + WorkbID
                ,null,null,null,null,null);
        SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spnnr_cursor, adapterCols, adapterRowViews,0);
        sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assert ws_spinner != null;
        ws_spinner.setAdapter(sca);

        if (intent.hasExtra(MainListActivity.MESSAGE_ORDERID))
        {
            OrdrID = intent.getIntExtra(MainListActivity.MESSAGE_ORDERID,0);

            newOrder = false;

            SQLiteDatabase sql = dbHelper.getWritableDatabase();

            Cursor cursor = sql.rawQuery(WorkbookContract.GET_ORDER_BY_ID(OrdrID),null);

            cursor.moveToFirst();

            TextView tview;

            tview = (TextView) findViewById(R.id.number_text);
            assert tview != null;
            tview.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR)));

            tview = (TextView) findViewById(R.id.targetdate_text);
            assert tview != null;
            tview.setText(DateHelper.ISOtoDMY(cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE))));

            tview = (TextView) findViewById(R.id.time_text);
            assert tview != null;
            tview.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TIME)));

            tview = (TextView) findViewById(R.id.docdate_text);
            assert tview != null;
            tview.setText(DateHelper.ISOtoDMY(cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE))));

            CheckBox chkBox = (CheckBox) findViewById(R.id.checkBox);
            assert chkBox != null;
            chkBox.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_WIP)) == 1);
        } else {
            TextView tview = (TextView) findViewById(R.id.docdate_text);
            assert tview != null;
            tview.setText(DateHelper.DMYFormat.format(new Date()));
        }

        TextView dateField = (TextView)findViewById(R.id.targetdate_text);
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(v);
            }
        });

        dateField = (TextView)findViewById(R.id.docdate_text);
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(v);
            }
        });

        Cursor crs = dbHelper.getReadableDatabase().
                rawQuery(WorkbookContract.GET_WORKSTATION_BY_ID(PrjID),null);
        crs.moveToFirst();
        for (int i = 0; i < ws_spinner.getCount(); i++)
        {
            Cursor c = (Cursor) ws_spinner.getItemAtPosition(i);
            if (c.getString(c.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME)).equals(
                    crs.getString(crs.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME))))
            {
                ws_spinner.setSelection(i);
                break;
            }
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor titleCrs = db.rawQuery(WorkbookContract.GET_WORKBOOKS_BY_ID(WorkbID),null);
        String title;

        titleCrs.moveToFirst();
        title = titleCrs.getString(titleCrs.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));
        title += "> ";

        titleCrs = db.rawQuery(WorkbookContract.GET_WORKSTATION_BY_ID(PrjID),null);

        titleCrs.moveToFirst();
        title += titleCrs.getString(titleCrs.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME));
        title += "> ";

        if (newOrder)
        {
            title += "new Order";
        } else {
            titleCrs = db.rawQuery(WorkbookContract.GET_ORDER_BY_ID(OrdrID),null);
            titleCrs.moveToFirst();
            title += titleCrs.getString(titleCrs.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR));
        }

        getSupportActionBar().setTitle(title);
    }

    void addWorkstation(View _)
    {
        Log.d("image button", "addWorkstation()");
        AddWorkstationDialogFragment awdf = new AddWorkstationDialogFragment();
        awdf.setCleanup(new Runnable() {
            @Override
            public void run() {
                reloadSpinner();
            }
        });
        awdf.setWorkbookID(WorkbID);
        awdf.show(getSupportFragmentManager(), "add");
    }

    void commitOrder(View _) {
        Log.d("fab", "commitOrder()");
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues vals = new ContentValues();

        vals.put(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR,
                ((TextView)findViewById(R.id.number_text)).getText().toString());

        vals.put(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TIME,
                ((TextView)findViewById(R.id.time_text)).getText().toString());
        vals.put(WorkbookContract.OrderEntry.COLUMN_NAME_WORKSTATION_ID,
                PrjID);
        vals.put(WorkbookContract.OrderEntry.COLUMN_NAME_WIP,
                ((CheckBox)findViewById(R.id.checkBox)).isChecked() ? 1 : 0);

        vals.put(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE,
                ((TextView)findViewById(R.id.docdate_text)).getText().toString());

        vals.put(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE,
                ((TextView)findViewById(R.id.targetdate_text)).getText().toString());

        int ws_id = ((Cursor)((Spinner) findViewById(R.id.ws_spinner)).getSelectedItem()).getInt(0);
        vals.put(WorkbookContract.OrderEntry.COLUMN_NAME_WORKSTATION_ID,
                ws_id);


        if (!DateHelper.isValidDate(vals.getAsString(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE)) ||
                !DateHelper.isValidDate(vals.getAsString(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE))) {
            Toast toast = Toast.makeText(getApplicationContext(), "Fehlerhafter Datumseintrag!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {

            vals.put(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE,
                    DateHelper.DMYtoISO(vals.getAsString(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE)));
            vals.put(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE,
                    DateHelper.DMYtoISO(vals.getAsString(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE)));


            if (newOrder)
            {
                db.insert(WorkbookContract.OrderEntry.TABLE_NAME,
                        null,
                        vals);
                finish();
            } else {
                db.update(WorkbookContract.OrderEntry.TABLE_NAME,
                        vals,
                        WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_ID + "=?",
                        new String[] {OrdrID.toString()});
                finish();
            }
        }
    }

    void selectTime (View view)
    {
        TimePickerFragment tpf = new TimePickerFragment();

        tpf.setTargetText((TextView)view);

        tpf.show(getSupportFragmentManager(),"Zeit");
    }

    void selectDate (View view)
    {
        DatePickerFragment dpf = new DatePickerFragment();

        dpf.setTargetText((TextView)view);

        dpf.show(getSupportFragmentManager(),"Zieldatum");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void reloadSpinner()
    {
        Spinner ws_spinner = (Spinner)findViewById(R.id.ws_spinner);
        Cursor cursor = (Cursor)ws_spinner.getSelectedItem();

        String[] queryCols      = new String[]{WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID + " as _id",
                WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME};
        String[] adapterCols    = new String[]{WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME};
        int[] adapterRowViews   = new int[]{android.R.id.text1};

        Cursor spnnr_cursor = dbHelper.getWritableDatabase().query(true,
                WorkbookContract.WorkstationEntry.TABLE_NAME,
                queryCols,
                WorkbookContract.WorkstationEntry.COLUMN_NAME_WORKBOOK_ID + "=" + WorkbID
                ,null,null,null,null,null);
        SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spnnr_cursor, adapterCols, adapterRowViews,0);
        sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ws_spinner.setAdapter(sca);


        if (cursor != null) {
            for (int i = 0; i < ws_spinner.getCount(); i++) {
                Cursor c = (Cursor) ws_spinner.getItemAtPosition(i);
                if (c.getString(c.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME)).equals(
                        cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME)))) {
                    ws_spinner.setSelection(i);
                    break;
                }
            }
        } else
        {
            ws_spinner.setSelection(0);
        }
    }

}
