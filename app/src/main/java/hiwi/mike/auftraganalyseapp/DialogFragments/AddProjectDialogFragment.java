package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;

/**
 * Created by dave on 16.06.16.
 */
public class AddProjectDialogFragment extends DialogFragment {

    private Runnable onCleanup;
    private int      workbook_id;

    public void setCleanup (Runnable run)
    {
        onCleanup = run;
    }
    public void setWorkbookID (int wbid)  { workbook_id = wbid;}

    public Dialog onCreateDialog(final Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());

        builder.setMessage("Name der neuen Maschine/Arbeitsplatz")
                .setView(input)
                .setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());
                        if (input.getText().length() > 0) {
                            dbHelper.getWritableDatabase().execSQL(WorkbookContract.INSERT_WORKSTATION(input.getText().toString(), workbook_id));
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