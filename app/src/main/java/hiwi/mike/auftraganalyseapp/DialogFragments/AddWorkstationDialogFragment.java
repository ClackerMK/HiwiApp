package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 16.06.16.
 */
public class AddWorkstationDialogFragment extends DialogFragment {

    private Runnable onCleanup;
    private int      workbook_id;

    public void setCleanup (Runnable run)
    {
        onCleanup = run;
    }
    public void setWorkbookID (int wbid)  { workbook_id = wbid;}

    public Dialog onCreateDialog(final Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Context context = getActivity();

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.editworkstationdialog,null);

        final EditText inp_name = (EditText)layout.findViewById(R.id.name);
        final EditText inp_output = (EditText)layout.findViewById(R.id.output);

        builder.setMessage("Neue/r Maschine/Arbeitsplatz")
                .setView(layout)
                .setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());
                        if (inp_name.getText().length() > 0 && inp_output.getText().length() > 0 ) {
                            dbHelper.getWritableDatabase().execSQL(WorkbookContract.INSERT_WORKSTATION(
                                    inp_name.getText().toString(),
                                    Integer.parseInt(inp_output.getText().toString()),
                                    workbook_id));
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