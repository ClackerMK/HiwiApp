package hiwi.mike.auftraganalyseapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import hiwi.mike.auftraganalyseapp.CursorAdapter.OrderCursorAdapter;
import hiwi.mike.auftraganalyseapp.CursorAdapter.ProjectCursorAdapter;
import hiwi.mike.auftraganalyseapp.CursorAdapter.WorkbookCursorAdapter;
import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.DialogFragments.AddWorkstationDialogFragment;
import hiwi.mike.auftraganalyseapp.DialogFragments.AddWorkbookDialogFragment;
import hiwi.mike.auftraganalyseapp.DialogFragments.EditWorkbookDialogFragment;
import hiwi.mike.auftraganalyseapp.DialogFragments.EditWorkstationDialogFragment;
import hiwi.mike.auftraganalyseapp.DialogFragments.OnAcceptDialogFragment;

public class MainListActivity extends AppCompatActivity {

    private enum Tables {
        Workbooks,
        Workstations,
        Orders
    }

    private Tables  currentTable = Tables.Workbooks;
    private Integer currentWBID = null;
    private Integer currentPrjID = null;

    private WorkbookDbHelper dbHelper;

    private Menu menu;

    static public final String MESSAGE_WBID = "MESSAGE_WBID";
    static public final String MESSAGE_ORDERID = "MESSAGE_ORDERID";
    static public final String MESSAGE_PRJID = "MESSAGE_PRJID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new WorkbookDbHelper(this);

        switch_to_TableView(Tables.Workbooks, null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment frgmnt = null;

                switch (currentTable)
                {
                    case Workbooks:
                        frgmnt = new AddWorkbookDialogFragment();
                        ((AddWorkbookDialogFragment) frgmnt).setCleanup(new Runnable() {
                            @Override
                            public void run() {
                                reloadAll();
                            }
                        });
                        break;
                    case Workstations:
                        frgmnt = new AddWorkstationDialogFragment();
                        ((AddWorkstationDialogFragment) frgmnt).setCleanup(new Runnable() {
                            @Override
                            public void run() {
                                reloadAll();
                            }
                        });
                        ((AddWorkstationDialogFragment) frgmnt).setWorkbookID(currentWBID);
                        break;
                    case Orders:
                        Intent intent = new Intent(getBaseContext(), EditOrderActivity.class);
                        intent.putExtra(MESSAGE_WBID, currentWBID);
                        intent.putExtra(MESSAGE_PRJID,currentPrjID);
                        startActivityForResult(intent,0);
                        break;
                }
                if (frgmnt != null) {
                    frgmnt.show(getSupportFragmentManager(), "add");
                }
            }
        });

        ListView lv;

        lv = (ListView) findViewById(R.id.listView);
        assert lv != null;
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor crs;

                switch (currentTable)
                {
                    case Workbooks:
                        final Integer WBid = (int)view.getTag();
                        EditWorkbookDialogFragment wbEditFragment = new EditWorkbookDialogFragment();

                        crs = db.rawQuery(WorkbookContract.GET_WORKBOOKS_BY_ID(WBid),null);
                        crs.moveToFirst();

                        wbEditFragment.setWorkbook_id(WBid);
                        wbEditFragment.setName(crs.getString(crs.getColumnIndexOrThrow(
                                WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME)));
                        wbEditFragment.setCleanup(new Runnable() {
                            @Override
                            public void run() {
                                reloadAll();
                            }
                        });

                        wbEditFragment.show(getFragmentManager(), null);
                        break;
                    case Workstations:
                        final Integer WSid = (int)view.getTag();
                        EditWorkstationDialogFragment wsEditFragment = new EditWorkstationDialogFragment();

                        crs = db.rawQuery(WorkbookContract.GET_WORKSTATION_BY_ID(WSid),null);
                        crs.moveToFirst();

                        wsEditFragment.setName(crs.getString(crs.getColumnIndexOrThrow(
                                WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME)));
                        wsEditFragment.setOutput(crs.getDouble(crs.getColumnIndexOrThrow(
                                WorkbookContract.WorkstationEntry.COLUMN_NAME_OUTPUT)));
                        wsEditFragment.setWorkstation_id(WSid);
                        wsEditFragment.setCleanup(new Runnable() {
                            @Override
                            public void run() {
                                reloadAll();
                            }
                        });
                        wsEditFragment.show(getFragmentManager(), null);
                        break;
                    case Orders:
                        final Integer ORDid = (int)view.getTag();
                        OnAcceptDialogFragment acceptDiaFrag = new OnAcceptDialogFragment();

                        crs = db.rawQuery(WorkbookContract.GET_ORDER_BY_ID(ORDid),null);
                        crs.moveToFirst();

                        acceptDiaFrag.setMessage("Wirklich den Auftrag " +
                                crs.getString(crs.getColumnIndexOrThrow(WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR)) +
                                " löschen?");

                        acceptDiaFrag.setOnAccept(new Runnable() {
                            @Override
                            public void run() {
                                db.delete(WorkbookContract.OrderEntry.TABLE_NAME,
                                        WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_ID + "=?",
                                        new String[] {ORDid.toString()});
                            }
                        });
                        acceptDiaFrag.setOnCleanup(new Runnable() {
                            @Override
                            public void run() {
                                reloadAll();
                            }
                        });
                        acceptDiaFrag.show(getFragmentManager(), null);
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            switch (currentTable)
            {
                case Workstations:
                    switch_to_TableView(Tables.Workbooks, null);
                    break;
                case Orders:
                    switch_to_TableView(Tables.Workstations, null);
                    break;
            }
            return true;
        } else if (id == R.id.action_export)
        {
            /*ExportDialogFragment edf = new ExportDialogFragment();

            edf.setWorkbook_id(currentWBID);
            edf.setProject_id(currentPrjID);

            edf.show(getSupportFragmentManager(),null);*/
            Intent intent = new Intent(this, NewExportActivity.class);
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        reloadAll();
    }

    public void switchToNextList(View view)
    {
        switch (currentTable)
        {
            case Workbooks:
                switch_to_TableView(Tables.Workstations, (Integer)view.getTag());
                break;
            case Workstations:
                switch_to_TableView(Tables.Orders, (Integer)view.getTag());
                break;
            case Orders:
                Intent intent = new Intent(this, EditOrderActivity.class);
                intent.putExtra(MESSAGE_ORDERID, (Integer)view.getTag());
                intent.putExtra(MESSAGE_WBID, currentWBID);
                intent.putExtra(MESSAGE_PRJID,currentPrjID);
                startActivityForResult(intent,1);
                break;
        }
    }

    public void switch_to_TableView(Tables trgt, Integer id)
    {
        SQLiteDatabase  db = dbHelper.getWritableDatabase();
        Cursor          crs = null;
        CursorAdapter   adapter = null;
        ListView        lv = (ListView) findViewById(R.id.listView);
        Toolbar         tb = (Toolbar) findViewById(R.id.toolbar);
        Cursor          titleCrs = null;
        String          title = "";

        switch (trgt) {
            case Workbooks:
                currentWBID = null;
                currentPrjID = null;

                crs = db.rawQuery(WorkbookContract.GET_ALL_WORKBOOKS(), null);
                adapter = new WorkbookCursorAdapter(getBaseContext(), crs, 0);

                title = getString(R.string.app_name);

                hideBackButton();
                break;
            case Workstations:
                if (id != null)
                {
                    currentWBID = id;
                }
                currentPrjID = null;

                /*ContentValues vals;
                vals.put(WorkbookContract.WorkbookEntry.COLUMN_NAME_LAST_OPENED, );
                db.update(WorkbookContract.WorkbookEntry.TABLE_NAME,,)
                */
                crs = db.rawQuery(WorkbookContract.GET_WORKSTATIONS_BY_WORKBOOK(currentWBID), null);
                adapter = new ProjectCursorAdapter(getBaseContext(), crs, 0);

                titleCrs = db.rawQuery(WorkbookContract.GET_WORKBOOKS_BY_ID(currentWBID), null);
                titleCrs.moveToFirst();
                title = titleCrs.getString(
                        titleCrs.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));

                showBackButton();
                break;
            case Orders:
                if (id != null)
                {
                    currentPrjID = id;
                }

                crs = db.rawQuery(WorkbookContract.GET_ORDERS_BY_WORKSTATIONS(currentPrjID), null);
                adapter = new OrderCursorAdapter(getBaseContext(), crs, 0);

                titleCrs = db.rawQuery(WorkbookContract.GET_WORKBOOKS_BY_ID(currentWBID), null);
                titleCrs.moveToFirst();
                title = titleCrs.getString(
                        titleCrs.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));
                title += "> ";
                titleCrs = db.rawQuery(WorkbookContract.GET_WORKSTATION_BY_ID(currentPrjID), null);
                titleCrs.moveToFirst();
                title += titleCrs.getString(
                        titleCrs.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME)
                );

                showBackButton();
                break;
        }

        assert tb != null;
        tb.setTitle(title);
        assert lv != null;
        lv.setAdapter(adapter);
        currentTable = trgt;
        reloadAll();
    }

    public void reloadAll()
    {
        SQLiteDatabase          db = dbHelper.getWritableDatabase();
        Cursor                  crs = null;
        ListView                lv = (ListView) findViewById(R.id.listView);
        assert lv != null;
        final ListAdapter       adapter = lv.getAdapter();

        switch (currentTable) {
            case Workbooks:
                crs = db.rawQuery(WorkbookContract.GET_ALL_WORKBOOKS(), null);
                break;
            case Workstations:
                crs = db.rawQuery(WorkbookContract.GET_WORKSTATIONS_BY_WORKBOOK(currentWBID), null);
                break;
            case Orders:
                crs = db.rawQuery(WorkbookContract.GET_ORDERS_BY_WORKSTATIONS(currentPrjID), null);
                break;
        }

        ((CursorAdapter)adapter).changeCursor(crs);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((CursorAdapter)adapter).notifyDataSetChanged();
            }
        });
    }

    public void hideBackButton()
    {
        if (getSupportActionBar() != null)
        {getSupportActionBar().setDisplayHomeAsUpEnabled(false);}
    }

    public void showBackButton()
    {
        if (getSupportActionBar() != null)
        {getSupportActionBar().setDisplayHomeAsUpEnabled(true);}
    }
}
