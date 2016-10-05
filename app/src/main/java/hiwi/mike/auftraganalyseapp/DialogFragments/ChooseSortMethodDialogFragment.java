package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 05.10.16.
 */
public class ChooseSortMethodDialogFragment extends DialogFragment {

    private String selectedSortMethod;
    private String currentWorkbook;
    private Runnable onCleanup;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //String[] sort_array = getResources().getStringArray(R.array.sortmodes);

        builder.setTitle("Sortieren nach:");
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        switch (currentWorkbook)
        {
            case WorkbookContract.WorkbookEntry.TABLE_NAME:
                builder.setItems(R.array.sortmodes_workbooks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] columnNames = new String[2];

                        switch (which)
                        {
                            case 0:
                                selectedSortMethod = WorkbookContract.WorkbookEntry.COLUMN_NAME_LAST_OPENED + " DESC";
                                break;
                            case 1:
                                selectedSortMethod = WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME;
                                break;
                            case 2:
                                selectedSortMethod = WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME + " DESC";
                                break;
                        }

                        onCleanup.run();
                    }
                });
                break;
            case WorkbookContract.WorkstationEntry.TABLE_NAME:
                builder.setItems(R.array.sortmodes_workstations, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] columnNames = new String[2];

                        switch (which)
                        {
                            case 0:
                                selectedSortMethod = WorkbookContract.WorkstationEntry.COLUMN_NAME_LAST_OPENED + " DESC";
                                break;
                            case 1:
                                selectedSortMethod = WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME;
                                break;
                            case 2:
                                selectedSortMethod = WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME + " DESC";
                                break;
                            case 3:
                                selectedSortMethod = WorkbookContract.WorkstationEntry.COLUMN_NAME_OUTPUT;
                                break;
                            case 4:
                                selectedSortMethod = WorkbookContract.WorkstationEntry.COLUMN_NAME_OUTPUT + " DESC";
                        }

                        onCleanup.run();
                    }
                });
                break;
            case WorkbookContract.OrderEntry.TABLE_NAME:
                builder.setItems(R.array.sortmodes_orders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] columnNames = new String[2];

                        switch (which)
                        {
                            case 0:
                                selectedSortMethod = WorkbookContract.OrderEntry.COLUMN_NAME_LAST_OPENED + " DESC";
                                break;
                            case 1:
                                selectedSortMethod = WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR;
                                break;
                            case 2:
                                selectedSortMethod = WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR + " DESC";
                                break;
                            case 3:
                                selectedSortMethod = WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE;
                                break;
                            case 4:
                                selectedSortMethod = WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE + " DESC";
                                break;
                            case 5:
                                selectedSortMethod = WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE;
                                break;
                            case 6:
                                selectedSortMethod = WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE + " DESC";
                                break;
                            case 7:
                                selectedSortMethod = WorkbookContract.OrderEntry.COLUMN_NAME_WIP + " DESC, " +
                                        WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR + " ASC";
                                break;
                        }

                        onCleanup.run();
                    }
                });
                break;
            default:
                throw new IllegalArgumentException("Missing Info about ");
        }


        return builder.create();
    }

    public String getSelectedSortMethod() {
        return selectedSortMethod;
    }

    public void setCurrentWorkbook(String currentWorkbook) {
        this.currentWorkbook = currentWorkbook;
    }

    public void setOnCleanup(Runnable onCleanup) {
        this.onCleanup = onCleanup;
    }
}
