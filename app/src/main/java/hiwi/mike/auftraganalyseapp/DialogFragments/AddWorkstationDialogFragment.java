package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;

public class AddWorkstationDialogFragment extends DialogFragment {

    private Runnable onCleanup;
    private int      workbook_id;

    public void setCleanup (Runnable run)
    {
        onCleanup = run;
    }
    public void setWorkbook_id (int wbid) { workbook_id = wbid;}

    public Dialog onCreateDialog(final Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());

        builder.setMessage("Name der neuen Arbeitsmappe")
                .setView(input)
                .setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());
                        if (input.getText().length() > 0) {
                            ContentValues vals = new ContentValues();
                            vals.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME, input.getText().toString());
                            vals.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_WORKBOOK_ID, workbook_id);

                            dbHelper.getWritableDatabase().insert(WorkbookContract.WorkstationEntry.TABLE_NAME, null, vals);

                            onCleanup.run();
                        } else
                        {

                        }
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}