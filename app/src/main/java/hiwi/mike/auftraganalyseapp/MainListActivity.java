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
import hiwi.mike.auftraganalyseapp.CursorAdapter.WorkstationCursorAdapter;
import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.DialogFragments.AddProjectDialogFragment;
import hiwi.mike.auftraganalyseapp.DialogFragments.AddWorkbookDialogFragment;
import hiwi.mike.auftraganalyseapp.DialogFragments.AddWorkstationDialogFragment;
import hiwi.mike.auftraganalyseapp.DialogFragments.OnAcceptDialogFragment;

public class MainListActivity extends AppCompatActivity {

    private enum Tables {
        Workbooks,
        Projects,
        Orders,
        Workstations
    }

    private Tables  currentTable = Tables.Workbooks;
    private Tables  priorTable = null;
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
                    case Projects:
                        frgmnt = new AddProjectDialogFragment();
                        ((AddProjectDialogFragment) frgmnt).setCleanup(new Runnable() {
                            @Override
                            public void run() {
                                reloadAll();
                            }
                        });
                        ((AddProjectDialogFragment) frgmnt).setWorkbookID(currentWBID);
                        break;
                    case Workstations:
                        frgmnt = new AddWorkstationDialogFragment();
                        ((AddWorkstationDialogFragment) frgmnt).setCleanup(new Runnable() {
                            @Override
                            public void run() {
                                reloadAll();
                            }
                        });
                        ((AddWorkstationDialogFragment) frgmnt).setWorkbook_id(currentWBID);
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
                OnAcceptDialogFragment acceptDiaFrag = new OnAcceptDialogFragment();
                final SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor crs;


                acceptDiaFrag.setOnCleanup(new Runnable() {
                    @Override
                    public void run() {
                        reloadAll();
                    }
                });

                switch (currentTable)
                {
                    case Workbooks:
                        final Integer WBid = (int)view.getTag();

                        crs = db.rawQuery(WorkbookContract.GET_WORKBOOKS_BY_ID(WBid),null);
                        crs.moveToFirst();
                        acceptDiaFrag.setMessage("Wirklich die Mappe " +
                                crs.getString(crs.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME)) +
                                " und alle dazugehörigen Projekte und Aufträge löschen?");

                        acceptDiaFrag.setOnAccept(new Runnable() {
                            @Override
                            public void run() {
                                db.delete(WorkbookContract.WorkbookEntry.TABLE_NAME,
                                        WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_ID + "=?",
                                        new String[] {WBid.toString()});
                            }
                        });
                        break;
                    case Projects:
                        final Integer PRJid = (int)view.getTag();

                        crs = db.rawQuery(WorkbookContract.GET_WORKSTATION_BY_ID(PRJid),null);
                        crs.moveToFirst();
                        acceptDiaFrag.setMessage("Wirklich das Projekt " +
                                crs.getString(crs.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME)) +
                                " und alle dazugehörigen Aufträge löschen?");

                        acceptDiaFrag.setOnAccept(new Runnable() {
                            @Override
                            public void run() {
                                db.delete(WorkbookContract.WorkstationEntry.TABLE_NAME,
                                        WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID + "=?",
                                        new String[] {PRJid.toString()});
                            }
                        });
                        break;
                    case Workstations:
                        final Integer WSid = (int)view.getTag();

                        crs = db.rawQuery(WorkbookContract.GET_WORKSTATION_BY_ID(WSid),null);
                        crs.moveToFirst();
                        acceptDiaFrag.setMessage("Wirklich die Arbeitsstation " +
                                crs.getString(crs.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME)) +
                                " und alle dazugehörigen Aufträge löschen?");

                        acceptDiaFrag.setOnAccept(new Runnable() {
                            @Override
                            public void run() {
                                db.delete(WorkbookContract.WorkstationEntry.TABLE_NAME,
                                        WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID + "=?",
                                        new String[] {WSid.toString()});
                            }
                        });
                        break;
                    case Orders:
                        final Integer ORDid = (int)view.getTag();

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
                        break;
                }

                acceptDiaFrag.show(getFragmentManager(), null);

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
        if (id == R.id.action_workstations)
        {
            switch_to_TableView(Tables.Workstations, null);
            return true;
        } else if (id == android.R.id.home)
        {
            switch (currentTable)
            {
                case Projects:
                    switch_to_TableView(Tables.Workbooks, null);
                    break;
                case Workstations:
                    switch_to_TableView(priorTable,null);
                    break;
                case Orders:
                    switch_to_TableView(Tables.Projects, null);
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
                switch_to_TableView(Tables.Projects, (Integer)view.getTag());
                break;
            case Projects:
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
                hideWorkstationOption();
                break;
            case Projects:
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
                showWorkstationOption();
                break;
            case Workstations:
                crs = db.rawQuery(WorkbookContract.GET_WORKSTATION_BY_WRB_ID(currentWBID), null);
                adapter = new WorkstationCursorAdapter(getBaseContext(), crs, 0);

                titleCrs = db.rawQuery(WorkbookContract.GET_WORKBOOKS_BY_ID(currentWBID), null);
                titleCrs.moveToFirst();
                title = titleCrs.getString(
                        titleCrs.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));
                title += "> Workstations";

                showBackButton();
                hideWorkstationOption();
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
                showWorkstationOption();
                break;
        }

        assert tb != null;
        tb.setTitle(title);
        assert lv != null;
        lv.setAdapter(adapter);
        priorTable   = currentTable;
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
            case Projects:
                crs = db.rawQuery(WorkbookContract.GET_WORKSTATIONS_BY_WORKBOOK(currentWBID), null);
                break;
            case Workstations:
                crs = db.rawQuery(WorkbookContract.GET_WORKSTATION_BY_WRB_ID(currentWBID), null);
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

    public void hideWorkstationOption()
    {
        if (menu != null)
        {menu.findItem(R.id.action_workstations).setVisible(false);}
    }

    public void showWorkstationOption()
    {
        if (menu != null)
        {menu.findItem(R.id.action_workstations).setVisible(true);}
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