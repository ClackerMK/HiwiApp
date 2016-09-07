package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 16.06.16.
 */
public class EditWorkbookDialogFragment extends DialogFragment {

    private Runnable onCleanup;
    private int      workbook_id;
    private String   name;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Context context = getActivity();

        final EditText inp_name = new EditText(context);

        inp_name.setText(name);

        builder.setMessage("Bearbeite Arbeitsmappe")
                .setView(inp_name)
                .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());
                        if (inp_name.getText().length() > 0) {
                            ContentValues values = new ContentValues();
                            values.put(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME,
                                    inp_name.getText().toString());

                            dbHelper.getWritableDatabase().update(
                                    WorkbookContract.WorkbookEntry.TABLE_NAME,
                                    values,
                                    WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_ID + "= ?",
                                    new String[]{Integer.toString(workbook_id)});
                            onCleanup.run();
                        } else {

                        }
                    }
                })
                .setNegativeButton("Löschen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OnAcceptDialogFragment acceptDiaFragment = new OnAcceptDialogFragment();
                        final WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());

                        acceptDiaFragment.setMessage("Diese Arbeitsmappe und alle dazugehörigen Arbeitsstationen und Aufträge löschen?");
                        acceptDiaFragment.setOnAccept(new Runnable() {
                            @Override
                            public void run() {
                                dbHelper.getWritableDatabase().delete(WorkbookContract.WorkbookEntry.TABLE_NAME,
                                        WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_ID + "= ?",
                                        new String[]{Integer.toString(workbook_id)});
                            }
                        });
                        acceptDiaFragment.setOnCleanup(new Runnable() {
                            @Override
                            public void run() {
                                onCleanup.run();
                            }
                        });

                        acceptDiaFragment.show(getFragmentManager(), null);
                    }
                });
        return builder.create();
    }

    public void setCleanup (Runnable run)
    {
        onCleanup = run;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorkbook_id(int workbook_id) {
        this.workbook_id = workbook_id;
    }
}