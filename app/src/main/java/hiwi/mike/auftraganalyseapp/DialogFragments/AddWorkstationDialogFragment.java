package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.Helper.Helper;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 16.06.16.
 */
public class AddWorkstationDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private Runnable onCleanup;
    private int      workbook_id;

    private EditText inp_name;
    private EditText inp_output;
    private EditText inp_reihenfolge_other;
    private Spinner  inp_reihenfolge_spinner;
    private EditText inp_kapstrg_other;
    private Spinner  inp_kapstrg_spinner;

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

        // Init Basic Text Fields
        inp_name = (EditText) layout.findViewById(R.id.name);
        inp_output = (EditText) layout.findViewById(R.id.output);

        // Init Reihenfolge Spinner and Other Text Field
        inp_reihenfolge_other = (EditText) layout.findViewById(R.id.reihenfolge_other);
        inp_reihenfolge_spinner = (Spinner) layout.findViewById(R.id.reihenfolge_spinner);

        ArrayAdapter<CharSequence> ReiheAdapter = ArrayAdapter.createFromResource(context,
                R.array.reihenfolge_array, android.R.layout.simple_spinner_item);
        ReiheAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        inp_reihenfolge_spinner.setAdapter(ReiheAdapter);
        inp_reihenfolge_spinner.setOnItemSelectedListener(this);

        inp_reihenfolge_spinner.setSelection(getResources().getStringArray(R.array.reihenfolge_array).length - 1);

        // Init KapStrg Spinner and Other Text Field
        inp_kapstrg_other = (EditText) layout.findViewById(R.id.kapstrg_other);
        inp_kapstrg_spinner = (Spinner) layout.findViewById(R.id.kapstrg_spinner);


        ArrayAdapter<CharSequence> KapStrgAdapter = ArrayAdapter.createFromResource(context,
                R.array.kapstrg_array, android.R.layout.simple_spinner_item);
        KapStrgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        inp_kapstrg_spinner.setAdapter(KapStrgAdapter);
        inp_kapstrg_spinner.setOnItemSelectedListener(this);

        inp_reihenfolge_spinner.setSelection(getResources().getStringArray(R.array.kapstrg_array).length - 1);


        builder.setMessage("Neue/r Maschine/Arbeitsplatz")
                .setView(layout)
                .setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());
                        if (inp_name.getText().length() > 0 && inp_output.getText().length() > 0 ) {
                            ContentValues values = new ContentValues();
                            values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME,
                                    inp_name.getText().toString());
                            values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_OUTPUT,
                                    Double.parseDouble(inp_output.getText().toString()));
                            values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_WORKBOOK_ID,
                                    workbook_id);

                            if (inp_reihenfolge_spinner.getSelectedItem().equals("andere:"))
                            {
                                values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_REIHENFOLGE,
                                        inp_reihenfolge_other.getText().toString());
                            }else if (!inp_reihenfolge_spinner.getSelectedItem().equals("nicht definiert"))
                            {
                                values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_REIHENFOLGE,
                                        (String)inp_reihenfolge_spinner.getSelectedItem());
                            }else
                            {
                                values.putNull(WorkbookContract.WorkstationEntry.COLUMN_NAME_REIHENFOLGE);
                            }

                            if (inp_kapstrg_spinner.getSelectedItem().equals("andere:"))
                            {
                                values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_KAPSTRG,
                                        inp_kapstrg_other.getText().toString());
                            }else if (!inp_kapstrg_spinner.getSelectedItem().equals("nicht definiert"))
                            {
                                values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_KAPSTRG,
                                        (String)inp_kapstrg_spinner.getSelectedItem());
                            }else
                            {
                                values.putNull(WorkbookContract.WorkstationEntry.COLUMN_NAME_KAPSTRG);
                            }

                            dbHelper.getWritableDatabase().insert(
                                    WorkbookContract.WorkstationEntry.TABLE_NAME,
                                    null,
                                    values);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getItemAtPosition(pos).equals("andere:"))
        {
            if (parent == inp_reihenfolge_spinner)
                inp_reihenfolge_other.setVisibility(View.VISIBLE);
            else
                inp_kapstrg_other.setVisibility(View.VISIBLE);

        } else
        {
            if (parent == inp_reihenfolge_spinner)
                inp_reihenfolge_other.setVisibility(View.GONE);
            else
                inp_kapstrg_other.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}