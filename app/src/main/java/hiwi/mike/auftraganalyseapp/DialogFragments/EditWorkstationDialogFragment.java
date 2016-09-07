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
public class EditWorkstationDialogFragment extends DialogFragment {

    private Runnable onCleanup;
    private int      workstation_id;
    private String   name;
    private Double   output;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Context context = getActivity();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.editworkstationdialog, null);

        final EditText inp_name = (EditText) layout.findViewById(R.id.name);
        final EditText inp_output = (EditText) layout.findViewById(R.id.output);

        inp_name.setText(name);
        inp_output.setText(output.toString());

        builder.setMessage("Bearbeite Maschine/Arbeitsplatz")
                .setView(layout)
                .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());
                        if (inp_name.getText().length() > 0 && inp_output.getText().length() > 0) {
                            ContentValues values = new ContentValues();
                            values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME,
                                    inp_name.getText().toString());
                            values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_OUTPUT,
                                    Double.parseDouble(inp_output.getText().toString()));

                            dbHelper.getWritableDatabase().update(
                                    WorkbookContract.WorkstationEntry.TABLE_NAME,
                                    values,
                                    WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID + "= ?",
                                    new String[]{Integer.toString(workstation_id)});
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

                        acceptDiaFragment.setMessage("Diese Arbeitsstation und alle dazugehörigen Aufträge löschen?");
                        acceptDiaFragment.setOnAccept(new Runnable() {
                            @Override
                            public void run() {
                                dbHelper.getWritableDatabase().delete(WorkbookContract.WorkstationEntry.TABLE_NAME,
                                        WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID + "= ?",
                                        new String[]{Integer.toString(workstation_id)});
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

    public void setWorkstation_id(int workstation_id) {
        this.workstation_id = workstation_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOutput(Double output) {
        this.output = output;
    }
}