package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;

/**
 * Created by dave on 21.07.16.
 */
public class OnAcceptDialogFragment extends DialogFragment {
    private Runnable    onCleanup;
    private Runnable    onAccept;
    private String      message;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setOnAccept(Runnable onAccept) {
        this.onAccept = onAccept;
    }

    public void setOnCleanup(Runnable onCleanup) {
        this.onCleanup = onCleanup;
    }

    public Dialog onCreateDialog(final Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        onAccept.run();
                        onCleanup.run();
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        onCleanup.run();
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
